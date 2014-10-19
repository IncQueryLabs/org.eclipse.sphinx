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

public interface IWorkflowRunnerCLIConstants {

	// TODO Externalize descriptions

	/*
	 * Application name.
	 */
	String APPLICATION_NAME = Activator.getPlugin().getSymbolicName() + ".WorkflowRunner"; //$NON-NLS-1$

	/*
	 * Command line syntax.
	 */
	String COMMAND_LINE_SYNTAX = "eclipse -noSplash -data <workspace location> -application " + APPLICATION_NAME + " [options]"; //$NON-NLS-1$ //$NON-NLS-2$

	/*
	 * Workflow option.
	 */
	String OPTION_WORKFLOW = "workflow"; //$NON-NLS-1$
	String OPTION_WORKFLOW_ARG_NAME = "path or name";
	String OPTION_WORKFLOW_DESCRIPTION = "workspace-relative <path> of the workflow file or fully qualified <name> of the workflow class to be run";

	/*
	 * Model option.
	 */
	String OPTION_MODEL = "model"; //$NON-NLS-1$
	String OPTION_MODEL_ARG_NAME = "path or uri";
	String OPTION_MODEL_DESCRIPTION = "workspace-relative <path> or absolute <uri> of the model resource or model element to be processed by the workflow";
}
