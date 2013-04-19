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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewInitialModelCreationPage;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage;

/**
 * The properties of the initial model for a new model file, i.e., the IMetaModelDescriptor and the EPackage and the
 * EClassifier of the root object selected by the user. This class is used to share the property values between the
 * wizard pages and wizards, e.g., {@link NewInitialModelCreationPage} sets the property values, and
 * {@link NewModelFileCreationPage} as well as {@link AbstractNewModelFileWizard} use the selected property values to
 * create the initial model and the new model file.
 */
public class NewModelFileProperties<T extends IMetaModelDescriptor> {

	private T mmDescriptor;
	private EPackage rootObjectEPackage;
	private EClassifier rootObjectEClassifier;

	public NewModelFileProperties() {
	}

	public NewModelFileProperties(T mmDescriptor) {
		this.mmDescriptor = mmDescriptor;
	}

	public T getMetaModelDescriptor() {
		return mmDescriptor;
	}

	public void setMetaModelDescriptor(T mmDescriptor) {
		this.mmDescriptor = mmDescriptor;
	}

	public EPackage getRootObjectEPackage() {
		return rootObjectEPackage;
	}

	public void setRootObjectEPackage(EPackage rootObjectEPackage) {
		this.rootObjectEPackage = rootObjectEPackage;
	}

	public EClassifier getRootObjectEClassifier() {
		return rootObjectEClassifier;
	}

	public void setRootObjectEClassifier(EClassifier rootObjectEClassifier) {
		this.rootObjectEClassifier = rootObjectEClassifier;
	}
}
