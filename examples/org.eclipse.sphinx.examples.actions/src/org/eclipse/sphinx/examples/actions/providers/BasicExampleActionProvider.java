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
package org.eclipse.sphinx.examples.actions.providers;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider;
import org.eclipse.sphinx.examples.actions.ProjectStatisticsAction;
import org.eclipse.sphinx.examples.common.ui.ISphinxExampleMenuConstants;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * Basic Action Provider for examples of Sphinx actions.
 */
public class BasicExampleActionProvider extends BasicActionProvider {

	/**
	 * The action responsible for creating a report on project's content (number of files, number of objects per type,
	 * available types, etc.)
	 */
	private ProjectStatisticsAction projectStatisticsAction;

	/*
	 * @see org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider#doInit()
	 */
	@Override
	public void doInit() {
		projectStatisticsAction = new ProjectStatisticsAction();

		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(projectStatisticsAction);

			ISelection selection = selectionProvider.getSelection();
			IStructuredSelection structuredSelection = SelectionUtil.getStructuredSelection(selection);
			projectStatisticsAction.updateSelection(structuredSelection);
		}
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider#addSubMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected IMenuManager addSubMenu(IMenuManager contextMenuManager) {
		IMenuManager subMenuManager = contextMenuManager.findMenuUsingPath(ISphinxExampleMenuConstants.MENU_SPHINX_EXAMPLES_ID);
		if (subMenuManager == null) {
			subMenuManager = new MenuManager(ISphinxExampleMenuConstants.MENU_SPHINX_EXAMPLES_LABEL,
					ISphinxExampleMenuConstants.MENU_SPHINX_EXAMPLES_ID);
			contextMenuManager.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, subMenuManager);

			subMenuManager.add(new Separator(ISphinxExampleMenuConstants.GROUP_SPHINX_EXAMPLES));
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
		subMenuManager.add(new ActionContributionItem(projectStatisticsAction));
	}

	/*
	 * @see org.eclipse.ui.actions.ActionGroup#dispose()
	 */
	@Override
	public void dispose() {
		if (selectionProvider != null && projectStatisticsAction != null) {
			selectionProvider.removeSelectionChangedListener(projectStatisticsAction);
		}

		super.dispose();
	}
}