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
package org.eclipse.sphinx.emf.resource;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * An {@link AdapterFactory adapter factory} for creating {@link ExtendedResource} adapters.
 */
public class ExtendedResourceAdapterFactory extends AdapterFactoryImpl {

	/**
	 * The singleton instance of this {@link AdapterFactory adapter factory}.
	 */
	public static ExtendedResourceAdapterFactory INSTANCE = new ExtendedResourceAdapterFactory();

	// Protected default constructor for singleton pattern
	protected ExtendedResourceAdapterFactory() {
		// Nothing to do
	}

	/*
	 * @see org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#isFactoryForType(java.lang.Object)
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return type == ExtendedResource.class;
	}

	/**
	 * Returns either a previously associated {@link ExtendedResource} adapter or a newly associated one, as
	 * appropriate. {@link Adapter#isAdapterForType(Object) Checks} if the an adapter for {@link ExtendedResource} is
	 * already associated with the target and returns it in that case; {@link AdapterFactory#adaptNew(Notifier, Object)
	 * creates} a new {@link ExtendedResource} adapter if possible otherwise.
	 * 
	 * @param target
	 *            The notifier to adapt.
	 * @return A previously existing associated {@link ExtendedResource} adapter, a new associated
	 *         {@link ExtendedResource} adapter if possible, or <code>null</code> otherwise.
	 * @see AdapterFactory#adapt(Notifier, Object)
	 * @see Adapter#isAdapterForType(Object)
	 * @see AdapterFactory#adaptNew(Notifier, Object)
	 */
	public ExtendedResource adapt(Notifier target) {
		if (target != null) {
			return (ExtendedResource) adapt(target, ExtendedResource.class);
		}
		return null;
	}

	/*
	 * @see org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#createAdapter(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	protected Adapter createAdapter(Notifier target) {
		if (target instanceof Resource) {
			return new ExtendedResourceAdapter();
		}
		return null;
	}
}
