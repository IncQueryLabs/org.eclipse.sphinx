/**
 * <copyright>
 *
 * Copyright (c) 2013 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [405059] Enable BasicMetaModelVersionGroup to open appropriate model version preference page
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards.groups;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.sphinx.platform.ui.fields.ComboField;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.SelectionButtonField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * A basic metamodel version group which is composed of buttons for the workspace default metamodel version and
 * alternate metamodel version, configureWorkspaceSettingsLink, and a Combo for different metamodel versions.
 * <p>
 * This class is useful for the creation of a new model project wizard page. It can be overridden by clients.
 */
public class BasicMetaModelVersionGroup implements IFieldListener, SelectionListener {

	protected static final Pattern META_MODEL_NAME_PATTERN = Pattern.compile("(\\w+)( \\d(.\\d(.\\d)?)?)?"); //$NON-NLS-1$

	protected static final String LAST_SELECTED_METAMODEL_VERSION_OPTION = Activator.getPlugin().getSymbolicName()
			+ "last.selected.metamodel.version"; //$NON-NLS-1$
	protected static final String LAST_SELECTED_METAMODEL_VERSION_KEY = Activator.getPlugin().getSymbolicName()
			+ "last.selected.project.metamodel.version"; //$NON-NLS-1$

	protected static final int WORKSPACE_DEFAULT_METAMODEL_VERSION = 0;
	protected static final int ALTERNATE_METAMODEL_VERSION = 1;

	private IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference;
	private String metaModelVersionPreferencePageId;

	protected Group group;
	protected SelectionButtonField workspaceDefaultMetaModelVersionButton, alternateMetaModelVersionButton;
	protected ComboField metaModelVersionCombo;
	protected Link configureWorkspaceSettingsLink;
	protected List<IMetaModelDescriptor> supportedMetaModelVersions = new ArrayList<IMetaModelDescriptor>();

	private String metaModelVersionName = null;
	private String metaModelVersionLabel = null;

	/**
	 * Creates a new instance of the metamodel version group. The buttons for the workspace default metamodel version
	 * and alternate metamodel version, configureWorkspaceSettingsLink, and a combo for different metamodel versions are
	 * added in this group.
	 * 
	 * @param parent
	 *            the parent {@linkplain Composite composite}
	 * @param baseMetaModelDescriptor
	 *            the base {@linkplain IMetaModelDescriptor metamodel} of the model project to be created
	 * @param metaModelVersionPreference
	 *            the metamodel version {@linkplain IProjectWorkspacePreference preference} object
	 * @param metaModelVersionPreferencePageId
	 *            the metamodel version preference page id
	 */
	public BasicMetaModelVersionGroup(Composite parent, IMetaModelDescriptor baseMetaModelDescriptor,
			IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference, String metaModelVersionPreferencePageId) {
		Assert.isNotNull(metaModelVersionPreference);

		this.metaModelVersionPreference = metaModelVersionPreference;
		this.metaModelVersionPreferencePageId = metaModelVersionPreferencePageId;

		group = new Group(parent, SWT.NONE);
		group.setFont(parent.getFont());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout gridLayout = new GridLayout(3, false);
		group.setLayout(gridLayout);
		group.setText(getMetaModelVersionGroupLabel());

		// add a selection button widget for the workspace default metamodel version
		workspaceDefaultMetaModelVersionButton = new SelectionButtonField(SWT.RADIO);
		workspaceDefaultMetaModelVersionButton.setLabelText(NLS.bind(Messages.label_defaultMetaModelVersion,
				getWorkspaceDefaultMetaModelVersionLabel(), metaModelVersionPreference.getFromWorkspace().getName()));
		workspaceDefaultMetaModelVersionButton.fillIntoGrid(group, 2);
		workspaceDefaultMetaModelVersionButton.addFieldListener(this);

		// add a link widget for the workspace settings link
		configureWorkspaceSettingsLink = new Link(group, SWT.NONE);
		configureWorkspaceSettingsLink.setFont(group.getFont());
		configureWorkspaceSettingsLink.setText(MessageFormat.format("<a>{0}</a>", Messages.message_configureSetting)); //$NON-NLS-1$
		configureWorkspaceSettingsLink.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		configureWorkspaceSettingsLink.addSelectionListener(this);

		// add a selection button widget for the alternate metamodel version
		alternateMetaModelVersionButton = new SelectionButtonField(SWT.RADIO);
		alternateMetaModelVersionButton.setLabelText(getMetaModelVersionAlternateLabel());
		alternateMetaModelVersionButton.fillIntoGrid(group, 1);
		alternateMetaModelVersionButton.addFieldListener(this);

		// add a combo widget for the metamodel versions
		metaModelVersionCombo = new ComboField(SWT.READ_ONLY);
		metaModelVersionCombo.fillIntoGrid(group, 2);
		metaModelVersionCombo.addFieldListener(this);

		// set the supported metamodel versions in the combo field as the available items, and set the given
		// metaModelDescriptor as the selected item
		fillSupportedMetaModelVersions(baseMetaModelDescriptor);

		Combo comboControl = (Combo) metaModelVersionCombo.getComboControl();
		comboControl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		comboControl.setVisibleItemCount(10);

		switch (getLastSelectedMetaModelVersionOption()) {
		case WORKSPACE_DEFAULT_METAMODEL_VERSION:
			workspaceDefaultMetaModelVersionButton.setSelection(true);
			break;
		case ALTERNATE_METAMODEL_VERSION:
			alternateMetaModelVersionButton.setSelection(true);
			break;
		default:
			workspaceDefaultMetaModelVersionButton.setSelection(true);
			break;
		}

		updateEnableState();
	}

