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
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.UnloadModelResourceJob;
import org.eclipse.sphinx.emf.workspace.loading.operations.AbstractLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileUnloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ModelLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.UnloadModelResourceOperation;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;

public class LoadJobFactory {

	public LoadJobFactory() {
		// Nothing to do
	}

	public Job createFileLoadJob(FileLoadOperation fileLoadOperation) {
		return new ModelLoadJob<FileLoadOperation>(fileLoadOperation);
	}

	public Job createProjectLoadJob(ProjectLoadOperation prjLoadOperation) {
		return new ModelLoadJob<ProjectLoadOperation>(prjLoadOperation);
	}

	public Job createModelLoadJob(ModelLoadOperation modelLoadOperation) {
		return new ModelLoadJob<ModelLoadOperation>(modelLoadOperation);
	}

	public Job createFileReloadJob(FileReloadOperation fileReloadOperation) {
		return new ModelLoadJob<FileReloadOperation>(fileReloadOperation);
	}

	public Job createProjectReloadJob(ProjectReloadOperation projectReloadOperation) {
		return new ModelLoadJob<ProjectReloadOperation>(projectReloadOperation);
	}

	public Job createFileUnloadJob(FileUnloadOperation fileUnloadOperation) {
		return new ModelLoadJob<FileUnloadOperation>(fileUnloadOperation);
	}

	public Job createUnloadModelResourceJob(UnloadModelResourceOperation unloadModelResourceOperation) {
		return new UnloadModelResourceJob(unloadModelResourceOperation);
	}

	/**
	 * @param files
	 * @param mmDescriptor
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean shouldCreateLoadJob(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				ModelLoadJob loadJob = (ModelLoadJob) job;
				AbstractLoadOperation operation = loadJob.getOperation();
				if ((operation instanceof FileLoadOperation || operation instanceof ProjectLoadOperation) && loadJob.covers(files, mmDescriptor)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean shouldCreateLoadJob(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				ModelLoadJob loadJob = (ModelLoadJob) job;
				AbstractLoadOperation operation = loadJob.getOperation();
				if (operation instanceof ProjectLoadOperation && loadJob.covers(projects, includeReferencedProjects, mmDescriptor)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param files
	 * @param mmDescriptor
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean shouldCreateReloadJob(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				ModelLoadJob loadJob = (ModelLoadJob) job;
				AbstractLoadOperation operation = loadJob.getOperation();
				if ((operation instanceof FileReloadOperation || operation instanceof ProjectReloadOperation) && loadJob.covers(files, mmDescriptor)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean shouldCreateReloadJob(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				ModelLoadJob loadJob = (ModelLoadJob) job;
				AbstractLoadOperation operation = loadJob.getOperation();
				if (operation instanceof ProjectReloadOperation && loadJob.covers(projects, includeReferencedProjects, mmDescriptor)) {
					return false;
				}
			}
		}
		return true;
	}
}
