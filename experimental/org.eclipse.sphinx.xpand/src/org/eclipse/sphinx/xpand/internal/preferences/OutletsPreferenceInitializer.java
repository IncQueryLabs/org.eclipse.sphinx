package org.eclipse.sphinx.xpand.internal.preferences;

import org.artop.ecl.platform.util.PlatformLogUtil;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.sphinx.xpand.internal.Activator;

/**
 * Initializes the Outlets preference with its default value.
 */
public class OutletsPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * The qualifier for the Outlets preference.
	 */
	public static final String QUALIFIER = Activator.getPlugin().getSymbolicName();

	/**
	 * The key for the Outlets preference.
	 */
	public static final String PREF_OUTLETS = "xpand.outlets"; //$NON-NLS-1$

	/**
	 * The default value for the Outlets preference.
	 */
	public static final String PREF_OUTLETS_DEFAULT = "@${project_loc}/gen"; //$NON-NLS-1$

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

		defaultPreferences.put(PREF_OUTLETS, PREF_OUTLETS_DEFAULT);
	}

	/**
	 * Returns the {@link IEclipsePreferences default preference} for {@link OutletsPreferenceInitializer#QUALIFIER}.
	 * 
	 * @return The {@link IEclipsePreferences default preferences} for {@link OutletsPreferenceInitializer#QUALIFIER} or
	 *         <code>null</code> if no such could be determined.
	 */
	private IEclipsePreferences getDefaultPreferences() {
		DefaultScope defaultScope = new DefaultScope();
		return defaultScope.getNode(QUALIFIER);
	}
}
