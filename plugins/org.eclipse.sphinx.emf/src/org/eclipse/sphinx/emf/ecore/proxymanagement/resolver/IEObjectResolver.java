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
package org.eclipse.sphinx.emf.ecore.proxymanagement.resolver;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An IEObjectResolver handles requests for finding EObjects within a model scope. A model scope is simply a set of
 * EObjects. In most cases the model scope will be a {@link ResourceSet}. IEObjectResolvers can be chained together. An
 * IEObjectResolver which does not know how to resolve an EObject from a model scope will delegate the request to the
 * next IEObjectResolver in line.
 */
public interface IEObjectResolver {

	/**
	 * Resolves an EObject from within a certain scope.
	 * 
	 * @param request
	 *            The <code>ResolveRequest</code> specifying the EObject to resolve and the scope in which to to search
	 *            for it.
	 * @return The EObject which is to be resolved or a corresponding proxy EObject if the EObject could not be
	 *         resolved.
	 */
	EObject resolve(ResolveRequest request);

	/**
	 * Appends another IEObjectResolver to the delegation chain. The provided IEObjectResolver will be appended to the
	 * end of the chain to which this IEObjectResolver will delegate all calls it can not handle itself.
	 * 
	 * @param resolver
	 *            The IEObjectResolver to append to the chain of
	 * @return This EObjectResolver
	 */
	IEObjectResolver append(IEObjectResolver resolver);

}