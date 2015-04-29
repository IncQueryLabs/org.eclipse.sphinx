/**
 * <copyright>
 *
 * Copyright (c) 2008-2015 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [400897] ExtendedResourceAdapter's approach of reflectively clearing all EObject fields when performing memory-optimized unloads bears the risk of leaving some EObjects leaked
 *     itemis - [441970] Result returned by ExtendedResourceAdapter#getHREF(EObject) must default to complete object URI (edit)
 *     itemis - [442342] Sphinx doen't trim context information from proxy URIs when serializing proxyfied cross-document references
 *     itemis - [443647] Enable HREF representing serialized cross-document references to be customized through ExtendedResource of resource being serialized
 *     itemis - [458862] Navigation from problem markers in Check Validation view to model editors and Model Explorer view broken
 *     itemis - [460260] Expanded paths are collapsed on resource reload
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.ReflectUtil;

/**
 * {@link Adapter}-based implementation of {@link ExtendedResource}.
 */
public class ExtendedResourceAdapter extends AdapterImpl implements ExtendedResource {

	private static final URI EMPTY_URI = URI.createURI(""); //$NON-NLS-1$

	/**
	 * The map of options that are used to to control the handling of problems encountered while the {@link Resource
	 * resource} has been loaded or saved.
	 */
	protected Map<Object, Object> problemHandlingOptions;

