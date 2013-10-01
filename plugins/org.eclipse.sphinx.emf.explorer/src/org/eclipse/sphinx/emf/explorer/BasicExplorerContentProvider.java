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
 *     itemis - [348822] Enable BasicExplorerContentProvider to be used for displaying model content under folders and projects
 *     itemis - [418005] Add support for model files with multiple root elements
 *      
 * </copyright>
 */
package org.eclipse.sphinx.emf.explorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.internal.Tracing;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.eclipse.ui.navigator.INavigatorContentDescriptor;

/**
 * EMF model content provider for use with the Common Navigator framework. It deals with the workspace integration of
 * the EMF model and internally delegates to EMF-based model content provider to get the children and parent of model
 * objects.
 */
@SuppressWarnings("restriction")
public class BasicExplorerContentProvider implements ICommonContentProvider, IViewerProvider {

	protected static final int LIMIT_INDIVIDUAL_RESOURCES_REFRESH = 20;

	protected static final int LIMIT_INDIVIDUAL_OBJECTS_REFRESH = 100;

	protected Viewer viewer;

	protected INavigatorContentDescriptor contentDescriptor;

	protected Map<TransactionalEditingDomain, AdapterFactoryContentProvider> modelContentProviders = new WeakHashMap<TransactionalEditingDomain, AdapterFactoryContentProvider>();

	protected ResourceSetListener resourceChangedListener;

	protected ResourceSetListener resourceMovedListener;

	protected ResourceSetListener nonContainmentReferenceChangeListener;

	protected ResourceSetListener modelContentRootChangeListener;

	/**
	 * Returns the viewer whose content is provided by this content provider.
	 * 
	 * @return the viewer for this content provider
	 */
	public Viewer getViewer() {
		return viewer;
	}

	public void init(ICommonContentExtensionSite config) {
		contentDescriptor = config.getExtension().getDescriptor();
	}

	protected boolean isTriggerPoint(IResource resource) {
		return contentDescriptor.isTriggerPoint(resource);
	}

	protected boolean isPossibleChild(Object object) {
		return contentDescriptor.isPossibleChild(object);
	}

	public void saveState(IMemento memento) {
		// Do nothing by default
	}

	public void restoreState(IMemento memento) {
		// Do nothing by default
	}

	/**
	 * Retrieves the model {@link Resource resource} behind specified workspace {@link IResource resource}. Returns
	 * <code>null</code> if no such is available or the {@link IModelDescriptor model} behind specified workspace
	 * resource has not been loaded yet.
	 * <p>
	 * Default implementation supports the handling of {@link IFile file} resources including lazy loading of the
	 * underlying {@link IModelDescriptor model}s: if the given file belongs to some model that has not been loaded yet
	 * then the loading of that model, i.e., the given file and all other files belonging to the same, will be
	 * triggered. The model loading will be performed asynchronously and therefore won't block the UI. When the model
	 * loading has been completed, the {@link #resourceChangedListener} automatically refreshes the underlying
	 * {@link #viewer viewer} so that the model elements contained by the given file become visible.
	 * <p>
	 * Clients may override this method so as to add support for other resource types (e.g., {@link IProject project}s
	 * or {@link IFolder folder}s) or implement different lazy or eager loading strategies.
	 * 
	 * @param workspaceResource
	 *            The workspace resource of which the model resource is to be retrieved.
	 * @return The model resource behind specified workspace resource or <code>null</code> if no such is available or
	 *         the model behind specified workspace resource has not been loaded yet.
	 */
	protected Resource getModelResource(IResource workspaceResource) {
		// Is given workspace resource a file?
		if (workspaceResource instanceof IFile) {
			// Get model behind given workspace file
			IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel((IFile) workspaceResource);
			if (modelDescriptor != null) {
				// Try to retrieve model resource behind given workspace file but don't force it to be loaded in case
				// that this has not been done yet
				Resource modelResource = EcorePlatformUtil.getResource((IFile) workspaceResource);

				// Given model resource already loaded?
				if (modelResource != null) {
					return modelResource;
				} else {
					// Make sure that resource set listeners for refreshing the viewer are installed on editing domain
					// and get notified when model resources get loaded subsequently
					addTransactionalEditingDomainListeners(modelDescriptor.getEditingDomain());

					// Request asynchronous loading of model behind given workspace file
					ModelLoadManager.INSTANCE.loadModel(modelDescriptor, true, null);
				}
			}
		}
		return null;
	}

