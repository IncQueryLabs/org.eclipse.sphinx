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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelFileJob;
import org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelFileWizard;
import org.eclipse.sphinx.emf.workspace.ui.wizards.NewModelFileProperties;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewInitialModelCreationPage;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.messages.Messages;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.wizards.pages.NewHummingbirdFileCreationPage;
import org.eclipse.ui.IWorkbench;

/**
 * Generic wizard that creates a new Hummingbird file in the workspace. Two pages are added for selecting the properties
 * of the initial Hummingbird model and the Hummingbird file to be created. When being finished a
 * {@linkplain CreateNewModelFileJob new model file job} is run to create and save the new Hummingbird file in the
 * workspace.
 */
public class NewHummingbirdFileWizard extends AbstractNewModelFileWizard<HummingbirdMMDescriptor> {

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.BasicNewModelFileWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(Messages.wizard_newHummingbirdFile_title);
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.BasicNewModelFileWizard#addPages()
	 */
	@Override
	public void addPages() {
		newModelFileProperties = new NewModelFileProperties<HummingbirdMMDescriptor>();

		// Create and add model instance creation page
		newInitialModelCreationPage = new NewInitialModelCreationPage<HummingbirdMMDescriptor>("NewInitialHummingbirdModelCreationPage", selection, //$NON-NLS-1$
				newModelFileProperties, HummingbirdMMDescriptor.INSTANCE);
		newInitialModelCreationPage.setTitle(Messages.page_newInitialHummingbirdModelCreation_title);
		newInitialModelCreationPage.setDescription(Messages.page_newInitialHummingbirdModelCreation_description);
		addPage(newInitialModelCreationPage);

		// Create and add model file creation page
		newModelFileCreationPage = new NewHummingbirdFileCreationPage("NewHummingbirdFileCreationPage", selection, newModelFileProperties); //$NON-NLS-1$
		addPage(newModelFileCreationPage);
	}

	/**
	 * Creates a new instance of {@linkplain CreateNewModelFileJob}.
	 * 
	 * @param newFile
	 *            the {@linkplain IFile model file} to be created
	 * @return a new instance of job that creates a new model file. This job is a unit of runnable work that can be
	 *         scheduled to be run with the job manager.
	 */
	@Override
	protected Job createCreateNewModelFileJob(IFile newFile) {
		Assert.isNotNull(newModelFileProperties);

		return new CreateNewModelFileJob(Messages.job_createNewHummingbirdFile_name, newFile, newModelFileProperties.getMetaModelDescriptor(),
				newModelFileProperties.getRootObjectEPackage(), newModelFileProperties.getRootObjectEClassifier());
	}
}
