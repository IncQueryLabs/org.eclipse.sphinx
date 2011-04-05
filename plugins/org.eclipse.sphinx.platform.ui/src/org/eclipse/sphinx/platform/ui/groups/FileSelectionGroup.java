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
package org.eclipse.sphinx.platform.ui.groups;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.ListButtonsField;
import org.eclipse.sphinx.platform.ui.fields.ListField;
import org.eclipse.sphinx.platform.ui.fields.SelectionButtonField;
import org.eclipse.sphinx.platform.ui.fields.adapters.IListAdapter;
import org.eclipse.sphinx.platform.ui.groups.messages.Messages;
import org.eclipse.sphinx.platform.ui.internal.Activator;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

public class FileSelectionGroup extends AbstractGroup {

	// Dialog setting
	protected static final String SECTION_NAME = Activator.getDefault().getBundle().getSymbolicName() + ".SECTION"; //$NON-NLS-1$
	protected static final String STORE_SELECTED_CHECK_FILES = "SELECTED_CHECK_FILES"; //$NON-NLS-1$
	protected static final String STORE_ENABLE_BUTTON = "ENABLE_BUTTON"; //$NON-NLS-1$

	protected ListButtonsField fileListField;
	protected SelectionButtonField enableButton;
	protected Set<IFile> selectedFiles = new HashSet<IFile>();

	protected String enableText;
	protected String fileListLabel;
	protected String fileExtension;
	protected IProject project;

	public FileSelectionGroup(String groupName, String enableText, String fileListLabel, String fileExtension, IProject project) {
		this(groupName, enableText, fileListLabel, fileExtension, project, null);
	}

	public FileSelectionGroup(String groupName, String enableText, String fileListLabel, String fileExtension, IProject project,
			IDialogSettings dialogSettings) {
		super(groupName, dialogSettings);

		this.enableText = enableText;
		this.fileListLabel = fileListLabel;
		this.fileExtension = fileExtension;
		this.project = project;
	}

	@Override
	protected void doCreateContent(Composite parent, final int numColumns) {
		final Group fileSelectionGroup = new Group(parent, SWT.SHADOW_NONE);
		fileSelectionGroup.setText(groupName);
		fileSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		GridLayout fileSelectionGroupLayout = new GridLayout();
		fileSelectionGroupLayout.numColumns = numColumns;
		fileSelectionGroup.setLayout(fileSelectionGroupLayout);
		if (enableText != null && enableText.length() > 0) {
			enableButton = new SelectionButtonField(SWT.CHECK);
		}
		if (enableText != null && enableText.length() > 0) {
			enableButton.setLabelText(enableText);
			enableButton.setSelection(false);
			enableButton.fillIntoGrid(fileSelectionGroup, numColumns);
			enableButton.addFieldListener(new IFieldListener() {
				public void dialogFieldChanged(IField field) {
					updateFileSelectionEnableState(enableButton.isSelected());
				}
			});
		}

		fileListField = new ListButtonsField(createListAdapter(parent), new String[] { Messages.label_AddButton, Messages.label_RemoveButton },
				new LabelProvider()) {
			@Override
			public int getNumberOfControls() {
				return numColumns;
			}
		};
		fileListField.setRemoveButtonIndex(1);
		fileListField.fillIntoGrid(fileSelectionGroup, numColumns);
		// Add label to fileListField if no button
		if (enableButton == null) {
			fileListField.setLabelText(fileListLabel);
			updateFileSelectionEnableState(true);
		}

		// Load Dialog Settings
		loadGroupSettings();
	}

	public void updateFileSelectionEnableState(boolean enabled) {
		if (fileListField != null) {
			fileListField.setEnabled(enabled);
		}
	}

	private IListAdapter createListAdapter(final Composite parent) {
		return new IListAdapter() {

			public void selectionChanged(ListField field) {
				// Do nothing.
			}

			public void doubleClicked(ListField field) {
				// Do nothing.
			}

			public void customButtonPressed(ListField field, int index) {
				if (index == 0) {
					selectCheckFiles(parent);
				}
			}
		};
	}

	private void selectCheckFiles(Composite parent) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(parent.getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle(Messages.title_fileSelection);
		dialog.setMessage(NLS.bind(Messages.desc_fileSelection, groupName));
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(true);
		if (project != null) {
			dialog.setInitialSelection(project);
		}
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IFile) {
					return fileExtension.equals(((IFile) element).getFileExtension());
				}
				if (element instanceof IResource) {
					return !ExtendedPlatform.isPlatformPrivateResource(((IResource) element));
				}
				return true;
			}
		});
		dialog.setValidator(new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				String pluginId = Activator.getDefault().getBundle().getSymbolicName();
				for (Object file : selection) {
					if (!(file instanceof IFile) || ((IFile) file).exists() && !fileExtension.equals(((IFile) file).getFileExtension())) {
						return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, NLS.bind(Messages.msg_fileSelectionError, groupName), null);
					}
				}
				return Status.OK_STATUS;
			}
		});

		if (dialog.open() == IDialogConstants.OK_ID) {
			for (Object object : dialog.getResult()) {
				addFile((IFile) object);
			}
		}
	}

	public void addFile(IFile file) {
		Assert.isNotNull(file);

		if (selectedFiles != null && fileListField != null) {
			selectedFiles.add(file);
			fileListField.addElement(file.getName());
		}
	}

	@Override
	public void saveGroupSettings() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			IDialogSettings section = settings.getSection(SECTION_NAME);
			if (section == null) {
				section = settings.addNewSection(SECTION_NAME);
			}
			Collection<IFile> files = getFiles();
			String[] items = new String[files.size()];
			int i = 0;
			for (IFile file : files) {
				items[i] = file.getFullPath().makeRelative().toString();
				i++;
			}
			section.put(STORE_SELECTED_CHECK_FILES, items);
			section.put(STORE_ENABLE_BUTTON, getEnableButtonState());
		}
	}

	@Override
	protected void loadGroupSettings() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			IDialogSettings section = settings.getSection(SECTION_NAME);
			if (section != null) {
				String[] items = section.getArray(STORE_SELECTED_CHECK_FILES);
				boolean enableCheck = section.getBoolean(STORE_ENABLE_BUTTON);
				if (items != null) {
					setEnabledButtonSelection(enableCheck);
					for (String fullPath : items) {
						IFile file = getFile(fullPath);
						if (file != null) {
							addFile(file);
						}
					}
					updateFileSelectionEnableState(enableCheck);
				}
			}
		}
	}

	protected IFile getFile(String fullPath) {
		if (fullPath != null && fullPath.length() > 0) {
			Path path = new Path(fullPath);
			if (path.segmentCount() > 1) {
				return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		}
		return null;
	}

	public Collection<IFile> getFiles() {
		return selectedFiles;
	}

	public boolean getEnableButtonState() {
		if (enableButton != null) {
			return enableButton.isSelected();
		}
		return false;
	}

	public void setEnabledButtonSelection(boolean selected) {
		if (enableButton != null) {
			enableButton.setSelection(selected);
		}
	}
}
