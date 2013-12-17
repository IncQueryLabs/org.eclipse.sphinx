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
package org.eclipse.sphinx.emf.ui.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.sphinx.emf.ui.properties.filters.IPropertySourceFilter;
import org.eclipse.sphinx.emf.ui.properties.filters.PropertySourceFilterRegistry;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class FilteringPropertySource implements IPropertySource {

	IPropertySource propertySourceDelegate;
	IPropertySourceFilter propertySourceFilter;

	public FilteringPropertySource(IPropertySource propertySourceDelegate) {
		Assert.isNotNull(propertySourceDelegate);

		this.propertySourceDelegate = propertySourceDelegate;
	}

	public IPropertySourceFilter getPropertySourceFilter() {
		if (propertySourceFilter == null) {
			Object owner = propertySourceDelegate.getEditableValue();
			propertySourceFilter = PropertySourceFilterRegistry.INSTANCE.getPropertySourceFilter(owner);
		}
		return propertySourceFilter;
	}

	@Override
	public Object getEditableValue() {
		return propertySourceDelegate.getEditableValue();
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (getPropertySourceFilter() != null) {
			return getPropertySourceFilter().getAcceptedPropertyDescriptors(propertySourceDelegate);
		} else {
			return propertySourceDelegate.getPropertyDescriptors();
		}
	}

	@Override
	public Object getPropertyValue(Object id) {
		return propertySourceDelegate.getPropertyValue(id);
	}

	@Override
	public boolean isPropertySet(Object id) {
		return propertySourceDelegate.isPropertySet(id);
	}

	@Override
	public void resetPropertyValue(Object id) {
		propertySourceDelegate.resetPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		propertySourceDelegate.setPropertyValue(id, value);
	}
}