	protected String getMetaModelVersionName() {
		if (metaModelVersionName == null) {
			IMetaModelDescriptor mmDescriptor = metaModelVersionPreference.getFromWorkspace();
			Matcher matcher = META_MODEL_NAME_PATTERN.matcher(mmDescriptor.getName());
			if (matcher.find()) {
				metaModelVersionName = matcher.group(1);
			}
		}
		return metaModelVersionName;
	}

	public void setMetaModelVersionName(String metaModelVersionName) {
		this.metaModelVersionName = metaModelVersionName;
	}

	public String getMetaModelVersionLabel() {
		if (metaModelVersionLabel == null) {
			metaModelVersionLabel = Messages.defaultMetaModelVersionLabel;
		}
		return metaModelVersionLabel;
	}

	/**
	 * Sets the label of this metamodel version. This method can be overridden by AUTOSAR, to set the label to "release"
	 * 
	 * @param metaModelVersionLabel
	 */
	public void setMetaModelVersionLabel(String metaModelVersionLabel) {
		this.metaModelVersionLabel = metaModelVersionLabel;
	}

	/**
	 * Returns the label of this metamodel version group.
	 * <p>
	 * For example, Hummingbird version options, AUTOSAR release options, etc.
	 */
	protected String getMetaModelVersionGroupLabel() {
		return NLS.bind(Messages.label_metaModelVersionGroup, getMetaModelVersionName(), getMetaModelVersionLabel());
	}

	/**
	 * Returns the label of alternate metamodel version.
	 * <p>
	 * For example, "Alternate version", "Alternate release", etc.
	 */
	protected String getMetaModelVersionAlternateLabel() {
		return NLS.bind(Messages.label_metaModelVersionAlternate, getMetaModelVersionLabel());
	}

	/**
	 * Returns the label of workspace default metamodel version.
	 * <p>
	 * For example, "Workspace default version", "Workspace default release", etc.
	 */
	protected String getWorkspaceDefaultMetaModelVersionLabel() {
		return NLS.bind(Messages.label_workspaceDefaultMetaModelVersion, getMetaModelVersionLabel());
	}

	/**
	 * Sets the combo items using all the metamodel version descriptors of given {@link IMetaModelDescriptor
	 * metaModelDescriptor}. Sets the last selected metamodel version descriptor as the selected item of the combo if it
	 * exists, otherwise uses the default metamodel version descriptor from the workspace.
	 */
	public void fillSupportedMetaModelVersions(IMetaModelDescriptor metaModelDescriptor) {
		supportedMetaModelVersions = MetaModelDescriptorRegistry.INSTANCE.getDescriptors(metaModelDescriptor, true);
		if (!supportedMetaModelVersions.isEmpty()) {
			String[] items = new String[supportedMetaModelVersions.size()];
			for (int index = 0; index < supportedMetaModelVersions.size(); index++) {
				IMetaModelDescriptor descriptor = supportedMetaModelVersions.get(index);
				items[index] = String.format(Messages.metaModelVersion_labelPattern, descriptor.getName(), descriptor.getNamespace());
			}
			metaModelVersionCombo.setItems(items);
		}

		String selectedMetaModelVersionId = getLastSelectedMetaModelVersionIdentifier();
		IMetaModelDescriptor descriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(selectedMetaModelVersionId);
		if (supportedMetaModelVersions.contains(descriptor)) {
			metaModelVersionCombo.selectItem(supportedMetaModelVersions.indexOf(descriptor));
		} else {
			IMetaModelDescriptor workspaceMetaModelVersion = metaModelVersionPreference.getFromWorkspace();
			if (supportedMetaModelVersions.contains(workspaceMetaModelVersion)) {
				metaModelVersionCombo.selectItem(supportedMetaModelVersions.indexOf(workspaceMetaModelVersion));
			}
		}
	}

