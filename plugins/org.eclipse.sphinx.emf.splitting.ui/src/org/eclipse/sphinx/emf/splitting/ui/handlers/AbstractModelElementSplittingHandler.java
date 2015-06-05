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
package org.eclipse.sphinx.emf.splitting.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.splitting.IModelElementSplittingListener;
import org.eclipse.sphinx.emf.splitting.operations.BasicModelElementSplittingOperation;
import org.eclipse.sphinx.emf.splitting.operations.IModelElementSplittingOperation;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractModelElementSplittingHandler extends AbstractHandler {

	protected IFile modelFile;

	protected abstract IModelElementSplittingListener getModelElementSplittingListerners();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Retrieve selected model file
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		modelFile = getModelFile(selection);
		if (modelFile == null) {
			throw new ExecutionException("No model file selected"); //$NON-NLS-1$
		}

		Resource resource = getResource(createProgressMonitor());
		if (resource != null) {
			IWorkspaceOperation operation = createModelElementSplittingOperation(resource);
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

	protected IModelElementSplittingOperation createModelElementSplittingOperation(Resource resource) {
		BasicModelElementSplittingOperation operation = new BasicModelElementSplittingOperation(resource);
		IModelElementSplittingListener modelElementSplittingListerners = getModelElementSplittingListerners();
		operation.setModelElementSplittingListener(modelElementSplittingListerners);
		return operation;
	}

	protected Resource getResource(IProgressMonitor monitor) {
		if (modelFile != null) {
			if (ModelDescriptorRegistry.INSTANCE.isModelFile(modelFile)) {
				ModelLoadManager.INSTANCE.loadFile(modelFile, false, monitor);
				return EcorePlatformUtil.getResource(modelFile);
			} else {
				return EcoreResourceUtil.loadResource(null, EcorePlatformUtil.createURI(modelFile.getFullPath()),
						EcoreResourceUtil.getDefaultLoadOptions());
			}
		}
		return null;
	}

	protected IFile getModelFile(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object selected = structuredSelection.getFirstElement();
				if (selected instanceof IFile) {
					return (IFile) selected;
				}
			}
		}
		return null;
	}

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}
}
