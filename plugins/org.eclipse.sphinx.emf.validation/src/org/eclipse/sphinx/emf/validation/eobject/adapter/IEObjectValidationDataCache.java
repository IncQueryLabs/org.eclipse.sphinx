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

public interface IEObjectValidationDataCache {

	/**
	 * Checks if the severity value stored into the adapter is up-to-date.
	 * 
	 * @return <tt>true</tt> if the stored serverity value is up-to-dat, otherwise <tt>false</tt>.
	 */
	public boolean isSeverityOk();

	/**
	 * Returns the severity value stored for the adapted object.
	 * 
	 * @return The stored severity value.
	 * @see org.eclipse.sphinx.emf.validation.markers.ValidationStatusCode
	 */
	public int getSeverity();

}
