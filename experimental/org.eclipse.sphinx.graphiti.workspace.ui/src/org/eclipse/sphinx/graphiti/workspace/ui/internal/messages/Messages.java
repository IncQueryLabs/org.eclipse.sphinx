/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
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
package org.eclipse.sphinx.graphiti.workspace.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sphinx.graphiti.workspace.ui.internal.messages.messages"; //$NON-NLS-1$

	public static String DiagramContainerWizardPage_PageExtensionError;
	public static String DiagramContainerWizardPage_PageName;
	public static String DiagramContainerWizardPage_PageTitle;
	public static String DiagramContainerWizardPage_PageDescription;

	public static String DiagramTypeWizardPage_PageTitle;
	public static String DiagramTypeWizardPage_PageDescription;
	public static String DiagramTypeWizardPage_DiagramTypeField;
	public static String DiagramTypeWizardPage_ErrorOccuredTitle;

	public static String CreateDiagramWizard_NoContainerFoundErrorTitle;
	public static String CreateDiagramWizard_NoAccessibleContainerFoundError;
	public static String CreateDiagramWizard_SavingDiagramOperation;
	public static String CreateDiagramWizard_OpeningEditorError;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}