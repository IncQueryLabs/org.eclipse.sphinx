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
package org.eclipse.sphinx.emf.workspace.syncing;

import org.eclipse.sphinx.emf.workspace.internal.syncing.BasicModelSynchronizerDelegate;
import org.eclipse.sphinx.emf.workspace.internal.syncing.ModelSyncRequest;
import org.eclipse.sphinx.platform.resources.syncing.AbstractResourceSynchronizer;

public class ModelSynchronizer extends AbstractResourceSynchronizer<IModelSyncRequest> {

	/**
	 * The singleton instance.
	 */
	public static final ModelSynchronizer INSTANCE = new ModelSynchronizer();

	/**
	 * Protected constructor for singleton pattern.
	 */
	protected ModelSynchronizer() {
		BasicModelSynchronizerDelegate delegate = new BasicModelSynchronizerDelegate();
		addDelegate(delegate);
	}

	@Override
	protected IModelSyncRequest createSyncRequest() {
		return new ModelSyncRequest();
	}
}
