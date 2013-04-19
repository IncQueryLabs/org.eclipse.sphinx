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
package org.eclipse.sphinx.emf.workspace.ui.wizards;

import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelFileJob;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewInitialModelCreationPage;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage;

/**
 * Generic wizard that creates a new model file in the workspace. Two pages are added for selecting the properties of
 * the initial model and the model file to be created. When being finished a {@linkplain CreateNewModelFileJob new model
 * file job} is run to create and save the new model file in the workspace.
 */
public class GenericNewModelFileWizard extends AbstractNewModelFileWizard<IMetaModelDescriptor> {

	/*
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		newModelFileProperties = new NewModelFileProperties<IMetaModelDescriptor>();

		// Create a page for users to choose the meta-model, EPackage and EClassifier of the model to be created
		newInitialModelCreationPage = new NewInitialModelCreationPage<IMetaModelDescriptor>(
				"NewModel", selection, newModelFileProperties, MetaModelDescriptorRegistry.ANY_MM); //$NON-NLS-1$
		addPage(newInitialModelCreationPage);

		// Create a model file creation page
		newModelFileCreationPage = new NewModelFileCreationPage<IMetaModelDescriptor>("NewModelFile", selection, null, newModelFileProperties); //$NON-NLS-1$
		addPage(newModelFileCreationPage);
	}
}
