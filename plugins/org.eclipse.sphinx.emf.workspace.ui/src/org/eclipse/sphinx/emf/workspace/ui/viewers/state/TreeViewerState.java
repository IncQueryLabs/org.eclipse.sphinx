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
package org.eclipse.sphinx.emf.workspace.ui.viewers.state;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers.ITreeElementStateProvider;

public class TreeViewerState implements ITreeViewerState {

	protected List<ITreeElementStateProvider> expandedElements = null;
	protected List<ITreeElementStateProvider> selectedElements = null;

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.viewers.state.ITreeViewerState#getExpandedElements()
	 */
	@Override
	public List<ITreeElementStateProvider> getExpandedElements() {
		if (expandedElements == null) {
			expandedElements = new ArrayList<ITreeElementStateProvider>();
		}
		return expandedElements;
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.viewers.state.ITreeViewerState#getSelectedElements()
	 */
	@Override
	public List<ITreeElementStateProvider> getSelectedElements() {
		if (selectedElements == null) {
			selectedElements = new ArrayList<ITreeElementStateProvider>();
		}
		return selectedElements;
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.viewers.state.ITreeViewerState#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getExpandedElements().isEmpty() && getSelectedElements().isEmpty();
	}
}
