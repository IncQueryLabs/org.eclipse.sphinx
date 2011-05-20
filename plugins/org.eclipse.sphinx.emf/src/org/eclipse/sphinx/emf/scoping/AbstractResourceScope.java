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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractResourceScope implements IResourceScope {

	/*
	 * @see org.eclipse.sphinx.emf.scoping.IResourceScope#exists()
	 */
	public boolean exists() {
		return getRoot() != null && getRoot().isAccessible();
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.model.IResourceScope#getLoadedResources(org.eclipse.emf.transaction.TransactionalEditingDomain
	 * , boolean)
	 */
	public Collection<Resource> getLoadedResources(final TransactionalEditingDomain editingDomain, final boolean includeReferencedScopes) {
		try {
			return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<List<Resource>>() {
				public void run() {
					ResourceSet resourceSet = editingDomain.getResourceSet();
					if (resourceSet instanceof ScopingResourceSet) {
						setResult(((ScopingResourceSet) resourceSet).getResourcesInScope(AbstractResourceScope.this, includeReferencedScopes));
					} else {
						setResult(resourceSet.getResources());
					}
				}
			});
		} catch (InterruptedException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return Collections.emptyList();
	}

	/*
	 * @see org.eclipse.sphinx.emf.model.IResourceScope#getPersistedFiles(boolean)
	 */
	// TODO Add filtering process to only return files matching the scope
	// see org.eclipse.sphinx.platform.util.ExtendedPlatform.collectAllFiles(IProject, IResource[], List<IFile>,
	// Collection<IProject>, boolean) for details
	public Collection<IFile> getPersistedFiles(boolean includeReferencedScopes) {
		HashSet<IFile> files = new HashSet<IFile>();
		files.addAll(getFilesUnderRoot(getRoot()));
		if (includeReferencedScopes) {
			Collection<IResource> dependingRoots = getReferencedRoots();
			for (IResource dependingRoot : dependingRoots) {
				files.addAll(getFilesUnderRoot(dependingRoot));
			}
		}
		return Collections.unmodifiableCollection(files);
	}

	protected Collection<IFile> getFilesUnderRoot(IResource root) {
		HashSet<IFile> files = new HashSet<IFile>();
		if (root instanceof IProject) {
			IProject project = (IProject) root;
			files.addAll(ExtendedPlatform.getAllFiles(project, false));
		} else if (root instanceof IFile) {
			files.add((IFile) root);
		} else if (root instanceof IFolder) {
			IFolder folder = (IFolder) root;
			files.addAll(ExtendedPlatform.getAllFiles(folder));
		}
		return files;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof IResourceScope) {
			return ((IResourceScope) object).getRoot().equals(getRoot()) && object.getClass().equals(this.getClass());
		}
		return false;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getRoot().hashCode() + this.getClass().hashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + "[" + getRoot().getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ 
	}
}
