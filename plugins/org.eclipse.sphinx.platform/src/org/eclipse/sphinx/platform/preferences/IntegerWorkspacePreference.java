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
package org.eclipse.sphinx.platform.preferences;

/**
 * {@link IWorkspacePreference} implementation for Integer-typed preferences.
 */
public class IntegerWorkspacePreference extends AbstractWorkspacePreference<Integer> {

	public IntegerWorkspacePreference(String qualifier, String key, Integer defaultValue) {
		super(qualifier, key, Integer.toString(defaultValue));
	}

	@Override
	protected Integer toObject(String valueAsString) {
		return Integer.valueOf(valueAsString);
	}
}
