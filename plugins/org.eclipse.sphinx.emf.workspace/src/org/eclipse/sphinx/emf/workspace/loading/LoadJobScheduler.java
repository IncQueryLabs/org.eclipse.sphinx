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

package org.eclipse.sphinx.emf.workspace.loading;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadJob;
import org.eclipse.sphinx.emf.workspace.loading.operations.AbstractLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileUnloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ModelLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.UnloadModelResourceOperation;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;

@SuppressWarnings("rawtypes")
public class LoadJobScheduler {

	private LoadJobFactory loadJobFactory;

	protected LoadJobScheduler() {
		loadJobFactory = new LoadJobFactory();
	}

	public void scheduleProjectLoadJob(ProjectLoadOperation prjLoadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateLoadJob(prjLoadOperation.getProjects(), prjLoadOperation.isIncludeReferencedProjects(),
				prjLoadOperation.getMetaModelDescriptor())) {
			return;
		}
		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ProjectLoadOperation) {
					((ProjectLoadOperation) operation).addProjects(prjLoadOperation.getProjects());
					return;
				}
			}
		}
		// Otherwise, create new job
		Job job = loadJobFactory.createProjectLoadJob(prjLoadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(prjLoadOperation.getRule());
		job.schedule();
	}

	public void scheduleFileLoadJob(FileLoadOperation fileLoadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateLoadJob(fileLoadOperation.getFiles(), fileLoadOperation.getMetaModelDescriptor())) {
			return;
		}
		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof FileLoadOperation) {
					((FileLoadOperation) operation).addFiles(fileLoadOperation.getFiles());
					return;
				}
			}
		}
		// Otherwise, create new job
		Job job = loadJobFactory.createFileLoadJob(fileLoadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileLoadOperation.getRule());
		job.schedule();
	}

	public void scheduleProjectReloadJob(ProjectReloadOperation projectReloadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateReloadJob(projectReloadOperation.getProjects(), projectReloadOperation.isIncludeReferencedProjects(),
				projectReloadOperation.getMetaModelDescriptor())) {
			return;
		}

		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ProjectReloadOperation) {
					((ProjectReloadOperation) operation).addProjects(projectReloadOperation.getProjects());
					return;
				}
			}
		}

		// Otherwise, create new job
		Job job = loadJobFactory.createProjectReloadJob(projectReloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(projectReloadOperation.getRule());
		job.schedule();
	}

	public void scheduleFileReloadJob(FileReloadOperation fileReloadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateReloadJob(fileReloadOperation.getFiles(), fileReloadOperation.getMetaModelDescriptor())) {
			return;
		}

		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof FileReloadOperation) {
					((FileReloadOperation) operation).addFiles(fileReloadOperation.getFiles());
					return;
				}
			}
		}

		// Otherwise, create new job
		Job job = loadJobFactory.createFileReloadJob(fileReloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileReloadOperation.getRule());
		job.schedule();
	}

	public void scheduleModelLoadJob(ModelLoadOperation modelLoadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateLoadJob(modelLoadOperation.getFiles(), modelLoadOperation.getModelDescriptor().getMetaModelDescriptor())) {
			return;
		}
		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ModelLoadOperation) {
					((ModelLoadOperation) operation).addFiles(modelLoadOperation.getFiles());
					return;
				}
			}
		}
		// Otherwise, create new job
		Job job = loadJobFactory.createModelLoadJob(modelLoadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(modelLoadOperation.getRule());
		job.schedule();
	}

	public void scheduleUnloadModelResourceJob(UnloadModelResourceOperation unloadModelResourceOperation) {
		Job job = loadJobFactory.createUnloadModelResourceJob(unloadModelResourceOperation);
		job.setPriority(Job.BUILD);
		job.setRule(unloadModelResourceOperation.getRule());
		job.schedule();

	}

	public void scheduleFileUnloadJob(FileUnloadOperation fileUnloadOperation) {
		Job job = loadJobFactory.createFileUnloadJob(fileUnloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileUnloadOperation.getRule());
		job.schedule();
	}
}
