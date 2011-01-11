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
package org.eclipse.sphinx.emf.workspace.internal.saving;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.ResourceUndoContext;
import org.eclipse.sphinx.emf.saving.IResourceSaveIndicator;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;

/**
 * Default implementation of {@linkplain IResourceSaveIndicator} that keeps up-to-date two lists of
 * {@linkplain Resource resource}s:
 * <ul>
 * <li><em>saved resources</em>&nbsp;&#150;&nbsp;containing resources that are <b>not dirty</b>;</li>
 * <li><em>resources to save</em>&nbsp;&#150;&nbsp;containing resources that are <b>dirty</b>.</li>
 * </ul>
 * <p>
 * This implementation also encapsulate a {@linkplain ResourceSetListener} whose behavior can be assimilated to an
 * <em>object changed listener</em> since &#150; when it is notified (synchronously, at the end of a transaction) &#150;
 * it asks affected resources (<em>i.e.</em> resources containing model modifications) to be marked as dirty. Once
 * created, that listener is added as a resource set listener on the {@linkplain TransactionalEditingDomain editing
 * domain} this {@linkplain IResourceSaveIndicator indicator} is associated to.
 */
public class ResourceSaveIndicator implements IResourceSaveIndicator {

	/**
	 * The {@linkplain TransactionalEditingDomain editing domain} this {@linkplain IResourceSaveIndicator indicator} is
	 * associated to.
	 */
	private TransactionalEditingDomain editingDomain;

	/**
	 * The list of {@linkplain Resource resource}s where the content of the physical workspace {@linkplain IResource
	 * resource} is not the same as that of the EMF {@linkplain Resource resource} behind; <em>i.e.</em> this list
	 * contains resources that are <b>dirty</b>.
	 */
	private Set<Resource> dirtyResources = Collections.synchronizedSet(new HashSet<Resource>());

	/**
	 * The list of {@linkplain URI}s which were just saved and where the content of the physical workspace
	 * {@linkplain IResource resource} became again the same as that of the EMF {@linkplain Resource resource} behind;
	 * <em>i.e.</em> this list contains URIs of resources that are <b>no longer dirty</b> but were so just before.
	 */
	private Set<URI> savedURIs = Collections.synchronizedSet(new HashSet<URI>());

	/**
	 * The {@linkplain ResourceSetListener listener} that is notified when a model is modified. When notified, this
	 * listener retrieves the resources affected by the modification and then asks to mark them as dirty.
	 */
	protected ResourceSetListener objectChangedListener;

	/**
	 * Constructor.
	 * <p>
	 * Asks for the creation and the add of a {@linkplain ResourceSetListener listener} on the specified
	 * {@linkplain TransactionalEditingDomain editing domain}. This listener is a {@linkplain ResourceSetListener} and
	 * can be seen as an <em>object changed listener</em> since it will be notified each time a model is modified.
	 * 
	 * @param editingDomain
	 *            The {@linkplain TransactionalEditingDomain editing domain} this {@linkplain IResourceSaveIndicator
	 *            indicator} must be associated to.
	 */
	public ResourceSaveIndicator(TransactionalEditingDomain editingDomain) {
		this.editingDomain = editingDomain;
		addTransactionalEditingDomainListeners();
	}

	/*
	 * @see org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate#dispose()
	 */
	public void dispose() {
		removeTransactionalEditingDomainListeners();
	}

	/**
	 * Adds the <em>object changed {@linkplain ResourceSetListener listener}</em> on the
	 * {@linkplain TransactionalEditingDomain editing domain} this {@linkplain IResourceSaveIndicator indicator} is
	 * associated to.
	 */
	protected void addTransactionalEditingDomainListeners() {
		// Create and register ResourceSetChangedListener that detects changed objects
		objectChangedListener = createObjectChangedListener();
		Assert.isNotNull(objectChangedListener);
		if (editingDomain != null) {
			editingDomain.addResourceSetListener(objectChangedListener);
		}
	}

	/**
	 * Removes the <em>object changed {@linkplain ResourceSetListener listener}</em> from the
	 * {@linkplain TransactionalEditingDomain editing domain} this {@linkplain IResourceSaveIndicator indicator} is
	 * associated to.
	 */
	protected void removeTransactionalEditingDomainListeners() {
		if (editingDomain != null && objectChangedListener != null) {
			editingDomain.removeResourceSetListener(objectChangedListener);
		}
	}

