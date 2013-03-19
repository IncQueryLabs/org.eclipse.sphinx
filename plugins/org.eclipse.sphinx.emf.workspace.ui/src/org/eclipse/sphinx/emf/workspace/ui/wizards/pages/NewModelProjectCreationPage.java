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
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
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

	protected BasicMetaModelVersionGroup metaModelVersionGroup;
	protected IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> metaModelVersionPreference;
	protected IMetaModelDescriptor metaModelDescriptor;

	/**
	 * Creates a new instance of {@linkplain IProject project} creation wizard page with given page name, metamodel
	 * version preference and metamodel descriptor.
	 * 
	 * @param pageName
	 *            the name of this page
	 * @param metaModelVersionPreference
	 *            the required metamodel version preference
	 * @param metaModelDescriptor
	 *            the required metamodel descriptor
	 */
	public NewModelProjectCreationPage(String pageName,
			IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> metaModelVersionPreference, IMetaModelDescriptor metaModelDescriptor) {
		super(pageName);
		this.metaModelVersionPreference = metaModelVersionPreference;
		this.metaModelDescriptor = metaModelDescriptor;
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
		createMetaModelVersionGroup(parent, metaModelVersionPreference, metaModelDescriptor);

		Dialog.applyDialogFont(getControl());
	}

	/**
	 * Creates a {@link BasicMetaModelVersionGroup} release group which composes of fields for the default metamodel
	 * version, alternate metamodel versions, etc.. This method may be overridden by subclasses to provide specific
	 * metamodel version group.
	 * 
	 * @param parent
	 *            the {@linkplain Composite parent}
	 * @param metaModelVersionPreference
	 *            the {@linkplain IProjectWorkspacePreference projectWorkspacePreference} to be created in this project
	 * @param mmDescriptor
	 *            the {@linkplain IMetaModelDescriptor meta-model descriptor} to be created in this project
	 */
	public void createMetaModelVersionGroup(Composite parent,
			IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> metaModelVersionPreference, IMetaModelDescriptor mmDescriptor) {
		metaModelVersionGroup = new BasicMetaModelVersionGroup(parent, metaModelVersionPreference, mmDescriptor);
	}

	public IMetaModelDescriptor getMetaModelVersionDescriptor() {
		return metaModelVersionGroup.getMetaModelVersionDescriptor();
	}

	public void saveDialogSettings() {
		metaModelVersionGroup.saveDialogSettings();
	}
}
