/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4Sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4Sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * This class extends TextCellEditor and is used in
 * {@link BasicTransactionalAdvancedPropertySection#createModelPropertySourceProvider(org.eclipse.emf.transaction.TransactionalEditingDomain)}
 * to enable the edition of the proxy URI of a proxy inside the property sheet page of a single feature's reference
 * element. This let to edit the proxy URI in text mode, validate and set this value to the concerned proxy object.
 */
public class ProxyURICellEditor extends TextCellEditor {

	/**
	 * A delegate for handling validation and conversion for proxy URIs.
	 */
	protected static class ProxyURIHandler implements ICellEditorValidator {

		protected EObject owner;
		protected EObject oldValue;

		public ProxyURIHandler(EObject owner, EObject oldValue) {
			Assert.isNotNull(owner);
			Assert.isNotNull(oldValue);

			this.owner = owner;
			this.oldValue = oldValue;
		}

		public String isValid(Object value) {
			System.out.println(value);
			ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(owner.eResource());
			if (extendedResource != null) {
				if (value instanceof String) {
					Diagnostic diagnostic = extendedResource.validateURI((String) value);
					return Diagnostic.OK_INSTANCE == diagnostic ? null : diagnostic.getMessage();
				}
			}
			return null;
		}

		public Object toValue(String valueAsString) {
			URI proxyURI = URI.createURI(valueAsString);
			if (!proxyURI.equals(((InternalEObject) oldValue).eProxyURI())) {
				EFactory factory = oldValue.eClass().getEPackage().getEFactoryInstance();
				EObject valueAsObject = factory.create(oldValue.eClass());
				((InternalEObject) valueAsObject).eSetProxyURI(proxyURI);
				return valueAsObject;
			}
			return oldValue;
		}

		public String toString(Object valueAsObject) {
			if (valueAsObject instanceof String) {
				return (String) valueAsObject;
			} else {
				if (valueAsObject instanceof EObject) {
					InternalEObject internalValue = (InternalEObject) valueAsObject;
					if (internalValue.eIsProxy()) {
						return internalValue.eProxyURI().toString();
					}
				}
			}
			return ""; //$NON-NLS-1$
		}
	}

	protected ProxyURIHandler valueHandler;

	public ProxyURICellEditor(Composite parent, EObject owner, final EStructuralFeature feature, EObject value) {
		super(parent);
		Assert.isNotNull(owner);
		Assert.isNotNull(feature);
		Assert.isNotNull(value);

		valueHandler = new ProxyURIHandler(owner, value);
		setValidator(valueHandler);

		// setValue(internalValue.eProxyURI().toString());

		// addListener(new ICellEditorListener() {
		//
		// public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		// }
		//
		// public void cancelEditor() {
		// }
		//
		// public void applyEditorValue() {
		// Object editorValue = getValue();
		// if (editorValue != null && internalValue != null) {
		// URI newProxyURI = URI.createURI(editorValue.toString());
		// URI oldProxyURI = internalValue.eProxyURI();
		// internalValue.eSetProxyURI(newProxyURI);
		//
		// // Notify adapters about value change arising from value proxy URI change if
		// // required
		// /*
		// * !! Important Note !! Don't raise notification with value object as notifier and eProxyURI as
		// * "feature". The change of the value object's proxy URI is semantically equivalent with replacing
		// * the value object with the old proxy URI by another value object with the new proxy URI. Therefore
		// * notification must happen wrt owner object and feature of value object.
		// */
		// if (internalOwner.eNotificationRequired()) {
		// // Restore old value proxy
		// EFactory eFactoryInstance = internalValue.eClass().getEPackage().getEFactoryInstance();
		// InternalEObject internalOldValue = (InternalEObject) eFactoryInstance.create(internalValue.eClass());
		// internalOldValue.eSetProxyURI(oldProxyURI);
		//
		// // Deliver set notification for replacement of old value proxy by new value proxy
		// internalOwner.eNotify(new ENotificationImpl(internalOwner, Notification.SET, feature, internalOldValue,
		// internalValue));
		// }
		// }
		// }
		// });

		// setValidator(new ICellEditorValidator() {
		// public String isValid(Object editorValue) {
		// ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(internalOwner.eResource());
		// if (extendedResource != null) {
		// if (editorValue instanceof String) {
		// Diagnostic diagnostic = extendedResource.validateURI((String) editorValue);
		// return Diagnostic.OK_INSTANCE == diagnostic ? null : diagnostic.getMessage();
		// }
		// }
		// return null;
		// }
		// });
	}

	@Override
	public Object doGetValue() {
		String valueAsString = (String) super.doGetValue();
		return valueHandler.toValue(valueAsString);
	}

	@Override
	public void doSetValue(Object value) {
		String valueAsString = valueHandler.toString(value);
		super.doSetValue(valueAsString);
	}
}
