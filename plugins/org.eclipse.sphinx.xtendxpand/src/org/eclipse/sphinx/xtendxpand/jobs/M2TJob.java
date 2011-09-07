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
package org.eclipse.sphinx.xtendxpand.jobs;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xtendxpand.internal.Activator;

public class M2TJob extends WorkspaceJob {

	private CheckJob checkJob;

	private XpandJob xpandJob;

	public M2TJob(String name, XpandJob xpandJob) {
		this(name, xpandJob, null);
	}

	public M2TJob(String name, XpandJob xpandJob, CheckJob checkJob) {
		super(name);
		this.xpandJob = xpandJob;
		this.checkJob = checkJob;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		try {
			// Run check if required
			if (checkJob != null) {
				IStatus status = checkJob.run(progress.newChild(50));

				// Abort if check job ends with errors or is cancelled; continue when there are no errors or
				// only warnings
				if (status.getSeverity() == IStatus.ERROR || progress.isCanceled()) {
					throw new OperationCanceledException();
				}
			}

			// Run Xpand
			return xpandJob.runInWorkspace(progress.newChild(50));
		} catch (OperationCanceledException ex) {
			return Status.CANCEL_STATUS;
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
		}
	}

	@Override
	public boolean belongsTo(Object family) {
		return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
	}

	public CheckJob getCheckJob() {
		return checkJob;
	}

	public XpandJob getXpandJob() {
		return xpandJob;
	}
}
