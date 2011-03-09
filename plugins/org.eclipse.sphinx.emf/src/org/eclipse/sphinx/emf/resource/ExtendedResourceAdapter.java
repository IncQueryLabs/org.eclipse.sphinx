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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.platform.util.ReflectUtil;

/**
 * {@link Adapter}-based implementation of {@link ExtendedResource}.
 */
public class ExtendedResourceAdapter extends AdapterImpl implements ExtendedResource {

	/**
	 * The map of options that are used to to control the handling of problems encountered while the {@link Resource
	 * resource} has been loaded or saved.
	 */
	protected Map<Object, Object> problemHandlingOptions;

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getDefaultLoadOptions()
	 */
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
	public Map<Object, Object> getProblemHandlingOptions() {
		if (problemHandlingOptions == null) {
			problemHandlingOptions = new HashMap<Object, Object>();
			problemHandlingOptions.put(OPTION_MAX_PROBLEM_MARKER_COUNT, OPTION_MAX_PROBLEM_MARKER_COUNT_DEFAULT);
			problemHandlingOptions.put(OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING, OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING_DEFAULT);
			problemHandlingOptions.put(OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING, OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING_DEFAULT);
		}
		return problemHandlingOptions;
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#unloaded(org.eclipse.emf.ecore.InternalEObject)
	 */
	public void unloaded(EObject eObject) {
		// Memory-optimized unload to be performed?
		if (getDefaultLoadOptions().get(ExtendedResource.OPTION_UNLOAD_MEMORY_OPTIMIZED) == Boolean.TRUE) {
			/*
			 * !! Important Note !! DON'T SET PROXY URIs; they take too much memory and are useless when the complete
			 * ResourceSet is unloaded, or a self-contained set of resources with no outgoing and incoming
			 * cross-document references (which typically happens when a project or the entire workbench is closed).
			 */

			// Clear all fields on unloaded EObject to make sure that it gets garbage collected as fast as possible;
			// prevent MinimalEObjectImpl's internal fields from being cleared so as to make sure that unloaded EObject
			// can still be identified as proxy
			try {
				ReflectUtil.clearAllFields(eObject, new String[] { "eFlags", "eStorage" }); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalAccessException ex) {
				// Ignore exceptions
			}

			// Leave an as short as possible dummy URI in place; this is necessary to make sure that clients which
			// access the unloaded EObject subsequently consider it as proxy just as it would be the case after a
			// regular unload
			((InternalEObject) eObject).eSetProxyURI(URI.createURI("")); //$NON-NLS-1$
		} else {
			// Perform regular unload but enable proxy creation strategy to be overridden
			if (!eObject.eIsProxy()) {
				((InternalEObject) eObject).eSetProxyURI(getURI(eObject));
			}
			eObject.eAdapters().clear();
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getURI(org.eclipse.emf.ecore.EObject)
	 */
	public URI getURI(EObject eObject) {
		return getURI(null, null, eObject);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#getURI(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject)
	 */
	public URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject) {
		Resource resource = (Resource) getTarget();

		// Has given eObject been removed or is it directly or indirectly contained by some other eObject that has been
		// removed?
		if (eObject.eResource() == null && oldOwner != null && oldFeature != null) {
			// Has given eObject an ID?
			String id = EcoreUtil.getID(eObject);
			if (id != null) {
				// Proceed as in EcoreUtil#getURI()/ResourceImpl#getURIFragment() and return a URI with object ID as
				// fragment
				return resource.getURI().appendFragment(id);
			} else {
				// Use provided oldOwner and oldFeature to calculate given eObject's old URI that it had prior to the
				// removal

				// Retrieve the oldOwner's URI fragment
				URI oldOwnerURI = EcoreUtil.getURI(oldOwner);
				String oldOwnerURIFragment = oldOwnerURI.fragment();

				// Restore URI fragment segment that pointed from oldOwner to removed eObject (which may be the given
				// eObject itself or some other eObject that directly or indirectly contains given eObject
				EObject eObjectRootContainer = EcoreUtil.getRootContainer(eObject);
				String eObjectRootContainerURIFragmentSegment = ((InternalEObject) oldOwner).eURIFragmentSegment(oldFeature, eObjectRootContainer);

				// Calculate URI fragment segments for given eObject in case that it is an eObject that is directly or
				// indirectly contained by the removed eObject
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
			}
		}

		// Calculate and return normal URI, i.e., same as EcoreUtil#getURI() would
		return resource.getURI().appendFragment(resource.getURIFragment(eObject));
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#validateURI(java.lang.String)
	 */
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
