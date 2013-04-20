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
 *     itemis - [406062] Removal of the required project nature parameter in NewModelFileCreationPage constructor and CreateNewModelProjectJob constructor
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelProjectJob;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * Abstract wizard for creating a new model project in the workspace. Two pages are added for selecting the properties
 * of the model project and references to other projects. When being finished a
 * {@linkplain createCreateNewModelProjectJob new model project job} is run to create the model project in the
 * workspace.
 */
public abstract class AbstractNewModelProjectWizard<T extends IMetaModelDescriptor> extends BasicNewProjectResourceWizard {

	protected WizardNewProjectCreationPage mainPage;
	protected WizardNewProjectReferencePage referencePage;

	protected T metaModelVersionDescriptor;
	protected IProjectWorkspacePreference<T> metaModelVersionPreference;

	/**
	 * Creates a wizard for creating a new model project in the workspace.
	 */
	public AbstractNewModelProjectWizard() {
	}

	/**
	 * Creates a wizard for creating a new model project in the workspace with required metamodel version descriptor and
	 * metamodel version preference.
	 * 
	 * @param metaModelVersionDescriptor
	 *            the meta-model version that the project will be used for; when set to <code>null</code> no metamodel
	 *            version will be configured
	 * @param metaModelVersionPreference
	 *            the metamodel version preference of the project; when set to <code>null</code> no metamodel version
	 *            will be configured s
	 */
	public AbstractNewModelProjectWizard(T metaModelVersionDescriptor, IProjectWorkspacePreference<T> metaModelVersionPreference) {
		this.metaModelVersionDescriptor = metaModelVersionDescriptor;
		this.metaModelVersionPreference = metaModelVersionPreference;
	}

	/*
	 * @see org.eclipse.sphinx.emf.workspace.ui.wizards.BasicNewModelFileWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(Messages.wizard_newModelProject_title);
	}

	/*
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#addPages()
	 */
	@Override
	public void addPages() {
		mainPage = createMainPage(true);
		if (mainPage == null) {
			mainPage = createMainPage();
		}
		Assert.isNotNull(mainPage);
		addPage(mainPage);

		// Only add page if there are already projects in the workspace
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
			referencePage = createReferencePage();
			addPage(referencePage);
		}
	}

	/**
	 * Creates the {@link WizardNewProjectCreationPage main page} for the creation of the new model project.
	 * 
	 * @param createWorkingSetGroup
	 *            <code>true</code> if a group for choosing a working set for the new model project should be added to
	 *            the page, false otherwise
	 * @return a main page for the creation of the new model project as appropriate
	 */
	protected abstract WizardNewProjectCreationPage createMainPage();

	/**
	 * Creates the {@link WizardNewProjectCreationPage main page} for the creation of the new model project.
	 * 
	 * @param createWorkingSetGroup
	 *            <code>true</code> if a group for choosing a working set for the new model project should be added to
	 *            the page, false otherwise
	 * @return a main page for the creation of the new model project as appropriate
	 * @deprecated Use {@link #createMainPage()} instead.
	 */
	@Deprecated
	protected WizardNewProjectCreationPage createMainPage(boolean createWorkingSetGroup) {
		return null;
	}

	/**
	 * Creates the reference page.
	 */
	protected WizardNewProjectReferencePage createReferencePage() {
		WizardNewProjectReferencePage referencePage = new WizardNewProjectReferencePage("WizardNewProjectReferencePage"); //$NON-NLS-1$
		referencePage.setTitle(Messages.page_newProjectReference_title);
		referencePage.setDescription(Messages.page_newProjectReference_description);
		return referencePage;
	}

	/*
	 * Creates a new project, update the perspective, and open in new perspective.
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IProject projectHandle = mainPage.getProjectHandle();
		URI location = !mainPage.useDefaults() ? mainPage.getLocationURI() : null;
		IProject[] referencedProjects = referencePage != null ? referencePage.getReferencedProjects() : null;

		// Create a new model project creation job
		CreateNewModelProjectJob<T> job = createCreateNewProjectJob(Messages.job_createNewModelProject_name, projectHandle, location);
		if (job == null) {
			job = createCreateNewModelProjectJob(projectHandle, location);
		}
		job.setReferencedProjects(referencedProjects);
		job.setUIInfoAdaptable(WorkspaceUndoUtil.getUIInfoAdapter(getShell()));

		// Setup post creation actions
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().getSeverity() == IStatus.OK) {
					Display display = ExtendedPlatformUI.getDisplay();
					if (display != null) {
						display.asyncExec(new Runnable() {
							public void run() {
								updatePerspective();

								// Reveal and select the new model project in current view
								selectAndReveal(projectHandle, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
							}
						});
					}
				}
			}
		});
		job.schedule();

		return true;
	}

	/**
	 * Creates a new instance of {@linkplain CreateNewModelProjectJob}. This method may be overridden by clients.
	 * 
	 * @param newProject
	 *            the {@linkplain IProject project} resource to be created
	 * @param location
	 *            the {@linkplain URI location} where the project will be created. If null the default location will be
	 *            used.
	 * @return
	 */
	protected CreateNewModelProjectJob<T> createCreateNewModelProjectJob(IProject newProject, URI location) {
		return new CreateNewModelProjectJob<T>(Messages.job_createNewModelProject_name, newProject, location, metaModelVersionDescriptor,
				metaModelVersionPreference);
	}

	/**
	 * Creates a new instance of {@linkplain CreateNewModelProjectJob}. This method may be overridden by clients.
	 * 
	 * @param newProject
	 *            the {@linkplain IProject project} resource to be created
	 * @param location
	 *            the {@linkplain URI location} where the project will be created. If null the default location will be
	 *            used.
	 * @deprecated Use {@link #createCreateNewModelProjectJob (IProject, URI) instead}
	 * @return
	 */
	@Deprecated
	protected CreateNewModelProjectJob<T> createCreateNewProjectJob(String name, IProject newProject, URI location) {
		return null;
	}
}
