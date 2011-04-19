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
package org.eclipse.sphinx.xpand.ui.outlet.providers;

import org.eclipse.jface.viewers.ArrayContentProvider;

public class OutletTableContentProvider extends ArrayContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof OutletProvider) {
			return ((OutletProvider) inputElement).getOutlets().toArray();
		}
		return super.getElements(inputElement);
	}
}
