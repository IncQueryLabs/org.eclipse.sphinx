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
package org.eclipse.sphinx.examples.hummingbird.ide.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.examples.hummingbird.ide.internal.Activator;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * Initializes the Hummingbird preferences with default values.
 */
public class HummingbirdPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * The qualifier which is commonly used for all Hummingbird preferences.
	 */
	public static final String QUALIFIER = Activator.getPlugin().getSymbolicName();

	/**
	 * The key for Hummingbird metamodel version preference.
	 */
	public static final String PREF_METAMODEL_VERSION = "hummingbird.metamodel.version"; //$NON-NLS-1$

	/**
	 * The default value for {@link #PREF_METAMODEL_VERSION}.
	 */
	public static final String PREF_METAMODEL_VERSION_DEFAULT = getMetamodelVersionDefault();

	/**
	 * The minor release/revision used when saving Hummingbird XMI files
	 */
	public static final String PREF_RESOURCE_VERSION = "hummingbird.resource.version"; //$NON-NLS-1$

	/**
	 * The minor release/revision used when saving Hummingbird XMI files
	 */
	public static final String PREF_RESOURCE_VERSION_DEFAULT = "same.as.in.original.resource"; //$NON-NLS-1$

	/*
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaultPreferences = getDefaultPreferences();
		if (defaultPreferences == null) {
			RuntimeException ex = new RuntimeException("Failed to retrieve default preferences for '" + QUALIFIER + "'."); //$NON-NLS-1$ //$NON-NLS-2$
			PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
		}

		defaultPreferences.put(PREF_METAMODEL_VERSION, PREF_METAMODEL_VERSION_DEFAULT);
		defaultPreferences.put(PREF_RESOURCE_VERSION, PREF_RESOURCE_VERSION_DEFAULT);
	}

	/**
	 * Returns the {@link IEclipsePreferences default preference} for {@link HummingbirdPreferenceInitializer#QUALIFIER}
	 * .
	 * 
	 * @return The {@link IEclipsePreferences default preferences} for
	 *         {@link HummingbirdPreferenceInitializer#QUALIFIER} or <code>null</code> if no such could be determined.
	 */
	private IEclipsePreferences getDefaultPreferences() {
		DefaultScope defaultScope = new DefaultScope();
		return defaultScope.getNode(QUALIFIER);
	}

	/**
	 * Returns the id of the most recent {@link HummingbirdMMDescriptor Hummingbird metamodel descriptor} as default.
	 * 
	 * @return The default value for {@link #PREF_METAMODEL_VERSION} or an empty string if no such could be determined.
	 */
	private static String getMetamodelVersionDefault() {
		IMetaModelDescriptor mostRecentMMDescriptor = null;
		for (IMetaModelDescriptor mmDescriptor : MetaModelDescriptorRegistry.INSTANCE.getDescriptors(HummingbirdMMDescriptor.INSTANCE)) {
			if (mostRecentMMDescriptor == null || mostRecentMMDescriptor.getOrdinal() < mmDescriptor.getOrdinal()) {
				mostRecentMMDescriptor = mmDescriptor;
			}
		}
		return mostRecentMMDescriptor != null ? mostRecentMMDescriptor.getIdentifier() : ""; //$NON-NLS-1$
	}
}
