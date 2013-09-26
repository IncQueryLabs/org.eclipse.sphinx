/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 See4sys, BMW Car IT, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     BMW Car IT - [374883] Improve handling of out-of-sync workspace files during descriptor initialization
 *     itemis - [409458] Enhance ScopingResourceSetImpl#getEObjectInScope() to enable cross-document references between model files with different metamodels
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
 *     
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.scoping.IResourceScope;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;

/**
 * A default implementation of the {@link ScopingResourceSet} interface.
 */
public class ScopingResourceSetImpl extends ExtendedResourceSetImpl implements ScopingResourceSet {

	/**
	 * Default constructor.
	 */
	public ScopingResourceSetImpl() {
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ScopingResourceSet#getResourcesInModel(java.lang.Object)
	 */
	public List<Resource> getResourcesInModel(Object contextObject) {
		return getResourcesInScope(contextObject, true, false);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ScopingResourceSet#getResourcesInModel(java.lang.Object, boolean)
	 */
	public List<Resource> getResourcesInModel(Object contextObject, boolean includeReferencedScopes) {
		return getResourcesInScope(contextObject, includeReferencedScopes, false);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ScopingResourceSet#getResourcesInScope(java.lang.Object)
	 */
	public List<Resource> getResourcesInScope(Object contextObject) {
		return getResourcesInScope(contextObject, true, true);
	}

	public List<Resource> getResourcesInScope(Object contextObject, boolean includeReferencedScopes) {
		return getResourcesInScope(contextObject, includeReferencedScopes, true);
	}

	/**
	 * Retrieves the {@link Resource resource}s in this {@link ResourceSet resource set} which belong to the
	 * {@link IModelDescriptor model} behind the contextObject.
	 * 
	 * @param contextObject
	 * @param includeReferencedScopes
	 * @param ignoreMetaModel
	 * @return The {@link Resource}s in this {@link ResourceSet resource set} which belong to the
	 *         {@link IModelDescriptor model} behind the contextObject.
	 */
	protected List<Resource> getResourcesInScope(Object contextObject, boolean includeReferencedScopes, boolean ignoreMetaModel) {
		// Context object in resource being located inside workspace?
		Resource contextResource = getResource(contextObject);
		if (contextResource == null || isPlatformResource(contextResource)) {
			// Retrieve resource scope(s) and metamodel descriptor(s) behind given context object
			Map<IResourceScope, Set<IMetaModelDescriptor>> contextResourceScopes;
			if (contextResource != null) {
				contextResourceScopes = getResourceScopes(contextResource);
			} else {
				contextResourceScopes = getResourceScopes(contextObject);
			}

			// Collect resources which belong to same resource scope(s) and metamodel(s) as the context object does
			/*
			 * !! Important Note !! A LinkedHashSet is used to preserve the ordering of the Resources. Using a simple
			 * HashSet will not preserve the ordering which leads to inconsistent results when implementing Resource
			 * merging based on the getResourcesInScope() method.
			 */
			Set<Resource> resourcesInScope = new LinkedHashSet<Resource>();
			List<Resource> safeResources = new ArrayList<Resource>(getResources());
			for (Resource resource : safeResources) {
				for (IResourceScope contextResourceScope : contextResourceScopes.keySet()) {
					if (contextResourceScope.belongsTo(resource, includeReferencedScopes)) {
						if (ignoreMetaModel || hasMatchingMetaModel(contextResourceScopes.get(contextResourceScope), resource)) {
							resourcesInScope.add(resource);
							break;
						}
					}
				}
			}
			return Collections.unmodifiableList(new ArrayList<Resource>(resourcesInScope));
		} else {
			// Retrieve metamodel descriptor behind given context object
			IMetaModelDescriptor contextMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(contextResource);

			// Collect only resources which are located outside workspace
			/*
			 * !! Important Note !! A LinkedHashSet is used to preserve the ordering of the Resources. Using a simple
			 * HashSet will not preserve the ordering which leads to inconsistent results when implementing Resource
			 * merging based on the getResourcesInScope() method.
			 */
			Set<Resource> resourcesInScope = new LinkedHashSet<Resource>();
			List<Resource> safeResources = new ArrayList<Resource>(getResources());
			for (Resource resource : safeResources) {
				if (!isPlatformResource(resource)) {
					if (ignoreMetaModel || hasMatchingMetaModel(Collections.singleton(contextMMDescriptor), resource)) {
						resourcesInScope.add(resource);
					}
				}
			}
			return Collections.unmodifiableList(new ArrayList<Resource>(resourcesInScope));
		}
	}

	protected Resource getResource(Object object) {
		URI uri = null;
		if (object instanceof IFile) {
			uri = EcorePlatformUtil.createURI(((IFile) object).getFullPath());
		} else if (object instanceof URI) {
			uri = (URI) object;
		}

		Resource resource = null;
		if (uri != null) {
			resource = getResource(uri.trimFragment(), false);
		} else {
			resource = EcoreResourceUtil.getResource(object);
		}
		return resource;
	}

	protected boolean isPlatformResource(Resource resource) {
		return resource.getURI().isPlatformResource();
	}

	protected Map<IResourceScope, Set<IMetaModelDescriptor>> getResourceScopes(Resource contextResource) {
		Map<IResourceScope, Set<IMetaModelDescriptor>> resourceScopes = new HashMap<IResourceScope, Set<IMetaModelDescriptor>>(1);
		IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(contextResource);
		if (modelDescriptor != null) {
			resourceScopes.put(modelDescriptor.getScope(), Collections.singleton(modelDescriptor.getMetaModelDescriptor()));
		}
		return resourceScopes;
	}

	protected Map<IResourceScope, Set<IMetaModelDescriptor>> getResourceScopes(Object contextObject) {
		Map<IResourceScope, Set<IMetaModelDescriptor>> resourceScopes = new HashMap<IResourceScope, Set<IMetaModelDescriptor>>(1);
		if (contextObject instanceof URI) {
			URI contextURI = (URI) contextObject;
			if (contextURI.isPlatformResource()) {
				IPath contextPath = new Path(contextURI.toPlatformString(true));
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(contextPath);
				if (resource instanceof IContainer) {
					contextObject = resource;
				}
			}
		}
		if (contextObject instanceof IContainer) {
			IContainer contextContainer = (IContainer) contextObject;
			for (IModelDescriptor modelDescriptor : ModelDescriptorRegistry.INSTANCE.getModels(contextContainer)) {
				IResourceScope resourceScope = modelDescriptor.getScope();
				Set<IMetaModelDescriptor> mmDescriptors = resourceScopes.get(resourceScope);
				if (mmDescriptors == null) {
					mmDescriptors = new HashSet<IMetaModelDescriptor>(1);
					resourceScopes.put(resourceScope, mmDescriptors);
				}
				mmDescriptors.add(modelDescriptor.getMetaModelDescriptor());
			}
		}
		if (contextObject instanceof IModelDescriptor) {
			IModelDescriptor modelDescriptor = (IModelDescriptor) contextObject;
			resourceScopes.put(modelDescriptor.getScope(), Collections.singleton(modelDescriptor.getMetaModelDescriptor()));
		}
		if (contextObject instanceof IResourceScope) {
			resourceScopes.put((IResourceScope) contextObject, Collections.singleton(MetaModelDescriptorRegistry.ANY_MM));
		}
		return resourceScopes;
	}

	protected boolean hasMatchingMetaModel(Set<IMetaModelDescriptor> mmDescriptors, Resource resource) {
		Assert.isNotNull(mmDescriptors);

		IMetaModelDescriptor resourceMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resource);
		if (resourceMMDescriptor != null) {
			for (IMetaModelDescriptor mmDescriptor : mmDescriptors) {
				if (mmDescriptor.equals(resourceMMDescriptor) || MetaModelDescriptorRegistry.ANY_MM == mmDescriptor) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResourceSetImpl#getEObject(org.eclipse.emf.common.util.URI,
	 * org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor, java.lang.Object, boolean)
	 */
	@Override
	protected EObject getEObject(URI uri, IMetaModelDescriptor targetMetaModelDescriptor, Object contextObject, boolean loadOnDemand) {
		Assert.isNotNull(uri);

		// Fragment-based URI not knowing its target resource?
		if (uri.segmentCount() == 0) {
			// Search for object behind given URI within relevant set of potential target resources in scope
			List<Resource> resources = getResourcesToSearchIn(getResourcesInScope(contextObject), uri, targetMetaModelDescriptor);
			return safeFindEObjectInResources(resources, uri, loadOnDemand);
		} else {
			// Target resource is known, so search for object behind given URI only in that resource
			Resource resource = safeGetResource(uri.trimFragment().trimQuery(), loadOnDemand);
			if (resource != null) {
				// Do we have any context information?
				if (contextObject != null) {
					// Retrieve EObject behind URI fragment only if target resource is in scope
					List<Resource> resourcesInScope = getResourcesInScope(contextObject, true, true);
					if (resourcesInScope.contains(resource)) {
						return safeGetEObjectFromResource(resource, uri.fragment());
					}
				} else {
					// Apply default behavior and retrieve EObject behind URI fragment regardless of the
					// resource's scope
					return safeGetEObjectFromResource(resource, uri.fragment());
				}
			}
			return null;
		}
	}

	/**
	 * Determines the set of {@link Resource resource}s to be considered for resolving given {@link URI}. Only called
	 * when given URI is fragment-based (i.e., has no segments and doesn't reference any explicit target resource).
	 * <p>
	 * This implementation uses the provided {@link EObject context object}, if not <code>null</code>, to retrieve the
	 * {@link #getResourcesInScope(Object) resources in scope}, and returns {@link #getResources() all resources} in
	 * this {@link ScopingResourceSetImpl resource set} otherwise. Clients may override this method in order to tweak
	 * the set of {@link Resource resource}s used for resolving fragment-based {@link URI}s.
	 * </p>
	 * 
	 * @param uri
	 *            The fragment-based {@link URI} to resolve.
	 * @param loadOnDemand
	 *            Whether to create and load the {@link Resource target resource}, if it isn't already present in this
	 *            {@link ScopingResourceSetImpl resource set}.
	 * @param contextObject
	 *            The {@link EObject context object} used to determine the set of {@link Resource resource}s to be
	 *            considered for resolving given fragment-based {@link URI}.
	 * @return The set of {@link Resource resource}s to be considered for resolving given fragment-based {@link URI}.
	 * @see #getResourcesInScope(Object)
	 * @see #getResources()
	 * @deprecated Use {@link ExtendedResourceSetImpl#getResourcesToSearchIn(List, URI, IMetaModelDescriptor)} instead.
	 */
	@Deprecated
	protected List<Resource> getResourcesToSearchIn(URI uri, boolean loadOnDemand, EObject contextObject) {
		// Do we have any context information?
		if (contextObject != null) {
			// Only resources in scope are relevant
			return getResourcesInScope(contextObject);
		} else {
			// All resources regardless of their scope need to be considered
			return Collections.unmodifiableList(getResources());
		}
	}
}
