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
package org.eclipse.sphinx.emf.validation.ui.util;

import org.eclipse.osgi.util.NLS;

//TODO Move content of the class to internal Messages class.
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

	public static String _UI_liveValidation_groupText;
	public static String _UI_enableDisableLiveValidationPreferencesMsg;

	public static String _UI_automaticValidation_groupText;
	public static String _UI_enableDisableAutomaticValidationPreferencesMsg;

	public static String _UI_EMFConstraintsGroupText;
	public static String _UI_EMFConstraintsEnabledPreferencesMsg;

	public static String _UI_StrictEditingGroupText;
	public static String _UI_StrictEditingFieldLabel;

	public static String _UI_ProblemIndicationGroupText;
	public static String _UI_ProblemIndicationFieldLabelText;

	public static String _UI_PerformanceOptimizationGroupText;
	public static String _UI_MaxPerfLabelText;

	public static String _UI_subValidationMonitorIntro;

	public static String _UI_Workbench_showIn;

	public static String _UI_progressBar_InitialMsg;
	public static String _UI_progressBarMulti_ErrWarnInfo;

	public static String _Job_HandleDiagnostic;
	public static String _Job_Clean_Markers;

	public static String _UI_Clean_menu_item;
	public static String _UI_Clean_simple_description;

	public static String _UI_ValidationReport_menu_item;
	public static String _UI_ValidationReport_simple_description;

	public static String _UI_Validate_menu_item;
	public static String _UI_Validate_simple_description;

	public static String _UI_Wizard_Report_Default_File_Pattern;

	public static String _UI_Wizard_Report_ShellTitle;
	public static String _UI_Wizard_Report_Title;
	public static String _UI_Wizard_Report_Description;
	public static String _UI_Wizard_Report_TargetDirectory;
	public static String _UI_Wizard_Report_ReportFilePattern;
	public static String _UI_Wizard_Report_Browse;
	public static String _UI_Wizard_Report_Error;
	public static String _UI_Wizard_Report_MainPage_Title;
	public static String _UI_Wizard_Report_Browse_Popup_Title;

	public static String _UI_Wizard_Report_Err_Target_Directory_Empty;
	public static String _UI_Wizard_Report_Err_Target_Directory_Must_Exist;
	public static String _UI_Wizard_Report_Err_File_Empty;
	public static String _UI_Wizard_Report_Err_File_Extension_Not_Compliant;
	public static String _UI_Wizard_Report_Err_FileName_Not_Compliant;

	public static String _UI_FilePatternHelp;

	static {
		// Load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
