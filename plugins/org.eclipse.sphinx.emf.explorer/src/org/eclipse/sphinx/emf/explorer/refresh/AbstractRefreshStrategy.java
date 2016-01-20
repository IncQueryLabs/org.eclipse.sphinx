/**
 * <copyright>
 *
 * Copyright (c) 2016 itemis and others.
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
package org.eclipse.sphinx.emf.explorer.refresh;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.explorer.IModelCommonContentProvider;
import org.eclipse.sphinx.emf.workspace.ui.viewers.state.ITreeViewerState;

/**
 * Represents a refresh operation on a JFace {@link Viewer viewer} that follows some specific strategy. Optionally
 * records the underlying viewer's expansion and selection state before the refresh operation and restored it
 * thereafter.
 * <p>
 * Clients are supposed to subclass this class and implement its {@link #refresh(Viewer)} method. In the most simple
 * case, this can be done by just invoking {@link Viewer#refresh()}. However, the main purpose of this class is to
 * enable clients to implement a some more sophisticated logic that performs a selective refresh of the viewer items
 * that are actually need to be refreshed which can save a lot of runtime-performance in many situations.
 * </p>
 */
public abstract class AbstractRefreshStrategy implements Runnable {

	protected final IModelCommonContentProvider contentProvider;

	private final boolean preserveTreeViewerState;

	public AbstractRefreshStrategy(IModelCommonContentProvider contentProvider, boolean preserveTreeViewerState) {
		Assert.isNotNull(contentProvider);
		this.contentProvider = contentProvider;
		this.preserveTreeViewerState = preserveTreeViewerState;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		final Viewer viewer = contentProvider.getViewer();
		if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
						// Record current tree viewer expansion and selection state if needed
						ITreeViewerState state = null;
						if (preserveTreeViewerState) {
							state = contentProvider.recordViewerState();
						}

						// Perform viewer refresh relying on implemented refresh strategy
						refresh(viewer);

						// Restore previous tree viewer expansion and selection state if needed
						if (preserveTreeViewerState) {
							contentProvider.applyViewerState(state);
						}
					}
				}
			});
		}
	}

	protected final void refresh(Viewer viewer) {
		/*
		 * Performance optimization: Perform selective refresh on affected viewer items only if number of the latter is
		 * reasonably low.
		 */
		if (viewer instanceof StructuredViewer && shouldPerformSelectiveRefresh()) {
			// Perform a selective viewer refresh
			performSelectiveRefresh((StructuredViewer) viewer);
		} else {
			// Perform a full viewer refresh otherwise
			viewer.refresh();
		}
	}

	protected abstract boolean shouldPerformSelectiveRefresh();

	protected abstract void performSelectiveRefresh(StructuredViewer viewer);
}