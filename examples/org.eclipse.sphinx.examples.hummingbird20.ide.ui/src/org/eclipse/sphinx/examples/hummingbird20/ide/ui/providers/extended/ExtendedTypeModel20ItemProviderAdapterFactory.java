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
package org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.extended;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.edit.TypeModel20ItemProviderAdapterFactory;

public class ExtendedTypeModel20ItemProviderAdapterFactory extends TypeModel20ItemProviderAdapterFactory {

	@Override
	public Adapter createComponentTypeAdapter() {
		return new ExtendedComponentTypeItemProvider(this);
	}

	@Override
	public Adapter createParameterAdapter() {
		return new ExtendedParameterItemProvider(this);
	}

	@Override
	public Adapter createPortAdapter() {
		return new ExtendedPortItemProvider(this);
	}

	@Override
	public Adapter createPlatformAdapter() {
		return new ExtendedPlatformItemProvider(this);
	}

	@Override
	public Adapter createInterfaceAdapter() {
		return new ExtendedInterfaceItemProvider(this);
	}
}
