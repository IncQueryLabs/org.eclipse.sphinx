/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.emf.validation.ui.actions.contributor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sphinx.emf.validation.preferences.IValidationPreferences;
import org.eclipse.sphinx.emf.validation.ui.Activator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AutomaticValidationActionContributor implements IWorkbenchWindowActionDelegate, IPropertyChangeListener {

	private IAction me = null;

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public void init(IWorkbenchWindow window) {
		preferenceStore.addPropertyChangeListener(this);
	}

	public void dispose() {
		preferenceStore.removePropertyChangeListener(this);
	}

	public void run(IAction action) {
		preferenceStore.setValue(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION, action.isChecked());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Init action part
		if (me == null) {
			me = action;
			me.setChecked(preferenceStore.getBoolean(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION));
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (me != null) {
			if (event.getProperty().equals(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION)) {
				me.setChecked(preferenceStore.getBoolean(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION));
			}
		}
	}
}
