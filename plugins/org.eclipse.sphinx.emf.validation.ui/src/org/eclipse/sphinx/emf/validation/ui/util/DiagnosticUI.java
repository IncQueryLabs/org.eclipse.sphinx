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

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.ui.dialogs.DiagnosticDialog;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.edit.ui.EMFEditUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class
 */
public class DiagnosticUI {

	/**
	 * Switch to display the nicer popup window for this list of {@link Diagnostic}
	 * 
	 * @param diagnostics
	 * @return
	 */
	static public int showDiagnostic(List<Diagnostic> diagnostics) {
		Assert.isNotNull(diagnostics);

		return diagnostics.size() == 1 ? showDiagnosticSingle(diagnostics.get(0)) : showDiagnosticMulti(diagnostics);

	}

	/**
	 * Utility method to dispay a simple {@link Diagnostic} into a popup window.
	 * 
	 * @param diagnostic
	 *            the diagnostic to display
	 * @return the code status associated to the pressed button
	 */
	static public int showDiagnosticSingle(Diagnostic diagnostic) {
		int severity = diagnostic.getSeverity();
		String title = null;
		String message = null;

		if (severity == Diagnostic.ERROR || severity == Diagnostic.WARNING) {
			title = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationProblems_title"); //$NON-NLS-1$
			message = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationProblems_message"); //$NON-NLS-1$
		} else {
			title = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationResults_title"); //$NON-NLS-1$
			message = EMFEditUIPlugin.INSTANCE.getString(severity == Diagnostic.OK ? "_UI_ValidationOK_message" : "_UI_ValidationResults_message"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		int result = 0;
		if (diagnostic.getSeverity() == Diagnostic.OK) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
			result = Window.CANCEL;
		} else {
			result = DiagnosticDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message, diagnostic);
		}

		return result;
	}

	/**
	 * Utility method to display a list of {@link Diagnostic} into a popup window.
	 * 
	 * @param diagnostics
	 *            the list of Diagnostic to display
	 * @return the code status associated to the pressed button
	 */
	static public int showDiagnosticMulti(List<Diagnostic> diagnostics) {
		Assert.isNotNull(diagnostics);

		boolean isDiagnosticsOk = true;
		boolean isInfo = false;

		for (Diagnostic diagnostic : diagnostics) {
			if (diagnostic.getSeverity() == Diagnostic.ERROR || diagnostic.getSeverity() == Diagnostic.WARNING) {
				isDiagnosticsOk = false;
				break;
			} else if (diagnostic.getSeverity() == Diagnostic.INFO) {
				isInfo = true;
			}
		}

		int result = 0;
		String title = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationProblems_title"); //$NON-NLS-1$
		String message = "MESSAGE"; //$NON-NLS-1$

		if (isDiagnosticsOk == false) {
			title = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationProblems_title"); //$NON-NLS-1$
			message = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationProblems_message"); //$NON-NLS-1$
		} else {
			title = EMFEditUIPlugin.INSTANCE.getString("_UI_ValidationResults_title"); //$NON-NLS-1$
			message = EMFEditUIPlugin.INSTANCE.getString(!isInfo ? "_UI_ValidationOK_message" : "_UI_ValidationResults_message"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (isDiagnosticsOk == true) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
			result = Window.CANCEL;
		} else {
			result = ExtendedDiagnosticDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message, diagnostics);
			result = Window.OK;
		}

		return result;
	}
}
