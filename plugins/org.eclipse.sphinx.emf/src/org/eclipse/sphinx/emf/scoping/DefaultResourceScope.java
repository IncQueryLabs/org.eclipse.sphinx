/**
 * <copyright>
 * 
 * Copyright (c) 2013 itemis and others.
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
package org.eclipse.sphinx.emf.scoping;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Provides a default implementation for the {@link IResourceScope} interface.
 * <p>
 * Clients that wish to implement custom resource scopes can extend this class and override only the methods which they
 * are interested in.
 * </p>
 * 
 * @see IResourceScope
 * @since 0.8.0
 */
public class DefaultResourceScope implements IResourceScope {

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#exists()
	 */
	public boolean exists() {
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getRoot()
	 */
	public IResource getRoot() {
		return null;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getReferencedRoots()
	 */
	public Collection<IResource> getReferencedRoots() {
		return Collections.emptyList();
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getReferencingRoots()
	 */
	public Collection<IResource> getReferencingRoots() {
		return Collections.emptyList();
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getPersistedFiles(boolean)
	 */
	public Collection<IFile> getPersistedFiles(boolean includeReferencedScopes) {
		return Collections.emptyList();
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#getLoadedResources(org.eclipse.emf.transaction.
	 * TransactionalEditingDomain, boolean)
	 */
	public Collection<Resource> getLoadedResources(TransactionalEditingDomain editingDomain, boolean includeReferencedScopes) {
		return Collections.emptyList();
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.core.resources.IFile, boolean)
	 */
	public boolean belongsTo(IFile file, boolean includeReferencedScopes) {
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.emf.ecore.resource.Resource, boolean)
	 */
	public boolean belongsTo(Resource resource, boolean includeReferencedScopes) {
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#belongsTo(org.eclipse.emf.common.util.URI, boolean)
	 */
	public boolean belongsTo(URI uri, boolean includeReferencedScopes) {
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.core.resources.IFile, boolean)
	 */
	public boolean didBelongTo(IFile file, boolean includeReferencedScopes) {
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.emf.ecore.resource.Resource, boolean)
	 */
	public boolean didBelongTo(Resource resource, boolean includeReferencedScopes) {
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#didBelongTo(org.eclipse.emf.common.util.URI, boolean)
	 */
	public boolean didBelongTo(URI uri, boolean includeReferencedScopes) {
		return false;
	}
}