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
package org.eclipse.sphinx.platform.jobs;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.platform.internal.Activator;
import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;
import org.eclipse.sphinx.platform.util.StatusUtil;

/**
 * A {@link WorkspaceJob} that can be used to run {@link IWorkspaceOperation}s as a background task.
 */
public class WorkspaceOperationWorkspaceJob extends WorkspaceJob {

	protected IWorkspaceOperation operation;

	public WorkspaceOperationWorkspaceJob(IWorkspaceOperation operation) {
		super(operation.getLabel());

		Assert.isNotNull(operation);
		this.operation = operation;

		setPriority(Job.BUILD);
	}

	/*
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			operation.run(monitor);
		} catch (OperationCanceledException exception) {
			return Status.CANCEL_STATUS;
		} catch (CoreException ex) {
			return ex.getStatus();
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getDefault(), ex);
		}
		return Status.OK_STATUS;
	}
}