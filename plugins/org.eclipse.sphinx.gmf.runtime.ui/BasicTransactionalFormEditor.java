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
package org.eclipse.sphinx.gmf.runtime.ui.editor.document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.ResourceUndoContext;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.ui.util.EcoreUIUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.domain.WorkspaceEditingDomainManager;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

/**
 * A basic Eclipse Forms-based model editor.
 */
public class BasicTransactionalFormEditor extends FormEditor implements IEditingDomainProvider, ISelectionProvider, IMenuListener, IViewerProvider,
		IGotoMarker, IPersistableEditor, ITabbedPropertySheetPageContributor, ISaveablesSource {

	/**
	 * The undo context for this form editor.
	 */
	protected IUndoContext undoContext;

	/**
	 * The EObject that is currently being edited.
	 */
	private EObject modelRoot = null;

	/**
	 * The EObject that has been edited before.
	 */
	private EObject oldModelRoot = null;

	protected AdapterFactoryItemDelegator itemDelegator;

	private ResourceSetListener resourceLoadedListener;

	private ResourceSetListener resourceMovedListener;

	private ResourceSetListener resourceRemovedListener;

	private IOperationHistoryListener affectedObjectsListener;

	private ResourceSetListener objectRemovedListener;

	private CommandStackListener commandStackListener;

	/**
	 * This creates a model editor.
	 */
	public BasicTransactionalFormEditor() {
		// Create undo context
		undoContext = new ObjectUndoContext(this);
	}

	protected void updateEditorInput(URI newURI) {
		Assert.isNotNull(newURI);

		URI newInputURI = newURI;
		URI oldInputURI = EcoreUIUtil.getURIFromEditorInput(getEditorInput());
		if (!newURI.hasFragment() && oldInputURI != null && oldInputURI.hasFragment()) {
			newInputURI = newURI.appendFragment(oldInputURI.fragment());
		}

		if (!newInputURI.equals(oldInputURI)) {
			IEditorInput newInput = new URIEditorInput(newInputURI);

			// Set new editor input
			setInputWithNotify(newInput);

			// Update editor part title
			setTitleToolTip(getTitleToolTip());
		}
	}

	/**
	 * @return The root object of the model part that is currently being edited in this editor or <code>null</code> if
	 *         no such is available.
	 */
	public Object getModelRoot() {
		if (modelRoot == null || modelRoot.eIsProxy() || modelRoot.eResource() == null || !modelRoot.eResource().isLoaded()) {
			URI editorInputURI = EcoreUIUtil.getURIFromEditorInput(getEditorInput());
			if (editorInputURI != null) {
				if (oldModelRoot == null) {
					oldModelRoot = modelRoot;
				}

				modelRoot = getEObject(editorInputURI);

				if (modelRoot != null && oldModelRoot != null) {
					oldModelRoot = null;
				}
			}
		}
		return modelRoot;
	}

	/**
	 * @return The root object of the model part that has been edited before if no such is currently available, or
	 *         <code>null</code> otherwise.
	 * @see #getModelRoot()
	 */
	public Object getOldModelRoot() {
		return oldModelRoot;
	}

	protected EObject getEObject(final URI uri) {
		final TransactionalEditingDomain editingDomain = getEditingDomain(uri);
		if (editingDomain != null) {
			final boolean loadOnDemand = getEditorInput() instanceof FileStoreEditorInput ? true : false;
			try {
				return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<EObject>() {
					public void run() {
						if (uri.hasFragment()) {
							setResult(EcoreResourceUtil.getModelFragment(editingDomain.getResourceSet(), uri, loadOnDemand));
						} else {
							setResult(EcoreResourceUtil.getModelRoot(editingDomain.getResourceSet(), uri, loadOnDemand));
						}
					}
				});
			} catch (InterruptedException ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}
		}
		return null;
	}

	protected TransactionalEditingDomain getEditingDomain(final URI uri) {
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(uri);
		if (editingDomain == null && getEditorInput() instanceof FileStoreEditorInput) {
			// If the file has been deleted
			if (((FileStoreEditorInput) getEditorInput()).exists()) {
				String modelNamespace = EcoreResourceUtil.readModelNamespace(null, EcoreUIUtil.getURIFromEditorInput(getEditorInput()));
				editingDomain = WorkspaceEditingDomainManager.INSTANCE.getEditingDomainMapping().getEditingDomain(null,
						MetaModelDescriptorRegistry.INSTANCE.getDescriptor(java.net.URI.create(modelNamespace)));
			}
		}
		return editingDomain;

	}

	public Resource getModelRootResource() {
		Object modelRoot = getModelRoot();
		if (modelRoot instanceof EObject) {
			return ((EObject) modelRoot).eResource();
		}
		return null;
	}

	protected String getEditorInputName() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			return editorInput.getName();
		}

		Object modelRoot = getModelRoot();
		AdapterFactoryItemDelegator itemDelegator = getItemDelegator();
		if (modelRoot != null && itemDelegator != null) {
			// Return label of model object on which editor has been opened
			return itemDelegator.getText(modelRoot);
		}

		return editorInput.getName();
	}

	protected Image getEditorInputImage() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			ImageDescriptor imageDescriptor = editorInput.getImageDescriptor();
			return ExtendedImageRegistry.getInstance().getImage(imageDescriptor);
		}

		Object modelRoot = getModelRoot();
		AdapterFactoryItemDelegator itemDelegator = getItemDelegator();
		if (modelRoot != null && itemDelegator != null) {
			// Return icon of model object on which editor has been opened
			Object imageURL = itemDelegator.getImage(modelRoot);
			return ExtendedImageRegistry.getInstance().getImage(imageURL);
		}

		ImageDescriptor imageDescriptor = editorInput.getImageDescriptor();
		return ExtendedImageRegistry.getInstance().getImage(imageDescriptor);
	}

	public IUndoContext getUndoContext() {
		return undoContext;
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		if (key.equals(IUndoContext.class)) {
			// used by undo/redo actions to get their undo context
			return undoContext;
		} else {
			return super.getAdapter(key);
		}
	}

	@Override
	public boolean isDirty() {
		// For resources outside the workspace
		if (getEditorInput() instanceof FileStoreEditorInput && ((FileStoreEditorInput) getEditorInput()).exists()) {
			return ((BasicCommandStack) getEditingDomain().getCommandStack()).isSaveNeeded();
		}
		Object modelRoot = getModelRoot();
		if (modelRoot instanceof EObject) {
			// Return true if the model, this editor or both are dirty
			return ModelSaveManager.INSTANCE.isDirty(((EObject) modelRoot).eResource());
		}
		return false;
	}

	/**
	 * This is for implementing {@link IEditorPart} and simply saves the model file.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			Object modelRoot = getModelRoot();
			if (modelRoot instanceof EObject) {
				// Save the all dirty resources of underlying model
				ModelSaveManager.INSTANCE.saveModel(((EObject) modelRoot).eResource(), getSaveOptions(), false, monitor);
			}
		} finally {
			/*
			 * !! Important Note !! Normally we shouldn't need to close down the progress monitor at this point.
			 * However, it looks like that the progress monitor is not handled appropriately by whoever call us here
			 * because we have observed the progress bar stays frozen at 100% after completion of the save operation. In
			 * order to avoid that we notify the progress monitor that the save work is done right here.
			 */
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * This is called during startup.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) {
		addTransactionalEditingDomainListeners((TransactionalEditingDomain) getEditingDomain());
	}

	public IOperationHistory getOperationHistory() {
		EditingDomain editingDomain = getEditingDomain();
		if (editingDomain != null) {
			IWorkspaceCommandStack commandStack = (IWorkspaceCommandStack) editingDomain.getCommandStack();
			if (commandStack != null) {
				return commandStack.getOperationHistory();
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		removeTransactionalEditingDomainListeners((TransactionalEditingDomain) getEditingDomain());

		IOperationHistory operationHistory = getOperationHistory();
		if (operationHistory != null) {
			operationHistory.dispose(getUndoContext(), true, true, true);
		}
	}

	protected void addTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
		if (editingDomain != null) {
			// Create and register ResourceSetChangedListener that detects loaded resources
			resourceLoadedListener = createResourceLoadedListener();
			Assert.isNotNull(resourceLoadedListener);
			editingDomain.addResourceSetListener(resourceLoadedListener);

			// Create and register ResourceSetChangedListener that detects renamed or moved resources
			resourceMovedListener = createResourceMovedListener();
			Assert.isNotNull(resourceMovedListener);
			editingDomain.addResourceSetListener(resourceMovedListener);

			// Create and register ResourceSetChangedListener that detects removed resources
			resourceRemovedListener = createResourceRemovedListener();
			Assert.isNotNull(resourceRemovedListener);
			editingDomain.addResourceSetListener(resourceRemovedListener);

			// Create and register ResourceSetChangedListener that detects removed objects
			objectRemovedListener = createObjectRemovedListener();
			Assert.isNotNull(objectRemovedListener);
			editingDomain.addResourceSetListener(objectRemovedListener);

			// Create and register IOperationHistoryListener that detects changed objects
			affectedObjectsListener = createAffectedObjectsListener();
			Assert.isNotNull(affectedObjectsListener);
			((IWorkspaceCommandStack) editingDomain.getCommandStack()).getOperationHistory().addOperationHistoryListener(affectedObjectsListener);
		}
	}

	protected void removeTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
		if (editingDomain != null) {
			if (resourceLoadedListener != null) {
				editingDomain.removeResourceSetListener(resourceLoadedListener);
			}
			if (resourceMovedListener != null) {
				editingDomain.removeResourceSetListener(resourceMovedListener);
			}
			if (resourceRemovedListener != null) {
				editingDomain.removeResourceSetListener(resourceRemovedListener);
			}
			if (objectRemovedListener != null) {
				editingDomain.removeResourceSetListener(objectRemovedListener);
			}
			if (affectedObjectsListener != null) {
				IOperationHistory operationHistory = ((IWorkspaceCommandStack) editingDomain.getCommandStack()).getOperationHistory();
				operationHistory.removeOperationHistoryListener(affectedObjectsListener);
			}
		}
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

								// Is loaded resource equal to model root resource?
								URI editorInputURI = EcoreUIUtil.getURIFromEditorInput(getEditorInput());
								if (editorInputURI != null && loadedResource.getURI().equals(editorInputURI.trimFragment())) {
									// Handle loaded model root resource
									handleModelRootResourceLoaded();
									break;
								}
							}
						}
					}
				}
			}

			private void handleModelRootResourceLoaded() {
				IWorkbenchPartSite site = getSite();
				if (site != null && site.getShell() != null && !site.getShell().isDisposed()) {
					site.getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							// Discard undo context and reset dirty state
							IOperationHistory operationHistory = getOperationHistory();
							if (operationHistory != null) {
								operationHistory.dispose(undoContext, true, true, true);
							}

							// Update this editor's dirty state
							firePropertyChange(IEditorPart.PROP_DIRTY);

							// Update editor part name
							setPartName(getEditorInputName());
							setTitleImage(getEditorInputImage());
						}
					});
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

						// Is removed resource equal to model root resource?
						if (notification.getOldValue() instanceof URI) {
							URI oldResourceURI = (URI) notification.getOldValue();
							URI editorInputURI = EcoreUIUtil.getURIFromEditorInput(getEditorInput());
							if (editorInputURI != null && oldResourceURI != null && oldResourceURI.equals(editorInputURI.trimFragment())) {
								// Handle moved model root resource
								handleModelRootResourceMoved((URI) notification.getNewValue());
								break;
							}
						}
					}
				}
			}

			private void handleModelRootResourceMoved(final URI newResourceURI) {
				IWorkbenchPartSite site = getSite();
				if (site != null && site.getShell() != null && !site.getShell().isDisposed()) {
					site.getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							// Discard undo context
							IOperationHistory operationHistory = getOperationHistory();
							if (operationHistory != null) {
								operationHistory.dispose(undoContext, true, true, true);
							}

							// Update this editor's dirty state
							firePropertyChange(IEditorPart.PROP_DIRTY);

							// Update editor input
							updateEditorInput(newResourceURI);
						}
					});
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

				// Is model root resource part of removed resources?
				URI editorInputURI = EcoreUIUtil.getURIFromEditorInput(getEditorInput());
				if (editorInputURI != null) {
					URI modelRootResourceURI = editorInputURI.trimFragment();
					for (Resource removedResource : removedResources) {
						if (removedResource.getURI().equals(modelRootResourceURI)) {
							// Handle removed model root resource
							handleModelRootResourceRemoved();
						}
					}
				}
			}

			private void handleModelRootResourceRemoved() {
				// Close editor
				close(false);
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

				// Is model root on which this editor had been opened so far or one of its containers part of the
				// objects that have been removed?
				if (getModelRoot() == null) {
					Object oldModelRoot = getOldModelRoot();
					if (removedObjects.contains(oldModelRoot)) {
						// Handle removed model root
						handleModelRootRemoved();
					} else {
						if (oldModelRoot instanceof EObject) {
							for (EObject parent = ((EObject) oldModelRoot).eContainer(); parent != null; parent = parent.eContainer()) {
								if (removedObjects.contains(parent)) {
									// Handle removed model root
									handleModelRootRemoved();
									return;
								}
							}
						}
					}
				}
			}

			private void handleModelRootRemoved() {
				// Close editor
				close(false);
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected IOperationHistoryListener createAffectedObjectsListener() {
		return new IOperationHistoryListener() {
			public void historyNotification(final OperationHistoryEvent event) {
				if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
					handleOperationAboutToExecute(event.getOperation());
				} else if (event.getEventType() == OperationHistoryEvent.DONE || event.getEventType() == OperationHistoryEvent.UNDONE
						|| event.getEventType() == OperationHistoryEvent.REDONE) {
					Set<?> affectedResources = ResourceUndoContext.getAffectedResources(event.getOperation());
					if (affectedResources.contains(getModelRootResource())) {
						handleOperationFinished(event.getOperation());
					}
				}
			}

			private void handleOperationAboutToExecute(final IUndoableOperation operation) {
				if (operation.canUndo()) {
					IWorkbenchPartSite site = getSite();
					if (site != null && site.getShell() != null && !site.getShell().isDisposed()) {
						site.getShell().getDisplay().syncExec(new Runnable() {
							public void run() {
								if (isActivePart() || isMyActivePropertySheetPage()) {
									EditingDomain editingDomain = getEditingDomain();
									if (editingDomain != null && editingDomain.getCommandStack() instanceof IWorkspaceCommandStack) {
										IWorkspaceCommandStack cmdStack = (IWorkspaceCommandStack) editingDomain.getCommandStack();
										IUndoContext defaultUndoContext = cmdStack.getDefaultUndoContext();
										if (defaultUndoContext != null) {
											// Remove default undo context and add this editor's undo context
											operation.removeContext(defaultUndoContext);
										}
									}
									operation.addContext(getUndoContext());
								}
							}
						});
					}
				}
			}

			private void handleOperationFinished(final IUndoableOperation operation) {
				IWorkbenchPartSite site = getSite();
				if (site != null && site.getShell() != null && !site.getShell().isDisposed()) {
					site.getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							// Update editor input
							URI newModelRootURI = EcoreUtil.getURI((EObject) getModelRoot());
							updateEditorInput(newModelRootURI);

							// Update editor part name
							setPartName(getEditorInputName());
							setTitleImage(getEditorInputImage());
						}
					});
				}
			}
		};
	}
}