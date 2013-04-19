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
package org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.messages.Messages"; //$NON-NLS-1$

	public static String wizard_newHummingbirdProject_title;
	public static String page_newHummingbirdProjectCreation_title;
	public static String page_newHummingbirdProjectCreation_description;

	public static String wizard_newHummingbirdFile_title;
	public static String page_newInitialHummingbirdModelCreation_title;
	public static String page_newInitialHummingbirdModelCreation_description;
	public static String page_newHummingbirdFileCreation_title;
	public static String page_newHummingbirdFileCreation_description;

	public static String job_createNewHummingbirdProject_name;
	public static String job_createNewHummingbirdFile_name;

	static {
		/* Load message values from bundle file. */
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
