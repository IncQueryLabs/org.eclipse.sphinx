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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolver;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.resource.ExtendedResourceSet;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public abstract class AbstractIncQueryProxyResolver implements IProxyResolver {

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
	protected abstract EObject[] getEObjectCandidates(EObject proxy, Object contextObject, ViatraQueryEngine engine);

	/**
	 * @param uri
	 * @param contextObject
	 * @param engine
	 * @return
	 */
	// TODO Move engine parameter to first place, remove contextObject parameter
	protected abstract EObject[] getEObjectCandidates(URI uri, Object contextObject, ViatraQueryEngine engine);

	protected EObject getMatchingEObject(URI uri, Object contextObject, EObject[] candidates) {
		if (uri != null && candidates != null) {
			for (EObject candidate : candidates) {
				if (matchesEObjectCandidate(uri, contextObject, candidate)) {
					return candidate;
				}
			}
		}
		return null;
	}

	protected boolean matchesEObjectCandidate(URI uri, Object contextObject, EObject candidate) {
		return matchesEObjectCandidate(uri, candidate);
	}

	protected boolean matchesEObjectCandidate(URI uri, EObject candidate) {
		URI candidateURI = EcoreResourceUtil.getURI(candidate);
		return uri.equals(candidateURI);
	}

	@Override
	public boolean canResolve(EObject eObject) {
		if (eObject != null) {
			return canResolve(eObject.eClass());
		}
		return false;
	}

	protected abstract boolean isTypeSupported(EClass eType);

	@Override
	public boolean canResolve(EClass eType) {
		if (eType != null) {
			return !eType.isAbstract() && !eType.isInterface() && isTypeSupported(eType);
		}
		return false;
	}

	protected URI trimContextInfo(URI proxyURI, EObject contextObject) {
		if (contextObject != null) {
			Resource contextResource = contextObject.eResource();
			if (contextResource != null) {
				ResourceSet contextResourceSet = contextResource.getResourceSet();
				if (contextResourceSet instanceof ExtendedResourceSet) {
					return ((ExtendedResourceSet) contextResourceSet).trimProxyContextInfo(proxyURI);
				}
			}
		}
		return proxyURI;
	}

	@Override
	public EObject getEObject(EObject proxy, EObject contextObject, boolean loadOnDemand) {
		try {
			if (proxy != null) {
				URI uri = trimContextInfo(((InternalEObject) proxy).eProxyURI(), contextObject);
				ViatraQueryEngine engine = getIncQueryEngineHelper().getEngine(contextObject);
				EObject[] candidates = getEObjectCandidates(proxy, contextObject, engine);
				return getMatchingEObject(uri, contextObject, candidates);
			}
		} catch (ViatraQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}

	@Override
	public EObject getEObject(URI uri, ExtendedResourceSet contextResourceSet, Object contextObject, boolean loadOnDemand) {
		try {
			if (contextResourceSet != null) {
				uri = contextResourceSet.trimProxyContextInfo(uri);
				ViatraQueryEngine engine = getIncQueryEngineHelper().getEngine(contextResourceSet);
				EObject[] candidates = getEObjectCandidates(uri, contextObject, engine);
				return getMatchingEObject(uri, contextObject, candidates);
			}
		} catch (ViatraQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}
}
