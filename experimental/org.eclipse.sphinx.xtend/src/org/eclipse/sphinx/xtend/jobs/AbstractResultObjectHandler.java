/**
 * <copyright>
 * 
 * Copyright (c) 2011 itemis and others.
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
package org.eclipse.sphinx.xtend.jobs;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * Abstract base class for implementing handlers that can be registered as {@link IJobChangeListener} on an
 * {@link XtendJob} instance or a {@link Job} instance that encloses the latter and processes the
 * {@link XtendJob#getResultObjects() result objects} produced by the {@link XtendJob}.
 * 
 * @see XtendJob
 */
public abstract class AbstractResultObjectHandler extends JobChangeAdapter {

	protected XtendJob xtendJob = null;

	public AbstractResultObjectHandler() {
	}

	public AbstractResultObjectHandler(XtendJob xtendJob) {
		this.xtendJob = xtendJob;
	}

	protected XtendJob getXtendJob(IJobChangeEvent event) {
		// Refer to job in job change event if it is an XtendJob
		if (event != null) {
			Job job = event.getJob();
			if (job instanceof XtendJob) {
				return (XtendJob) job;
			}
		}

		// Use preconfigured XtendJob otherwise
		return xtendJob;
	}

	@Override
	public void done(IJobChangeEvent event) {
		XtendJob xtendJob = getXtendJob(event);
		if (xtendJob != null) {
			handleResultObjects(xtendJob.getResultObjects());
		}
	}

	protected abstract void handleResultObjects(Map<Object, Collection<?>> resultObjects);
}
