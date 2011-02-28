/**
 * <copyright>
 * 
 * Copyright (c) See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xpand.ui.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.xtend.shared.ui.StorageFinder;
import org.eclipse.xtend.shared.ui.core.internal.ResourceID;

public class WorkspaceStorageFinder implements StorageFinder {

	protected IScopingResourceLoader workspaceResourceLoader;

	protected static final Pattern patternNamespace = Pattern.compile("::"); //$NON-NLS-1$
	protected static final Pattern patternSlash = Pattern.compile("/"); //$NON-NLS-1$

	public WorkspaceStorageFinder() {
		workspaceResourceLoader = createScopingResourceLoader();
	}

	public ResourceID findXtendXpandResourceID(IJavaProject javaProject, IStorage storage) {
		workspaceResourceLoader.setContextProject(javaProject.getProject());

		if (storage instanceof IFile) {
			IFile file = (IFile) storage;

			// Ignore copies of Xtend/Xpand files under java output folder
			IPath javaOutputPath = getJavaOutputPath(javaProject.getProject());
			if (javaOutputPath != null && javaOutputPath.isPrefixOf(file.getFullPath())) {
				return null;
			}
			return new ResourceID(workspaceResourceLoader.getDefinitionName(file, null), file.getFileExtension());
		}
		return null;
	}

	protected IPath getJavaOutputPath(IProject project) {
		try {
			IJavaProject javaProject = JavaCore.create(project);
			return javaProject.getOutputLocation();
		} catch (JavaModelException ex) {
			// Ignore exception
		}
		return null;
	}

	public IStorage findStorage(IJavaProject javaProject, ResourceID resourceID, boolean searchJars) {
		workspaceResourceLoader.setContextProject(javaProject.getProject());
		workspaceResourceLoader.setSearchArchives(searchJars);
		URL resourceURL = workspaceResourceLoader.getResource(resourceID.toFileName());
		if (resourceURL != null) {
			try {
				return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(resourceURL.toURI().getPath()));
			} catch (URISyntaxException ex) {
				// Ignore exception
			}
		}
		return null;
	}

	public int getPriority() {
		return 1;
	}

	protected IScopingResourceLoader createScopingResourceLoader() {
		return new BasicWorkspaceResourceLoader();
	}
}
