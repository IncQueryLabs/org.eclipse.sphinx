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
package org.eclipse.sphinx.emf.incquery.proxymanagment;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolver;
import org.eclipse.sphinx.emf.incquery.AbstractIncQueryProvider;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

// TODO Rename to AbstractIncQueryProxyResolver
public abstract class AbstractProxyResolver extends AbstractIncQueryProvider implements IProxyResolver {

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

	protected Class<?> getInstanceClass(EObject proxy) {
		if (proxy != null && proxy.eClass() != null) {
			return proxy.eClass().getInstanceClass();
		}
		return null;
	}

	protected EObject getMatchingEObject(EObject proxy, EObject[] eObjectCandidates) {
		if (proxy != null && eObjectCandidates != null) {
			for (EObject eObj : eObjectCandidates) {
				if (matches(proxy, eObj)) {
					return eObj;
				}
			}
		}
		return null;
	}

	protected EObject getMatchingEObject(URI uri, EObject[] eObjectCandidates) {
		if (uri != null && eObjectCandidates != null) {
			for (EObject eObj : eObjectCandidates) {
				if (matches(uri, eObj)) {
					return eObj;
				}
			}
		}
		return null;
	}

	protected boolean matches(EObject proxy, EObject candidate) {
		URI proxyURI = ((InternalEObject) proxy).eProxyURI();
		return matches(proxyURI, candidate);
	}

	protected boolean matches(URI proxyURI, EObject candidate) {
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
			EObject[] eObjectCandidates = getEObjectCandidates(proxy, contextObject, engine);
			return getMatchingEObject(proxy, eObjectCandidates);
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}

	@Override
	public EObject getEObject(URI uri, boolean loadOnDemand) {
		try {
			Resource contextResource = getContextResource(uri);
			if (contextResource != null) {
				IncQueryEngine engine = getIncQueryEngineHelper().getEngine(contextResource);
				EObject[] eObjectCandidates = getEObjectCandidates(uri, contextResource, engine);
				return getMatchingEObject(uri, eObjectCandidates);
			}
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}

	protected Resource getContextResource(URI proxyURI) {
		return EcorePlatformUtil.getResource(proxyURI.trimFragment());
	}
}
