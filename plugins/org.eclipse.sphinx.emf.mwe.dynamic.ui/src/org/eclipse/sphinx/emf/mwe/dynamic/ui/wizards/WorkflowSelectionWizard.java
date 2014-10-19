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
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards.pages.WorkflowSelectionWizardPage;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.ui.internal.WorkbenchPlugin;

@SuppressWarnings("restriction")
public class WorkflowSelectionWizard extends Wizard {

	public final static String DIALOG_SETTINGS_SECTION_WORKFLOW_SELECTION = "workflowSelection"; //$NON-NLS-1$
	public final static String DIALOG_SETTINGS_KEY_WORKFLOW_PATHS = "workflowPaths"; //$NON-NLS-1$

	private WorkflowSelectionWizardPage page = null;

	private IFile workflowFile = null;

	private String workflowClassName = null;

	private IStructuredSelection selection = null;

	public WorkflowSelectionWizard() {
		this(null);
	}

	public WorkflowSelectionWizard(IProject contextProject) {
		setWindowTitle(Messages.workflowSelectionWizard_Title);

		IJavaProject contextJavaProject = XtendUtil.getJavaProject(contextProject);
		setSelection(contextJavaProject != null ? new StructuredSelection(contextJavaProject) : StructuredSelection.EMPTY);

		IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection(DIALOG_SETTINGS_SECTION_WORKFLOW_SELECTION);
		if (section == null) {
			section = workbenchSettings.addNewSection(DIALOG_SETTINGS_SECTION_WORKFLOW_SELECTION);
		}
		setDialogSettings(section);
	}

	public IStructuredSelection getSelection() {
		return selection;
	}

	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}

	public IFile getWorkflowFile() {
		return workflowFile;
	}

	public void setWorkflowFile(IFile file) {
		workflowFile = file;
	}

	public String getWorkflowClassName() {
		return workflowClassName;
	}

	public void setWorkflowClassName(String workflowClassName) {
		this.workflowClassName = workflowClassName;
	}

	/*
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		page = createWorkflowSelectionWizardPage();
		page.setSlection(getSelection());
		addPage(page);
	}

	protected WorkflowSelectionWizardPage createWorkflowSelectionWizardPage() {
		return new WorkflowSelectionWizardPage();
	}

	/*
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		page.saveWidgetValues();
		IFile selectedFile = page.getWorkflowFile();
		String className = page.getWorkflowClass();
		setWorkflowFile(selectedFile);
		setWorkflowClassName(className);
		return true;
	}
}