/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.tests.platform.perfs;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.platform.perfs.Measurement;
import org.eclipse.sphinx.platform.perfs.PerformanceStats;
import org.eclipse.sphinx.platform.perfs.PerfsFactory;
import org.eclipse.sphinx.platform.perfs.services.ModelPersistenceService;
import org.eclipse.sphinx.platform.perfs.util.LogUtil;

public class PerfStatsExample {

	private File perfsModel;

	public PerfStatsExample(File perfsModel) {
		this.perfsModel = perfsModel;
	}

	/**
	 * Returns the resource behind the {@link #getPerfsModel() performance model}. If the performance model file does
	 * not exist then we create a new one.
	 * 
	 * @return the resource behind the {@link #getPerfsModel() performance model}.
	 */
	public Resource getPerfsModelResource() {
		Assert.isNotNull(perfsModel);

		if (!perfsModel.exists()) {
			return ModelPersistenceService.INSTANCE.createResource(perfsModel);
		}
		return ModelPersistenceService.INSTANCE.getResource(perfsModel);
	}

	public File getPerfsModel() {
		return perfsModel;
	}

	public Collection<Measurement> getPerfMeasurements() {
		Set<Measurement> measurements = new HashSet<Measurement>();

		Measurement method1Measurement = PerfsFactory.eINSTANCE.createMeasurement();
		method1Measurement.setName("Method 1 Call"); //$NON-NLS-1$
		method1Measurement.start();
		method1();
		method1Measurement.stop();
		measurements.add(method1Measurement);

		Measurement method2Measurement = PerfsFactory.eINSTANCE.createMeasurement();
		method2Measurement.setName("Method 2 Call"); //$NON-NLS-1$
		method2Measurement.start();
		method2();
		method2Measurement.stop();
		measurements.add(method2Measurement);
		return measurements;
	}

	/**
	 * Removes obsolete measurements from performance model and adds given measurements instead.
	 */
	public static void updatePerfsModel(Resource resource, Collection<Measurement> measurements) {
		Assert.isNotNull(resource);
		Assert.isNotNull(measurements);

		// Retrieve performance stats from given resource
		PerformanceStats performanceStats = null;
		for (EObject rootObject : resource.getContents()) {
			if (rootObject instanceof PerformanceStats) {
				performanceStats = (PerformanceStats) rootObject;
				break;
			}
		}

		// Create new performance stats if no such was existing so far
		if (performanceStats == null) {
			performanceStats = PerfsFactory.eINSTANCE.createPerformanceStats();
			resource.getContents().add(performanceStats);
		}

		// Remove obsolete measurements
		for (Iterator<Measurement> iter = performanceStats.getMeasurements().iterator(); iter.hasNext();) {
			Measurement measurement = iter.next();
			for (Measurement currentMeasurement : measurements) {
				if (measurement.getName() != null && measurement.getName().equals(currentMeasurement.getName())) {
					iter.remove();
				}
			}
		}

		// Add provided measurements
		performanceStats.getMeasurements().addAll(measurements);
	}

	public static void logPerfSats(Resource resource) {
		Assert.isNotNull(resource);

		// Retrieve performance stats from given resource
		PerformanceStats performanceStats = null;
		for (EObject rootObject : resource.getContents()) {
			if (rootObject instanceof PerformanceStats) {
				performanceStats = (PerformanceStats) rootObject;
				break;
			}
		}

		if (performanceStats != null) {
			MultiStatus status = new MultiStatus(Activator.getPlugin().getSymbolicName(), IStatus.INFO, "Performance Statistics", null); //$NON-NLS-1$
			EList<Measurement> measurements = performanceStats.getMeasurements();
			if (!measurements.isEmpty()) {
				LogUtil.log(status, measurements);
			}
		}
	}

	/*
	 * A sample method performing a time loop . Effective waiting time may defer from the value provide in argument.
	 * @param timeOut value of the timeLoop in millisecond.
	 */
	private void timeLoop(long timeOut) {
		long currentCpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		while (ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() <= currentCpuTime + timeOut * (long) 1e6) {

		}
	}

	public void method1() {
		timeLoop(1000);
	}

	public void method2() {
		timeLoop(2000);
	}

	public void method3() {
		timeLoop(3000);
	}
}
