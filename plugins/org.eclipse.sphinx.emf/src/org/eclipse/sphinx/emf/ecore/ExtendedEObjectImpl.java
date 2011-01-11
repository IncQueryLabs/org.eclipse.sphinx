/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Added is proxy check to eResolveProxy
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.ecore;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelper;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelperAdapterFactory;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;

/**
 * This class redefines the EObjectImpl to override method <code>eResolveProxy</code>.
 */
public class ExtendedEObjectImpl extends EObjectImpl {

	/*
	 * @see org.eclipse.emf.ecore.impl.BasicEObjectImpl#eResolveProxy(org.eclipse.emf.ecore.InternalEObject)
	 */
	@Override
	public EObject eResolveProxy(InternalEObject proxy) {
		if (proxy == null) {
			return null;
		}
		if (isNoProxy(proxy)) {
			return proxy;
		}

		Resource resource = eResource();
		if (resource != null) {
			ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet != null) {
				return eResolveProxyInResourceSet(resourceSet, proxy);
			}
		}
		return super.eResolveProxy(proxy);
	}

	private boolean isNoProxy(InternalEObject proxy) {
		return proxy.eProxyURI() == null;
	}

	protected EObject eResolveProxyInResourceSet(ResourceSet resourceSet, InternalEObject proxy) {
		Assert.isNotNull(resourceSet);
		Assert.isNotNull(proxy);

		URI proxyURI = proxy.eProxyURI();

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

		// Are we in a ScopingResourceSet?
		if (resourceSet instanceof ScopingResourceSet) {
			// Delegate to resource scope-aware proxy resolution
			EObject resolvedEObject = ((ScopingResourceSet) resourceSet).getEObjectInScope(proxyURI, true, this);
			if (resolvedEObject != null) {
				return resolvedEObject;
			}
		} else {
			// Delegate to conventional EMF proxy resolution; it may be able to do the job if we are in a conventional
			// ResourceSet and the proxy URI is not fragment-based
			EObject resolvedEObject = super.eResolveProxy(proxy);
			if (resolvedEObject != proxy) {
				return resolvedEObject;
			}
		}

		if (proxyHelper != null) {
			// Remember proxy as known unresolved proxy
			proxyHelper.getBlackList().addProxyURI(proxyURI);
		}
		return proxy;
	}
}
