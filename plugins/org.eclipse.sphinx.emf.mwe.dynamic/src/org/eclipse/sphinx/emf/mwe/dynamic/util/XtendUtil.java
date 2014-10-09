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
package org.eclipse.sphinx.emf.mwe.dynamic.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sphinx.emf.mwe.dynamic.IXtendConstants;

public class XtendUtil {

	public static boolean isXtendFile(IFile file) {
		return IXtendConstants.XTEND_FILE_EXTENSION.equals(file.getFileExtension());
	}

	public static IPath getJavaPath(IPath xtendOrJavaPath) {
		IPath javaPath = new Path(""); //$NON-NLS-1$

		for (String segment : xtendOrJavaPath.segments()) {
			if (segment.equals(IXtendConstants.JAVA_SRC_FOLDER_NAME)) {
				javaPath = javaPath.append(IXtendConstants.XTEND_GEN_FOLDER_NAME);
			} else {
				javaPath = javaPath.append(segment);
			}
		}

		return javaPath.removeFileExtension().addFileExtension(IXtendConstants.JAVA_FILE_EXTENSION);
	}

	public static IFile getJavaFile(IFile xtendOrJavaFile) {
		IPath javaPath = getJavaPath(xtendOrJavaFile.getFullPath());
		return ResourcesPlugin.getWorkspace().getRoot().getFile(javaPath);
	}
}
