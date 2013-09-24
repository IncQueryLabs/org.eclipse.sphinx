/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 See4sys, itemis, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [346715] IMetaModelDescriptor methods of MetaModelDescriptorRegistry taking EObject or Resource arguments should not start new EMF transactions
 *     BMW Car IT - [373481] Performance optimizations for model loading. Added referenced projects cache.
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.scoping;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.scoping.ProjectResourceScopeProvider.IReferencedProjectsProvider;
import org.eclipse.sphinx.emf.scoping.ProjectResourceScopeProvider.ReferencedProjectsProvider;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

public class ProjectResourceScope extends AbstractResourceScope {

	protected IProject rootProject;

	// use a non-caching provider by default
	protected IReferencedProjectsProvider referencedProjectsProvider = new ReferencedProjectsProvider();

	public ProjectResourceScope(IResource resource) {
		Assert.isNotNull(resource);
		rootProject = resource.getProject();
	}

	void setReferencedProjectsProvider(IReferencedProjectsProvider referencedProjectsProvider) {
		this.referencedProjectsProvider = referencedProjectsProvider;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.core.resources.IFile, boolean)
	 */
	public boolean belongsTo(IFile file, boolean includeReferencedScopes) {
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.emf.ecore.resource.Resource, boolean)
	 */
	public boolean belongsTo(Resource resource, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(resource);
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.emf.common.util.URI, boolean)
	 */
	public boolean belongsTo(URI uri, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(uri);
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
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
			Collection<?> allReferencedProjects = referencedProjectsProvider.get(rootProject);
			return (Collection<IResource>) allReferencedProjects;
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
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.emf.ecore.resource.Resource, boolean)
	 */
	public boolean didBelongTo(Resource resource, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(resource);
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.emf.common.util.URI, boolean)
	 */
	public boolean didBelongTo(URI uri, boolean includeReferencedScopes) {
		IFile file = EcorePlatformUtil.getFile(uri);
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
	}

	protected boolean belongsToRootOrReferencedProjects(IFile file, boolean includeReferencedScopes) {
		if (file != null) {
			return rootProject.equals(file.getProject()) || includeReferencedScopes && getReferencedRoots().contains(file.getProject());
		}
		return false;
	}

	/**
	 * @deprecated Use {@link #belongsToRootOrReferencedProjects(IFile, boolean)} instead.
	 */
	@Deprecated
	protected boolean belongsToRootOrDependingProjects(IFile file, boolean includeReferencedScopes) {
		return belongsToRootOrReferencedProjects(file, includeReferencedScopes);
	}

}
