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
package org.eclipse.sphinx.emf.validation.listeners;

import org.eclipse.emf.validation.service.IValidationListener;
import org.eclipse.emf.validation.service.ValidationEvent;
import org.eclipse.sphinx.emf.validation.eobject.adapter.EObjectValidationDataCacheAdapterFactory;

/**
 * A listener listening for {@link org.eclipse.emf.validation.service.ValidationEvent validation events} in order to
 * clean an {@link org.eclipse.sphinx.emf.validation.eobject.adapter.EObjectValidationDataCacheAdapter
 * EObjectValidationDataCacheAdapter}.
 */
public class CachedDataCleaner implements IValidationListener {

	public void validationOccurred(ValidationEvent event) {
		EObjectValidationDataCacheAdapterFactory.initVAdapters();
	}
}
