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
package org.eclipse.sphinx.xpand.ui.groups;

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
import org.eclipse.sphinx.xpand.ui.groups.messages.Messages;
import org.eclipse.sphinx.xpand.ui.outlet.providers.OutletProvider;
import org.eclipse.sphinx.xpand.ui.outlet.providers.OutletTableContentProvider;
import org.eclipse.sphinx.xpand.ui.outlet.providers.OutletTableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.xpand2.output.Outlet;

public class OutletsGroup {

	/**
	 * The name of the OutletsGroup.
	 */
	protected String groupName;

	/**
	 * The table presenting the outlets.
	 */
	private TableViewer tableViewer;

	/**
	 * The outlet provider.
	 */
	private OutletProvider outletProvider;

	/**
	 * The add outlets button. to add, edit and remove outlets.
	 */
	private Button addButton;

	/**
	 * The edit outlets button.
	 */
	private Button editButton;

	/**
	 * The remove outlets button.
	 */
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

	public OutletsGroup(Composite parent, String groupName, OutletProvider outletProvider, int numColumns, boolean addButtons) {
		this(groupName, outletProvider);
		createContent(parent, numColumns, addButtons);
	}

	private OutletsGroup(String groupName, OutletProvider outletProvider) {
		this.groupName = groupName;
		this.outletProvider = outletProvider;
	}

	protected void createContent(Composite parent, int numColumns, boolean addButtons) {
		Assert.isNotNull(parent.getShell());

		Group outletsGroup = new Group(parent, SWT.SHADOW_NONE);
		outletsGroup.setText(groupName);
		outletsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		GridLayout outletsGroupLayout = new GridLayout();
		outletsGroupLayout.numColumns = numColumns;
		outletsGroup.setLayout(outletsGroupLayout);

		GC gc = new GC(parent.getShell());
		gc.setFont(JFaceResources.getDialogFont());

		Composite tableComposite = new Composite(outletsGroup, SWT.NONE);
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
		column1.setText(Messages.label_OutletsGroup_TableColumn_Name);
		int minWidth = computeMinimumColumnWidth(gc, Messages.label_OutletsGroup_TableColumn_Name);
		columnLayout.setColumnData(column1, new ColumnWeightData(1, minWidth, true));

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText(Messages.label_OutletsGroup_TableColumn_Path);
		minWidth = computeMinimumColumnWidth(gc, Messages.label_OutletsGroup_TableColumn_Path);
		columnLayout.setColumnData(column2, new ColumnWeightData(3, minWidth, true));

		gc.dispose();

		tableViewer = new TableViewer(table);
		tableViewer.setLabelProvider(new OutletTableLabelProvider());
		tableViewer.setContentProvider(new OutletTableContentProvider());
		tableViewer.setInput(outletProvider);

		if (addButtons) {
			addTableViewerListener();
			addButtons(outletsGroup);
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

		addButton = SWTUtil.createButton(buttonsComposite, Messages.label_AddButton, SWT.PUSH);
		addButton.addListener(SWT.Selection, listener);

		editButton = SWTUtil.createButton(buttonsComposite, Messages.label_EditButton, SWT.PUSH);
		editButton.addListener(SWT.Selection, listener);

		removeButton = SWTUtil.createButton(buttonsComposite, Messages.label_RemoveButton, SWT.PUSH);
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
