/**
 * <copyright>
 *
 * Copyright (c) 2013 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BMW Car IT - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.ecore.proxymanagement.resolver;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * A ResolveRequest specifies a request to find an EObject within a certain set of EObjects.
 */
public class ResolveRequest {

	private EObject fProxyToResolve;
	private EObject fScopeContext;
	private boolean fIncludeUnloadedEObjects;

	/**
	 * Creates a new ResolveRequest for finding an EObject within a specified set of EObjects.
	 * 
	 * @param proxyToResolve
	 *            A proxy EObject containing the URI of the EObject which is to be resolved.
	 * @param scopeContext
	 *            An EObject which is to be used to calculate the set of EObjects from which to resolve the specified
	 *            EObject.
	 */
	public ResolveRequest(EObject proxyToResolve, EObject scopeContext) {
		setProxyToResolve(proxyToResolve);
		setResolutionScopeContext(scopeContext);
	}

	private void setProxyToResolve(EObject proxyToResolve) {
		checkIsProxy(proxyToResolve);
		fProxyToResolve = proxyToResolve;
	}

	private void checkIsProxy(EObject proxyToResolve) {
		if (!proxyToResolve.eIsProxy()) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns the proxy EObject which specifies the EObject to be resolved.
	 * 
	 * @return The proxy EObject to resolve.
	 */
	public EObject getProxyToResolve() {
		return fProxyToResolve;
	}

	/**
	 * Returns the URI of the EObject which is to be resolved.
	 * 
	 * @return The URI of the EObject to find.
	 */
	public URI getUriToResolve() {
		return ((InternalEObject) fProxyToResolve).eProxyURI();
	}

	private void setResolutionScopeContext(EObject resolutionScopeContext) {
		fScopeContext = resolutionScopeContext;
	}

	/**
	 * Returns the EObject from which the model scope to search in is calculated.
	 * 
	 * @return The EObject from which the resolution scope is to be calculated.
	 */
	public EObject getScopeContext() {
		return fScopeContext;
	}

	/**
	 * Sets if unloaded model EObjects are to be included in the search. This means that unloaded files containing
	 * EObjects which are part of the specified resolution scope will be loaded during the EObject resolution.
	 * 
	 * @param includeUnloadedEObjects
	 *            Determines if unloaded EObjects in the specified model scope shall be included in the search and
	 *            therefore shall be loaded.
	 */
	public void setIncludeUnloadedEObjects(boolean includeUnloadedEObjects) {
		fIncludeUnloadedEObjects = includeUnloadedEObjects;
	}

	/**
	 * Determines if unloaded model EObjects are to be included in the search.
	 * 
	 * @return <code>true</code> if unloaded EObjects shall be taken into account when resolving the specified EObject,
	 *         <code>false</code> otherwise.
	 */
	public boolean includeUnloadedEObjects() {
		return fIncludeUnloadedEObjects;
	}

}