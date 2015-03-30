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
package org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers;

import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.navigator.CommonViewer;

public abstract class AbstractTreeElementStateProvider implements ITreeElementStateProvider {

	protected TreeViewer viewer;

	public AbstractTreeElementStateProvider(TreeViewer viewer) {
		Assert.isNotNull(viewer);

		this.viewer = viewer;
	}

	protected boolean canGetChildren(Object element) {
		if (element == null) {
			return false;
		}

		if (viewer instanceof CommonViewer) {
			Set<?> contentExtensions = ((CommonViewer) viewer).getNavigatorContentService().findContentExtensionsByTriggerPoint(element);
			return !contentExtensions.isEmpty();
		}

		return true;
	}

	@Override
	public boolean isResolved() {
		if (hasUnderlyingModel() && !isUnderlyingModelLoaded()) {
			return !canUnderlyingModelBeLoaded();
		} else {
			// Provided tree element doesn't exist?
			if (isStale()) {
				// Provider is fully resolved
				return true;
			} else {
				// Provider is fully resolved as soon as navigator content associated with tree element becomes
				// available
				return canGetChildren(getTreeElement());
			}
		}
	}

	@Override
	public boolean canBeExpanded() {
		if (isStale()) {
			return false;
		} else {
			Object element = getTreeElement();
			return element != null && viewer.isExpandable(element);
		}
	}

	@Override
	public boolean isExpanded() {
		if (isStale()) {
			return false;
		} else {
			Object element = getTreeElement();
			return element != null && viewer.getExpandedState(element);
		}
	}
}