/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.mwe.dynamic.headless.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sphinx.emf.mwe.dynamic.headless.internal.messages.messages"; //$NON-NLS-1$

	public static String BasicWorkflowRunnerApplication_ApplicationName;

	public static String cliOption_workflow_argName;
	public static String cliOption_workflow_description;

	public static String cliOption_model_argName;
	public static String cliOption_model_description;

	public static String cliOption_skipSave_description;

	public static String cliError_workflowClassNotFound;
	public static String cliError_workflowFileDoesNotExist;
	public static String cliError_modelResourceDoesNotExist;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
