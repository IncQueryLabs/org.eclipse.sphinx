/**
 * <copyright>
 * 
 * Copyright (c) 2008-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [410825] Make sure that EcorePlatformUtil#getResourcesInModel(contextResource, includeReferencedModels) method return resources of the context resource in the same resource set
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;
import org.eclipse.sphinx.emf.scoping.IResourceScope;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * A default implementation of model descriptor.
 * 
 * @see IModelDescriptor
 */
public class ModelDescriptor extends PlatformObject implements IModelDescriptor {

	/**
	 * The editing domain of the described model.
	 */
	protected TransactionalEditingDomain fEditingDomain;

	/**
	 * The meta-model descriptor.
	 */
	protected IMetaModelDescriptor fMMDescriptor;

	/**
	 * The {@link IResourceScope resource scope} of the model described here.
	 */
	protected IResourceScope fResourceScope;

	/**
	 * Constructor.
	 * 
	 * @param mmDescriptor
	 *            The meta-model descriptor.
	 * @param editingDomain
	 *            The editing domain of the described model.
	 * @param rootProject
	 *            The root project of the described model.
	 */
	public ModelDescriptor(IMetaModelDescriptor mmDescriptor, TransactionalEditingDomain editingDomain, IResourceScope resourceScope) {
		Assert.isNotNull(mmDescriptor);
		Assert.isNotNull(editingDomain);
		Assert.isNotNull(resourceScope);
		fMMDescriptor = mmDescriptor;
		fEditingDomain = editingDomain;
		fResourceScope = resourceScope;
	}

	/*
	 * @see org.eclipse.sphinx.emf.internal.model.IDescriptor#getMetaModelDescriptor()
	 */
	public IMetaModelDescriptor getMetaModelDescriptor() {
		return fMMDescriptor;
	}

	/*
	 * @see org.eclipse.sphinx.emf.internal.model.IDescriptor#getEditingDomain()
	 */
	public TransactionalEditingDomain getEditingDomain() {
		return fEditingDomain;
	}

	/*
	 * @see org.eclipse.sphinx.emf.platform.model.IModelDescriptor#getProject()
	 */
	public IResource getRoot() {
		return fResourceScope.getRoot();
	}

	/*
	 * @see org.eclipse.sphinx.emf.model.IModelDescriptor#getProjects()
	 */
	public Collection<IResource> getReferencedRoots() {
		return fResourceScope.getReferencedRoots();
	}

	public IResourceScope getScope() {
		return fResourceScope;
	}

	/*
	 * @see org.eclipse.sphinx.emf.platform.model.IModelIdentifier#belongsTo(org.eclipse.core.resources.IFile)
	 */
	public boolean belongsTo(IFile file, boolean includeReferencedScopes) {
		if (file != null) {
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(file);
			if (fMMDescriptor.equals(mmDescriptor)) {
				return fResourceScope.belongsTo(file, includeReferencedScopes);
			}
		}

		return false;
	}

	public boolean belongsTo(URI uri, boolean includeReferencedScopes) {
		if (uri != null) {
			Resource resource = EcorePlatformUtil.getResource(uri);
			if (resource != null) {
				return belongsTo(resource, includeReferencedScopes);
			} else {
				IFile file = EcorePlatformUtil.getFile(uri);
				return belongsTo(file, includeReferencedScopes);
			}
		}
		return false;
	}

	public boolean belongsTo(final Resource resource, boolean includeReferencedScopes) {
		if (resource != null) {
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resource);
			if (fMMDescriptor.equals(mmDescriptor)) {
				if (fResourceScope.belongsTo(resource, includeReferencedScopes)) {
					// Make sure that given resource is actually contained in the resource set of this model
					// descriptor's editing domain
					/*
					 * !! Important Note !! The provided resource could be a resource in a resource set that is not
					 * managed by Sphinx (but privately owned by a traditional EMF model editor instead). In this case
					 * we must make sure that the resource is NOT deemed a part of the Sphinx model represented by this
					 * model descriptor. Not doing so would let Sphinx assume that the resource is present in the
					 * resource set of this model descriptor's editing domain and most likely cause pretty unpredictable
					 * side effects.
					 */
					try {
						return TransactionUtil.runExclusive(fEditingDomain, new RunnableWithResult.Impl<Boolean>() {
							public void run() {
								setResult(fEditingDomain.getResourceSet().getResources().contains(resource));
							}
						});
					} catch (InterruptedException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					}
				}
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.model.IModelDescriptor#didBelongTo(org.eclipse.core.resources.IFile)
	 */
	public boolean didBelongTo(IFile file, boolean includeReferencedScopes) {
		if (file != null) {
			IMetaModelDescriptor oldMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getOldDescriptor(file);
			if (fMMDescriptor.equals(oldMMDescriptor)) {
				return fResourceScope.didBelongTo(file, includeReferencedScopes);
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.model.IModelDescriptor#didBelongTo(org.eclipse.core.resources.IFile)
	 */
	public boolean didBelongTo(URI uri, boolean includeReferencedScopes) {
		if (uri != null) {
			Resource resource = EcorePlatformUtil.getResource(uri);
			if (resource != null) {
				return didBelongTo(resource, includeReferencedScopes);
			} else {
				IFile file = EcorePlatformUtil.getFile(uri);
				return didBelongTo(file, includeReferencedScopes);
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.model.IModelDescriptor#didBelongTo(org.eclipse.emf.ecore.resource.Resource)
	 */
	public boolean didBelongTo(Resource resource, boolean includeReferencedScopes) {
		if (resource != null) {
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getOldDescriptor(resource);
			if (fMMDescriptor.equals(mmDescriptor)) {
				return fResourceScope.didBelongTo(resource, includeReferencedScopes);
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.sphinx.emf.model.IModelDescriptor#getLoadedResources()
	 */
	public Collection<Resource> getLoadedResources(final boolean includeReferencedScopes) {
		try {
			return TransactionUtil.runExclusive(fEditingDomain, new RunnableWithResult.Impl<List<Resource>>() {
				public void run() {
					ResourceSet resourceSet = fEditingDomain.getResourceSet();
					if (resourceSet instanceof ScopingResourceSet) {
						setResult(((ScopingResourceSet) resourceSet).getResourcesInModel(ModelDescriptor.this, includeReferencedScopes));
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof ModelDescriptor) {
			ModelDescriptor otherModelDescriptor = (ModelDescriptor) object;
			return fMMDescriptor.equals(otherModelDescriptor.fMMDescriptor) && fEditingDomain.equals(otherModelDescriptor.fEditingDomain)
					&& fResourceScope.equals(otherModelDescriptor.getScope());
		}
		return false;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return fMMDescriptor.hashCode() + fEditingDomain.hashCode() + fResourceScope.getRoot().hashCode();
	}

	@Override
	public String toString() {
		return fMMDescriptor + "@" + fResourceScope.getRoot().getName(); //$NON-NLS-1$
	}

	public Collection<IFile> getPersistedFiles(boolean includeReferencedScopes) {
		Collection<IFile> persistedFiles = new HashSet<IFile>();
		for (IFile file : fResourceScope.getPersistedFiles(includeReferencedScopes)) {
			IMetaModelDescriptor descriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(file);
			if (fMMDescriptor.equals(descriptor)) {
				persistedFiles.add(file);
			}
		}

		return Collections.unmodifiableCollection(persistedFiles);
	}

	public Collection<IResource> getReferencingRoots() {

		return fResourceScope.getReferencingRoots();
	}

}
