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
package org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers.extended.ExtendedHummingbird10ItemProviderAdapterFactory;

public class Hummingbird10ItemProviderAdapterFactory extends ComposedAdapterFactory {

	public Hummingbird10ItemProviderAdapterFactory() {
		super(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		addAdapterFactory(new ExtendedHummingbird10ItemProviderAdapterFactory());
	}
}
