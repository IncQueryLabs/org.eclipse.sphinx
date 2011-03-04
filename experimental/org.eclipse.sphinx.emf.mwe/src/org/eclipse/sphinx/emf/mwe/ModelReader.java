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
package org.eclipse.sphinx.emf.mwe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.lib.WorkflowComponentWithModelSlot;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;

public class ModelReader extends WorkflowComponentWithModelSlot {

	private String projectName;
	private String metaModelId;

	@Override
	public void checkConfiguration(Issues issues) {
		super.checkConfiguration(issues);
		checkRequiredConfigProperty("projectName", projectName, issues); //$NON-NLS-1$
		checkRequiredConfigProperty("metaModelId", metaModelId, issues); //$NON-NLS-1$
	}

	@Override
	protected void invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		if (!project.exists()) {
			issues.addError("Project '" + projectName + "' does not exist in the workspace " + workspace.getRoot().getLocation().toFile()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		if (!project.isAccessible()) {
			issues.addError("Project '" + projectName + "' is not accessible in the workspace " + workspace.getRoot().getLocation().toFile()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		List<EObject> modelRoots = new ArrayList<EObject>();
		IMetaModelDescriptor mmd = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(metaModelId);
		if (mmd == null) {
			issues.addError("Model Descriptor '" + metaModelId + "' unknown"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		Collection<IModelDescriptor> models = ModelDescriptorRegistry.INSTANCE.getModels(project, mmd);
		if (models.isEmpty()) {
			issues.addError("Project '" + projectName + "' does not contain any " + mmd.getName() + " models"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}

		// TODO Rework to support the case where multiple models for same metamodel descriptor exist in given project
		IModelDescriptor modelDescriptor = models.iterator().next();

		ModelLoadManager.INSTANCE.loadModel(modelDescriptor, false, null);

		for (Resource resource : modelDescriptor.getLoadedResources(true)) {
			EObject modelRoot = EcoreResourceUtil.getModelRoot(resource);
			issues.addInfo("Loaded resource " + resource.getURI()); //$NON-NLS-1$
			modelRoots.add(modelRoot);
		}
		if (modelRoots.isEmpty()) {
			issues.addError("Project '" + projectName + "' does not contain" + mmd.getName() + "resources"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		ctx.set(getModelSlot(), modelRoots);
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setMetaModelId(String metaModelId) {
		this.metaModelId = metaModelId;
	}

	public String getMetaModelId() {
		return metaModelId;
	}
}
