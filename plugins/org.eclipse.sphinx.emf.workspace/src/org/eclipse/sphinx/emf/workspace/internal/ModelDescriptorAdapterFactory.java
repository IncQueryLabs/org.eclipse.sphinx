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
package org.eclipse.sphinx.emf.workspace.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.saving.IModelSaveIndicator;
import org.eclipse.sphinx.emf.workspace.internal.saving.ModelSaveIndicator;

public class ModelDescriptorAdapterFactory implements IAdapterFactory {

	IModelSaveIndicator saveIndicator = new ModelSaveIndicator();

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IModelSaveIndicator.class.equals(adapterType) && adaptableObject instanceof IModelDescriptor) {
			return saveIndicator;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class<?>[] { IModelSaveIndicator.class };
	}

}
