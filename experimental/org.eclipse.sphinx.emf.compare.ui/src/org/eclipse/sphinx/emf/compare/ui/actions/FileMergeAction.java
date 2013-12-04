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
package org.eclipse.sphinx.emf.compare.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action for merging {@link IFile file}s in the workspace. Supports and is enabled only when selected {@link IFile
 * file}s are model files. Used as target for "Merge With > Each Other" popup menu contribution of this plug-in.
 * 
 * @see BasicAutoMergeAction
 */
public class FileMergeAction implements IObjectActionDelegate {

	private BasicAutoMergeAction modelMergeActionDelegate = new BasicAutoMergeAction();

	/*
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 * org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// Nothing to do
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (modelMergeActionDelegate.isEnabled()) {
			modelMergeActionDelegate.run();
		}
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// Propagate selection to delegates
		modelMergeActionDelegate.selectionChanged(SelectionUtil.getStructuredSelection(selection));

		// Update enablement state
		if (action != null) {
			action.setEnabled(modelMergeActionDelegate.isEnabled());
		}
	}
}
