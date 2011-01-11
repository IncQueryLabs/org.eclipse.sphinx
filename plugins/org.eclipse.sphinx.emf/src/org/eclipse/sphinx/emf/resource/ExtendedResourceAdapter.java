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

import java.util.HashMap;
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
	public void unloaded(InternalEObject internalEObject) {
		// Memory-optimized unload to be performed?
		if (getDefaultLoadOptions().get(ExtendedResource.OPTION_UNLOAD_MEMORY_OPTIMIZED) == Boolean.TRUE) {
			/*
			 * !! Important Note !! DON'T SET PROXY URIs; they take too much memory and are useless when the complete
			 * ResourceSet is unloaded, or a self-contained set of resources with no outgoing and incoming
			 * cross-document references (which typically happens when a project or the entire workbench is closed).
			 */

			// Clear all fields on unloaded EObject to make sure that it gets garbage collected as fast as possible
			try {
				ReflectUtil.clearAllFields(internalEObject);
			} catch (IllegalAccessException ex) {
				// Ignore exceptions
			}

			// Leave an as short as possible dummy URI in place; this is necessary to avoid NullPointerExceptions when
			// it happens that things like ECrossReferenceAdapter try to access the proxy URI of unloaded EObjects (see
			// e.g., org.eclipse.emf.ecore.util.ECrossReferenceAdapter.InverseCrossReferencer.addProxy(EObject,
			// EObject))
			internalEObject.eSetProxyURI(URI.createURI("")); //$NON-NLS-1$
		} else {
			// Perform regular unload but enable proxy creation strategy to be overridden
			if (!internalEObject.eIsProxy()) {
				internalEObject.eSetProxyURI(createProxyURI(internalEObject));
			}
			internalEObject.eAdapters().clear();
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#createProxyURI(org.eclipse.emf.ecore.InternalEObject)
	 */
	public URI createProxyURI(InternalEObject internalEObject) {
		return createProxyURI(null, null, internalEObject);
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

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResource#createProxyURI(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject)
	 */
	public URI createProxyURI(EObject owner, EStructuralFeature feature, EObject eObject) {
		Resource resource = (Resource) getTarget();
		String eObjectURIFragment = resource.getURIFragment(eObject);
		String eObjectURIFragmentSegment = ((InternalEObject) owner).eURIFragmentSegment(feature, eObject);
		URI ownerURI = EcoreUtil.getURI(owner);
		String uriFragment = ownerURI.fragment() + "/" + eObjectURIFragmentSegment + "/" + eObjectURIFragment; //$NON-NLS-1$ //$NON-NLS-2$
		return ownerURI.trimFragment().appendFragment(uriFragment);
	}

	/*
	 * @see org.artop.ecl.emf.resource.ExtendedResource#validateURI(java.lang.String)
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
}
