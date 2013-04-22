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

	public BasicNewModelProjectWizard(boolean createWorkingSetGroup, T baseMetaModelDescriptor,
			IProjectWorkspacePreference<T> metaModelVersionPreference, String metaModelVersionPreferencePageId) {
		super(createWorkingSetGroup, baseMetaModelDescriptor, metaModelVersionPreference, metaModelVersionPreferencePageId);
	}

	/**
	 * @deprecated Use
	 *             {@link #BasicNewModelProjectWizard(boolean, IMetaModelDescriptor, IProjectWorkspacePreference, String)
	 *             )} instead.
	 */
	@Deprecated
	public BasicNewModelProjectWizard(String projectNatureId, IProjectWorkspacePreference<T> metaModelVersionPreference) {
		this(false, (T) null, metaModelVersionPreference, null);
	}

	/**
	 * @deprecated Use
	 *             {@link #BasicNewModelProjectWizard(boolean, IMetaModelDescriptor, IProjectWorkspacePreference, String)
	 *             )} instead.
	 */
	@Deprecated
	public BasicNewModelProjectWizard(T metaModelVersionDescriptor, String projectNatureId, IProjectWorkspacePreference<T> metaModelVersionPreference) {
		this(false, (T) null, metaModelVersionPreference, null);
	}

	@Override
	protected WizardNewProjectCreationPage createMainPage() {
		return null;
	}
}
