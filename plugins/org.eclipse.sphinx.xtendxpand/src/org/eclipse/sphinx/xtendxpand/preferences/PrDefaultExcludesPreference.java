package org.eclipse.sphinx.xtendxpand.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.platform.preferences.AbstractProjectWorkspacePreference;
import org.eclipse.sphinx.xtendxpand.internal.preferences.OutletsPreferenceInitializer;
import org.eclipse.sphinx.xtendxpand.util.XtendXpandUtil;

public class PrDefaultExcludesPreference extends AbstractProjectWorkspacePreference<Boolean> {

	/**
	 * Default instance of {@link PrExcludesPreference}.
	 */
	public static final PrDefaultExcludesPreference INSTANCE = new PrDefaultExcludesPreference(XtendXpandUtil.XTEND_XPAND_NATURE_ID,
			OutletsPreferenceInitializer.QUALIFIER, OutletsPreferenceInitializer.PREF_PR_DEFAULT_EXCLUDES,
			OutletsPreferenceInitializer.PREF_PR_DEFAULT_EXCLUDES_DEFAULT);

	public PrDefaultExcludesPreference(String requiredProjectNatureId, String qualifier, String key, boolean defaultValue) {
		super(requiredProjectNatureId, qualifier, key, Boolean.toString(defaultValue));
	}

	@Override
	protected Boolean toObject(IProject project, String valueAsString) {
		return valueAsString == null ? OutletsPreferenceInitializer.PREF_PR_DEFAULT_EXCLUDES_DEFAULT : Boolean.parseBoolean(valueAsString);
	}

	@Override
	protected String toString(IProject project, Boolean valueAsObject) {
		return valueAsObject.toString();
	}
}
