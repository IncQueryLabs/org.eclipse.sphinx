/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [393310] Viewer input for GenericContentsTreeSection should be calculated using content provider
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.editors.forms.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalFormEditor;
import org.eclipse.sphinx.emf.editors.forms.internal.Activator;
import org.eclipse.sphinx.emf.editors.forms.pages.AbstractFormPage;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class GenericContentsTreeSection extends AbstractViewerFormSection {

	public GenericContentsTreeSection(AbstractFormPage formPage, Object sectionInput) {
		this(formPage, sectionInput, SWT.NONE);
	}

	public GenericContentsTreeSection(AbstractFormPage formPage, Object sectionInput, int style) {
		super(formPage, sectionInput, style);
		description = Activator.getPlugin().getString("GenericContentsSection_description"); //$NON-NLS-1$
	}

	protected Object getSectionInputParent() {
		IContentProvider contentProvider = getContentProvider();
		if (contentProvider instanceof ITreeContentProvider) {
			return ((ITreeContentProvider) contentProvider).getParent(sectionInput);
		}
		return null;
	}

	@Override
	public Object getViewerInput() {
		// Don't display resource behind section input in viewer
		if (!(sectionInput instanceof Resource)) {
			return getSectionInputParent();
		}
		return sectionInput;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}

	@Override
	protected void createSectionClientContent(final IManagedForm managedForm, final SectionPart sectionPart, Composite sectionClient) {
		Assert.isNotNull(managedForm);
		Assert.isNotNull(sectionPart);
		Assert.isNotNull(sectionClient);

		// Create model contents tree
		FormToolkit toolkit = managedForm.getToolkit();
		Tree tree = toolkit.createTree(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create model contents tree viewer
		final BasicTransactionalFormEditor formEditor = formPage.getTransactionalFormEditor();
		TreeViewer treeViewer = new TreeViewer(tree) {
			@Override
			public void setSelection(ISelection selection) {
				selection = !SelectionUtil.getStructuredSelection(selection).isEmpty() ? selection : formEditor.getDefaultSelection();
				super.setSelection(selection);
			}
		};
		viewer = treeViewer;
		IContentProvider contentProvider = getContentProvider();
		if (contentProvider != null) {
			treeViewer.setContentProvider(getContentProvider());
		}
		IBaseLabelProvider labelProvider = getLabelProvider();
		if (labelProvider != null) {
			treeViewer.setLabelProvider(labelProvider);
		}
		treeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				// Show only sectionInput but not its siblings
				return parentElement != getSectionInputParent() || element == sectionInput;
			}
		});
		treeViewer.setInput(getViewerInput());
		formEditor.createContextMenuFor(treeViewer);
	}

	@Override
	public void refreshSection() {
		refreshSectionTitle();
	}

	protected void refreshSectionTitle() {
		if (title == null && isControlAccessible(section)) {
			section.setText(Activator.getPlugin().getString("GenericContentsSection_title", new Object[] { getSectionInputName() })); //$NON-NLS-1$
		}
	}
}
