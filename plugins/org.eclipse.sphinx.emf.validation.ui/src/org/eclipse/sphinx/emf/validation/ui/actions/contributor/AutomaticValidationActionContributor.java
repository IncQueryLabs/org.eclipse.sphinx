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

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sphinx.emf.validation.preferences.IValidationPreferences;
import org.eclipse.sphinx.emf.validation.ui.Activator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AutomaticValidationActionContributor implements IWorkbenchWindowActionDelegate, IPropertyChangeListener,
		Preferences.IPropertyChangeListener {

	private IAction me = null;

	private org.eclipse.sphinx.emf.validation.Activator coreActivator = org.eclipse.sphinx.emf.validation.Activator.getDefault();

	public void dispose() {
		// nothing to dispose
	}

	public void init(IWorkbenchWindow window) {
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		coreActivator.getPluginPreferences().addPropertyChangeListener(this);
	}

	public void run(IAction action) {
		coreActivator.getPluginPreferences().setValue(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION, action.isChecked());
		coreActivator.savePluginPreferences();
	}

	public void selectionChanged(IAction action, ISelection selection) {

		// Init action part
		if (me == null) {
			me = action;
			me.setChecked(coreActivator.getPluginPreferences().getBoolean(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION));
		}
	}

	public void propertyChange(PropertyChangeEvent event) {

		boolean oldValue = false;
		boolean newValue = false;

		try {
			oldValue = preftoBool(event.getOldValue());
			newValue = preftoBool(event.getNewValue());
		} catch (Exception ex) {
		}

		if (me != null) {
			me.setChecked(newValue);
		}

	}

	public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {

		if (me != null) {
			me.setChecked(coreActivator.getPluginPreferences().getBoolean(IValidationPreferences.PREF_ENABLE_AUTOMATIC_VALIDATION));
		}

	}

	/**
	 * Boolean pref can be a String or a Boolean depending of trapped the action on preference page
	 * 
	 * @param o
	 *            a Java Object
	 * @return translation to a boolean
	 */
	private boolean preftoBool(Object o) throws Exception {
		boolean res;

		if (o instanceof Boolean) {
			res = ((Boolean) o).booleanValue();
		} else if (o instanceof String) {
			if (((String) o).compareTo("true") == 0) { //$NON-NLS-1$
				res = true;
			} else if (((String) o).compareTo("false") == 0) { //$NON-NLS-1$
				res = false;
			} else {
				throw new Exception();
			}
		} else {
			throw new Exception();
		}

		return res;
	}

}
