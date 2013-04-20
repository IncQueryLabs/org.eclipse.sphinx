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
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @deprecated Use {@link AbstractNewModelProjectWizard} or {@link NewSimpleModelProjectWizard} instead.
 */
@Deprecated
public class BasicNewModelProjectWizard<T extends IMetaModelDescriptor> extends AbstractNewModelProjectWizard<T> {

	public BasicNewModelProjectWizard() {
	}

	public BasicNewModelProjectWizard(T metaModelVersionDescriptor, IProjectWorkspacePreference<T> metaModelVersionPreference) {
		super(metaModelVersionDescriptor, metaModelVersionPreference);
	}

	/**
	 * @deprecated Use {@link #BasicNewModelProjectWizard(IMetaModelDescriptor, IProjectWorkspacePreference)} instead.
	 */
	@Deprecated
	public BasicNewModelProjectWizard(String projectNatureId, IProjectWorkspacePreference<T> metaModelVersionPreference) {
		this((T) null, metaModelVersionPreference);
	}

	/**
	 * @deprecated Use {@link #BasicNewModelProjectWizard(IMetaModelDescriptor, IProjectWorkspacePreference)} instead.
	 */
	@Deprecated
	public BasicNewModelProjectWizard(T metaModelVersionDescriptor, String projectNatureId, IProjectWorkspacePreference<T> metaModelVersionPreference) {
		this(metaModelVersionDescriptor, metaModelVersionPreference);
	}

	@Override
	protected WizardNewProjectCreationPage createMainPage() {
		return null;
	}
}