	/**
	 * Creates the {@linkplain ResourceSetListener listener} that must be used as <em>object changed listener</em> in
	 * order to ask affected resources to be marked as dirty.
	 * <p>
	 * This listener is a {@linkplain ResourceSetListener} whose notification filters are:
	 * <ul>
	 * <li>the notifier of the event must not be a {@linkplain Resource};</li>
	 * <li>the type of the event must not be {@linkplain Notification#RESOLVE};</li>
	 * </ul>
	 * 
	 * @return The created <em>object changed {@linkplain ResourceSetListener listener}</em>.
	 */
	protected ResourceSetListener createObjectChangedListener() {
		return new ResourceSetListenerImpl(NotificationFilter.createNotifierTypeFilter(Resource.class).negated()
				.and(NotificationFilter.createEventTypeFilter(Notification.RESOLVE).negated())) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				makeDirty(ResourceUndoContext.getAffectedResources(event.getNotifications()));
			}
		};
	}

	/**
	 * Makes given {@linkplain Resource resource}s dirty and then asks {@linkplain ModelSaveManager} to notify
	 * interested listeners that resources are now dirty.
	 * 
	 * @param resources
	 *            The resources to mark as dirty.
	 */
	protected void makeDirty(Set<Resource> resources) {
		for (Resource resource : resources) {
			// Mark resource as to be saved
			setDirty(resource);

			// FIXME Find how the reduce the number of notifications
			// Notify listeners that resource is now dirty
			ModelSaveManager.INSTANCE.notifyDirtyChanged(resource);
		}
	}

	/*
	 * @see
	 * org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate#handleResourceChanged(org.eclipse.emf.ecore.resource
	 * .Resource)
	 */
	public boolean handleResourceChanged(Resource resource) {
		unsetSaved(resource);
		return true;
	}

	/*
	 * @see
	 * org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate#handleResourceDeleted(org.eclipse.emf.ecore.resource
	 * .Resource)
	 */
	public boolean handleResourceDeleted(Resource resource) {
		return true;
	}

	/*
	 * @see
	 * org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate#handleResourceMoved(org.eclipse.emf.ecore.resource
	 * .Resource, org.eclipse.emf.common.util.URI)
	 */
	public boolean handleResourceMoved(Resource resource, URI newURI) {
		return true;
	}

	/*
	 * @see org.eclipse.sphinx.emf.saving.IResourceSaveIndicator#isDirty(org.eclipse.emf.ecore.resource.Resource)
	 */
	public boolean isDirty(Resource resource) {
		return dirtyResources.contains(resource);
	}

	/*
	 * @see org.eclipse.sphinx.emf.saving.IResourceSaveIndicator#setDirty(org.eclipse.emf.ecore.resource.Resource)
	 */
	public void setDirty(Resource resource) {
		if (resource != null && EcoreResourceUtil.exists(resource.getURI())) {
			dirtyResources.add(resource);
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.saving.IResourceSaveIndicator#getDirtyResources()
	 */
	public Collection<Resource> getDirtyResources() {
		return Collections.unmodifiableSet(new HashSet<Resource>(dirtyResources));
	}

	/*
	 * @see org.eclipse.sphinx.emf.saving.IResourceSaveIndicator#isSaved(org.eclipse.emf.common.util.URI)
	 */
	public boolean isSaved(URI uri) {
		return savedURIs.contains(uri);
	}

	/*
	 * @see org.eclipse.sphinx.emf.saving.IResourceSaveIndicator#setSaved(org.eclipse.emf.ecore.resource.Resource)
	 */
	public void setSaved(Resource resource) {
		if (resource != null) {
			setSaved(Collections.singletonList(resource));
		}
	}

	/*
	 * @see org.eclipse.sphinx.emf.saving.IResourceSaveIndicator#setSaved(java.util.Collection)
	 */
	public void setSaved(Collection<Resource> resources) {
		if (resources != null) {
			synchronized (savedURIs) {
				for (Resource resource : resources) {
					savedURIs.add(resource.getURI());
					dirtyResources.remove(resource);
				}
			}
		}
	}

	private void unsetSaved(Resource resource) {
		savedURIs.remove(resource.getURI());
	}
}
