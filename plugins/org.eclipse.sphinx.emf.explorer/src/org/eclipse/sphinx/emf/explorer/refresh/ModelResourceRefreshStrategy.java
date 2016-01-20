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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sphinx.emf.explorer.IModelCommonContentProvider;

public class ModelResourceRefreshStrategy extends AbstractRefreshStrategy implements Runnable {

	private static final int MAX_INDIVIDUAL_MODEL_RESOURCE_REFRESHES = 20;

	private Set<Resource> modelResourcesToRefresh = null;

	public ModelResourceRefreshStrategy(IModelCommonContentProvider contentProvider, boolean affectsTreeViewerState) {
		super(contentProvider, affectsTreeViewerState);
	}

	public Set<Resource> getModelResourcesToRefresh() {
		if (modelResourcesToRefresh == null) {
			modelResourcesToRefresh = new HashSet<Resource>();
		}
		return modelResourcesToRefresh;
	}

	@Override
	protected boolean shouldPerformSelectiveRefresh() {
		return getModelResourcesToRefresh().size() < MAX_INDIVIDUAL_MODEL_RESOURCE_REFRESHES;
	}

	@Override
	protected void performSelectiveRefresh(StructuredViewer viewer) {
		for (Resource modelResource : getModelResourcesToRefresh()) {
			IResource workspaceResource = contentProvider.getWorkspaceResource(modelResource);
			if (workspaceResource != null && workspaceResource.isAccessible()) {
				/*
				 * !! Important Note !! Refresh viewer if file behind resource matches trigger point condition. Refresh
				 * viewer regardless of that if resource has just been unloaded because the underlying file might not
				 * match the trigger condition anymore in this case (e.g. if the file's XML namespace or content type
				 * has been changed)
				 */
				if (contentProvider.isTriggerPoint(workspaceResource) || !modelResource.isLoaded()) {
					viewer.refresh(workspaceResource, true);
				}
			}
		}
	}
}