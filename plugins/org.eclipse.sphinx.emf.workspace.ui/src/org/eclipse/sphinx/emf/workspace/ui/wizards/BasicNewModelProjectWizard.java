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
package org.eclipse.sphinx.emf.workspace.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelProjectJob;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * 
 */
/**
 * 
 */
@SuppressWarnings("restriction")
public class BasicNewModelProjectWizard extends BasicNewProjectResourceWizard {

	protected WizardNewProjectCreationPage mainPage;

	protected WizardNewProjectReferencePage referencePage;

	protected IMetaModelDescriptor metaModelDescriptor;

	/**
	 * The id of the {@link IProjectNature project nature}.
	 */
	private String projectNatureId;

	private IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> projectWorkspacePreference;

	/**
	 * Creates a wizard for creating a new project resource in the workspace.
	 */
	public BasicNewModelProjectWizard() {
		super();
	}

	/**
	 * Creates a wizard for creating a new project resource in the workspace.
	 */
	public BasicNewModelProjectWizard(String projectNatureId,
			IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> projectWorkspacePreference) {
		this(null, projectNatureId, projectWorkspacePreference);
	}

	/**
	 * Creates a wizard for creating a new project resource in the workspace.
	 */
	public BasicNewModelProjectWizard(IMetaModelDescriptor metaModelDescriptor, String projectNatureId,
			IProjectWorkspacePreference<? extends AbstractMetaModelDescriptor> projectWorkspacePreference) {
		super();
		this.metaModelDescriptor = metaModelDescriptor;
		this.projectNatureId = projectNatureId;
		this.projectWorkspacePreference = projectWorkspacePreference;
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public void addPages() {
		mainPage = createMainPage(true);
		addPage(mainPage);

		// Only add page if there are already projects in the workspace
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
			referencePage = createReferencePage();
			addPage(referencePage);
		}
	}

	protected WizardNewProjectCreationPage createMainPage(final boolean createWorkingSetGroup) {
		WizardNewProjectCreationPage mainPage = new WizardNewProjectCreationPage("basicNewProjectPage") { //$NON-NLS-1$
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org.eclipse.swt.widgets.Composite)
			 */
			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				if (createWorkingSetGroup) {
					createWorkingSetGroup((Composite) getControl(), getSelection(), new String[] { "org.eclipse.ui.resourceWorkingSetPage" }); //$NON-NLS-1$
				}
				Dialog.applyDialogFont(getControl());
			}
		};
		mainPage.setTitle(Messages.wizardNewProject_newProjectTitle);
		mainPage.setDescription(Messages.wizardNewProject_newProjectDescription);
		return mainPage;
	}

	protected WizardNewProjectReferencePage createReferencePage() {
		WizardNewProjectReferencePage referencePage = new WizardNewProjectReferencePage("basicReferenceProjectPage"); //$NON-NLS-1$
		referencePage.setTitle(Messages.wizardNewProject_newProjectReferenceTitle);
		referencePage.setDescription(Messages.wizardNewProject_newProjectReferenceDescription);
		return referencePage;
	}

	/*
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		URI location = !mainPage.useDefaults() ? mainPage.getLocationURI() : null;
		IProject[] referencedProjects = referencePage != null ? referencePage.getReferencedProjects() : null;
		final IProject projectHandle = mainPage.getProjectHandle();

		CreateNewModelProjectJob job = createCreateNewProjectJob(org.eclipse.sphinx.emf.workspace.internal.messages.Messages.job_creatingNewProject,
				projectHandle, location);
		job.setReferencedProjects(referencedProjects);
		job.setUIInfoAdaptable(WorkspaceUndoUtil.getUIInfoAdapter(getShell()));

		// reveal the project after creation
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().getSeverity() == IStatus.OK) {
					Display display = ExtendedPlatformUI.getDisplay();
					if (display != null) {
						display.asyncExec(new Runnable() {
							public void run() {
								updatePerspective();
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

	protected CreateNewModelProjectJob createCreateNewProjectJob(String name, IProject project, URI location) {
		return new CreateNewModelProjectJob(name, project, location, metaModelDescriptor, projectNatureId, projectWorkspacePreference);
	}
}
