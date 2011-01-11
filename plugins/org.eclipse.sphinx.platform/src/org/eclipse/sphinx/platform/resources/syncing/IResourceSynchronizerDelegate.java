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
package org.eclipse.sphinx.platform.resources.syncing;

import org.eclipse.sphinx.platform.resources.IResourceChangeHandler;

public interface IResourceSynchronizerDelegate<T extends IResourceSyncRequest> extends IResourceChangeHandler {

	void setSyncRequest(T syncRequest);
}
