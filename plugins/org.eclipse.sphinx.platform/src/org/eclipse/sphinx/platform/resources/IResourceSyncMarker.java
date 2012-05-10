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

import org.eclipse.sphinx.platform.internal.Activator;

public interface IResourceSyncMarker {

	/**
	 * Resource out-of-sync problem marker type.
	 */
	public static final String RESOURCE_SYNC_PROBLEM = Activator.PLUGIN_ID + ".resourcesyncproblemmarker"; //$NON-NLS-1$

}
