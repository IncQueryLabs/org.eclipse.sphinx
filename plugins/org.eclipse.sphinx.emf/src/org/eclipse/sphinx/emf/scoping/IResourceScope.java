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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

public interface IResourceScope {

	/**
	 * Returns the root {@link IResource} of that {@link IResourceScope resource scope}.
	 * 
	 * @return The root {@link IResource resource} of that scope.
	 */
	IResource getRoot();

	/**
	 * Returns true if this {@link IResourceScope resource scope} exists.
	 * <p>
	 * This method is guaranteed to have a very little performance overhead.
	 * </p>
	 * 
	 * @return <code>true</code> if the model exist (i.e if all user defined conditions required are full filled)
	 */
	boolean exists();

	/**
	 * Determines if a {@link IFile file} belongs to this scope.
	 * 
	 * @param file
	 *            The {@link IFile file} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return <code>true</code> if the {@link IFile file} is in the scope.
	 */
	boolean belongsTo(IFile file, boolean includeReferencedScopes);

	/**
	 * Determines if a {@link Resource resource} belongs to this scope.
	 * 
	 * @param resource
	 *            The {@link Resource resource} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return <code>true</code> if the {@link Resource resource} is in the scope.
	 */
	boolean belongsTo(Resource resource, boolean includeReferencedScopes);

	/**
	 * Determines if an {@link URI uri} point to an element that belongs to this scope.
	 * 
	 * @param uri
	 *            The {@link URI uri} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return <code>true</code> if the {@link URI uri} is in the scope.
	 */
	boolean belongsTo(URI uri, boolean includeReferencedScopes);

	/**
	 * Determines if a {@link IFile file} did belong to this scope (i.e is no more in the scope but was previously in).
	 * 
	 * @param file
	 *            The {@link IFile file} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return <code>true</code> if the {@link IFile file} was in the scope.
	 */
	boolean didBelongTo(IFile file, boolean includeReferencedScopes);

	/**
	 * Determines if a {@link Resource resource} did belong to this scope (i.e is no more in the scope but was
	 * previously in).
	 * 
	 * @param resource
	 *            The {@link Resource resource} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return <code>true</code> if the {@link Resource resource} was in the scope.
	 */
	boolean didBelongTo(Resource resource, boolean includeReferencedScopes);

	/**
	 * Determines if an {@link URI uri} did belong to this scope (i.e is no more in the scope but was previously in).
	 * 
	 * @param uri
	 *            The {@link URI uri} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return <code>true</code> if the {@link URI uri} was in the scope.
	 */
	boolean didBelongTo(URI uri, boolean includeReferencedScopes);

	/**
	 * Returns the roots of other {@link IResourceScope resource scope}s which are referenced by this
	 * {@link IResourceScope resource scope}.
	 * 
	 * @return {@link Collection} of resources containing all {@link IResourceScope resource scope}'s roots referenced
	 *         by this scope.
	 */
	Collection<IResource> getReferencedRoots();

	/**
	 * Returns the roots of other {@link IResourceScope resource scope}s which reference this {@link IResourceScope
	 * resource scope}.
	 * 
	 * @return {@link Collection} of {@link IResource workspac resources} containing all {@link IResourceScope resource
	 *         scope}'s roots referencing this scope.
	 */
	Collection<IResource> getReferencingRoots();

	/**
	 * Returns all the files in this scope persisted in the workspace.
	 * 
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return {@link Collection} of persisted {@link IFile file}s owned by this scope.
	 */
	Collection<IFile> getPersistedFiles(boolean includeReferencedScopes);

	/**
	 * Returns {@link IResource resource}s loaded in the given editing domain being part of that scope.
	 * 
	 * @param editingDomain
	 *            The {@link TransactionalEditingDomain editing domain} to be investigated.
	 * @param includeReferencedScopes
	 *            Determines if scopes referenced by the current {@link IResourceScope resource scope} must be
	 *            investigated.
	 * @return {@link Collection} of {@link Resource resource}s owned by this scope and loaded in the provided
	 *         {@link TransactionalEditingDomain editing domain}.
	 */
	Collection<Resource> getLoadedResources(TransactionalEditingDomain editingDomain, boolean includeReferencedScopes);
}
