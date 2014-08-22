/**
 * <copyright>
 *
 * Copyright (c) 2008-2014 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [441970] Result returned by ExtendedResourceAdapter#getHREF(EObject) must default to complete object URI (edit)
 *     itemis - [442342] Sphinx doen't trim context information from proxy URIs when serializing proxyfied cross-document references
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

	/**
	 * Creates a fragment-based Hummingbird 2.0 {@link URI} from given original {@link URI}.
	 *
	 * @param uri
	 *            The original {@link URI} to be handled.
	 * @return The resulting fragment-based Hummingbird 2.0 {@link URI}.
	 */
	protected URI createHummingbirdURI(URI uri) {
		if (uri != null && !HB_SCHEME.equals(uri.scheme())) {
			return URI.createURI(HB_SCHEME + URI_SCHEME_SEPARATOR + URI_SEGMENT_SEPARATOR + URI_FRAGMENT_SEPARATOR + uri.fragment(), true);
		}
		return uri;
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter#getURI(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject) {
		// Get full URI
		URI uri = super.getURI(oldOwner, oldFeature, eObject);

		// Convert full URI into a fragment-based Hummingbird 2.0 URI
		return createHummingbirdURI(uri);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter#createURI(java.lang.String)
	 */
	@Override
	public URI createURI(String uriLiteral) {
		// Is given URI literal a fragment-only URI string?
		if (uriLiteral.startsWith(URI_FRAGMENT_SEPARATOR)) {
			// Create corresponding fragment-only URI object
			URI uri = URI.createURI(uriLiteral);

			// Create and return corresponding fragment-based Hummingbird 2.0 URI
			return createHummingbirdURI(uri);
		}

		// Backward compatibility: Is given URI literal a fragment-only URI string without leading fragment separator?
		if (!uriLiteral.contains(URI_FRAGMENT_SEPARATOR)) {
			// Create corresponding fragment-only URI object
			URI uri = URI.createURI(URI_FRAGMENT_SEPARATOR + uriLiteral);

			// Create and return corresponding fragment-based Hummingbird 2.0 URI
			return createHummingbirdURI(uri);
		}

		return super.createURI(uriLiteral);
	}

	/*
	 * @see org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter#getHREF(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public URI getHREF(EObject eObject) {
		// Return a fragment-only URI of given object as HREF
		URI uri = getURI(eObject);
		return URI.createURI(URI_FRAGMENT_SEPARATOR + uri.fragment());
	}
}
