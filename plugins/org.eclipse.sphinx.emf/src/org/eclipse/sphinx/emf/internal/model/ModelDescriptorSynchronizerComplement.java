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
 *     itemis - [409014] Listener URIChangeDetector registered for all transactional editing domains
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.domain.factory.AbstractResourceSetListenerInstaller;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.internal.metamodel.InternalMetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class ModelDescriptorSynchronizerComplement extends ResourceSetListenerImpl {

	public class ModelDescriptorSynchronizerComplementInstaller extends AbstractResourceSetListenerInstaller<ModelDescriptorSynchronizerComplement> {
		public ModelDescriptorSynchronizerComplementInstaller() {
			super(ModelDescriptorSynchronizerComplement.class);
		}
	}

	public ModelDescriptorSynchronizerComplement() {
		super(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__IS_LOADED).or(
				NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResourceSet(), ResourceSet.RESOURCE_SET__RESOURCES)));
	}

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
		handleModelResourceLoaded(loadedResources);
		handleModelResourceUnloaded(unloadedResources);
	}

	/**
	 * Handles the case where the specified set of {@link Resource resource}s has been loaded.
	 * 
	 * @param resources
	 *            The set of {@linkplain Resource resource}s that has been loaded.
	 */
	private void handleModelResourceLoaded(final Collection<Resource> resources) {
		if (!resources.isEmpty()) {
			Job job = new Job(Messages.job_addingModelDescriptors) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						SubMonitor progress = SubMonitor.convert(monitor, resources.size());
						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}

						for (Resource resource : resources) {
							// Add descriptor for model behind loaded resource to ModelDescriptorRegistry if not
							// already done so
							IFile file = EcorePlatformUtil.getFile(resource);
							if (file == null || !file.exists()) {
								ModelDescriptorRegistry.INSTANCE.addModel(resource);
							}

							progress.worked(1);
							if (progress.isCanceled()) {
								throw new OperationCanceledException();
							}
						}
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
			};

			job.setPriority(Job.SHORT);
			job.setRule(createLoadSchedulingRule(resources));
			job.setSystem(true);
			job.schedule();
		}
	}

	protected ISchedulingRule createLoadSchedulingRule(Collection<Resource> resources) {
		Assert.isNotNull(resources);

		// Collect files attached to resources
		Set<ISchedulingRule> rules = new HashSet<ISchedulingRule>();
		for (Resource resource : resources) {
			IFile file = EcorePlatformUtil.getFile(resource);
			if (file != null) {
				// Use parent resource as rule because URIConverterImpl may refresh file
				rules.add(file.getParent());
			}
		}
		return MultiRule.combine(rules.toArray(new ISchedulingRule[rules.size()]));
	}

	/**
	 * Handles the case where the specified set of {@link Resource resource}s has been unloaded.
	 * 
	 * @param resources
	 *            The set of {@linkplain Resource resource} that has been unloaded.
	 */
	private void handleModelResourceUnloaded(final Collection<Resource> resources) {
		if (!resources.isEmpty()) {
			for (Resource resource : resources) {
				// Remove underlying model file from file meta-model descriptor cache if resource exists
				// only in memory
				IFile file = EcorePlatformUtil.getFile(resource);
				if (!EcoreResourceUtil.exists(resource.getURI())) {
					InternalMetaModelDescriptorRegistry.INSTANCE.removeCachedDescriptor(file);
				}
			}

			Job job = new Job(Messages.job_removingModelDescriptors) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						SubMonitor progress = SubMonitor.convert(monitor, resources.size());
						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}

						for (Resource resource : resources) {
							// Remove descriptor for model behind unloaded only in memory resource from
							// ModelDescriptorRegistry if it is the last resource of the given model
							IFile file = EcorePlatformUtil.getFile(resource);
							if (file == null || !file.exists()) {
								ModelDescriptorRegistry.INSTANCE.removeModel(resource);
							}

							progress.worked(1);
							if (progress.isCanceled()) {
								throw new OperationCanceledException();
							}
						}
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
			};
			job.setPriority(Job.SHORT);
			job.setRule(ResourcesPlugin.getWorkspace().getRoot());
			job.setSystem(true);
			job.schedule();
		}
	}
}
