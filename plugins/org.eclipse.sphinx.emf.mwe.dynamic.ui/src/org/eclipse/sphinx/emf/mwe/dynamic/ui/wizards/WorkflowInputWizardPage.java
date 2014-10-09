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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

public class WorkflowInputWizardPage extends WizardPage {

	private static final int COMBO_HISTORY_LENGTH = 5;
	private static String previouslySelectedFile = "";

	private Button browseWorkspace;
	private Combo workflowPathCombo;

	protected WorkflowInputWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite workArea = new Composite(parent, SWT.NONE);
		setControl(workArea);
		workArea.setLayout(new GridLayout());
		workArea.setLayoutData(new GridData(SWT.BORDER | GridData.FILL_HORIZONTAL));
		createInputSlectionControl(workArea);
		restoreWidgetValues();
	}

	private void createInputSlectionControl(final Composite workArea) {
		Group group = new Group(workArea, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(Messages.workflowInputWizardPage_SelectionGroupTitle);

		// Workflow path composite
		Composite workflowPathComposite = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		workflowPathComposite.setLayout(layout);
		workflowPathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// workflow path label
		Label workflowPathLabel = new Label(workflowPathComposite, SWT.NONE);
		workflowPathLabel.setLayoutData(new GridData(160, SWT.DEFAULT));
		workflowPathLabel.setText("&File path:");

		// Path combo
		workflowPathCombo = new Combo(workflowPathComposite, SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.grabExcessHorizontalSpace = true;
		workflowPathCombo.setLayoutData(layoutData);
		workflowPathCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateWidgetEnablements();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		// browse button
		browseWorkspace = new Button(workflowPathComposite, SWT.PUSH);
		browseWorkspace.setText("&Browse...");
		setButtonLayoutData(browseWorkspace);
		browseWorkspace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowseWorkspace(workArea);
			}
		});
	}

	protected void handleBrowseWorkspace(Composite parent) {
		IWorkspaceRoot rootElement = ResourcesPlugin.getWorkspace().getRoot();
		ResourceSelectionDialog dialog = new ResourceSelectionDialog(parent.getShell(), rootElement, "Select a workflow file...");
		dialog.setTitle(Messages.workflowInputWizardPage_fileSelectionDialogTitle);
		dialog.open();
		Object[] result = dialog.getResult();
		if (result != null) {
			for (Object x : result) {
				previouslySelectedFile = x.toString();
				workflowPathCombo.add(x.toString());
				workflowPathCombo.setText(previouslySelectedFile);
			}
		}
		updateWidgetEnablements();
	}

	@Override
	public boolean isPageComplete() {
		return determinePageCompletion();
	}

	protected void updateWidgetEnablements() {
		boolean pageComplete = determinePageCompletion();
		setPageComplete(pageComplete);
		if (pageComplete) {
			setMessage(null);
		}
	}

	protected boolean determinePageCompletion() {
		boolean complete = validateWorkflowInput();
		if (complete) {
			setErrorMessage(null);
		}
		return complete;
	}

	private boolean validateWorkflowInput() {
		boolean isValid = true;
		IFile selectedFile = getSelectedFile();
		if (selectedFile == null) {
			setErrorMessage("Please select a workflow input");
			isValid = false;
		} else {
			setErrorMessage(null);
		}
		return isValid;
	}

	private void restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			restoreFromHistory(settings, WorkflowInputWizard.STORE_FILES, workflowPathCombo);
		}
	}

	private void restoreFromHistory(IDialogSettings settings, String key, Combo combo) {
		String[] sourceNames = settings.getArray(key);
		if (sourceNames == null) {
			return;
		}
		for (String sourceName : sourceNames) {
			combo.add(sourceName);
		}
	}

	public void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			saveInHistory(settings, WorkflowInputWizard.STORE_FILES, workflowPathCombo.getText());
		}
	}

	private void saveInHistory(IDialogSettings settings, String key, String value) {
		String[] sourceNames = settings.getArray(key);
		if (sourceNames == null) {
			sourceNames = new String[0];
		}
		sourceNames = addToHistory(sourceNames, value);
		settings.put(key, sourceNames);
	}

	private String[] addToHistory(String[] history, String newEntry) {
		List<String> historyList = new java.util.ArrayList<String>(Arrays.asList(history));
		addToHistory(historyList, newEntry);
		String[] r = new String[historyList.size()];
		historyList.toArray(r);
		return r;
	}

	private void addToHistory(List<String> history, String newEntry) {
		history.remove(newEntry);
		history.add(0, newEntry);
		if (history.size() > COMBO_HISTORY_LENGTH) {
			history.remove(COMBO_HISTORY_LENGTH);
		}
	}

	public IFile getSelectedFile() {
		if (workflowPathCombo.getText() != null && workflowPathCombo.getText().length() > 0) {
			IPath path = Path.fromOSString(workflowPathCombo.getText());
			IPath removeFirstSegments = path.removeFirstSegments(1);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(removeFirstSegments);
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}
}