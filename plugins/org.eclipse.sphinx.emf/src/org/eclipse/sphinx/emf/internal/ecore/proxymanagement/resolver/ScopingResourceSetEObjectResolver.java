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
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;

public class ScopingResourceSetEObjectResolver extends ResourceSetEObjectResolver {

	@Override
	protected EObject delegateRequest(ResolveRequest request, ResourceSet scopeResourceSet) {
		return ((ScopingResourceSet) scopeResourceSet).getEObjectInScope(request.getUriToResolve(), request.includeUnloadedEObjects(),
				request.getScopeContext());
	}

	@Override
	protected boolean canDelegateTo(ResourceSet scopeResourceSet) {
		return scopeResourceSet instanceof ScopingResourceSet;
	}

}