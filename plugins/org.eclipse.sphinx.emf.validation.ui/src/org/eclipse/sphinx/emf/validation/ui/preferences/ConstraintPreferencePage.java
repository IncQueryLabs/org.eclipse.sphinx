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
package org.eclipse.sphinx.emf.validation.ui.preferences;

import org.eclipse.emf.validation.ui.preferences.ConstraintsSelectionBlock;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class ConstraintPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	protected ConstraintsSelectionBlock constraintsComposite;

	@Override
	protected Control createContents(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		result.setLayout(layout);

		constraintsComposite = new ConstraintsSelectionBlock();

		constraintsComposite.createComposite(result);

		applyDialogFont(result);

		return result;
	}

	@Override
	protected void performDefaults() {
		constraintsComposite.performDefaults();
	}

	public void init(IWorkbench workbench) {
		/* Does nothing. */
	}

	@Override
	public boolean performOk() {
		return constraintsComposite.performOk();
	}

}