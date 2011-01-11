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
package org.eclipse.sphinx.platform.ui.fields;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.sphinx.platform.ui.fields.adapters.IListAdapter;
import org.eclipse.sphinx.platform.ui.internal.util.LayoutUtil;
import org.eclipse.sphinx.platform.ui.internal.util.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * 
 */
public class ListButtonsField extends ListField {

	/**
	 * Constructor.
	 * 
	 * @param adapter
	 *            The list adapter.
	 * @param buttonLabels
	 *            The labels of the buttons to create next to the list.
	 * @param provider
	 *            The label provider to use for the list.
	 */
	public ListButtonsField(IListAdapter adapter, String[] buttonLabels, ILabelProvider provider) {
		super(adapter, buttonLabels, provider);
	}

	@Override
	protected Control[] doFillIntoGrid(Composite parent, int nColumns) {
		PixelConverter converter = new PixelConverter(parent);

		Control label = getLabelControl(parent, 1);
		// TODO Remove following lines as layout data should already be set.
		if (fUseFormLayout) {
			TableWrapData twdLabel = LayoutUtil.tableWrapDataForLabel(1);
			twdLabel.valign = TableWrapData.TOP;
			label.setLayoutData(twdLabel);
		} else {
			GridData gdLabel = LayoutUtil.gridDataForLabel(1);
			gdLabel.verticalAlignment = GridData.BEGINNING;
			label.setLayoutData(gdLabel);
		}

		Composite composite = createSpecificComposite(parent, nColumns);

		Control list = getListControl(composite);
		// Leave last column for buttons if such are defined, span over buttons column otherwise
		int nListColumns = hasButtons() ? 1 : 2;
		if (fUseFormLayout) {
			list.setLayoutData(LayoutUtil.tableWrapDataForList(nListColumns, converter));
		} else {
			list.setLayoutData(LayoutUtil.gridDataForList(nListColumns, converter));
		}

		Composite buttons = getButtonBox(composite);
		if (buttons != null) {
			if (fUseFormLayout) {
				buttons.setLayoutData(LayoutUtil.tableWrapDataForButtons(1));
			} else {
				buttons.setLayoutData(LayoutUtil.gridDataForButtons(1));
			}
		}

		return buttons != null ? new Control[] { label, list, buttons } : new Control[] { label, list };
	}

	private Composite createSpecificComposite(Composite parent, int nColumns) {
		Layout layout;
		if (fUseFormLayout) {
			layout = LayoutUtil.tableWrapLayoutForSpecificComposite(getNumberOfControls() - 1);
		} else {
			layout = LayoutUtil.gridLayoutForSpecificComposite(getNumberOfControls());
		}

		Object data;
		if (fUseFormLayout) {
			data = LayoutUtil.tableWrapDataForSpecificComposite(nColumns - 1);
		} else {
			data = LayoutUtil.gridDataForSpecificComposite(nColumns - 1);
		}

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(layout);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		return composite;
	}
}
