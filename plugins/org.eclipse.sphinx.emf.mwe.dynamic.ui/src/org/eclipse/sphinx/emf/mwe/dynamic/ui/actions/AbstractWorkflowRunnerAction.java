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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards.WorkflowInputWizard;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public abstract class AbstractWorkflowRunnerAction extends BaseSelectionListenerAction {

	protected AbstractWorkflowRunnerAction(String text) {
		super(text);
	}

	protected Object getSelectedObject() {
		return getStructuredSelection().getFirstElement();
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

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.size() == 1 && getSelectedObject() != null;
	}

	protected String getOperationName() {
		return Messages.operation_runningWorkflow;
	}

	protected abstract IWorkspaceOperation createWorkflowRunnerOperation();

}
