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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sphinx.emf.explorer.IModelCommonContentProvider;

public class WorkspaceResourceRefreshStrategy extends AbstractRefreshStrategy implements Runnable {

	private static final int MAX_INDIVIDUAL_WORKSPACE_RESOURCE_REFRESHES = 20;

	private Set<IResource> workspaceResourcesToRefresh = null;

	public WorkspaceResourceRefreshStrategy(IModelCommonContentProvider contentProvider, boolean preserveTreeViewerState) {
		super(contentProvider, preserveTreeViewerState);
	}

	public Set<IResource> getWorkspaceResourcesToRefresh() {
		if (workspaceResourcesToRefresh == null) {
			workspaceResourcesToRefresh = new HashSet<IResource>();
		}
		return workspaceResourcesToRefresh;
	}

	@Override
	protected boolean shouldPerformSelectiveRefresh() {
		return getWorkspaceResourcesToRefresh().size() < MAX_INDIVIDUAL_WORKSPACE_RESOURCE_REFRESHES;
	}

	@Override
	protected void performSelectiveRefresh(StructuredViewer viewer) {
		for (IResource workspaceResource : getWorkspaceResourcesToRefresh()) {
			if (workspaceResource != null && workspaceResource.isAccessible()) {
				if (contentProvider.isTriggerPoint(workspaceResource)) {
					viewer.refresh(workspaceResource, true);
				}
			}
		}
	}
}