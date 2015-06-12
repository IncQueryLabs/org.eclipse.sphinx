/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.splitting.BasicModelSplitOperation;
import org.eclipse.sphinx.emf.splitting.IModelSplitOperation;
import org.eclipse.sphinx.emf.splitting.IModelSplitPolicy;
import org.eclipse.sphinx.emf.ui.internal.Activator;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.jobs.WorkspaceOperationWorkspaceJob;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public class BasicModelSplitAction extends BaseSelectionListenerAction {

	protected IFile modelFile;
	protected IModelSplitPolicy modelSplitPolicy;
	private boolean runInBackground;

	public BasicModelSplitAction(String text, IModelSplitPolicy modelSplitPolicy) {
		super(text);

		Assert.isNotNull(modelSplitPolicy);
		this.modelSplitPolicy = modelSplitPolicy;
		setRunInBackground(false);
	}

	public boolean isRunInBackground() {
		return runInBackground;
	}

	public void setRunInBackground(boolean runInBackground) {
		this.runInBackground = runInBackground;
	}

	/*
	 * @see
	 * org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection
	 * )
	 */
	@Override
	public boolean updateSelection(IStructuredSelection selection) {
		// Check if selection contains precisely 1 model file
		modelFile = null;
		if (selection.size() == 1) {
			Object selected = selection.getFirstElement();
			if (selected instanceof IFile) {
				modelFile = (IFile) selected;
			}
		}
		return modelFile != null;
	}

	protected IModelSplitOperation createModelSplitOperation(Resource resource) {
		return new BasicModelSplitOperation(resource, modelSplitPolicy);
	}

	protected WorkspaceOperationWorkspaceJob createWorkspaceOperationJob(IModelSplitOperation operation) {
		return new WorkspaceOperationWorkspaceJob(operation);
	}

	/*
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// TODO Defer model loading to BasicModelSplitOperation
		Resource resource = getResource();
		if (resource != null) {
			// Create the model split operation
			IModelSplitOperation operation = createModelSplitOperation(resource);

			if (isRunInBackground()) {
				// Run the workflow operation in a workspace job
				WorkspaceOperationWorkspaceJob job = createWorkspaceOperationJob(operation);
				job.schedule();
			} else {
				// Run the workflow operation in a progress monitor dialog
				try {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(ExtendedPlatformUI.getActiveShell());
					dialog.run(true, true, new RunnableWithProgressAdapter(operation));
				} catch (InterruptedException ex) {
					// Operation has been canceled by user, do nothing
				} catch (InvocationTargetException ex) {
					PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
				}
			}
		}
	}

	protected Resource getResource() {
		if (modelFile != null) {
			if (ModelDescriptorRegistry.INSTANCE.isModelFile(modelFile)) {
				return EcorePlatformUtil.loadResource(modelFile, EcoreResourceUtil.getDefaultLoadOptions());
			} else {
				return EcoreResourceUtil.loadResource(null, EcorePlatformUtil.createURI(modelFile.getFullPath()),
						EcoreResourceUtil.getDefaultLoadOptions());
			}
		}
		return null;
	}
}
