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
package org.eclipse.sphinx.testutils.integration.messages;

import org.eclipse.osgi.util.NLS;

public class AbstractIntegrationTestCaseMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sphinx.testutils.integration.messages.PerformanceStatsTestsMessages"; //$NON-NLS-1$

	/* --------------------------------------------------------------------- */
	/*
	 * Messages category for arguments in methods.
	 */

	public static String assert_ReferenceWorkspaceInitialized;
	public static String assert_ReferenceDescriptorInitialized;
	public static String assert_ModelCreatedForProject;
	public static String assert_RunningTimeUnderLowerBound;
	public static String assert_EventExceedTimeOut;
	public static String assert_EventInContextExceedTimeOut;
	static {
		// Load message values from bundle file
		NLS.initializeMessages(AbstractIntegrationTestCaseMessages.BUNDLE_NAME, AbstractIntegrationTestCaseMessages.class);
	}
}
