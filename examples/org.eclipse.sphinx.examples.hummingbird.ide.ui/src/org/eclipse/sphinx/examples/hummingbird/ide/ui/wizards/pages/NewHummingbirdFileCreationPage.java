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
package org.eclipse.sphinx.examples.hummingbird.ide.ui.wizards.pages;

import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.InitialModelProperties;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;
import org.eclipse.sphinx.examples.hummingbird.ide.preferences.IHummingbirdPreferences;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.messages.Messages;

/**
 * A main page for a wizard that creates a Hummingbird file resource. The new model file is to be created based on the
 * given {@linkplain InitialModelProperties new model file properties} (metamodel, ePackage and eClassifier).
 */
public class NewHummingbirdFileCreationPage extends NewModelFileCreationPage<HummingbirdMMDescriptor> {

	/**
	 * Creates a new instance of new Hummingbird file creation wizard page.
	 * 
	 * @param pageId
	 *            the name of the page
	 * @param selection
	 *            the current resource selection
	 * @param initialModelProperties
	 *            the chosen {@linkplain InitialModelProperties initial model properties} (metamodel, EPackage and
	 *            EClassifier) to be used as basis for creating the initial model of the new model file
	 */
	public NewHummingbirdFileCreationPage(String pageId, IStructuredSelection selection,
			InitialModelProperties<HummingbirdMMDescriptor> initialModelProperties) {
		super(pageId, selection, IHummingbirdPreferences.METAMODEL_VERSION, initialModelProperties);
		setTitle(Messages.page_newHummingbirdFileCreation_title);
		setDescription(Messages.page_newHummingbirdFileCreation_description);
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage#getDefaultNewFileExtension()
	 */
	@Override
	public String getDefaultNewFileExtension() {
		EPackage rootObjectEPackage = initialModelProperties.getRootObjectEPackage();
		if (rootObjectEPackage != null) {
			String packageName = rootObjectEPackage.getName();
			Collection<String> validFileExtensions = getValidFileExtensions();
			if (validFileExtensions.contains(packageName)) {
				return rootObjectEPackage.getName();
			}
		}

		return super.getDefaultNewFileExtension();
	}
}
