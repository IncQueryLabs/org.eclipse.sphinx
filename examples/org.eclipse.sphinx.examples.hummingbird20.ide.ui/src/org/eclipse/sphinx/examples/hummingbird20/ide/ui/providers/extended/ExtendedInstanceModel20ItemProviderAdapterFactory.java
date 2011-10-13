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
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.edit.InstanceModel20ItemProviderAdapterFactory;

public class ExtendedInstanceModel20ItemProviderAdapterFactory extends InstanceModel20ItemProviderAdapterFactory {

	@Override
	public Adapter createComponentAdapter() {
		return new ExtendedComponentItemProvider(this);
	}

	@Override
	public Adapter createParameterValueAdapter() {
		return new ExtendedParameterValueItemProvider(this);
	}

	@Override
	public Adapter createConnectionAdapter() {
		return new ExtendedConnectionItemProvider(this);
	}
}
