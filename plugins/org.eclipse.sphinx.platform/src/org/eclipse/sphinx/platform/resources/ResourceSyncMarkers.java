/**
 * <copyright>
 * 
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.platform.resources;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.internal.messages.Messages;

/**
 * Utility class to work with out-of-sync resource problem markers.
 */
public class ResourceSyncMarkers {
	/**
	 * Checks wether the resource is in-sync with the file system and updates its out-of-sync marker accordingly. If the
	 * resource is out-of-sync a problem marker is created otherwise any previously existing markers are removed. This
	 * method will not synchronize the resource.
	 * 
	 * @param markerJob
	 *            the marker job that will be used to asynchronously create or delete markers
	 * @param resource
	 *            the resource to check
	 * @return true if the resource is in-sync false otherwise.
	 * @see IResourceSyncMarker#RESOURCE_SYNC_PROBLEM
	 */
	public static boolean updateMarker(MarkerJob markerJob, IResource resource) {
		if (resource.isSynchronized(IResource.DEPTH_ZERO)) {
			// remove out-of-sync markers that may have been created previously
			markerJob.deleteMarker(resource, IResourceSyncMarker.RESOURCE_SYNC_PROBLEM);
			return true;
		} else {
			// create an out-of-sync marker warning the user
			markerJob.createMarker(resource, IResourceSyncMarker.RESOURCE_SYNC_PROBLEM, IMarker.SEVERITY_WARNING,
					NLS.bind(Messages.warning_resourceIsOutOfSync, resource.getFullPath()));
			return false;
		}
	}
}
