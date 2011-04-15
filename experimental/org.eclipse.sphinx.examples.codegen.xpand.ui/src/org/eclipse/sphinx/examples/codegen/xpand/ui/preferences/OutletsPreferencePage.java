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
package org.eclipse.sphinx.examples.codegen.xpand.ui.preferences;

import org.eclipse.sphinx.xpand.ui.preferences.AbstractOutletsPreferencePage;

public class OutletsPreferencePage extends AbstractOutletsPreferencePage {

	public static final String PREFERENCE_PAGE_ID = "org.eclipse.sphinx.examples.codegen.xpand.ui.preferencePages.outlets"; //$NON-NLS-1$

	public static final String PROPERTY_PAGE_ID = "org.eclipse.sphinx.examples.codegen.xpand.ui.propertyPages.outlets"; //$NON-NLS-1$

	@Override
	protected String getPreferencePageID() {
		return PREFERENCE_PAGE_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROPERTY_PAGE_ID;
	}
}
