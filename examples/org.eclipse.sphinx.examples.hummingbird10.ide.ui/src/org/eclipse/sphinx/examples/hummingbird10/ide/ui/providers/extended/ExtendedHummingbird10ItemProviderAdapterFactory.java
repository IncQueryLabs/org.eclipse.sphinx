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
package org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers.extended;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.sphinx.examples.hummingbird10.edit.Hummingbird10ItemProviderAdapterFactory;

public class ExtendedHummingbird10ItemProviderAdapterFactory extends Hummingbird10ItemProviderAdapterFactory {

	@Override
	public Adapter createComponentAdapter() {
		return new ExtendedComponentItemProvider(this);
	}

	@Override
	public Adapter createParameterAdapter() {
		return new ExtendedParameterItemProvider(this);
	}

	@Override
	public Adapter createConnectionAdapter() {
		return new ExtendedConnectionItemProvider(this);
	}
}
