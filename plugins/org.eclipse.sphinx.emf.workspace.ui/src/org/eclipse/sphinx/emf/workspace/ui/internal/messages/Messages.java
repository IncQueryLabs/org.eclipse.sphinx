/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - Added messages for creating new project
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages"; //$NON-NLS-1$

	/*
	 * Other messages
	 */
	public static String error_failedToSaveModelsDuringWorkbenchClosing;
	public static String error_unableToFindPerspective;
	public static String error_fileExtension;

	public static String eobjectMustBeInResource;

	// New Linked Folder
	public static String wizardNewLinkedFolder_title;
	public static String wizardNewLinkedFolderCreationPage_title;
	public static String wizardNewLinkedFolderCreationPage_description;
	public static String wizardNewLinkedFolderCreationPage_errorTitle;
	public static String wizardNewLinkedFolderCreationPage_internalErrorTitle;
	public static String wizardNewLinkedFolderCreationPage_targetFolderLabel;
	public static String wizardNewLinkedFolderCreationPage_internalErrorMessage;

	public static String wizardNewLinkedFolderCreationPage_noParentSelected;
	public static String wizardNewLinkedFolderCreationPage_selectedParentProjectNotOpen;

	// New Linked File
	public static String wizardNewLinkedFile_title;
	public static String wizardNewLinkedFileCreationPage_title;
	public static String wizardNewLinkedFileCreationPage_description;
	public static String wizardNewLinkedFileCreationPage_errorTitle;
	public static String wizardNewLinkedFileCreationPage_internalErrorTitle;
	public static String wizardNewLinkedFileCreationPage_targetFileLabel;
	public static String wizardNewLinkedFileCreationPage_internalErrorMessage;

	public static String wizardNewLinkedFileCreationPage_noParentSelected;
	public static String wizardNewLinkedFileCreationPage_selectedParentProjectNotOpen;

	// New Project Creation
	public static String wizardNewProjectCreationPage_newProjectCreationPageNameLabel;
	public static String wizardNewProjectCreationPage_newProjectNameEmptyLabel;
	public static String wizardNewProjectCreationPage_newProjectExistsErrorMessage;
	public static String wizardNewProjectCreationPage_newProjectCaseVariantExistsError;
	public static String wizardNewProjectCreationPage_location;

	public static String wizardNewProject_newProjectOpeningWindowErrorMessage;
	public static String wizardNewProject_newProjectPerspSwitchMessageLabel;
	public static String wizardNewProject_newProjectPerspSwitchMessageWithDescLabel;
	public static String wizardNewProject_newProjectPerspSwitchTitle;
	public static String wizardNewProject_newProjectWizardTitle;
	public static String wizardNewProject_newProjectTitle;
	public static String wizardNewProject_newProjectDescription;
	public static String wizardNewProject_newProjectReferenceTitle;
	public static String wizardNewProject_newProjectReferenceDescription;
	public static String wizardNewProject_newProjectWindowTitle;

	public static String job_creatingNewModelProject;
	public static String title_newModelFile;
	public static String default_modelFileBaseName;
	public static String message_configureSetting;
	public static String label_metaModelVersionGroup;
	public static String label_metaModelVersionAlternate;
	public static String label_workspaceDefaultMetaModelVersion;
	public static String metaModelVersion_labelPattern;
	public static String FilteredItemsSelectionDialog_pattern;
	public static String error_emptySelectedMM;
	public static String error_emptySelectedEPackage;
	public static String error_emptySelectedEClassifier;
	public static String title_metaModelDescriptorBrowseDialog;
	public static String title_epackageBrowseDialog;
	public static String title_eclassifierBrowseDialog;
	public static String title_newModelCreationPage;
	public static String description_newModelCreationPage;
	public static String label_browseButton;
	public static String description_newModelFileCreationPage;
	public static String error_requiredProjectNature;
	public static String msg_defaultRequiredProjectType;
	public static String title_newModelFileWizard;
	public static String job_creatingNewModelFile;
	public static String UI_OpenEditorError_label;
	public static String label_metaModelComboButtonField;
	public static String label_epackageComboButtonField;
	public static String label_eclassifierComboButtonField;
	public static String defaultMetaModelVersionLabel;

	public static String label_defaultMetaModelVersion;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
