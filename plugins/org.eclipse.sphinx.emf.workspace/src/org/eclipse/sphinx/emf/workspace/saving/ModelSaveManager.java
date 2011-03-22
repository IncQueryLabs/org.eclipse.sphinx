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
package org.eclipse.sphinx.emf.workspace.saving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.saving.SaveIndicatorUtil;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.referentialintegrity.IURIChangeListener;
import org.eclipse.sphinx.emf.workspace.referentialintegrity.URIChangeEvent;
import org.eclipse.sphinx.emf.workspace.referentialintegrity.URIChangeListenerRegistry;
import org.eclipse.sphinx.emf.workspace.referentialintegrity.URIChangeNotification;
import org.eclipse.sphinx.platform.messages.PlatformMessages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * The Workspace Model Save Manager.
 */
public class ModelSaveManager {

	/**
	 * The singleton instance.
	 */
	public static ModelSaveManager INSTANCE = new ModelSaveManager();

	/**
	 * Listeners of model's dirty state changes.
	 */
	private Collection<IModelDirtyChangeListener> modelDirtyChangeListeners = new ArrayList<IModelDirtyChangeListener>();

	private IURIChangeListener uriChangeListener = new IURIChangeListener() {
		public void uriChanged(URIChangeEvent event) {
			if (event != null) {
				HashSet<Resource> dirtyResources = new HashSet<Resource>();
				Resource changedResource = (Resource) event.getSource();
				if (changedResource != null) {
					List<URIChangeNotification> notifications = event.getNotifications();
					for (URIChangeNotification notification : notifications) {
						EObject newEObject = notification.getNewEObject();
						Collection<Setting> inverseReferences = EObjectUtil.getInverseReferences(newEObject, true);
						for (Setting inverseReference : inverseReferences) {
							Resource referringResource = inverseReference.getEObject().eResource();
							if (!changedResource.equals(referringResource) && !dirtyResources.contains(referringResource)
									&& !SaveIndicatorUtil.isDirty(WorkspaceEditingDomainUtil.getEditingDomain(referringResource), referringResource)) {
								SaveIndicatorUtil.setDirty(WorkspaceEditingDomainUtil.getEditingDomain(referringResource), referringResource);
								dirtyResources.add(referringResource);
							}
						}
					}
				}
			}
		}
	};

	/**
	 * Private constructor for singleton pattern.
	 */
	private ModelSaveManager() {
		URIChangeListenerRegistry.INSTANCE.addListener(uriChangeListener);
	}

	@Override
	protected void finalize() throws Throwable {
		URIChangeListenerRegistry.INSTANCE.removeListener(uriChangeListener);
		super.finalize();
	}

