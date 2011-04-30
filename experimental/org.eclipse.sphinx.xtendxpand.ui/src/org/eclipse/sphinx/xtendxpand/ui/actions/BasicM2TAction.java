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
package org.eclipse.sphinx.xtendxpand.ui.actions;

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
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.mwe.resources.IWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.jobs.XpandJob;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.util.XtendXpandUtil;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xtendxpand.ui.wizards.M2TConfigurationWizard;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.xtend.typesystem.MetaModel;

public class BasicM2TAction extends BaseSelectionListenerAction {

	public static final String PROJECT_RELATIVE_DEFAULT_OUTLET_PATH = "gen"; //$NON-NLS-1$

	public static final String DEFAULT_TEMPLATE_NAME = "main"; //$NON-NLS-1$

	private IWorkspaceResourceLoader workspaceResourceLoader;
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
		EObject modelObject = getSelectedModelObject();

		// Definition to be used for selected model object available right away?
		if (getDefinitionName(modelObject) != null) {
			// Show console and make sure that all system output produced during execution gets displayed there
			ExtendedPlatformUI.showSystemConsole();

			// Schedule model to text transformation job
			Job job = createXpandJob();
			job.setPriority(Job.BUILD);
			IFile file = EcorePlatformUtil.getFile(modelObject);
			if (file != null) {
				job.setRule(file.getProject());
			}
			job.schedule();
			return;
		}

		// Open wizard that lets user select the definition to be used
		else {
			M2TConfigurationWizard wizard = new M2TConfigurationWizard(modelObject, getMetaModel());
			wizard.setM2TJobName(getM2TJobName());
			wizard.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
			wizard.setOutletsPreference(getOutletsPreference());
			wizard.setDefaultOutlet(getDefaultOutlet());
			WizardDialog wizardDialog = new WizardDialog(ExtendedPlatformUI.getDisplay().getActiveShell(), wizard);
			wizardDialog.open();
		}
	}

	protected XpandJob createXpandJob() {
		XpandJob job = new XpandJob(getM2TJobName(), getMetaModel(), getXpandEvaluationRequests());
		job.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
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
		EObject selected = getSelectedModelObject();
		String definitionName = getDefinitionName(selected);
		if (definitionName != null && selected != null) {
			XpandEvaluationRequest request = new XpandEvaluationRequest(definitionName, selected);
			return Collections.singletonList(request);
		}
		return Collections.emptyList();
	}

	protected String getDefinitionName(EObject modelObject) {
		IFile templateFile = getTemplateFile(modelObject);
		if (templateFile != null) {
			return XtendXpandUtil.getQualifiedName(templateFile, getTemplateName());
		}
		return null;
	}

	protected IFile getTemplateFile(EObject modelObject) {
		IFile modelFile = EcorePlatformUtil.getFile(modelObject);
		if (modelFile != null) {
			IPath templatePath = modelFile.getFullPath().removeFileExtension().addFileExtension(IXtendXpandConstants.TEMPLATE_EXTENSION);
			return ResourcesPlugin.getWorkspace().getRoot().getFile(templatePath);
		}
		return null;
	}

	protected String getTemplateName() {
		return DEFAULT_TEMPLATE_NAME;
	}

	protected IWorkspaceResourceLoader getWorkspaceResourceLoader() {
		if (workspaceResourceLoader == null) {
			workspaceResourceLoader = createWorkspaceResourceLoader();
		}
		return workspaceResourceLoader;
	}

	protected IWorkspaceResourceLoader createWorkspaceResourceLoader() {
		return new BasicWorkspaceResourceLoader();
	}

	protected OutletsPreference getOutletsPreference() {
		return null;
	}

	protected Collection<ExtendedOutlet> getOutlets() {
		// Return outlets resulting outlets preference if available
		OutletsPreference outletsPreference = getOutletsPreference();
		if (outletsPreference != null) {
			IFile file = EcorePlatformUtil.getFile(getSelectedModelObject());
			if (file != null && file.getProject() != null) {
				return outletsPreference.get(file.getProject());
			}
		}

		// Return default outlet otherwise
		ExtendedOutlet defaultOutlet = getDefaultOutlet();
		if (defaultOutlet != null) {
			return Collections.singletonList(defaultOutlet);
		}

		return Collections.<ExtendedOutlet> emptyList();
	}

	protected ExtendedOutlet getDefaultOutlet() {
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
			return new ExtendedOutlet(defaultOutletContainer);
		}
		return null;
	}

	protected String getProjectRelativeDefaultOutletPath() {
		return PROJECT_RELATIVE_DEFAULT_OUTLET_PATH;
	}
}