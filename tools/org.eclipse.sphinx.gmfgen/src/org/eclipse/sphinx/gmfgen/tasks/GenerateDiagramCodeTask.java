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

import org.apache.tools.ant.BuildException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;

public class GenerateDiagramCodeTask extends GMFTask {

	private boolean ignoreLoadErrors;

	private boolean ignoreValidationErrors;

	public void setIgnoreLoadErrors(boolean ignoreLoadErrors) {
		this.ignoreLoadErrors = ignoreLoadErrors;
	}

	public void setIgnoreValidationErrors(boolean ignoreValidationErrors) {
		this.ignoreValidationErrors = ignoreValidationErrors;
	}

	@Override
	public void doExecute() throws BuildException {
		System.out.println("Instantiate generation operation"); //$NON-NLS-1$
		GenerateDiagramCodeOperation op = new GenerateDiagramCodeOperation();

		System.out.println("Setting genmodel"); //$NON-NLS-1$
		op.setGenModel(getGenModel());

		System.out.println("Setting load status"); //$NON-NLS-1$
		op.setLoadStatus(getLoadStatus());

		System.out.println("Setting ignored load errors :" + ignoreLoadErrors);//$NON-NLS-1$
		op.setIgnoreLoadErrors(ignoreLoadErrors);

		System.out.println("Setting ignored validation errors :" + ignoreValidationErrors); //$NON-NLS-1$
		op.setIgnoreValidationErrors(ignoreValidationErrors);

		System.out.println("Setting up monitor"); //$NON-NLS-1$
		IProgressMonitor monitor = BasicMonitor.toIProgressMonitor(new BasicMonitor.Printing(System.out));

		try {
			System.out.println("Running the operation"); //$NON-NLS-1$
			op.run(monitor);
		} catch (CoreException e) {
			throw new BuildException(e);
		}
	}
}
