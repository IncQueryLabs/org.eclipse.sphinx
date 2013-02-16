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
package org.eclipse.sphinx.tests.emf.workspace.integration.internal.loading;

import java.lang.ref.WeakReference;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.internal.loading.LoadJob;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;

/**
 * 
 */
@SuppressWarnings({ "nls", "restriction", "unchecked" })
abstract class AbstractLoadJobTest extends DefaultIntegrationTestCase {

	/**
	 * The loading job that this test may interrupt (by asking it to fall asleep) and that must be waked up before the
	 * end the test (aims at avoiding deadlock for instance).
	 */
	private WeakReference<LoadJob> loadJob;

	protected static final boolean SHOULD_CREATE = true;
	protected static final boolean SHOULD_NOT_CREATE = false;

	/**
	 * Creates assertions on the fact that:
	 * <ul>
	 * <li>No loading job is queued in the {@linkplain IJobManager job manager};</li>
	 * </ul>
	 */
	protected <T extends LoadJob> void assertNoLoadJobIsSleeping() {
		try {

			// Retrieves from JobManager the list of jobs that belong to the "model loading" family
			Job[] jobs = Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING);

			// Verify that no loading job is queued
			Assert.assertEquals(MSG_expectedNoJob, 0, jobs.length);
		} catch (AssertionFailedError err) {
			// Resume job manager to avoid deadlocks and failure of successor tests
			wakeUp();
			// Propagate error so that test execution can be interrupted
			throw err;
		}
	}

	/**
	 * Creates assertions on the fact that:
	 * <ul>
	 * <li>One and only one loading job is queued in the {@linkplain IJobManager job manager};</li>
	 * <li>The retrieved loading job is expected to be instance of the specified {@linkplain ILoadJob} class;</li>
	 * <li>This job is really is the {@linkplain Job#SLEEPING SLEEPING} state.</li>
	 * </ul>
	 * 
	 * @param <T>
	 *            The expected type of loading job.
	 * @param clazz
	 *            The expected subclass of {@linkplain LoadJob}.
	 */
	protected <T extends LoadJob> void assertOnlyOneLoadJobIsSleeping(Class<T> clazz) {
		try {

			// Retrieves from JobManager the list of jobs that belong to the "model loading" family
			Job[] jobs = Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING);

			// Verify that one and only one loading job is queued
			Assert.assertEquals(MSG_expectedOneJob, 1, jobs.length);

			// Verify that type of job is the expected one (usually ModelLoadJob or FileLoadJob)
			Assert.assertTrue(NLS.bind(MSG_expectedTypeOfJob, clazz.getSimpleName(), getSimpleName(jobs[0].getClass())), clazz.isInstance(jobs[0]));

			// Keep a reference on that job
			loadJob = new WeakReference<LoadJob>(clazz.cast(jobs[0]));
			// Ask for the loading job to fall asleep
			loadJob.get().sleep();

			// Verify that current loading job is really in the SLEEPING state
			Assert.assertTrue(MSG_expectedSleepingJob, loadJob.get().getState() == Job.SLEEPING);

		} catch (AssertionFailedError err) {
			// Resume job manager to avoid deadlocks and failure of successor tests
			wakeUp();
			// Propagate error so that test execution can be interrupted
			throw err;
		}
	}

	@Override
	protected boolean isProjectsClosedOnStartup() {
		return true;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Job.getJobManager().suspend();
	}

	protected void finish() {
		// Reset reference to loading job
		loadJob = null;
		// Verify that there no loading job left
		int nbOfJobs = Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING).length;
		Assert.assertEquals("No job from MODEL LOADING family should exist anymore; ", 0, nbOfJobs);
	}

	protected void wakeUp() {

		// !!! Important note !!
		// /*
		// * Perform the wake up before creating assertions: it causes dead-locks if any assertion is violated.
		// */

		// Wake up the loading job that was asked for sleeping, if any
		if (loadJob != null) {
			loadJob.get().wakeUp();
		}

		// Resume the Job Manager so that loading job could be scheduled
		Job.getJobManager().resume();

		try {
			// Waits until loading ends
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, null);
		} catch (OperationCanceledException ex) {
			throw new RuntimeException(ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static final String MSG_expectedNoJob = "No job from family MODEL LOADING should be found";
	private static final String MSG_expectedOneJob = "One and only one job from family MODEL LOADING should be found";
	private static final String MSG_expectedTypeOfJob = "Job is expected to be an instance of <{0}> but was <{1}>";
	private static final String MSG_expectedSleepingJob = "Sleep ask failed (model loading job was already running)";

	private static final String MSG_SHOULD_CREATE = "One loading job should be created for {0}(s) [{1}] with meta-model descriptor \"{2}\"";
	private static final String MSG_SHOULD_NOT_CREATE = "No loading job should be created for {0}(s) [{1}] with meta-model descriptor \"{2}\"";

	private static <T extends Job> String getSimpleName(Class<T> jobClass) {
		String className = jobClass.getSimpleName();
		if (className == null || "".equals(className)) {
			className = getSimpleName((Class<T>) jobClass.getSuperclass());
		}
		return className;
	}

	protected static <T extends IResource> String getMessage(boolean shouldCreate, Collection<T> resources, IMetaModelDescriptor mmDescriptor) {
		String type = "resource";
		if (!resources.isEmpty()) {
			IResource resource = resources.iterator().next();
			if (resource instanceof IProject) {
				type = "project";
			} else if (resource instanceof IFile) {
				type = "file";
			}
		}
		String msg = shouldCreate ? MSG_SHOULD_CREATE : MSG_SHOULD_NOT_CREATE;
		return NLS.bind(msg, new Object[] { type, getResourcesNames(resources), mmDescriptor });
	}

	protected static <T extends IResource> String getResourcesNames(Collection<T> resources) {
		StringBuffer str = new StringBuffer();
		for (T resource : resources) {
			str.append((str.length() > 0 ? ", " : "") + resource.getName());
		}
		return str.toString();
	}
}
