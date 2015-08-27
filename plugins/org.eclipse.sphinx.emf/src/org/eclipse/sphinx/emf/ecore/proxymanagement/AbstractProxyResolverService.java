/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - 475954: Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.ecore.proxymanagement;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public abstract class AbstractProxyResolverService implements IProxyResolverService {

	private List<IProxyResolver> proxyResolvers = new ArrayList<IProxyResolver>();

	public AbstractProxyResolverService() {
		initProxyResolvers();
	}

	protected abstract void initProxyResolvers();

	protected abstract EClass getTargetEClass(URI uri);

	protected List<IProxyResolver> getProxyResolvers() {
		if (proxyResolvers == null) {
			proxyResolvers = new ArrayList<IProxyResolver>();
		}
		return proxyResolvers;
	}

	protected IProxyResolver getProxyResolver(EClass eType) {
		for (IProxyResolver resolver : getProxyResolvers()) {
			if (resolver.canResolve(eType)) {
				return resolver;
			}
		}
		return null;
	}

	@Override
	public EObject getEObject(EObject proxy, EObject contextObject, boolean loadOnDemand) {
		IProxyResolver proxyResolver = getProxyResolver(proxy.eClass());
		if (proxyResolver != null) {
			return proxyResolver.getEObject(proxy, contextObject, loadOnDemand);
		}
		return null;
	}

	@Override
	public EObject getEObject(URI uri, boolean loadOnDemand) {
		EClass targetEClass = getTargetEClass(uri);
		if (targetEClass != null) {
			IProxyResolver proxyResolver = getProxyResolver(targetEClass);
			if (proxyResolver != null) {
				return proxyResolver.getEObject(uri, loadOnDemand);
			}
		}
		return null;
	}
}
