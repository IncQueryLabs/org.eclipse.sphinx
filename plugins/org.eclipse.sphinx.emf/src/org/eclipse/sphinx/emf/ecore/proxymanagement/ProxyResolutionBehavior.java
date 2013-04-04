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
package org.eclipse.sphinx.emf.ecore.proxymanagement;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelper;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelperAdapterFactory;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.resolver.DefaultResourceSetEObjectResolver;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.resolver.IEObjectResolver;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.resolver.EObjectResolveRequest;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.resolver.ScopingResourceSetEObjectResolver;

public class ProxyResolutionBehavior {

	/**
	 * Singleton instance
	 */
	public static final ProxyResolutionBehavior INSTANCE = new ProxyResolutionBehavior(
			new ScopingResourceSetEObjectResolver().append(new DefaultResourceSetEObjectResolver()));

	private IEObjectResolver fEObjectResolver;

	/**
	 * Protected constructor for the singleton pattern
	 */
	protected ProxyResolutionBehavior(IEObjectResolver eObjectResolver) {
		fEObjectResolver = eObjectResolver;
	}

	public EObject eResolveProxy(EObject contextObject, EObject proxy) {
		if (proxy == null) {
			return null;
		}
		if (isNoProxy(proxy)) {
			return proxy;
		}

		Resource resource = contextObject.eResource();
		if (resource != null) {
			ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet != null) {
				return eResolveProxyInResourceSet(resourceSet, contextObject, proxy);
			}
		}

		return EcoreUtil.resolve(proxy, contextObject);
	}

	protected boolean isNoProxy(EObject proxy) {
		return ((InternalEObject) proxy).eProxyURI() == null;
	}

	protected EObject eResolveProxyInResourceSet(ResourceSet resourceSet, EObject contextObject, EObject proxy) {
		Assert.isNotNull(resourceSet);
		Assert.isNotNull(proxy);

		URI proxyURI = ((InternalEObject) proxy).eProxyURI();

		// Retrieve proxy helper in order to resolve proxy in a performance-optimized way
		ProxyHelper proxyHelper = ProxyHelperAdapterFactory.INSTANCE.adapt(resourceSet);
		if (proxyHelper != null) {
			// If proxy URI references a known unresolved proxy then don't try to resolve it again
			if (proxyHelper.getBlackList().existsProxyURI(proxyURI)) {
				return proxy;
			}

			// Fragment-based proxy?
			if (proxyURI.segmentCount() == 0) {
				// If lookup-based proxy resolution is possible then go ahead and try to do so
				if (proxyURI.segmentCount() == 0 && proxyHelper.getLookupResolver().isAvailable()) {
					EObject resolvedEObject = proxyHelper.getLookupResolver().get(proxyURI);
					if (resolvedEObject != null) {
						return resolvedEObject;
					}
				}

				// If resolution of fragment-based proxies is currently disabled then don't just leave it as is
				if (proxyHelper.isIgnoreFragmentBasedProxies()) {
					return proxy;
				}
			}
		}
		EObjectResolveRequest resolveRequest = new EObjectResolveRequest(proxy, contextObject);
		resolveRequest.setIncludeUnloadedEObjects(true);
		EObject resolvedEObject = fEObjectResolver.resolve(resolveRequest);

		if (resolvedEObject == proxy && proxyHelper != null) {
			// Remember proxy as known unresolved proxy
			proxyHelper.getBlackList().addProxyURI(proxyURI);
		}
		return resolvedEObject;
	}

}
