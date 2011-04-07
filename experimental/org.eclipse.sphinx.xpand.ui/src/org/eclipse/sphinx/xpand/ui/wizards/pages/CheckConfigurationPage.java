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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.groups.FileSelectionGroup;
import org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xtend.check.CheckEvaluationRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtend.typesystem.MetaModel;

public class CheckConfigurationPage extends AbstractWizardPage {

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
			checkGroup = new FileSelectionGroup(Messages.label_checkModelBlock, Messages.label_useCheckModelButton, Messages.label_checkModelBlock,
					IXtendXpandConstants.CHECK_EXTENSION, modelFile.getProject(), getDialogSettings());
			checkGroup.createContent(parent, 3);
		}
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
		if (checkGroup != null) {
			checkGroup.saveGroupSettings();
		}
	}
}
