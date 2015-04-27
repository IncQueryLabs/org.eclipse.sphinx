package org.eclipse.sphinx.emf.workspace.ui.internal.views;

import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.ui.views.ReferencesView;

public class ToggleReferencesModeAction extends Action {

	private ReferencesView referencesView;
	private int mode;

	public ToggleReferencesModeAction(ReferencesView referencesView, int mode) {
		super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		if (mode == ReferencesView.REFERENCED_OBJECTS_MODE) {
			setText(Messages.action_toggleReferencesMode_referenced_label);
			setToolTipText(Messages.action_toggleReferencesMode_referenced_toolTip);
			setDescription(Messages.action_toggleReferencesMode_referenced_description);
			Activator.getPlugin().setLocalImageDescriptors(this, "rv_references.gif"); //$NON-NLS-1$

		} else if (mode == ReferencesView.REFERENCING_OBJECTS_MODE) {
			setText(Messages.action_toggleReferencesMode_referencing_label);
			setToolTipText(Messages.action_toggleReferencesMode_referencing_toolTip);
			setDescription(Messages.action_toggleReferencesMode_referencing_description);
			Activator.getPlugin().setLocalImageDescriptors(this, "rv_referents.gif"); //$NON-NLS-1$

		} else {
			throw new RuntimeException(NLS.bind(Messages.error_UnsupportedMode, mode));
		}
		this.referencesView = referencesView;
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	@Override
	public void run() {
		referencesView.setMode(mode);
	}
}
