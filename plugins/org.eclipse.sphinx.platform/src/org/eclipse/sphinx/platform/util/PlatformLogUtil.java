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
package org.eclipse.sphinx.platform.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;

/**
 * This class is used to log messages to the platform error log.
 */
public final class PlatformLogUtil {

	public static void logAsError(Plugin plugin, Object object) {
		IStatus status = StatusUtil.createErrorStatus(plugin, object);
		if (plugin != null) {
			plugin.getLog().log(status);
		} else {
			printStatus(status);
		}
	}

	public static void logAsWarning(Plugin plugin, Object object) {
		IStatus status = StatusUtil.createWarningStatus(plugin, object);
		if (plugin != null) {
			plugin.getLog().log(status);
		} else {
			printStatus(status);
		}
	}

	public static void logAsInfo(Plugin plugin, Object object) {
		IStatus status = StatusUtil.createInfoStatus(plugin, object);
		if (plugin != null) {
			plugin.getLog().log(status);
		} else {
			printStatus(status);
		}
	}

	private static void printStatus(IStatus status) {
		Assert.isNotNull(status);

		// TODO Provide a somehow more sophisticated implementation
		System.out.println(status.getMessage());
		Throwable exception = status.getException();
		if (exception != null) {
			exception.printStackTrace();
		}
	}
}
