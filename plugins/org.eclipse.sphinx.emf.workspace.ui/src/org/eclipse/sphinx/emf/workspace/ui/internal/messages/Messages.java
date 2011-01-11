/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
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

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