	/**
	 * Returns a list of objects which are to be used as roots of the model content provided by this
	 * {@link BasicExplorerContentProvider content provider} for the given model resource. They may or may not be the
	 * actual root objects of the model resource.
	 * <p>
	 * The model content roots can be thought of being mapped to the workspace {@link IResource resource} behind given
	 * model resource and are the actual parent objects of the model objects to be displayed as virtual children of the
	 * workspace resource. The model content roots themselves will not become visible in the underlying viewer, only the
	 * workspace resource they are mapped to and their children will.
	 * </p>
	 * <p>
	 * This implementation returns a list containing the provided model resource itself as default. Clients are free to
	 * override and implement alternative behaviors as appropriate.
	 * </p>
	 * 
	 * @param modelResource
	 *            The model resource of which the model content roots are to be retrieved.
	 * @return The list of notifier objects which are to be used as roots of the model content provided by this
	 *         {@link BasicExplorerContentProvider content provider}.
	 */
	protected List<Object> getModelContentRoots(Resource modelResource) {
		if (modelResource != null) {
			ArrayList<Object> modelContentRoots = new ArrayList<Object>(3);

			// Ensure backward compatibility
			if (!modelResource.getContents().isEmpty()) {
				Object deprecatedModelContentRoot = getModelContentRoot(modelResource.getContents().get(0));
				if (deprecatedModelContentRoot != null) {
					modelContentRoots.add(deprecatedModelContentRoot);
				}
			}

			// Return model resource as only model content root by default
			if (modelContentRoots.isEmpty()) {
				modelContentRoots.add(modelResource);
			}

			return modelContentRoots;
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the {@link IResource resource} corresponding to given model {@link Resource resource}.
	 * 
	 * @param modelResource
	 *            The model resource of which the workspace resource it to be returned.
	 * @return The workspace {@link IResource resource} corresponding to given model resource.
	 * @see #getModelResource(IResource)
	 */
	protected IResource getWorkspaceResource(Resource modelResource) {
		return EcorePlatformUtil.getFile(modelResource);
	}

	/**
	 * Retrieves the model root behind specified {@link IResource resource}. Returns <code>null</code> if no such is
	 * available or the {@link IModelDescriptor model} behind specified resource has not been loaded yet.
	 * <p>
	 * Default implementation supports the handling of {@link IFile file} resources including lazy loading of the
	 * underlying {@link IModelDescriptor model}s: if the given file belongs to some model that has not been loaded yet
	 * then the loading of that model, i.e., the given file and all other files belonging to the same model, will be
	 * triggered. The model loading will be done asynchronously and therefore won't block the UI. When the model loading
	 * has been completed, the {@link #resourceChangedListener} automatically refreshes the underlying {@link #viewer
	 * viewer} so that the model elements contained by the given file become visible.
	 * <p>
	 * Clients may override this method so as to add support for other resource types (e.g., {@link IProject project}s
	 * or {@link IFolder folder}s) or implement different lazy or eager loading strategies.
	 * 
	 * @param resource
	 *            The {@link IResource resource} whose model root is to be retrieved.
	 * @return The model root behind specified resource or <code>null</code> if no such is available or the model behind
	 *         specified resource has not been loaded yet.
	 */
	@Deprecated
	protected Object getModelRoot(IResource resource) {
		// Is given workspace resource a file?
		if (resource instanceof IFile) {
			// Get model behind given file
			IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel((IFile) resource);
			if (modelDescriptor != null) {
				// Get model root of given file but don't force it to be loaded in case that this has not
				// been done yet
				Object modelRoot = null;
				Resource modelResource = EcorePlatformUtil.getResource((IFile) resource);
				if (modelResource != null && !modelResource.getContents().isEmpty()) {
					modelRoot = modelResource.getContents().get(0);
				}

				// Given file not loaded yet?
				if (modelRoot == null) {
					// Make sure that resource set listeners for refreshing the viewer are installed on editing domain
					// and get notified when model resources have been loaded
					addTransactionalEditingDomainListeners(modelDescriptor.getEditingDomain());

					// Request asynchronous loading of model behind given file
					ModelLoadManager.INSTANCE.loadModel(modelDescriptor, true, null);
				}
				return modelRoot;
			}
		}

		return null;
	}

	/**
	 * Retrieves the <em>root element</em> of the model contained in the specified {@link IFile file}. Returns
	 * <code>null</code> if that file has not yet been loaded into the {@linkplain ResourceSet} of the specified
	 * {@link TransactionalEditingDomain editingDomain} or if it is empty.
	 * <p>
	 * Default implementation provides a lazy loading mechanism. When the user expands an model file of some
	 * {@link IModelDescriptor model} which has not been loaded yet, this and all other model files which belong to that
	 * {@link IModelDescriptor model} will be loaded by that time. This loading will be done asynchronously using
	 * {@linkplain ModelLoadManager} API, and therefore won't block the UI while the loading process is ongoing. Once
	 * the loading of the {@link IModelDescriptor model} will be finished, the refresh mechanism (i.e., the
	 * {@linkplain ResourceSetListener resource changed listener}) will make sure that the view gets refreshed and the
	 * model elements inside the expanded model file become visible.
	 * <p>
	 * Clients may override this method in order to provide a custom lazy or eager loading strategy.
	 * 
	 * @param editingDomain
	 *            The {@linkplain TransactionalEditingDomain editing domain} owning the {@linkplain ResourceSet resource
	 *            set} inside which the {@linkplain Resource resource} corresponding to the specified {@link IFile file}
	 *            should be loaded.
	 * @param file
	 *            The {@linkplain IFile file} which <em>model root</em> must be returned.
	 * @return The root element of the model owned by the specified {@link IFile file}; or <code>null</code> if file has
	 *         not been loaded.
	 * @deprecated Use {@link #getModelRoot(IResource)} instead. Rationale: Navigation into models should not be
	 *             supported only from files but also from projects and folders. The TransactionalEditingDomain can be
	 *             easily retrieved inside the method and therefore does not need to be provided by the caller.
	 */
	@Deprecated
	protected EObject getModelRoot(TransactionalEditingDomain editingDomain, IFile file) {
		Object modelRoot = getModelRoot(file);
		return modelRoot instanceof EObject ? (EObject) modelRoot : null;
	}

	/**
	 * Returns the {@link EObject object} or {@link Resource resource} which is used as root of the model content
	 * provided by this {@link BasicExplorerContentProvider content provider} for given model object. The model content
	 * root is the model object that is mapped to the {@link IResource workspace resource} under which the model behind
	 * given model object is made visible. It may or may not be the actual root object of the model. The model content
	 * root itself will not become visible in the underlying viewer, only the workspace resource it is mapped to and its
	 * children will.
	 * 
	 * @param object
	 *            An arbitrary model object to be investigated.
	 * @return The {@link EObject object} or {@link Resource resource} which is used as root of the model content
	 *         provided by this content provider for given model object or <code>null</code> if given object is no model
	 *         object or has no parent that corresponds to the expected model content root.
	 * @deprecated Use {@link #getModelContentRoots(Resource)} instead.
	 */
	@Deprecated
	protected Object getModelContentRoot(Object object) {
		if (object instanceof EObject) {
			return getModelContentRoot((EObject) object);
		} else if (object instanceof IWrapperItemProvider) {
			return getModelContentRoot((IWrapperItemProvider) object);
		} else if (object instanceof FeatureMap.Entry) {
			return getModelContentRoot((FeatureMap.Entry) object);
		} else if (object instanceof TransientItemProvider) {
			return getModelContentRoot((TransientItemProvider) object);
		}
		return null;
	}

	/**
	 * Returns the {@link EObject object} or {@link Resource resource} which is used as root of the model content
	 * provided by this {@link BasicExplorerContentProvider content provider} for given {@link EObject model object}.
	 * The model content root is the model object that is mapped to the {@link IResource workspace resource} under which
	 * the model behind given model object is made visible. It may or may not be the actual root object of the model.
	 * The model content root itself will not become visible in the underlying viewer, only the workspace resource it is
	 * mapped to and its children will.
	 * <p>
	 * This implementation returns the {@link Resource resource} behind given {@link EObject model object} as model
	 * content root. Clients should override this method if they require some other model object to be used instead.
	 * </p>
	 * <p>
	 * The most typical use cases and implementations of this method are as follows:
	 * <ul>
	 * <li>The {@link Resource resource} behind given model object is to be used as model content root:<br>
	 * <blockquote><code>return object.eResource();</code></blockquote></li>
	 * <li>The {@link EObject model root object} behind given model object is to be used as model content root:<br>
	 * <blockquote><code>return EcoreUtil.getRootContainer(object);</code></blockquote></li>
	 * </ul>
	 * </p>
	 * 
	 * @param object
	 *            An arbitrary {@link EObject object} to be investigated.
	 * @return The {@link EObject object} or {@link Resource resource} which is used as root of the model content
	 *         provided by this content provider for given model object or <code>null</code> if given object is no model
	 *         object or has no parent that corresponds to the expected model content root.
	 * @deprecated Use {@link #getModelContentRoots(Resource)} instead.
	 */
	@Deprecated
	protected Object getModelContentRoot(EObject object) {
		Assert.isNotNull(object);

		// Ensure backward compatibility
		Object mappedModelRoot = getMappedModelRoot(object);
		if (mappedModelRoot != null) {
			return mappedModelRoot;
		}

		return object.eResource();
	}

	/**
	 * @deprecated Use {@link #getModelContentRoot(EObject)} instead.
	 */
	@Deprecated
	protected Object getMappedModelRoot(EObject object) {
		return null;
	}

	/**
	 * @deprecated Use {@link #getModelContentRoots(Resource)} instead.
	 */
	@Deprecated
	protected Object getModelContentRoot(IWrapperItemProvider wrapperItemProvider) {
		Object unwrapped = AdapterFactoryEditingDomain.unwrap(wrapperItemProvider);
		return getModelContentRoot(unwrapped);
	}

	/**
	 * @deprecated Use {@link #getModelContentRoots(Resource)} instead.
	 */
	@Deprecated
	protected Object getModelContentRoot(FeatureMap.Entry entry) {
		Object unwrapped = AdapterFactoryEditingDomain.unwrap(entry);
		return getModelContentRoot(unwrapped);
	}

	/**
	 * @deprecated Use {@link #getModelContentRoots(Resource)} instead.
	 */
	@Deprecated
	protected Object getModelContentRoot(TransientItemProvider transientItemProvider) {
		Object target = transientItemProvider.getTarget();
		return getModelContentRoot(target);
	}

	/**
	 * Returns the {@link IResource resource} corresponding to given {@link #getModelContentRoot(Object) model content
	 * root}.
	 * 
	 * @param modelContentRoot
	 *            The {@link #getModelContentRoot(Object) model content root} object in question.
	 * @return The {@link IResource resource} corresponding to given model content root.
	 * @see #getModelContentRoot(Object)
	 * @deprecated Use {@link #getWorkspaceResource(Resource)} instead.
	 */
	@Deprecated
	protected IResource getUnderlyingWorkspaceResource(Object modelContentRoot) {
		return EcorePlatformUtil.getFile(modelContentRoot);
	}

	protected AdapterFactoryContentProvider getModelContentProvider(Object object) {
		// Retrieve editing domain behind specified object
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(object);
		if (editingDomain != null) {
			// Retrieve model content provider for given editing domain; create new one if not existing yet
			AdapterFactoryContentProvider modelContentProvider = modelContentProviders.get(editingDomain);
			if (modelContentProvider == null) {
				modelContentProvider = createModelContentProvider(editingDomain);
				modelContentProviders.put(editingDomain, modelContentProvider);
				addTransactionalEditingDomainListeners(editingDomain);
			}
			return modelContentProvider;
		}
		return null;
	}

	protected AdapterFactoryContentProvider createModelContentProvider(final TransactionalEditingDomain editingDomain) {
		Assert.isNotNull(editingDomain);
		AdapterFactory adapterFactory = getAdapterFactory(editingDomain);
		return new TransactionalAdapterFactoryContentProvider(editingDomain, adapterFactory) {
			@Override
			// Overridden to avoid somewhat annoying logging of Eclipse exceptions resulting from event queue
			// dispatching that is done before transaction is acquired and actually starts to run
			protected <T> T run(RunnableWithResult<? extends T> run) {
				try {
					return TransactionUtil.runExclusive(editingDomain, run);
				} catch (Exception e) {
					Tracing.catching(TransactionalAdapterFactoryLabelProvider.class, "run", e); //$NON-NLS-1$

					// propagate interrupt status because we are not throwing
					Thread.currentThread().interrupt();

					return null;
				}
			}
		};
	}

	/**
	 * Returns the {@link AdapterFactory adapter factory} to be used by this {@link BasicExplorerContentProvider content
	 * provider} for creating {@link ItemProviderAdapter item provider}s which control the way how {@link EObject model
	 * element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns the {@link AdapterFactory adapter factory} which is embedded in the given
	 * <code>editingDomain</code> by default. Clients which want to use an alternative {@link AdapterFactory adapter
	 * factory} (e.g., an {@link AdapterFactory adapter factory} that creates {@link ItemProviderAdapter item provider}s
	 * which are specifically designed for the {@link IEditorPart editor} in which this
	 * {@link BasicExplorerContentProvider content provider} is used) may override {@link #getCustomAdapterFactory()}
	 * and return any {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter
	 * factory} will then be returned as result by this method.
	 * </p>
	 * 
	 * @param editingDomain
	 *            The {@link TransactionalEditingDomain editing domain} whose embedded {@link AdapterFactory adapter
	 *            factory} is to be returned as default. May be left <code>null</code> if
	 *            {@link #getCustomAdapterFactory()} has been overridden and returns a non-<code>null</code> result.
	 * @return The {@link AdapterFactory adapter factory} that will be used by this {@link BasicExplorerContentProvider
	 *         content provider}. <code>null</code> if no custom {@link AdapterFactory adapter factory} is provided
	 *         through {@link #getCustomAdapterFactory()} and no <code>editingDomain</code> has been specified.
	 * @see #getCustomAdapterFactory()
	 */
	protected AdapterFactory getAdapterFactory(TransactionalEditingDomain editingDomain) {
		AdapterFactory customAdapterFactory = getCustomAdapterFactory();
		if (customAdapterFactory != null) {
			return customAdapterFactory;
		} else if (editingDomain != null) {
			return ((AdapterFactoryEditingDomain) editingDomain).getAdapterFactory();
		}
		return null;
	}

	/**
	 * Returns a custom {@link AdapterFactory adapter factory} to be used by this {@link BasicExplorerContentProvider
	 * content provider} for creating {@link ItemProviderAdapter item provider}s which control the way how
	 * {@link EObject model element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns <code>null</code> as default. Clients which want to use their own
	 * {@link AdapterFactory adapter factory} (e.g., an {@link AdapterFactory adapter factory} that creates
	 * {@link ItemProviderAdapter item provider}s which are specifically designed for the {@link IEditorPart editor} in
	 * which this {@link BasicExplorerContentProvider content provider} is used) may override this method and return any
	 * {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter factory} will
	 * then be returned as result by {@link #getAdapterFactory(TransactionalEditingDomain)}.
	 * </p>
	 * 
	 * @return The custom {@link AdapterFactory adapter factory} that is to be used by this
	 *         {@link BasicExplorerContentProvider content provider}. <code>null</code> the default
	 *         {@link AdapterFactory adapter factory} returned by {@link #getAdapterFactory(TransactionalEditingDomain)}
	 *         should be used instead.
	 * @see #getAdapterFactory(TransactionalEditingDomain)
	 */
	protected AdapterFactory getCustomAdapterFactory() {
		return null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public Object[] getChildren(Object parentElement) {
		ArrayList<Object> children = new ArrayList<Object>();
		try {
			// Is parent element a workspace resource?
			if (parentElement instanceof IResource) {
				// Retrieve model resource behind the workspace resource handed in as parent element
				Resource modelResource = getModelResource((IResource) parentElement);

				// Get corresponding model content provider
				AdapterFactoryContentProvider contentProvider = getModelContentProvider(modelResource);
				if (contentProvider != null) {
					// Set model resource as model content provider input
					contentProvider.inputChanged(viewer, null, modelResource);

					// Determine the model content roots, i.e., the actual parent objects of the model objects to be
					// displayed as virtual children of the workspace resource
					for (Object modelContentRoot : getModelContentRoots(modelResource)) {
						// Retrieve children of current parent object
						children.addAll(Arrays.asList(contentProvider.getChildren(modelContentRoot)));
					}
				}
			}

			// Assume that parent element is a model object
			else {
				// Try to obtain corresponding model content provider
				AdapterFactoryContentProvider contentProvider = getModelContentProvider(parentElement);
				if (contentProvider != null) {
					// Retrieve children of specified parent element
					children.addAll(Arrays.asList(contentProvider.getChildren(parentElement)));
				}
			}
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return children.toArray(new Object[children.size()]);
	}

	public Object getParent(Object element) {
		Object parent = null;
		AdapterFactoryContentProvider contentProvider = getModelContentProvider(element);
		if (contentProvider != null) {
			parent = contentProvider.getParent(element);
		}

		// Is parent element a model content root?
		Resource modelResource = EcoreResourceUtil.getResource(parent);
		List<Object> modelContentRoots = getModelContentRoots(modelResource);
		if (modelContentRoots.contains(parent)) {
			// Return corresponding workspace resource
			return getWorkspaceResource(modelResource);
		}
		return parent;
	}

	public void dispose() {
		for (TransactionalEditingDomain editingDomain : modelContentProviders.keySet()) {
			removeTransactionalEditingDomainListeners(editingDomain);
			AdapterFactoryContentProvider modelContentProvider = modelContentProviders.get(editingDomain);
			modelContentProvider.dispose();
		}
		modelContentProviders.clear();
	}

	protected void addTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
		Assert.isNotNull(editingDomain);

		if (resourceChangedListener == null) {
			resourceChangedListener = createResourceChangedListener();
			Assert.isNotNull(resourceChangedListener);
		}
		editingDomain.addResourceSetListener(resourceChangedListener);

		if (resourceMovedListener == null) {
			resourceMovedListener = createResourceMovedListener();
			Assert.isNotNull(resourceMovedListener);
		}
		editingDomain.addResourceSetListener(resourceMovedListener);

		if (nonContainmentReferenceChangeListener == null) {
			nonContainmentReferenceChangeListener = createNonContainmentReferenceChangeListener();
			Assert.isNotNull(nonContainmentReferenceChangeListener);
		}
		editingDomain.addResourceSetListener(nonContainmentReferenceChangeListener);

		if (modelContentRootChangeListener == null) {
			modelContentRootChangeListener = createModelContentRootChangeListener();
			Assert.isNotNull(modelContentRootChangeListener);
		}
		editingDomain.addResourceSetListener(modelContentRootChangeListener);
	}

	protected void removeTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
		Assert.isNotNull(editingDomain);

		if (resourceChangedListener != null) {
			editingDomain.removeResourceSetListener(resourceChangedListener);
		}
		if (resourceMovedListener != null) {
			editingDomain.removeResourceSetListener(resourceMovedListener);
		}
		if (nonContainmentReferenceChangeListener != null) {
			editingDomain.removeResourceSetListener(nonContainmentReferenceChangeListener);
		}
		if (modelContentRootChangeListener != null) {
			editingDomain.removeResourceSetListener(modelContentRootChangeListener);
		}
	}

	/**
	 * Creates a ResourceSetChangedListener that detects (re-)loaded resources resources and refreshes their parent(s).
	 */
	protected ResourceSetListener createResourceChangedListener() {
		return new ResourceSetListenerImpl(NotificationFilter
				.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__IS_LOADED).or(
						NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResourceSet(), ResourceSet.RESOURCE_SET__RESOURCES))) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				Set<Resource> loadedResources = new HashSet<Resource>();
				Set<Resource> unloadedResources = new HashSet<Resource>();
				Set<Resource> addedResources = new HashSet<Resource>();
				Set<Resource> removedResources = new HashSet<Resource>();
				Set<Resource> changedResources = new HashSet<Resource>();

				// Analyze notifications for changed resources; record only loaded and unloaded or added and removed
				// resources which have not got unloaded/loaded or removed/added again later on
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
				changedResources.addAll(loadedResources);
				changedResources.addAll(addedResources);
				changedResources.addAll(unloadedResources);
				changedResources.addAll(removedResources);

				// Handle changed resources
				refreshViewerOnModelResources(changedResources);
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
				Set<Resource> movedResources = new HashSet<Resource>();
				for (Notification notification : event.getNotifications()) {
					movedResources.add((Resource) notification.getNotifier());
				}
				refreshViewerOnModelResources(movedResources);
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected ResourceSetListener createNonContainmentReferenceChangeListener() {
		return new ResourceSetListenerImpl(NotificationFilter.createNotifierTypeFilter(EObject.class)) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				Set<EObject> objectsToRefresh = new HashSet<EObject>();
				for (Notification notification : event.getNotifications()) {
					EObject object = (EObject) notification.getNotifier();
					if (notification.getFeature() instanceof EReference) {
						EReference reference = (EReference) notification.getFeature();
						if (!reference.isContainment() && !reference.isContainer() && reference.getEType() instanceof EClass) {
							objectsToRefresh.add(object);
						}
					}
				}
				refreshViewerOnModelObjects(objectsToRefresh);
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	protected ResourceSetListener createModelContentRootChangeListener() {
		return new ResourceSetListenerImpl(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__CONTENTS)
				.or(NotificationFilter.createNotifierTypeFilter(EObject.class))) {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				Set<EObject> addedValues = new HashSet<EObject>();
				Set<EObject> removedValues = new HashSet<EObject>();
				Set<EObject> changedValues = new HashSet<EObject>();

				// Analyze notifications for changed values; record only added and removed values which have not got
				// removed/added again later on
				for (Notification notification : event.getNotifications()) {
					Object notifier = notification.getNotifier();
					if (notifier instanceof Resource || notification.getFeature() instanceof EReference) {
						if (notification.getEventType() == Notification.ADD || notification.getEventType() == Notification.ADD_MANY) {
							List<EObject> newValues = new ArrayList<EObject>();
							Object newValue = notification.getNewValue();
							if (newValue instanceof List<?>) {
								@SuppressWarnings("unchecked")
								List<EObject> newValueList = (List<EObject>) newValue;
								newValues.addAll(newValueList);
							} else if (newValue instanceof EObject) {
								newValues.add((EObject) newValue);
							}

							for (EObject value : newValues) {
								if (removedValues.contains(value)) {
									removedValues.remove(value);
								} else {
									addedValues.add(value);
								}
							}
						} else if (notification.getEventType() == Notification.REMOVE || notification.getEventType() == Notification.REMOVE_MANY) {
							List<EObject> oldValues = new ArrayList<EObject>();
							Object oldValue = notification.getOldValue();
							if (oldValue instanceof List<?>) {
								@SuppressWarnings("unchecked")
								List<EObject> oldValueList = (List<EObject>) oldValue;
								oldValues.addAll(oldValueList);
							} else if (oldValue instanceof Resource) {
								oldValues.add((EObject) oldValue);
							}

							for (EObject value : oldValues) {
								if (addedValues.contains(value)) {
									addedValues.remove(value);
								} else {
									removedValues.add(value);
								}
							}
						}
					}

				}
				changedValues.addAll(addedValues);
				changedValues.addAll(removedValues);

				// Explicitly refresh parents of changed values in case that they are workspace resources. This is
				// necessary because viewer refreshes triggered by the model content provider only affect the model
				// content roots behind the workspace resources. The latter are not represented by any tree item
				// in the viewer and so these refresh attempts have no effect.
				Set<IResource> resourcesToRefresh = new HashSet<IResource>();
				for (EObject changedObject : changedValues) {
					Object changedObjectParent = getParent(changedObject);
					if (changedObjectParent instanceof IResource) {
						resourcesToRefresh.add((IResource) changedObjectParent);
					}
				}
				refreshViewerOnWorkspaceResources(resourcesToRefresh);
			}

			@Override
			public boolean isPostcommitOnly() {
				return true;
			}
		};
	}

