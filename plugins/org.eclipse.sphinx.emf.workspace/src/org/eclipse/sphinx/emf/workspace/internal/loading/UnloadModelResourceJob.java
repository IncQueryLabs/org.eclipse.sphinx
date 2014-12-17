/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.internal.loading;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.emf.workspace.loading.operations.UnloadModelResourceOperation;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class UnloadModelResourceJob extends Job {

	private UnloadModelResourceOperation unloadModelResourceOperation;

	/**
	 * Constructor.
	 *
	 * @param unloadModelResourceOperation
	 *            The operation which this job is supposed to run.
	 */
	public UnloadModelResourceJob(UnloadModelResourceOperation unloadModelResourceOperation) {
		super(unloadModelResourceOperation.getLabel());
		this.unloadModelResourceOperation = unloadModelResourceOperation;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			unloadModelResourceOperation.run(monitor);
		} catch (OperationCanceledException ex) {
			return Status.CANCEL_STATUS;
		} catch (CoreException ex) {
			return ex.getStatus();
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(Object family) {
		return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family) || IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
	}
}
