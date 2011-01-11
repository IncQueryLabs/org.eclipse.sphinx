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
 * {@link IWorkspacePreference} implementation for String-typed preferences.
 */
public class StringWorkspacePreference extends AbstractWorkspacePreference<String> {

	public StringWorkspacePreference(String qualifier, String key, String defaultValue) {
		super(qualifier, key, defaultValue);
	}

	@Override
	protected String toObject(String valueAsString) {
		return valueAsString;
	}
}
