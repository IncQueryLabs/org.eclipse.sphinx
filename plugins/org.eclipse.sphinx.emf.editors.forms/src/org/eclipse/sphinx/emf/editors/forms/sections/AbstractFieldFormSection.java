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
package org.eclipse.sphinx.emf.editors.forms.sections;

import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalEditorActionBarContributor;
import org.eclipse.sphinx.emf.editors.forms.pages.AbstractFormPage;
import org.eclipse.swt.events.FocusEvent;

public abstract class AbstractFieldFormSection extends AbstractFormSection {

	public AbstractFieldFormSection(AbstractFormPage formPage, Object sectionInput) {
		super(formPage, sectionInput);
	}

	public AbstractFieldFormSection(AbstractFormPage formPage, Object sectionInput, int style) {
		super(formPage, sectionInput, style);
	}

	@Override
	protected void focusGained(FocusEvent e) {
		super.focusGained(e);
		EditingDomainActionBarContributor actionBarContributor = formPage.getTransactionalFormEditor().getActionBarContributor();
		if (actionBarContributor instanceof BasicTransactionalEditorActionBarContributor) {
			((BasicTransactionalEditorActionBarContributor) actionBarContributor).clearGlobalActionHandlers();
		}
	}
}
