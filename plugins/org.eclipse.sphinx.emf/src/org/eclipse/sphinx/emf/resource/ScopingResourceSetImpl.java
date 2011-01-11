/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.internal.resource.ResourceProblemMarkerService;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.scoping.IResourceScope;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

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
			Map<IResourceScope, Set<IMetaModelDescriptor>> contextResourceScopes = getResourceScopes(contextObject, contextResource);

			// Collect resources which belong to same resource scope(s) and metamodel(s) as the context object does
			HashSet<Resource> resourcesInScope = new HashSet<Resource>();
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
			Set<Resource> resourcesInScope = new HashSet<Resource>();
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

	protected Map<IResourceScope, Set<IMetaModelDescriptor>> getResourceScopes(Object object, Resource containerResource) {
		Map<IResourceScope, Set<IMetaModelDescriptor>> resourceScopes = new HashMap<IResourceScope, Set<IMetaModelDescriptor>>(1);
		if (object instanceof IContainer) {
			IContainer container = (IContainer) object;
			for (IModelDescriptor modelDescriptor : ModelDescriptorRegistry.INSTANCE.getModels(container)) {
				IResourceScope resourceScope = modelDescriptor.getScope();
				Set<IMetaModelDescriptor> mmDescriptors = resourceScopes.get(resourceScope);
				if (mmDescriptors == null) {
					mmDescriptors = new HashSet<IMetaModelDescriptor>(1);
					resourceScopes.put(resourceScope, mmDescriptors);
				}
				mmDescriptors.add(modelDescriptor.getMetaModelDescriptor());
			}
		} else if (object instanceof IModelDescriptor) {
			IModelDescriptor modelDescriptor = (IModelDescriptor) object;
			resourceScopes.put(modelDescriptor.getScope(), Collections.singleton(modelDescriptor.getMetaModelDescriptor()));
		} else if (object instanceof IResourceScope) {
			resourceScopes.put((IResourceScope) object, Collections.singleton(MetaModelDescriptorRegistry.ANY_MM));
		} else {
			IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(containerResource);
			if (modelDescriptor != null) {
				resourceScopes.put(modelDescriptor.getScope(), Collections.singleton(modelDescriptor.getMetaModelDescriptor()));
			}
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
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getEObject(org.eclipse.emf.common.util.URI, boolean)
	 */
	@Override
	public EObject getEObject(URI uri, boolean loadOnDemand) {
		return getEObjectInScope(uri, loadOnDemand, null);
	}

	/**
	 * Resolves given {@link URI} and returns corresponding {@link EObject}. If the {@link URI} is fragment-based (i.e.,
	 * has no segments and doesn't reference any explicit target resource) the provided {@link EObject context object}
	 * is used to determine the set of {@link Resource resource}s to be considered for resolving it. In case that given
	 * {@link URI} is a fragment-based {@link proxy URI} the {@link EObject context object} typically is the
	 * {@link EObject owner} of the {@link EReference reference} which points at the {@link EObject proxy}.
	 * 
	 * @param uri
	 *            The {@link URI} to resolve.
	 * @param loadOnDemand
	 *            Whether to create and load the {@link Resource target resource}, if it isn't already present in this
	 *            {@link ScopingResourceSetImpl resource set}.
	 * @param contextObject
	 *            The {@link EObject context object} used to determine the set of {@link Resource resource}s to be
	 *            considered for resolving given {@link URI} in case that is is a fragment-based {@link URI}.
	 * @return The {@link EObject} behind given {@link URI}, or <code>null</code> if given {@link URI} cannot be
	 *         resolved.
	 * @see #getResourcesToSearchIn(URI, boolean, EObject)
	 * @see #findEObject(URI, boolean, List)
	 */
	public EObject getEObjectInScope(URI uri, boolean loadOnDemand, EObject contextObject) {
		if (uri == null) {
			return null;
		}

		// Fragment-based URI not knowing its target resource?
		if (uri.segmentCount() == 0) {
			// Determine set of resources to search in
			List<Resource> resources = getResourcesToSearchIn(uri, loadOnDemand, contextObject);

			// Search for object behind given URI inside these resources
			return findEObject(uri, loadOnDemand, resources);
		} else {
			// Target resource is known, proceed normally

			// Just get resource behind URI segments if it is already loaded
			Resource resource = getResource(uri.trimFragment(), false);

			// Load it if not done so yet and a demand load has been requested
			if ((resource == null || !resource.isLoaded()) && loadOnDemand) {
				try {
					resource = getResource(uri.trimFragment(), true);
				} catch (Exception ex) {
					try {
						// Check if some resource has been created for given URI and added to the resource set
						/*
						 * !! Important Note !! Don't rely on resource returned by previous call to
						 * ResourceSet#getResource() but try to retrieve it again because ResourceSet#getResource() may
						 * fail and return null but resource for given URI may all the same have been added to resource
						 * set.
						 */
						resource = getResource(uri.trimFragment(), false);
						if (resource != null) {
							// Make sure that resource gets unloaded and removed from resource set again
							EcoreResourceUtil.unloadResource(resource, true);

							// Exception due to something different than that resource does not exist?
							if (EcoreResourceUtil.exists(resource.getURI())) {
								// Leave an error about what has happened on resource
								Throwable cause = ex.getCause();
								if (cause instanceof XMIException) {
									resource.getErrors().add((XMIException) cause);
								} else {
									Exception causeEx = cause instanceof Exception ? (Exception) cause : null;
									resource.getErrors().add(
											new XMIException(NLS.bind(Messages.error_problemOccurredWhenLoadingResource, uri.toString()), causeEx,
													uri.toString(), 1, 1));
								}
							}
						} else {
							// Leave a trace about what has happened in error log
							PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
						}
					} catch (Exception e) {
						// Log original exception in error log if something goes wrong
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					}
				}
			}

			// Any resource available and loaded now?
			if (resource != null) {
				if (resource.isLoaded()) {
					try {
						// Do we have any context information?
						if (contextObject != null) {
							// Retrieve EObject behind URI fragment only if resource is in scope
							List<Resource> resourcesInScope = getResourcesInScope(contextObject, true, true);
							if (resourcesInScope.contains(resource)) {
								return resource.getEObject(uri.fragment());
							}
						} else {
							// Apply default behavior and retrieve EObject behind URI fragment regardless of the
							// resource's scope
							return resource.getEObject(uri.fragment());
						}
					} catch (Exception ex) {
						// Leave an error about what has happened on resource
						resource.getErrors().add(
								new ProxyURIIntegrityException(NLS.bind(Messages.error_problemOccurredWhenResolvingProxyURI, uri), ex));
					}
				}

				// Handle problems that may have been encountered during proxy resolution
				ResourceProblemMarkerService.INSTANCE.updateProblemMarkers(resource, true, null);
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
	 */
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

	/**
	 * Resolves given {@link URI} against given set of {@link Resource resource}s.Only called when given URI is
	 * fragment-based (i.e., has no segments and doesn't reference any explicit target resource).
	 * 
	 * @param uri
	 *            The fragment-based {@link URI} to resolve.
	 * @param loadOnDemand
	 *            Whether to create and load the {@link Resource target resource}, if it isn't already present in this
	 *            {@link ScopingResourceSetImpl resource set}.
	 * @param resources
	 *            The set of {@link Resource resource}s to resolve given fragment-based {@link URI} against.
	 * @return The {@link EObject} behind given fragment-based {@link URI}, or <code>null</code> if given {@link URI}
	 *         cannot be resolved.
	 */
	protected EObject findEObject(URI uri, boolean loadOnDemand, List<Resource> resources) {
		if (uri == null) {
			return null;
		}

		// Target element may be in any of the specified resources
		EObject resolvedEObject = null;
		for (Resource resource : resources) {
			try {
				EObject eObject = resource.getEObject(uri.fragment());
				if (eObject != null) {
					resolvedEObject = eObject;
					break;
				}
			} catch (Exception ex) {
				resource.getErrors().add(new ProxyURIIntegrityException(NLS.bind(Messages.error_problemOccurredWhenResolvingProxyURI, uri), ex));
			}
		}

		// Handle problems that may have been encountered during proxy resolution
		ResourceProblemMarkerService.INSTANCE.updateProblemMarkers(resources, true, null);

		return resolvedEObject;
	}
}
