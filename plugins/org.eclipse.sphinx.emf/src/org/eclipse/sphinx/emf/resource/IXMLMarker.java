/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.emf.resource;

import org.eclipse.sphinx.emf.Activator;

public interface IXMLMarker {

	/**
	 * XML well-formedness problem marker type.
	 * 
	 * @see IMarker#getType()
	 */
	public static final String XML_WELLFORMEDNESS_PROBLEM = Activator.getPlugin().getSymbolicName() + ".xmlwellformednessproblemmarker"; //$NON-NLS-1$

	/**
	 * XML Integrity problem marker type.
	 * 
	 * @see IMarker#getType()
	 */
	public static final String XML_INTEGRITY_PROBLEM = Activator.getPlugin().getSymbolicName() + ".xmlintegrityproblemmarker"; //$NON-NLS-1$

	/**
	 * XML Validity problem marker type.
	 * 
	 * @see IMarker#getType()
	 */
	public static final String XML_VALIDITY_PROBLEM = Activator.getPlugin().getSymbolicName() + ".xmlvalidityproblemmarker"; //$NON-NLS-1$
}
