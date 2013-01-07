/**
 * <copyright>
 * 
 * Copyright (c) 2013 itemis and others.
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
package org.eclipse.sphinx.emf.ecore;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.sphinx.emf.ecore.proxymanagement.ProxyResolutionBehavior;

public class ExtendedMinimalEObjectImpl extends MinimalEObjectImpl2 {

	protected ProxyResolutionBehavior proxyResolution;

	/**
	 * Creates a minimal EObject.
	 */
	protected ExtendedMinimalEObjectImpl() {
		super();

		proxyResolution = new ProxyResolutionBehavior(this);
	}

	/*
	 * @see org.eclipse.emf.ecore.impl.BasicEObjectImpl#eResolveProxy(org.eclipse.emf.ecore.InternalEObject)
	 */
	@Override
	public EObject eResolveProxy(InternalEObject proxy) {
		return proxyResolution.eResolveProxy(proxy);
	}
}
