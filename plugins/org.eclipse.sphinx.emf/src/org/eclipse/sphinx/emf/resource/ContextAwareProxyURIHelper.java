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
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
 *     itemis - [421205] Model descriptor registry does not return correct model descriptor for (shared) plugin resources
 *     
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;

/**
 * A helper class for creating and analyzing proxy {@link URI}s that carry the target {@link IMetaModelDescriptor
 * metamodel descriptor} and the context {@link URI} as context information.
 */
public class ContextAwareProxyURIHelper {

	private static final String CONTEXT_AWARE_PROXY_URI_QUERY_KEY_TARGET_METAMODEL_DESCRIPTOR = "tgtMMD"; //$NON-NLS-1$
	private static final Pattern CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_PATTERN_TARGET_METAMODEL_DESCRIPTOR = createURIQueryValuePattern(CONTEXT_AWARE_PROXY_URI_QUERY_KEY_TARGET_METAMODEL_DESCRIPTOR);

	private static final String CONTEXT_AWARE_PROXY_URI_QUERY_KEY_CONTEXT_URI = "ctxURI"; //$NON-NLS-1$
	private static final Pattern CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_PATTERN_CONTEXT_URI = createURIQueryValuePattern(CONTEXT_AWARE_PROXY_URI_QUERY_KEY_CONTEXT_URI);

	private static Pattern createURIQueryValuePattern(String key) {
		String amp = ExtendedResource.URI_KEY_VALUE_PAIR_SEPARATOR;
		String eq = ExtendedResource.URI_KEY_VALUE_SEPARATOR;

		// Regular expression in clear text: ([^&]+&)*key=([^&]*)(&[^&]+)*
		return Pattern.compile("([^" + amp + "]+" + amp + ")*" + key + eq + "([^" + amp + "]*)(" + amp + "[^" + amp + "]+)*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	}

	private static final int CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_GROUP_IDX = 2;

	/**
	 * Augments given {@link InternalEObject proxy} to a context-aware proxy by adding key/value pairs that contain the
	 * target {@link IMetaModelDescriptor metamodel descriptor} and a context {@link URI} to the {@link URI#query()
	 * query string} of the proxy URI.
	 */
	public void augmentToContextAwareProxy(EObject proxy, Resource contextResource) {
		Assert.isNotNull(proxy);
		Assert.isNotNull(contextResource);

		// Build target metamodel descriptor query field
		StringBuilder targetMMDescriptorQueryField = null;
		IMetaModelDescriptor proxyMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(proxy);
		IMetaModelDescriptor contextMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(contextResource);
		if (proxyMMDescriptor != null) {
			/*
			 * Performance optimization: Add target metamodel descriptor field to context-aware URI only when metamodel
			 * behind proxy is different from that of the context model, i.e. the model that references it. The presence
			 * of the target metamodel descriptor is only required for resolving proxies across different metamodels and
			 * would needlessly blow up the proxy URI size otherwise.
			 */
			if (!proxyMMDescriptor.equals(contextMMDescriptor)) {
				targetMMDescriptorQueryField = new StringBuilder();
				targetMMDescriptorQueryField.append(CONTEXT_AWARE_PROXY_URI_QUERY_KEY_TARGET_METAMODEL_DESCRIPTOR);
				targetMMDescriptorQueryField.append(ExtendedResource.URI_KEY_VALUE_SEPARATOR);
				targetMMDescriptorQueryField.append(proxyMMDescriptor.getIdentifier());
			}
		}

		// Build context URI query field
		URI contextURI = null;
		IModelDescriptor contextModelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(contextResource);
		if (contextModelDescriptor != null) {
			/*
			 * Performance optimization: Don't use URI of context resource itself but only the URI of the root of the
			 * model that contains the context resource as context URI. The latter is sufficient for identifying the
			 * context and indicating it to applications relying on it (e.g., resource scoping). As it is nearly all of
			 * the time significantly shorter than the context resource URI it greatly helps to avoid that proxy URIs
			 * grow too long.
			 */
			IPath rootPath = contextModelDescriptor.getRoot().getFullPath();
			contextURI = URI.createPlatformResourceURI(rootPath.toString(), true);
		} else {
			contextURI = contextResource.getURI();
		}
		StringBuilder contextURIQueryField = new StringBuilder();
		contextURIQueryField.append(CONTEXT_AWARE_PROXY_URI_QUERY_KEY_CONTEXT_URI);
		contextURIQueryField.append(ExtendedResource.URI_KEY_VALUE_SEPARATOR);
		contextURIQueryField.append(contextURI);

		// Augment existing proxy URI with target metamodel descriptor and context URI query fields
		URI proxyURI = ((InternalEObject) proxy).eProxyURI();
		StringBuilder newQuery = new StringBuilder();

		String oldQuery = proxyURI.query();
		if (oldQuery != null) {
			newQuery.append(oldQuery);
			newQuery.append(ExtendedResource.URI_KEY_VALUE_PAIR_SEPARATOR);
		}

		if (targetMMDescriptorQueryField != null) {
			newQuery.append(targetMMDescriptorQueryField);
			newQuery.append(ExtendedResource.URI_KEY_VALUE_PAIR_SEPARATOR);
		}

		newQuery.append(contextURIQueryField);

		proxyURI = URI.createHierarchicalURI(proxyURI.scheme(), proxyURI.authority(), proxyURI.device(), proxyURI.segments(), newQuery.toString(),
				proxyURI.fragment());
		((InternalEObject) proxy).eSetProxyURI(proxyURI);
	}

	/**
	 * Extracts the identifier of the target {@link IMetaModelDescriptor metamodel descriptor} carried by given
	 * context-aware proxy {@link URI}.
	 */
	public String getTargetMetaModelDescriptorId(URI uri) {
		Assert.isNotNull(uri);

		String query = uri.query();
		if (query != null) {
			Matcher matcher = CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_PATTERN_TARGET_METAMODEL_DESCRIPTOR.matcher(query);
			if (matcher.matches()) {
				return matcher.group(CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_GROUP_IDX);
			}
		}

		// No information about target metamodel descriptor available on proxy URI
		return null;
	}

	/**
	 * Extracts the context {@link URI} carried by given context-aware proxy {@link URI}.
	 */
	public URI getContextURI(URI uri) {
		Assert.isNotNull(uri);

		String query = uri.query();
		if (query != null) {
			Matcher matcher = CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_PATTERN_CONTEXT_URI.matcher(query);
			if (matcher.matches()) {
				String contextURI = matcher.group(CONTEXT_AWARE_PROXY_URI_QUERY_VALUE_GROUP_IDX);
				return URI.createURI(contextURI);
			}
		}

		// No context information available on proxy URI; use workspace root as context URI to make sure that the whole
		// workspace with all resources is used as context
		IPath workspaceRootPath = ResourcesPlugin.getWorkspace().getRoot().getFullPath();
		return URI.createPlatformResourceURI(workspaceRootPath.toString(), true);
	}
}
