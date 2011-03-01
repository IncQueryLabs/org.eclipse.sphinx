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
package org.eclipse.sphinx.xpand.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.artop.ecl.emf.util.EcorePlatformUtil;
import org.artop.ecl.emf.util.EcoreResourceUtil;
import org.artop.ecl.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.jobs.M2TJob;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xpand.ui.wizards.M2TConfigurationWizard;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.xpand2.XpandUtil;
import org.eclipse.xpand2.output.Outlet;

public class BasicM2TAction extends BaseSelectionListenerAction {

	/** The path that is used when no default outlet is provided. */
	public static final String DEFAULT_OUTLET_PATH = "gen"; //$NON-NLS-1$

	public static final String DEFAULT_TEMPLATE_NAME = "main"; //$NON-NLS-1$

	protected IScopingResourceLoader scopingResourceLoader;

	private OutletsPreference outletsPreference;

	public BasicM2TAction(String text) {
		super(text);
	}

	public OutletsPreference getOutletsPreference() {
		return outletsPreference;
	}

	public void setOutletsPreference(OutletsPreference outletsPreference) {
		this.outletsPreference = outletsPreference;
	}

	protected EObject getSelectedModelObject() {
		Object selected = getStructuredSelection().getFirstElement();
		if (selected instanceof EObject) {
			return (EObject) selected;
		}
		Resource resource = EcorePlatformUtil.getResource(selected);
		return EcoreResourceUtil.getModelRoot(resource);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.size() == 1 && getSelectedModelObject() != null && !getXpandEvaluationRequests().isEmpty();
	}

	@Override
	public void run() {
		if (getDefinitionName() != null) {
			Job job = createM2TJob();
			// Show console and make sure that all system output produced during execution gets displayed there
			ExtendedPlatformUI.showSystemConsole();
			job.schedule();
			return;
		}

		M2TConfigurationWizard wizard = new M2TConfigurationWizard(getSelectedModelObject(), getScopingResourceLoader(), getDefaultOutletURI());
		WizardDialog wizardDialog = new WizardDialog(ExtendedPlatformUI.getDisplay().getActiveShell(), wizard);
		wizardDialog.open();
	}

	protected M2TJob createM2TJob() {
		M2TJob job = new M2TJob(getM2TJobName(), getXpandEvaluationRequests());
		job.setScopingResourceLoader(getScopingResourceLoader());
		job.setDefaultOutletURI(getDefaultOutletURI());
		job.getOutlets().addAll(getOutlets());
		job.setPriority(Job.BUILD);
		job.setRule(EcorePlatformUtil.getFile(getSelectedModelObject()).getProject());
		return job;
	}

	protected String getM2TJobName() {
		return Messages.job_generatingCode;
	}

	protected Collection<XpandEvaluationRequest> getXpandEvaluationRequests() {
		List<XpandEvaluationRequest> requests = new ArrayList<XpandEvaluationRequest>();
		requests.add(new XpandEvaluationRequest(getDefinitionName(), getSelectedModelObject()));
		return requests;
	}

	protected String getDefinitionName() {
		IFile templateFile = getTemplateFile();
		if (templateFile != null) {
			return getScopingResourceLoader().getDefinitionName(templateFile, getTemplateName());
		}
		return null;
	}

	protected IFile getTemplateFile() {
		IFile moduleDefFile = EcorePlatformUtil.getFile(getSelectedModelObject());
		if (moduleDefFile != null) {
			IPath templatePath = moduleDefFile.getFullPath().removeFileExtension().addFileExtension(XpandUtil.TEMPLATE_EXTENSION);
			return ResourcesPlugin.getWorkspace().getRoot().getFile(templatePath);
		}
		return null;
	}

	protected String getTemplateName() {
		return DEFAULT_TEMPLATE_NAME;
	}

	protected IScopingResourceLoader getScopingResourceLoader() {
		if (scopingResourceLoader == null) {
			scopingResourceLoader = createScopingResourceLoader();
		}
		return scopingResourceLoader;
	}

	protected IScopingResourceLoader createScopingResourceLoader() {
		return new BasicWorkspaceResourceLoader();
	}

	protected URI getDefaultOutletURI() {
		IFile file = EcorePlatformUtil.getFile(getStructuredSelection().getFirstElement());
		if (file != null) {
			IProject project = file.getProject();
			return EcorePlatformUtil.createURI(project.getFolder(getDefaultOutletPath()).getFullPath());
		}
		return null;
	}

	protected String getDefaultOutletPath() {
		return DEFAULT_OUTLET_PATH;
	}

	protected Collection<Outlet> getOutlets() {
		if (outletsPreference != null) {
			IFile file = EcorePlatformUtil.getFile(getSelectedModelObject());
			if (file != null && file.getProject() != null) {
				return new ArrayList<Outlet>(outletsPreference.get(file.getProject()));
			}
		}
		return Collections.emptyList();
	}
}