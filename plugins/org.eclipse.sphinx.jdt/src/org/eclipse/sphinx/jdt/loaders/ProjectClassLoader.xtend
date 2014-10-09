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
package org.eclipse.sphinx.jdt.loaders

import java.net.URL
import java.net.URLClassLoader
import org.eclipse.core.runtime.Assert
import org.eclipse.jdt.core.IClasspathEntry
import org.eclipse.jdt.core.IJavaProject

import static extension org.eclipse.sphinx.jdt.util.JavaExtensions.*

class ProjectClassLoader extends URLClassLoader {

	private IJavaProject project;

	protected static def URL[] getJavaOutputURLs(IJavaProject javaProject) {
		Assert.isNotNull(javaProject)
		Assert.isLegal(javaProject.exists && javaProject.isOpen)

		// Retrieve and return URL of absolute file system location behind default output location of given Java project
		val outputURL = javaProject.outputLocation.location?.toFile.toURI.toURL
		if (outputURL != null) #[outputURL] else #[]
	}

	protected static def ClassLoader createDependenciesClassLoader(IJavaProject javaProject, ClassLoader parent) {
		Assert.isNotNull(javaProject)
		Assert.isLegal(javaProject.exists && javaProject.isOpen)

		var ClassLoader lastClassLoader = parent

		// Create class loader for required other projects in the workspace and add it to parent class loader hierarchy
		val entries = javaProject.getResolvedClasspath(true)
		val Iterable<String> requiredProjectNames = entries.filter[entryKind == IClasspathEntry.CPE_PROJECT].map[
			path.segment(0)]
		for (requiredProjectName : requiredProjectNames) {
			lastClassLoader = new ProjectClassLoader(requiredProjectName.javaProject, lastClassLoader)
		}

		// Create class loader for required plug-ins and libraries and add it to parent class loader hierarchy
		val libraryURLs = entries.filter[entryKind == IClasspathEntry.CPE_LIBRARY].map[file].filter[exists].map[
			toURI.toURL]
		lastClassLoader = new URLClassLoader(libraryURLs, lastClassLoader)

		lastClassLoader
	}

	new(IJavaProject javaProject, ClassLoader parent) {
		super(getJavaOutputURLs(javaProject), createDependenciesClassLoader(javaProject, parent))
		this.project = javaProject
	}

	override toString() {
		class.simpleName + " [project=" + project.project.name + "]";
	}
}
