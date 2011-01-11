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
package org.eclipse.sphinx.emf.scoping;

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

	/*
	 * @see org.eclipse.sphinx.platform.resources.syncing.AbstractResourceSynchronizer#createSyncRequest()
	 */
	@Override
	protected IResourceScopeMarkerSyncRequest createSyncRequest() {
		return new ResourceScopeMarkerSyncRequest();
	}
}
