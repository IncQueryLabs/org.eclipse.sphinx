package org.eclipse.sphinx.xtendxpand.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.platform.preferences.AbstractProjectWorkspacePreference;
import org.eclipse.sphinx.xtendxpand.internal.preferences.OutletsPreferenceInitializer;
import org.eclipse.sphinx.xtendxpand.util.XtendXpandUtil;

public class PrExcludesPreference extends AbstractProjectWorkspacePreference<String> {

	/**
	 * Default instance of {@link PrExcludesPreference}.
	 */
	public static final PrExcludesPreference INSTANCE = new PrExcludesPreference(XtendXpandUtil.XTEND_XPAND_NATURE_ID,
			OutletsPreferenceInitializer.QUALIFIER, OutletsPreferenceInitializer.PREF_PR_EXCLUDES,
			OutletsPreferenceInitializer.PREF_PR_EXCLUDES_DEFAULT);

	public PrExcludesPreference(String requiredProjectNatureId, String qualifier, String key, String defaultValueAsString) {
		super(requiredProjectNatureId, qualifier, key, defaultValueAsString);
	}

	@Override
	protected String toObject(IProject project, String valueAsString) {
		return valueAsString == null ? "" : valueAsString; //$NON-NLS-1$
	}

	@Override
	protected String toString(IProject project, String valueAsObject) {
		return valueAsObject;
	}
}
