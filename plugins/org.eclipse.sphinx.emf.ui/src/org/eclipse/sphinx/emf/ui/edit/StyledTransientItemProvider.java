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
import org.eclipse.sphinx.emf.edit.TransientItemProvider;

public class StyledTransientItemProvider extends TransientItemProvider {

	/**
	 * Standard constructor for creation of transient item provider instances through an {@link AdapterFactory adapter
	 * factory}.
	 *
	 * @param adapterFactory
	 *            The adapter factory which created this transient item provider instance.
	 */
	public StyledTransientItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/*
	 * Overridden to create StyledDelegatingWrapperItemProvider instead of the default DelegatingWrapperItemProvider, so
	 * that StyledString-typed strings can be used as labels instead of ordinary text strings.
	 * @see org.eclipse.sphinx.emf.edit.TransientItemProvider#createWrapper(java.lang.Object,
	 * org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object, int)
	 */
	@Override
	protected Object createWrapper(Object object, EStructuralFeature feature, Object value, int index) {
		if (!isWrappingNeeded(object)) {
			return value;
		}
		if (object instanceof EObject) {
			if (FeatureMapUtil.isFeatureMap(feature)) {
				value = new FeatureMapEntryWrapperItemProvider((FeatureMap.Entry) value, (EObject) object, (EAttribute) feature, index,
						adapterFactory, getResourceLocator());
			} else if (feature instanceof EAttribute) {
				value = new AttributeValueWrapperItemProvider(value, (EObject) object, (EAttribute) feature, index, adapterFactory,
						getResourceLocator());
			}
		} else {
			if (!((EReference) feature).isContainment()) {
				value = new StyledDelegatingWrapperItemProvider(value, object, feature, index, adapterFactory);
			}
		}
		return value;
	}

}
