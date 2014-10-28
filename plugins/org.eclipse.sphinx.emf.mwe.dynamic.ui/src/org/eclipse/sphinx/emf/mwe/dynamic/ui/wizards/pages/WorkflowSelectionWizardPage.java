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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.dialogs.WorkflowSelectionDialog;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.wizards.WorkflowSelectionWizard;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

// TODO Rebase on org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage and org.eclipse.sphinx.platform.ui.fields.*
public class WorkflowSelectionWizardPage extends WizardPage {

	private static final int MAX_COMBO_HISTORY_LENGTH = 5;

	private Button browseWorkspaceButton;
	private Combo workflowPathCombo;
	private IPath oldWorkflowPath = null;
	private Composite workflowClassComposite;
	private Combo workflowClassCombo;

	private IStructuredSelection selection = null;

	public WorkflowSelectionWizardPage() {
		super("Whatever"); //$NON-NLS-1$
		setTitle(Messages.workflowSelectionWizardPage_title);
		setDescription(Messages.workflowSelectionWizardPage_description);
	}

	/*
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.BORDER | GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout());
		setControl(composite);
		createWorkflowSelectionGroup(composite);
		restoreWidgetValues();
		initWidgetValues();
	}

	protected void createWorkflowSelectionGroup(final Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		group.setText(Messages.workflowSelectionWizardPage_workflowGroupTitle);

		// Workflow path composite
		Composite workflowPathComposite = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		workflowPathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		workflowPathComposite.setLayout(layout);

		// Workflow path label
		Label workflowPathLabel = new Label(workflowPathComposite, SWT.NONE);
		workflowPathLabel.setLayoutData(new GridData(160, SWT.DEFAULT));
		workflowPathLabel.setText(Messages.workflowSelectionWizardPage_workflowPathLabel);

		// Workflow path combo
		workflowPathCombo = new Combo(workflowPathComposite, SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.grabExcessHorizontalSpace = true;
		workflowPathCombo.setLayoutData(layoutData);
		workflowPathCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateWorkflowClassCombo();
				updateWidgetEnablements();
			}
		});
		workflowPathCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateWorkflowClassCombo();
				updateWidgetEnablements();
			}
		});

		// Browse button
		browseWorkspaceButton = new Button(workflowPathComposite, SWT.PUSH);
		browseWorkspaceButton.setText(Messages.workflowSelectionWizardPage_browseWorkspaceButtonLabel);
		setButtonLayoutData(browseWorkspaceButton);
		browseWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowseWorkspace(parent);
				updateWorkflowClassCombo();
				updateWidgetEnablements();
			}
		});

		// Workflow class composite
		workflowClassComposite = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		workflowClassComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		workflowClassComposite.setLayout(layout);

		// Workflow class label
		Label workflowClassLabel = new Label(workflowClassComposite, SWT.NONE);
		workflowClassLabel.setLayoutData(new GridData(160, SWT.DEFAULT));
		workflowClassLabel.setText(Messages.workflowSelectionWizardPage_workflowClassLabel);

		// Workflow class combo
		workflowClassCombo = new Combo(workflowClassComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.grabExcessHorizontalSpace = true;
		workflowClassCombo.setLayoutData(layoutData);
		workflowClassCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateWidgetEnablements();
			}
		});
	}

	protected void handleBrowseWorkspace(Composite parent) {
		WorkflowSelectionDialog dialog = new WorkflowSelectionDialog(parent.getShell());
		dialog.setInitialSelections(Collections.singletonList(getSelection()).toArray());
		dialog.create();
		dialog.open();

		IPath workflowPath = dialog.getWorkflowPath();
		if (workflowPath != null && !workflowPath.equals(oldWorkflowPath)) {
			workflowPathCombo.add(workflowPath.toOSString());
			workflowPathCombo.setText(workflowPath.toOSString());
			oldWorkflowPath = workflowPath;
		}
	}

	protected void updateWorkflowClassCombo() {
		workflowClassCombo.removeAll();
		IFile workflowFile = getWorkflowFile();
		if (XtendUtil.isXtendFile(workflowFile)) {
			workflowClassCombo.setEnabled(true);
			List<String> classNames = XtendUtil.getAvailableClassNames(workflowFile);
			for (String className : classNames) {
				workflowClassCombo.add(className);
			}

		} else {
			workflowClassCombo.setEnabled(false);
			if (workflowFile != null) {
				workflowClassCombo.add(workflowFile.getFullPath().removeFileExtension().lastSegment());
			}
		}

		// If only one class is available, select it by default
		if (workflowClassCombo.getItems().length == 1) {
			workflowClassCombo.select(0);
		}
	}

	protected void updateWidgetEnablements() {
		setPageComplete(isPageComplete());
	}

	/*
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		setErrorMessage(null);

		IFile workflowFile = getWorkflowFile();
		if (workflowFile == null) {
			setErrorMessage(Messages.workflowSelectionWizardPage_workflowFileErrorMessage);
			return false;
		}

		if (XtendUtil.isXtendFile(workflowFile)) {
			if (workflowClassCombo.getText().isEmpty()) {
				setErrorMessage(Messages.workflowSelectionWizardPage_workflowClassErrorMessage);
				return false;
			}
		}

		return true;
	}

	protected void initWidgetValues() {
		if (workflowPathCombo.getItemCount() > 0) {
			workflowPathCombo.select(0);
		}
	}

	protected void restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String[] workflowPathHistory = settings.getArray(WorkflowSelectionWizard.DIALOG_SETTINGS_KEY_WORKFLOW_PATHS);
			if (workflowPathHistory != null && workflowPathHistory.length > 0) {
				for (String workflowPath : workflowPathHistory) {
					workflowPathCombo.add(workflowPath);
				}
			}
		}
	}

	public void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			List<String> workflowPathHistory;

			String[] workflowPathHistoryAsArray = settings.getArray(WorkflowSelectionWizard.DIALOG_SETTINGS_KEY_WORKFLOW_PATHS);
			if (workflowPathHistoryAsArray != null) {
				workflowPathHistory = new ArrayList<String>(Arrays.asList(workflowPathHistoryAsArray));
			} else {
				workflowPathHistory = new ArrayList<String>(MAX_COMBO_HISTORY_LENGTH);
			}

			workflowPathHistory.remove(workflowPathCombo.getText());
			workflowPathHistory.add(0, workflowPathCombo.getText());
			if (workflowPathHistory.size() > MAX_COMBO_HISTORY_LENGTH) {
				workflowPathHistory.remove(MAX_COMBO_HISTORY_LENGTH);
			}

			workflowPathHistoryAsArray = workflowPathHistory.toArray(new String[workflowPathHistory.size()]);
			settings.put(WorkflowSelectionWizard.DIALOG_SETTINGS_KEY_WORKFLOW_PATHS, workflowPathHistoryAsArray);
		}
	}

	public void setSlection(IStructuredSelection selection) {
		this.selection = selection;
	}

	public IStructuredSelection getSelection() {
		return selection;
	}

	public IFile getWorkflowFile() {
		String text = workflowPathCombo.getText();
		if (text != null && !text.isEmpty()) {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(text));
		}
		return null;
	}

	public String getWorkflowClass() {
		return workflowClassCombo.getText();
	}
}