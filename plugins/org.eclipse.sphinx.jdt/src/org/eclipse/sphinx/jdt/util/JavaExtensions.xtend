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
import org.eclipse.jdt.launching.IVMInstall
import org.eclipse.jdt.launching.JavaRuntime
import org.eclipse.jdt.launching.IVMInstall2
import org.eclipse.core.runtime.Assert
import org.eclipse.osgi.util.NLS
import org.eclipse.sphinx.jdt.internal.messages.Messages
import org.eclipse.sphinx.platform.util.PlatformLogUtil
import org.eclipse.sphinx.jdt.internal.Activator

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
	
	static def void validateCompilerCompliance(String compliance) {
		val IVMInstall install = JavaRuntime.getDefaultVMInstall()
		if (install instanceof IVMInstall2) {
			val String compilerCompliance = getCompilerCompliance(install, compliance)
			// Compliance to set must be equal or less than compliance level from VMInstall.
			if (!compilerCompliance.equals(compliance)) {
				try {
					val float complianceToSet = Float.parseFloat(compliance)
					val float complianceFromVM = Float.parseFloat(compilerCompliance)
					Assert.isLegal(complianceToSet <= complianceFromVM, NLS.bind(Messages.error_JRECompliance_NotCompatible, compliance, compilerCompliance))
				} catch (NumberFormatException ex) {
					PlatformLogUtil.logAsWarning(Activator.getDefault(), ex)
				}
			}
		}
	}

	private static def String getCompilerCompliance(IVMInstall2 vMInstall, String defaultCompliance) {
		Assert.isNotNull(vMInstall)

		val String version = vMInstall.getJavaVersion();
		if (version == null) {
			return defaultCompliance;
		} else if (version.startsWith(JavaCore.VERSION_1_8)) {
			return JavaCore.VERSION_1_8;
		} else if (version.startsWith(JavaCore.VERSION_1_7)) {
			return JavaCore.VERSION_1_7;
		} else if (version.startsWith(JavaCore.VERSION_1_6)) {
			return JavaCore.VERSION_1_6;
		} else if (version.startsWith(JavaCore.VERSION_1_5)) {
			return JavaCore.VERSION_1_5;
		} else if (version.startsWith(JavaCore.VERSION_1_4)) {
			return JavaCore.VERSION_1_4;
		} else if (version.startsWith(JavaCore.VERSION_1_3)) {
			return JavaCore.VERSION_1_3;
		} else if (version.startsWith(JavaCore.VERSION_1_2)) {
			return JavaCore.VERSION_1_3;
		} else if (version.startsWith(JavaCore.VERSION_1_1)) {
			return JavaCore.VERSION_1_3;
		}
		return defaultCompliance;
	}
}
