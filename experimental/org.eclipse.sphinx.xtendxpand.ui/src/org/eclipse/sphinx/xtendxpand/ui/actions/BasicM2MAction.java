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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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
import org.eclipse.sphinx.xtend.XtendEvaluationRequest;
import org.eclipse.sphinx.xtend.jobs.XtendJob;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xtendxpand.ui.wizards.M2MConfigurationWizard;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.xtend.shared.ui.MetamodelContributor;
import org.eclipse.xtend.shared.ui.core.metamodel.MetamodelContributorRegistry;
import org.eclipse.xtend.typesystem.MetaModel;

public class BasicM2MAction extends BaseSelectionListenerAction {

	public static final String PROJECT_RELATIVE_DEFAULT_OUTLET_PATH = "gen"; //$NON-NLS-1$

	public static final String DEFAULT_EXTENSION_NAME = "main"; //$NON-NLS-1$

	private IWorkspaceResourceLoader workspaceResourceLoader;

	private MetaModel metaModel;

	public BasicM2MAction(String text) {
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
		if (getExtensionName(getSelectedModelObject()) != null) {
			Job job = createXtendJob();
			// Show console and make sure that all system output produced during execution gets displayed there
			ExtendedPlatformUI.showSystemConsole();
			job.schedule();
			return;
		}

		M2MConfigurationWizard wizard = new M2MConfigurationWizard(getSelectedModelObject(), getMetaModel());
		wizard.setM2MJobName(getM2MJobName());
		wizard.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
		WizardDialog wizardDialog = new WizardDialog(ExtendedPlatformUI.getDisplay().getActiveShell(), wizard);
		wizardDialog.open();
	}

	protected XtendJob createXtendJob() {
		// TODO Create enclosing WorkspaceJob which retrieves result from XtendJob and puts it on clipboard of shared
		// editing domain for metamodel behind result objects
		XtendJob job = new XtendJob(getM2MJobName(), getMetaModel(), getXtendEvaluationRequests());
		job.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
		job.setPriority(Job.BUILD);
		job.setRule(EcorePlatformUtil.getFile(getSelectedModelObject()).getProject());
		return job;
	}

	protected String getM2MJobName() {
		return Messages.job_modelTransformation;
	}

	// TODO Support a collection of MetaModels rather than a single MetaModel
	// TODO Do same thing in BasicM2TAction and XpandJob
	protected MetaModel getMetaModel() {
		if (metaModel == null) {
			metaModel = createMetaModel();
		}
		return metaModel;
	}

	protected MetaModel createMetaModel() {
		IFile file = EcorePlatformUtil.getFile(getSelectedModelObject());

		// Add metamodels defined by Xtend/Xpand preferences
		IJavaProject javaProject = JavaCore.create(file.getProject());
		List<? extends MetamodelContributor> contributors = MetamodelContributorRegistry.getActiveMetamodelContributors(javaProject);
		for (MetamodelContributor contributor : contributors) {
			for (MetaModel metaModel : contributor.getMetamodels(javaProject, null)) {
				// TODO return list of MetaModels
				return metaModel;
			}
		}

		// Add MetaModels resulting behind models in workspace if no metamodels defined by Xtend/Xpand preferences are
		// available
		IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(file);
		return new SphinxManagedEmfMetaModel(model);
	}

	protected Collection<XtendEvaluationRequest> getXtendEvaluationRequests() {
		EObject selected = getSelectedModelObject();
		String extensionName = getExtensionName(selected);
		if (extensionName != null && selected != null) {
			XtendEvaluationRequest request = new XtendEvaluationRequest(extensionName, selected);
			return Collections.singletonList(request);
		}
		return Collections.emptyList();
	}

	protected String getExtensionName(EObject modelObject) {
		IFile extensionFile = getExtensionFile(modelObject);
		if (extensionFile != null) {
			return getWorkspaceResourceLoader().getQualifiedName(extensionFile, getExtName());
		}
		return null;
	}

	protected IFile getExtensionFile(EObject modelObject) {
		IFile modelFile = EcorePlatformUtil.getFile(modelObject);
		if (modelFile != null) {
			IPath templatePath = modelFile.getFullPath().removeFileExtension().addFileExtension(IXtendXpandConstants.EXTENSION_EXTENSION);
			return ResourcesPlugin.getWorkspace().getRoot().getFile(templatePath);
		}
		return null;
	}

	protected String getExtName() {
		return DEFAULT_EXTENSION_NAME;
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

	protected String getProjectRelativeDefaultOutletPath() {
		return PROJECT_RELATIVE_DEFAULT_OUTLET_PATH;
	}
}