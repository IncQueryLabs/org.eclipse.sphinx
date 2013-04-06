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
 *     itemis - [403693] NewModelProjectCreationPage#createMetaModelVersionGroup() should not return the group object being created
 *     itemis - [403728] NewModelProjectCreationPage and NewModelFileCreationPage should provided hooks for creating additional controls
 *     itemis - [405059] Enable BasicMetaModelVersionGroup to open appropriate model version preference page
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.ui.wizards.groups.BasicMetaModelVersionGroup;
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Basic main page for a wizard that creates a {@linkplain IProject project} containing model files.
 * <p>
 * This page may be used by clients as it is; it may also be subclassed to suit. Subclasses may override these methods
 * if required:
 * <p>
 * <code>createControl</code> method to create the specific controls for this project creation page.
 * <code>createMetaModelVersionGroup</code> - method to create the specific metamodel version group for this project
 * creation page
 */
public class NewModelProjectCreationPage extends WizardNewProjectCreationPage {

	protected IMetaModelDescriptor baseMetaModelDescriptor;
	protected IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference;
	protected String metaModelVersionPreferencePageId;

	protected BasicMetaModelVersionGroup metaModelVersionGroup;

	/**
	 * Creates a new instance of the new model project creation wizard page for the specified base metamodel.
	 * 
	 * @param pageName
	 *            the name of this page
	 * @param baseMetaModelDescriptor
	 *            the base {@linkplain IMetaModelDescriptor meta-model} of the model project to be created
	 * @param metaModelVersionPreference
	 *            the metamodel version {@linkplain IProjectWorkspacePreference preference} object
	 * @param metaModelVersionPreferencePageId
	 *            the metamodel version preference page id
	 */
	public NewModelProjectCreationPage(String pageName, IMetaModelDescriptor baseMetaModelDescriptor,
			IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference, String metaModelVersionPreferencePageId) {
		super(pageName);
		this.metaModelVersionPreference = metaModelVersionPreference;
		this.baseMetaModelDescriptor = baseMetaModelDescriptor;
		this.metaModelVersionPreferencePageId = metaModelVersionPreferencePageId;
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		createAdditionalControls((Composite) getControl());
	}

	/**
	 * Creates controls for specific project creation options to be placed behind those for project name and location
	 * (which are created by {@link WizardNewProjectCreationPage#createControl(Composite)}).
	 * <p>
	 * This implementation creates a {@link BasicMetaModelVersionGroup group} for selecting the metamodel version to be
	 * assigned to the new project.
	 * </p>
	 * This method may be overridden by subclasses to provide enhanced or custom implementations.
	 * 
	 * @param parent
	 *            the parent composite
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(Composite)
	 */
	protected void createAdditionalControls(Composite parent) {
		createMetaModelVersionGroup(parent, baseMetaModelDescriptor, metaModelVersionPreference, metaModelVersionPreferencePageId);

		Dialog.applyDialogFont(getControl());
	}

	/**
	 * Creates a {@link BasicMetaModelVersionGroup metamodel version group} enabling the metamodel version of the model
	 * {@link IProject project} under creation to be chosen.
	 * 
	 * @param parent
	 *            the parent {@linkplain Composite composite}
	 * @param baseMetaModelDescriptor
	 *            the base {@linkplain IMetaModelDescriptor meta-model} of the model project to be created
	 * @param metaModelVersionPreference
	 *            the metamodel version {@linkplain IProjectWorkspacePreference preference} object
	 * @param metaModelVersionPreferencePageId
	 *            the metamodel version preference page id
	 */
	protected void createMetaModelVersionGroup(Composite parent, IMetaModelDescriptor baseMetaModelDescriptor,
			IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference, String metaModelVersionPreferencePageId) {
		metaModelVersionGroup = new BasicMetaModelVersionGroup(parent, baseMetaModelDescriptor, metaModelVersionPreference,
				metaModelVersionPreferencePageId);
	}

	public IMetaModelDescriptor getMetaModelVersionDescriptor() {
		return metaModelVersionGroup.getMetaModelVersionDescriptor();
	}
}
