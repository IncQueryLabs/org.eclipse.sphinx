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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.proxymanagment.AbstractIncQueryProxyResolver;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.incquery.WorkspaceIncQueryEngineHelper;

public abstract class AbstractScopingIncQueryProxyResolver extends AbstractIncQueryProxyResolver {

	@Override
	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		// TODO Check if WorkspaceIncQueryEngineHelper/DelegatingScopingResourceSetImpl make any sense given that
		// NavigatorHelper seems to operate on entire resource set in spite of that
		// TODO Add scoping of matches returned by EMF-IncQuery also to model query and search capabilities
		return new WorkspaceIncQueryEngineHelper();
	}

	@Override
	protected boolean matches(URI proxyURI, Object contextObject, EObject candidate) {
		// FIXME Check if it wouldn't be more appropriate to use
		// org.eclipse.sphinx.emf.resource.ExtendedResourceSetImpl.trimProxyContextInfo(URI)
		proxyURI = proxyURI.trimQuery();
		URI candidateURI = EcoreResourceUtil.getURI(candidate);
		if (contextObject != null) {
			if (isResourceInScope(candidate.eResource(), contextObject)) {
				return proxyURI.equals(candidateURI);
			}
		} else {
			return proxyURI.equals(candidateURI);
		}
		return false;
	}

	protected boolean isResourceInScope(Resource candidateResource, Object contextObject) {
		if (contextObject != null) {
			ResourceSet candiateResourceSet = candidateResource.getResourceSet();
			if (candiateResourceSet instanceof ScopingResourceSet) {
				return ((ScopingResourceSet) candiateResourceSet).isResourceInScope(candidateResource, contextObject);
			}
		}
		return true;
	}
}
