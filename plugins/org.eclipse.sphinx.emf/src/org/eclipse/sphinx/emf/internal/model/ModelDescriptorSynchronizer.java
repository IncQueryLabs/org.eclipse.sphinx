/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Avoid usage of Object.finalize
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.model;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sphinx.platform.resources.syncing.AbstractResourceSynchronizer;

public class ModelDescriptorSynchronizer extends AbstractResourceSynchronizer<IModelDescriptorSyncRequest> {

	/**
	 * The singleton instance.
	 */
	public static final ModelDescriptorSynchronizer INSTANCE = new ModelDescriptorSynchronizer();

	public void start() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this,
				IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_BUILD
						| IResourceChangeEvent.POST_CHANGE);
	}

	public void stop() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/**
	 * Protected constructor for singleton pattern.
	 */
	protected ModelDescriptorSynchronizer() {
		addDelegate(BasicModelDescriptorSynchronizerDelegate.INSTANCE);
	}

	@Override
	protected IModelDescriptorSyncRequest createSyncRequest() {
		return new ModelDescriptorSyncRequest();
	}
}
