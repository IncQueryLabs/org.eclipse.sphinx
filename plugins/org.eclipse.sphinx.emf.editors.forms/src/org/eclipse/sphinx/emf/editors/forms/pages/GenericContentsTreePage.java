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
package org.eclipse.sphinx.emf.editors.forms.pages;

import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalFormEditor;
import org.eclipse.sphinx.emf.editors.forms.internal.messages.Messages;
import org.eclipse.sphinx.emf.editors.forms.layouts.LayoutFactory;
import org.eclipse.sphinx.emf.editors.forms.sections.GenericContentsTreeSection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

public class GenericContentsTreePage extends AbstractFormPage {

	protected GenericContentsTreeSection contentsTreeSection;

	public GenericContentsTreePage(BasicTransactionalFormEditor formEditor) {
		this(formEditor, Messages.page_contentsTreePage_title);
	}

	public GenericContentsTreePage(BasicTransactionalFormEditor formEditor, String title) {
		super(formEditor, title);
	}

	@Override
	protected void doCreateFormContent(final IManagedForm managedForm) {
		// Create single columned page layout
		Composite body = managedForm.getForm().getBody();
		body.setLayout(LayoutFactory.createFormBodyGridLayout(false, 1));

		// Create model contents tree section
		contentsTreeSection = new GenericContentsTreeSection(this, pageInput);
		contentsTreeSection.createContent(managedForm, body);
		addSection(contentsTreeSection);
	}

	@Override
	public boolean isEmpty() {
		return contentsTreeSection.isEmpty();
	}
}