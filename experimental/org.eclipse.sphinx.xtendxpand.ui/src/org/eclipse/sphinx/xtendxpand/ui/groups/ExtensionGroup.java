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
package org.eclipse.sphinx.xtendxpand.ui.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.internal.xtend.xtend.ast.Extension;
import org.eclipse.internal.xtend.xtend.ast.ExtensionFile;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.fields.ComboField;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.StringButtonField;
import org.eclipse.sphinx.platform.ui.fields.adapters.IButtonAdapter;
import org.eclipse.sphinx.platform.ui.groups.AbstractGroup;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.xtend.XtendEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.ui.internal.Activator;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.xtend.shared.ui.core.IXtendXpandProject;
import org.eclipse.xtend.shared.ui.core.IXtendXpandResource;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.Type;

public class ExtensionGroup extends AbstractGroup {

	/**
	 * The extension group dialog settings.
	 */
	protected static final String M2M_TRANSFORM_SECTION = Activator.getPlugin().getSymbolicName() + ".M2M_TRANSFORM_SECTION"; //$NON-NLS-1$
	protected static final String STORE_EXTENSION_PATH = "EXTENSION_PATH$"; //$NON-NLS-1$
	protected static final String STORE_SELECTED_EXTENSION_BLOCK = "SELECTED_EXTENSION_BLOCK"; //$NON-NLS-1$

	/**
	 * The Xtend file path field.
	 */
	protected StringButtonField extensionPathField;

	/**
	 * The extension to be used in the relevant Xtend file.
	 */
	protected ComboField extensionNameField;

	/**
	 * The selected model object.
	 */
	protected EObject modelObject;

	/**
	 * The metamodel to be use.
	 */
	protected MetaModel metaModel;

	/**
	 * Defined extensions in the relevant Xtend file.
	 */
	private List<Extension> extensions;

	public ExtensionGroup(String groupName, EObject modelObject, MetaModel metaModel) {
		this(groupName, modelObject, metaModel, null);
	}

	public ExtensionGroup(String groupName, EObject modelObject, MetaModel metaModel, IDialogSettings dialogSettings) {
		super(groupName, dialogSettings);

		this.modelObject = modelObject;
		this.metaModel = metaModel;
	}