	/**
	 * Gets the last selected metamodel versions from DialogSettings.
	 */
	public int getLastSelectedMetaModelVersionOption() {
		IDialogSettings dialogSettings = Activator.getPlugin().getDialogSettings();
		if (dialogSettings.get(LAST_SELECTED_METAMODEL_VERSION_OPTION) == null) {
			return WORKSPACE_DEFAULT_METAMODEL_VERSION;
		}
		return dialogSettings.getInt(LAST_SELECTED_METAMODEL_VERSION_OPTION);
	}

	public void updateEnableState() {
		metaModelVersionCombo.setEnabled(alternateMetaModelVersionButton.isSelected());
		configureWorkspaceSettingsLink.setEnabled(workspaceDefaultMetaModelVersionButton.isSelected());
	}

	/**
	 * Gets the last selected metamodel version identifier from DialogSettings.
	 */
	public String getLastSelectedMetaModelVersionIdentifier() {
		IDialogSettings dialogSettings = Activator.getPlugin().getDialogSettings();
		return dialogSettings.get(LAST_SELECTED_METAMODEL_VERSION_KEY);
	}

	/**
	 * Stores the comboField selected item in DialogSettings.
	 * 
	 * @param comboField
	 *            the {@link ComboField combo field} for the metamodel version resource
	 */
	public void storeSelectionState(ComboField comboField) {
		int index = comboField.getSelectionIndex();
		if (index > -1) {
			IMetaModelDescriptor descriptor = supportedMetaModelVersions.get(index);
			if (descriptor != null) {
				String item = descriptor.getIdentifier();
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_VERSION_KEY, item);
			}
		}
	}

	/**
	 * Gets the metamodel version descriptor depending on the selection.
	 * <p>
	 * If the workspaceDefaultMetaModelVersionButton is selected, returns the default metamodel version descriptor from
	 * the workspace. If the alternateMetaModelVersionButton is selected, returns the item selected in the combo field.
	 * 
	 * @return the metamodel version descriptor selected from the Combo field if the alternateMetaModelVersionButton
	 *         button is selected, otherwise return the default metamodel version descriptor
	 */
	public IMetaModelDescriptor getMetaModelVersionDescriptor() {
		if (workspaceDefaultMetaModelVersionButton.isSelected()) {
			return metaModelVersionPreference.getFromWorkspace();
		} else if (alternateMetaModelVersionButton.isSelected()) {
			int index = metaModelVersionCombo.getSelectionIndex();
			if (index > -1) {
				IMetaModelDescriptor descriptor = supportedMetaModelVersions.get(index);
				return descriptor;
			}
		}
		return null;
	}

	/*
	 * @see
	 * org.eclipse.sphinx.platform.ui.fields.IFieldListener#dialogFieldChanged(org.eclipse.sphinx.platform.ui.fields
	 * .IField)
	 */
	public void dialogFieldChanged(IField field) {
		updateEnableState();
		if (field == workspaceDefaultMetaModelVersionButton) {
			Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_VERSION_OPTION, WORKSPACE_DEFAULT_METAMODEL_VERSION);
		} else if (field == alternateMetaModelVersionButton) {
			Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_VERSION_OPTION, ALTERNATE_METAMODEL_VERSION);
		} else if (field == metaModelVersionCombo) {
			if (alternateMetaModelVersionButton.isSelected()) {
				storeSelectionState((ComboField) field);
			}
		}
	}

	/*
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		PreferencesUtil.createPreferenceDialogOn(null, metaModelVersionPreferencePageId, new String[] { metaModelVersionPreferencePageId }, null)
				.open();
	}

	/*
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		widgetDefaultSelected(event);
	}
}
