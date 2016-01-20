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
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sphinx.emf.explorer.IModelCommonContentProvider;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;

public class ModelObjectRefreshStrategy extends AbstractRefreshStrategy implements Runnable {

	private static final int MAX_INDIVIDUAL_MODEL_OBJECT_REFRESHES = 100;

	private Set<EObject> modelObjectsToRefresh = null;

	public ModelObjectRefreshStrategy(IModelCommonContentProvider contentProvider) {
		super(contentProvider, false);
	}

	public Set<EObject> getModelObjectsToRefresh() {
		if (modelObjectsToRefresh == null) {
			modelObjectsToRefresh = new HashSet<EObject>();
		}
		return modelObjectsToRefresh;
	}

	@Override
	protected boolean shouldPerformSelectiveRefresh() {
		return getModelObjectsToRefresh().size() < MAX_INDIVIDUAL_MODEL_OBJECT_REFRESHES;
	}

	@Override
	protected void performSelectiveRefresh(StructuredViewer viewer) {
		for (EObject modelObject : getModelObjectsToRefresh()) {
			if (contentProvider.isPossibleChild(modelObject)) {
				// Is current object a model content root?
				Resource modelResource = EcoreResourceUtil.getResource(modelObject);
				List<Object> modelContentRoots = contentProvider.getModelContentRoots(modelResource);
				if (modelContentRoots.contains(modelObject)) {
					// Refresh corresponding workspace resource
					IResource workspaceResource = contentProvider.getWorkspaceResource(modelResource);
					if (workspaceResource != null && workspaceResource.isAccessible()) {
						if (contentProvider.isTriggerPoint(workspaceResource)) {
							viewer.refresh(workspaceResource, true);
						}
					}
				} else {
					// Directly refresh the object
					viewer.refresh(modelObject, true);
				}
			}
		}
	}
}