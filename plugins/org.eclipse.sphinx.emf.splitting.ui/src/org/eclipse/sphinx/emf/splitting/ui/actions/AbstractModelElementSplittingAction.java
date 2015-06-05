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
package org.eclipse.sphinx.emf.splitting.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.splitting.IModelElementSplittingListener;
import org.eclipse.sphinx.emf.splitting.operations.BasicModelElementSplittingOperation;
import org.eclipse.sphinx.emf.splitting.operations.IModelElementSplittingOperation;
import org.eclipse.sphinx.emf.splitting.ui.internal.Activator;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public abstract class AbstractModelElementSplittingAction extends BaseSelectionListenerAction {

	protected IFile selectedModelFile;

	protected abstract IModelElementSplittingListener getModelElementSplittingListerners();

	public AbstractModelElementSplittingAction(String text) {
		super(text);
	}

	/*
	 * @see
	 * org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection
	 * )
	 */
	@Override
	public boolean updateSelection(IStructuredSelection selection) {
		// Check if selection contains precisely 1 model file
		selectedModelFile = null;
		if (selection.size() == 1) {
			Object selected = selection.getFirstElement();
			if (selected instanceof IFile) {
				selectedModelFile = (IFile) selected;
			}
		}
		return selectedModelFile != null;
	}

	/*
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		Resource resource = getResource(createProgressMonitor());
		if (resource != null) {
			IWorkspaceOperation operation = createModelElementSplittingOperation(resource);
			if (operation != null) {
				try {
					Shell shell = ExtendedPlatformUI.getActiveShell();
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
					dialog.run(true, true, new RunnableWithProgressAdapter(operation));
				} catch (InterruptedException ex) {
					// Operation has been canceled by user, do nothing
				} catch (InvocationTargetException ex) {
					PlatformLogUtil.logAsError(Activator.getDefault(), ex);
				}
			}
		}
	}

	protected IModelElementSplittingOperation createModelElementSplittingOperation(Resource resource) {
		BasicModelElementSplittingOperation operation = new BasicModelElementSplittingOperation(resource);
		IModelElementSplittingListener modelElementSplittingListerners = getModelElementSplittingListerners();
		operation.setModelElementSplittingListener(modelElementSplittingListerners);
		return operation;
	}

	protected Resource getResource(IProgressMonitor monitor) {
		if (selectedModelFile != null) {
			if (ModelDescriptorRegistry.INSTANCE.isModelFile(selectedModelFile)) {
				ModelLoadManager.INSTANCE.loadFile(selectedModelFile, false, monitor);
				return EcorePlatformUtil.getResource(selectedModelFile);
			} else {
				return EcoreResourceUtil.loadResource(null, EcorePlatformUtil.createURI(selectedModelFile.getFullPath()),
						EcoreResourceUtil.getDefaultLoadOptions());
			}
		}
		return null;
	}

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}
}
