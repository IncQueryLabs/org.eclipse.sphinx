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
package org.eclipse.sphinx.examples.actions.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	/*
	 * "Project Statistics" messages
	 */

	public static String act_ProjectStatistics_label;
	public static String act_ProjectStatistics_result_ProjectName;
	public static String act_ProjectStatistics_result_Summary;
	public static String act_ProjectStatistics_result_columLabel_Quantity;
	public static String act_ProjectStatistics_result_columLabel_Type;
	public static String act_ProjectStatistics_result_eof;
	public static String dlg_ProjectStatistics_fileAlreadyExists_title;
	public static String dlg_ProjectStatistics_fileAlreadyExists_desc;

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.examples.actions.internal.messages.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
