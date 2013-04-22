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
 *     itemis - [400306] Add a NewModelCreationPage class allowing to choose the metamodel, EPackage and EClassifier when creating a new model file
 *     itemis - [406062] Removal of the required project nature parameter in NewModelFileCreationPage constructor and CreateNewModelProjectJob constructor
 *     itemis - [406194] Enable title and descriptions of model project and file creation wizards to be calculated automatically
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog;
import org.eclipse.sphinx.platform.ui.fields.ComboButtonField;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Basic (optional) page for choosing the properties of the initial model for a new model file to be created.
 * <p>
 * This page lists available metamodels, ePackages and eClassifiers for creating a model file. It allows clients to
 * select the metamodel, ePackage and eClassifier that they would like to use for creating their model file. The
 * selected results of metamodel, ePackage and eClassifier are passed to the post pages, e.g., NewModelFileCreationPage,
 * using {@linkplain InitialModelProperties model file properties}. This page may be used by clients as it is; it may
 * also be subclassed to suit.
 */
public class InitialModelCreationPage<T extends IMetaModelDescriptor> extends WizardPage {

	/**
	 * A metamodel descriptor browse dialog enabling the filtering of metamodel descriptor objects. The supported
	 * metamodel descriptors are used as the resource to be filtered.
	 */
	private class MetaModelDescriptorBrowseDialog extends AbstractBrowseDialog<T> {

