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
import org.eclipse.sphinx.emf.workspace.ui.wizards.NewModelFileProperties;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;
import org.eclipse.sphinx.examples.hummingbird.ide.preferences.IHummingbirdPreferences;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.internal.messages.Messages;

/**
 * A main page for a wizard that creates a Hummingbird file resource. The new model file is to be created based on the
 * given {@linkplain NewModelFileProperties new model file properties} (metamodel, ePackage and eClassifier).
 */
public class NewHummingbirdFileCreationPage extends NewModelFileCreationPage<HummingbirdMMDescriptor> {

	/**
	 * Creates a new instance of new Hummingbird file creation wizard page.
	 * 
	 * @param pageId
	 *            the name of the page
	 * @param selection
	 *            the current resource selection
	 * @param properties
	 *            the {@linkplain NewModelFileProperties new model file properties} selected by previous page or by
	 *            initial setting
	 */
	public NewHummingbirdFileCreationPage(String pageId, IStructuredSelection selection, NewModelFileProperties<HummingbirdMMDescriptor> properties) {
		super(pageId, selection, IHummingbirdPreferences.METAMODEL_VERSION, properties);
		setTitle(Messages.page_newHummingbirdFileCreation_title);
		setDescription(Messages.page_newHummingbirdFileCreation_description);
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage#getDefaultNewFileExtension()
	 */
	@Override
	public String getDefaultNewFileExtension() {
		EPackage rootObjectEPackage = newModelFileProperties.getRootObjectEPackage();
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
