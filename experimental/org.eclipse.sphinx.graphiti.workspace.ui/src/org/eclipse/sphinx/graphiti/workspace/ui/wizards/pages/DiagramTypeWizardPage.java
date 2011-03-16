/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.graphiti.dt.IDiagramType;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.graphiti.workspace.ui.ElementTreeSelectionGroup;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.Activator;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.graphiti.workspace.ui.providers.ElementTreeContentProvider;
import org.eclipse.sphinx.graphiti.workspace.ui.providers.ElementTreeLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class DiagramTypeWizardPage extends AbstractWizardPage {

	private static final String PAGE_DESC = Messages.DiagramTypeWizardPage_PageDescription;
	private static final String PAGE_TITLE = Messages.DiagramTypeWizardPage_PageTitle;

	private static final String SELECTED_TYPE = "selectedtype"; //$NON-NLS-1$

	Combo comboBox;
	ElementTreeSelectionGroup modelObjectSelectionGroup;

	public DiagramTypeWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public DiagramTypeWizardPage(String pageName) {
		super(pageName);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESC);
	}

	@Override
	protected void createWizardContents(Composite parent) {
		// project specification group
		Composite projectGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label
		Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setFont(parent.getFont());
		projectLabel.setText(Messages.DiagramTypeWizardPage_DiagramTypeField);

		// new project name entry field
		comboBox = new Combo(projectGroup, SWT.READ_ONLY | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		comboBox.setLayoutData(data);
		comboBox.setFont(parent.getFont());
		comboBox.setVisibleItemCount(12);
		comboBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IDialogSettings dialogSettings = Activator.getDefault().getDialogSettings();
				dialogSettings.put(SELECTED_TYPE, comboBox.getText());
			}
		});

		// set the contents of the Combo-widget
		comboBox.setItems(getAllAvailableDiagramTypes());
		if (getInitialValue() != null) {
			comboBox.setText(getInitialValue());
		}

		modelObjectSelectionGroup = new ElementTreeSelectionGroup(projectGroup, new ElementTreeContentProvider(), new ElementTreeLabelProvider(),
				new Listener() {

					public void handleEvent(Event event) {

					}
				});
		GridData modelObjectLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		modelObjectLayoutData.horizontalSpan = 2;
		modelObjectSelectionGroup.setLayoutData(modelObjectLayoutData);
	}

	protected String[] getAllAvailableDiagramTypes() {
		Vector<String> diagramIds = new Vector<String>();
		for (IDiagramType diagramType : GraphitiUi.getExtensionManager().getDiagramTypes()) {
			diagramIds.add(diagramType.getId());
		}

		return diagramIds.toArray(new String[] {});
	}

	protected String getInitialValue() {
		// Get last choice
		IDialogSettings dialogSettings = Activator.getDefault().getDialogSettings();
		String selType = dialogSettings.get(SELECTED_TYPE);
		List<String> asList = Arrays.asList(comboBox.getItems());
		if (asList.contains(selType)) {
			return selType;
		}
		return null;
	}

	public String getSelectedType() {
		return comboBox.getText();
	}

	public Object getSelectedModelObject() {
		ISelection selection = modelObjectSelectionGroup.getSelection();
		if (selection instanceof IStructuredSelection) {
			return ((IStructuredSelection) selection).getFirstElement();
		}
		return null;
	}
}
