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
package org.eclipse.sphinx.xpand.ui.dialogs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.providers.OutletProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.xpand2.output.Outlet;

public class EditOutletDialog extends StatusDialog {

	private ExtendedOutlet outlet;
	private OutletProvider outletProvider;
	private String initialOutletName;
	private boolean editableName;

	private Text nameText;
	private Text locationText;

	private Button workspaceBrowse;
	private Button fileBrowse;
	private Button variables;

	protected Listener listener = new Listener();

	public EditOutletDialog(Shell parent, ExtendedOutlet outlet, boolean edit, boolean editableName, OutletProvider outletProvider) {
		super(parent);
		String title = edit ? "Edit Outlet" : "New Outlet";
		setTitle(title);
		this.outlet = outlet;
		this.editableName = editableName;
		this.outletProvider = outletProvider;
		initialOutletName = outlet.getName();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite ancestor) {
		Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		parent.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(layoutData);

		createLabel(parent, "Name:", 1);
		nameText = createText(parent, GridData.BEGINNING, 1);
		nameText.setEditable(editableName);
		if (editableName) {
			if (outlet.getName() != null) {
				nameText.setText(outlet.getName());
			}
			nameText.addModifyListener(listener);
		} else {
			nameText.setText("<default>");
		}

		createLabel(parent, "Location:", 1);

		locationText = createText(parent, GridData.FILL_HORIZONTAL, 1);
		if (outlet.getPathExpression() != null) {
			locationText.setText(outlet.getPathExpression());
		}
		locationText.addModifyListener(listener);

		createLabel(parent, "", 1); //$NON-NLS-1$

		Composite buttonsParent = new Composite(parent, SWT.None);
		GridLayout blayout = new GridLayout();
		blayout.numColumns = 3;
		buttonsParent.setLayout(blayout);
		GridData bLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonsParent.setLayoutData(bLayoutData);

		workspaceBrowse = createPushButton(buttonsParent, "Workspace...", null);
		workspaceBrowse.addSelectionListener(listener);

		fileBrowse = createPushButton(buttonsParent, "File System...", null);
		fileBrowse.addSelectionListener(listener);

		variables = createPushButton(buttonsParent, "Variables...", null);
		variables.addSelectionListener(listener);
		return parent;
	}

	protected void handleNameChanged() {
		IStatus nameStatus = validateOutletName();
		updateStatus(nameStatus);
		if (nameStatus.isOK()) {
			outlet.setName(nameText.getText());
		}
	}

	protected void handleLocationChanged() {
		IStatus locationStatus = validateOutletLocation();
		updateStatus(locationStatus);
		if (locationStatus.isOK()) {
			String location = locationText.getText();
			outlet.setPathExpression(location, outletProvider.getProject() != null);
		}
	}

	protected void handleBrowseFileSystem() {
		String filePath = locationText.getText();
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		filePath = dialog.open();
		if (filePath != null) {
			locationText.setText(filePath);
		}
	}

	protected void handleBrowseWorkspace() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setTitle("Container Selection");
		dialog.setMessage("Select a container for the generated files:");
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		if (outletProvider.getProject() != null) {
			dialog.setInitialSelection(outletProvider.getProject());
		}
		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource) dialog.getFirstResult();
			if (resource != null) {
				String location;
				IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
				if (resource.getProject() == outletProvider.getProject()) {
					location = variableManager.generateVariableExpression(ExtendedOutlet.VARIABLE_PROJECT_LOC, null) + IPath.SEPARATOR
							+ resource.getProjectRelativePath().toString();
				} else {
					location = variableManager.generateVariableExpression(ExtendedOutlet.VARIABLE_WORKSPACE_LOC, null)
							+ resource.getFullPath().toString();

				}
				locationText.setText(location);
			}
		}
	}

	protected void handleInsertVariable() {
		MessageDialog.openWarning(getShell(), "Select Variable", "Not supported yet!");
	}

	protected IStatus validateOutletName() {
		String name = nameText.getText();
		if (name.trim().length() == 0) {
			return StatusUtil.createStatus(IStatus.ERROR, IStatus.ERROR, "Outlet name must not be empty!", Activator.getPlugin().getSymbolicName(),
					null);
		}
		if (name.equals(initialOutletName)) {
			return Status.OK_STATUS;
		}
		for (Outlet outlet : outletProvider.getOutlets()) {
			if (name.equals(outlet.getName())) {
				return StatusUtil.createStatus(IStatus.ERROR, IStatus.ERROR, "Outlet name already exists!", Activator.getPlugin().getSymbolicName(),
						null);
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Validates that the location is not empty and match some predefined regex. We don't validate the container
	 * existence, it will be created by Xpand during the generation if it does not exist.
	 * 
	 * @return
	 */
	// TODO (aakar) Enhance the validation by using some regex !!
	protected IStatus validateOutletLocation() {
		String location = locationText.getText();
		if (location.trim().length() == 0) {
			return StatusUtil.createStatus(IStatus.ERROR, IStatus.ERROR, "Outlet location must not be empty!", Activator.getPlugin()
					.getSymbolicName(), null);
		}
		return Status.OK_STATUS;
	}

	private Label createLabel(Composite parent, String text, int hspan) {
		Label label = new Label(parent, SWT.NONE);
		label.setFont(parent.getFont());
		label.setText(text);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		gd.grabExcessHorizontalSpace = false;
		label.setLayoutData(gd);
		return label;
	}

	private Button createPushButton(Composite parent, String label, Image image) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null) {
			button.setImage(image);
		}
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);
		setButtonDimensionHint(button);
		return button;
	}

	private Text createText(Composite parent, int style, int hspan) {
		Text text = new Text(parent, SWT.BORDER);
		text.setFont(parent.getFont());
		GridData gd = new GridData(style);
		gd.horizontalSpan = hspan;
		text.setLayoutData(gd);
		return text;
	}

	private void setButtonDimensionHint(Button button) {
		Assert.isNotNull(button);
		Object gd = button.getLayoutData();
		if (gd instanceof GridData) {
			((GridData) gd).widthHint = getButtonWidthHint(button);
			((GridData) gd).horizontalAlignment = GridData.FILL;
		}
	}

	public ExtendedOutlet getOutlet() {
		return outlet;
	}

	@Override
	public void create() {
		super.create();
		// Update initial OK button to be disabled for new Outlet
		updateButtonsEnableState(validateOutletName());
	}

	private int getButtonWidthHint(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	class Listener extends SelectionAdapter implements ModifyListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			Object source = e.getSource();
			if (source == fileBrowse) {
				handleBrowseFileSystem();
			} else if (source == workspaceBrowse) {
				handleBrowseWorkspace();
			} else if (source == variables) {
				handleInsertVariable();
			}
		}

		public void modifyText(ModifyEvent e) {
			Object source = e.getSource();
			if (source == nameText) {
				handleNameChanged();
			} else if (source == locationText) {
				handleLocationChanged();
			}
		}
	}
}
