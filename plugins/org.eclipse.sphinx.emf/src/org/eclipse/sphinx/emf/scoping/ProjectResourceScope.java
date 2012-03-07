/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [346715] IMetaModelDescriptor methods of MetaModelDescriptorRegistry taking EObject or Resource arguments should not start new EMF transactions
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.scoping;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.resources.DefaultResourceChangeHandler;
import org.eclipse.sphinx.platform.resources.ResourceDeltaVisitor;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ProjectResourceScope extends AbstractResourceScope {

	static class ReferencedProjectsCache {
		class InvalidationListener implements IResourceChangeListener {
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					IResourceDelta delta = event.getDelta();
					if (delta != null) {
						IResourceDeltaVisitor visitor = new ResourceDeltaVisitor(event.getType(), new DefaultResourceChangeHandler() {
							@Override
							public void handleProjectCreated(int eventType, IProject project) {
								invalidate();
							}

							@Override
							public void handleProjectOpened(int eventType, IProject project) {
								invalidate();
							}

							@Override
							public void handleProjectRenamed(int eventType, IProject oldProject, IProject newProject) {
								invalidate();
							}

							@Override
							public void handleProjectDescriptionChanged(int eventType, IProject project) {
								invalidate();
							}

							@Override
							public void handleProjectClosed(int eventType, IProject project) {
								invalidate();
							}

							@Override
							public void handleProjectRemoved(int eventType, IProject project) {
								invalidate();
							}
						});

						delta.accept(visitor);
					}
				} catch (CoreException ex) {
					PlatformLogUtil.logAsError(Activator.getDefault(), ex);
				}
			}
		}

		Map<IProject, Collection<IProject>> cache = new WeakHashMap<IProject, Collection<IProject>>();

		ReferencedProjectsCache() {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(new InvalidationListener());
		}

		synchronized Collection<IProject> get(IProject p) {
			Collection<IProject> referencedProjects = cache.get(p);

			if (referencedProjects == null) {
				referencedProjects = ExtendedPlatform.getAllReferencedProjects(p);

				cache.put(p, referencedProjects);
			}

			return referencedProjects;
		}

		synchronized void invalidate() {
			cache.clear();
		}
	}

	protected IProject rootProject;

	protected ReferencedProjectsCache referencedProjectsCache;

	public ProjectResourceScope(IResource resource) {
		Assert.isNotNull(resource);
		rootProject = resource.getProject();
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.core.resources.IFile, boolean)
	 */
	public boolean belongsTo(IFile file, boolean includeReferencedScopes) {
		return belongsToRootOrDependingProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.emf.ecore.resource.Resource, boolean)
	 */
	public boolean belongsTo(Resource resource, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(resource);
		return belongsToRootOrDependingProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.emf.common.util.URI, boolean)
	 */
	public boolean belongsTo(URI uri, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(uri);
		return belongsToRootOrDependingProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getReferencingRoots()
	 */
	@SuppressWarnings("unchecked")
	public Collection<IResource> getReferencingRoots() {
		if (rootProject != null) {
			Collection<?> allReferencingProjects = ExtendedPlatform.getAllReferencingProjects(rootProject);
			return (Collection<IResource>) allReferencingProjects;
		} else {
			return Collections.emptySet();
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getReferencedRoots()
	 */
	@SuppressWarnings("unchecked")
	public Collection<IResource> getReferencedRoots() {
		if (rootProject != null) {
			if (referencedProjectsCache == null) {
				referencedProjectsCache = new ReferencedProjectsCache();
			}
			Collection<?> allReferencingProjects = referencedProjectsCache.get(rootProject);
			return (Collection<IResource>) allReferencingProjects;
		} else {
			return Collections.emptySet();
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getRoot()
	 */
	public IResource getRoot() {
		return rootProject;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.core.resources.IFile, boolean)
	 */
	public boolean didBelongTo(IFile file, boolean includeReferencedScopes) {
		return belongsToRootOrDependingProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.emf.ecore.resource.Resource, boolean)
	 */
	public boolean didBelongTo(Resource resource, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(resource);
		return belongsToRootOrDependingProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.emf.common.util.URI, boolean)
	 */
	public boolean didBelongTo(URI uri, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(uri);
		return belongsToRootOrDependingProjects(file, includeReferencedScopes);
	}

	protected boolean belongsToRootOrDependingProjects(IFile file, boolean includeReferencedScopes) {
		if (file != null) {
			return rootProject.equals(file.getProject()) || includeReferencedScopes && getReferencedRoots().contains(file.getProject());
		}
		return false;
	}
}
