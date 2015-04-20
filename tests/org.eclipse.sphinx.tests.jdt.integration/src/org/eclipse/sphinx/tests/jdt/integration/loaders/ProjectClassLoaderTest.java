/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.tests.jdt.integration.loaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sphinx.jdt.loaders.DelegatingCompositeBundleClassLoader;
import org.eclipse.sphinx.jdt.loaders.ProjectClassLoader;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

@SuppressWarnings("nls")
public class ProjectClassLoaderTest extends DefaultIntegrationTestCase {

	private static final String[] PLUGIN_DEPENDENCIES = new String[] { "org.eclipse.sphinx.emf", "org.eclipse.sphinx.emf.check",
			"org.eclipse.sphinx.emf.mwe.dynamic", "org.eclipse.sphinx.emf.workspace", "org.eclipse.sphinx.examples.hummingbird20",
			"org.eclipse.sphinx.examples.hummingbird.ide", "org.eclipse.sphinx.examples.workflows.lib", "org.eclipse.sphinx.jdt",
			"org.eclipse.sphinx.platform" };

	private IJavaProject javaProject;

	public ProjectClassLoaderTest() {
		// Set subset of projects to load
		Set<String> projectsToLoad = getProjectSubsetToLoad();
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_WORKFLOWS);
	}

	protected List<ClassLoader> getClassLoaderHierarchy() {
		List<ClassLoader> classLoaderHierarchy = new ArrayList<ClassLoader>();
		ProjectClassLoader projectClassLoader = new ProjectClassLoader(getProject());
		classLoaderHierarchy.add(projectClassLoader);

		ClassLoader parentClassLoader = projectClassLoader.getParent();
		while (parentClassLoader != null) {
			classLoaderHierarchy.add(parentClassLoader);
			parentClassLoader = parentClassLoader.getParent();
		}

		return classLoaderHierarchy;
	}

	protected IJavaProject getProject() {
		if (javaProject == null) {
			javaProject = JavaCore.create(refWks.hbProject20_Workflows);
		}
		return javaProject;
	}

	public void testClassLoaderHierarchy() throws Exception {
		List<ClassLoader> classLoaderHierarchy = getClassLoaderHierarchy();
		assertEquals(6, classLoaderHierarchy.size());
	}

	public void testProjectClassLoader() throws Exception {
		List<ProjectClassLoader> projectClassLoaders = new ArrayList<ProjectClassLoader>();
		List<DelegatingCompositeBundleClassLoader> compositeBundleClassLoaders = new ArrayList<DelegatingCompositeBundleClassLoader>();
		for (ClassLoader classLoader : getClassLoaderHierarchy()) {
			if (classLoader instanceof ProjectClassLoader) {
				projectClassLoaders.add((ProjectClassLoader) classLoader);
			}
			if (classLoader instanceof DelegatingCompositeBundleClassLoader) {
				compositeBundleClassLoaders.add((DelegatingCompositeBundleClassLoader) classLoader);
			}
		}
		assertEquals(1, projectClassLoaders.size());
		assertEquals(getProject(), projectClassLoaders.get(0).getProject());

		assertEquals(1, compositeBundleClassLoaders.size());
		List<ClassLoader> bundleClassLoaders = compositeBundleClassLoaders.get(0).getBundleClassLoaders();
		assertTrue(bundleClassLoaders.size() > 0);
	}

	public void testDelegatingCompositeBundleClassLoader() throws Exception {
		List<DelegatingCompositeBundleClassLoader> composeBundleClassLoaders = new ArrayList<DelegatingCompositeBundleClassLoader>();
		for (ClassLoader classLoader : getClassLoaderHierarchy()) {
			if (classLoader instanceof DelegatingCompositeBundleClassLoader) {
				composeBundleClassLoaders.add((DelegatingCompositeBundleClassLoader) classLoader);
			}
		}

		assertEquals(1, composeBundleClassLoaders.size());
		List<ClassLoader> bundleClassLoaders = composeBundleClassLoaders.get(0).getBundleClassLoaders();
		assertTrue(bundleClassLoaders.size() > 0);

		for (String dependency : PLUGIN_DEPENDENCIES) {
			boolean found = false;
			for (ClassLoader classLoader : bundleClassLoaders) {
				if (classLoader != null && classLoader.toString().indexOf(dependency) > 0) {
					found = true;
					break;
				}
			}
			if (!found) {
				fail(dependency + " plugin not found in bundles of " + composeBundleClassLoaders);
			}
		}
	}
}
