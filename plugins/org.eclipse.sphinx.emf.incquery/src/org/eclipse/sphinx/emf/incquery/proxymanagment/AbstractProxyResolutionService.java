/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.incquery.proxymanagment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolverService;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolver;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;

public abstract class AbstractProxyResolutionService implements IProxyResolverService {

	private List<IProxyResolver> proxyResolvers = new ArrayList<IProxyResolver>();
	private IncQueryEngineHelper incQueryEngineHelper;

	public AbstractProxyResolutionService() {
		initProxyResolvers();
	}

	protected abstract void initProxyResolvers();

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

	protected IncQueryEngineHelper getIncQueryEngineHelper() {
		if (incQueryEngineHelper == null) {
			incQueryEngineHelper = new IncQueryEngineHelper();
		}
		return incQueryEngineHelper;
	}

	@Override
	public EObject getEObject(EObject proxy, EObject contextObject, boolean loadOnDemand) {
		IProxyResolver proxyResolver = getProxyResolver(proxy.eClass());
		if (proxyResolver != null) {
			return proxyResolver.getEObject(proxy, contextObject, false);
		}
		return null;
	}
}
