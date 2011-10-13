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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.sphinx.emf.explorer.BasicExplorerLabelProvider;

public class ExtendedExplorerLabelProvider extends BasicExplorerLabelProvider {

	@Override
	protected AdapterFactory getCustomAdapterFactory() {
		// TODO Think of sharing composed item provider adapter factory between content and label provider (e.g. by
		// using using viewer.getData(key))
		return new Hummingbird20ItemProviderAdapterFactory();
	}
}