	@Override
	protected void doCreateContent(final Composite parent, int numColumns) {
		parent.setLayout(new GridLayout(numColumns, false));

		// Extension File Path
		extensionPathField = new StringButtonField(new IButtonAdapter() {

			public void changeControlPressed(IField field) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(parent.getShell(), new WorkbenchLabelProvider(),
						new WorkbenchContentProvider());
				dialog.setTitle(Messages.label_extensionSelection);
				dialog.setMessage(Messages.msg_chooseExtension);
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
				IFile modelFile = EcorePlatformUtil.getFile(modelObject);
				if (modelFile != null) {
					dialog.setInitialSelection(modelFile.getProject());
				}
				dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
				dialog.addFilter(new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						if (element instanceof IFile) {
							return IXtendXpandConstants.EXTENSION_EXTENSION.equals(((IFile) element).getFileExtension());
						}
						if (element instanceof IResource) {
							return !ExtendedPlatform.isPlatformPrivateResource(((IResource) element));
						}
						return true;
					}
				});
				dialog.setValidator(new ISelectionStatusValidator() {

					public IStatus validate(Object[] selection) {
						int nSelected = selection.length;
						String pluginId = Activator.getPlugin().getSymbolicName();
						if (nSelected == 1 && selection[0] instanceof IFile) {
							IFile selectedFile = (IFile) selection[0];
							if (selectedFile.exists() && IXtendXpandConstants.EXTENSION_EXTENSION.equals(selectedFile.getFileExtension())) {
								return Status.OK_STATUS;
							}
						}
						return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, Messages.msg_chooseExtensionError, null);
					}
				});
				if (dialog.open() == IDialogConstants.OK_ID) {
					IFile file = (IFile) dialog.getFirstResult();
					if (file != null) {
						extensionPathField.setText(file.getFullPath().makeRelative().toString());
						updateExtensionNameComboItems(file);
					}
				}
			}
		});
		extensionPathField.setButtonLabel(Messages.label_browse);
		extensionPathField.setLabelText(Messages.label_extensionPath);
		extensionPathField.fillIntoGrid(parent, numColumns);
		extensionPathField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateExtensionNameComboItems(getFile(extensionPathField.getText()));
				notifyGroupChanged(extensionPathField);
			}
		});

		// Extension Field
		extensionNameField = new ComboField(true);
		extensionNameField.setLabelText(Messages.label_extensionName);
		extensionNameField.fillIntoGrid(parent, numColumns);
		extensionNameField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				notifyGroupChanged(extensionPathField);
			}
		});

		// Load the group settings.
		loadGroupSettings();
	}

	/**
	 * Updates items of extensions combo field after loading selected Xtend file.
	 */
	public void updateExtensionNameComboItems(IFile templateFile) {
		ExtensionFile extensionFile = loadExtensionFile(templateFile);
		if (extensionFile != null) {
			extensions = extensionFile.getExtensions();
			extensionNameField.setItems(createExtensionNameComboItems(extensions));
			return;
		}
		extensionNameField.setItems(new String[0]);
	}

	/**
	 * Creates extensions fields items.
	 */
	protected String[] createExtensionNameComboItems(List<Extension> extensions) {
		List<String> result = new ArrayList<String>();
		if (metaModel != null) {
			Type type = metaModel.getType(modelObject);
			if (type != null) {
				for (Extension extension : extensions) {
					result.add(extension.getQualifiedName());
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public String getExtensionName() {
		String selectedExtensionName = getSelectedExtensionNameComboItem();
		if (selectedExtensionName != null) {
			return selectedExtensionName;
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Loads an Xtend resource.
	 */
	protected ExtensionFile loadExtensionFile(final IFile templateFile) {
		if (templateFile != null && templateFile.exists() && IXtendXpandConstants.EXTENSION_EXTENSION.equals(templateFile.getFileExtension())) {
			final IXtendXpandProject project = org.eclipse.xtend.shared.ui.Activator.getExtXptModelManager().findProject(templateFile);
			if (project != null) {
				final IXtendXpandResource resource = project.findXtendXpandResource(templateFile);
				return (ExtensionFile) resource.getExtXptResource();
			}
		}
		return null;
	}

	@Override
	public boolean isGroupComplete() {
		IFile templateFile = getFile(getExtensionPathField().getText());
		if (templateFile != null) {
			return templateFile.exists() && getExtensionNameField().getSelectionIndex() != -1;
		}
		return false;
	}

	/**
	 * Gets the file located at the given full path or returns null.
	 */
	protected IFile getFile(String fullPath) {
		if (fullPath != null && fullPath.length() > 0) {
			Path path = new Path(fullPath);
			if (path.segmentCount() > 1) {
				return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		}
		return null;
	}

	public String getSelectedExtensionNameComboItem() {
		if (extensionNameField != null && !extensionNameField.getComboControl().isDisposed()) {
			String[] items = extensionNameField.getItems();
			int selectionIndex = extensionNameField.getSelectionIndex();
			if (items.length > 0 && selectionIndex != -1) {
				return items[selectionIndex];
			}
		}
		return null;
	}

	public StringButtonField getExtensionPathField() {
		return extensionPathField;
	}

	public ComboField getExtensionNameField() {
		return extensionNameField;
	}

	public List<Extension> getExtensions() {
		return extensions;
	}

	public Collection<XtendEvaluationRequest> getXtendEvaluationRequests() {
		List<XtendEvaluationRequest> requests = new ArrayList<XtendEvaluationRequest>();
		if (modelObject != null) {
			String definitionName = getExtensionName();
			if (definitionName != null && definitionName.length() > 0) {
				requests.add(new XtendEvaluationRequest(definitionName, modelObject));
			}
		}
		return requests;
	}

	/**
	 * Loads the template path and the define block from the dialog settings. Must call
	 * {@link #setDialogSettings(IDialogSettings)} before calling this method.
	 */
	@Override
	protected void loadGroupSettings() {
		String templatePath = getExtensionPathFromDialogSettings();
		if (templatePath != null) {
			extensionPathField.setText(templatePath);
			updateExtensionNameComboItems(getFile(templatePath));
			String defineBlock = getExtensionNameFromDialogSettings();
			if (defineBlock != null) {
				extensionNameField.selectItem(defineBlock);
			}
		}
	}

	public String getExtensionPathFromDialogSettings() {
		String result = null;
		String extensionPathDialogSettingsKey = getExtensionPathDialogSettingsKey(modelObject);
		IDialogSettings extensionPathSection = getExtensionPathSection();
		if (extensionPathSection != null) {
			String extensionPath = extensionPathSection.get(extensionPathDialogSettingsKey);
			if (extensionPath != null) {
				IFile extensionFile = getFile(extensionPath);
				if (extensionFile != null && extensionFile.exists()) {
					result = extensionPath;
				}
			}
		}
		return result;
	}

	public String getExtensionNameFromDialogSettings() {
		String result = null;
		IDialogSettings extensionPathSection = getExtensionPathSection();
		if (extensionPathSection != null) {
			result = extensionPathSection.get(STORE_SELECTED_EXTENSION_BLOCK);
		}
		return result;
	}

	protected IDialogSettings getExtensionPathSection() {
		IDialogSettings result = null;
		String extensionPathDialogSettingsKey = getExtensionPathDialogSettingsKey(modelObject);
		IDialogSettings section = getDialogSettings().getSection(M2M_TRANSFORM_SECTION);
		if (section != null) {
			result = section.getSection(extensionPathDialogSettingsKey);
		}
		return result;
	}

	/**
	 * Saves, using the {@link DialogSettings} dialogSettings, the state of the different fields of this group.
	 * 
	 * @param templatePathDialogSettingsKey
	 * @see #setDialogSettings(IDialogSettings)
	 */
	@Override
	public void saveGroupSettings() {
		IDialogSettings settings = getDialogSettings();
		String extensionPathDialogSettingsKey = getExtensionPathDialogSettingsKey(modelObject);
		if (settings != null) {
			IDialogSettings topLevelSection = settings.getSection(M2M_TRANSFORM_SECTION);
			if (topLevelSection == null) {
				topLevelSection = settings.addNewSection(M2M_TRANSFORM_SECTION);
			}
			if (extensionPathField.getText().trim().length() != 0) {
				IDialogSettings extensionPathSection = topLevelSection.getSection(extensionPathDialogSettingsKey);
				if (extensionPathSection == null) {
					extensionPathSection = topLevelSection.addNewSection(extensionPathDialogSettingsKey);
				}
				extensionPathSection.put(extensionPathDialogSettingsKey, extensionPathField.getText());
				String[] items = extensionNameField.getItems();
				int selectionIndex = extensionNameField.getSelectionIndex();
				if (items.length > 0 && selectionIndex != -1) {
					extensionPathSection.put(STORE_SELECTED_EXTENSION_BLOCK, items[selectionIndex]);
				}
			}
		}
	}

	protected String getExtensionPathDialogSettingsKey(EObject object) {
		Assert.isNotNull(object);

		URI uri;
		ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(object.eResource());
		if (extendedResource != null) {
			uri = extendedResource.getURI(object);
		} else {
			uri = EcoreUtil.getURI(object);
		}

		return ExtensionGroup.STORE_EXTENSION_PATH + uri.toString();
	}
}
