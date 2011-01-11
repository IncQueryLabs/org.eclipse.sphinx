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
package org.eclipse.sphinx.emf.validation.eobject.adapter;

import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * This adapter represent a behaviour extension to {@link org.eclipse.emf.ecore.EObject EObject} in order to store them
 * validation status.
 */
public class EObjectValidationDataCacheAdapter extends AdapterImpl implements IEObjectValidationDataCache {

	static final boolean DEFAULT_SEVERITY_OK = false;

	protected int severity = -1;
	protected boolean isSeverityOk = DEFAULT_SEVERITY_OK;

	public boolean isSeverityOk() {
		return isSeverityOk;
	}

	public int getSeverity() {
		return severity;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return type == EObjectValidationDataCacheAdapter.class;
	}

	/**
	 * Set the attribute used to check is the severity status stored is up-to-date
	 * 
	 * @param value
	 *            the current severity value state
	 */
	public void setSeverityDataOk(boolean value) {
		isSeverityOk = value;
	}

	/**
	 * Set the severity attribute
	 * 
	 * @param value
	 *            the severity status code
	 * @see ValidationStatusCode
	 */
	public void setSeverity(int value) {
		severity = value;
	}

}
