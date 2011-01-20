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
package org.eclipse.sphinx.examples.hummingbird20.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter;

/**
 * {@link Adapter}-based implementation of {@link ExtendedResource} for Hummingbird.
 */
public class ExtendedHummingbirdResourceAdapter extends ExtendedResourceAdapter {

	/**
	 * Arbitrary scheme for Hummingbird URIs enabling to reference Hummingbird elements without having to precise the
	 * files in which they are located.
	 */
	public static final String HB_SCHEME = "hb"; //$NON-NLS-1$

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter#getURI(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject) {
		URI uri = super.getURI(oldOwner, oldFeature, eObject);
		// Make sure that cross-document references to Hummingbird 2.0 resources are fragment-based
		return createHummingbirdURI(uri.fragment());
	}

	/**
	 * Creates a fragment-based Hummingbird 2.0 {@link URI} from given {@link URI} fragment.
	 * 
	 * @param uriFragment
	 *            The {@link URI} fragment the URI to handled.
	 * @return The resulting fragment-based Hummingbird 2.0 {@link URI}.
	 */
	protected static URI createHummingbirdURI(String uriFragment) {
		if (uriFragment != null && uriFragment.length() > 0) {
			return URI.createURI(HB_SCHEME + URI_SCHEME_SEPARATOR + URI_SEGMENT_SEPARATOR + URI_FRAGMENT_SEPARATOR + uriFragment, true);
		}
		return null;
	}
}
