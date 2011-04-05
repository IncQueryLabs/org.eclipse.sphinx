/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xpand.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.sphinx.platform.ui.preferences.AbstractPreferenceAndPropertyPage;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.groups.OutletsGroup;
import org.eclipse.sphinx.xpand.ui.outlet.providers.OutletProvider;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class XpandPreferencePage extends AbstractPreferenceAndPropertyPage {

	public static final String PROP_PAGE_ID = "propertyPages.xpand"; //$NON-NLS-1$

	private OutletsGroup outletsGroup;

	private TableViewer tableViewer;

	private OutletProvider outletProvider;

	public XpandPreferencePage() {
		super(OutletsPreference.XTEND_XPAND_NATURE_ID, GRID);
	}

	public XpandPreferencePage(String requiredProjectNatureId, int style) {
		super(requiredProjectNatureId, style);
	}

	protected OutletsPreference getOutletsPreference() {
		return OutletsPreference.INSTANCE;
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		if (isProjectPreferencePage()) {
			ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(new ProjectScope((IProject) getElement()), getOutletsPreference()
					.getQualifier());
			return scopedPreferenceStore;
		}
		return null;
	}

	@Override
	protected void addFields(Composite parent) {
		addOutletsGroup(parent);
	}

	protected void addOutletsGroup(Composite parent) {
		outletProvider = new OutletProvider((IProject) getElement(), getOutletsPreference());
		outletsGroup = new OutletsGroup("Outlets", outletProvider);
		outletsGroup.createContent(parent, 2);
		tableViewer = outletsGroup.getTableViewer();
		Dialog.applyDialogFont(parent);
	}

	@Override
	protected void enablePreferenceContent(boolean useProjectSpecificSettings) {

	}

	@Override
	protected String getPreferencePageID() {
		return null;
	}

	@Override
	protected String getPropertyPageID() {
		return PROP_PAGE_ID;
	}

	@Override
	protected void performDefaults() {
		outletProvider.setToDefault();
		tableViewer.refresh();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		outletProvider.store();
		return super.performOk();
	}

	@Override
	public void dispose() {
		outletProvider.dispose();
		super.dispose();
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		if (((GridLayout) getFieldEditorParent().getLayout()).numColumns == 0) {
			((GridLayout) getFieldEditorParent().getLayout()).numColumns = 1;
		}
	}
}
