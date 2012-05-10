/**
 * <copyright>
 * 
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.platform.resources;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.platform.internal.Activator;
import org.eclipse.sphinx.platform.messages.PlatformMessages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * A job which can be used to asynchronously create and delete markers.
 * <p>
 * The job is useful to avoid deadlocks related to the global workspace lock which is needed to manipulate resource
 * markers.
 * <p>
 * The job maintains an internal queue of marker manipulation tasks when the job gets executed this queue will be
 * processed.
 */
public class MarkerJob extends WorkspaceJob {

	protected static abstract class MarkerTask {
		IResource resource;
		String type;

		abstract void execute() throws CoreException;
	}

	protected static class CreateMarkerTask extends MarkerTask {
		int severity;
		String message;
		Map<String, Object> attributes;

		@Override
		void execute() throws CoreException {
			IMarker m = resource.createMarker(type);
			m.setAttribute(IMarker.SEVERITY, severity);
			m.setAttribute(IMarker.MESSAGE, message);
			if (attributes != null) {
				m.setAttributes(attributes);
			}
		}
	}

	protected static class DeleteMarkerTask extends MarkerTask {
		@Override
		void execute() throws CoreException {
			resource.deleteMarkers(type, false, IResource.DEPTH_ZERO);
		}
	}

	protected Queue<MarkerTask> taskQueue = new ConcurrentLinkedQueue<MarkerTask>();

	/**
	 * Creates a new marker job instance.
	 */
	public MarkerJob() {
		super(PlatformMessages.job_updatingProblemMarkers);
		setSystem(true);
		setPriority(Job.BUILD);
		setRule(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Processes the internal task queue.
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		MarkerTask mt = null;
		while ((mt = taskQueue.poll()) != null) {
			try {
				mt.execute();
			} catch (CoreException ex) {
				if (mt.resource.isAccessible() == false || mt.resource.isSynchronized(IResource.DEPTH_ZERO) == false) {
					// do not log errors for inaccessible or out-of-sync resources
					continue;
				}
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			}
		}

		return Status.OK_STATUS;
	}

	/**
	 * Adds a create marker task to the internal queue.
	 * 
	 * @param resource
	 *            the resource for which the marker will be created
	 * @param type
	 *            the marker type
	 * @param severity
	 *            the marker severity
	 * @param severity
	 *            the marker message
	 * @see IResource#createMarker(String)
	 */
	public void createMarker(IResource resource, String type, int severity, String message) {
		CreateMarkerTask cmt = new CreateMarkerTask();
		cmt.resource = resource;
		cmt.type = type;
		cmt.severity = severity;
		cmt.message = message;
		synchronized (taskQueue) {
			taskQueue.add(cmt);
		}
	}

	/**
	 * Adds a create marker task to the internal queue.
	 * 
	 * @param resource
	 *            the resource for which the marker will be created
	 * @param type
	 *            the marker type
	 * @param attributes
	 *            marker attributes that will be passed to the newly created marker using
	 *            {@link IMarker#setAttributes(Map)}.
	 * @see IResource#createMarker(String)
	 */
	public void createMarker(IResource resource, String type, Map<String, Object> attributes) {
		CreateMarkerTask cmt = new CreateMarkerTask();
		cmt.resource = resource;
		cmt.type = type;
		cmt.attributes = attributes;
		synchronized (taskQueue) {
			taskQueue.add(cmt);
		}
	}

	/**
	 * Adds a delete marker task to the internal queue which will delete all existing markers of the specified type.
	 * 
	 * @param resource
	 *            the resource for which markers will be deleted
	 * @param type
	 *            the marker type to delete
	 * @see IResource#deleteMarkers(String, boolean, int)
	 */
	public void deleteMarker(IResource resource, String type) {
		DeleteMarkerTask dmt = new DeleteMarkerTask();
		dmt.resource = resource;
		dmt.type = type;
		synchronized (taskQueue) {
			taskQueue.add(dmt);
		}
	}
}
