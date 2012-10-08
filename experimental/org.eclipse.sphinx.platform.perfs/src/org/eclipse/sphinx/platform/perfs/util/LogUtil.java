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
package org.eclipse.sphinx.platform.perfs.util;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.perfs.Measurement;
import org.eclipse.sphinx.platform.perfs.PerformanceStats;
import org.eclipse.sphinx.platform.perfs.internal.Activator;
import org.eclipse.sphinx.platform.perfs.internal.messages.Messages;

public final class LogUtil {

	// Prevent instantiation for singleton pattern
	private LogUtil() {
	}

	public static void log(IStatus status) {
		Activator.getPlugin().getLog().log(status);
	}

	public static void addStatus(MultiStatus status, IStatus childStatus) {
		if (status != null && childStatus != null) {
			status.add(childStatus);
		}
	}

	public static void log(MultiStatus status, PerformanceStats perfStats) {
		if (status != null && perfStats != null) {
			log(status, perfStats.getMeasurements());
		}
	}

	public static void log(MultiStatus status, Collection<Measurement> measurements) {
		if (status != null && measurements != null) {
			for (Measurement measurement : measurements) {
				Status measurementStatus = null;
				if (measurement.getChildren().isEmpty()) {
					measurementStatus = new Status(IStatus.INFO, Activator.getPlugin().getSymbolicName(), getLogMesage(measurement));
					addStatus(status, measurementStatus);
				} else {
					measurementStatus = new MultiStatus(Activator.getPlugin().getSymbolicName(), IStatus.INFO, getLogMesage(measurement), null);
					addStatus(status, measurementStatus);
					// Log child measurements as children
					log((MultiStatus) measurementStatus, measurement.getChildren());
				}
			}

			// Log the result multi status
			log(status);
		}
	}

	private static String getLogMesage(Measurement measurement) {
		Assert.isNotNull(measurement);
		return NLS.bind(Messages.msg_PeformanceStatistics, measurement.getName(), measurement.getTotal() / (long) 1e6);
	}
}
