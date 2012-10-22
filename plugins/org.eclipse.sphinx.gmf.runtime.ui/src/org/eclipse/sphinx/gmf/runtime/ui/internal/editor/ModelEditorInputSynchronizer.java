/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.gmf.runtime.ui.internal.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.ResourceUndoContext;
import org.eclipse.ui.IEditorInput;

public class ModelEditorInputSynchronizer implements IDisposable {

	private ResourceSetListener resourceLoadedListener;
	private ResourceSetListener resourceMovedListener;
	private ResourceSetListener resourceRemovedListener;
	private IOperationHistoryListener objectChangedListener;
	private ResourceSetListener objectRemovedListener;

	protected IEditorInput editorInput;
	protected TransactionalEditingDomain editingDomain;
	protected IModelEditorInputChangeAnalyzer editorInputChangeAnalyzer;
	protected IModelEditorInputChangeHandler editorInputChangeHandler;

	public ModelEditorInputSynchronizer(IEditorInput editorInput, TransactionalEditingDomain editingDomain,
			IModelEditorInputChangeAnalyzer editorInputChangeAnalyzer, IModelEditorInputChangeHandler editorInputChangeHandler) {
		Assert.isNotNull(editorInput);
		Assert.isNotNull(editingDomain);
		Assert.isNotNull(editorInputChangeAnalyzer);
		Assert.isNotNull(editorInputChangeHandler);

		this.editorInput = editorInput;
		this.editingDomain = editingDomain;
		this.editorInputChangeAnalyzer = editorInputChangeAnalyzer;
		this.editorInputChangeHandler = editorInputChangeHandler;

		installModelChangeListeners();
	}

	protected void installModelChangeListeners() {
		// Create and register listener that detects loaded resources
		resourceLoadedListener = createResourceLoadedListener();
		Assert.isNotNull(resourceLoadedListener);
		editingDomain.addResourceSetListener(resourceLoadedListener);

		// Create and register listener that detects renamed or moved resources
		resourceMovedListener = createResourceMovedListener();
		Assert.isNotNull(resourceMovedListener);
		editingDomain.addResourceSetListener(resourceMovedListener);

		// Create and register listener that detects removed resources
		resourceRemovedListener = createResourceRemovedListener();
		Assert.isNotNull(resourceRemovedListener);
		editingDomain.addResourceSetListener(resourceRemovedListener);

		// Create and register listener that detects changed objects
		objectChangedListener = createObjectChangedListener();
		Assert.isNotNull(objectChangedListener);
		getOperationHistory().addOperationHistoryListener(objectChangedListener);

		// Create and register listener that detects removed objects
		objectRemovedListener = createObjectRemovedListener();
		Assert.isNotNull(objectRemovedListener);
		editingDomain.addResourceSetListener(objectRemovedListener);
	}

	protected void uninstallModelChangeListeners() {
		editingDomain.removeResourceSetListener(resourceLoadedListener);
		editingDomain.removeResourceSetListener(resourceMovedListener);
		editingDomain.removeResourceSetListener(resourceRemovedListener);
		getOperationHistory().removeOperationHistoryListener(objectChangedListener);
		editingDomain.removeResourceSetListener(objectRemovedListener);
	}

	protected ResourceSetListener createResourceLoadedListener() {
		return new ResourceSetListenerImpl(
				NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__IS_LOADED)) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				// Retrieve loaded resources from notification
				List<?> notifications = event.getNotifications();
				for (Object object : notifications) {
					if (object instanceof Notification) {
						Notification notification = (Notification) object;
						if (notification.getNewBooleanValue()) {
							Resource loadedResource = (Resource) notification.getNotifier();
							// Has loaded resource not been unloaded again subsequently?
							if (loadedResource.isLoaded()) {
								// Is loaded resource the resource containing editor input object?
								if (editorInputChangeAnalyzer.containEditorInputResourceURI(editorInput,
										Collections.singleton(loadedResource.getURI()))) {
									// Handle (re-)loaded editor input resource
									editorInputChangeHandler.handleEditorInputResourceLoaded(editorInput);
									break;
								}
							}
						}
					}
				}
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected ResourceSetListener createResourceMovedListener() {
		return new ResourceSetListenerImpl(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__URI)) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				// Retrieve moved resources from notification
				List<?> notifications = event.getNotifications();
				for (Object object : notifications) {
					if (object instanceof Notification) {
						Notification notification = (Notification) object;
						// Is moved resource the resource containing editor input object?
						if (notification.getOldValue() instanceof URI) {
							URI oldResourceURI = (URI) notification.getOldValue();
							if (oldResourceURI != null) {
								if (editorInputChangeAnalyzer.containEditorInputResourceURI(editorInput, Collections.singleton(oldResourceURI))) {
									// Handle moved editor input resource
									editorInputChangeHandler.handleEditorInputResourceMoved(editorInput, oldResourceURI,
											(URI) notification.getNewValue());
									break;
								}
							}
						}
					}
				}
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected ResourceSetListener createResourceRemovedListener() {
		return new ResourceSetListenerImpl(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResourceSet(),
				ResourceSet.RESOURCE_SET__RESOURCES).and(
				NotificationFilter.createEventTypeFilter(Notification.REMOVE).or(NotificationFilter.createEventTypeFilter(Notification.REMOVE_MANY)))) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				// Retrieve removed resources from notification
				Set<Resource> removedResources = new HashSet<Resource>();
				List<?> notifications = event.getNotifications();
				for (Object object : notifications) {
					if (object instanceof Notification) {
						Notification notification = (Notification) object;
						Object oldValue = notification.getOldValue();
						if (oldValue instanceof Resource) {
							Resource oldResource = (Resource) oldValue;
							// Has old resource not been added back subsequently?
							if (oldResource.getResourceSet() == null) {
								removedResources.add(oldResource);
							}
						}
						if (oldValue instanceof List<?>) {
							@SuppressWarnings("unchecked")
							List<Resource> oldResources = (List<Resource>) oldValue;
							for (Resource oldResource : oldResources) {
								// Has old resource not been added back subsequently?
								if (oldResource.getResourceSet() == null) {
									removedResources.add(oldResource);
								}
							}
						}
					}
				}

