/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird10.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sphinx.emf.resource.ExtendedSAXXMIHandler;
import org.eclipse.sphinx.emf.resource.ExtendedXMILoadImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <!-- begin-user-doc --> The <b>Resource </b> associated with the package. <!-- end-user-doc -->
 * 
 * @see org.eclipse.sphinx.examples.hummingbird10.util.Hummingbird10ResourceFactoryImpl
 * @generated
 */
public class Hummingbird10ResourceImpl extends XMIResourceImpl {
	/**
	 * Creates an instance of the resource. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param uri
	 *            the URI of the new resource.
	 * @generated
	 */
	public Hummingbird10ResourceImpl(URI uri) {
		super(uri);
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLLoad()
	 */
	@Override
	protected XMLLoad createXMLLoad() {
		// Use extended XMILoad implementation for XMI to include support for on-the-fly resource migration,
		// resource-centric problem handling, enhanced entity resolution and ignorable whitespace suppression
		return new ExtendedXMILoadImpl(createXMLHelper()) {
			@Override
			protected DefaultHandler makeDefaultHandler() {
				return new ExtendedSAXXMIHandler(resource, helper, options);
			}
		};
	}
} // Hummingbird10ResourceImpl
