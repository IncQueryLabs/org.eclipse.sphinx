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
package org.eclipse.sphinx.emf.ui.actions.providers;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sphinx.emf.ui.actions.BasicOpenInEditorAction;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * Implementation of basic {@linkplain BasicActionProvider action provider} providing a basic
 * {@linkplain BasicOpenInEditorAction open in editor} action. Such action is directly added as a global action in the
 * contextual menu so that default behavior on double-click could be overridden (opening selected element in editor
 * instead of expand/collapse it).
 */
public class BasicEditorActionProvider extends BasicActionProvider {

	/**
	 * This is the action used to open current selection in an editor.
	 */
	protected BasicOpenInEditorAction openInEditorAction;

	protected IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent evt) {
			ISelection selection = evt.getSelection();
			if (selection instanceof IStructuredSelection) {
				if (openInEditorAction.isEnabled()) {
					openInEditorAction.run();
				}
			}
		}
	};

	@Override
	public void doInit() {
		openInEditorAction = new BasicOpenInEditorAction();

		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(openInEditorAction);

			ISelection selection = selectionProvider.getSelection();
			IStructuredSelection structuredSelection = SelectionUtil.getStructuredSelection(selection);
			openInEditorAction.updateSelection(structuredSelection);
		}

		if (viewer instanceof StructuredViewer) {
			((StructuredViewer) viewer).addDoubleClickListener(doubleClickListener);
		}
	}

	@Override
	public void fillContextMenu(IMenuManager menuManager) {
		super.fillContextMenu(menuManager);

		// Contribute open in editor action
		menuManager.appendToGroup(ICommonMenuConstants.GROUP_OPEN, new ActionContributionItem(openInEditorAction));
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);

		// Set Open in Editor action as global open action in order to override default double click behavior which is
		// to expand/collapse selected element upon double click (see
		// org.eclipse.ui.navigator.CommonNavigator#handleDoubleClick() for details)
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openInEditorAction);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (viewer instanceof StructuredViewer) {
			((StructuredViewer) viewer).removeDoubleClickListener(doubleClickListener);
		}
		if (selectionProvider != null && openInEditorAction != null) {
			selectionProvider.removeSelectionChangedListener(openInEditorAction);
		}
	}
}