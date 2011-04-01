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
package org.eclipse.sphinx.xpand.ui.groups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.internal.xpand2.ast.AbstractDefinition;
import org.eclipse.internal.xpand2.ast.Template;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.fields.ComboField;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.StringButtonField;
import org.eclipse.sphinx.platform.ui.fields.StringField;
import org.eclipse.sphinx.platform.ui.fields.adapters.IButtonAdapter;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
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
import org.eclipse.xpand2.XpandUtil;
import org.eclipse.xtend.shared.ui.core.IXtendXpandProject;
import org.eclipse.xtend.shared.ui.core.IXtendXpandResource;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.Type;

public class TemplateGroup {

	// Dialog setting
	protected static final String CODE_GEN_SECTION = Activator.getPlugin().getSymbolicName() + ".CODE_GEN_SECTION"; //$NON-NLS-1$
	public static final String STORE_TEMPLATE_PATH = "TEMPLATE_PATH$"; //$NON-NLS-1$
	protected static final String STORE_SELECTED_DEFINE_BLOCK = "SELECTED_DEFINE_BLOCK"; //$NON-NLS-1$

	/**
	 * The name of the template group.
	 */
	protected String groupName;

	/**
	 * The template file path field.
	 */
	protected StringButtonField templatePathField;

	/**
	 * The define block field.
	 */
	protected ComboField definitionComboField;

	/**
	 * The definition name field.
	 */
	protected StringField definitionNameField;

	/**
	 * The selected model object.
	 */
	protected EObject modelObject;

	/**
	 * The metamodel to be use.
	 */
	protected MetaModel metaModel;

	/**
	 * Defined definitions in the template file.
	 */
	private AbstractDefinition[] definitions;

	/**
	 * The dialog settings for this group; <code>null</code> if none.
	 */
	private IDialogSettings dialogSettings = null;

	public TemplateGroup(String groupName, EObject modelObject, MetaModel metaModel) {
		this.groupName = groupName;
		this.modelObject = modelObject;
		this.metaModel = metaModel;
	}

