/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [473260] Progress indication of check framework
 *     itemis - [473261] Check Validation: Cancel button unresponsive
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.util.WorkflowRunnerActionHandlerHelper;
import org.eclipse.sphinx.platform.jobs.WorkspaceOperationWorkspaceJob;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class BasicWorkflowRunnerHandler extends AbstractHandler {

	private IStructuredSelection selection;
	private boolean runInBackground;

	protected WorkflowRunnerActionHandlerHelper helper;

	public BasicWorkflowRunnerHandler() {
		setRunInBackground(false);
		helper = new WorkflowRunnerActionHandlerHelper();
	}

	public boolean isRunInBackground() {
		return runInBackground;
	}

	public void setRunInBackground(boolean runInBackground) {
		this.runInBackground = runInBackground;
	}

	protected IStructuredSelection getStructuredSelection() {
		return selection;
	}

	protected String getOperationName() {
		return Messages.operation_runWorkflow_label;
	}

	protected IWorkflowRunnerOperation createWorkflowRunnerOperation() {
		Object workflow = helper.getWorkflow(getStructuredSelection());
		if (workflow == null) {
			workflow = helper.promptForWorkflow(getStructuredSelection());
		}

		IWorkflowRunnerOperation operation = new BasicWorkflowRunnerOperation(getOperationName(), workflow);
		List<URI> modelURIs = helper.getModelURIs(getStructuredSelection());
		operation.getModelURIs().addAll(modelURIs);

		return operation;
	}

	protected WorkspaceJob createWorkspaceOperationJob(IWorkflowRunnerOperation operation) {
		return new WorkspaceOperationWorkspaceJob(operation);
	}

	/*
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = SelectionUtil.getStructuredSelection(HandlerUtil.getActiveMenuSelection(event));

		ExtendedPlatformUI.showSystemConsole();

		// Create the workflow operation
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation();

		if (isRunInBackground()) {
			// Run the workflow operation in a workspace job
			WorkspaceJob job = createWorkspaceOperationJob(operation);
			job.schedule();
		} else {
			// Run the workflow operation in a progress monitor dialog
			try {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(ExtendedPlatformUI.getActiveShell());
				dialog.run(true, true, new RunnableWithProgressAdapter(operation));
			} catch (InterruptedException ex) {
				// Operation has been canceled by user, do nothing
			} catch (InvocationTargetException ex) {
				throw new ExecutionException(ex.getMessage(), ex);
			}
		}

		return null;
	}
}