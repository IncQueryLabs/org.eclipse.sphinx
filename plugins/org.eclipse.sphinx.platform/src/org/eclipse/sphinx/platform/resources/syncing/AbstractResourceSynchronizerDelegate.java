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

import org.eclipse.core.runtime.Assert;
import org.eclipse.sphinx.platform.resources.DefaultResourceChangeHandler;

public abstract class AbstractResourceSynchronizerDelegate<T extends IResourceSyncRequest> extends DefaultResourceChangeHandler implements
		IResourceSynchronizerDelegate<T> {

	protected T syncRequest;

	@Override
	public void setSyncRequest(T syncRequest) {
		Assert.isNotNull(syncRequest);
		this.syncRequest = syncRequest;
	}
}
