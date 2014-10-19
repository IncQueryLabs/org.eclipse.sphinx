/**
 * <copyright>
 *
 * Copyright (c) See4sys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic.headless;

import org.eclipse.sphinx.emf.mwe.dynamic.headless.internal.Activator;
import org.eclipse.sphinx.emf.mwe.dynamic.headless.internal.messages.Messages;

public interface IWorkflowRunnerCLIConstants {

	/*
	 * Application name.
	 */
	String APPLICATION_NAME = Activator.getPlugin().getSymbolicName() + ".WorkflowRunner"; //$NON-NLS-1$

	/*
	 * Workflow option.
	 */
	String OPTION_WORKFLOW = "workflow"; //$NON-NLS-1$
	String OPTION_WORKFLOW_ARG_NAME = Messages.cliOption_workflow_argName;
	String OPTION_WORKFLOW_DESCRIPTION = Messages.cliOption_workflow_description;

	/*
	 * Model option.
	 */
	String OPTION_MODEL = "model"; //$NON-NLS-1$
	String OPTION_MODEL_ARG_NAME = Messages.cliOption_model_argName;
	String OPTION_MODEL_DESCRIPTION = Messages.cliOption_model_description;
}