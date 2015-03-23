/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
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
package org.eclipse.sphinx.documentationview.bootstrap.internal;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.osgi.framework.BundleContext;

/**
 * General Activator to return all the files required by others to the views. Uses the Bootstrap and JQuery libraries as
 * approved by Eclipse IP process.
 */
public class Activator extends Plugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.eclipse.sphinx.documentationview.bootstrap"; //$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin;

	/** The bundle context */
	private static BundleContext context;

	private static final String JQUERY_DIR = "jquery"; //$NON-NLS-1$
	private static final String BOOTSTRAP_DIR = "dist"; //$NON-NLS-1$

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns the shared bundle context
	 *
	 * @return the shared bundle context
	 */
	public static BundleContext getContext() {
		return context;
	}

	/*
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
		Activator.context = bundleContext;
	}

	/*
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		Activator.context = null;
		super.stop(bundleContext);
	}

	public static String bootstrapDir() {
		return getDirectoryPath(new Path(BOOTSTRAP_DIR));
	}

	public static String jqueryDir() {
		return getDirectoryPath(new Path(JQUERY_DIR));
	}

	private static String getDirectoryPath(Path path) {
		URL url = FileLocator.findEntries(context.getBundle(), path)[0];
		URI uri = null;
		try {
			uri = FileLocator.toFileURL(url).toURI();
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}

		File file = new File(uri);
		return file.getAbsolutePath();
	}
}