	/**
	 * Whether to use context-aware proxy URIs or not.
	 */
	private Boolean useContextAwareProxyURIs = null;

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getDefaultLoadOptions()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getDefaultLoadOptions() {
		Map<Object, Object> defaultLoadOptions = null;
		Resource targetResource = (Resource) getTarget();
		if (targetResource instanceof XMLResource) {
			defaultLoadOptions = ((XMLResource) targetResource).getDefaultLoadOptions();
		} else {
			try {
				defaultLoadOptions = (Map<Object, Object>) ReflectUtil.getInvisibleFieldValue(targetResource, "defaultLoadOptions"); //$NON-NLS-1$
			} catch (Exception ex) {
				// Ignore exception
			}
		}
		return defaultLoadOptions != null ? defaultLoadOptions : new HashMap<Object, Object>();
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getDefaultSaveOptions()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getDefaultSaveOptions() {
		Map<Object, Object> defaultSaveOptions = null;
		Resource targetResource = (Resource) getTarget();
		if (targetResource instanceof XMLResource) {
			defaultSaveOptions = ((XMLResource) targetResource).getDefaultLoadOptions();
		} else {
			try {
				defaultSaveOptions = (Map<Object, Object>) ReflectUtil.getInvisibleFieldValue(targetResource, "defaultSaveOptions"); //$NON-NLS-1$
			} catch (Exception ex) {
				// Ignore exception
			}
		}
		return defaultSaveOptions != null ? defaultSaveOptions : new HashMap<Object, Object>();
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getLoadProblemOptions()
	 */
	@Override
	public Map<Object, Object> getProblemHandlingOptions() {
		if (problemHandlingOptions == null) {
			problemHandlingOptions = new HashMap<Object, Object>();
			problemHandlingOptions.put(OPTION_MAX_PROBLEM_MARKER_COUNT, OPTION_MAX_PROBLEM_MARKER_COUNT_DEFAULT);
			problemHandlingOptions.put(OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING, OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING_DEFAULT);
			problemHandlingOptions.put(OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING, OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING_DEFAULT);
		}
		return problemHandlingOptions;
	}

	protected boolean isUseContextAwareProxyURIs() {
		if (useContextAwareProxyURIs == null) {
			Map<Object, Object> loadOptions = getDefaultLoadOptions();
			useContextAwareProxyURIs = !Boolean.FALSE.equals(loadOptions.get(OPTION_USE_CONTEXT_AWARE_PROXY_URIS));
		}
		return useContextAwareProxyURIs;
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#unloaded(org.eclipse.emf.ecore.InternalEObject)
	 */
	@Override
	public void unloaded(EObject eObject) {
		// Remove all adapters from unloaded eObject
		eObject.eAdapters().clear();

		// Memory-optimized unload to be performed?
		if (getDefaultLoadOptions().get(ExtendedResource.OPTION_UNLOAD_MEMORY_OPTIMIZED) == Boolean.TRUE) {
			// Turn unloaded eObject into a proxy using an as short as possible dummy URI
			/*
			 * !! Important Note !! Setting the regular full proxy URI would take way too much memory and is generally
			 * useless when the complete ResourceSet, or a self-contained set of resources with no outgoing and incoming
			 * cross-document references gets unloaded (which typically happens when a project or the entire workbench
			 * is closed). However, we must leave an as short as possible dummy URI in place to make sure that clients
			 * which access the unloaded eObject for whatever reason subsequently don't end up considering it a regular
			 * eObject that is still loaded.
			 */
			((InternalEObject) eObject).eSetProxyURI(EMPTY_URI);
		} else {
			// Turn unloaded eObject into a proxy using the regular full proxy URI; enable proxy URI creation to be
			// customized by delegating it to #getURI()
			URI uri = getURI(eObject);
			if (!eObject.eIsProxy()) {
				((InternalEObject) eObject).eSetProxyURI(uri);
			}

			// Is proxy URI a fragment-based URI not knowing the resource containing the eObject it refers to?
			if (uri != null && uri.segmentCount() == 0) {
				// Add adapter to unloaded eObject keeping track of the resource which formerly contained it
				eObject.eAdapters().add(new OldResourceProviderAdapter((Resource) getTarget()));
			}
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getURI(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public URI getURI(EObject eObject) {
		return getURI(null, null, eObject, false);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getURI(org.eclipse.emf.ecore.EObject, boolean)
	 */
	@Override
	public URI getURI(EObject eObject, boolean resolve) {
		return getURI(null, null, eObject, resolve);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getURI(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject) {
		return getURI(oldOwner, oldFeature, eObject, false);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getURI(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject, boolean)
	 */
	@Override
	public URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject, boolean resolve) {
		Assert.isNotNull(eObject);
		URI uri;

		// eObject not yet removed?
		if (eObject.eResource() != null || eObject.eIsProxy()) {
			// Compute the given eObject's URI in the same way as EMF would do normally
			uri = EcoreUtil.getURI(eObject);
		} else {
			// Restore the given eObject's old URI that it had before it was removed
			uri = restoreURI(oldOwner, oldFeature, eObject);
		}

		// Is URI a fragment-based URI not knowing the resource containing the eObject it refers to but expected to be
		// resolved against its underlying resource?
		if (uri.segmentCount() == 0 && resolve) {
			uri = resolveURI(oldOwner, eObject, uri);
		}

		return uri;
	}

	protected URI internalGetURI(EObject eObject) {
		return EcoreUtil.getURI(eObject);
	}

	protected URI restoreURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject) {
		Assert.isNotNull(eObject);

		if (oldOwner != null && oldFeature != null) {
			// Retrieve the oldOwner's URI fragment
			URI oldOwnerURI = EcoreResourceUtil.getURI(oldOwner);
			String oldOwnerURIFragment = oldOwnerURI.fragment();

			// Restore URI fragment segment that pointed from oldOwner to removed eObject (which may be the
			// given eObject itself or some other eObject that directly or indirectly contains given eObject
			EObject eObjectRootContainer = EcoreUtil.getRootContainer(eObject);
			String eObjectRootContainerURIFragmentSegment = ((InternalEObject) oldOwner).eURIFragmentSegment(oldFeature, eObjectRootContainer);

			// Calculate URI fragment segments for given eObject in case that it is an eObject that is directly
			// or indirectly contained by the removed eObject
			List<String> eObjectURIFragmentSegments = new ArrayList<String>();
			InternalEObject internalEObject = (InternalEObject) eObject;
			for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container = internalEObject
					.eInternalContainer()) {
				eObjectURIFragmentSegments.add(container.eURIFragmentSegment(internalEObject.eContainingFeature(), internalEObject));
				internalEObject = container;
			}

			// Compose and return the eObject' old URI
			StringBuilder oldEObjectURIFragment = new StringBuilder();
			oldEObjectURIFragment.append(oldOwnerURIFragment);
			oldEObjectURIFragment.append(URI_SEGMENT_SEPARATOR);
			oldEObjectURIFragment.append(eObjectRootContainerURIFragmentSegment);
			for (int i = eObjectURIFragmentSegments.size() - 1; i >= 0; --i) {
				oldEObjectURIFragment.append(URI_SEGMENT_SEPARATOR);
				oldEObjectURIFragment.append(eObjectURIFragmentSegments.get(i));
			}
			return oldOwnerURI.trimFragment().appendFragment(oldEObjectURIFragment.toString());
		} else {
			// Try to calculate the given eObject's URI in the same way as EcoreUtil#getURI() would, but use target
			// resource rather than object resource for that purpose
			Resource targetResource = (Resource) getTarget();
			URI targetResourceURI = targetResource.getURI();
			String uriFragment = targetResource.getURIFragment(eObject);
			return targetResourceURI == null ? URI.createURI(URI_FRAGMENT_SEPARATOR + uriFragment) : targetResourceURI.appendFragment(uriFragment);
		}
	}

	protected URI resolveURI(EObject oldOwner, EObject eObject, URI uri) {
		Assert.isNotNull(eObject);
		Assert.isNotNull(uri);

		// Retrieve the resource of given eObject
		Resource resource = eObject.eResource();
		if (resource == null) {
			// Retrieve the given eObject's old resource by referring to the eObject's old owner if any or to target
			// resource otherwise
			if (oldOwner != null) {
				resource = oldOwner.eResource();
			} else {
				resource = (Resource) getTarget();
			}
		}

		// Construct resolved eObject URI by using the URI of its resource as prefix and its URI fragment as postfix
		return resource.getURI().appendFragment(uri.fragment());
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#createURI(java.lang.String, org.eclipse.emf.ecore.EClass)
	 */
	@Override
	public URI createURI(String uriLiteral, EClass eClass) {
		// Return URI object corresponding to given URI literal as is
		return URI.createURI(uriLiteral);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getHREF(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public URI getHREF(EObject eObject) {
		// Let HREF default to URI of given object
		return getURI(eObject);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#validateURI(java.lang.String)
	 */
	@Override
	public Diagnostic validateURI(String uri) {
		Assert.isNotNull(uri);
		try {
			URI.createURI(uri, true);
		} catch (IllegalArgumentException ex) {
			return new BasicDiagnostic(Activator.getPlugin().getSymbolicName(), Diagnostic.ERROR, ex.getMessage(), new Object[] {});
		}
		return Diagnostic.OK_INSTANCE;
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#augmentToContextAwareProxy(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void augmentToContextAwareProxy(EObject proxy) {
		// Context-aware proxy URIs required?
		if (isUseContextAwareProxyURIs()) {
			Resource contextResource = (Resource) getTarget();
			ResourceSet resourceSet = contextResource.getResourceSet();

			// Augment the proxy's URI to a context-aware proxy URI
			if (resourceSet instanceof ExtendedResourceSet) {
				((ExtendedResourceSet) resourceSet).augmentToContextAwareProxy(proxy, contextResource);
			}
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#trimProxyContextInfo(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public URI trimProxyContextInfo(URI proxyURI) {
		// Context-aware proxy URIs being used?
		if (isUseContextAwareProxyURIs()) {
			Resource contextResource = (Resource) getTarget();
			ResourceSet resourceSet = contextResource.getResourceSet();

			// Trim all proxy context information
			if (resourceSet instanceof ExtendedResourceSet) {
				return ((ExtendedResourceSet) resourceSet).trimProxyContextInfo(proxyURI);
			}
		}

		return proxyURI;
	}

	/*
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	@Override
	public boolean isAdapterForType(Object type) {
		return type == ExtendedResource.class;
	}

	/*
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void setTarget(Notifier newTarget) {
		Assert.isLegal(newTarget == null || newTarget instanceof Resource);
		super.setTarget(newTarget);
	}
}
