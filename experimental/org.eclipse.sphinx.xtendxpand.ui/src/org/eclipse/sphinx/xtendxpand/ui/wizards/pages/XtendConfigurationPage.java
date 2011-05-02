/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [343844] Enable multiple Xtend MetaModels to be configured on BasicM2xAction, M2xConfigurationWizard, and Xtend/Xpand/CheckJob
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xtendxpand.ui.wizards.pages;

import java.util.Collection;
import java.util.MissingResourceException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.groups.IGroupListener;
import org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage;
import org.eclipse.sphinx.xtend.XtendEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.ui.groups.ExtensionGroup;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtend.typesystem.MetaModel;

public class XtendConfigurationPage extends AbstractWizardPage {

	// Extension Group
	protected ExtensionGroup extensionGroup;

	protected EObject modelObject;

	protected Collection<MetaModel> metaModels;

	public XtendConfigurationPage(String pageName) {
		super(pageName);
	}

	public void init(EObject modelObject, Collection<MetaModel> metaModels) {
		this.modelObject = modelObject;
		this.metaModels = metaModels;
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
		// Creates the extension group field and load dialog settings.
		createExtensionGroup(parent);
	}

	/**
	 * Creates the template group field and load dialog settings.
	 */
	protected void createExtensionGroup(Composite parent) {
		extensionGroup = new ExtensionGroup(Messages.label_extension, modelObject, metaModels, getDialogSettings());
		extensionGroup.createContent(parent, 3);
		extensionGroup.addGroupListener(new IGroupListener() {

			public void groupChanged(IField field) {
				getWizard().getContainer().updateButtons();
			}
		});
	}

	@Override
	protected String doGetDescription() throws MissingResourceException {
		return Messages.desc_modelTransformation;
	}

	@Override
	protected String doGetTitle() throws MissingResourceException {
		return Messages.title_launchModelTransformation;
	}

	@Override
	protected boolean doIsPageComplete() {
		return extensionGroup.isGroupComplete();
	}

	@Override
	protected IStatus doValidateRules() {
		return null;
	}

	public Collection<XtendEvaluationRequest> getXtendEvaluationRequests() {
		return extensionGroup.getXtendEvaluationRequests();
	}

	@Override
	public void finish() {
		if (extensionGroup != null) {
			extensionGroup.saveGroupSettings();
		}
	}
}
