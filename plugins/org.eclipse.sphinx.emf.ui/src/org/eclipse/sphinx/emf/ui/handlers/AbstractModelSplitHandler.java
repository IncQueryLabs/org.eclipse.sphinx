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
package org.eclipse.sphinx.emf.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.splitting.BasicModelSplitOperation;
import org.eclipse.sphinx.emf.splitting.IModelSplitOperation;
import org.eclipse.sphinx.emf.splitting.IModelSplitPolicy;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.jobs.WorkspaceOperationWorkspaceJob;
import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

// TODO Harmonize code with other handlers (e.g., org.eclipse.sphinx.emf.mwe.dynamic.ui.handlers.BasicWorkflowRunnerHandler)
public abstract class AbstractModelSplitHandler extends AbstractHandler {

	private boolean runInBackground;

	protected IFile modelFile;

	public boolean isRunInBackground() {
		return runInBackground;
	}

	protected boolean updateSelection(ISelection selection) {
		// Check if selection contains precisely 1 model file
		modelFile = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object selected = structuredSelection.getFirstElement();
				if (selected instanceof IFile) {
					modelFile = (IFile) selected;
				}
			}
		}
		return modelFile != null;
	}

	protected abstract IModelSplitPolicy getModelSplitPolicy();

	protected IModelSplitOperation createModelSplitOperation(Resource resource) {
		return new BasicModelSplitOperation(resource, getModelSplitPolicy());
	}

	protected WorkspaceOperationWorkspaceJob createWorkspaceOperationJob(IModelSplitOperation operation) {
		return new WorkspaceOperationWorkspaceJob(operation);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (!updateSelection(selection)) {
			return null;
		}

		// TODO Defer model loading to operation
		Resource resource = getResource();
		if (resource != null) {
			IWorkspaceOperation operation = createModelSplitOperation(resource);
			if (operation != null) {
				try {
					Shell shell = HandlerUtil.getActiveShell(event);
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
					dialog.run(true, true, new RunnableWithProgressAdapter(operation));
				} catch (InterruptedException ex) {
					// Operation has been canceled by user, do nothing
				} catch (InvocationTargetException ex) {
					throw new ExecutionException(ex.getMessage(), ex);
				}
			}
		}

		return null;
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
