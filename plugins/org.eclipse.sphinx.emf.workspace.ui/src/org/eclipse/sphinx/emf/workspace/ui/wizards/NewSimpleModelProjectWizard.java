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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * A wizard that creates a new simple model project in the workspace. Two pages are added for selecting the properties
 * of the model project and references to other projects. When being finished a
 * {@linkplain createCreateNewModelProjectJob new model project job} is run to create the model project in the
 * workspace.
 */
public class NewSimpleModelProjectWizard extends AbstractNewModelProjectWizard<IMetaModelDescriptor> {

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.AbstractNewModelProjectWizard#createMainPage(boolean)
	 */
	@Override
	protected WizardNewProjectCreationPage createMainPage() {
		WizardNewProjectCreationPage mainPage = new WizardNewProjectCreationPage("WizardNewProjectCreationPage") { //$NON-NLS-1$
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org.eclipse.swt.widgets.Composite)
			 */
			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup((Composite) getControl(), getSelection(), new String[] { "org.eclipse.ui.resourceWorkingSetPage" }); //$NON-NLS-1$
				Dialog.applyDialogFont(getControl());
			}
		};
		mainPage.setTitle(Messages.page_newModelProjectCreation_title);
		mainPage.setDescription(Messages.page_newModelProjectCreation_description);
		return mainPage;
	}
}
