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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelFileJob;
import org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelFileWizard;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.InitialModelCreationPage;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.InitialModelProperties;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage;
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

	protected InitialModelProperties<HummingbirdMMDescriptor> initialModelProperties = new InitialModelProperties<HummingbirdMMDescriptor>();

	protected InitialModelCreationPage<HummingbirdMMDescriptor> initialModelCreationPage;

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelFileWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(Messages.wizard_newHummingbirdFile_title);
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelFileWizard#addPages()
	 */
	@Override
	public void addPages() {
		initialModelProperties = new InitialModelProperties<HummingbirdMMDescriptor>();

		// Create and add initial model creation page
		initialModelCreationPage = new InitialModelCreationPage<HummingbirdMMDescriptor>("InitialHummingbirdModelCreationPage", selection, //$NON-NLS-1$
				initialModelProperties, HummingbirdMMDescriptor.INSTANCE);
		initialModelCreationPage.setTitle(Messages.page_newInitialHummingbirdModelCreation_title);
		initialModelCreationPage.setDescription(Messages.page_newInitialHummingbirdModelCreation_description);
		addPage(initialModelCreationPage);

		super.addPages();
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelFileWizard#createMainPage()
	 */
	@Override
	protected NewModelFileCreationPage<HummingbirdMMDescriptor> createMainPage() {
		return new NewHummingbirdFileCreationPage("NewHummingbirdFileCreationPage", selection, initialModelProperties); //$NON-NLS-1$
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelFileWizard#createCreateNewModelFileJob(org.eclipse
	 * .core.resources.IFile)
	 */
	@Override
	protected Job createCreateNewModelFileJob(IFile newFile) {
		return new CreateNewModelFileJob(Messages.job_createNewHummingbirdFile_name, newFile, initialModelProperties.getMetaModelDescriptor(),
				initialModelProperties.getRootObjectEPackage(), initialModelProperties.getRootObjectEClassifier());
	}
}
