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

public class DefaultResourceChangeHandler implements IResourceChangeHandler {

	public void handleProjectCreated(int eventType, IProject project) {
		// Do nothing by default
	}

	public void handleProjectOpened(int eventType, IProject project) {
		// Do nothing by default
	}

	public void handleProjectRenamed(int eventType, IProject oldProject, IProject newProject) {
		// Do nothing by default
	}

	public void handleProjectDescriptionChanged(int eventType, IProject project) {
		// Do nothing by default
	}

	public void handleProjectSettingsChanged(int eventType, IProject project, Collection<String> preferenceFileNames) {
		// Do nothing by default
	}

	public void handleProjectAboutToBeClosed(IProject project) {
		// Do nothing by default
	}

	public void handleProjectAboutToBeDeleted(IProject project) {
		// Do nothing by default
	}

	public void handleProjectClosed(int eventType, IProject project) {
		// Do nothing by default
	}

	public void handleProjectRemoved(int eventType, IProject project) {
		// Do nothing by default
	}

	public void handleFileAdded(int eventType, IFile file) {
		// Do nothing by default
	}

	public void handleFileChanged(int eventType, IFile file) {
		// Do nothing by default
	}

	public void handleFileMoved(int eventType, IFile oldFile, IFile newFile) {
		// Do nothing by default
	}

	public void handleFileRemoved(int eventType, IFile file) {
		// Do nothing by default
	}
}
