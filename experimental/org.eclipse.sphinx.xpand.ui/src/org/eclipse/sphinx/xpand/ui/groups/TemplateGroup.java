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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.internal.xpand2.ast.AbstractDefinition;
import org.eclipse.internal.xpand2.ast.Template;
import org.eclipse.internal.xpand2.model.XpandDefinition;
import org.eclipse.jface.dialogs.IDialogConstants;
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
	protected ComboField defineBlockField;

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

	public TemplateGroup(Composite parent, String groupName, int numColumns, EObject modelObject, MetaModel metaModel) {
		this(groupName, modelObject, metaModel);
		createContent(parent, numColumns);
	}

	private TemplateGroup(String groupName, EObject modelObject, MetaModel metaModel) {
		this.groupName = groupName;
		this.modelObject = modelObject;
		this.metaModel = metaModel;
	}

	protected void createContent(final Composite parent, int numColumns) {
		Group templateGroup = new Group(parent, SWT.SHADOW_NONE);
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
						updateDefineBlockItems(file);
					}
				}
			}
		});
		templatePathField.setButtonLabel(Messages.label_browse);
		templatePathField.setLabelText(Messages.label_templatePath);
		templatePathField.fillIntoGrid(templateGroup, numColumns);
		templatePathField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateDefineBlockItems(getFile(templatePathField.getText()));
				updateDefinitionaNameField();
			}
		});

		// Define Block
		defineBlockField = new ComboField(true);
		defineBlockField.setLabelText(Messages.label_defineBlock);
		defineBlockField.fillIntoGrid(templateGroup, numColumns);
		defineBlockField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateDefinitionaNameField();
			}
		});

		// Feedback
		definitionNameField = new StringField();
		definitionNameField.setLabelText(Messages.label_definitionName);
		definitionNameField.setEditable(false);
		definitionNameField.fillIntoGrid(templateGroup, numColumns);
	}

	/**
	 * Updates items of define block field after loading selected template file.
	 */
	public void updateDefineBlockItems(IFile templateFile) {
		Template template = loadTemplate(templateFile);
		if (template != null) {
			definitions = template.getAllDefinitions();
			defineBlockField.setItems(createDefineBlockItems(definitions));
			return;
		}
		defineBlockField.setItems(new String[0]);
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

	public String getDefinitionName() {
		if (defineBlockField.getSelectionIndex() != -1) {
			String definitionName = defineBlockField.getItems()[defineBlockField.getSelectionIndex()];
			for (AbstractDefinition definition : definitions) {
				if (definitionName.equals(definition.getName())) {
					return getQualifiedDefinitionName(definition);
				}
			}
		}
		return ""; //$NON-NLS-1$
	}

	// TODO File bug to Xpand: org.eclipse.internal.xpand2.ast.AbstractDefinition#getQualifiedName() must not remove the
	// 4 last characters of the definition's file name in hard-coded manner because it might yield the file's base name
	// without extension only.
	protected String getQualifiedDefinitionName(XpandDefinition definition) {
		String fileName = definition.getFileName();
		if (fileName != null) {
			String prefix = fileName.replaceAll("/", IXtendXpandConstants.NS_DELIMITER); //$NON-NLS-1$ 
			if (prefix.endsWith(XpandUtil.TEMPLATE_EXTENSION)) {
				prefix = prefix.substring(0, prefix.length() - XpandUtil.TEMPLATE_EXTENSION.length());
			}
			return prefix + IXtendXpandConstants.NS_DELIMITER + definition.getName();
		}
		return definition.getName();
	}

	protected void updateDefinitionaNameField() {
		IFile templateFile = getFile(templatePathField.getText());
		if (templateFile != null) {
			definitionNameField.setText(getDefinitionName());
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

	public StringButtonField getTemplatePathField() {
		return templatePathField;
	}

	public ComboField getDefineBlockField() {
		return defineBlockField;
	}

	public StringField getDefinitionNameField() {
		return definitionNameField;
	}

	public AbstractDefinition[] getDefinitions() {
		return definitions;
	}

}
