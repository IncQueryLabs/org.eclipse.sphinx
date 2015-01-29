/**
 * <copyright>
 *
 * Copyright (c) 2008-2015 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [457704] Integrate EMF compare 3.x in Sphinx
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.compare.ui.actions.providers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sphinx.emf.compare.ui.ICompareMenuConstants;
import org.eclipse.sphinx.emf.compare.ui.actions.BasicCompareAction;
import org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider;
import org.eclipse.ui.IWorkbenchActionConstants;

public class BasicCompareActionProvider extends BasicActionProvider {

	/**
	 * The compare action to use.
	 */
	protected BasicCompareAction compareAction;

	// FIXME check if auto merge is needed
	// protected BasicAutoMergeAction mergeAutoAction;

	@Override
	protected void doInit() {
		compareAction = new BasicCompareAction();
		// FIXME mergeAutoAction = new BasicAutoMergeAction();

		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(compareAction);
			// FIXME selectionProvider.addSelectionChangedListener(mergeAutoAction);

			ISelection selection = selectionProvider.getSelection();
			IStructuredSelection structuredSelection = selection instanceof IStructuredSelection ? (IStructuredSelection) selection
					: StructuredSelection.EMPTY;

			compareAction.updateSelection(structuredSelection);
			// FIXME mergeAutoAction.updateSelection(structuredSelection);
		}
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider#addSubMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected IMenuManager addSubMenu(IMenuManager contextMenuManager) {
		IMenuManager subMenuManager = contextMenuManager.findMenuUsingPath(ICompareMenuConstants.MENU_COMPARE_WITH_ID);
		if (subMenuManager == null) {
			subMenuManager = new MenuManager(ICompareMenuConstants.MENU_COMPARE_WITH_LABEL, ICompareMenuConstants.MENU_COMPARE_WITH_ID);
			subMenuManager.add(new Separator(ICompareMenuConstants.MENU_COMPARE_WITH_GROUP));
			contextMenuManager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, subMenuManager);
		}
		return subMenuManager;
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider#fillSubMenu(org.eclipse.jface.action.IMenuManager
	 * )
	 */
	@Override
	protected void fillSubMenu(IMenuManager subMenuManager) {
		subMenuManager.add(compareAction);
		// FIXME subMenuManager.add(new ActionContributionItem(mergeAutoAction));
	}

	@Override
	public void dispose() {
		super.dispose();
		if (selectionProvider != null) {
			if (compareAction != null) {
				selectionProvider.removeSelectionChangedListener(compareAction);
			}
			// FIXME if (mergeAutoAction != null) {
			// selectionProvider.removeSelectionChangedListener(mergeAutoAction);
			// }
		}
	}
}
