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
package org.eclipse.sphinx.xpand.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.jobs.M2TJob;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xpand.ui.wizards.M2TConfigurationWizard;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.xpand2.XpandUtil;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xtend.typesystem.MetaModel;

public class BasicM2TAction extends BaseSelectionListenerAction {

	public static final String PROJECT_RELATIVE_DEFAULT_OUTLET_PATH = "gen"; //$NON-NLS-1$

	public static final String DEFAULT_TEMPLATE_NAME = "main"; //$NON-NLS-1$

	private IScopingResourceLoader scopingResourceLoader;
	private MetaModel metaModel;

	public BasicM2TAction(String text) {
		super(text);
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
		return selection.size() == 1 && getSelectedModelObject() != null;
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

		M2TConfigurationWizard wizard = new M2TConfigurationWizard(getSelectedModelObject(), getMetaModel());
		wizard.setM2TJobName(getM2TJobName());
		wizard.setScopingResourceLoader(getScopingResourceLoader());
		wizard.setOutletsPreference(getOutletsPreference());
		wizard.setDefaultOutlet(getDefaultOutlet());
		WizardDialog wizardDialog = new WizardDialog(ExtendedPlatformUI.getDisplay().getActiveShell(), wizard);
		wizardDialog.open();
	}

	protected M2TJob createM2TJob() {
		M2TJob job = new M2TJob(getM2TJobName(), getMetaModel(), getXpandEvaluationRequests());
		job.setScopingResourceLoader(getScopingResourceLoader());
		job.getOutlets().addAll(getOutlets());
		job.setPriority(Job.BUILD);
		job.setRule(EcorePlatformUtil.getFile(getSelectedModelObject()).getProject());
		return job;
	}

	protected String getM2TJobName() {
		return Messages.job_generatingCode;
	}

	protected MetaModel getMetaModel() {
		if (metaModel == null) {
			metaModel = createMetaModel();
		}
		return metaModel;
	}

	protected MetaModel createMetaModel() {
		IFile file = EcorePlatformUtil.getFile(getSelectedModelObject());
		IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(file);
		return new SphinxManagedEmfMetaModel(model);
	}

	protected Collection<XpandEvaluationRequest> getXpandEvaluationRequests() {
		String definitionName = getDefinitionName();
		EObject selected = getSelectedModelObject();
		if (definitionName != null && selected != null) {
			XpandEvaluationRequest request = new XpandEvaluationRequest(definitionName, selected);
			return Collections.singletonList(request);
		}
		return Collections.emptyList();
	}

	protected String getDefinitionName() {
		IFile templateFile = getTemplateFile();
		if (templateFile != null) {
			return getScopingResourceLoader().getDefinitionName(templateFile, getTemplateName());
		}
		return null;
	}

	protected IFile getTemplateFile() {
		IFile modelFile = EcorePlatformUtil.getFile(getSelectedModelObject());
		if (modelFile != null) {
			IPath templatePath = modelFile.getFullPath().removeFileExtension().addFileExtension(XpandUtil.TEMPLATE_EXTENSION);
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

	protected Collection<Outlet> getOutlets() {
		// Return outlets resulting outlets preference if available
		OutletsPreference outletsPreference = getOutletsPreference();
		if (outletsPreference != null) {
			IFile file = EcorePlatformUtil.getFile(getSelectedModelObject());
			if (file != null && file.getProject() != null) {
				return new ArrayList<Outlet>(outletsPreference.get(file.getProject()));
			}
		}

		// Return default outlet otherwise
		Outlet defaultOutlet = getDefaultOutlet();
		return defaultOutlet != null ? Collections.singletonList(defaultOutlet) : Collections.<Outlet> emptyList();
	}

	protected OutletsPreference getOutletsPreference() {
		return null;
	}

	protected Outlet getDefaultOutlet() {
		IFile file = EcorePlatformUtil.getFile(getStructuredSelection().getFirstElement());
		if (file != null) {
			IProject project = file.getProject();
			IContainer defaultOutletContainer;
			String projectRelativeDefaultOutletPath = getProjectRelativeDefaultOutletPath();
			if (projectRelativeDefaultOutletPath != null && projectRelativeDefaultOutletPath.length() > 0) {
				defaultOutletContainer = project.getFolder(projectRelativeDefaultOutletPath);
			} else {
				defaultOutletContainer = project;
			}
			return new Outlet(defaultOutletContainer.getLocation().toFile().getAbsolutePath());
		}
		return null;
	}

	protected String getProjectRelativeDefaultOutletPath() {
		return PROJECT_RELATIVE_DEFAULT_OUTLET_PATH;
	}
}