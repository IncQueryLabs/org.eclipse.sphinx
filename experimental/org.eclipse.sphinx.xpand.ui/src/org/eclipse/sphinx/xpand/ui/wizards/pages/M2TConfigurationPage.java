/**
 * <copyright>
 * 
 * Copyright (c) See4sys and others.
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
package org.eclipse.sphinx.xpand.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.internal.xpand2.ast.AbstractDefinition;
import org.eclipse.internal.xpand2.ast.Template;
import org.eclipse.internal.xpand2.model.XpandDefinition;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.fields.ComboField;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.fields.IFieldListener;
import org.eclipse.sphinx.platform.ui.fields.StringButtonField;
import org.eclipse.sphinx.platform.ui.fields.StringField;
import org.eclipse.sphinx.platform.ui.fields.adapters.IButtonAdapter;
import org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.xpand2.XpandUtil;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xtend.shared.ui.core.IXtendXpandProject;
import org.eclipse.xtend.shared.ui.core.IXtendXpandResource;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.Type;

public class M2TConfigurationPage extends AbstractWizardPage {

	private final String WORKSPACE_SELECTION_DIALOG = Activator.getPlugin().getSymbolicName() + ".WORKSPACE_SELECTION_DIALOG"; //$NON-NLS-1$

	// Dialog setting
	protected static final String CODE_GEN_SECTION = Activator.getPlugin().getSymbolicName() + ".CODE_GEN_SECTION"; //$NON-NLS-1$
	protected static final String STORE_TEMPLATE_PATH = "TEMPLATE_PATH$"; //$NON-NLS-1$
	protected static final String STORE_SELECTED_DEFINE_BLOCK = "SELECTED_DEFINE_BLOCK"; //$NON-NLS-1$

	// Template Group
	protected StringButtonField templatePathField;
	protected ComboField defineBlockField;
	protected StringField definitionNameField;

	// Output Group
	protected Button useDefaultPathButton;
	protected StringButtonField outputPathField;

	protected EObject modelObject;
	protected MetaModel metaModel;
	protected OutletsPreference outletsPreference;
	protected Outlet defaultOutlet;

	private AbstractDefinition[] definitions;

	public M2TConfigurationPage(String pageName) {
		super(pageName);
	}

	public void init(EObject modelObject, MetaModel metaModel, OutletsPreference outletsPreference, Outlet defaultOutlet) {
		this.modelObject = modelObject;
		this.metaModel = metaModel;
		this.outletsPreference = outletsPreference;
		this.defaultOutlet = defaultOutlet;
	}

	@Override
	protected void doCreateControl(Composite parent) {
		initializeDialogUnits(parent);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(layout);

		// Create page content
		createPageContent(composite);
		setControl(composite);
	}

	protected void createPageContent(Composite parent) {
		createTemplateBlock(parent);
		createOutputBlock(parent);
	}

	protected void createTemplateBlock(Composite parent) {
		Group templateGroup = new Group(parent, SWT.SHADOW_NONE);
		templateGroup.setText(Messages.label_template);
		templateGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		GridLayout templateGroupLayout = new GridLayout();
		templateGroupLayout.numColumns = 3;
		templateGroup.setLayout(templateGroupLayout);

		// Template File Path
		templatePathField = new StringButtonField(new IButtonAdapter() {

			public void changeControlPressed(IField field) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
						new WorkbenchContentProvider());
				dialog.setTitle(Messages.label_templateSelection);
				dialog.setMessage(Messages.msg_chooseTemplate);
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
				IFile modelFile = EcorePlatformUtil.getFile(modelObject);
				if (modelFile != null) {
					dialog.setInitialSelection(modelFile.getProject());
				}
				dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
				dialog.setDialogBoundsSettings(getDialogBoundsSettings(WORKSPACE_SELECTION_DIALOG), Dialog.DIALOG_PERSISTSIZE);
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
								return new Status(IStatus.OK, pluginId, IStatus.OK, "", //$NON-NLS-1$
										null);
							}
						}
						return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, "An Xpand template file should be selected !", null);
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
		templatePathField.setButtonLabel("Browse...");
		templatePathField.setLabelText("Template path:");
		templatePathField.fillIntoGrid(templateGroup, 3);
		templatePathField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateDefineBlockItems(findMemberFromText(templatePathField.getText()));
				updateDefinitionaNameField();
				getWizard().getContainer().updateButtons();
			}
		});

		// Define Block
		defineBlockField = new ComboField(true);
		defineBlockField.setLabelText(Messages.label_defineBlock);
		defineBlockField.fillIntoGrid(templateGroup, 3);
		defineBlockField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				updateDefinitionaNameField();
				getWizard().getContainer().updateButtons();
			}
		});

		// Feedback
		definitionNameField = new StringField();
		definitionNameField.setLabelText(Messages.label_definitionName);
		definitionNameField.setEditable(false);
		definitionNameField.fillIntoGrid(templateGroup, 3);

		// Load Dialog Settings
		loadTemplateBlockSettings();
	}

	protected void updateDefineBlockItems(IResource resource) {
		if (resource instanceof IFile) {
			Template template = loadTemplate((IFile) resource);
			if (template != null) {
				definitions = template.getAllDefinitions();
				defineBlockField.setItems(createDefineBlockItems(definitions));
			}
		}
		defineBlockField.setItems(new String[0]);
	}

	protected String[] createDefineBlockItems(AbstractDefinition[] definitions) {
		List<String> result = new ArrayList<String>();
		if (metaModel != null) {
			Type type = metaModel.getType(modelObject);
			if (type != null) {
				for (AbstractDefinition definition : definitions) {
					if (type.getName().equals(definition.getTargetType())) {
						result.add(definition.getName());
					}
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	protected String getDefinitionName() {
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

	protected String getQualifiedDefinitionName(XpandDefinition definition) {
		String fileName = definition.getFileName();
		if (fileName != null) {
			String prefix = fileName.replaceAll("/", "::"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prefix.endsWith(XpandUtil.TEMPLATE_EXTENSION)) {
				prefix = prefix.substring(0, prefix.length() - XpandUtil.TEMPLATE_EXTENSION.length());
			}
			return prefix + "::" + definition.getName(); //$NON-NLS-1$
		}
		return definition.getName();
	}

	protected void updateDefinitionaNameField() {
		IResource templateFile = findMemberFromText(templatePathField.getText());
		if (templateFile != null) {
			definitionNameField.setText(getDefinitionName());
		} else {
			definitionNameField.setText("..."); //$NON-NLS-1$
		}
	}

	// TODO Create link to preference page if outlets preference is configured on wizard; pass outlet preference from
	// wizard to constructor of page and pull the corresponding parts from
	// org.artop.ecuc.gautosar.codegen.xpand.ui.wizards.pages.EcucM2TConfigurationPage.createOutputBlock(Composite)
	// in here for that purpose
	protected void createOutputBlock(Composite parent) {
		Group outputGroup = new Group(parent, SWT.SHADOW_NONE);
		outputGroup.setText(Messages.label_output);
		outputGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		GridLayout outputGroupLayout = new GridLayout();
		outputGroupLayout.numColumns = 3;
		outputGroup.setLayout(outputGroupLayout);

		useDefaultPathButton = new Button(outputGroup, SWT.CHECK);
		useDefaultPathButton.setText(Messages.label_useDefaultPath);
		useDefaultPathButton.setSelection(true);
		GridData butData = new GridData();
		butData.horizontalSpan = 3;
		useDefaultPathButton.setLayoutData(butData);

		useDefaultPathButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnableState(!useDefaultPathButton.getSelection());
			}
		});

		outputPathField = new StringButtonField(new IButtonAdapter() {

			public void changeControlPressed(IField field) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getControl().getShell(), ResourcesPlugin.getWorkspace().getRoot(),
						true, ""); //$NON-NLS-1$
				if (dialog.open() == Window.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 0) {
						return;
					}
					IPath path = (IPath) result[0];
					outputPathField.setText(path.makeRelative().toString());
				}
			}
		});
		outputPathField.setLabelText(Messages.label_path);
		outputPathField.setButtonLabel(Messages.label_browse);
		if (defaultOutlet != null) {
			IContainer defaultOutletContainer = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(defaultOutlet.getPath()));
			if (defaultOutletContainer != null) {
				outputPathField.setText(defaultOutletContainer.getFullPath().makeRelative().toString());
			}
		}
		outputPathField.fillIntoGrid(outputGroup, 3);
		outputPathField.addFieldListener(new IFieldListener() {

			public void dialogFieldChanged(IField field) {
				getWizard().getContainer().updateButtons();
			}
		});
		updateEnableState(!useDefaultPathButton.getSelection());
	}

	protected void updateEnableState(boolean enabled) {
		outputPathField.setEnabled(enabled);
	}

	protected IDialogSettings getDialogBoundsSettings(String id) {
		IDialogSettings settings = getDialogSettings();
		IDialogSettings section = settings.getSection(id);
		if (section == null) {
			section = settings.addNewSection(id);
		}
		return section;
	}

	@Override
	protected String doGetDescription() throws MissingResourceException {
		return Messages.desc_config;
	}

	@Override
	protected String doGetTitle() throws MissingResourceException {
		return Messages.title_launchGen;
	}

	@Override
	protected boolean doIsPageComplete() {
		return isTemplateBlockComplete() && isOutputBlockComplete();

	}

	protected boolean isTemplateBlockComplete() {
		IResource resource = findMemberFromText(templatePathField.getText());
		return resource != null && resource instanceof IFile && defineBlockField.getSelectionIndex() != -1;
	}

	protected IResource findMemberFromText(String text) {
		if (text != null) {
			text = text.startsWith("/") ? text : "/".concat(text); //$NON-NLS-1$ //$NON-NLS-2$
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(text));
			return resource;
		}
		return null;
	}

	protected boolean isOutputBlockComplete() {
		if (!useDefaultPathButton.getSelection()) {
			IResource resource = findMemberFromText(outputPathField.getText());
			return resource != null && resource instanceof IContainer;
		}
		return true;
	}

	@Override
	protected IStatus doValidateRules() {
		return null;
	}

	/**
	 * Loads an Xpand resource.
	 */
	protected Template loadTemplate(final IFile templateFile) {
		if (templateFile.exists() && XpandUtil.TEMPLATE_EXTENSION.equals(templateFile.getFileExtension())) {
			final IXtendXpandProject project = org.eclipse.xtend.shared.ui.Activator.getExtXptModelManager().findProject(templateFile);
			if (project != null) {
				final IXtendXpandResource resource = project.findXtendXpandResource(templateFile);
				return (Template) resource.getExtXptResource();
			}
		}
		return null;
	}

	public Collection<XpandEvaluationRequest> getXpandEvaluationRequests() {
		List<XpandEvaluationRequest> requests = new ArrayList<XpandEvaluationRequest>();
		if (modelObject != null) {
			requests.add(new XpandEvaluationRequest(getDefinitionName(), modelObject));
		}
		return requests;
	}

	public Collection<Outlet> getOutlets() {
		if (outletsPreference != null) {
			IFile file = EcorePlatformUtil.getFile(modelObject);
			if (file != null && file.getProject() != null) {
				return new ArrayList<Outlet>(outletsPreference.get(file.getProject()));
			}
		}

		IResource defaultOutletContainer;
		String defaultOutletPath = outputPathField.getText();
		if (defaultOutletPath != null) {
			defaultOutletContainer = ResourcesPlugin.getWorkspace().getRoot().findMember(defaultOutletPath);
			if (defaultOutletContainer != null && defaultOutletContainer.isAccessible()) {
				defaultOutlet = new Outlet(defaultOutletContainer.getLocation().toFile().getAbsolutePath());
				return Collections.singletonList(defaultOutlet);
			}
		}

		return Collections.<Outlet> emptyList();
	}

	public void finish() {
		saveWidgetValues();
	}

	protected void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			IDialogSettings section = settings.getSection(CODE_GEN_SECTION);
			if (section == null) {
				section = settings.addNewSection(CODE_GEN_SECTION);
			}
			if (templatePathField.getText().trim().length() != 0) {
				section.put(getTemplatePathDialogSettingsKey(), templatePathField.getText());
				String[] items = defineBlockField.getItems();
				int selectionIndex = defineBlockField.getSelectionIndex();
				if (items.length > 0 && selectionIndex != -1) {
					section.put(STORE_SELECTED_DEFINE_BLOCK, items[selectionIndex]);
				}
			}
		}
	}

	protected void loadTemplateBlockSettings() {
		IDialogSettings section = getDialogSettings().getSection(CODE_GEN_SECTION);
		if (section != null) {
			String path = section.get(getTemplatePathDialogSettingsKey());
			if (path != null) {
				IResource template = findMemberFromText(path);
				if (template != null) {
					templatePathField.setText(path);
					updateDefineBlockItems(template);
					defineBlockField.selectItem(section.get(STORE_SELECTED_DEFINE_BLOCK));
				}
			}
		}
	}

	protected String getTemplatePathDialogSettingsKey() {
		// FIXME modelObject may be null
		return STORE_TEMPLATE_PATH + modelObject.eResource().getURIFragment(modelObject);
	}
}
