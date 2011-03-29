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
package org.eclipse.sphinx.xpand.ui.blocks;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.sphinx.platform.ui.util.SWTUtil;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.ui.dialogs.EditOutletDialog;
import org.eclipse.sphinx.xpand.ui.providers.OutletProvider;
import org.eclipse.sphinx.xpand.ui.providers.OutletTableContentProvider;
import org.eclipse.sphinx.xpand.ui.providers.OutletTableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.xpand2.output.Outlet;

// TODO Rename to OutletsGroup
public class OutletsBlock {

	/** The table presenting the outlets. */
	private TableViewer tableViewer;

	private OutletProvider outletProvider;

	/** The buttons to add, edit and remove outlets. */
	private Button addButton;
	private Button editButton;
	private Button removeButton;

	private Listener listener = new Listener() {

		public void handleEvent(Event event) {
			if (event.widget == addButton) {
				add();
			} else if (event.widget == editButton) {
				edit();
			} else if (event.widget == removeButton) {
				remove();
			}
		}
	};

	public OutletsBlock(Composite parent, OutletProvider outletProvider, boolean addButtons) {
		this.outletProvider = outletProvider;
		createContent(parent, addButtons);
	}

	protected void createContent(Composite parent, boolean addButtons) {
		Assert.isNotNull(parent.getShell());

		GC gc = new GC(parent.getShell());
		gc.setFont(JFaceResources.getDialogFont());

		Composite tableComposite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 360;
		data.heightHint = convertHeightInCharsToPixels(10, gc);
		tableComposite.setLayoutData(data);

		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);
		Table table = new Table(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Name");
		int minWidth = computeMinimumColumnWidth(gc, "Name");
		columnLayout.setColumnData(column1, new ColumnWeightData(1, minWidth, true));

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Path");
		minWidth = computeMinimumColumnWidth(gc, "Path");
		columnLayout.setColumnData(column2, new ColumnWeightData(3, minWidth, true));

		gc.dispose();

		tableViewer = new TableViewer(table);
		tableViewer.setLabelProvider(new OutletTableLabelProvider());
		tableViewer.setContentProvider(new OutletTableContentProvider());
		tableViewer.setInput(outletProvider);

		if (addButtons) {
			addTableViewerListener();
			addButtons(parent);
		}
	}

	protected void addTableViewerListener() {
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				edit();
			}
		});

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				updateButtons();
			}
		});
	}

	protected void addButtons(Composite parent) {
		Composite buttonsComposite = new Composite(parent, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridLayout blayout = new GridLayout();
		blayout.marginHeight = 0;
		blayout.marginWidth = 0;
		buttonsComposite.setLayout(blayout);

		addButton = SWTUtil.createButton(buttonsComposite, "New...", SWT.PUSH);
		addButton.addListener(SWT.Selection, listener);

		editButton = SWTUtil.createButton(buttonsComposite, "Edit...", SWT.PUSH);
		editButton.addListener(SWT.Selection, listener);

		removeButton = SWTUtil.createButton(buttonsComposite, "Remove", SWT.PUSH);
		removeButton.addListener(SWT.Selection, listener);
		updateButtons();
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setEnabled(boolean enabled) {
		tableViewer.getTable().setEnabled(enabled);
	}

	public Composite getButtonsComposite() {
		if (addButton != null) {
			return addButton.getParent();
		}
		return null;
	}

	/**
	 * Updates the buttons.
	 */
	protected void updateButtons() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		int selectionCount = selection.size();
		int itemCount = tableViewer.getTable().getItemCount();
		editButton.setEnabled(selectionCount == 1);
		removeButton.setEnabled(selectionCount > 0 && selectionCount <= itemCount && !containsDefaultOutlet(selection));
	}

	protected void add() {
		ExtendedOutlet outlet = editOutlet(new ExtendedOutlet(), false, true);
		if (outlet != null) {
			outletProvider.addOutlet(outlet);
			tableViewer.refresh();
			tableViewer.setSelection(new StructuredSelection(outlet));
		}
	}

	protected void edit() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		ExtendedOutlet selectedOutlet = (ExtendedOutlet) selection.getFirstElement();
		ExtendedOutlet outletToEdit = new ExtendedOutlet(selectedOutlet.getPathExpression(), outletProvider.getProject());
		outletToEdit.setName(selectedOutlet.getName());
		ExtendedOutlet editedOutlet = editOutlet(outletToEdit, true, selectedOutlet.getName() != null);
		if (editedOutlet != null) {
			selectedOutlet.setPathExpression(editedOutlet.getPathExpression(), outletProvider.getProject());
			selectedOutlet.setName(editedOutlet.getName());
			tableViewer.refresh();
		}
	}

	protected void remove() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		for (Object element : selection.toList()) {
			outletProvider.removeOutlet((ExtendedOutlet) element);
		}
		tableViewer.refresh();
	}

	protected ExtendedOutlet editOutlet(ExtendedOutlet outlet, boolean edit, boolean isNameModifiable) {
		EditOutletDialog dialog = new EditOutletDialog(getTableViewer().getControl().getShell(), outlet, edit, isNameModifiable, outletProvider);
		if (dialog.open() == Window.OK) {
			return dialog.getOutlet();
		}
		return null;
	}

	protected boolean containsDefaultOutlet(IStructuredSelection selection) {
		for (Object element : selection.toList()) {
			if (((Outlet) element).getName() == null) {
				return true;
			}
		}
		return false;
	}

	protected int convertHeightInCharsToPixels(int chars, GC gc) {
		if (gc.getFontMetrics() == null) {
			return 0;
		}
		return Dialog.convertHeightInCharsToPixels(gc.getFontMetrics(), chars);
	}

	private int computeMinimumColumnWidth(GC gc, String string) {
		return gc.stringExtent(string).x + 10;
	}
}
