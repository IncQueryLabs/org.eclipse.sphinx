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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.util;

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards.WorkflowSelectionWizard;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;

public class WorkflowRunnerActionHandlerHelper {

	public boolean isWorkflow(Object selectedObject) {
		// Java class
		if (selectedObject instanceof IType) {
			return true;
		}

		// Java file
		if (selectedObject instanceof ICompilationUnit) {
			return true;
		}

		// Xtend file
		if (selectedObject instanceof IFile && XtendUtil.isXtendFile((IFile) selectedObject)) {
			return true;
		}

		return false;
	}

	public Object getWorkflow(Object selectedObject) {
		// Try to get workflow file from selection
		if (isWorkflow(selectedObject)) {
			return selectedObject;
		}

		// Try to retrieve file behind selection and query workflow in enclosing project
		IFile selectedFile = null;
		if (selectedObject instanceof IFile) {
			selectedFile = (IFile) selectedObject;
		} else {
			selectedFile = EcorePlatformUtil.getFile(selectedObject);
		}
		return queryWorkflowFile(selectedFile != null ? selectedFile.getProject() : null);
	}

	public IFile queryWorkflowFile(IProject contextProject) {
		WorkflowSelectionWizard wizard = new WorkflowSelectionWizard(contextProject);
		WizardDialog wizardDialog = new WizardDialog(ExtendedPlatformUI.getActiveShell(), wizard);
		wizardDialog.create();
		wizardDialog.open();

		IFile workflowFile = wizard.getWorkflowFile();
		String workflowClassName = wizard.getWorkflowClassName();
		return XtendUtil.getJavaFile(workflowFile, workflowClassName);
	}

	public Object getModel(Object selectedObject) {
		// Model object
		if (selectedObject instanceof EObject) {
			return Collections.singletonList(selectedObject);
		}

		// Model resource
		Resource resource = EcorePlatformUtil.getResource(selectedObject);
		if (resource != null) {
			return resource.getContents();
		}

		return null;
	}
}
