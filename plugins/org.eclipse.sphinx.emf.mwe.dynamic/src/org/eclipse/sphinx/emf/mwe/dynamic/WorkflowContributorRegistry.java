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
 *     itemis - [458921] Newly introduced registries for metamodel serives, check validators and workflow contributors are not standalone-safe
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic;

import static org.eclipse.sphinx.jdt.util.JavaExtensions.isDevModePluginClasspathLocation;
import static org.eclipse.sphinx.jdt.util.JavaExtensions.isInstalledPluginClasspathRootLocation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.osgi.framework.Bundle;

public class WorkflowContributorRegistry {

	private static final String EXTP_WORKFLOW_CONTRIBUTORS = Activator.INSTANCE.getSymbolicName() + ".workflowContributors"; //$NON-NLS-1$
	private static final String ELEM_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String ATTR_PLUGIN_ID = "pluginId"; //$NON-NLS-1$

	public static final WorkflowContributorRegistry INSTANCE = new WorkflowContributorRegistry(Platform.getExtensionRegistry(),
			PlatformLogUtil.getLog(Activator.getPlugin()));

	private Set<String> contributorPluginIds = null;

	private IExtensionRegistry extensionRegistry;

	private ILog log;

	private WorkflowContributorRegistry(IExtensionRegistry extensionRegistry, ILog log) {
		this.extensionRegistry = extensionRegistry;
		this.log = log;
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

	/*
	 * pluginId = org.eclipse.sphinx.examples.workflows => expected to match:
	 * W:/eclipse-sphinx/org.eclipse.sphinx/plugins/org.eclipse.sphinx.examples.workflows/bin/, W:/eclipse
	 * -sphinx/org.eclipse.sphinx/examples/org.eclipse.sphinx.examples.workflows/bin/org/eclipse/sphinx/examples
	 * /workflows/model/PrintModelContentWorkflow.class
	 * W:/eclipse-sphinx/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool
	 * /plugins/org.eclipse.sphinx.examples.workflows_0.9.0.v20141209-1519.jar, expected to ignore: /External Plug-in
	 * Libraries, /MyProject, C:/Program%20Files/Java/jdk1.7.0_55/jre/lib/resources.jar,
	 * W:/eclipse-sphinx/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool
	 * /plugins/org.eclipse.sphinx.examples.workflows.lib_0.9.0.v20141209-1519.jar
	 */
	public boolean isContributorClasspathLocation(IPath classpathLocation) {
		return getContributorPluginId(classpathLocation) != null;
	}

	public String getContributorPluginId(IPath classpathLocation) {
		if (classpathLocation != null && classpathLocation.segmentCount() > 1) {
			for (String id : getContributorPluginIds()) {
				if (isInstalledPluginClasspathRootLocation(id, classpathLocation) || isDevModePluginClasspathLocation(id, classpathLocation)) {
					return id;
				}
			}
		}
		return null;
	}

	public Class<?> loadContributedClass(IType contributedType) throws ClassNotFoundException {
		Assert.isNotNull(contributedType);

		String contributorPluginId = getContributorPluginId(contributedType.getPath());
		if (contributorPluginId != null) {
			Bundle contributorBundle = ExtendedPlatform.loadBundle(contributorPluginId);
			if (contributorBundle != null) {
				return contributorBundle.loadClass(contributedType.getFullyQualifiedName());
			}
		}
		throw new ClassNotFoundException(contributedType.getFullyQualifiedName());
	}
}
