/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 itemis, See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Added is proxy check to eResolveProxy
 *     itemis - Externalized and Reworked proxy resolution behavior
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.ecore;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.sphinx.emf.ecore.proxymanagement.ProxyResolutionBehavior;

/**
 * This class redefines the EObjectImpl to override method <code>eResolveProxy</code>.
 */
public class ExtendedEObjectImpl extends EObjectImpl {

	protected ProxyResolutionBehavior proxyResolution;

	public ExtendedEObjectImpl() {
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