		public MetaModelDescriptorBrowseDialog(Shell shell, Collection<T> metaModelDescriptors) {
			super(shell, metaModelDescriptors);
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toObject(java.lang.String)
		 */
		@Override
		protected T toObject(String itemAsString) {
			for (T descriptor : supportedMetaModelDescriptors) {
				if (descriptor.getName() == itemAsString) {
					return descriptor;
				}
			}
			return null;
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toString(java.lang.Object)
		 */
		@Override
		protected String toString(T itemAsObject) {
			return itemAsObject.getName();
		}

		/**
		 * Returns the selection of metamodel descriptor made by the user, or <code>null</code> if the selection was
		 * canceled.
		 */
		@SuppressWarnings("unchecked")
		public T getMetaModelResult() {
			Object[] result = super.getResult();
			return (T) result[0];
		}

	}

	/**
	 * A EPackage browse dialog enabling the filtering of EPackage objects. The supported EPackages are used as the
	 * resource to be filtered.
	 */
	private class EPackageBrowseDialog extends AbstractBrowseDialog<EPackage> {

		public EPackageBrowseDialog(Shell shell, Collection<EPackage> ePackage) {
			super(shell, ePackage);
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toObject(java.lang.String)
		 */
		@Override
		protected EPackage toObject(String itemAsString) {
			for (EPackage epackage : supportedEPackages) {
				if (epackage.getName() == itemAsString) {
					return epackage;
				}
			}
			return null;
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toString(java.lang.Object)
		 */
		@Override
		protected String toString(EPackage itemAsObject) {
			return itemAsObject.getName();
		}

		/**
		 * Returns the selection of EPackage made by the user, or <code>null</code> if the selection was canceled.
		 */
		public EPackage getEPackageResult() {
			Object[] result = super.getResult();
			return (EPackage) result[0];
		}
	}

	/**
	 * A EClassifier browse dialog enabling the filtering of EClassifier objects. The supported EClassifiers are used as
	 * the resource to be filtered.
	 */
	private class EClassifierBrowseDialog extends AbstractBrowseDialog<EClassifier> {

		public EClassifierBrowseDialog(Shell shell, Collection<EClassifier> eClassifier) {
			super(shell, eClassifier);
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toObject(java.lang.String)
		 */
		@Override
		protected EClassifier toObject(String itemAsString) {
			for (EClassifier eclassifier : supportedEClassifiers) {
				if (eclassifier.getName() == itemAsString) {
					return eclassifier;
				}
			}
			return null;
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toString(java.lang.Object)
		 */
		@Override
		protected String toString(EClassifier itemAsObject) {
			return itemAsObject.getName();
		}

		/**
		 * Returns the selection of eclassifier made by the user, or <code>null</code> if the selection was canceled.
		 */
		public EClassifier getEClassifierResult() {
			Object[] result = super.getResult();
			return (EClassifier) result[0];
		}
	}

	private Composite container;
	private final String LAST_SELECTED_METAMODEL_KEY = Activator.getPlugin().getSymbolicName() + "last.selected.metamodel"; //$NON-NLS-1$
	private final String LAST_SELECTED_EPACKAGE_KEY = Activator.getPlugin().getSymbolicName() + "last.selected.eproject"; //$NON-NLS-1$
	private final String LAST_SELECTED_ECLASSIFIER_KEY = Activator.getPlugin().getSymbolicName() + "last.selected.eproject.eclassifier"; //$NON-NLS-1$

	protected ComboButtonField metaModelCombo, ePackageCombo, eClassifierCombo;
	protected List<T> supportedMetaModelDescriptors = new ArrayList<T>();
	protected List<EPackage> supportedEPackages = new ArrayList<EPackage>();
	protected List<EClassifier> supportedEClassifiers = new ArrayList<EClassifier>();
	protected InitialModelProperties<T> initialModelProperties;
	protected T baseMetaModelDescriptor;
	protected ISelection selection;

	/**
	 * Button adapter for the choice of meta-model. This class will create a
	 * {@linkplain MetaModelDescriptorBrowseDialog metamodel descriptor browse dialog} which allows users to filter and
	 * selete the metamodel descriptor that they would like to use to create the new model file. The selection result is
	 * stored. This result is also used to set the relative supported EPackages.
	 */
	protected class MetaModelButtonAdapter extends AbstractButtonAdapter {

		/*
		 * @see org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#doCreateDialog()
		 */
		@Override
		protected Dialog doCreateDialog() {
			// Add the items in supportedMetaModelDescriptors as input for metamodel browse dialog
			Shell parent = ExtendedPlatformUI.getActiveShell();
			MetaModelDescriptorBrowseDialog dialog = new MetaModelDescriptorBrowseDialog(parent, supportedMetaModelDescriptors);
			dialog.setTitle(Messages.dialog_metaModelDescriptorBrowse_title);
			dialog.setMessage(Messages.dialog_browse_message);
			return dialog;
		}

		/*
		 * @see
		 * org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#performOk(org.eclipse.sphinx.platform
		 * .ui.fields.IField, org.eclipse.jface.dialogs.Dialog)
		 */
		@Override
		protected void performOk(IField field, Dialog dialog) {
			super.performOk(field, dialog);

			// Get the resulting metamodel descriptor from browse dialog and store it
			@SuppressWarnings("unchecked")
			T result = ((MetaModelDescriptorBrowseDialog) dialog).getMetaModelResult();
			storeBrowserSelectionMetaModel((ComboButtonField) field, result);

			// Set the relative supported epackages depending on the metamodel selection result, and fill the resource
			// of ePackageCombo
			initSupportedEPackages();
			fillEPackageCombo();
		}
	}

	/**
	 * Button adapter for the choice of EPackage. This class will create a {@linkplain EPackageBrowseDialog epackage
	 * browse dialog} which allows users to filter and selete the epackage that they would like to use to create the new
	 * model file. The selection result is stored. This result is also used to set the relative supported EClassifiers.
	 */
	protected class EPackageButtonAdapter extends AbstractButtonAdapter {

		/*
		 * @see org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#doCreateDialog()
		 */
		@Override
		protected Dialog doCreateDialog() {
			// Add the supportEPackages as resource for this ePackage dialog
			Shell parent = ExtendedPlatformUI.getActiveShell();
			EPackageBrowseDialog dialog = new EPackageBrowseDialog(parent, supportedEPackages);

			dialog.setTitle(Messages.dialog_ePackageBrowse_title);
			dialog.setMessage(Messages.dialog_browse_message);

			return dialog;
		}

		/*
		 * @see
		 * org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#performOk(org.eclipse.sphinx.platform
		 * .ui.fields.IField, org.eclipse.jface.dialogs.Dialog)
		 */
		@Override
		protected void performOk(IField field, Dialog dialog) {
			super.performOk(field, dialog);

			// Get the dialog result, and restore it
			@SuppressWarnings("unchecked")
			EPackage result = ((EPackageBrowseDialog) dialog).getEPackageResult();
			storeBrowserSelectionEPackage((ComboButtonField) field, result);

			// Set relative supported eClassifiers, and fill the resource of eClassifierCombo
			initSupportedEClassifiers();
			fillEClassifierCombo();
		}
	}

	/**
	 * Button adapter for the choice of EClassifier. This class will create a {@linkplain EClassifierBrowseDialog
	 * eclassifier browse dialog} which allows users to filter and selete the eclassifier that they would like to use to
	 * create the new model file. The selection result is stored.
	 */
	protected class EClassifierButtonAdapter extends AbstractButtonAdapter {

		/*
		 * @see org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#doCreateDialog()
		 */
		@Override
		protected Dialog doCreateDialog() {
			// Add the supportEClassifiers as resource for this eClassifier dialog
			Shell parent = ExtendedPlatformUI.getActiveShell();
			EClassifierBrowseDialog dialog = new EClassifierBrowseDialog(parent, supportedEClassifiers);

			dialog.setTitle(Messages.dialog_eClassifierBrowse_title);
			dialog.setMessage(Messages.dialog_browse_message);

			return dialog;
		}

		/*
		 * @see
		 * org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#performOk(org.eclipse.sphinx.platform
		 * .ui.fields.IField, org.eclipse.jface.dialogs.Dialog)
		 */
		@Override
		protected void performOk(IField field, Dialog dialog) {
			super.performOk(field, dialog);

			// Get the dialog result, and restore it
			@SuppressWarnings("unchecked")
			EClassifier result = ((EClassifierBrowseDialog) dialog).getEClassifierResult();
			storeBrowserSelectionEClassifier((ComboButtonField) field, result);
		}
	}

	/**
	 * Creates a new instance of model creation wizard page. The supported available metamodel descriptors and EPackages
	 * are initialized. The selected results of metamodel, ePackage and eClassifier are passed to the post pages, e.g.,
	 * NewModelFileCreationPage, using {@linkplain InitialModelProperties model file properties}.
	 * 
	 * @param pageName
	 *            the name of the page
	 * @param selection
	 *            the current resource {@linkplain ISelection selection}
	 * @param initialModelProperties
	 *            the chosen {@linkplain InitialModelProperties initial model properties} (metamodel, EPackage and
	 *            EClassifier) to be used as basis for creating the initial model of the new model file, must not be
	 *            <code>null</code>
	 * @param baseMetaModelDescriptor
	 *            the base meta model descriptor to be used for the creation of model file
	 */
	public InitialModelCreationPage(String pageName, ISelection selection, InitialModelProperties<T> initialModelProperties, T baseMetaModelDescriptor) {
		super(pageName);
		Assert.isNotNull(initialModelProperties);
		Assert.isLegal(baseMetaModelDescriptor != MetaModelDescriptorRegistry.NO_MM);

		this.selection = selection;
		this.initialModelProperties = initialModelProperties;
		this.baseMetaModelDescriptor = baseMetaModelDescriptor;

		if (baseMetaModelDescriptor != null && baseMetaModelDescriptor.getName() != MetaModelDescriptorRegistry.ANY_MM.getName()) {
			setTitle(NLS.bind(Messages.page_newInitialModelCreation_title, baseMetaModelDescriptor.getName()));
			setDescription(NLS.bind(Messages.page_newInitialModelCreation_description, baseMetaModelDescriptor.getName()));
		} else {
			setTitle(Messages.page_newInitialModelCreation_defaultTitle);
			setDescription(NLS.bind(Messages.page_newInitialModelCreation_description, Messages.default_metamodelName));
		}

		// Initialize supported meta-models
		supportedMetaModelDescriptors = getSupportedMetaModelDescriptors();

		// Initialize supported EPackages
		for (int index = 0; index < supportedMetaModelDescriptors.size(); index++) {
			Collection<EPackage> ePackages = supportedMetaModelDescriptors.get(index).getEPackages();
			supportedEPackages.addAll(ePackages);
		}
	}

	/**
	 * Gets the supported metamodel descriptors, all the metamodel descriptors that are registed by default. This method
	 * can be overridden by clients to provide specific suported metamodel descriptors.
	 */
	protected List<T> getSupportedMetaModelDescriptors() {
		return MetaModelDescriptorRegistry.INSTANCE.getDescriptors(baseMetaModelDescriptor, true);
	}

	/**
	 * Creates and adds the ComboButtonFields for metamodel, ePackage and eClassifier under the given parent composite.
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginRight = IDialogConstants.SMALL_INDENT;
		gridLayout.marginLeft = IDialogConstants.SMALL_INDENT;
		gridLayout.horizontalSpacing = IDialogConstants.HORIZONTAL_MARGIN;
		gridLayout.verticalSpacing = IDialogConstants.VERTICAL_MARGIN;
		container.setLayout(gridLayout);

		// Create a ComboButtonField for meta-model
		createMetaModelComboButtonField(container);
		// Create a ComboButtonField for EPackage
		createEPackageComboButtonField(container);
		// Create a ComboButtonField for EClassifier
		createEClassifierComboButtonField(container);

		// Add listeners
		metaModelCombo.addFieldListener(new fieldListener());
		fillMetaModelCombo();
		ePackageCombo.addFieldListener(new fieldListener());
		eClassifierCombo.addFieldListener(new fieldListener());

		setControl(container);
		setPageComplete(validatePage());

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
	}

	/**
	 * Creates a ComboButtonField for the choice of meta-model.
	 */
	protected void createMetaModelComboButtonField(Composite container) {
		MetaModelButtonAdapter metaModelAdapter = new MetaModelButtonAdapter();
		metaModelCombo = new ComboButtonField(metaModelAdapter);
		metaModelCombo.setLabelText(Messages.combo_metaModelDescriptor_label);
		metaModelCombo.setButtonLabel(Messages.button_browse_label);
		metaModelCombo.fillIntoGrid(container, 3);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		metaModelCombo.getComboControl().setLayoutData(gridData);
	}

	/**
	 * Creates a ComboButtonField for the choice of EPackage.
	 */
	protected void createEPackageComboButtonField(Composite container) {
		EPackageButtonAdapter packageAdapter = new EPackageButtonAdapter();
		ePackageCombo = new ComboButtonField(packageAdapter);
		ePackageCombo.setLabelText(Messages.combo_ePackage_label);
		ePackageCombo.setButtonLabel(Messages.button_browse_label);
		ePackageCombo.fillIntoGrid(container, 3);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		ePackageCombo.getComboControl().setLayoutData(gridData);
	}

	/**
	 * Creates a ComboButtonField for the choice of EClassifier.
	 */
	protected void createEClassifierComboButtonField(Composite container) {
		EClassifierButtonAdapter classifierAdapter = new EClassifierButtonAdapter();
		eClassifierCombo = new ComboButtonField(classifierAdapter);
		eClassifierCombo.setLabelText(Messages.combo_eClassifier_label);
		eClassifierCombo.setButtonLabel(Messages.button_browse_label);
		eClassifierCombo.fillIntoGrid(container, 3);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		eClassifierCombo.getComboControl().setLayoutData(gridData);
	}

	/**
	 * Sets the supported meta-model descriptors as the resource items in the meta-model combo field.
	 */
	protected void fillMetaModelCombo() {
		String[] items = new String[supportedMetaModelDescriptors.size()];
		for (int index = 0; index < supportedMetaModelDescriptors.size(); index++) {
			items[index] = supportedMetaModelDescriptors.get(index).getName();
		}
		metaModelCombo.setItems(items);
	}

	/**
	 * Sets the supported EPackages as the resource items in the EPackage combo field.
	 */
	protected void fillEPackageCombo() {

		String[] items = new String[supportedEPackages.size()];
		for (int index = 0; index < supportedEPackages.size(); index++) {
			items[index] = supportedEPackages.get(index).getName();
		}
		ePackageCombo.setItems(items);

	}

	/**
	 * Set the supported EClassifiers as the resource items in the EClassifier combo field.
	 */
	protected void fillEClassifierCombo() {
		String[] items = new String[supportedEClassifiers.size()];
		for (int index = 0; index < supportedEClassifiers.size(); index++) {
			items[index] = supportedEClassifiers.get(index).getName();
		}
		eClassifierCombo.setItems(items);
	}

	/**
	 * Field listener to deal with change events of dialog fields.
	 */
	public class fieldListener implements IFieldListener, SelectionListener {
		/*
		 * @see
		 * org.eclipse.sphinx.platform.ui.fields.IFieldListener#dialogFieldChanged(org.eclipse.sphinx.platform.ui.fields
		 * .IField)
		 */
		public void dialogFieldChanged(IField field) {
			if (field == metaModelCombo) {
				storeSelectionMetaModel((ComboButtonField) field);
				initSupportedEPackages();
				fillEPackageCombo();
			} else if (field == ePackageCombo) {
				storeSelectionEPackage((ComboButtonField) field);
				initSupportedEClassifiers();
				fillEClassifierCombo();
			} else if (field == eClassifierCombo) {
				storeSelectionEClassifier((ComboButtonField) field);
			}
		}

		/*
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}

		/*
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}
	}

	/**
	 * Stores the meta-model descriptor selected by the metamodel browse dialog, and sets the value as the selected item
	 * in the combo field.
	 * 
	 * @param field
	 *            the {@linkplain ComboButtonField field} of meta-model
	 * @param metaModelDescriptor
	 *            the {@linkplain T metamodel descriptor } result selected by the metamodel browse dialog
	 */
	private void storeBrowserSelectionMetaModel(ComboButtonField field, T metaModelDescriptor) {
		// Store the selected meta-model
		Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_KEY, metaModelDescriptor.getIdentifier());

		// Set the selected meta-model of elements
		initialModelProperties.setMetaModelDescriptor(metaModelDescriptor);

		// Set the selected meta-model as the selection item in the combo field
		int metaModelIndex = supportedMetaModelDescriptors.indexOf(metaModelDescriptor);
		Combo control = (Combo) field.getComboControl();
		control.select(metaModelIndex);
		setPageComplete(validatePage());
	}

	/**
	 * Stores the EPackage selected by the epackage browse dialog, and sets the value as the selected item in the combo
	 * field.
	 * 
	 * @param field
	 *            the {@linkplain ComboButtonField field } of EPackage
	 * @param epackage
	 *            the {@linkplain EPackage epackage} result selected by the browser
	 */
	private void storeBrowserSelectionEPackage(ComboButtonField field, EPackage epackage) {
		// Store the selected EPackage
		Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_EPACKAGE_KEY, epackage.getName());

		// Set the value of selected EPackage of elements
		initialModelProperties.setRootObjectEPackage(epackage);

		// Set the selected EPackage as the selection item in the combo field
		int ePackageIndex = supportedEPackages.indexOf(epackage);
		Combo control = (Combo) field.getComboControl();
		control.select(ePackageIndex);
		setPageComplete(validatePage());
	}

	/**
	 * Stores the EClassifier selected by the eclassifier browse dialog, and sets the value as the selected item in the
	 * combo field.
	 * 
	 * @param field
	 *            the {@link ComboButtonField field} of EClassifier
	 * @param eclassifier
	 *            the {@link EClassifier eclassifier} result selected by the eclassifier browse dialog
	 */
	private void storeBrowserSelectionEClassifier(ComboButtonField field, EClassifier eclassifier) {
		// Store the selected EPackage
		Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_ECLASSIFIER_KEY, eclassifier.getName());

		// Set the value of selected EClassifier of elements
		initialModelProperties.setRootObjectEClassifier(eclassifier);

		// Set the selected EClassifier as the selection item in the combo field
		int indexEClassifier = supportedEClassifiers.indexOf(eclassifier);
		Combo control = (Combo) field.getComboControl();
		control.select(indexEClassifier);
		setPageComplete(validatePage());
	}

	/**
	 * Stores meta-model descriptor result selected by the combo.
	 * 
	 * @param field
	 *            the {@link ComboButtonField field} of meta-model
	 */
	private void storeSelectionMetaModel(ComboButtonField field) {
		int index = field.getSelectionIndex();
		if (index > -1) {

			// Get the selected meta-model descriptor identifier
			String descriptor = supportedMetaModelDescriptors.get(index).getIdentifier();
			if (descriptor != null) {
				// Store the selected meta-model
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_KEY, descriptor);

				// Set the selected meta-model value in the elements
				initialModelProperties.setMetaModelDescriptor(supportedMetaModelDescriptors.get(index));
				setPageComplete(validatePage());
			}
		}
	}

	/**
	 * Stores the EPackage result selected by the combo.
	 * 
	 * @param field
	 *            the {@link ComboButtonField field} of EPackage
	 */
	private void storeSelectionEPackage(ComboButtonField field) {
		int index = field.getSelectionIndex();
		if (index > -1) {

			// Get the selected EPackage
			EPackage epackage = supportedEPackages.get(index);
			String packageName = epackage.getName();
			if (packageName != null) {
				// Store the selected EPackage
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_EPACKAGE_KEY, packageName);

				// Set the selected EPackage value in the elements
				initialModelProperties.setRootObjectEPackage(supportedEPackages.get(index));
				setPageComplete(validatePage());
			}
		}
	}

	/**
	 * Stores the EClassifier result selected by the combo.
	 * 
	 * @param field
	 *            the {@link ComboButtonField field} of EClassifier
	 */
	private void storeSelectionEClassifier(ComboButtonField field) {
		int index = field.getSelectionIndex();
		if (index > -1) {

			// Get the selected EClassifier
			String eclassifier = supportedEClassifiers.get(index).getName();
			if (eclassifier != null) {
				// Store the selected EClassifier
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_ECLASSIFIER_KEY, eclassifier);

				// Set the selected EClassifier value in the elements
				initialModelProperties.setRootObjectEClassifier(supportedEClassifiers.get(index));
				setPageComplete(validatePage());
			}
		}
	}

