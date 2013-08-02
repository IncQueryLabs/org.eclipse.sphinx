/**
 * <copyright>
 * 
 * Copyright (c) 2013 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     itemis - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;

/**
 * A helper class for creating and analyzing {@link URI}s that carry the target {@link IMetaModelDescriptor metamodel
 * descriptor} and the source {@link URI} as context information.
 */
public class ContextAwareURIHelper {

	private static final String CONTEXT_AWARE_URI_QUERY_KEY_TARGET_METAMODEL_DESCRIPTOR = "tgtMMD"; //$NON-NLS-1$
	private static final Pattern CONTEXT_AWARE_URI_QUERY_VALUE_PATTERN_TARGET_METAMODEL_DESCRIPTOR = createURIQueryValuePattern(CONTEXT_AWARE_URI_QUERY_KEY_TARGET_METAMODEL_DESCRIPTOR);

	private static final String CONTEXT_AWARE_URI_QUERY_KEY_CONTEXT_URI = "ctxURI"; //$NON-NLS-1$
	private static final Pattern CONTEXT_AWARE_URI_QUERY_VALUE_PATTERN_CONTEXT_URI = createURIQueryValuePattern(CONTEXT_AWARE_URI_QUERY_KEY_CONTEXT_URI);

	private static Pattern createURIQueryValuePattern(String key) {
		String amp = ExtendedResource.URI_KEY_VALUE_PAIR_SEPARATOR;
		String eq = ExtendedResource.URI_KEY_VALUE_SEPARATOR;
		// Regular expression in clear text: ([^&]+&)*key=([^&]*)(&[^&]+)*
		return Pattern.compile("([^" + amp + "]+" + amp + ")*" + key + eq + "([^" + amp + "]*)(" + amp + "[^" + amp + "]+)*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	}

	private static final int CONTEXT_AWARE_URI_QUERY_VALUE_GROUP_IDX = 2;

	/**
	 * Augments the {@link URI} of given {@link InternalEObject proxy} to a context-aware URI by adding key/value pairs
	 * that contain the target {@link IMetaModelDescriptor metamodel descriptor} and context {@link URI} to the
	 * {@link URI#query() query string} of the proxy URI.
	 */
	public void augmentToContextAwareURI(EObject proxy, URI contextURI) {
		Assert.isNotNull(proxy);

		URI proxyURI = ((InternalEObject) proxy).eProxyURI();
		StringBuilder newQuery = new StringBuilder();
		String oldQuery = proxyURI.query();
		if (oldQuery != null) {
			newQuery.append(oldQuery);
		}

		if (newQuery.length() > 0) {
			newQuery.append(ExtendedResource.URI_KEY_VALUE_PAIR_SEPARATOR);
		}

		IMetaModelDescriptor metaModelDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(proxy);
		if (metaModelDescriptor != null) {
			newQuery.append(CONTEXT_AWARE_URI_QUERY_KEY_TARGET_METAMODEL_DESCRIPTOR);
			newQuery.append(ExtendedResource.URI_KEY_VALUE_SEPARATOR);
			newQuery.append(metaModelDescriptor.getIdentifier());
		}

		if (newQuery.length() > 0) {
			newQuery.append(ExtendedResource.URI_KEY_VALUE_PAIR_SEPARATOR);
		}

		if (contextURI != null) {
			newQuery.append(CONTEXT_AWARE_URI_QUERY_KEY_CONTEXT_URI);
			newQuery.append(ExtendedResource.URI_KEY_VALUE_SEPARATOR);
			newQuery.append(contextURI);
		}

		proxyURI = URI.createHierarchicalURI(proxyURI.scheme(), proxyURI.authority(), proxyURI.device(), proxyURI.segments(), newQuery.toString(),
				proxyURI.fragment());
		((InternalEObject) proxy).eSetProxyURI(proxyURI);
	}

	/**
	 * Extracts the identifier of the {@link IMetaModelDescriptor metamodel descriptor} carried by given context-aware
	 * {@link URI}.
	 */
	public String getMetaModelDescriptorId(URI uri) {
		Assert.isNotNull(uri);

		String query = uri.query();
		if (query != null) {
			Matcher matcher = CONTEXT_AWARE_URI_QUERY_VALUE_PATTERN_TARGET_METAMODEL_DESCRIPTOR.matcher(query);
			if (matcher.matches()) {
				return matcher.group(CONTEXT_AWARE_URI_QUERY_VALUE_GROUP_IDX);
			}
		}
		return null;
	}

	/**
	 * Extracts the context {@link URI} carried by given context-aware {@link URI}.
	 */
	public URI getContextURI(URI uri) {
		Assert.isNotNull(uri);

		String query = uri.query();
		if (query != null) {
			Matcher matcher = CONTEXT_AWARE_URI_QUERY_VALUE_PATTERN_CONTEXT_URI.matcher(query);
			if (matcher.matches()) {
				String contextURI = matcher.group(CONTEXT_AWARE_URI_QUERY_VALUE_GROUP_IDX);
				return URI.createURI(contextURI);
			}
		}
		return null;
	}
}
