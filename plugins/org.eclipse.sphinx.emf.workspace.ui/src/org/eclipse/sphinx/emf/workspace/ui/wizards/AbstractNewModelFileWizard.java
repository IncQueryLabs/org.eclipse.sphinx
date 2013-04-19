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
 *     itemis - [405023] Enable NewModelFileCreationPage to be used without having to pass an instance of NewModelFileProperties to its constructor
 *     itemis - [406062] Removal of the required project nature parameter in NewModelFileCreationPage constructor and CreateNewModelProjectJob constructor
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelFileJob;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewInitialModelCreationPage;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelFileCreationPage;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * Abstract wizard for creating a new model file in the workspace. No pages are added by default. When being finished a
 * {@linkplain CreateNewModelFileJob new model file job} is run to create and save the new model file in the workspace.
 */
public abstract class AbstractNewModelFileWizard<T extends IMetaModelDescriptor> extends BasicNewResourceWizard {

	protected NewModelFileProperties<T> newModelFileProperties;

	protected NewInitialModelCreationPage<T> newInitialModelCreationPage;
	protected NewModelFileCreationPage<T> newModelFileCreationPage;

	/*
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(Messages.wizard_newModelFile_title);
	}

	/*
	 * Creates a new model file, selects it in the current view, and opens it in an editor
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Assert.isNotNull(newModelFileCreationPage);

		// Make required wizard result information accessible for asynchronous operation
		final IFile newFile = newModelFileCreationPage.getNewFile();

		// Create new model file
		Job job = createCreateNewModelFileJob(newFile);

		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult() != null && event.getResult().isOK()) {
					Display display = ExtendedPlatformUI.getDisplay();
					if (display != null) {
						display.asyncExec(new Runnable() {
							public void run() {
								// Select the newly created file in current view
								selectAndReveal(newFile);

								// Open new file in an editor
								openNewModelInEditor(newFile);
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
	 * Creates a new instance of {@linkplain CreateNewModelFileJob}. This method may be overridden by clients to create
	 * a specific create new model file job.
	 * 
	 * @param newFile
	 *            the {@linkplain IFile model file} to be created
	 * @return a new instance of job that creates a new model file. This job is a unit of runnable work that can be
	 *         scheduled to be run with the job manager.
	 */
	protected Job createCreateNewModelFileJob(IFile newFile) {
		Assert.isNotNull(newModelFileProperties);

		return new CreateNewModelFileJob(Messages.job_creatingNewModelFile, newFile, newModelFileProperties.getMetaModelDescriptor(),
				newModelFileProperties.getRootObjectEPackage(), newModelFileProperties.getRootObjectEClassifier());
	}

	/**
	 * Opens newly created model in an editor.
	 * 
	 * @param modelFile
	 *            the {@linkplain IFile model file} to be opened
	 */
	protected void openNewModelInEditor(IFile modelFile) {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		try {
			page.openEditor(new FileEditorInput(modelFile),
					PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(modelFile.getFullPath().toString()).getId());
		} catch (PartInitException exception) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), exception);
		}
	}
}
