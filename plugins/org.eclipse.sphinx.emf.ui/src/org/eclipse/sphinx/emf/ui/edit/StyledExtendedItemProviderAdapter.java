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
 *     itemis - [446576] Add support for providing labels with different fonts and styles for model elements through BasicExplorerLabelProvider
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.edit;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.edit.provider.AttributeValueWrapperItemProvider;
import org.eclipse.emf.edit.provider.FeatureMapEntryWrapperItemProvider;
import org.eclipse.sphinx.emf.edit.ExtendedItemProviderAdapter;

public class StyledExtendedItemProviderAdapter extends ExtendedItemProviderAdapter {

	public StyledExtendedItemProviderAdapter(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/*
	 * Override to create StyledDelegatingWrapperItemProvider instead of the default DelegatingWrapperItemProvider, so
	 * that the styledString will be used instead of the text string
	 * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#createWrapper(org.eclipse.emf.ecore.EObject,
	 * org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object, int)
	 */
	@Override
	protected Object createWrapper(EObject object, EStructuralFeature feature, Object value, int index) {
		if (!isWrappingNeeded(object)) {
			return value;
		}

		if (FeatureMapUtil.isFeatureMap(feature)) {
			value = new FeatureMapEntryWrapperItemProvider((FeatureMap.Entry) value, object, (EAttribute) feature, index, adapterFactory,
					getResourceLocator());
		} else if (feature instanceof EAttribute) {
			value = new AttributeValueWrapperItemProvider(value, object, (EAttribute) feature, index, adapterFactory, getResourceLocator());
		} else if (!((EReference) feature).isContainment()) {
			value = new StyledDelegatingWrapperItemProvider(value, object, feature, index, adapterFactory);
		}

		return value;
	}

}
