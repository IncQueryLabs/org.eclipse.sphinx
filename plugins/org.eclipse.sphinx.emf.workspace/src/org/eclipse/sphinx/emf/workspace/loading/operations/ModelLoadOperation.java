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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ModelLoadOperation extends AbstractFileLoadOperation {

	private IModelDescriptor modelDescriptor;

	public ModelLoadOperation(IModelDescriptor modelDescriptor, Collection<IFile> files) {
		super(Messages.job_loadingModelResources, files, modelDescriptor.getMetaModelDescriptor());
		this.modelDescriptor = modelDescriptor;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		runCollectAndLoadModelFiles(getModelDescriptor().getEditingDomain(), getFiles(), monitor);
	}

	public IModelDescriptor getModelDescriptor() {
		return modelDescriptor;
	}

	private void runCollectAndLoadModelFiles(TransactionalEditingDomain editingDomain, Collection<IFile> files, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(files);

		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_loadingModelFiles, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Collect model files to load
		SubMonitor collectProgress = progress.newChild(1).setWorkRemaining(files.size());
		Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad = new HashMap<TransactionalEditingDomain, Collection<IFile>>();
		for (IFile file : files) {
			try {
				// Exclude inaccessible files
				if (file.isAccessible()) {
					// Ignore files that already have been loaded
					if (!EcorePlatformUtil.isFileLoaded(file)) {
						// Retrieve already existing load file request for given editing domain
						Collection<IFile> filesToLoadInEditingDomain = filesToLoad.get(editingDomain);
						if (filesToLoadInEditingDomain == null) {
							filesToLoadInEditingDomain = new HashSet<IFile>();
							filesToLoad.put(editingDomain, filesToLoadInEditingDomain);
						}
						// Add current file to load file request
						filesToLoadInEditingDomain.add(file);
					}
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			collectProgress.worked(1);
			if (collectProgress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		// Nothing to load?
		if (filesToLoad.size() == 0) {
			progress.done();
			// TODO Surround with appropriate tracing option
			// System.out.println("[ModelLoadManager#runCollectAndLoadModelFiles()] No model files to be loaded");
			return;
		}
		runLoadModelFiles(filesToLoad, progress.newChild(99));
		// TODO Surround with appropriate tracing option
		// System.out.println("[ModelLoadManager#runCollectAndLoadModelFiles()] Loaded " +
		// getFilesToLoadCount(filesToLoad) + " model file(s)");
	}
}