				// Include removed resources the resource containing editor input object?
				Set<URI> removedResourceURIs = new HashSet<URI>(removedResources.size());
				for (Resource removedResource : removedResources) {
					removedResourceURIs.add(removedResource.getURI());
				}
				if (editorInputChangeAnalyzer.containEditorInputResourceURI(editorInput, removedResourceURIs)) {
					// Handle removed editor input resource
					editorInputChangeHandler.handleEditorInputResourceRemoved(editorInput);
				}
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected ResourceSetListener createObjectRemovedListener() {
		return new ResourceSetListenerImpl(NotificationFilter.createEventTypeFilter(Notification.REMOVE).or(
				NotificationFilter.createEventTypeFilter(Notification.REMOVE_MANY))) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				// Retrieve removed objects from notification
				Set<EObject> removedObjects = new HashSet<EObject>();
				List<?> notifications = event.getNotifications();
				for (Object object : notifications) {
					if (object instanceof Notification) {
						Notification notification = (Notification) object;
						Object oldValue = notification.getOldValue();
						if (oldValue instanceof EObject) {
							EObject oldObject = (EObject) oldValue;
							// Has old object not been added back subsequently?
							if (oldObject.eResource() == null) {
								removedObjects.add(oldObject);
							}
						}
						if (oldValue instanceof List<?>) {
							for (Object oldValueItem : (List<?>) oldValue) {
								if (oldValueItem instanceof EObject) {
									EObject oldObject = (EObject) oldValueItem;
									// Has old object not been added back subsequently?
									if (oldObject.eResource() == null) {
										removedObjects.add(oldObject);
									}
								}
							}
						}
					}
				}

				// Was or did removed object contain the editor input object?
				if (editorInputChangeAnalyzer.containEditorInputObject(editorInput, removedObjects)) {
					// Handle removed editor input object
					editorInputChangeHandler.handleEditorInputObjectRemoved(editorInput);
				}
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected IOperationHistoryListener createObjectChangedListener() {
		return new IOperationHistoryListener() {
			public void historyNotification(final OperationHistoryEvent event) {
				if (event.getEventType() == OperationHistoryEvent.DONE || event.getEventType() == OperationHistoryEvent.UNDONE
						|| event.getEventType() == OperationHistoryEvent.REDONE) {
					Set<Resource> affectedResources = ResourceUndoContext.getAffectedResources(event.getOperation());
					Set<URI> affectedResourceURIs = new HashSet<URI>(affectedResources.size());
					for (Resource affectedResource : affectedResources) {
						affectedResourceURIs.add(affectedResource.getURI());
					}
					if (editorInputChangeAnalyzer.containEditorInputResourceURI(editorInput, affectedResourceURIs)) {
						editorInputChangeHandler.handleEditorInputObjectChanged(editorInput);
					}
				}
			}
		};
	}

	protected IOperationHistory getOperationHistory() {
		CommandStack commandStack = editingDomain.getCommandStack();
		if (commandStack instanceof IWorkspaceCommandStack) {
			return ((IWorkspaceCommandStack) commandStack).getOperationHistory();
		}
		return OperationHistoryFactory.getOperationHistory();
	}

	/*
	 * @see org.eclipse.emf.edit.provider.IDisposable#dispose()
	 */
	public void dispose() {
		uninstallModelChangeListeners();
	}
}
