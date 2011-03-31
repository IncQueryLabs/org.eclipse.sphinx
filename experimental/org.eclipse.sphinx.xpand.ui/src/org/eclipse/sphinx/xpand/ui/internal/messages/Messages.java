/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xpand.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.xpand.ui.internal.messages.messages"; //$NON-NLS-1$

	public static String job_generatingCode;

	public static String label_template;
	public static String label_output;
	public static String label_templatePath;
	public static String label_templateSelection;
	public static String label_defineBlock;
	public static String label_useDefaultPath;
	public static String label_path;
	public static String label_browse;
	public static String label_configPageName;
	public static String label_definitionName;
	public static String label_useCheckModelButton;
	public static String label_checkModelBlock;
	public static String label_checkFiles;
	public static String label_configureProjectSpecificSettings;

	public static String msg_chooseTemplate;
	public static String msg_chooseTemplateError;
	public static String desc_config;
	public static String title_launchGen;
	public static String title_codeGen;

	static {
		// Initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
