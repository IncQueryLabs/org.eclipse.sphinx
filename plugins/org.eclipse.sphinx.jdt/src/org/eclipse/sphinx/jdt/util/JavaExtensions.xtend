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
package org.eclipse.sphinx.jdt.util

import java.io.File
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.IPath
import org.eclipse.jdt.core.IClasspathEntry
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.JavaCore

class JavaExtensions {

	static def File getFile(IClasspathEntry entry) {
		if (entry.path.toFile.exists) {
			entry.path.toFile
		} else {
			ResourcesPlugin.getWorkspace.root.location.append(entry.path).toFile
		}
	}

	static def IJavaProject getJavaProject(String projectName) {
		JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName))
	}

	/**
	 * Returns the absolute path in the local file system corresponding to given workspace-relative path.
	 *
	 * @param workspacePath the workspace-relative path to some resource in the workspace
	 *
	 * @return the absolute path in the local file system corresponding to given <code>workspacePath</code>, or null if no path can be determined
	 */
	static def IPath getLocation(IPath workspacePath) {
		ResourcesPlugin.getWorkspace().getRoot().findMember(workspacePath)?.getLocation()
	}
}
