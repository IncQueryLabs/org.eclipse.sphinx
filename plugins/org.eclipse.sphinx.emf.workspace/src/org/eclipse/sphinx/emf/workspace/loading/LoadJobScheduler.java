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

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
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

	protected void scheduleModelLoadJob(FileLoadOperation fileLoadOperation) {
		// Check first if an existing FileLoadOperation job covers the files
		if (coveredByExistingLoadJob(fileLoadOperation)) {
			return;
		}

		// Add the files to an existing scheduled job if any. Otherwise, create new job.
		if (!addToExistingLoadJob(fileLoadOperation)) {
			Job job = createModelLoadJob(fileLoadOperation);
			job.setPriority(Job.BUILD);
			job.setRule(fileLoadOperation.getRule());
			job.schedule();
		}
	}

	protected void scheduleModelLoadJob(ProjectLoadOperation prjLoadOperation) {
		// Check first if an existing ProjectLoadOperation job covers the projects
		if (coveredByExistingLoadJob(prjLoadOperation)) {
			return;
		}
		// Add the projects to an existing scheduled job if any. Otherwise, create new job.
		if (!addToExistingLoadJob(prjLoadOperation)) {
			Job job = createModelLoadJob(prjLoadOperation);
			job.setPriority(Job.BUILD);
			job.setRule(prjLoadOperation.getRule());
			job.schedule();
		}
	}

	protected void scheduleModelLoadJob(ModelLoadOperation modelLoadOperation) {
		// Check first if an existing ModelLoadOperation job covers the model
		if (coveredByExistingLoadJob(modelLoadOperation)) {
			return;
		}

		// Otherwise, create new job
		Job job = createModelLoadJob(modelLoadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(modelLoadOperation.getRule());
		job.schedule();
	}

	protected void scheduleModelLoadJob(FileReloadOperation fileReloadOperation) {
		// Check first if an existing FileReloadOperation job covers the files
		if (coveredByExistingReloadJob(fileReloadOperation)) {
			return;
		}

		// Add the files to an existing scheduled job if any. Otherwise, create new job.
		if (!addToExistingReLoadJob(fileReloadOperation)) {
			Job job = createModelLoadJob(fileReloadOperation);
			job.setPriority(Job.BUILD);
			job.setRule(fileReloadOperation.getRule());
			job.schedule();
		}
	}

	protected void scheduleModelLoadJob(ProjectReloadOperation projectReloadOperation) {
		// Check first if an existing ProjectReloadOperation job covers the projects
		if (coveredByExistingReloadJob(projectReloadOperation)) {
			return;
		}

		// Add the projects to an existing scheduled job if any. Otherwise, create new job.
		if (!addToExistingReLoadJob(projectReloadOperation)) {
			Job job = createModelLoadJob(projectReloadOperation);
			job.setPriority(Job.BUILD);
			job.setRule(projectReloadOperation.getRule());
			job.schedule();
		}
	}

	protected void scheduleModelLoadJob(FileUnloadOperation fileUnloadOperation) {
		Job job = createModelLoadJob(fileUnloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(fileUnloadOperation.getRule());
		job.schedule();
	}

	protected void scheduleModelLoadJob(ModelUnloadOperation modelUnloadOperation) {
		Job job = createModelLoadJob(modelUnloadOperation);
		job.setPriority(Job.BUILD);
		job.setRule(modelUnloadOperation.getRule());
		job.schedule();
	}

	/**
	 * @param fileLoadOperation
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean coveredByExistingLoadJob(FileLoadOperation fileLoadOperation) {
		if (fileLoadOperation != null) {
			Collection<IFile> files = fileLoadOperation.getFiles();
			IMetaModelDescriptor metaModelDescriptor = fileLoadOperation.getMetaModelDescriptor();
			for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
				if (job instanceof ModelLoadJob) {
					ModelLoadJob loadJob = (ModelLoadJob) job;
					AbstractLoadOperation operation = loadJob.getOperation();
					if ((operation instanceof FileLoadOperation || operation instanceof ProjectLoadOperation)
							&& loadJob.covers(files, metaModelDescriptor)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param prjLoadOperation
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean coveredByExistingLoadJob(ProjectLoadOperation prjLoadOperation) {
		if (prjLoadOperation != null) {
			Collection<IProject> projects = prjLoadOperation.getProjects();
			boolean includeReferencedProjects = prjLoadOperation.isIncludeReferencedProjects();
			IMetaModelDescriptor metaModelDescriptor = prjLoadOperation.getMetaModelDescriptor();
			for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
				if (job instanceof ModelLoadJob) {
					ModelLoadJob loadJob = (ModelLoadJob) job;
					AbstractLoadOperation operation = loadJob.getOperation();
					if (operation instanceof ProjectLoadOperation && loadJob.covers(projects, includeReferencedProjects, metaModelDescriptor)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param modelLoadOperation
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean coveredByExistingLoadJob(ModelLoadOperation modelLoadOperation) {
		if (modelLoadOperation != null) {
			final IModelDescriptor modelDescriptor = modelLoadOperation.getModelDescriptor();
			final boolean includeReferencedProjects = modelLoadOperation.isIncludeReferencedScopes();
			final Collection<IFile> persistedFiles = modelDescriptor.getPersistedFiles(includeReferencedProjects);
			IMetaModelDescriptor metaModelDescriptor = modelLoadOperation.getMetaModelDescriptor();
			for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
				if (job instanceof ModelLoadJob) {
					ModelLoadJob loadJob = (ModelLoadJob) job;
					AbstractLoadOperation operation = loadJob.getOperation();
					if (operation instanceof ModelLoadOperation && loadJob.covers(persistedFiles, metaModelDescriptor)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param fileReloadOperation
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean coveredByExistingReloadJob(FileReloadOperation fileReloadOperation) {
		if (fileReloadOperation != null) {
			Collection<IFile> files = fileReloadOperation.getFiles();
			IMetaModelDescriptor metaModelDescriptor = fileReloadOperation.getMetaModelDescriptor();
			for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
				if (job instanceof ModelLoadJob) {
					ModelLoadJob loadJob = (ModelLoadJob) job;
					AbstractLoadOperation operation = loadJob.getOperation();
					if ((operation instanceof FileReloadOperation || operation instanceof ProjectReloadOperation)
							&& loadJob.covers(files, metaModelDescriptor)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param projectReloadOperation
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean coveredByExistingReloadJob(ProjectReloadOperation projectReloadOperation) {
		if (projectReloadOperation != null) {
			Collection<IProject> projects = projectReloadOperation.getProjects();
			boolean includeReferencedProjects = projectReloadOperation.isIncludeReferencedProjects();
			IMetaModelDescriptor metaModelDescriptor = projectReloadOperation.getMetaModelDescriptor();
			for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
				if (job instanceof ModelLoadJob) {
					ModelLoadJob loadJob = (ModelLoadJob) job;
					AbstractLoadOperation operation = loadJob.getOperation();
					if (operation instanceof ProjectReloadOperation && loadJob.covers(projects, includeReferencedProjects, metaModelDescriptor)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean addToExistingLoadJob(FileLoadOperation fileLoadOperation) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob && job.getState() != Job.RUNNING) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof FileLoadOperation) {
					((FileLoadOperation) operation).addFiles(fileLoadOperation.getFiles());
					return true;
				}
			}
		}
		return false;
	}

	private boolean addToExistingLoadJob(ProjectLoadOperation prjLoadOperation) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob && job.getState() != Job.RUNNING) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ProjectLoadOperation) {
					((ProjectLoadOperation) operation).addProjects(prjLoadOperation.getProjects());
					return true;
				}
			}
		}
		return false;
	}

	private boolean addToExistingReLoadJob(FileReloadOperation fileReloadOperation) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof FileReloadOperation && job.getState() != Job.RUNNING) {
					((FileReloadOperation) operation).addFiles(fileReloadOperation.getFiles());
					return true;
				}
			}
		}
		return false;
	}

	private boolean addToExistingReLoadJob(ProjectReloadOperation projectReloadOperation) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob && job.getState() != Job.RUNNING) {
				AbstractLoadOperation operation = ((ModelLoadJob) job).getOperation();
				if (operation instanceof ProjectReloadOperation) {
					((ProjectReloadOperation) operation).addProjects(projectReloadOperation.getProjects());
					return true;
				}
			}
		}
		return false;
	}

	private <T extends AbstractLoadOperation> Job createModelLoadJob(T operation) {
		return new ModelLoadJob<T>(operation);
	}
}
