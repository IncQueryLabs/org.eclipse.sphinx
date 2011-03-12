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
package org.eclipse.sphinx.examples.codegen.xpand.ui.internal;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider;
import org.eclipse.sphinx.examples.codegen.xpand.ui.ISphinxCodeGenExampleMenuConstants;
import org.eclipse.sphinx.examples.codegen.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.sphinx.xpand.ui.actions.BasicM2TAction;
import org.eclipse.ui.navigator.ICommonMenuConstants;

public class SphinxCodeGenExampleActionProvider extends BasicActionProvider {

	protected BasicM2TAction launchCodeGenAction;

	@Override
	public void doInit() {
		launchCodeGenAction = createLaunchCodeGenAction();

		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(launchCodeGenAction);

			ISelection selection = selectionProvider.getSelection();
			IStructuredSelection structuredSelection = SelectionUtil.getStructuredSelection(selection);

			launchCodeGenAction.selectionChanged(structuredSelection);
		}
	}

	protected BasicM2TAction createLaunchCodeGenAction() {
		return new BasicM2TAction(Messages.menuItem_launchCodeGen);
	}

	/*
	 * @see
	 * org.artop.ecl.emf.validation.ui.actions.providers.AbstractValidationActionProvider#addSubMenu(org.eclipse.jface
	 * .action.IMenuManager)
	 */
	@Override
	protected IMenuManager addSubMenu(IMenuManager contextMenuManager) {
		IMenuManager subMenuManager = contextMenuManager.findMenuUsingPath(ISphinxCodeGenExampleMenuConstants.MENU_GENERATE_ID);
		if (subMenuManager == null) {
			subMenuManager = new MenuManager(ISphinxCodeGenExampleMenuConstants.MENU_GENERATE_LABEL,
					ISphinxCodeGenExampleMenuConstants.MENU_GENERATE_ID);
			contextMenuManager.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, subMenuManager);
		}
		return subMenuManager;
	}

	@Override
	protected void fillSubMenu(IMenuManager subMenuManager) {
		if (launchCodeGenAction != null) {
			subMenuManager.add(new ActionContributionItem(launchCodeGenAction));
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		if (selectionProvider != null) {
			if (launchCodeGenAction != null) {
				selectionProvider.removeSelectionChangedListener(launchCodeGenAction);
			}
		}
	}
}
