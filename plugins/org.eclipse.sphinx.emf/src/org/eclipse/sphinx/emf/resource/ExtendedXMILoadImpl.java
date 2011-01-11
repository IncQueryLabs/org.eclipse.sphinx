/**
 * <copyright>
 *
 * Copyright (c) 2002-2006 IBM Corporation, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   See4sys - copied from org.eclipse.emf.ecore.xmi.impl.XMILoadImpl 
 *             for letting it extend {@link ExtendedXMLLoadImpl} instead 
 *             of {@link XMLLoadImpl}
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is an exact copy of {@link XMILoadImpl} except that it extends {@link ExtendedXMLLoadImpl} instead of
 * {@link XMLLoadImpl}.
 */
public class ExtendedXMILoadImpl extends ExtendedXMLLoadImpl {

	/**
	 * Constructor for XMILoad.
	 */
	public ExtendedXMILoadImpl(XMLHelper helper) {
		super(helper);
	}

	@Override
	protected DefaultHandler makeDefaultHandler() {
		return new SAXXMIHandler(resource, helper, options);
	}
}
