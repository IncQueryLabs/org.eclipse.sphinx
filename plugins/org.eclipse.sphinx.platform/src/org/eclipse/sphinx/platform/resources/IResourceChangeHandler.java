/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.platform.resources;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public interface IResourceChangeHandler {

	void handleProjectCreated(int eventType, IProject project);

	void handleProjectOpened(int eventType, IProject project);

	void handleProjectRenamed(int eventType, IProject oldProject, IProject newProject);

	void handleProjectDescriptionChanged(int eventType, IProject project);

	void handleProjectSettingsChanged(int eventType, IProject project, Collection<String> preferenceFileNames);

	void handleProjectAboutToBeClosed(IProject project);

	void handleProjectAboutToBeDeleted(IProject project);

	void handleProjectClosed(int eventType, IProject project);

	void handleProjectRemoved(int eventType, IProject project);

	void handleFileAdded(int eventType, IFile file);

	void handleFileChanged(int eventType, IFile file);

	void handleFileMoved(int eventType, IFile oldFile, IFile newFile);

	void handleFileRemoved(int eventType, IFile file);
}
