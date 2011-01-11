/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.emf.explorer.internal.actions;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.sphinx.emf.explorer.internal.Activator;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveableFilter;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CloseResourceAction;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;

/**
 * 
 */
public class CloseResourceOverrideAction extends CloseResourceAction {

	/**
	 * The shell in which to show the progress and problems dialog.
	 */
	private final IShellProvider shellProvider;

	/**
	 * The id of this action.
	 */
	public static final String ID = Activator.getPlugin().getSymbolicName() + ".CloseResourceAction"; //$NON-NLS-1$

	/**
	 * Creates a new action.
	 * 
	 * @param provider
	 *            the shell provider for any dialogs
	 */
	public CloseResourceOverrideAction(final IShellProvider provider) {
		super(provider);
		Assert.isNotNull(provider);
		shellProvider = provider;
	}

	/**
	 * Creates a new action.
	 * 
	 * @param provider
	 *            the shell provider for any dialogs
	 * @param text
	 *            the action's label
	 */
	public CloseResourceOverrideAction(IShellProvider provider, String text) {
		super(provider, text);
		Assert.isNotNull(provider);
		shellProvider = provider;
	}

	private void initAction() {
		setId(ID);
		setToolTipText(IDEWorkbenchMessages.CloseResourceAction_toolTip);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.CLOSE_RESOURCE_ACTION);
	}

	@Override
	public void run() {
		if (!saveDirtyModels()) {
			return;
		}
		super.run();
	}

	/**
	 * Causes all dirty models associated to the resource(s) to be saved, if so specified by the user, and closed.
	 */
	protected boolean saveDirtyModels() {
		// Get the items to close
		@SuppressWarnings("unchecked")
		final List<IProject> projects = getSelectedResources();
		if (projects == null || projects.isEmpty()) {
			// No action needs to be taken since no projects are selected
			return false;
		}

		final boolean canceled[] = new boolean[1];
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				// TODO Externalize message
				SafeRunner.run(new SafeRunnable("An error occurred while saving dirty models in the workbench"/*
																											 * Messages.error_failedToSaveModelsInWorkbench
																											 */) {
					public void run() {
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (window == null) {
							IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
							if (windows.length > 0) {
								window = windows[0];
							}
						}
						if (window != null) {
							canceled[0] = !PlatformUI.getWorkbench().saveAll(window, window, new BasicModelSaveableFilter(projects), true);
						}
					}
				});
			}
		});

		// Abort operation if saving has been canceled by user
		if (canceled[0]) {
			return false;
		}

		// Force reset of dirty information on all models in given projects for clearing dirty information of those
		// models that have not been taken into account by the save operation (happens e.g. when user deselects some
		// or all of them before proceeding with the save operation)
		for (IProject project : projects) {
			Collection<IProject> projectGroup = ExtendedPlatform.getProjectGroup(project, true);
			for (IProject proj : projectGroup) {
				ModelSaveManager.INSTANCE.setSaved(proj);
			}
		}

		return true;
	}
}
