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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.platform.cli.AbstractCLIApplication;

public class BasicWorkflowRunnerApplication extends AbstractCLIApplication {

	private static final String WORKFLOW_FILE_OPTION = "file"; //$NON-NLS-1$
	private static final String WORKFLOW_INPUT = "input"; //$NON-NLS-1$

	@Override
	protected String getApplicationName() {
		return "BasicWorkflowRunner"; //$NON-NLS-1$
	}

	@Override
	protected void defineOptions() {
		super.defineOptions();

		OptionBuilder.isRequired();
		OptionBuilder.withArgName("workflowFile");//$NON-NLS-1$
		OptionBuilder.hasArgs(1);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("Path of the workflow file"); //$NON-NLS-1$
		addOption(OptionBuilder.create(WORKFLOW_FILE_OPTION));

		OptionBuilder.isRequired(false);
		OptionBuilder.withArgName("workflowInput");//$NON-NLS-1$
		OptionBuilder.hasArgs(1);
		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription("Path of the workflow model input"); //$NON-NLS-1$
		addOption(OptionBuilder.create(WORKFLOW_INPUT));
	}

	@Override
	protected Object interrogate() throws Throwable {
		super.interrogate();

		// Retrieve options
		CommandLine commandLine = getCommandLine();
		String workflowFilePath = commandLine.getOptionValue(WORKFLOW_FILE_OPTION);
		String workflowInputPath = commandLine.getOptionValue(WORKFLOW_INPUT);

		// create the workflow operation
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation();
		operation.setWorkflow(getWorkflowFile(workflowFilePath));
		operation.setInput(getWorkflowInput(workflowInputPath));

		// Run the operation
		operation.run(new NullProgressMonitor());

		return ERROR_NO;
	}

	protected BasicWorkflowRunnerOperation createWorkflowRunnerOperation() {
		return new BasicWorkflowRunnerOperation(getApplicationName());
	}

	private Object getWorkflowFile(String filePath) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = new Path(filePath);
		IFile fileForLocation = workspaceRoot.getFileForLocation(path);
		if (fileForLocation.exists()) {
			return fileForLocation;
		}
		throw new UnsupportedOperationException("Could not find workflow file at the specified location " + filePath); //$NON-NLS-1$
	}

	private Object getWorkflowInput(String modelPath) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = new Path(modelPath);
		IFile fileForLocation = workspaceRoot.getFileForLocation(path);
		if (fileForLocation.exists()) {
			return fileForLocation;
		}
		throw new UnsupportedOperationException("Could not find workflow input at the specified location " + modelPath); //$NON-NLS-1$
	}
}