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
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird.ide.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelProjectJob;
import org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelProjectWizard;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelProjectCreationPage;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;
import org.eclipse.sphinx.examples.hummingbird.ide.preferences.IHummingbirdPreferences;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.messages.Messages;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.preferences.IHummingbirdPreferencesUI;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Basic wizard that creates a new Hummingbird project resource in the workspace.
 */
public class NewHummingbirdProjectWizard extends AbstractNewModelProjectWizard<HummingbirdMMDescriptor> {

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelProjectWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle(Messages.wizard_newHummingbirdProject_title);
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelProjectWizard#createMainPage(boolean)
	 */
	@Override
	protected WizardNewProjectCreationPage createMainPage() {
		NewModelProjectCreationPage<HummingbirdMMDescriptor> newModelProjectCreationPage = new NewModelProjectCreationPage<HummingbirdMMDescriptor>(
				"NewHummingbirdProjectCreationPage", getSelection(), true, HummingbirdMMDescriptor.INSTANCE, IHummingbirdPreferences.METAMODEL_VERSION, //$NON-NLS-1$
				IHummingbirdPreferencesUI.HUMMINGBIRD_METAMODEL_VERSION_PREFERENCE_PAGE_ID);

		newModelProjectCreationPage.setTitle(Messages.page_newHummingbirdProjectCreation_title);
		newModelProjectCreationPage.setDescription(Messages.page_newHummingbirdProjectCreation_description);

		return newModelProjectCreationPage;
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelProjectWizard#createCreateNewProjectJob(java.lang
	 * .String , org.eclipse.core.resources.IProject, java.net.URI)
	 */
	@Override
	protected CreateNewModelProjectJob<HummingbirdMMDescriptor> createCreateNewModelProjectJob(IProject project, URI location) {
		@SuppressWarnings("unchecked")
		NewModelProjectCreationPage<HummingbirdMMDescriptor> newModelProjectCreationPage = (NewModelProjectCreationPage<HummingbirdMMDescriptor>) mainPage;

		return new CreateNewModelProjectJob<HummingbirdMMDescriptor>(Messages.job_createNewHummingbirdProject_name, project, location,
				newModelProjectCreationPage.getMetaModelVersionDescriptor(), IHummingbirdPreferences.METAMODEL_VERSION);
	}
}
