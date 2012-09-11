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
package org.eclipse.sphinx.emf.editors.forms.nebula.providers;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor.PropertyValueWrapper;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class BasicModelXViewerLabelProvider extends XViewerLabelProvider implements ITableFontProvider {

	private final XViewer viewer;
	private final AdapterFactoryItemDelegator itemDelegator;

	public BasicModelXViewerLabelProvider(XViewer viewer, AdapterFactoryItemDelegator itemDelegator) {
		super(viewer);
		this.viewer = viewer;
		this.itemDelegator = itemDelegator;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		return null;
	}

	// TODO Enable display formats to be customized on a per feature basis
	@Override
	public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		IItemPropertyDescriptor propertyDescriptor = findPropertyDescriptorFor(element, xCol.getId());
		if (propertyDescriptor != null) {
			Object propertyValue = propertyDescriptor.getPropertyValue(element);
			if (propertyValue instanceof PropertyValueWrapper) {
				Object editableValue = ((PropertyValueWrapper) propertyValue).getEditableValue(element);
				if (editableValue instanceof Double) {
					return new DecimalFormat("0.00").format(editableValue); //$NON-NLS-1$
				}
				if (editableValue instanceof Date) {
					return DateFormat.getDateInstance().format((Date) editableValue);
				}
			}
			return propertyDescriptor.getLabelProvider(element).getText(propertyValue);
		}
		return ""; //$NON-NLS-1$
	}

	protected IItemPropertyDescriptor findPropertyDescriptorFor(Object object, String id) {
		Assert.isNotNull(id);

		List<IItemPropertyDescriptor> propertyDescriptors = itemDelegator.getPropertyDescriptors(object);
		if (propertyDescriptors != null) {
			for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors) {
				if (id.equals(propertyDescriptor.getId(object))) {
					return propertyDescriptor;
				}
			}
		}
		return null;
	}

	public Font getFont(Object element, int columnIndex) {
		return viewer.getControl().getFont();
	}
}