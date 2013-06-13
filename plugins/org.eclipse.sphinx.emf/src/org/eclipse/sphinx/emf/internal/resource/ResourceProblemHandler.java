/**
 * <copyright>
 * 
 * Copyright (c) 2008-2013 See4sys, BMW Car IT, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - [374883] Improve handling of out-of-sync workspace files during descriptor initialization
 *     BMW Car IT - Avoid usage of Object.finalize
 *     itemis - [409014] Listener URIChangeDetector registered for all transactional editing domains
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.domain.factory.AbstractResourceSetListenerInstaller;
import org.eclipse.sphinx.emf.saving.SaveIndicatorUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
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
public class ResourceProblemHandler extends ResourceSetListenerImpl implements IResourceChangeListener {

	public class ResourceProblemHandlerInstaller extends AbstractResourceSetListenerInstaller<ResourceProblemHandler> {
		public ResourceProblemHandlerInstaller() {
			super(ResourceProblemHandler.class);
		}
	}

	/**
	 * Default constructor.
	 */
	public ResourceProblemHandler() {
		super(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__IS_LOADED).or(
				NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResourceSet(), ResourceSet.RESOURCE_SET__RESOURCES)));
	}

	@Override
	public void setTarget(TransactionalEditingDomain domain) {
		super.setTarget(domain);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	public void unsetTarget(TransactionalEditingDomain domain) {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

		super.unsetTarget(domain);
	}

	/*
	 * @see org.eclipse.emf.transaction.ResourceSetListenerImpl#resourceSetChanged(org.eclipse.emf.transaction.
	 * ResourceSetChangeEvent)
	 */
	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		Set<Resource> loadedResources = new HashSet<Resource>();

		// Analyze notifications for loaded resources; record loaded resources regardless of whether or not they have
		// got unloaded subsequently or not
		for (Notification notification : event.getNotifications()) {
			Object notifier = notification.getNotifier();
			if (notifier instanceof Resource) {
				Resource resource = (Resource) notifier;
				Boolean newValue = (Boolean) notification.getNewValue();
				if (newValue) {
					loadedResources.add(resource);
				}
			} else if (notifier instanceof ResourceSet) {
				Object newValue = notification.getNewValue();

				if (notification.getEventType() == Notification.ADD || notification.getEventType() == Notification.ADD_MANY) {
					List<Resource> newResources = new ArrayList<Resource>();
					if (newValue instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<Resource> newResourcesValue = (List<Resource>) newValue;
						newResources.addAll(newResourcesValue);
					} else if (newValue instanceof Resource) {
						newResources.add((Resource) newValue);
					}

					loadedResources.addAll(newResources);
				}
			}
		}

		// Handle loaded resources
		handleLoadedResources(loadedResources);
	}

	/*
	 * @see org.eclipse.emf.transaction.ResourceSetListenerImpl#isPostcommitOnly()
	 */
	@Override
	public boolean isPostcommitOnly() {
		return true;
	}

	protected void handleLoadedResources(Collection<Resource> resources) {
		ResourceProblemMarkerService.INSTANCE.updateProblemMarkers(resources, null);
	}

	/*
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				final Set<IFile> savedFiles = new HashSet<IFile>();

				// Investigate resource delta on saved files
				IResourceDeltaVisitor visitor = new ResourceDeltaVisitor(event.getType(), new DefaultResourceChangeHandler() {
					@Override
					public void handleFileChanged(int eventType, IFile file) {
						/*
						 * !! Important Note !! We must not try to obtain the model resource behind the changed file in
						 * the present execution context. This would require requesting exclusive access to underlying
						 * editing domain by creating a read transaction. However, the workspace is locked during
						 * resource change event processing. Any attempt of obtaining exclusive editing domain access
						 * while this is the case would therefore introduce a major risk of deadlocks. Some other thread
						 * might be waiting for exclusive workspace access but already have exclusive editing domain
						 * access.
						 */
						TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getMappedEditingDomain(file);
						URI uri = EcorePlatformUtil.createURI(file.getFullPath());
						if (SaveIndicatorUtil.isSaved(editingDomain, uri)) {
							savedFiles.add(file);
						}
					}
				});
				delta.accept(visitor);

				// Handle saved files
				handleSavedFiles(savedFiles);
			}
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
	}

	protected void handleSavedFiles(Collection<IFile> files) {
		Assert.isNotNull(files);

		Set<Resource> resources = new HashSet<Resource>();
		for (IFile file : files) {
			Resource resource = EcorePlatformUtil.getResource(file);
			if (resource != null) {
				resources.add(resource);
			}
		}

		handleSavedResources(resources);
	}

	protected void handleSavedResources(Collection<Resource> resources) {
		ResourceProblemMarkerService.INSTANCE.updateProblemMarkers(resources, null);
	}
}