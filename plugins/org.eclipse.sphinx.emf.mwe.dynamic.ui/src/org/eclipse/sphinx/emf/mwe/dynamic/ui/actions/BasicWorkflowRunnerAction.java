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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.Activator;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;

public class BasicWorkflowRunnerAction extends AbstractWorkflowRunnerAction {

	public BasicWorkflowRunnerAction() {
		this("Default title");
	}

	protected BasicWorkflowRunnerAction(String text) {
		super(text);
	}

	@Override
	protected IWorkflowRunnerOperation createWorkflowRunnerOperation() {
		IWorkflowRunnerOperation operation = new BasicWorkflowRunnerOperation(getOperationName());
		operation.setInput(getWorkflowInput());
		operation.setWorkflow(getWorkflowFile());
		return operation;
	}

	@Override
	public void run() {
		ExtendedPlatformUI.showSystemConsole();
		// create the workflow operation
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation();
		// Run the operation in a progress monitor dialog
		try {
			Shell shell = ExtendedPlatformUI.getActiveShell();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.run(true, true, new RunnableWithProgressAdapter(operation));
		} catch (InterruptedException ex) {
			// Operation has been canceled by user, do nothing
		} catch (InvocationTargetException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
	}
}