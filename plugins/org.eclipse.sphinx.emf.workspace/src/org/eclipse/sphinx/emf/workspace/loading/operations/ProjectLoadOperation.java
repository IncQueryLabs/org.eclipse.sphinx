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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadingPerformanceStats;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

public class ProjectLoadOperation extends AbstractProjectLoadOperation {

	public ProjectLoadOperation(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		super(mmDescriptor != null ? Messages.job_loadingModel : Messages.job_loadingModels, projects, includeReferencedProjects, mmDescriptor);
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		runLoadProjects(getProjects(), isIncludeReferencedProjects(), getMetaModelDescriptor(), monitor);
	}

	protected void runLoadProjects(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(projects);

		for (IProject project : projects) {
			String taskName = mmDescriptor != null ? NLS.bind(Messages.task_loadingModelInProject, mmDescriptor.getName(), project.getName()) : NLS
					.bind(Messages.task_loadingModelsInProject, project.getName());
			SubMonitor progress = SubMonitor.convert(monitor, taskName, 100);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			Collection<IFile> files = ExtendedPlatform.getAllFiles(project, includeReferencedProjects);
			progress.worked(1);

			// No files found?
			if (files.size() == 0) {
				progress.done();
				continue;
			}

			String context = ModelLoadingPerformanceStats.ModelContext.CONTEXT_LOAD_PROJECT.getName() + " " + project.getName(); //$NON-NLS-1$
			ModelLoadingPerformanceStats.INSTANCE.openContext(context);
			runDetectAndLoadModelFiles(files, mmDescriptor, progress.newChild(99));
			ModelLoadingPerformanceStats.INSTANCE.closeAndLogCurrentContext();
		}
	}
}
