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
package org.eclipse.sphinx.emf.internal.metamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.resources.DefaultResourceChangeHandler;
import org.eclipse.sphinx.platform.resources.ResourceDeltaVisitor;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;

/**
 * Updater responsible for keeping {@linkplain ModelDescriptorRegistry model descriptor registry} up-to-date. The three
 * basic operations that need to be performed are:
 * <ul>
 * <li>when a resource is loaded: create/retrieve the corresponding model descriptor and add the loaded file to it;</li>
 * <li>when a resource is unloaded: retrieve the corresponding model descriptor, remove the loaded file from it, and
 * remove it if empty;</li>
 * <li>when a project description has changed: update corresponding model descriptors.</li>
 * </ul>
 * <p>
 * This updater is a {@linkplain ResourceSetListener resource set listener} that is contributed to the platform through
 * <tt>org.eclipse.emf.transaction.listeners</tt> extension point. In addition, it is declared as a
 * {@linkplain IResourceChangeListener resource change listener} on the workspace (in order to detect
 * {@linkplain IProjectDescription project description} changes).
 * </p>
 */
public class MetaModelDescriptorCacheUpdater extends ResourceSetListenerImpl implements IResourceChangeListener {

	/**
	 * Default constructor.
	 */
	public MetaModelDescriptorCacheUpdater() {
		super(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__IS_LOADED).or(
				NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResourceSet(), ResourceSet.RESOURCE_SET__RESOURCES)));
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/*
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.finalize();
	}

	/*
	 * @see org.eclipse.emf.transaction.ResourceSetListenerImpl#resourceSetChanged(ResourceSetChangeEvent)
	 */
	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		Set<Resource> loadedResources = new HashSet<Resource>();
		Set<Resource> unloadedResources = new HashSet<Resource>();
		Set<Resource> addedResources = new HashSet<Resource>();
		Set<Resource> removedResources = new HashSet<Resource>();

		// Analyze notifications for loaded and unloaded resources; record only resources which have not got
		// unloaded/loaded again later on
		for (Notification notification : event.getNotifications()) {
			Object notifier = notification.getNotifier();
			if (notifier instanceof Resource) {
				Resource resource = (Resource) notifier;
				Boolean newValue = (Boolean) notification.getNewValue();
				if (newValue) {
					if (unloadedResources.contains(resource)) {
						unloadedResources.remove(resource);
					} else {
						loadedResources.add(resource);
					}
				} else {
					if (loadedResources.contains(resource)) {
						loadedResources.remove(resource);
					} else {
						unloadedResources.add(resource);
					}
				}
			} else if (notifier instanceof ResourceSet) {
				if (notification.getEventType() == Notification.ADD || notification.getEventType() == Notification.ADD_MANY) {
					List<Resource> newResources = new ArrayList<Resource>();
					Object newValue = notification.getNewValue();
					if (newValue instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<Resource> newResourcesValue = (List<Resource>) newValue;
						newResources.addAll(newResourcesValue);
					} else if (newValue instanceof Resource) {
						newResources.add((Resource) newValue);
					}

					for (Resource newResource : newResources) {
						if (removedResources.contains(newResource)) {
							removedResources.remove(newResource);
						} else {
							addedResources.add(newResource);
						}
					}
				} else if (notification.getEventType() == Notification.REMOVE || notification.getEventType() == Notification.REMOVE_MANY) {
					List<Resource> oldResources = new ArrayList<Resource>();
					Object oldValue = notification.getOldValue();
					if (oldValue instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<Resource> oldResourcesValue = (List<Resource>) oldValue;
						oldResources.addAll(oldResourcesValue);
					} else if (oldValue instanceof Resource) {
						oldResources.add((Resource) oldValue);
					}

					for (Resource oldResource : oldResources) {
						if (addedResources.contains(oldResource)) {
							addedResources.remove(oldResource);
						} else {
							removedResources.add(oldResource);
						}
					}
				}
			}
		}
		loadedResources.addAll(addedResources);
		unloadedResources.addAll(removedResources);

		// Handle loaded and unloaded resources
		handleLoadedResources(loadedResources);
		handleUnloadedResources(unloadedResources);
	}

	/**
	 * Handles the case where the specified set of {@link Resource resource}s has been loaded.
	 * 
	 * @param resources
	 *            The set of {@linkplain Resource resource}s that has been loaded.
	 */
	private void handleLoadedResources(Set<Resource> resources) {
		Assert.isNotNull(resources);

		for (Resource resource : resources) {
			IFile file = EcorePlatformUtil.getFile(resource);
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resource);
			InternalMetaModelDescriptorRegistry.INSTANCE.addCachedDescriptor(file, mmDescriptor);
		}
	}

	/**
	 * Handles the case where the specified set of {@link Resource resource}s has been unloaded.
	 * 
	 * @param resources
	 *            The set of {@linkplain Resource resource} that has been unloaded.
	 */
	private void handleUnloadedResources(Set<Resource> resources) {
		Assert.isNotNull(resources);

		for (Resource resource : resources) {
			// Remove underlying model file from file meta-model descriptor cache if resource exists
			// only in memory
			IFile file = EcorePlatformUtil.getFile(resource);
			if (!EcoreResourceUtil.exists(resource.getURI())) {
				InternalMetaModelDescriptorRegistry.INSTANCE.removeCachedDescriptor(file);
			}
		}

		// Clear old meta-model descriptors
		MetaModelDescriptorCacheUpdater.this.clearCachedOldDescriptors();
	}

	/*
	 * @see org.eclipse.emf.transaction.ResourceSetListenerImpl#isPostcommitOnly()
	 */
	@Override
	public boolean isPostcommitOnly() {
		return true;
	}

	/*
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				IResourceDeltaVisitor visitor = new ResourceDeltaVisitor(event.getType(), new DefaultResourceChangeHandler() {

					@Override
					public void handleFileChanged(int eventType, IFile file) {
						// Remove entry for changed file from meta-model descriptor cache
						InternalMetaModelDescriptorRegistry.INSTANCE.removeCachedDescriptor(file);

						// Clear old meta-model descriptors
						MetaModelDescriptorCacheUpdater.this.clearCachedOldDescriptors();
					}

					@Override
					public void handleFileMoved(int eventType, IFile oldFile, IFile newFile) {
						// Remove entry for old file from meta-model descriptor cache and add an equivalent entry
						// for new file
						InternalMetaModelDescriptorRegistry.INSTANCE.moveCachedDescriptor(oldFile, newFile);

						// Clear old meta-model descriptors
						MetaModelDescriptorCacheUpdater.this.clearCachedOldDescriptors();
					}

					@Override
					public void handleFileRemoved(int eventType, IFile file) {
						// Remove entry for removed file from meta-model descriptor cache
						InternalMetaModelDescriptorRegistry.INSTANCE.removeCachedDescriptor(file);

						// Clear old meta-model descriptors
						MetaModelDescriptorCacheUpdater.this.clearCachedOldDescriptors();
					}
				});
				delta.accept(visitor);
			}
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
	}

	private void clearCachedOldDescriptors() {
		/*
		 * !! Important Note !! Perform as asynchronous operation belonging to model loading family, assign (lowest
		 * possible) Job.DECORATE priority and schedule on workspace root to make sure that cached old meta-model
		 * descriptors get forgotten once all model loading jobs are finished but remain available as long as other
		 * model loading jobs are still running.
		 */
		Job job = new Job(Messages.job_clearingOldMetaModelDescriptors) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					SubMonitor progress = SubMonitor.convert(monitor, 1);

					// Clear old meta-model descriptors
					InternalMetaModelDescriptorRegistry.INSTANCE.clearCachedOldDescriptors();
					progress.worked(1);

					return Status.OK_STATUS;
				} catch (OperationCanceledException ex) {
					return Status.CANCEL_STATUS;
				} catch (Exception ex) {
					return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
				}
			}

			@Override
			public boolean belongsTo(Object family) {
				return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family);
			}

			@Override
			public boolean shouldSchedule() {
				Job[] jobs = Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING);
				for (Job modelLoadingJob : jobs) {
					if (modelLoadingJob.getName().equals(Messages.job_clearingOldMetaModelDescriptors)) {
						return false;
					}
				}
				return true;
			}
		};
		job.setPriority(Job.DECORATE);
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setSystem(true);
		job.schedule();
	}
}
