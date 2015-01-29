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
package org.eclipse.sphinx.emf.search.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.search2.internal.ui.SearchMessages;

@SuppressWarnings("restriction")
public class ExpandAllAction extends Action {

	private TreeViewer treeViewer;

	public ExpandAllAction() {
		super(SearchMessages.ExpandAllAction_label);
		setToolTipText(SearchMessages.ExpandAllAction_tooltip);
		SearchPluginImages.setImageDescriptors(this, SearchPluginImages.T_LCL, SearchPluginImages.IMG_LCL_SEARCH_EXPAND_ALL);
	}

	public void setViewer(TreeViewer viewer) {
		treeViewer = viewer;
	}

	@Override
	public void run() {
		if (treeViewer != null) {
			treeViewer.expandAll();
		}
	}
}
