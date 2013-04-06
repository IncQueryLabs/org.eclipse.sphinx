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
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.jobs.CreateNewModelFileJob;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.ui.wizards.pages.NewModelCreationPage;
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
 * Basic wizard that creates a new model file in the workspace. Two pages are added for selecting the metamodel
 * descriptor, epackage and eclassifier to be used for the nuw model file. A {@linkplain CreateNewModelFileJob new model
 * file job} is created to create and save the model file in the workspace.
 * <p>
 * This class may be used by clients as it is; it may also be subclassed to suit.
 */
public class BasicNewModelFileWizard extends BasicNewResourceWizard {
	/**
	 * This is the file creation page.
	 */
	protected NewModelFileCreationPage newModelFileCreationPage;
	protected NewModelCreationPage newModelCreationPage;
	protected NewModelFileProperties newModelFileProperties;
	/**
	 * Project nature that is required by the preference.
	 */
	protected String requiredProjectNatureId;

	/**
	 * The properties of the new model file (IMetaModelDescriptor and the EPackage and the EClassifier of the root
	 * object) selected by the user. This class is used to share the property values between the model creation pages,
	 * e.g., the NewModelCreationPage sets the property values, and the second page, the NewModelFileCreationPage uses
	 * the selected property values to create a model file.
	 */
	public class NewModelFileProperties {

		private IMetaModelDescriptor mmDescriptor;
		private EPackage rootObjectEPackage;
		private EClassifier rootObjectEClassifier;

		public NewModelFileProperties() {
		}

		public NewModelFileProperties(IMetaModelDescriptor mmDescriptor) {
			this.mmDescriptor = mmDescriptor;
		}

		public IMetaModelDescriptor getMetaModelDescriptor() {
			return mmDescriptor;
		}

		public void setMetaModelDescriptor(IMetaModelDescriptor mmDescriptor) {
			this.mmDescriptor = mmDescriptor;
		}

		public EPackage getRootObjectEPackage() {
			return rootObjectEPackage;
		}

		public void setRootObjectEPackage(EPackage rootObjectEPackage) {
			this.rootObjectEPackage = rootObjectEPackage;
		}

		public EClassifier getRootObjectEClassifier() {
			return rootObjectEClassifier;
		}

		public void setRootObjectEClassifier(EClassifier rootObjectEClassifier) {
			this.rootObjectEClassifier = rootObjectEClassifier;
		}
	}

	/**
	 * Creates a wizard for creating a new file in the workspace.
	 */
	public BasicNewModelFileWizard() {
	}

	/**
	 * Creates a wizard for creating a new file in the workspace with a required nature id
	 */
	public BasicNewModelFileWizard(String requiredProjectNatureId) {
		this.requiredProjectNatureId = requiredProjectNatureId;
	}

	/*
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(Messages.title_newModelFileWizard);
	}

	/*
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		newModelFileProperties = new NewModelFileProperties();

		// Create a page for users to choose the meta-model, EPackage and EClassifier of the model to be created
		newModelCreationPage = new NewModelCreationPage("NewModel", selection, newModelFileProperties); //$NON-NLS-1$
		addPage(newModelCreationPage);

		// Create a model file creation page
		newModelFileCreationPage = new NewModelFileCreationPage("NewModelFile", selection, requiredProjectNatureId, newModelFileProperties); //$NON-NLS-1$
		addPage(newModelFileCreationPage);
	}

	/*
	 * Creates a new model file, selects it in the current view, and opens it in an editor
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
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
	 * @param modelFile
	 *            the {@linkplain IFile model file} to be created
	 * @return a new instance of job that creates a new model file. This job is a unit of runnable work that can be
	 *         scheduled to be run with the job manager.
	 */
	protected Job createCreateNewModelFileJob(IFile modelFile) {
		Assert.isNotNull(newModelFileProperties);

		return new CreateNewModelFileJob(Messages.job_creatingNewModelFile, modelFile, newModelFileProperties.getMetaModelDescriptor(),
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