	/**
	 * Refreshes viewer on specified workspace resources.
	 * 
	 * @param resources
	 */
	protected void refreshViewerOnWorkspaceResources(Set<? extends IResource> resources) {
		if (!resources.isEmpty()) {
			/*
			 * Performance optimization: Perform refresh on a per resource basis only if number of resources is
			 * reasonably low.
			 */
			if (resources.size() < LIMIT_INDIVIDUAL_RESOURCES_REFRESH) {
				for (IResource resource : resources) {
					if (resource != null && resource.isAccessible()) {
						if (isTriggerPoint(resource)) {
							refreshViewerOnObject(resource);
						}
					}
				}
			} else {
				// Perform a full viewer refresh otherwise
				refreshViewer();
			}
		}
	}

	/**
	 * Refreshes viewer on specified model resources.
	 * 
	 * @param resources
	 */
	protected void refreshViewerOnModelResources(Set<? extends Resource> resources) {
		if (!resources.isEmpty()) {
			/*
			 * Performance optimization: Perform refresh on a per resource basis only if number of resources is
			 * reasonably low.
			 */
			if (resources.size() < LIMIT_INDIVIDUAL_RESOURCES_REFRESH) {
				Set<IFile> files = new HashSet<IFile>();
				/*
				 * !! Important Note !! Retrieve complete set of workspace files behind model resources first and then
				 * perform refresh on these files rather than retrieving file from one model resource refreshing it and
				 * proceeding with the next. There might exist multiple model resources for the same workspace file and
				 * if so we would needlessly encounter multiple refreshes of the same file otherwise.
				 */
				for (Resource resource : resources) {
					IFile file = EcorePlatformUtil.getFile(resource);
					if (file != null && file.isAccessible()) {
						/*
						 * !! Important Note !! Refresh viewer if file behind resource matches trigger point condition.
						 * Refresh viewer regardless of that if resource has just been unloaded because the underlying
						 * file might not match the trigger condition anymore in this case (e.g. if the file's XML
						 * namespace or content type has been changed)
						 */
						if (isTriggerPoint(file) || !resource.isLoaded()) {
							files.add(file);
						}
					}
				}
				for (IFile file : files) {
					refreshViewerOnObject(file);
				}
			} else {
				// Perform a full viewer refresh otherwise
				refreshViewer();
			}
		}
	}

