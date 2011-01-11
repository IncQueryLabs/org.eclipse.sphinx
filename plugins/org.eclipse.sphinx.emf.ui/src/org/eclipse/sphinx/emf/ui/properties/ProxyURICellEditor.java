/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4Sys.
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;
import org.eclipse.swt.widgets.Composite;

/**
 * This class extends TextCellEditor and is used in
 * {@link BasicTransactionalAdvancedPropertySection#createModelPropertySourceProvider(org.eclipse.emf.transaction.TransactionalEditingDomain)}
 * to enable the edition of the proxy URI of a proxy inside the property sheet page of a single feature's reference
 * element. This let to edit the proxy URI in text mode, validate and set this value to the concerned proxy object.
 */
public class ProxyURICellEditor extends TextCellEditor {

	public ProxyURICellEditor(Composite composite, final EObject owner, final InternalEObject value) {
		super(composite);
		Assert.isNotNull(owner);
		Assert.isNotNull(value);

		URI proxyURI = value.eProxyURI();
		setValue(proxyURI.toString());

		addListener(new ICellEditorListener() {

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}

			public void cancelEditor() {
			}

			public void applyEditorValue() {
				Object editorValue = getValue();
				if (editorValue != null && value != null) {
					value.eSetProxyURI(URI.createURI(editorValue.toString()));
					ModelSaveManager.INSTANCE.setDirty(owner.eResource());
					ModelSaveManager.INSTANCE.notifyDirtyChanged(owner.eResource());

				}

			}
		});

		setValidator(new ICellEditorValidator() {
			public String isValid(Object editorValue) {
				ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(owner.eResource());
				if (extendedResource != null) {
					if (editorValue instanceof String) {
						Diagnostic diagnostic = extendedResource.validateURI((String) editorValue);
						return Diagnostic.OK_INSTANCE == diagnostic ? null : diagnostic.getMessage();
					}
				}
				return null;
			}
		});

	}

	@Override
	protected void doSetValue(Object editorValue) {
		if (editorValue instanceof String) {
			super.doSetValue(editorValue);
		} else {
			if (editorValue instanceof EObject) {
				InternalEObject eEditorValue = (InternalEObject) editorValue;
				if (eEditorValue.eIsProxy()) {
					super.doSetValue(eEditorValue.eProxyURI().toString());
				}
			}
		}
	}
}
