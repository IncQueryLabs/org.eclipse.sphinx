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
package org.eclipse.sphinx.emf.internal.ecore.proxymanagement.blacklist;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelper;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelperAdapterFactory;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.platform.resources.ResourceDeltaFlagsAnalyzer;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ModelIndexUpdater extends ResourceSetListenerImpl implements IResourceChangeListener {

	public ModelIndexUpdater() {
		super(NotificationFilter.ANY);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	protected void finalize() throws Throwable {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.finalize();
	}

	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		ProxyHelper proxyHelper = ProxyHelperAdapterFactory.INSTANCE.adapt(event.getEditingDomain().getResourceSet());
		List<?> notifications = event.getNotifications();
		for (Object object : notifications) {
			if (object instanceof Notification) {
				Notification notification = (Notification) object;
				Object notifier = notification.getNotifier();
				if (notifier instanceof Resource) {
					Resource resource = (Resource) notifier;
					if (notification.getFeatureID(Resource.class) == Resource.RESOURCE__IS_LOADED) {
						if (resource.isLoaded()) {
							proxyHelper.getBlackList().updateIndexOnResourceLoaded(resource);
						} else {
							// FIXME when called on post commit, resource content is empty
							proxyHelper.getBlackList().updateIndexOnResourceUnloaded(resource);
						}
					}
				} else if (notifier instanceof EObject) {
					// TODO When adding an EObject, note that update the index on the whole resource isn't so good. Use
					// ExtendedResource API to calculate metamodel-specific URIs
					if (notification.getNewValue() instanceof EObject && notification.getEventType() == Notification.ADD) {
						// Below is the improved code, but usage of ExtendedResource API needs to be incorporated since
						// EcoreUtil.getURI() does not yield appropriate results for metamodels using fragment-based
						// URIs
						// URI uri = EcoreUtil.getURI((EObject) notification.getNewValue());
						// if (ModelIndexManager.INSTANCE.existsProxyURI(uri)) {
						// ModelIndexManager.INSTANCE.removeProxyURI(uri);
						// }
						proxyHelper.getBlackList().updateIndexOnResourceLoaded(((EObject) notifier).eResource());
					}
					// When renaming, since URIs may depend on String attribute values
					else if (notification.getNewValue() instanceof String && notification.getEventType() == Notification.SET) {
						// TODO Get rid of updating index on the whole resource by calculating the URI of the notifier
						// using ExtendedResource API and then look in the index and update entry if there is any
						proxyHelper.getBlackList().updateIndexOnResourceLoaded(((EObject) notifier).eResource());
					}
				}
			}
		}
	}

	private void handleProjectDescriptionChanged(final IProject project) {
		/*
		 * !! Important Note !! Handle project description change in an asynchronous operation with exclusive access to
		 * the affected project for the following two reasons: 1/ In order to avoid deadlocks. The workspace is locked
		 * while IResourceChangeListeners are processed (exclusive workspace access) and updating the model descriptor
		 * registry may involve creating transactions (exclusive model access). In cases where another thread is around
		 * while we are called here which already has exclusive model access but waits for exclusive workspace access we
		 * would end up in a deadlock otherwise. 2/ In order to make sure that the model descriptor registry gets
		 * updated only AFTER all other IResourceChangeListeners have been processed which may be present and rely on
		 * the model descriptor registry's state BEFORE the update.
		 */
		if (project != null) {
			// TODO externalize job label string
			Job job = new Job("Update model index") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					HashSet<Resource> updatedResources = new HashSet<Resource>();

					try {
						IProjectDescription description = project.getDescription();
						if (description != null) {
							IProject[] referencedProjects = description.getReferencedProjects();
							for (IProject refrencedProject : referencedProjects) {
								Collection<IModelDescriptor> projectModels = ModelDescriptorRegistry.INSTANCE.getModels(refrencedProject);
								for (IModelDescriptor modelDescriptor : projectModels) {
									updatedResources.addAll(modelDescriptor.getLoadedResources(true));
								}
							}

						}
						for (Resource resource : updatedResources) {
							if (resource.isLoaded()) {
								ProxyHelper proxyHelper = ProxyHelperAdapterFactory.INSTANCE.adapt(resource.getResourceSet());
								proxyHelper.getBlackList().updateIndexOnResourceLoaded(resource);
							}
						}
					} catch (CoreException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					}
					return Status.OK_STATUS;
				}
			};
			job.setRule(project);
			job.setSystem(true);
			job.schedule();
		}

	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						try {
							IResource resource = delta.getResource();
							ResourceDeltaFlagsAnalyzer flags = new ResourceDeltaFlagsAnalyzer(delta);
							switch (delta.getKind()) {
							case IResourceDelta.ADDED:
								// do nothing
								break;
							case IResourceDelta.CHANGED:
								if (resource instanceof IProject) {
									IProject project = (IProject) resource;

									// Has a projet's description been changed (referenced projects, linked resources,
									// nature, etc.)?
									if (flags.DESCRIPTION && project.isOpen()) {
										for (IResourceDelta childDelta : delta.getAffectedChildren(IResourceDelta.CHANGED)) {
											ResourceDeltaFlagsAnalyzer childFlag = new ResourceDeltaFlagsAnalyzer(childDelta);
											if (childFlag.CONTENT && ExtendedPlatform.isProjectDescriptionFile(childDelta.getResource())) {
												handleProjectDescriptionChanged(project);
											}
										}
									}
								}
								break;
							case IResourceDelta.REMOVED:
								// do nothing
								break;
							default:
								break;
							}
						} catch (Exception ex) {
							PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
						}
						return true;
					}
				};
				delta.accept(visitor);
			}
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
	}
}
