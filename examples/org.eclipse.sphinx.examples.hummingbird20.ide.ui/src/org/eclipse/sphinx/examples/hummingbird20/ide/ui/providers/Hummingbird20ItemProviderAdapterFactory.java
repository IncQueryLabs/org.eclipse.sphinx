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
package org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.extended.ExtendedInstanceModel20ItemProviderAdapterFactory;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.extended.ExtendedTypeModel20ItemProviderAdapterFactory;

public class Hummingbird20ItemProviderAdapterFactory extends ComposedAdapterFactory {

	public Hummingbird20ItemProviderAdapterFactory() {
		super(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		addAdapterFactory(new ExtendedInstanceModel20ItemProviderAdapterFactory());
		addAdapterFactory(new ExtendedTypeModel20ItemProviderAdapterFactory());
	}
}
