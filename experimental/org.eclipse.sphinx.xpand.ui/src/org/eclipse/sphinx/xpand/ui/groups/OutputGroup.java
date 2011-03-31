/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xpand.ui.groups;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.SelectionButtonField;
import org.eclipse.sphinx.platform.ui.fields.StringButtonField;
import org.eclipse.sphinx.platform.ui.fields.adapters.IButtonAdapter;
import org.eclipse.sphinx.platform.ui.preferences.AbstractPreferenceAndPropertyPage;
import org.eclipse.sphinx.platform.ui.preferences.IPropertyPageIdProvider;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class OutputGroup {

	/**
	 * The name of the output group.
	 */
	protected String groupName;

	/**
	 * The use default output path button field.
	 */
	protected SelectionButtonField useDefaultPathButtonField;

	/**
	 * The output path to be use.
	 */
	protected StringButtonField outputPathField;

	/**
	 * The default outlet to be use.
	 */
	protected ExtendedOutlet defaultOutlet;

	/**
	 * The outlets preference to be use.
	 */
	protected OutletsPreference outletsPreference;

	/**
	 * The selected model object.
	 */
	protected EObject modelObject;

	public OutputGroup(Composite parent, String groupName, int numColumns, EObject modelObject, OutletsPreference outletsPreference) {
		this.groupName = groupName;
		this.modelObject = modelObject;
		this.outletsPreference = outletsPreference;
		createContent(parent, numColumns);
	}

	protected void createContent(final Composite parent, int numColumns) {
		final Group outputGroup = new Group(parent, SWT.SHADOW_NONE);
		outputGroup.setText(groupName);
		outputGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		GridLayout outputGroupLayout = new GridLayout();
		outputGroupLayout.numColumns = numColumns;
		outputGroup.setLayout(outputGroupLayout);

		// if outlets preference is not null then create a link toward this preference page.
		if (outletsPreference != null) {
			Link link = createLink(outputGroup, Messages.label_configureProjectSpecificSettings);
			link.setLayoutData(new GridData(GridData.END, GridData.END, true, true));
		} else {
			useDefaultPathButtonField = new SelectionButtonField(SWT.CHECK);
			useDefaultPathButtonField.setLabelText(Messages.label_useDefaultPath);
			useDefaultPathButtonField.fillIntoGrid(outputGroup, numColumns);
			useDefaultPathButtonField.setSelection(true);

			useDefaultPathButtonField.addFieldListener(new IFieldListener() {

				public void dialogFieldChanged(IField field) {
					updateEnableState(!useDefaultPathButtonField.isSelected());
				}

			});

			outputPathField = new StringButtonField(new IButtonAdapter() {

				public void changeControlPressed(IField field) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(parent.getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
							""); //$NON-NLS-1$
					if (dialog.open() == Window.OK) {
						Object[] result = dialog.getResult();
						if (result.length == 0) {
							return;
						}
						IPath path = (IPath) result[0];
						outputPathField.setText(path.makeRelative().toString());
					}
				}
			});
			outputPathField.setLabelText(Messages.label_path);
			outputPathField.setButtonLabel(Messages.label_browse);
			if (defaultOutlet != null) {
				IContainer defaultOutletContainer = defaultOutlet.getContainer();
				if (defaultOutletContainer != null) {
					outputPathField.setText(defaultOutletContainer.getFullPath().makeRelative().toString());
				}
			}
			outputPathField.fillIntoGrid(outputGroup, numColumns);
			outputPathField.addFieldListener(new IFieldListener() {

				public void dialogFieldChanged(IField field) {
					groupChanged(outputGroup);
				}
			});
			updateEnableState(!useDefaultPathButtonField.isSelected());
		}
	}

	/**
	 * This method should be overriding for instance by wizards that contain the output group field for example to
	 * adjust the enable state of the Back, Next, and Finish buttons wizard page.
	 */
	protected void groupChanged(Group group) {
		// Do nothing by default.
	}

	protected Link createLink(final Composite composite, String text) {
		Link link = new Link(composite, SWT.NONE);
		link.setFont(composite.getFont());
		link.setText("<A>" + text + "</A>"); //$NON-NLS-1$//$NON-NLS-2$
		link.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				doLinkActivated(composite.getShell(), (Link) e.widget);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				doLinkActivated(composite.getShell(), (Link) e.widget);
			}
		});
		return link;
	}

	protected void doLinkActivated(Shell shell, Link link) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(AbstractPreferenceAndPropertyPage.DATA_NO_LINK, Boolean.TRUE);
		IFile file = EcorePlatformUtil.getFile(modelObject);
		if (file != null) {
			openProjectProperties(shell, file.getProject(), data);
		}
	}

	protected void openProjectProperties(Shell shell, IProject project, Object data) {
		if (outletsPreference != null) {
			// TODO: Add OutletsPreferenceAdapterFactory in example project.
			// IPropertyPageIdProvider provider = (IPropertyPageIdProvider)
			// outletsPreference.getAdapter(IPropertyPageIdProvider);
			IPropertyPageIdProvider provider = null;
			if (provider != null) {
				String outletsPropertyPageId = provider.getPropertyPageId();
				if (outletsPropertyPageId != null) {
					PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(shell, project, outletsPropertyPageId,
							new String[] { outletsPropertyPageId }, data);
					dialog.open();
				}
			}
		}
	}

	protected void updateEnableState(boolean enabled) {
		outputPathField.setEnabled(enabled);
	}

	public SelectionButtonField getUseDefaultPathButtonField() {
		return useDefaultPathButtonField;
	}

	public StringButtonField getOutputPathField() {
		return outputPathField;
	}
}
