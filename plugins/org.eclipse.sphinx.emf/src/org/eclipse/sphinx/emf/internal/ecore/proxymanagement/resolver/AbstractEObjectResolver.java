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

import org.eclipse.emf.ecore.EObject;

/**
 * A default implementation of the IEObjectResolver providing basic behavior, like for instance the chaining and request
 * delegation.
 */
public abstract class AbstractEObjectResolver implements IEObjectResolver {

	private IEObjectResolver fNextResolver;

	/**
	 * {@inheritDoc}
	 */
	public EObject resolve(ResolveRequest request) {
		if (canHandle(request)) {
			return processRequestSafely(request);
		}
		if (fNextResolver != null) {
			return fNextResolver.resolve(request);
		}
		return request.getProxyToResolve();
	}

	/**
	 * {@inheritDoc}
	 */
	public IEObjectResolver append(IEObjectResolver resolver) {
		if (fNextResolver != null) {
			fNextResolver.append(resolver);
		} else {
			fNextResolver = resolver;
		}
		return this;
	}

	private EObject processRequestSafely(ResolveRequest request) {
		EObject resolvedEObject = processRequest(request);
		if (resolvedEObject != null) {
			return resolvedEObject;
		}
		return request.getProxyToResolve();
	}

	/**
	 * Executes the ResolveRequest and returns the EObject requested.
	 * 
	 * @param request
	 *            Specifies the EObject to find and the set of EObjects in which to search for it.
	 * @return The resolved EObject or if the requested EObject can not be resolved <code>null</code>.
	 */
	protected abstract EObject processRequest(ResolveRequest request);

	/**
	 * Determines if this <code>EObjectResolver</code> can handle the <param>request</param> or not.
	 * 
	 * @param request
	 *            The request to be handled by the <code>EObjectResolver</code>.
	 * @return <code>true</code> if this <code>EObjectResolver</code> can handle the request <code>false</code>
	 *         otherwise.
	 */
	protected abstract boolean canHandle(ResolveRequest request);

}