	public void createContent(final Composite parent, int numColumns) {
		final Group templateGroup = new Group(parent, SWT.SHADOW_NONE);
		templateGroup.setText(groupName);
		templateGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		GridLayout templateGroupLayout = new GridLayout();
		templateGroupLayout.numColumns = numColumns;
		templateGroup.setLayout(templateGroupLayout);

		// Template File Path
		templatePathField = new StringButtonField(new IButtonAdapter() {

			public void changeControlPressed(IField field) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(parent.getShell(), new WorkbenchLabelProvider(),
						new WorkbenchContentProvider());
				dialog.setTitle(Messages.label_templateSelection);
				dialog.setMessage(Messages.msg_chooseTemplate);
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
							return XpandUtil.TEMPLATE_EXTENSION.equals(((IFile) element).getFileExtension());
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
							if (selectedFile.exists() && XpandUtil.TEMPLATE_EXTENSION.equals(selectedFile.getFileExtension())) {
								return Status.OK_STATUS;
							}
						}
						return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, Messages.msg_chooseTemplateError, null);
					}
				});
				if (dialog.open() == IDialogConstants.OK_ID) {
					IFile file = (IFile) dialog.getFirstResult();
					if (file != null) {
						templatePathField.setText(file.getFullPath().makeRelative().toString());
						updateDefinitionComboItems(file);
					}
				}
			}
		});
		templatePathField.setButtonLabel(Messages.label_browse);
		templatePathField.setLabelText(Messages.label_templatePath);
		templatePathField.fillIntoGrid(templateGroup, numColumns);
		templatePathField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateDefinitionComboItems(getFile(templatePathField.getText()));
				updateDefinitionaNameField();
				groupChanged(templateGroup);
			}
		});

		// Definition Field
		definitionComboField = new ComboField(true);
		definitionComboField.setLabelText(Messages.label_defineBlock);
		definitionComboField.fillIntoGrid(templateGroup, numColumns);
		definitionComboField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateDefinitionaNameField();
				groupChanged(templateGroup);
			}
		});

		// Feedback
		definitionNameField = new StringField();
		definitionNameField.setLabelText(Messages.label_definitionName);
		definitionNameField.setEditable(false);
		definitionNameField.fillIntoGrid(templateGroup, numColumns);

		loadGroupSettings();
	}

	/**
	 * This method should be overriding for instance by wizards that contain the output group field for example to
	 * adjust the enable state of the Back, Next, and Finish buttons wizard page.
	 */
	protected void groupChanged(Group group) {
		// Do nothing by default.
	}

	/**
	 * Updates items of define block field after loading selected template file.
	 */
	public void updateDefinitionComboItems(IFile templateFile) {
		Template template = loadTemplate(templateFile);
		if (template != null) {
			definitions = template.getAllDefinitions();
			definitionComboField.setItems(createDefineBlockItems(definitions));
			return;
		}
		definitionComboField.setItems(new String[0]);
	}

	/**
	 * Creates define block items.
	 */
	protected String[] createDefineBlockItems(AbstractDefinition[] definitions) {
		List<String> result = new ArrayList<String>();
		if (metaModel != null) {
			Type type = metaModel.getType(modelObject);
			if (type != null) {
				for (AbstractDefinition definition : definitions) {
					if (type.getName().equals(definition.getTargetType()) || getSimpleTypeName(type).equals(definition.getTargetType())) {
						result.add(definition.getName());
					}
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets the simple name of the given <code>type</type>.
	 */
	protected String getSimpleTypeName(Type type) {
		String typeName = type.getName();
		String namespaceDelimiter = IXtendXpandConstants.NS_DELIMITER;
		int idx = typeName.lastIndexOf(namespaceDelimiter);
		return idx != -1 && typeName.length() >= idx + namespaceDelimiter.length() ? typeName.substring(idx + namespaceDelimiter.length()) : typeName;
	}

	// TODO File bug to Xpand: org.eclipse.internal.xpand2.ast.AbstractDefinition#getQualifiedName() must not remove the
	// 4 last characters of the definition's file name in hard-coded manner because it might yield the file's base name
	// without extension only.
	public String getQualifiedDefinitionName() {
		String selectedDefinitionName = getSelectedDefinitionComboItem();
		if (selectedDefinitionName != null) {
			return getQualifiedName(getFile(getTemplatePathField().getText()), selectedDefinitionName);
		}
		return ""; //$NON-NLS-1$
	}

	// TODO (aakar) Move to an utility class, used also in IScopResourceLoader
	protected String getQualifiedName(IFile underlyingFile, String statementName) {
		Assert.isNotNull(underlyingFile);

		if (underlyingFile.exists()) {
			StringBuilder path = new StringBuilder();
			IPath templateNamespace = underlyingFile.getProjectRelativePath().removeFileExtension();
			for (Iterator<String> iter = Arrays.asList(templateNamespace.segments()).iterator(); iter.hasNext();) {
				String segment = iter.next();
				path.append(segment);
				if (iter.hasNext()) {
					path.append(IXtendXpandConstants.NS_DELIMITER);
				}
			}
			if (statementName != null && statementName.length() > 0) {
				path.append(IXtendXpandConstants.NS_DELIMITER);
				path.append(statementName);
			}
			return path.toString();
		}
		return null;
	}

	protected void updateDefinitionaNameField() {
		IFile templateFile = getFile(templatePathField.getText());
		if (templateFile != null) {
			definitionNameField.setText(getQualifiedDefinitionName());
		} else {
			definitionNameField.setText("..."); //$NON-NLS-1$
		}
	}

	/**
	 * Loads an Xpand resource.
	 */
	protected Template loadTemplate(final IFile templateFile) {
		if (templateFile != null && templateFile.exists() && XpandUtil.TEMPLATE_EXTENSION.equals(templateFile.getFileExtension())) {
			final IXtendXpandProject project = org.eclipse.xtend.shared.ui.Activator.getExtXptModelManager().findProject(templateFile);
			if (project != null) {
				final IXtendXpandResource resource = project.findXtendXpandResource(templateFile);
				return (Template) resource.getExtXptResource();
			}
		}
		return null;
	}

	public boolean isGroupComplete() {
		IFile templateFile = getFile(getTemplatePathField().getText());
		if (templateFile != null) {
			return templateFile.exists() && getDefinitionComboField().getSelectionIndex() != -1;
		}
		return false;
	}

	/**
	 * Gets the file located at the given full path or returns null.
	 */
	// TODO (aakar) Put this in ExtendedPlatform
	protected IFile getFile(String fullPath) {
		if (fullPath != null && fullPath.length() > 0) {
			Path path = new Path(fullPath);
			if (path.segmentCount() > 1) {
				return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		}
		return null;
	}

	public StringButtonField getTemplatePathField() {
		return templatePathField;
	}

	public ComboField getDefinitionComboField() {
		return definitionComboField;
	}

	public String getSelectedDefinitionComboItem() {
		if (definitionComboField != null && !definitionComboField.getComboControl().isDisposed()) {
			String[] items = definitionComboField.getItems();
			int selectionIndex = definitionComboField.getSelectionIndex();
			if (items.length > 0 && selectionIndex != -1) {
				return items[selectionIndex];
			}
		}
		return null;
	}

	public StringField getDefinitionNameField() {
		return definitionNameField;
	}

	public AbstractDefinition[] getDefinitions() {
		return definitions;
	}

	/**
	 * Loads the template path and the define block from the dialog settings. Must call
	 * {@link #setDialogSettings(IDialogSettings)} before calling this method.
	 */
	protected void loadGroupSettings() {
		String templatePath = getTemplatePathFromDialogSettings();
		if (templatePath != null) {
			templatePathField.setText(templatePath);
			updateDefinitionComboItems(getFile(templatePath));
			String defineBlock = getDefinitionNameFromDialogSettings();
			if (defineBlock != null) {
				definitionComboField.selectItem(defineBlock);
			}
		}
	}

	public String getTemplatePathFromDialogSettings() {
		String result = null;
		String templatePathDialogSettingsKey = getTemplatePathDialogSettingsKey(modelObject);
		IDialogSettings templatePathSection = getTemplatePathSection();
		if (templatePathSection != null) {
			String templatePath = templatePathSection.get(templatePathDialogSettingsKey);
			if (templatePath != null) {
				IFile templateFile = getFile(templatePath);
				if (templateFile != null && templateFile.exists()) {
					result = templatePath;
				}
			}
		}
		return result;
	}

	public String getDefinitionNameFromDialogSettings() {
		String result = null;
		IDialogSettings templatePathSection = getTemplatePathSection();
		if (templatePathSection != null) {
			result = templatePathSection.get(STORE_SELECTED_DEFINE_BLOCK);
		}
		return result;
	}

	protected IDialogSettings getTemplatePathSection() {
		IDialogSettings result = null;
		String templatePathDialogSettingsKey = getTemplatePathDialogSettingsKey(modelObject);
		IDialogSettings section = getDialogSettings().getSection(CODE_GEN_SECTION);
		if (section != null) {
			result = section.getSection(templatePathDialogSettingsKey);
		}
		return result;
	}

	/**
	 * Sets the dialog settings for this group.
	 * <p>
	 * The dialog settings is used to record state between group invocations (i.e. template path and the selected define
	 * block inside this template)
	 * </p>
	 * 
	 * @param settings
	 *            the dialog settings, or <code>null</code> if none
	 * @see #getDialogSettings
	 */
	public void setDialogSettings(IDialogSettings dialogSettings) {
		this.dialogSettings = dialogSettings;
	}

	/**
	 * Returns the dialog settings for this group.
	 * 
	 * @return the dialog settings, or <code>null</code> if none
	 */
	protected IDialogSettings getDialogSettings() {
		return dialogSettings;
	}

	/**
	 * Saves, using the {@link DialogSettings} dialogSettings, the state of the different fields of this group.
	 * 
	 * @param templatePathDialogSettingsKey
	 * @see #setDialogSettings(IDialogSettings)
	 */
	public void saveGroupSettings() {
		IDialogSettings settings = getDialogSettings();
		String templatePathDialogSettingsKey = getTemplatePathDialogSettingsKey(modelObject);
		if (settings != null) {
			IDialogSettings topLevelSection = settings.getSection(CODE_GEN_SECTION);
			if (topLevelSection == null) {
				topLevelSection = settings.addNewSection(CODE_GEN_SECTION);
			}
			if (templatePathField.getText().trim().length() != 0) {
				IDialogSettings templatePathSection = topLevelSection.getSection(templatePathDialogSettingsKey);
				if (templatePathSection == null) {
					templatePathSection = topLevelSection.addNewSection(templatePathDialogSettingsKey);
				}
				templatePathSection.put(templatePathDialogSettingsKey, templatePathField.getText());
				String[] items = definitionComboField.getItems();
				int selectionIndex = definitionComboField.getSelectionIndex();
				if (items.length > 0 && selectionIndex != -1) {
					templatePathSection.put(STORE_SELECTED_DEFINE_BLOCK, items[selectionIndex]);
				}
			}
		}
	}

	protected String getTemplatePathDialogSettingsKey(EObject object) {
		return TemplateGroup.STORE_TEMPLATE_PATH + object.eResource().getURIFragment(object);
	}
}
