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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sphinx.emf.explorer.IModelCommonContentProvider;

public class FullRefreshStrategy extends AbstractRefreshStrategy implements Runnable {

	public FullRefreshStrategy(IModelCommonContentProvider contentProvider, boolean affectsTreeViewerState) {
		super(contentProvider, affectsTreeViewerState);
	}

	@Override
	protected boolean shouldPerformSelectiveRefresh() {
		// Always perform full viewer refresh
		return false;
	}

	@Override
	protected void performSelectiveRefresh(StructuredViewer viewer) {
		// Never invoked, do nothing
	}
}