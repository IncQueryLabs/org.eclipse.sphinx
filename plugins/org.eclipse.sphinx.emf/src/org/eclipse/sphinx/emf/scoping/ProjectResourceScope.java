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
package org.eclipse.sphinx.emf.scoping;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

public class ProjectResourceScope extends AbstractResourceScope {

	protected IProject rootProject;

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
		IFile file = getFile(resource);
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
	public Collection<IResource> getReferencingRoots() {
		HashSet<IResource> dependingRoots = new HashSet<IResource>();
		if (rootProject != null) {
			dependingRoots.addAll(ExtendedPlatform.getAllReferencingProjects(rootProject));
		}
		return dependingRoots;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getReferencedRoots()
	 */
	public Collection<IResource> getReferencedRoots() {
		HashSet<IResource> dependingRoots = new HashSet<IResource>();
		if (rootProject != null) {
			dependingRoots.addAll(ExtendedPlatform.getAllReferencedProjects(rootProject));
		}
		return dependingRoots;
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
		IFile file = getFile(resource);
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
