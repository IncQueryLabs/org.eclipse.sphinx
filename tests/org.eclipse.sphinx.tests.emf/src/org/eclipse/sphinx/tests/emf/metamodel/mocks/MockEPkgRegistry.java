/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
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
package org.eclipse.sphinx.tests.emf.metamodel.mocks;

import java.net.URI;
import java.util.HashMap;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;

@SuppressWarnings("serial")
public class MockEPkgRegistry extends HashMap<String, Object> implements Registry {

	public EFactory getEFactory(String nsURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public EPackage getEPackage(String nsURI) {
		return (EPackage) get(nsURI);
	}

	public void registerEPackage(URI namespace, EPackage ePkg) {
		put(namespace.toString(), ePkg);
	}

}
