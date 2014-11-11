/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.sphinx.emf.mwe.dynamic.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

// TODO Generalize and move to new plug-in org.eclipse.sphinx.xtend.ui
public class WorkflowFileSelectionDialog extends SelectionDialog {

	private TreeViewer viewer;

	private IStructuredSelection selected;

	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 300;
	private final static int SIZING_SELECTION_WIDGET_WIDTH = 500;

	public WorkflowFileSelectionDialog(Shell parentShell) {
		super(parentShell);
		setTitle(Messages.dialog_workflowFileSelection_title);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		initializeDialogUnits(composite);

		Composite viewerArea = new Composite(composite, SWT.NONE);
		viewerArea.setLayout(new GridLayout());

		viewer = createViewer(viewerArea);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		viewerArea.setLayoutData(data);
		viewer.getControl().setLayoutData(data);

		StructuredSelection initialSelection = (StructuredSelection) getInitialElementSelections().get(0);
		viewer.setSelection(initialSelection);
		Object selected = initialSelection.getFirstElement();
		if (selected != null) {
			viewer.setExpandedElements(new Object[] { selected });
		}
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				viewerSelectionChanged(event);
			}
		});
		Dialog.applyDialogFont(viewerArea);

		return composite;
	}

	protected TreeViewer createViewer(Composite parent) {
		TreeViewer treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = convertWidthInCharsToPixels(40);
		gd.heightHint = convertHeightInCharsToPixels(15);
		treeViewer.getTree().setLayoutData(gd);
		treeViewer.setLabelProvider(new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_SMALL_ICONS));
		treeViewer.setContentProvider(new FilteredJavaElementContentProvider());
		treeViewer.setComparator(new JavaElementComparator());
		treeViewer.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		return treeViewer;
	}

	protected void viewerSelectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		selected = (IStructuredSelection) selection;
	}

	protected TreeViewer getTreeViewer() {
		return viewer;
	}

	protected ISelection getSelection() {
		if (viewer == null) {
			return StructuredSelection.EMPTY;
		}
		return selected;
	}

	public IPath getWorkflowPath() {
		IPath fullPath = null;
		StructuredSelection result = (StructuredSelection) getSelection();
		if (result != null) {
			Object firstElement = result.getFirstElement();
			if (firstElement instanceof IFile) {
				fullPath = ((IFile) firstElement).getFullPath();
			}
			if (firstElement instanceof ICompilationUnit) {
				fullPath = ((ICompilationUnit) firstElement).getPath();
			}
		}
		return fullPath;
	}
}