	/**
	 * Adds a listener to this model save manager, which will be notified whenever the dirty state of one of its model
	 * changes.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addModelDirtyChangedListener(IModelDirtyChangeListener listener) {
		modelDirtyChangeListeners.add(listener);
	}

	/**
	 * Removes a IModelDirtyChangeListener from this model save manager.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeModelDirtyChangedListener(IModelDirtyChangeListener listener) {
		modelDirtyChangeListeners.remove(listener);
	}

	/**
	 * Notifies listeners that given source object's dirty state has changed.
	 * 
	 * @param source
	 *            The source object whose dirty state has changed.
	 */
	public void notifyDirtyChanged(Object source) {
		if (source == null) {
			return;
		}

		Set<IModelDescriptor> modelDescriptors = new HashSet<IModelDescriptor>();
		if (source instanceof IModelDescriptor) {
			modelDescriptors.add((IModelDescriptor) source);
		} else if (source instanceof IContainer) {
			IContainer container = (IContainer) source;
			modelDescriptors.addAll(ModelDescriptorRegistry.INSTANCE.getModels(container));
		} else {
			Resource resource = EcorePlatformUtil.getResource(source);
			if (resource != null) {
				IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(resource);
				if (modelDescriptor != null) {
					modelDescriptors.add(modelDescriptor);
				}
			} else {
				String reason = NLS.bind(Messages.error_unexpectedSourceType, source.getClass().getSimpleName());
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), new RuntimeException(PlatformMessages.error_caseNotYetSupported + "\n" + reason)); //$NON-NLS-1$
			}

		}

		for (IModelDirtyChangeListener listener : modelDirtyChangeListeners) {
			for (IModelDescriptor modelDescriptor : modelDescriptors) {
				listener.handleDirtyChangedEvent(modelDescriptor);
			}
		}
	}

	/**
	 * @param contextResource
	 *            A resource of the model whose dirty state must be returned.
	 * @return The dirty state of the given resource.
	 */
	public boolean isDirty(Resource contextResource) {
		return isDirty(EcorePlatformUtil.getFile(contextResource));
	}

	/**
	 * @param contextFile
	 *            The file whose dirty state must be returned.
	 * @return The dirty state of the given file.
	 */
	public boolean isDirty(IFile contextFile) {
		IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(contextFile);
		return isDirty(model);
	}

	/**
	 * @param container
	 *            The container whose dirty state must be returned.
	 * @return The dirty state of the given container.
	 */
	public boolean isDirty(IContainer container) {
		Collection<IModelDescriptor> models = ModelDescriptorRegistry.INSTANCE.getModels(container);
		for (IModelDescriptor modelDescriptor : models) {
			if (isDirty(modelDescriptor)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * @param modelDescriptor
	 *            The descriptor of the model whose dirty state must be returned.
	 * @return The dirty state of the given model.
	 */
	public boolean isDirty(IModelDescriptor modelDescriptor) {
		return SaveIndicatorUtil.isDirty(modelDescriptor);
	}

	/**
	 * Marks the given resource as dirty.
	 * 
	 * @param resource
	 *            The resource to mark as dirty.
	 */
	public void setDirty(Resource resource) {
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(resource);
		SaveIndicatorUtil.setDirty(editingDomain, resource);
	}

	/**
	 * Marks the given file as dirty.
	 * 
	 * @param file
	 *            The file to mark as dirty.
	 */
	public void setDirty(IFile file) {
		if (file != null && file.isAccessible()) {
			Resource resource = EcorePlatformUtil.getResource(file);
			if (resource != null) {
				setDirty(resource);
			}
		}
	}

	/**
	 * Marks the given container as dirty.
	 * 
	 * @param container
	 *            The container to mark as dirty.
	 */
	// FIXME Is that relevant to mark a container as dirty by marking all its members as dirty?
	public void setDirty(IContainer container) {
		if (container != null && container.isAccessible()) {
			try {
				for (IResource member : container.members()) {
					if (member instanceof IFile) {
						setDirty((IFile) member);
					} else if (member instanceof IContainer) {
						setDirty((IContainer) member);
					}
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			}
		}
	}

	/**
	 * Marks the given resource as saved.
	 * 
	 * @param resource
	 *            The resource to mark as saved.
	 */
	public void setSaved(Resource resource) {
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(resource);
		SaveIndicatorUtil.setSaved(editingDomain, resource);
	}

	/**
	 * Marks the given file as saved.
	 * 
	 * @param file
	 *            The file to mark as saved.
	 */
	public void setSaved(IFile file) {
		if (file != null && file.isAccessible()) {
			Resource resource = EcorePlatformUtil.getResource(file);
			if (resource != null) {
				setSaved(resource);
			}
		}
	}

	/**
	 * Marks the models behind the given container as saved.
	 * 
	 * @param container
	 *            The container to mark as saved.
	 */
	public void setSaved(IContainer container) {
		Collection<IModelDescriptor> models = ModelDescriptorRegistry.INSTANCE.getModels(container);
		for (IModelDescriptor modelDescriptor : models) {
			setSaved(modelDescriptor);
		}
	}

	/**
	 * Marks the given model as saved.
	 * 
	 * @param modelDescriptor
	 *            The modelDescriptor to mark as saved.
	 */
	public void setSaved(IModelDescriptor modelDescriptor) {
		if (modelDescriptor != null) {
			SaveIndicatorUtil.setSaved(modelDescriptor);
		}
	}

	/**
	 * Saves all modified resources of the model behind given resource (<em>i.e.</em> all resources in the context of
	 * the given one).
	 * 
	 * @param contextResource
	 *            The object resource identifying the model to save.
	 * @param async
	 *            If <code>true</code>, model will be saved within a workspace job.
	 * @param monitor
	 *            The progress monitor to use for showing save process progress.
	 */
	public void saveModel(Resource contextResource, boolean async, IProgressMonitor monitor) {
		saveModel(contextResource, EcoreResourceUtil.getDefaultSaveOptions(), async, monitor);
	}

	public void saveModel(Resource contextResource, Map<?, ?> saveOptions, boolean async, IProgressMonitor monitor) {
		EcorePlatformUtil.saveModel(contextResource, saveOptions, async, monitor);
		notifyDirtyChanged(contextResource);
	}

	/**
	 * Saves all modified resources of the model behind given {@linkplain IModelDescriptor model descriptor}.
	 * 
	 * @param descriptor
	 *            The descriptor of the model to save.
	 * @param async
	 *            If <code>true</code>, model will be saved within a workspace job.
	 * @param monitor
	 *            The progress monitor to use for showing save process progress.
	 */
	public void saveModel(IModelDescriptor modelDescriptor, boolean async, IProgressMonitor monitor) {
		saveModel(modelDescriptor, EcoreResourceUtil.getDefaultSaveOptions(), async, monitor);
	}

	public void saveModel(IModelDescriptor modelDescriptor, Map<?, ?> saveOptions, boolean async, IProgressMonitor monitor) {
		EcorePlatformUtil.saveModel(modelDescriptor, saveOptions, async, monitor);
		notifyDirtyChanged(modelDescriptor);
	}

	/**
	 * Saves all modified resources of all models behind given project.
	 * 
	 * @param project
	 *            The project whose models are to be saved.
	 * @param async
	 *            If <code>true</code>, models will be saved within a workspace job.
	 * @param monitor
	 *            The progress monitor to use for showing save process progress.
	 * @since 0.7.0
	 */
	public void saveProject(IProject project, boolean async, IProgressMonitor monitor) {
		saveProject(project, EcoreResourceUtil.getDefaultSaveOptions(), async, monitor);
	}

	public void saveProject(IProject project, Map<?, ?> saveOptions, boolean async, IProgressMonitor monitor) {
		EcorePlatformUtil.saveProject(project, saveOptions, async, monitor);
		notifyDirtyChanged(project);
	}
}
