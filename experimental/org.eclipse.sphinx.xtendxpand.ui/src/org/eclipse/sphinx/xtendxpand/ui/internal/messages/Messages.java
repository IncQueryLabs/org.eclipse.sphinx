/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtendxpand.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.xtendxpand.ui.internal.messages.messages"; //$NON-NLS-1$

	public static String job_generatingCode;
	public static String job_modelTransformation;

	public static String label_template;
	public static String label_extension;
	public static String label_output;
	public static String label_templateFile;
	public static String label_extensionFile;
	public static String label_templateSelection;
	public static String label_extensionSelection;
	public static String label_definition;
	public static String label_extensionBlock;
	public static String label_useDefaultPath;
	public static String label_path;
	public static String label_browse;
	public static String label_name;
	public static String label_configPageName;
	public static String label_xtendPageName;
	public static String label_definitionName;
	public static String label_extensionName;
	public static String label_functionFieldName;
	public static String label_useCheckModelButton;
	public static String label_checkModelBlock;
	public static String label_checkFiles;
	public static String label_configureProjectSpecificSettings;
	public static String label_outletsGroupName;
	public static String label_default;
	public static String label_location;
	public static String label_workspaceBrowse;
	public static String label_fileSystemBrowse;
	public static String label_variablesBrowse;
	public static String label_restoreDefaultButtons;

	public static String msg_chooseTemplate;
	public static String msg_chooseExtension;
	public static String msg_chooseTemplateError;
	public static String msg_chooseExtensionError;
	public static String msg_containerSelection;
	public static String msg_variableSelectionWarning;
	public static String msg_outletNameEmptyValidationError;
	public static String msg_outletNameExistValidationError;
	public static String msg_outletLocationEmptyValidationError;

	public static String title_launchGen;
	public static String title_codeGen;
	public static String title_editOutletDialog;
	public static String title_newOutletDialog;
	public static String title_containerSelection;
	public static String title_variableSelection;
	public static String title_modelTransformation;
	public static String title_launchModelTransformation;
	public static String title_outletConfigurationDialog;
	public static String title_checkConfigurationPage;

	public static String desc_config;
	public static String desc_modelTransformation;
	public static String desc_configOutlets;
	public static String desc_checkConfigurationPage;

	public static String task_CreationOfSubpackageExtensions;
	public static String task_AddPluginDependencies;
	public static String task_AddXtendXpandNature;
	public static String task_ConvertToJavaPluginProject;
	public static String task_ConvertToBSWPlatformProject;
	public static String task_AddMetamodelContributor;

	static {
		// Initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
