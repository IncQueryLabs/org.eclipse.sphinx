/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.examples.hummingbird20.editors.nebula.pages;

import org.eclipse.sphinx.emf.editors.forms.layouts.LayoutFactory;
import org.eclipse.sphinx.emf.editors.forms.pages.AbstractFormPage;
import org.eclipse.sphinx.examples.hummingbird20.editors.nebula.messages.Messages;
import org.eclipse.sphinx.examples.hummingbird20.editors.nebula.sections.GenericParameterValuesXViewerSection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

public class GenericParameterValuesOverviewPage extends AbstractFormPage {

	GenericParameterValuesXViewerSection parameterValuesSection;

	public GenericParameterValuesOverviewPage(FormEditor editor) {
		super(editor, Messages.title_GenericParameterValues_OverviewPage);
	}

	public GenericParameterValuesOverviewPage(FormEditor editor, String title) {
		super(editor, title);
	}

	@Override
	protected void doRefreshPage() {
		if (parameterValuesSection != null) {
			parameterValuesSection.refreshSection();
		}
	}

	@Override
	protected void doCreateFormContent(IManagedForm managedForm) {
		// Create single columned page layout
		Composite body = managedForm.getForm().getBody();
		body.setLayout(LayoutFactory.createFormBodyGridLayout(false, 1));

		// Create model contents tree section
		parameterValuesSection = new GenericParameterValuesXViewerSection(this, pageInput);
		parameterValuesSection.createContent(managedForm, body);
		addSection(parameterValuesSection);
	}
}
