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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.ecore.proxymanagement.IProxyResolver;
import org.eclipse.sphinx.emf.incquery.AbstractIncQueryProvider;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractProxyResolver extends AbstractIncQueryProvider implements IProxyResolver {

	public AbstractProxyResolver() {
		super();
	}

	/**
	 * @param proxy
	 * @param contextObject
	 * @param engine
	 * @return
	 */
	protected abstract EObject[] getEObjectCandidates(EObject proxy, EObject contextObject, IncQueryEngine engine);

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

	protected boolean matches(EObject proxy, EObject candidate) {
		URI proxyURI = ((InternalEObject) proxy).eProxyURI();
		URI candidateURI;
		ExtendedResource extendedTargetResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(candidate.eResource());
		if (extendedTargetResource != null) {
			candidateURI = extendedTargetResource.getURI(candidate);
		} else {
			candidateURI = EcoreUtil.getURI(candidate);
		}
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
			IncQueryEngine engine = getIncQueryEngineHelper().getEngine(contextObject.eResource());
			EObject[] eObjectCandidates = getEObjectCandidates(proxy, contextObject, engine);
			return getMatchingEObject(proxy, eObjectCandidates);
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}
}
