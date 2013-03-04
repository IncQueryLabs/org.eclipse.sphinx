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
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.ui.wizards.BasicNewModelFileWizard.NewModelFileProperties;
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
 * Basic (optional) page for a wizard that creates a file resource.
 * <p>
 * This page lists available metamodels, ePackages and eClassifiers for creating a model file. It allows clients to
 * select the metamodel, ePackage and eClassifier that they would like to use for creating their model file. The
 * selected results of metamodel, ePackage and eClassifier are passed to the post pages, e.g., NewModelFileCreationPage,
 * using {@linkplain NewModelFileProperties model file properties}. This page may be used by clients as it is; it may
 * also be subclassed to suit.
 */
public class NewModelCreationPage extends WizardPage {

	/**
	 * A metamodel descriptor browse dialog enabling the filtering of metamodel descriptor objects. The supported
	 * metamodel descriptors are used as the resource to be filtered.
	 */
	private class MetaModelDescriptorBrowseDialog extends AbstractBrowseDialog<IMetaModelDescriptor> {

		public MetaModelDescriptorBrowseDialog(Shell shell, Collection<IMetaModelDescriptor> mmDescriptors) {
			super(shell, mmDescriptors);
		}

		/*
		 * @see org.eclipse.sphinx.platform.ui.dialogs.AbstractBrowseDialog#toObject(java.lang.String)
		 */
		@Override
		protected IMetaModelDescriptor toObject(String itemAsString) {
			for (IMetaModelDescriptor descriptor : supportedMMDescriptorList) {
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
		protected String toString(IMetaModelDescriptor itemAsObject) {
			return itemAsObject.getName();
		}

		/**
		 * Returns the selection of metamodel descriptor made by the user, or <code>null</code> if the selection was
		 * canceled.
		 */
		public IMetaModelDescriptor getMetaModelResult() {
			Object[] result = super.getResult();
			return (IMetaModelDescriptor) result[0];
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
			for (EPackage epackage : supportedEPackageList) {
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
			for (EClassifier eclassifier : supportedEClassifierList) {
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

	protected ComboButtonField mmCombo, ePackageCombo, eClassifierCombo;
	protected List<IMetaModelDescriptor> supportedMMDescriptorList = new ArrayList<IMetaModelDescriptor>();
	protected List<EPackage> supportedEPackageList = new ArrayList<EPackage>();
	protected List<EClassifier> supportedEClassifierList = new ArrayList<EClassifier>();
	protected NewModelFileProperties newModelFileProperties;
	protected ISelection selection;

	/**
	 * Button adapter for the choice of meta-model. This class will create a
	 * {@linkplain MetaModelDescriptorBrowseDialog metamodel descriptor browse dialog} which allows users to filter and
	 * selete the metamodel descriptor that they would like to use to create the new model file. The selection result is
	 * stored. This result is also used to set the relative supported EPackages.
	 */
	public class MMButtonAdapter extends AbstractButtonAdapter {

		/*
		 * @see org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#doCreateDialog()
		 */
		@Override
		protected Dialog doCreateDialog() {
			// Add the items in supportedMMDescriptorList as input for metamodel browse dialog
			Shell parent = ExtendedPlatformUI.getActiveShell();
			MetaModelDescriptorBrowseDialog dialog = new MetaModelDescriptorBrowseDialog(parent, supportedMMDescriptorList);
			dialog.setTitle(Messages.title_metaModelDescriptorBrowseDialog);
			dialog.setMessage(Messages.FilteredItemsSelectionDialog_pattern);
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
			IMetaModelDescriptor result = ((MetaModelDescriptorBrowseDialog) dialog).getMetaModelResult();
			storeBrowserSelectionMM((ComboButtonField) field, result);

			// Set the relative supported epackages depending on the metamodel selection result, and fill the resource
			// of ePkgCombo
			setSupportedEPackages();
			fillSupportedEPackages();
		}
	}

	/**
	 * Button adapter for the choice of EPackage. This class will create a {@linkplain EPackageBrowseDialog epackage
	 * browse dialog} which allows users to filter and selete the epackage that they would like to use to create the new
	 * model file. The selection result is stored. This result is also used to set the relative supported EClassifiers.
	 */
	public class EPkgButtonAdapter extends AbstractButtonAdapter {

		/*
		 * @see org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#doCreateDialog()
		 */
		@Override
		protected Dialog doCreateDialog() {
			// Add the supportEPkgList as resource for this ePackage dialog
			Shell parent = ExtendedPlatformUI.getActiveShell();
			EPackageBrowseDialog dialog = new EPackageBrowseDialog(parent, supportedEPackageList);

			dialog.setTitle(Messages.title_epackageBrowseDialog);
			dialog.setMessage(Messages.FilteredItemsSelectionDialog_pattern);

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
			EPackage result = ((EPackageBrowseDialog) dialog).getEPackageResult();
			storeBrowserSelectionEPkg((ComboButtonField) field, result);

			// Set relative supported eClassifiers, and fill the resource of eClassifierCombo
			setSupportedEClassifiers();
			fillSupportedEClassifiers();
		}
	}

	/**
	 * Button adapter for the choice of EClassifier. This class will create a {@linkplain EClassifierBrowseDialog
	 * eclassifier browse dialog} which allows users to filter and selete the eclassifier that they would like to use to
	 * create the new model file. The selection result is stored.
	 */
	public class EClassifierButtonAdapter extends AbstractButtonAdapter {

		/*
		 * @see org.eclipse.sphinx.platform.ui.fields.adapters.AbstractButtonAdapter#doCreateDialog()
		 */
		@Override
		protected Dialog doCreateDialog() {
			// Add the supportEClassifierList as resource for this eClassifier dialog
			Shell parent = ExtendedPlatformUI.getActiveShell();
			EClassifierBrowseDialog dialog = new EClassifierBrowseDialog(parent, supportedEClassifierList);

			dialog.setTitle(Messages.title_eclassifierBrowseDialog);
			dialog.setMessage(Messages.FilteredItemsSelectionDialog_pattern);

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
			EClassifier result = ((EClassifierBrowseDialog) dialog).getEClassifierResult();
			storeBrowserSelectionEClassifier((ComboButtonField) field, result);
		}
	}

	/**
	 * Creates a new instance of model creation wizard page. The supported available metamodel descriptors and EPackages
	 * are initialized. The selected results of metamodel, ePackage and eClassifier are passed to the post pages, e.g.,
	 * NewModelFileCreationPage, using {@linkplain NewModelFileProperties model file properties}.
	 * 
	 * @param pageName
	 *            the name of the page
	 * @param selection
	 *            the current resource {@linkplain ISelection selection}
	 * @param newModelFileProperties
	 *            the {@linkplain NewModelFileProperties selected newModelFileProperties} (metamodel, ePackage and
	 *            eClassifier) to be used by the post pages for the creation of model file
	 */
	public NewModelCreationPage(String pageName, ISelection selection, NewModelFileProperties newModelFileProperties) {
		super(pageName);
		setTitle(Messages.title_newModelCreationPage);
		setDescription(Messages.description_newModelCreationPage);

		this.selection = selection;
		this.newModelFileProperties = newModelFileProperties;

		// Initialize supported meta-models
		supportedMMDescriptorList = getSupportedMetaModelDescriptors();

		// Initialize supported EPackages
		for (int index = 0; index < supportedMMDescriptorList.size(); index++) {
			Collection<EPackage> ePkgs = supportedMMDescriptorList.get(index).getEPackages();
			supportedEPackageList.addAll(ePkgs);
		}
	}

	/**
	 * Gets the supported metamodel descriptors, all the metamodel descriptors that are registed by default. This method
	 * can be overridden by clients to provide specific suported metamodel descriptors.
	 */
	protected List<IMetaModelDescriptor> getSupportedMetaModelDescriptors() {
		return MetaModelDescriptorRegistry.INSTANCE.getDescriptors(MetaModelDescriptorRegistry.ANY_MM);
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
		createMMComboButtonField(container);
		// Create a ComboButtonField for EPackage
		createEPackageComboButtonField(container);
		// Create a ComboButtonField for EClassifier
		createEClassifierComboButtonField(container);

		// Add listeners
		mmCombo.addFieldListener(new fieldListener());
		fillSupportedMMs();
		ePackageCombo.addFieldListener(new fieldListener());
		eClassifierCombo.addFieldListener(new fieldListener());

		setControl(container);
		setPageComplete(validatePage());
	}

	/**
	 * Creates a ComboButtonField for the choice of meta-model.
	 */
	public void createMMComboButtonField(Composite container) {
		MMButtonAdapter mmAdapter = new MMButtonAdapter();
		mmCombo = new ComboButtonField(mmAdapter);
		mmCombo.setLabelText(Messages.label_metaModelComboButtonField);
		mmCombo.setButtonLabel(Messages.label_browseButton);
		mmCombo.fillIntoGrid(container, 3);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		mmCombo.getComboControl().setLayoutData(gridData);
	}

	/**
	 * Creates a ComboButtonField for the choice of EPackage.
	 */
	public void createEPackageComboButtonField(Composite container) {
		EPkgButtonAdapter pkgAdapter = new EPkgButtonAdapter();
		ePackageCombo = new ComboButtonField(pkgAdapter);
		ePackageCombo.setLabelText(Messages.label_epackageComboButtonField);
		ePackageCombo.setButtonLabel(Messages.label_browseButton);
		ePackageCombo.fillIntoGrid(container, 3);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		ePackageCombo.getComboControl().setLayoutData(gridData);
	}

	/**
	 * Creates a ComboButtonField for the choice of EClassifier.
	 */
	public void createEClassifierComboButtonField(Composite container) {
		EClassifierButtonAdapter classifierAdapter = new EClassifierButtonAdapter();
		eClassifierCombo = new ComboButtonField(classifierAdapter);
		eClassifierCombo.setLabelText(Messages.label_eclassifierComboButtonField);
		eClassifierCombo.setButtonLabel(Messages.label_browseButton);
		eClassifierCombo.fillIntoGrid(container, 3);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		eClassifierCombo.getComboControl().setLayoutData(gridData);
	}

	/**
	 * Sets the supported meta-model descriptors as the resource items in the meta-model combo field.
	 */
	protected void fillSupportedMMs() {

		String[] items = new String[supportedMMDescriptorList.size()];
		for (int index = 0; index < supportedMMDescriptorList.size(); index++) {
			items[index] = supportedMMDescriptorList.get(index).getName();
		}
		mmCombo.setItems(items);
	}

	/**
	 * Sets the supported EPackages as the resource items in the EPackage combo field.
	 */
	private void fillSupportedEPackages() {

		String[] items = new String[supportedEPackageList.size()];
		for (int index = 0; index < supportedEPackageList.size(); index++) {
			items[index] = supportedEPackageList.get(index).getName();
		}
		ePackageCombo.setItems(items);

	}

	/**
	 * Set the supported EClassifiers as the resource items in the EClassifier combo field.
	 */
	private void fillSupportedEClassifiers() {
		String[] items = new String[supportedEClassifierList.size()];
		for (int index = 0; index < supportedEClassifierList.size(); index++) {
			items[index] = supportedEClassifierList.get(index).getName();
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
			if (field == mmCombo) {
				storeSelectionMM((ComboButtonField) field);
				setSupportedEPackages();
				fillSupportedEPackages();
			} else if (field == ePackageCombo) {
				storeSelectionEPackage((ComboButtonField) field);
				setSupportedEClassifiers();
				fillSupportedEClassifiers();
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
	 * @param mmDescriptor
	 *            the {@linkplain IMetaModelDescriptor metamodel descriptor } result selected by the metamodel browse
	 *            dialog
	 */
	private void storeBrowserSelectionMM(ComboButtonField field, IMetaModelDescriptor mmDescriptor) {
		// Store the selected meta-model
		Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_KEY, mmDescriptor.getIdentifier());

		// Set the selected meta-model of elements
		newModelFileProperties.setMetaModelDescriptor(mmDescriptor);

		// Set the selected meta-model as the selection item in the combo field
		int indexMM = supportedMMDescriptorList.indexOf(mmDescriptor);
		Combo control = (Combo) field.getComboControl();
		control.select(indexMM);
		setPageComplete(validatePage());
	}

	/**
	 * Stores the EPackage selected by the epackage browse dialog, and sets the value as the selected item in the combo
	 * field.
	 * 
	 * @param field
	 *            the {@linkplain ComboButtonField field } of EPackage
	 * @param result
	 *            the {@linkplain EPackage epackage} result selected by the browser
	 */
	private void storeBrowserSelectionEPkg(ComboButtonField field, EPackage epackage) {
		// Store the selected EPackage
		Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_EPACKAGE_KEY, epackage.getName());

		// Set the value of selected EPackage of elements
		newModelFileProperties.setRootObjectEPackage(epackage);

		// Set the selected EPackage as the selection item in the combo field
		int indexEPkg = supportedEPackageList.indexOf(epackage);
		Combo control = (Combo) field.getComboControl();
		control.select(indexEPkg);
		setPageComplete(validatePage());
	}

	/**
	 * Stores the EClassifier selected by the eclassifier browse dialog, and sets the value as the selected item in the
	 * combo field.
	 * 
	 * @param field
	 *            the {@link ComboButtonField field} of EClassifier
	 * @param result
	 *            the {@link EClassifier eclassifier} result selected by the eclassifier browse dialog
	 */
	private void storeBrowserSelectionEClassifier(ComboButtonField field, EClassifier eclassifier) {
		// Store the selected EPackage
		Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_ECLASSIFIER_KEY, eclassifier.getName());

		// Set the value of selected EClassifier of elements
		newModelFileProperties.setRootObjectEClassifier(eclassifier);

		// Set the selected EClassifier as the selection item in the combo field
		int indexEClassifier = supportedEClassifierList.indexOf(eclassifier);
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
	private void storeSelectionMM(ComboButtonField field) {
		int index = field.getSelectionIndex();
		if (index > -1) {

			// Get the selected meta-model descriptor identifier
			String descriptor = supportedMMDescriptorList.get(index).getIdentifier();
			if (descriptor != null) {
				// Store the selected meta-model
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_METAMODEL_KEY, descriptor);

				// Set the selected meta-model value in the elements
				newModelFileProperties.setMetaModelDescriptor(supportedMMDescriptorList.get(index));
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
			EPackage epkg = supportedEPackageList.get(index);
			String pkg = epkg.getName();
			if (pkg != null) {
				// Store the selected EPackage
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_EPACKAGE_KEY, pkg);

				// Set the selected EPackage value in the elements
				newModelFileProperties.setRootObjectEPackage(supportedEPackageList.get(index));
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
			String eclassifier = supportedEClassifierList.get(index).getName();
			if (eclassifier != null) {
				// Store the selected EClassifier
				Activator.getPlugin().getDialogSettings().put(LAST_SELECTED_ECLASSIFIER_KEY, eclassifier);

				// Set the selected EClassifier value in the elements
				newModelFileProperties.setRootObjectEClassifier(supportedEClassifierList.get(index));
				setPageComplete(validatePage());
			}
		}
	}

	/**
	 * Sets supported EPackages depending on the selected meta-model descriptor
	 */
	private void setSupportedEPackages() {
		IMetaModelDescriptor mmDescriptor = newModelFileProperties.getMetaModelDescriptor();
		if (mmDescriptor != null) {
			supportedEPackageList.clear();
			supportedEPackageList.addAll(mmDescriptor.getEPackages());
		}

	}

	/**
	 * Sets supported EClassifiers depending on the selected EPackage value
	 */
	private void setSupportedEClassifiers() {
		EPackage epkg = newModelFileProperties.getRootObjectEPackage();
		if (epkg != null) {
			supportedEClassifierList.clear();
			supportedEClassifierList.addAll(epkg.getEClassifiers());
		}

	}

	/**
	 * The framework calls this to validate if all the field are selected.
	 */
	protected boolean validatePage() {
		if (newModelFileProperties.getMetaModelDescriptor() == null) {
			setErrorMessage(Messages.error_emptySelectedMM);
			return false;
		}

		if (newModelFileProperties.getRootObjectEPackage() == null) {
			setErrorMessage(Messages.error_emptySelectedEPackage);
			return false;
		}

		if (newModelFileProperties.getRootObjectEClassifier() == null) {
			setErrorMessage(Messages.error_emptySelectedEClassifier);
			return false;
		}

		setErrorMessage(null);
		return true;
	}
}
