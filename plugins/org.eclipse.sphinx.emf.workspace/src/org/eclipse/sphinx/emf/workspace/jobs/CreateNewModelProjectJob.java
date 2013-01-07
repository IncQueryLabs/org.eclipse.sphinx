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
package org.eclipse.sphinx.emf.workspace.jobs;

import java.net.URI;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

/**
 * A job capable to create a new model project with given nature.<br/>
 * It will set by default the priority to Job.BUILD and the rule to the workspace root<br/>
 */
public class CreateNewModelProjectJob extends WorkspaceJob {

	protected IProject project;

	private URI location;

	private IProject[] referencedProjects;

	private IAdaptable uiInfo;

	private IMetaModelDescriptor metaModelDescriptor;

	/**
	 * The id of the {@link IProjectNature project nature}.
	 */
	private String projectNatureId;

	private IProjectWorkspacePreference<AbstractMetaModelDescriptor> projectWorkspacePreference;

	/**
	 * Create the job
	 * 
	 * @param name
	 *            the name of the job
	 */
	public CreateNewModelProjectJob(String name) {
		this(name, null);
	}

	/**
	 * Create the job
	 * 
	 * @param name
	 *            the name of the job
	 * @param projectNatureId
	 *            the id of the project nature
	 */
	public CreateNewModelProjectJob(String name, String projectNatureId) {
		super(name);

		this.projectNatureId = projectNatureId;
		setPriority(Job.BUILD);
		setRule(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Create the job
	 * 
	 * @param name
	 *            the name of the job, should not be null
	 * @param project
	 *            the project, should not be null
	 * @param location
	 *            the location where the project will be created. If null the default location will be used.
	 * @param metaModelDescriptor
	 *            the meta-model version that will be used by this project
	 * @param projectNatureId
	 *            the id of the project nature
	 */
	@SuppressWarnings("unchecked")
	public CreateNewModelProjectJob(String name, IProject project, URI location, IMetaModelDescriptor metaModelDescriptor, String projectNatureId,
			IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> projectWorkspacePreference) {
		this(name, projectNatureId);
		this.project = project;
		this.location = location;
		this.metaModelDescriptor = metaModelDescriptor;
		this.projectWorkspacePreference = (IProjectWorkspacePreference<AbstractMetaModelDescriptor>) projectWorkspacePreference;
	}

	/**
	 * @return the project
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Set the project. The project does not have to exists
	 * 
	 * @param project
	 *            the project to set, should not be null
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}

	/**
	 * Set the location where the project should be created. If null, the default location will be used
	 * 
	 * @param location
	 *            the location to set
	 */
	public void setLocation(URI location) {
		this.location = location;
	}

	/**
	 * Set an adaptable to be used by
	 * {@link IOperationHistory#execute(org.eclipse.core.commands.operations.IUndoableOperation, IProgressMonitor, IAdaptable)}
	 * <br/>
	 * At a minimum the adaptable should be able to adapt to org.eclipse.swt.widgets.Shell.<br/>
	 * Having a shell, such an adaptable can be obtained by <code>WorkspaceUndoUtil.getUIInfoAdapter(shell)</code><br/>
	 * If null, a default shell will be created to ask the user for confirmation.
	 * 
	 * @param uiInfo
	 *            the uiInfo adaptable to set
	 */
	public void setUIInfoAdaptable(IAdaptable uiInfo) {
		this.uiInfo = uiInfo;
	}

	/**
	 * @return the uiInfoAdaptable
	 */
	public IAdaptable getUIInfoAdaptable() {
		return uiInfo;
	}

	/**
	 * @return the referencedProjects
	 */
	public IProject[] getReferencedProjects() {
		return referencedProjects;
	}

	/**
	 * Set the referenced project. Can be null or an empty array
	 * 
	 * @param referencedProjects
	 *            the referencedProjects to set
	 */
	public void setReferencedProjects(IProject[] referencedProjects) {
		this.referencedProjects = referencedProjects;
	}

	/**
	 * @return the metaModelDescriptor
	 */
	public IMetaModelDescriptor getMetaModelDescriptor() {
		return metaModelDescriptor;
	}

	/**
	 * @param metaModelDescriptor
	 *            the release descriptor to set
	 */
	public void setMetaModelDescriptor(IMetaModelDescriptor metaModelDescriptor) {
		this.metaModelDescriptor = metaModelDescriptor;
	}

	public String getProjectNatureId() {
		return projectNatureId;
	}

	public void setProjectNatureId(String projectNatureId) {
		this.projectNatureId = projectNatureId;
	}

	/*
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, getName(), 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		createNewProject(progress.newChild(70));
		addNatures(progress.newChild(15));

		if (projectWorkspacePreference != null && metaModelDescriptor instanceof AbstractMetaModelDescriptor) {
			projectWorkspacePreference.setInProject(project, (AbstractMetaModelDescriptor) metaModelDescriptor);
		}
		progress.worked(15);

		return Status.OK_STATUS;
	}

	/**
	 * Create the project on disk
	 * 
	 * @param monitor
	 */
	protected void createNewProject(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}
		progress.subTask(Messages.job_creatingNewProject);

		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
		description.setLocationURI(location);

		// Update the referenced project if provided
		if (referencedProjects != null && referencedProjects.length > 0) {
			description.setReferencedProjects(referencedProjects);
		}

		project.create(description, progress.newChild(50));
		project.open(IResource.NONE, progress.newChild(50));
	}

	/**
	 * Set the nature to the project created by the job.
	 * 
	 * @param monitor
	 */
	protected void addNatures(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		if (projectNatureId != null) {
			progress.subTask(Messages.job_AddProjectNatures);
			ExtendedPlatform.addNature(project, projectNatureId, progress.newChild(100));
		}
	}
}
