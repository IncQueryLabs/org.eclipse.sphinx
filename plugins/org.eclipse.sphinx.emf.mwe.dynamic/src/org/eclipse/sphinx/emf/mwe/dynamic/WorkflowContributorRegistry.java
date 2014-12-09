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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.jdt.util.JavaExtensions;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.osgi.framework.Bundle;

public class WorkflowContributorRegistry {

	private final static String EXTP_WORKFLOW_CONTRIBUTORS = Activator.INSTANCE.getSymbolicName() + ".workflowContributors"; //$NON-NLS-1$
	private final static String ELEM_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private final static String ATTR_PLUGIN_ID = "pluginId"; //$NON-NLS-1$

	/**
	 * Returns a characteristic prefix of the classpath locations within the installed plug-in with given
	 * <code>id</code>. It is composed of the Platform's installation location followed by the name of the "plugins"
	 * folder inside it and the beginning of the name of the given plug-in's JAR file.
	 *
	 * @param pluginId
	 *            The id of the installed plug-in in question.
	 * @return The resulting characteristic classpath location prefix.
	 */
	private static IPath getInstalledPluginClasspathLocationPrefix(String pluginId) {
		IPath path = new Path(Platform.getInstallLocation().getURL().getPath());
		path = path.append("plugins"); //$NON-NLS-1$
		path = path.append(pluginId);
		return path;
	}

	/**
	 * Returns a characteristic snippet of the classpath locations within the "dev" mode plug-in with given
	 * <code>id</code>. It is composed of the name of the corresponding plug-in project in the development workspace
	 * followed by the name of the default output folder ("bin") of the same.
	 *
	 * @param pluginId
	 *            The id of the "dev" mode plug-in in question.
	 * @return The resulting characteristic classpath location snippet.
	 */
	private static IPath getDevModePluginClasspathLocationSnippet(String pluginId) {
		IPath path = new Path(pluginId);
		path = path.append(JavaExtensions.DEFAULT_OUTPUT_FOLDER_NAME);
		return path;
	}

	public static final WorkflowContributorRegistry INSTANCE = new WorkflowContributorRegistry(Platform.getExtensionRegistry(), Activator.getPlugin()
			.getLog());

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
	 * Libraries, /MyProject, C:/Program%20Files/Java/jdk1.7.0_55/jre/lib/resources.jar
	 */
	// TODO Consider to move this method to JavaExtensions
	public boolean isContributorClasspathLocation(IPath classpathLocation) {
		return getContributorPluginId(classpathLocation) != null;
	}

	// TODO Consider to move this method to JavaExtensions
	public String getContributorPluginId(IPath classpathLocation) {
		if (classpathLocation != null && classpathLocation.segmentCount() > 1) {
			for (String id : getContributorPluginIds()) {
				if (getInstalledPluginClasspathLocationPrefix(id).isPrefixOf(classpathLocation)
						|| classpathLocation.toString().contains(getDevModePluginClasspathLocationSnippet(id).toString())) {
					return id;
				}
			}
		}
		return null;
	}

	// TODO Consider to move this method to JavaExtensions
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

	// TODO Consider to move this method to JavaExtensions
	public IPath getContributorClasspathRootLocation(String contributorPluginId) {
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
