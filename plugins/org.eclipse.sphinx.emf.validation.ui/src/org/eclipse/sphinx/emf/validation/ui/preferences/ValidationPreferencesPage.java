/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.emf.validation.ui.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.sphinx.emf.validation.preferences.IValidationPreferences;
import org.eclipse.sphinx.emf.validation.ui.Activator;
import org.eclipse.sphinx.emf.validation.ui.util.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ValidationPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// Automatic Validation Group
	private Group fAutomaticValidationGroup;
	private BooleanFieldEditorEx fAutomaticValidationField;

	private IntegerFieldEditor maxNumberOfErrors;

	// EMF Rules Group
	private Group fEMFRulesGroup;
	private BooleanFieldEditorEx fEMFRulesField;

	private BooleanFieldEditor fStrictEditingField;

	// Performance Optimization Group
	private Group performanceOptimizationGroup;
	private BooleanFieldEditorEx fMaxPerformanceSettingsField;

	public ValidationPreferencesPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

	@Override
	public boolean isValid() {
		if (fMaxPerformanceSettingsField != null) {
			boolean enabled = getPreferenceStore().getBoolean(IValidationPreferences.PREF_APPLY_MAX_PERFORMANCE_SETTINGS);
			boolean selection = fMaxPerformanceSettingsField.getChangeControl(performanceOptimizationGroup).getSelection();
			if (selection != enabled) {
				fMaxPerformanceSettingsField.getChangeControl(performanceOptimizationGroup).setSelection(enabled);
				applyMaxPerformanceSettings();
			}
		}
		return super.isValid();
	}

	@Override
	protected void createFieldEditors() {

		Composite parent = getFieldEditorParent();
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(composite);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setPreferenceStore(org.eclipse.sphinx.emf.validation.ui.Activator.getDefault().getPreferenceStore());
		addAutomaticValidationField(composite);
		addEMFRuleField(composite);
		addStrictEditingGroup(composite);
		addProblemIndicationField(composite);
		addPerformanceOptimizationGroup(composite);
	}

	/**
	 * The automatic validation part
	 * 
	 * @param parent
	 */
	protected void addAutomaticValidationField(Composite parent) {

		// Create a Group to hold the version field
		fAutomaticValidationGroup = new Group(parent, SWT.NONE);
		fAutomaticValidationGroup.setText(Messages._UI_automaticValidation_groupText);

		GridLayout gridLayout = new GridLayout(2, false);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);

		fAutomaticValidationField = new BooleanFieldEditorEx(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION,
				Messages._UI_enableDisableAutomaticValidationPreferencesMsg, BooleanFieldEditor.DEFAULT, fAutomaticValidationGroup);
		addField(fAutomaticValidationField);

		fAutomaticValidationGroup.setLayoutData(gridData);
		fAutomaticValidationGroup.setLayout(gridLayout);
	}

	/**
	 * The automatic validation part
	 * 
	 * @param parent
	 */
	protected void addEMFRuleField(Composite parent) {

		// Create a Group to hold the version field
		fEMFRulesGroup = new Group(parent, SWT.NONE);
		fEMFRulesGroup.setText(Messages._UI_EMFConstraintsGroupText);

		GridLayout gridLayout = new GridLayout(2, false);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);

		fEMFRulesField = new BooleanFieldEditorEx(IValidationPreferences.PREF_ENABLE_EMF_DEFAULT_RULES,
				Messages._UI_EMFConstraintsEnabledPreferencesMsg, BooleanFieldEditor.DEFAULT, fEMFRulesGroup);
		addField(fEMFRulesField);

		fEMFRulesGroup.setLayoutData(gridData);
		fEMFRulesGroup.setLayout(gridLayout);

	}

	protected void addStrictEditingGroup(Composite parent) {

		Group restrictionGroup = new Group(parent, SWT.NONE);
		restrictionGroup.setText(Messages._UI_StrictEditingGroupText);
		GridLayout gridLayout = new GridLayout(2, false);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;

		fStrictEditingField = new BooleanFieldEditor(IValidationPreferences.PREF_STRICT_EDITING, Messages._UI_StrictEditingFieldLabel,
				restrictionGroup);
		addField(fStrictEditingField);
		restrictionGroup.setLayoutData(gridData);
		restrictionGroup.setLayout(gridLayout);
		Dialog.applyDialogFont(restrictionGroup);
	}

	/**
	 * The automatic validation part
	 * 
	 * @param parent
	 */
	protected void addProblemIndicationField(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages._UI_ProblemIndicationGroupText);

		GridLayout gridLayout = new GridLayout(2, false);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);

		maxNumberOfErrors = new IntegerFieldEditor(IValidationPreferences.PREF_MAX_NUMBER_OF_ERRORS, Messages._UI_ProblemIndicationFieldLabelText,
				group);
		addField(maxNumberOfErrors);

		group.setLayoutData(gridData);
		group.setLayout(gridLayout);

	}

	protected void addPerformanceOptimizationGroup(Composite parent) {

		performanceOptimizationGroup = new Group(parent, SWT.NONE);
		performanceOptimizationGroup.setText(Messages._UI_PerformanceOptimizationGroupText);
		GridLayout layout = new GridLayout(2, false);

		fMaxPerformanceSettingsField = new BooleanFieldEditorEx(IValidationPreferences.PREF_APPLY_MAX_PERFORMANCE_SETTINGS,
				Messages._UI_MaxPerfLabelText, BooleanFieldEditor.DEFAULT, performanceOptimizationGroup);
		fMaxPerformanceSettingsField.getChangeControl(performanceOptimizationGroup).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performApply();
				forwardPreferenceChange();
				applyMaxPerformanceSettings();
			}
		});
		addField(fMaxPerformanceSettingsField);
		performanceOptimizationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		performanceOptimizationGroup.setLayout(layout);
		updateEnabledState(getPreferenceStore().getBoolean(IValidationPreferences.PREF_APPLY_MAX_PERFORMANCE_SETTINGS));
		Dialog.applyDialogFont(performanceOptimizationGroup);
	}

	protected void forwardPreferenceChange() {
		IEclipsePreferences node = new InstanceScope().getNode("org.eclipse.sphinx.common"); //$NON-NLS-1$
		node.putBoolean(IValidationPreferences.PREF_APPLY_MAX_PERFORMANCE_SETTINGS, fMaxPerformanceSettingsField.getBooleanValue());
	}

	private void applyMaxPerformanceSettings() {
		Button button = fMaxPerformanceSettingsField.getChangeControl(performanceOptimizationGroup);
		boolean enabled = button.getSelection();
		if (enabled) {
			// Max performance settings for Automatic Validation group
			fAutomaticValidationField.getChangeControl(fAutomaticValidationGroup).setSelection(
					IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION_DEFAULT);

			// Max performance settings for EMF Rules group
			fEMFRulesField.getChangeControl(fEMFRulesGroup).setSelection(IValidationPreferences.PREF_ENABLE_EMF_DEFAULT_RULES_DEFAULT);
		}
		updateEnabledState(enabled);
	}

	private void updateEnabledState(boolean enabled) {
		fAutomaticValidationField.setEnabled(!enabled, fAutomaticValidationGroup);
		fEMFRulesField.setEnabled(!enabled, fEMFRulesGroup);
	}

	public class BooleanFieldEditorEx extends BooleanFieldEditor {

		private Button fChangeControl;

		/**
		 * @see BooleanFieldEditor#BooleanFieldEditor(java.lang.String, java.lang.String, int,
		 *      org.eclipse.swt.widgets.Composite)
		 */
		public BooleanFieldEditorEx(String name, String labelText, int style, Composite parent) {
			super(name, labelText, style, parent);
		}

		/**
		 * @see org.eclipse.jface.preference.BooleanFieldEditor#getChangeControl(Composite)
		 */
		@Override
		public Button getChangeControl(Composite parent) {
			if (fChangeControl == null) {
				fChangeControl = super.getChangeControl(parent);
			}
			return fChangeControl;
		}
	}
}
