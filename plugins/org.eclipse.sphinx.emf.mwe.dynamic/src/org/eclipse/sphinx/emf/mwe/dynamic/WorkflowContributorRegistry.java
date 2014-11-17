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
package org.eclipse.sphinx.emf.mwe.dynamic;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.jdt.util.JavaExtensions;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.osgi.framework.Bundle;

public class WorkflowContributorRegistry {

	private final static String EXTP_WORKFLOW_CONTRIBUTORS = Activator.INSTANCE.getSymbolicName() + ".workflowContributors"; //$NON-NLS-1$
	private final static String ELEM_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private final static String ATTR_PLUGIN_ID = "pluginId"; //$NON-NLS-1$

	public static final WorkflowContributorRegistry INSTANCE = new WorkflowContributorRegistry(Platform.getExtensionRegistry(), Activator.getPlugin()
			.getLog());

	private Set<IPath> contributorClasspathRootPaths = null;
	private Set<String> contributorPluginIds = null;

	private IExtensionRegistry extensionRegistry;

	private ILog log;

	private WorkflowContributorRegistry(IExtensionRegistry extensionRegistry, ILog log) {
		this.extensionRegistry = extensionRegistry;
		this.log = log;
	}

	public Set<IPath> getContributorClasspathRootPaths() {
		initialize();
		return contributorClasspathRootPaths;
	}

	public Set<String> getContributorPluginIds() {
		initialize();
		return contributorPluginIds;
	}

	/**
	 * Initialize internal data by reading from platform registry
	 */
	private void initialize() {
		if (extensionRegistry == null) {
			return;
		}

		if (contributorPluginIds == null) {
			synchronized (this) {
				contributorPluginIds = new HashSet<String>();

				IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(EXTP_WORKFLOW_CONTRIBUTORS);
				for (IConfigurationElement element : elements) {
					if (!element.getName().equals(ELEM_CONTRIBUTOR)) {
						continue;
					}

					// Retrieve contributor bundle id
					String id = element.getAttribute(ATTR_PLUGIN_ID);
					if (id == null || id.isEmpty()) {
						String msg = "Missing contributor id in " + EXTP_WORKFLOW_CONTRIBUTORS + " extension from " //$NON-NLS-1$ //$NON-NLS-2$
								+ element.getContributor().getName();
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), new RuntimeException(msg));
						log.log(status);
						continue;
					}

					contributorPluginIds.add(id);
				}
			}
		}
	}

	// TODO Consider to move this method to ExtendedPlatform
	public boolean isContributorClasspathRootPath(IPath path) {
		if (path != null && path.segmentCount() > 0) {
			for (String id : getContributorPluginIds()) {
				// Classpath root paths of binary plug-ins: end with plug-in id
				if (path.lastSegment().equals(id)) {
					return true;
				} else {
					// Classpath root paths of plug-in projects living in a development workbench and running in a
					// runtime workbench: end with default output folder name preceded by plug-in id
					if (path.lastSegment().equals(JavaExtensions.DEFAULT_OUTPUT_FOLDER_NAME)) {
						if (path.removeLastSegments(1).lastSegment().equals(id)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// TODO Consider to move this method to ExtendedPlatform
	public IPath getContributorClassPathRootPath(String contributorPluginId) {
		try {
			// Retrieve contributor bundle
			Bundle contributorBundle = ExtendedPlatform.loadBundle(contributorPluginId);
			if (contributorBundle != null) {
				// Retrieve and register the path to the contributor bundle's classpath root
				URL classpathRootURL = contributorBundle.getResource("/"); //$NON-NLS-1$
				URL resolvedClasspathRootURL = FileLocator.resolve(classpathRootURL);
				return new Path(resolvedClasspathRootURL.getPath()).removeTrailingSeparator();
			}
		} catch (IOException ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			log.log(status);
		}
		return null;
	}
}
