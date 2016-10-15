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
package org.eclipse.sphinx.emf.workspace.incquery.proxymanagement;

import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.proxymanagment.AbstractIncQueryProxyResolver;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;
import org.eclipse.sphinx.emf.workspace.incquery.WorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.incquery.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public abstract class AbstractScopingIncQueryProxyResolver extends AbstractIncQueryProxyResolver {

	@Override
	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		// TODO Check if WorkspaceIncQueryEngineHelper/DelegatingScopingResourceSetImpl make any sense given that
		// NavigatorHelper seems to operate on entire resource set in spite of that
		// TODO Add scoping of matches returned by EMF-IncQuery also to model query and search capabilities
		return new WorkspaceIncQueryEngineHelper();
	}

	@Override
	protected final EObject[] getEObjectCandidates(URI uri, Object contextObject, ViatraQueryEngine engine) {
		return new EObject[] {};
	}

	@Override
	protected boolean matchesEObjectCandidate(URI uri, Object contextObject, EObject candidate) {
		if (contextObject != null) {
			if (isResourceInScope(candidate.eResource(), contextObject)) {
				return matchesEObjectCandidate(uri, candidate);
			}
			return false;
		} else {
			return matchesEObjectCandidate(uri, candidate);
		}
	}

	protected boolean isResourceInScope(Resource resource, Object contextObject) {
		ResourceSet resourceSet = resource.getResourceSet();
		if (resourceSet instanceof ScopingResourceSet) {
			return ((ScopingResourceSet) resourceSet).isResourceInScope(resource, contextObject);
		} else {
			return true;
		}
	}

	/**
	 * @param proxy
	 * @param contextObject
	 * @param engine
	 * @return
	 */
	@Override
	protected EObject[] getEObjectCandidates(EObject proxy, Object contextObject, ViatraQueryEngine engine) {
		String name = getName(proxy);
		if (!isBlank(name)) {
			try {
				NavigationHelper baseIndex = EMFScope.extractUnderlyingEMFIndex(engine);
				EStructuralFeature nameFeature = getNameFeature(proxy.eClass());
				baseIndex.registerEStructuralFeatures(Collections.singleton(nameFeature));
				Set<EObject> candidates = baseIndex.findByFeatureValue(name, nameFeature);
				return candidates.toArray(new EObject[candidates.size()]);
			} catch (ViatraQueryException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return new EObject[] {};
	}

	protected abstract String getName(EObject proxy);

	protected abstract EStructuralFeature getNameFeature(EClass eclass);

	protected boolean isBlank(String text) {
		return text == null || text.isEmpty();
	}
}