	/**
	 * Refreshes viewer on specified model objects.
	 * 
	 * @param objects
	 */
	protected void refreshViewerOnModelObjects(Set<?> objects) {
		if (!objects.isEmpty()) {
			/*
			 * Performance optimization: Perform refresh on a per object basis only if number of objects is reasonably
			 * low.
			 */
			if (objects.size() < LIMIT_INDIVIDUAL_OBJECTS_REFRESH) {
				for (Object object : objects) {
					if (isPossibleChild(object)) {
						// Is current object a model content root?
						Resource modelResource = EcoreResourceUtil.getResource(object);
						List<Object> modelContentRoots = getModelContentRoots(modelResource);
						if (modelContentRoots.contains(object)) {
							// Refresh corresponding workspace resource
							IResource resource = getWorkspaceResource(modelResource);
							if (resource != null && resource.isAccessible()) {
								if (isTriggerPoint(resource)) {
									refreshViewerOnObject(resource);
								}
							}
						} else {
							// Directly refresh the object
							refreshViewerOnObject(object);
						}
					}
				}
			} else {
				// Perform a full viewer refresh otherwise
				refreshViewer();
			}
		}
	}

	/**
	 * Refreshes viewer on specified object.
	 * 
	 * @param object
	 */
	protected void refreshViewerOnObject(final Object object) {
		if (object != null) {
			if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
							if (viewer instanceof StructuredViewer) {
								StructuredViewer stucturedViewer = (StructuredViewer) viewer;
								stucturedViewer.refresh(object, true);
							}
						}
					}
				});
			}
		}
	}

	/**
	 * Refreshes viewer completely.
	 */
	protected void refreshViewer() {
		if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
						if (viewer instanceof StructuredViewer) {
							StructuredViewer stucturedViewer = (StructuredViewer) viewer;
							stucturedViewer.refresh();
						}
					}
				}
			});
		}
	}
}
