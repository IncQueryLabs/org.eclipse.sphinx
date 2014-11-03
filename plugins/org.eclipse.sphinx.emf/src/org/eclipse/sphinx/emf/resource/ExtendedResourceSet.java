/**
 * <copyright>
 *
 * Copyright (c) 2013-2014 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [442342] Sphinx doen't trim context information from proxy URIs when serializing proxyfied cross-document references
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolver;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.scoping.IResourceScope;

public interface ExtendedResourceSet extends ResourceSet, IProxyResolver {

	/**
	 * Augments given {@link InternalEObject proxy} to a context-aware proxy by adding key/value pairs that contain the
	 * target {@link IMetaModelDescriptor metamodel descriptor} and a context {@link URI} to the {@link URI#query()
	 * query string} of the proxy URI. Those are required to support the resolution of proxified references between
	 * objects from different metamodels and to honor the {@link IResourceScope resource scope} of the proxy URI when it
	 * is being resolved.
	 *
	 * @param proxy
	 *            The proxy to be handled.
	 * @param contextResource
	 *            The resource that identifies the context of the proxy (typically the resource containing it).
	 * @see #trimProxyContextInfo(URI)
	 */
	void augmentToContextAwareProxy(EObject proxy, Resource contextResource);

	/**
	 * If given {@link URI proxy URI} contains proxy context-related key/value pairs on its {@link URI#query() query
	 * string}, returns the URI formed by removing those key/value pairs or removing the query string entirely in case
	 * that no other key/value pairs exist; returns given proxy URI unchanged, otherwise.
	 *
	 * @param proxyURI
	 *            The context-aware proxy URI to be handled.
	 * @return The trimmed proxy URI.
	 * @see #augmentToContextAwareProxy(EObject)
	 */
	URI trimProxyContextInfo(URI proxyURI);
}
