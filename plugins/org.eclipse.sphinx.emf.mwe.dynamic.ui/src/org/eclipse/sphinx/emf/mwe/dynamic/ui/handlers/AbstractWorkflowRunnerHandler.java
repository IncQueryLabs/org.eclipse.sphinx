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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.mwe.dynamic.IXtendConstants;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.Activator;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards.WorkflowInputWizard;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractWorkflowRunnerHandler extends AbstractHandler {

	protected ISelection selection;

	/*
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = HandlerUtil.getActiveMenuSelection(event);
		ExtendedPlatformUI.showSystemConsole();
		// create the workflow operation
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation();
		// Run the operation in a progress monitor dialog
		try {
			Shell shell = HandlerUtil.getActiveShell(event);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.run(true, true, new RunnableWithProgressAdapter(operation));
		} catch (InterruptedException ex) {
			// Operation has been canceled by user, do nothing
		} catch (InvocationTargetException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
		return null;
	}

	protected IFile getWorkflowFile(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object selected = structuredSelection.getFirstElement();
				if (selected instanceof IFile) {
					IFile selectedFile = (IFile) selected;
					String fileExtension = selectedFile.getFileExtension();
					if (fileExtension != null && fileExtension.equals(IXtendConstants.XTEND_FILE_EXTENSION)) {
						return selectedFile;
					}
				}
			}
		}
		return null;
	}

	protected Object getSelectedObject() {
		return ((IStructuredSelection) getSelection()).getFirstElement();
	}

	protected Object getWorkflowInput() {
		Object selectedObject = getSelectedObject();
		// model object
		if (selectedObject instanceof EObject) {
			return selectedObject;
		}
		// model resource
		Resource resource = EcorePlatformUtil.getResource(selectedObject);
		if (resource != null && !resource.getContents().isEmpty()) {
			return resource.getContents().get(0);
		}
		return null;
	}

	protected Object getWorkflowFile() {
		Object selectedObject = getSelectedObject();
		// Java class
		if (selectedObject instanceof ICompilationUnit) {
			return selectedObject;
		}
		// file.xtend
		if (selectedObject instanceof IFile) {
			if (XtendUtil.isXtendFile((IFile) selectedObject)) {
				return selectedObject;
			}
			return queryWorkflowFile();
		}
		// unspecified, query it
		if (selectedObject == null || selectedObject instanceof EObject) {
			return queryWorkflowFile();
		}
		throw new UnsupportedOperationException(Messages.unknownFileType);
	}

	private IFile queryWorkflowFile() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		WorkflowInputWizard wizard = new WorkflowInputWizard();
		WizardDialog wizardDialog = new WizardDialog(window.getShell(), wizard);
		wizardDialog.open();
		return wizard.getWorkflowFile();
	}

	protected abstract IWorkflowRunnerOperation createWorkflowRunnerOperation();

	protected Object getSelection() {
		return selection;
	}

	protected String getOperationName() {
		return Messages.operation_runningWorkflow;
	}
}
