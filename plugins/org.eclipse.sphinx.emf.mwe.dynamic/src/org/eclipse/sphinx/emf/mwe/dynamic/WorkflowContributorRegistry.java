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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class WorkflowContributorRegistry {

	private final static String EXTP_WORKFLOW_CONTRIBUTORS = Activator.INSTANCE.getSymbolicName() + ".workflowContributors"; //$NON-NLS-1$
	private final static String ELEM_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private final static String ATTR_ID = "id"; //$NON-NLS-1$

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
					String id = element.getAttribute(ATTR_ID);
					if (id == null || id.isEmpty()) {
						String msg = "Missing contributor id in " + EXTP_WORKFLOW_CONTRIBUTORS + " extension from " //$NON-NLS-1$ //$NON-NLS-2$
								+ element.getContributor().getName();
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), new RuntimeException(msg));
						log.log(status);
						continue;
					}

					contributorPluginIds.add(id);

					// // Obtain contributor bundle
					// Bundle bundle = null;
					// if (id.equals(element.getContributor().getName())) {
					// bundle = Platform.getBundle(element.getContributor().getName());
					// } else {
					// bundle = ExtendedPlatform.loadBundle(id);
					// }
					// if (bundle == null) {
					//						String msg = "Unable to load workflow contributor bundle " + id; //$NON-NLS-1$
					// IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), new RuntimeException(msg));
					// log.log(status);
					// continue;
					// }
					//
					// // Retrieve and register the path to the contributor bundle's classpath root
					//					URL url = bundle.getResource("/"); //$NON-NLS-1$
					// try {
					// URL resolvedURL = FileLocator.resolve(url);
					// contributorClasspathRootPaths.add(new Path(resolvedURL.getPath()).removeTrailingSeparator());
					// } catch (Exception ex) {
					// IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					// log.log(status);
					// }
				}
			}
		}
	}
}
