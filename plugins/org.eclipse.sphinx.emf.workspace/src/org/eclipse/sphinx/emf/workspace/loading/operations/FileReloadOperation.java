/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.workspace.loading.operations;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;

public class FileReloadOperation extends AbstractFileLoadOperation {

	private Collection<IFile> files;
	private boolean memoryOptimized;

	/**
	 * Constructor.
	 *
	 * @param files
	 *            The list of files this reloading operation is supposed to cover.
	 * @param mmDescriptor
	 *            The {@linkplain IMetaModelDescriptor meta-model descriptor} considered for reloading.
	 * @param memoryOptimized
	 *            Will activate the memory optimization option for unloading the resource. This is only available if the
	 *            resource is an XMLResource.
	 */
	public FileReloadOperation(Collection<IFile> files, IMetaModelDescriptor mmDescriptor, boolean memoryOptimized) {
		super(Messages.job_reloadingModelResources, files, mmDescriptor);
		this.memoryOptimized = memoryOptimized;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		runDetectAndReloadModelFiles(files, getMetaModelDescriptor(), memoryOptimized, monitor);
	}

	public boolean isMemoryOptimized() {
		return memoryOptimized;
	}
}