	/**
	 * Checks if the eClass can be instantiated.
	 */
	private boolean isEClassInstantiated(EClass eClass) {
		return !eClass.isAbstract() && !eClass.isInterface();
	}

	/**
	 * Checks if the ePackage can be instantiated. If none of its contained eClasses can be instantiated, then it
	 * returns false, true otherwise.
	 */
	private boolean isEPackageInstantiated(EPackage ePackage) {
		for (EClassifier eClassifier : ePackage.getEClassifiers()) {
			if (eClassifier instanceof EClass) {
				EClass eClass = (EClass) eClassifier;
				if (isEClassInstantiated(eClass)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets supported EPackages depending on the selected meta-model descriptor
	 */
	protected void initSupportedEPackages() {
		T metaModelDescriptor = initialModelProperties.getMetaModelDescriptor();
		if (metaModelDescriptor != null) {
			supportedEPackages.clear();

			// Add only the ePackage that can be instantiated
			for (EPackage ePackage : metaModelDescriptor.getEPackages()) {
				if (isEPackageInstantiated(ePackage)) {
					supportedEPackages.add(ePackage);
				}
			}
		}
	}

	/**
	 * Sets supported EClassifiers depending on the selected EPackage value
	 */
	protected void initSupportedEClassifiers() {
		EPackage epackage = initialModelProperties.getRootObjectEPackage();
		if (epackage != null) {
			supportedEClassifiers.clear();

			// Add only the eClass that can be instantiated
			for (EClassifier eClassifier : epackage.getEClassifiers()) {
				if (eClassifier instanceof EClass) {
					EClass eClass = (EClass) eClassifier;
					if (isEClassInstantiated(eClass)) {
						supportedEClassifiers.add(eClass);
					}
				}
			}
		}
	}

	/**
	 * The framework calls this to validate if all the field are selected.
	 */
	protected boolean validatePage() {
		if (initialModelProperties.getMetaModelDescriptor() == null) {
			setErrorMessage(Messages.error_noMetaModelDescriptorSelected);
			return false;
		}

		if (initialModelProperties.getRootObjectEPackage() == null) {
			setErrorMessage(Messages.error_noEPackageSelected);
			return false;
		}

		if (initialModelProperties.getRootObjectEClassifier() == null) {
			setErrorMessage(Messages.error_noEClassifierSelected);
			return false;
		}

		setErrorMessage(null);
		return true;
	}
}
