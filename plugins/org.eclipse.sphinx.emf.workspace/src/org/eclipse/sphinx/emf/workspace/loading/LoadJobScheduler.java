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
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadJob;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.loading.operations.AbstractLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileUnloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ModelLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ModelUnloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectReloadOperation;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;

@SuppressWarnings("rawtypes")
public class LoadJobScheduler {

	private LoadJobFactory loadJobFactory;

	protected LoadJobScheduler() {
		loadJobFactory = new LoadJobFactory();
	}

	public void scheduleModelLoadJob(AbstractLoadOperation operation) {
		if (operation instanceof FileLoadOperation) {
			scheduleModelLoadJob((FileLoadOperation) operation);
		} else if (operation instanceof ProjectLoadOperation) {
			scheduleModelLoadJob((ProjectLoadOperation) operation);
		} else if (operation instanceof ModelLoadOperation) {
			scheduleModelLoadJob((ModelLoadOperation) operation);
		} else if (operation instanceof FileReloadOperation) {
			scheduleModelLoadJob((FileReloadOperation) operation);
		} else if (operation instanceof ProjectReloadOperation) {
			scheduleModelLoadJob((ProjectReloadOperation) operation);
		} else if (operation instanceof FileUnloadOperation) {
			scheduleModelLoadJob((FileUnloadOperation) operation);
		} else if (operation instanceof ModelUnloadOperation) {
			scheduleModelLoadJob((ModelUnloadOperation) operation);
		} else {
			throw new UnsupportedOperationException(NLS.bind(Messages.error_unsupportedLoadOperation, operation.getClass().getSimpleName()));
		}
	}

	private void scheduleModelLoadJob(FileLoadOperation fileLoadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateLoadJob(fileLoadOperation.getFiles(), fileLoadOperation.getMetaModelDescriptor())) {
			return;
		}
		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob && job.getState() != Job.RUNNING) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof FileLoadOperation) {
					((FileLoadOperation) operation).addFiles(fileLoadOperation.getFiles());
					return;
				}
			}
		}
		// Otherwise, create new job
		Job job = loadJobFactory.createModelLoadJob(fileLoadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileLoadOperation.getRule());
		job.schedule();
	}

	private void scheduleModelLoadJob(ProjectLoadOperation prjLoadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateLoadJob(prjLoadOperation.getProjects(), prjLoadOperation.isIncludeReferencedProjects(),
				prjLoadOperation.getMetaModelDescriptor())) {
			return;
		}
		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob && job.getState() != Job.RUNNING) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ProjectLoadOperation) {
					((ProjectLoadOperation) operation).addProjects(prjLoadOperation.getProjects());
					return;
				}
			}
		}
		// Otherwise, create new job
		Job job = loadJobFactory.createModelLoadJob(prjLoadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(prjLoadOperation.getRule());
		job.schedule();
	}

	private void scheduleModelLoadJob(ModelLoadOperation modelLoadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateLoadJob(modelLoadOperation.getFiles(), modelLoadOperation.getModelDescriptor().getMetaModelDescriptor())) {
			return;
		}
		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ModelLoadOperation && job.getState() != Job.RUNNING) {
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

	private void scheduleModelLoadJob(FileReloadOperation fileReloadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateReloadJob(fileReloadOperation.getFiles(), fileReloadOperation.getMetaModelDescriptor())) {
			return;
		}

		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof FileReloadOperation && job.getState() != Job.RUNNING) {
					((FileReloadOperation) operation).addFiles(fileReloadOperation.getFiles());
					return;
				}
			}
		}

		// Otherwise, create new job
		Job job = loadJobFactory.createModelLoadJob(fileReloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileReloadOperation.getRule());
		job.schedule();
	}

	private void scheduleModelLoadJob(ProjectReloadOperation projectReloadOperation) {
		// Check first if job should really be created or not
		if (!loadJobFactory.shouldCreateReloadJob(projectReloadOperation.getProjects(), projectReloadOperation.isIncludeReferencedProjects(),
				projectReloadOperation.getMetaModelDescriptor())) {
			return;
		}

		// Add the files to an existing scheduled job if any
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob && job.getState() != Job.RUNNING) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ProjectReloadOperation) {
					((ProjectReloadOperation) operation).addProjects(projectReloadOperation.getProjects());
					return;
				}
			}
		}

		// Otherwise, create new job
		Job job = loadJobFactory.createModelLoadJob(projectReloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(projectReloadOperation.getRule());
		job.schedule();
	}

	private void scheduleModelLoadJob(FileUnloadOperation fileUnloadOperation) {
		Job job = loadJobFactory.createModelLoadJob(fileUnloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileUnloadOperation.getRule());
		job.schedule();
	}

	private void scheduleModelLoadJob(ModelUnloadOperation modelUnloadOperation) {
		Job job = loadJobFactory.createModelLoadJob(modelUnloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(modelUnloadOperation.getRule());
		job.schedule();

	}
}
