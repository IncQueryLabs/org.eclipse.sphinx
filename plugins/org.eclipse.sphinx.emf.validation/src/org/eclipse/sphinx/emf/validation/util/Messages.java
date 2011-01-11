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
package org.eclipse.sphinx.emf.validation.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

	public static String __EValidatorRegstering_NoSuchPackage;
	public static String warningNoSuchMarker;
	public static String warningProblemWithMarkerOperationOnResource;
	public static String warningProblemWithWorkspaceOperation;
	public static String noMessageAvailableForThisMarker;

	public static String CSV_Report_Null_FileName;
	public static String CSV_Report_Null_Resource;
	public static String CSV_Report_Null_diagnostic;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
