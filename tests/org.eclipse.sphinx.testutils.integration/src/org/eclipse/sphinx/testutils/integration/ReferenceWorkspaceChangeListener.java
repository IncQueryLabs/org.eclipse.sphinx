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
package org.eclipse.sphinx.testutils.integration;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.resource.ResourceProblemMarkerService;
import org.eclipse.sphinx.platform.resources.DefaultResourceChangeHandler;
import org.eclipse.sphinx.platform.resources.ResourceDeltaVisitor;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * Listens for {@link Resource resource}s that have been loaded or saved and requests the problem markers of underlying
 * {@link IFile file}s to be updated according to the {@link Resource#getErrors() errors} and
 * {@link Resource#getWarnings() warnings} of each loaded or saved {@link Resource} resource.
 * 
 * @see ResourceProblemMarkerService#updateProblemMarkers(Collection, boolean,
 *      org.eclipse.core.runtime.IProgressMonitor)
 */
public class ReferenceWorkspaceChangeListener implements IResourceChangeListener {
	protected Set<IFile> changedFiles = new HashSet<IFile>();
	protected Set<IFile> addedFiles = new HashSet<IFile>();
	protected Set<String> changedDescriptionProjects = new HashSet<String>();
	protected Set<String> renamedProjects = new HashSet<String>();
	protected Map<String, Collection<String>> changedSettingProjects = new HashMap<String, Collection<String>>();

	/**
	 * Default constructor.
	 */
	public ReferenceWorkspaceChangeListener() {

	}

	/*
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.finalize();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {

				// Investigate resource delta on saved files
				IResourceDeltaVisitor visitor = new ResourceDeltaVisitor(event.getType(), new DefaultResourceChangeHandler() {

					@Override
					public void handleProjectDescriptionChanged(int eventType, IProject project) {
						changedDescriptionProjects.add(project.getName());
					}

					@Override
					public void handleProjectSettingsChanged(int eventType, IProject project, Collection<String> preferenceFileNames) {
						if (changedSettingProjects.containsKey(project.getName())) {
							changedSettingProjects.get(project.getName()).addAll(preferenceFileNames);

						} else {
							changedSettingProjects.put(project.getName(), preferenceFileNames);
						}
					}

					@Override
					public void handleFileAdded(int eventType, IFile file) {
						addedFiles.add(file);
					}

					@Override
					public void handleProjectOpened(int eventType, IProject project) {
					}

					@Override
					public void handleFileChanged(int eventType, IFile file) {
						changedFiles.add(file);
					}

					@Override
					public void handleProjectRenamed(int eventType, IProject oldProject, IProject newProject) {
						renamedProjects.add(newProject.getName());
					}

					@Override
					public void handleFileMoved(int eventType, IFile oldFile, IFile newFile) {
						addedFiles.add(newFile);
					}

				});
				delta.accept(visitor);

			}
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
	}

	public Set<IFile> getChangedFiles() {
		return changedFiles;
	}

	public Set<IFile> getAddedFiles() {
		return addedFiles;
	}

	public Set<String> getChangedDescriptionProjects() {
		return changedDescriptionProjects;
	}

	public Set<String> getRenamedProjects() {
		return renamedProjects;
	}

	public Map<String, Collection<String>> getChangedSettingProjects() {
		return changedSettingProjects;
	}

	public void clearHistory() {
		changedFiles.clear();
		addedFiles.clear();
		renamedProjects.clear();
		changedDescriptionProjects.clear();
		changedSettingProjects.clear();

	}
}