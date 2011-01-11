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

import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalEditorActionBarContributor;
import org.eclipse.sphinx.emf.editors.forms.pages.AbstractFormPage;
import org.eclipse.sphinx.platform.util.ReflectUtil;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;

public abstract class AbstractViewerFormSection extends AbstractFormSection implements IViewerProvider {

	protected StructuredViewer viewer;

	public AbstractViewerFormSection(AbstractFormPage formPage, Object sectionInput) {
		super(formPage, sectionInput);
	}

	public AbstractViewerFormSection(AbstractFormPage formPage, Object sectionInput, int style) {
		super(formPage, sectionInput, style);
	}

	@Override
	public void setSectionInput(Object sectionInput) {
		super.setSectionInput(sectionInput);
		if (viewer != null) {
			viewer.setInput(getViewerInput());
		}
	}

	public StructuredViewer getViewer() {
		return viewer;
	}

	public Object getViewerInput() {
		return sectionInput;
	}

	@Override
	protected Composite createSectionClient(final IManagedForm managedForm, final SectionPart sectionPart) {
		Composite composite = doCreateSectionClient(managedForm, sectionPart);
		// viewer is set after calling doCreateSectionClient(managedForm, sectionPart)
		if (viewer != null) {
			formPage.getTransactionalFormEditor().setSelectionProvider(viewer);
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					managedForm.fireSelectionChanged(sectionPart, event.getSelection());
					formPage.getTransactionalFormEditor().setSelectionProvider(viewer);
				}
			});
		}
		return composite;
	}

	@Override
	public boolean isEmpty() {
		if (viewer != null) {
			try {
				Object[] filteredChildren = (Object[]) ReflectUtil.invokeInvisibleMethod(viewer, "getFilteredChildren", getViewerInput()); //$NON-NLS-1$
				return filteredChildren.length == 0;
			} catch (Exception ex) {
				// Ignore exception
			}
		}
		return false;
	}

	@Override
	protected void focusGained(FocusEvent e) {
		super.focusGained(e);
		EditingDomainActionBarContributor actionBarContributor = formPage.getTransactionalFormEditor().getActionBarContributor();
		if (actionBarContributor instanceof BasicTransactionalEditorActionBarContributor) {
			((BasicTransactionalEditorActionBarContributor) actionBarContributor).setGlobalActionHandlers();
		}
	}
}
