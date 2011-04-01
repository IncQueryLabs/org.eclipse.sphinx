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
import java.util.List;
import java.util.MissingResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.groups.FileSelectionGroup;
import org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xtend.check.CheckEvaluationRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtend.typesystem.MetaModel;

public class CheckConfigurationPage extends AbstractWizardPage {

	// Dialog setting
	protected static final String CODE_GEN_SECTION = Activator.getPlugin().getSymbolicName() + ".CODE_GEN_SECTION"; //$NON-NLS-1$
	protected static final String STORE_TEMPLATE_PATH = "TEMPLATE_PATH$"; //$NON-NLS-1$
	protected static final String STORE_SELECTED_CHECK_FILES = "SELECTED_CHECK_FILES"; //$NON-NLS-1$
	protected static final String STORE_ENABLE_BUTTON = "ENABLEG_BUTTON"; //$NON-NLS-1$

	// Check model Group
	protected FileSelectionGroup checkGroup;

	protected EObject modelObject;
	protected MetaModel metaModel;

	public CheckConfigurationPage(String pageName) {
		super(pageName);
	}

	public void init(EObject modelObject, MetaModel metaModel) {
		this.modelObject = modelObject;
		this.metaModel = metaModel;
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
		createCheckGroup(parent);
	}

	protected void createCheckGroup(Composite parent) {
		IFile modelFile = EcorePlatformUtil.getFile(modelObject);
		if (modelFile != null) {
			checkGroup = new FileSelectionGroup(parent, Messages.label_checkModelBlock, Messages.label_useCheckModelButton,
					Messages.label_checkModelBlock, 3, IXtendXpandConstants.CHECK_EXTENSION, modelFile.getProject());
			checkGroup.updateFileSelectionEnableState(false);
		}

		// Load Dialog Settings
		loadCheckModelBlockSettings();
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
		return true;

	}

	@Override
	protected IStatus doValidateRules() {
		return null;
	}

	public Collection<CheckEvaluationRequest> getCheckEvaluationRequests() {
		List<CheckEvaluationRequest> requests = new ArrayList<CheckEvaluationRequest>();
		if (modelObject != null) {
			Collection<IFile> checkFiles = checkGroup.getFiles();
			if (!checkFiles.isEmpty()) {
				requests.add(new CheckEvaluationRequest(checkFiles, modelObject));
			}
		}
		return requests;
	}

	public boolean isCheckEnabled() {
		return checkGroup.getEnableButtonState();
	}

	@Override
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
			Collection<IFile> files = checkGroup.getFiles();
			String[] items = new String[files.size()];
			int i = 0;
			for (IFile file : files) {
				items[i] = file.getFullPath().makeRelative().toString();
				i++;
			}
			section.put(STORE_SELECTED_CHECK_FILES, items);
			section.put(STORE_ENABLE_BUTTON, checkGroup.getEnableButtonState());
		}
	}

	protected void loadCheckModelBlockSettings() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			IDialogSettings section = settings.getSection(CODE_GEN_SECTION);
			if (section != null) {
				String[] items = section.getArray(STORE_SELECTED_CHECK_FILES);
				boolean enableCheck = section.getBoolean(STORE_ENABLE_BUTTON);
				if (items != null) {
					checkGroup.setEnabledButtonSelection(enableCheck);
					for (String fullPath : items) {
						IFile file = getFile(fullPath);
						if (file != null) {
							checkGroup.addFile(file);
						}
					}
					checkGroup.updateFileSelectionEnableState(enableCheck);
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
}
