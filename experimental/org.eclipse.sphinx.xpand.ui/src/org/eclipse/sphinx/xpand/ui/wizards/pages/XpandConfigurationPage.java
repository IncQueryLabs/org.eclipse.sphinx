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

import java.util.Collection;
import java.util.MissingResourceException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.sphinx.platform.ui.groups.IGroupListener;
import org.eclipse.sphinx.platform.ui.wizards.pages.AbstractWizardPage;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.groups.OutputGroup;
import org.eclipse.sphinx.xpand.ui.groups.TemplateGroup;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtend.typesystem.MetaModel;

public class XpandConfigurationPage extends AbstractWizardPage {

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
		templateGroup = new TemplateGroup(Messages.label_template, modelObject, metaModel, getDialogSettings());
		templateGroup.createContent(parent, 3);
		templateGroup.addGroupListener(new IGroupListener() {

			public void groupChanged(IField field) {
				getWizard().getContainer().updateButtons();
			}
		});
	}

	/**
	 * Creates the output group field.
	 */
	protected void createOutputGroup(Composite parent) {
		outputGroup = new OutputGroup(Messages.label_output, modelObject, outletsPreference);
		outputGroup.createContent(parent, 3);
		outputGroup.addGroupListener(new IGroupListener() {

			public void groupChanged(IField field) {
				getWizard().getContainer().updateButtons();
			}
		});
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
		return templateGroup.isGroupComplete() && outputGroup.isGroupComplete();

	}

	@Override
	protected IStatus doValidateRules() {
		return null;
	}

	public Collection<XpandEvaluationRequest> getXpandEvaluationRequests() {
		return templateGroup.getXpandEvaluationRequests();
	}

	public Collection<? extends ExtendedOutlet> getOutlets() {
		return outputGroup.getOutlets();
	}

	@Override
	public void finish() {
		if (templateGroup != null) {
			templateGroup.saveGroupSettings();
		}
	}
}
