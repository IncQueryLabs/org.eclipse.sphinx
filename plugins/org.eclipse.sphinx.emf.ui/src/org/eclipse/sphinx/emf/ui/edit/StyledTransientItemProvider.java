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
import org.eclipse.emf.common.notify.Notifier;
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

	public StyledTransientItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * A constructor allowing to create transient item providers. Transient item providers are not created in the usual
	 * way (i.e., by calling {@link org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#adapt(Notifier, Object))} for
	 * the object that content non-model elements), their constructors explicitly add them to the eAdapters.
	 *
	 * @param adapterFactory
	 *            an adapter factory.
	 * @param parent
	 *            the parent of the element.
	 */
	public StyledTransientItemProvider(AdapterFactory adapterFactory, Notifier parent) {
		super(adapterFactory, parent);
	}

	/**
	 * Creates and returns a wrapper for the given value, at the given index in the given feature of the given object if
	 * such a wrapper is needed; otherwise, returns the original value.
	 * <p>
	 * This method is very similar to {@link #createWrapper(EObject, EStructuralFeature, Object, int)} but accepts and
	 * handles {@link Object} rather than just {@link EObject} <code>object</code> arguments.
	 * </p>
	 *
	 * @see #createWrapper(EObject, EStructuralFeature, Object, int)
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
				// Create StyledDelegatingWrapperItemProvider instead of the default DelegatingWrapperItemProvider, so
				// that the styledString will be used instead of the text string
				value = new StyledDelegatingWrapperItemProvider(value, object, feature, index, adapterFactory);
			}
		}
		return value;
	}

}
