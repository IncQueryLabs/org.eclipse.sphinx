/**
 * <copyright>
 * 
 * Copyright (c) 2013 itemis and others.
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
package org.eclipse.sphinx.emf.workspace.loading;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sphinx.emf.loading.IModelLoadService;
import org.eclipse.sphinx.emf.model.IModelDescriptor;

public class ModelLoadService implements IModelLoadService {

	/**
	 * @see org.eclipse.sphinx.emf.model.IModelLoadService.loadModel(IModelDescriptor, boolean, IProgressMonitor)
	 */
	public void loadModel(IModelDescriptor modelDescriptor, boolean async, IProgressMonitor monitor) {
		ModelLoadManager.INSTANCE.loadModel(modelDescriptor, async, monitor);
	}

	/**
	 * @see org.eclipse.sphinx.emf.model.IModelLoadService.loadModels(Collection<IModelDescriptor>, boolean,
	 *      IProgressMonitor)
	 */
	public void loadModels(Collection<IModelDescriptor> modelDescriptors, boolean async, IProgressMonitor monitor) {
		ModelLoadManager.INSTANCE.loadModels(modelDescriptors, true, async, monitor);
	}
}
