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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * A basic implementation for {@link EObjectResolver}s which simply delegate the resolution request to the ResouceSet of
 * the scope to search in.
 */
public abstract class ResourceSetEObjectResolver extends AbstractEObjectResolver {

	protected static final ResourceSet EMPTY_RESOURCESET = new ResourceSetImpl();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canHandle(EObjectResolveRequest request) {
		return canDelegateTo(getScopeResourceSet(request));
	}

	/**
	 * Determines if this ResourceSetEObjectResolver can delegate a request to the <param>scopeResourceSet</param>.
	 * 
	 * @param scopeResourceSet
	 *            The ResourceSet containing the set of EObjects in which to search.
	 * @return <code>true</code> if this ResourceSetEObjectResolver can delegate a request to the
	 *         <param>scopeResourceSet</param>, <code>false</code> otherwise.
	 */
	protected abstract boolean canDelegateTo(ResourceSet scopeResourceSet);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EObject processRequest(EObjectResolveRequest request) {
		return delegateRequest(request, getScopeResourceSet(request));
	}

	/**
	 * Delegates the <param>request</param> to the <param>scopeResourceSet</param>;
	 * 
	 * @param scopeResourceSet
	 *            The ResourceSet containing the set of EObjects in which to search.
	 * @return The resolved EObject or if the requested EObject can not be resolved a corresponding proxy EObject.
	 */
	protected abstract EObject delegateRequest(EObjectResolveRequest request, ResourceSet scopeResourceSet);

	private ResourceSet getScopeResourceSet(EObjectResolveRequest request) {
		EObject scopeContext = request.getScopeContext();
		Resource scopeResource = scopeContext.eResource();
		if (scopeResource == null) {
			return EMPTY_RESOURCESET;
		}
		return scopeResource.getResourceSet();
	}

}
