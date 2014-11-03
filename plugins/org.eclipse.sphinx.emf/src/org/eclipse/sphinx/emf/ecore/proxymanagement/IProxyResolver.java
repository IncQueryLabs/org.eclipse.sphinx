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
package org.eclipse.sphinx.emf.ecore.proxymanagement;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public interface IProxyResolver {

	boolean canResolve(EClass type);

	boolean canResolve(EObject proxy);

	/**
	 * Retrieves the {@linkplain EObject object} for specified {@link EObject proxy}. Allows to use provided
	 * <code>contextObject</code> - i.e., the object referencing the proxy - to customize or optimize the way how the
	 * resolution of the proxy is done.
	 *
	 * @param proxy
	 *            The {@EObject proxy} to be resolved.
	 * @param contextObject
	 *            The {@link EObject context object} that can be used customize or optimize the way how the resolution
	 *            of the proxy is done.
	 * @param loadOnDemand
	 *            Whether to load the resource or model containing the object that is referenced by given
	 *            <code>proxy</code> if it is not already loaded.
	 * @return The object that is represented by given <code>proxy</code> or <code>null</code> if given
	 *         <code>proxy</code> cannot be resolved.
	 */
	EObject getEObject(EObject proxy, EObject contextObject, boolean loadOnDemand);
}
