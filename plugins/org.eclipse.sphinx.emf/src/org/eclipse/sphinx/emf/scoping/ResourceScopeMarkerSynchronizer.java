/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - [374883] Improve handling of out-of-sync workspace files during descriptor initialization
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.scoping;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sphinx.emf.internal.scoping.IResourceScopeMarker;
import org.eclipse.sphinx.emf.internal.scoping.ResourceScopeMarkerSyncRequest;
import org.eclipse.sphinx.platform.resources.syncing.AbstractResourceSynchronizer;

/**
 * The ResourceScopeMarkerSynchronizer is in charge of synchronizing all the tasks relatives to
 * {@link IResourceScopeMarker Resource Scope Marker}s. see also {@link AbstractResourceSynchronizer}
 */
public class ResourceScopeMarkerSynchronizer extends AbstractResourceSynchronizer<IResourceScopeMarkerSyncRequest> {

	/**
	 * The singleton instance.
	 */
	public static final ResourceScopeMarkerSynchronizer INSTANCE = new ResourceScopeMarkerSynchronizer();

	/**
	 * Protected constructor for singleton pattern.
	 */
	protected ResourceScopeMarkerSynchronizer() {
	}

	public void start() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this,
				IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_BUILD
						| IResourceChangeEvent.POST_CHANGE);
	}

	public void stop() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/*
	 * @see org.eclipse.sphinx.platform.resources.syncing.AbstractResourceSynchronizer#createSyncRequest()
	 */
	@Override
	protected IResourceScopeMarkerSyncRequest createSyncRequest() {
		return new ResourceScopeMarkerSyncRequest();
	}
}
