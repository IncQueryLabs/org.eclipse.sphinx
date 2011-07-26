package org.eclipse.sphinx.xtendxpand.ui.groups;

import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.platform.ui.fields.SelectionButtonField;
import org.eclipse.sphinx.platform.ui.fields.StringField;
import org.eclipse.sphinx.platform.ui.groups.AbstractGroup;
import org.eclipse.sphinx.xtendxpand.preferences.PrDefaultExcludesPreference;
import org.eclipse.sphinx.xtendxpand.preferences.PrExcludesPreference;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ProtectedRegionGroup extends AbstractGroup {

	private IProject project;

	private StringField prExcludesField;
	private SelectionButtonField prDefaultExcludesField;

	public ProtectedRegionGroup(String groupName, IProject project) {
		super(groupName);
		this.project = project;
	}

	@Override
	protected void doCreateContent(Composite parent, int numColumns) {
		parent.setLayout(new GridLayout(numColumns, false));

		prExcludesField = new StringField();
		prExcludesField.setLabelText(Messages.label_prExcludes);
		prExcludesField.fillIntoGrid(parent, 3);
		prExcludesField.setTextWithoutUpdate(PrExcludesPreference.INSTANCE.get(project));
		prExcludesField.setToolTipText(Messages.tooltip_prExcludesField);

		prDefaultExcludesField = new SelectionButtonField(SWT.CHECK);
		prDefaultExcludesField.setLabelText(Messages.label_prDefaultExcludes);
		prDefaultExcludesField.fillIntoGrid(parent, 3);
		prDefaultExcludesField.setSelectionWithoutEvent(PrDefaultExcludesPreference.INSTANCE.get(project));
		prDefaultExcludesField.setToolTipText(Messages.tooltip_prDefaultExcludes);
	}

	public void setToDefault() {
		prExcludesField.setTextWithoutUpdate(PrExcludesPreference.INSTANCE.getDefaultValueAsObject());
		prDefaultExcludesField.setSelectionWithoutEvent(PrDefaultExcludesPreference.INSTANCE.getDefaultValueAsObject());
	}

	public void store() {
		PrExcludesPreference.INSTANCE.setInProject(project, prExcludesField.getText());
		PrDefaultExcludesPreference.INSTANCE.setInProject(project, prDefaultExcludesField.isSelected());
	}

	public void dispose() {
		prExcludesField.dispose();
		prDefaultExcludesField.dispose();
	}
}
