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
 *     itemis - [475954] Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.incquery.proxymanagment;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolver;
import org.eclipse.sphinx.emf.incquery.AbstractIncQueryProvider;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.resource.ExtendedResourceSet;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractIncQueryProxyResolver extends AbstractIncQueryProvider implements IProxyResolver {

	private IIncQueryEngineHelper incQueryEngineHelper;

	protected IIncQueryEngineHelper getIncQueryEngineHelper() {
		if (incQueryEngineHelper == null) {
			incQueryEngineHelper = createIncQueryEngineHelper();
		}
		return incQueryEngineHelper;
	}

	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		return new IncQueryEngineHelper();
	}

	/**
	 * @param proxy
	 * @param contextObject
	 * @param engine
	 * @return
	 */
	// TODO Move engine parameter to first place, remove contextObject parameter
	protected abstract EObject[] getEObjectCandidates(EObject proxy, Object contextObject, IncQueryEngine engine);

	/**
	 * @param proxyURI
	 * @param contextObject
	 * @param engine
	 * @return
	 */
	// TODO Move engine parameter to first place, remove contextObject parameter
	protected abstract EObject[] getEObjectCandidates(URI proxyURI, Object contextObject, IncQueryEngine engine);

	// Remove this method and refactor AbstractHummingbird20ProxyResolver and subclasses to use EClass instead
	protected Class<?> getInstanceClass(EObject proxy) {
		if (proxy != null && proxy.eClass() != null) {
			return proxy.eClass().getInstanceClass();
		}
		return null;
	}

	protected EObject getMatchingEObject(URI uri, Object contextObject, EObject[] candidates) {
		if (uri != null && candidates != null) {
			for (EObject candidate : candidates) {
				if (matches(uri, contextObject, candidate)) {
					return candidate;
				}
			}
		}
		return null;
	}

	protected boolean matches(URI proxyURI, Object contextObject, EObject candidate) {
		// FIXME Check if it wouldn't be more appropriate to use
		// org.eclipse.sphinx.emf.resource.ExtendedResourceSetImpl.trimProxyContextInfo(URI)
		proxyURI = proxyURI.trimQuery();
		URI candidateURI = EcoreResourceUtil.getURI(candidate);
		return proxyURI.equals(candidateURI);
	}

	@Override
	public boolean canResolve(EObject eObject) {
		if (eObject != null) {
			return canResolve(eObject.eClass());
		}
		return false;
	}

	@Override
	public boolean canResolve(EClass eType) {
		if (eType != null) {
			return getSupportedTypes().contains(eType.getInstanceClass()) && !eType.isAbstract() && !eType.isInterface();
		}
		return false;
	}

	@Override
	public EObject getEObject(EObject proxy, EObject contextObject, boolean loadOnDemand) {
		try {
			IncQueryEngine engine = getIncQueryEngineHelper().getEngine(contextObject);
			EObject[] candidates = getEObjectCandidates(proxy, contextObject, engine);
			return getMatchingEObject(((InternalEObject) proxy).eProxyURI(), contextObject, candidates);
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}

	@Override
	public EObject getEObject(URI uri, ExtendedResourceSet contextResourceSet, Object contextObject, boolean loadOnDemand) {
		try {
			if (contextResourceSet != null) {
				IncQueryEngine engine = getIncQueryEngineHelper().getEngine(contextResourceSet);
				EObject[] candidates = getEObjectCandidates(uri, contextObject, engine);
				return getMatchingEObject(uri, contextObject, candidates);
			}
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}
}
