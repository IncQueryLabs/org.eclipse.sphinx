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
package org.eclipse.sphinx.examples.hummingbird10.edit;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;

public class Hummingbird10MMDescriptorItemProviderAdapterFactory implements
		IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
	    if (adapterType.equals(IItemLabelProvider.class)) {
	        if (adaptableObject instanceof Hummingbird10MMDescriptor) {
	          return new Hummingbird10MMDescriptorItemLabelProvider();
	        }
	      }  
	      return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
	    return new Class<?>[] { IItemLabelProvider.class };
	}
}
