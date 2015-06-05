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
package org.eclipse.sphinx.examples.hummingbird20.splitting.ui.actions.providers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.IHummingbirdExampleMenuConstants;
import org.eclipse.sphinx.examples.hummingbird.ide.ui.actions.providers.AbstractHummingbirdExampleActionProvider;
import org.eclipse.sphinx.examples.hummingbird20.splitting.ui.IHummingbirdModelElementSplittingMenuConstants;
import org.eclipse.sphinx.examples.hummingbird20.splitting.ui.actions.Hummingbirg20ModelElementSplittingAction;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;

public class Hummingbirg20ModelElementSplittingActionProvider extends AbstractHummingbirdExampleActionProvider {

	protected Hummingbirg20ModelElementSplittingAction modelElementSplittingAction;

	@Override
	protected void doInit() {
		modelElementSplittingAction = new Hummingbirg20ModelElementSplittingAction();
		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(modelElementSplittingAction);
			ISelection selection = selectionProvider.getSelection();
			IStructuredSelection structuredSelection = SelectionUtil.getStructuredSelection(selection);
			modelElementSplittingAction.selectionChanged(structuredSelection);
		}
	}

	@Override
	protected IMenuManager addSubMenu(IMenuManager contextMenuManager) {
		IMenuManager examplesMenuManager = super.addSubMenu(contextMenuManager);

		IMenuManager splittingMenuManager = examplesMenuManager.findMenuUsingPath(IHummingbirdModelElementSplittingMenuConstants.MENU_SPLITTING_ID);
		if (splittingMenuManager == null) {
			splittingMenuManager = new MenuManager(IHummingbirdModelElementSplittingMenuConstants.MENU_SPLITTING_LABEL,
					IHummingbirdModelElementSplittingMenuConstants.MENU_SPLITTING_ID);
			examplesMenuManager.appendToGroup(IHummingbirdExampleMenuConstants.GROUP_HUMMINGBIRD_EXAMPLES, splittingMenuManager);
		}
		return splittingMenuManager;
	}

	@Override
	protected void fillSubMenu(IMenuManager subMenuManager) {
		subMenuManager.add(modelElementSplittingAction);
	}

	@Override
	public void dispose() {
		if (selectionProvider != null) {
			if (modelElementSplittingAction != null) {
				selectionProvider.removeSelectionChangedListener(modelElementSplittingAction);
			}
		}
		super.dispose();
	}
}
