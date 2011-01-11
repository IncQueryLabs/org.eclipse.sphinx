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
package org.eclipse.sphinx.gmfgen.tasks;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;
import org.eclipse.gmf.codegen.util.Generator;
import org.eclipse.sphinx.gmfgen.util.GMFCodeGenUtil;

@SuppressWarnings("restriction")
public class GenerateDiagramCodeOperation {

	private boolean ignoreLoadErrors;

	private boolean ignoreValidationErrors;

	private GenEditorGenerator genModel;

	private Diagnostic loadStatus;

	public void setIgnoreLoadErrors(boolean ignoreLoadErrors) {
		this.ignoreLoadErrors = ignoreLoadErrors;
	}

	public void setIgnoreValidationErrors(boolean ignoreValidationErrors) {
		this.ignoreValidationErrors = ignoreValidationErrors;
	}

	public void run(IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(genModel, "genModel must not be null"); //$NON-NLS-1$
		Assert.isNotNull(loadStatus, "loadStatus must not be null");//$NON-NLS-1$
		try {
			if (!canProcessGMFGenModel(loadStatus)) {
				throw new CoreException(BasicDiagnostic.toIStatus(loadStatus));
			}

			System.out.println("Validating gen model"); //$NON-NLS-1$
			if (!ignoreValidationErrors) {
				Diagnostic validStatus = validateGenModel();
				if (validStatus.getSeverity() != Diagnostic.OK) {
					throw new CoreException(BasicDiagnostic.toIStatus(validStatus));
				}
			}
			IStatus runStatus = doRun(monitor);
			if (!runStatus.isOK()) {
				throw new CoreException(runStatus);
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private boolean canProcessGMFGenModel(Diagnostic loadStatus) {
		if (loadStatus.getSeverity() != Diagnostic.OK) {
			if (genModel == null || !ignoreLoadErrors) {
				return false;
			}
		}
		return true;
	}

	private IStatus doRun(IProgressMonitor monitor) {
		Generator g = createGenerator();
		IStatus runStatus;
		try {
			g.run(monitor);
			runStatus = g.getRunStatus();
		} catch (InterruptedException e) {
			runStatus = Status.CANCEL_STATUS;
		}

		return runStatus;
	}

	private Generator createGenerator() {
		GMFCodeGenUtil g = new GMFCodeGenUtil();
		return new Generator(getGenModel(), g.getEmitters(getGenModel()));
	}

	public Diagnostic getLoadStatus() {
		return loadStatus;
	}

	public void setLoadStatus(Diagnostic loadStatus) {
		this.loadStatus = loadStatus;
	}

	public void setGenModel(GenEditorGenerator gmfGenModel) {
		genModel = gmfGenModel;
	}

	public GenEditorGenerator getGenModel() {
		return genModel;
	}

	private Diagnostic validateGenModel() {
		return Diagnostician.INSTANCE.validate(getGenModel());
	}
}
