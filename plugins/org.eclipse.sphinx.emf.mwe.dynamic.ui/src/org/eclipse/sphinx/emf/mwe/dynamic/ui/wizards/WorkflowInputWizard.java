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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.internal.WorkbenchPlugin;

public class WorkflowInputWizard extends Wizard {

	public final static String STORE_FILES = "WorkflowInputWizardFiles";

	private WorkflowInputWizardPage page = null;

	private IFile workflowFile = null;

	@SuppressWarnings("restriction")
	public WorkflowInputWizard() {
		IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection(STORE_FILES);
		if (section == null) {
			section = workbenchSettings.addNewSection(STORE_FILES);
		}
		setDialogSettings(section);
		setWindowTitle("Run Workflow");
	}

	@Override
	public void addPages() {
		page = new WorkflowInputWizardPage("Whatever");
		page.setTitle("Workflow File Selection");
		page.setDescription("Select a workflow file");
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		page.saveWidgetValues();
		IFile selectedFile = page.getSelectedFile();
		setWorkflowFile(selectedFile);
		return true;
	}

	public IFile getWorkflowFile() {
		return workflowFile;
	}

	public void setWorkflowFile(IFile file) {
		workflowFile = file;
	}
}