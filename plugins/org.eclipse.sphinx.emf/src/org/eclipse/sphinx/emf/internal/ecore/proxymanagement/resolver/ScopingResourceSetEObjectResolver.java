/**
 * <copyright>
 *
 * Copyright (c) 2013 BMW Car IT, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BMW Car IT - Initial API and implementation
 *      itemis - [409458] Enhance ScopingResourceSetImpl#getEObjectInScope() to enable cross-document references between model files with different metamodels
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.ecore.proxymanagement.resolver;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;

public class ScopingResourceSetEObjectResolver extends AbstractResourceSetEObjectResolver {

	@Override
	protected EObject delegateRequest(EObjectResolveRequest request, ResourceSet scopeResourceSet) {
		// Get metamodel descriptor of proxy and pass it along with other resolve request parameters to scoping resource
		// set
		EObject proxy = request.getProxyToResolve();
		IMetaModelDescriptor targetMetaModelDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(proxy);
		return ((ScopingResourceSet) scopeResourceSet).getEObjectInScope(request.getUriToResolve(), targetMetaModelDescriptor,
				request.getScopeContext(), request.includeUnloadedEObjects());
	}

	@Override
	protected boolean canDelegateTo(ResourceSet scopeResourceSet) {
		return scopeResourceSet instanceof ScopingResourceSet;
	}

}