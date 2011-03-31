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
package org.eclipse.sphinx.xpand.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.fields.ComboField;
import org.eclipse.sphinx.platform.ui.fields.SelectionButtonField;
import org.eclipse.sphinx.platform.ui.fields.StringButtonField;
import org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.groups.OutputGroup;
import org.eclipse.sphinx.xpand.ui.groups.TemplateGroup;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.xtend.typesystem.MetaModel;

public class XpandConfigurationPage extends AbstractWizardPage {

	// Dialog setting
	protected static final String CODE_GEN_SECTION = Activator.getPlugin().getSymbolicName() + ".CODE_GEN_SECTION"; //$NON-NLS-1$
	protected static final String STORE_TEMPLATE_PATH = "TEMPLATE_PATH$"; //$NON-NLS-1$
	protected static final String STORE_SELECTED_DEFINE_BLOCK = "SELECTED_DEFINE_BLOCK"; //$NON-NLS-1$

	// Template Group
	protected TemplateGroup templateGroup;

	// Output Group
	protected OutputGroup outputGroup;

	protected EObject modelObject;
	protected MetaModel metaModel;
	protected OutletsPreference outletsPreference;
	protected ExtendedOutlet defaultOutlet;

	public XpandConfigurationPage(String pageName) {
		super(pageName);
	}

	public void init(EObject modelObject, MetaModel metaModel, OutletsPreference outletsPreference, ExtendedOutlet defaultOutlet) {
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
		// Creates the template group field and load dialog settings.
		createTemplateGroup(parent);
		// Creates the output group field.
		createOutputGroup(parent);
	}

	/**
	 * Creates the template group field and load dialog settings.
	 */
	protected void createTemplateGroup(Composite parent) {
		templateGroup = new TemplateGroup(parent, Messages.label_template, 3, modelObject, metaModel);
		loadTemplateGroupSettings();
	}

	/**
	 * Creates the output group field.
	 */
	protected void createOutputGroup(Composite parent) {
		outputGroup = new OutputGroup(parent, Messages.label_output, 3, modelObject, outletsPreference) {

			@Override
			protected void groupChanged(Group group) {
				getWizard().getContainer().updateButtons();
			};
		};
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
		return isTemplateGroupComplete() && isOutputGroupComplete();

	}

	protected boolean isTemplateGroupComplete() {
		IFile templateFile = getFile(templateGroup.getTemplatePathField().getText());
		if (templateFile != null) {
			return templateFile.exists() && templateGroup.getDefineBlockField().getSelectionIndex() != -1;
		}
		return false;
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

	protected IContainer getContainer(String fullPath) {
		if (fullPath != null && fullPath.length() > 0) {
			IPath path = new Path(fullPath);
			path.makeAbsolute();
			IContainer container;
			if (path.segmentCount() == 1) {
				container = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
			} else {
				container = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
			}
			return container;
		}
		return null;
	}

	protected boolean isOutputGroupComplete() {
		SelectionButtonField field = outputGroup.getUseDefaultPathButtonField();
		if (field != null && !field.isSelected()) {
			return getContainer(outputGroup.getOutputPathField().getText()) != null;
		}
		return true;
	}

	@Override
	protected IStatus doValidateRules() {
		return null;
	}

	public Collection<XpandEvaluationRequest> getXpandEvaluationRequests() {
		List<XpandEvaluationRequest> requests = new ArrayList<XpandEvaluationRequest>();
		if (modelObject != null) {
			String definitionName = templateGroup.getDefinitionName();
			if (definitionName != null && definitionName.length() > 0) {
				requests.add(new XpandEvaluationRequest(definitionName, modelObject));
			}
		}
		return requests;
	}

	public Collection<ExtendedOutlet> getOutlets() {
		if (outletsPreference != null) {
			IFile file = EcorePlatformUtil.getFile(modelObject);
			if (file != null && file.getProject() != null) {
				return outletsPreference.get(file.getProject());
			}
		}

		IContainer defaultOutletContainer = getContainer(outputGroup.getOutputPathField().getText());
		if (defaultOutletContainer != null) {
			defaultOutlet = new ExtendedOutlet(defaultOutletContainer);
			return Collections.singletonList(defaultOutlet);
		}

		return Collections.<ExtendedOutlet> emptyList();
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
			StringButtonField templatePathField = templateGroup.getTemplatePathField();
			if (templatePathField.getText().trim().length() > 0) {
				section.put(getTemplatePathDialogSettingsKey(), templatePathField.getText());
				ComboField defineBlockField = templateGroup.getDefineBlockField();
				String[] items = defineBlockField.getItems();
				int selectionIndex = defineBlockField.getSelectionIndex();
				if (items.length > 0 && selectionIndex != -1) {
					section.put(STORE_SELECTED_DEFINE_BLOCK, items[selectionIndex]);
				}
			}
		}
	}

	protected void loadTemplateGroupSettings() {
		IDialogSettings section = getDialogSettings().getSection(CODE_GEN_SECTION);
		if (section != null) {
			String templatePath = section.get(getTemplatePathDialogSettingsKey());
			if (templatePath != null) {
				IFile templateFile = getFile(templatePath);
				if (templateFile != null && templateFile.exists()) {
					StringButtonField templatePathField = templateGroup.getTemplatePathField();
					templatePathField.setText(templatePath);
					templateGroup.updateDefineBlockItems(templateFile);
					ComboField defineBlockField = templateGroup.getDefineBlockField();
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
