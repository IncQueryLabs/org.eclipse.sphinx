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
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An IEObjectResolver which will simply delegate the resolution request to the ResourceSet specified in the request.
 */
public class DefaultResourceSetEObjectResolver extends ResourceSetEObjectResolver {

	@Override
	protected EObject delegateRequest(EObjectResolveRequest request, ResourceSet scopeResourceSet) {
		return scopeResourceSet.getEObject(request.getUriToResolve(), request.includeUnloadedEObjects());
	}

	@Override
	protected boolean canDelegateTo(ResourceSet resourceSet) {
		return true;
	}

}