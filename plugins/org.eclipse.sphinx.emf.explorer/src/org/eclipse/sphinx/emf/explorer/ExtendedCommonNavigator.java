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
 *     itemis - [420520] Model explorer view state not restored completely when affected model objects are added lately
 *     
 * </copyright>
 */
package org.eclipse.sphinx.emf.explorer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.EMFCommandOperation;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.domain.factory.EditingDomainFactoryListenerRegistry;
import org.eclipse.sphinx.emf.domain.factory.ITransactionalEditingDomainFactoryListener;
import org.eclipse.sphinx.emf.explorer.internal.Activator;
import org.eclipse.sphinx.emf.messages.EMFMessages;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.domain.WorkspaceEditingDomainManager;
import org.eclipse.sphinx.emf.workspace.internal.saving.ModelSavingPerformanceStats;
import org.eclipse.sphinx.emf.workspace.internal.saving.ModelSavingPerformanceStats.ModelEvent;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider.SiteNotifyingSaveablesLifecycleListener;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.messages.PlatformMessages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.SaveablesLifecycleEvent;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.SaveablesProvider;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Extends the behavior of the Eclipse {@linkplain CommonNavigator Common Navigator}.
 */
@SuppressWarnings("restriction")
public class ExtendedCommonNavigator extends CommonNavigator implements ITabbedPropertySheetPageContributor, IViewerProvider,
		ITransactionalEditingDomainFactoryListener {

	private static final String TAG_SELECTED = Activator.getPlugin().getSymbolicName() + ".selection"; //$NON-NLS-1$
	private static final String TAG_EXPANDED = Activator.getPlugin().getSymbolicName() + ".expanded"; //$NON-NLS-1$
	private static final String TAG_ELEMENT = Activator.getPlugin().getSymbolicName() + ".element"; //$NON-NLS-1$
	private static final String TAG_PATH = Activator.getPlugin().getSymbolicName() + ".path"; //$NON-NLS-1$
	private static final String TAG_URI = Activator.getPlugin().getSymbolicName() + ".uri"; //$NON-NLS-1$

	private IOperationHistoryListener affectedObjectsListener;
	private IResourceChangeListener resourceMarkerChangeListener;

	protected Set<IPropertySheetPage> propertySheetPages = new HashSet<IPropertySheetPage>();
	protected IUndoContext undoContext;

	protected SaveablesProvider modelSaveablesProvider;

	protected IPartListener partListener = new IPartListener() {

		@Override
		public void partActivated(IWorkbenchPart part) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part instanceof PropertySheet) {
				((PropertySheet) part).getCurrentPage().dispose();
				propertySheetPages.remove(((PropertySheet) part).getCurrentPage());
			}
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
		}

	};

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (modelSaveablesProvider == null) {
			modelSaveablesProvider = createModelSaveablesProvider();

			ISaveablesLifecycleListener modelSaveablesLifecycleListener = createModelSaveablesLifecycleListener();
			modelSaveablesProvider.init(modelSaveablesLifecycleListener);
		}

		site.getPage().addPartListener(partListener);

		// Create one ResourceListener that detects objects changed by EMF commands
		affectedObjectsListener = createAffectedObjectsListener();
		Assert.isNotNull(affectedObjectsListener);

		// Register this as an Editing Domain Factory Listener (dynamically)
		EditingDomainFactoryListenerRegistry.INSTANCE.addListener(MetaModelDescriptorRegistry.ANY_MM, null, this, null);

		// Register listener on already created editing domains
		for (TransactionalEditingDomain editingDomain : WorkspaceEditingDomainManager.INSTANCE.getEditingDomainMapping().getEditingDomains()) {
			// Register the ResourceListener that detects objects changed by EMF commands
			WorkspaceTransactionUtil.getOperationHistory(editingDomain).addOperationHistoryListener(affectedObjectsListener);
		}

		// Create and register IResourceChangeListener that detects resource maker changes
		if (resourceMarkerChangeListener == null) {
			resourceMarkerChangeListener = createResourceMarkerChangeListener();
			ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceMarkerChangeListener, IResourceChangeEvent.POST_CHANGE);
		}
	}

	@Override
	protected CommonViewer createCommonViewerObject(Composite aParent) {
		return new CommonViewer(getViewSite().getId(), aParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL) {
			@Override
			public void refresh() {
				super.refresh();
				restoreState(memento);
			}

			@Override
			public void refresh(Object element, boolean updateLabels) {
				super.refresh(element, updateLabels);
				restoreState(memento);
			}
		};
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// Give action providers a chance to initialize retargetable actions
		/*
		 * !! Important Note !! It is crucial to do this as early as possible because the connected
		 * TabbedPropertySheetPage will try to retrieve the retargetable undo and redo actions from this view part's
		 * action bars and set the same on its own action bars (see TabbedPropertySheetPage#setActionBars() for
		 * details). A failure to so will entail that the undo/redo actions in the Edit menu remain grayed out.
		 */
		getNavigatorActionService().fillActionBars(getViewSite().getActionBars());

		// Perform a view part level restore state
		/*
		 * !! Important Note !! CommonNavigator#createPartControl() also invokes restoreState() but only via the
		 * navigator content service. This entails that restoreState() is eventually only invoked on the subset of
		 * common content providers which are currently active according to the current selection and the triggerPoints
		 * condition on the org.eclipse.ui.navigator.navigatorContent/navigatorContent contribution. In contrast to
		 * that, the additional view part level restoreState() invocation implemented here takes always place and does
		 * not depend on the activation state of contributed common content providers.
		 */
		restoreState(memento);
	}

	@Override
	public boolean isDirty() {
		Saveable[] saveables = getActiveSaveables();
		for (Saveable element : saveables) {
			if (element.isDirty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Saveable[] getSaveables() {
		Set<Saveable> saveables = new HashSet<Saveable>(Arrays.asList(super.getSaveables()));
		if (modelSaveablesProvider != null) {
			saveables.addAll(Arrays.asList(modelSaveablesProvider.getSaveables()));
		}
		return saveables.toArray(new Saveable[saveables.size()]);
	}

	@Override
	public Saveable[] getActiveSaveables() {
		String contextGetActiveSaveables = ModelSavingPerformanceStats.ModelContext.CONTEXT_GET_ACTIVE_SAVEABLES.getName()
				+ this.getClass().getSimpleName();
		ModelEvent modelEvent = ModelSavingPerformanceStats.ModelEvent.EVENT_GET_SAVEABLE;

		// Open profiling context
		ModelSavingPerformanceStats.INSTANCE.openContext(contextGetActiveSaveables);
		ModelSavingPerformanceStats.INSTANCE.startNewEvent(modelEvent, this.getClass().getSimpleName());

		Set<Saveable> saveables = new HashSet<Saveable>(Arrays.asList(super.getActiveSaveables()));
		if (modelSaveablesProvider != null) {
			CommonViewer viewer = getCommonViewer();
			if (viewer != null) {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					for (Object selected : selection.toList()) {
						if (selected instanceof IContainer) {
							IContainer container = (IContainer) selected;
							Collection<IModelDescriptor> modelDescriptors = ModelDescriptorRegistry.INSTANCE.getModels(container);
							for (IModelDescriptor modelDescriptor : modelDescriptors) {
								Saveable saveable = modelSaveablesProvider.getSaveable(modelDescriptor);
								if (saveable != null) {
									saveables.add(saveable);
								}
							}
						} else {
							Saveable saveable = modelSaveablesProvider.getSaveable(selected);
							if (saveable != null) {
								saveables.add(saveable);
							}
						}
					}
				}
			} else {
				saveables.addAll(Arrays.asList(modelSaveablesProvider.getSaveables()));
			}
		}

		// Close and log profiling context;
		ModelSavingPerformanceStats.INSTANCE.endEvent(modelEvent, this.getClass().getSimpleName());
		ModelSavingPerformanceStats.INSTANCE.closeAndLogContext(contextGetActiveSaveables);
		return saveables.toArray(new Saveable[saveables.size()]);
	}

	@Override
	public String getContributorId() {
		return getViewSite().getId();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IPropertySheetPage.class == adapter) {
			return getPropertySheetPage();
		} else if (adapter == IUndoContext.class) {
			return getUndoContext();
		}
		return super.getAdapter(adapter);
	}

	/*
	 * @see org.eclipse.emf.common.ui.viewer.IViewerProvider#getViewer()
	 */
	@Override
	public Viewer getViewer() {
		return getCommonViewer();
	}

	@Override
	public void dispose() {
		if (modelSaveablesProvider != null) {
			modelSaveablesProvider.dispose();
		}
		modelSaveablesProvider = null;

		getSite().getPage().removePartListener(partListener);
		for (IPropertySheetPage propertySheetPage : propertySheetPages) {
			propertySheetPage.dispose();
		}
		propertySheetPages.clear();

		if (affectedObjectsListener != null) {
			for (TransactionalEditingDomain editingDomain : WorkspaceEditingDomainManager.INSTANCE.getEditingDomainMapping().getEditingDomains()) {
				WorkspaceTransactionUtil.getOperationHistory(editingDomain).removeOperationHistoryListener(affectedObjectsListener);
			}
			// Unregister this as an Editing Domain Factory Listener (dynamically)
			EditingDomainFactoryListenerRegistry.INSTANCE.removeListener(this);
		}
		affectedObjectsListener = null;

		if (resourceMarkerChangeListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceMarkerChangeListener);
		}
		resourceMarkerChangeListener = null;

		super.dispose();
	}

	public void restoreState(IMemento memento) {
		final CommonViewer viewer = getCommonViewer();
		if (viewer == null || memento == null) {
			return;
		}

		XMLMemento deferredMemento = XMLMemento.createWriteRoot(memento.getType());

		// Retrieve visible expanded elements to be restored
		final List<Object> expandedElements = new ArrayList<Object>();
		XMLMemento deferredExpandedMemento = (XMLMemento) deferredMemento.createChild(TAG_EXPANDED);
		IMemento expandedMemento = memento.getChild(TAG_EXPANDED);
		if (expandedMemento != null) {
			for (IMemento elementMemento : expandedMemento.getChildren(TAG_ELEMENT)) {

				String path = elementMemento.getString(TAG_PATH);
				if (path != null) {
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					if (resource instanceof IFile) {
						IFile file = (IFile) resource;
						if (EcorePlatformUtil.isFileLoaded(file)) {
							expandedElements.add(resource);
						} else {
							IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel((IFile) resource);
							if (modelDescriptor != null) {
								// Request asynchronous loading of model behind given workspace file
								ModelLoadManager.INSTANCE.loadModel(modelDescriptor, true, null);
							}
							deferredExpandedMemento.copyChild(elementMemento);
						}
					} else if (resource != null) {
						expandedElements.add(resource);
					}
					continue;
				}

				String uriString = elementMemento.getString(TAG_URI);
				if (uriString != null) {
					URI uri = URI.createURI(uriString, true);
					if (EcoreResourceUtil.exists(uri)) {
						EObject eObject = EcorePlatformUtil.getEObject(uri);
						if (eObject != null) {
							expandedElements.add(eObject);
						} else {
							deferredExpandedMemento.copyChild(elementMemento);
						}
					}
				}
			}
		}

		// Retrieve selected element(s) to be restored
		final List<Object> selectedElements = new ArrayList<Object>();
		XMLMemento deferredSelectedMemento = (XMLMemento) deferredMemento.createChild(TAG_SELECTED);
		IMemento selectedMemento = memento.getChild(TAG_SELECTED);
		if (selectedMemento != null) {
			for (IMemento elementMemento : selectedMemento.getChildren(TAG_ELEMENT)) {

				String uriString = elementMemento.getString(TAG_URI);
				if (uriString != null) {
					URI uri = URI.createURI(uriString, true);
					EObject eObject = EcorePlatformUtil.getEObject(uri);
					if (eObject != null) {
						selectedElements.add(eObject);
					} else {
						deferredSelectedMemento.copyChild(elementMemento);
					}
				}
			}
		}

		// Perform restoration of expanded elements and selection asynchronously within UI thread
		if (!expandedElements.isEmpty() || !selectedElements.isEmpty()) {
			if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				Display display = viewer.getControl().getDisplay();
				if (display != null) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
								for (Object element : expandedElements) {
									viewer.setExpandedState(element, true);
								}

								if (!selectedElements.isEmpty()) {
									viewer.setSelection(new StructuredSelection(selectedElements));
								}
							}
						}
					});
				}
			}
		}

		// Switch root memento to deferred mementos if any such are still around or null otherwise
		if (deferredExpandedMemento.getChildren().length > 0 || deferredSelectedMemento.getChildren().length > 0) {
			this.memento = deferredMemento;
		} else {
			this.memento = null;
		}
	}

	@Override
	public void saveState(IMemento memento) {
		CommonViewer viewer = getCommonViewer();
		if (viewer != null && memento != null) {
			// Save visible expanded elements
			Object expandedElements[] = viewer.getVisibleExpandedElements();
			if (expandedElements.length > 0) {
				IMemento expandedMemento = memento.createChild(TAG_EXPANDED);
				for (Object expandedElement : expandedElements) {
					if (expandedElement instanceof IResource) {
						IPath path = ((IResource) expandedElement).getFullPath();
						IMemento elementMemento = expandedMemento.createChild(TAG_ELEMENT);
						elementMemento.putString(TAG_PATH, path.toString());
					}
					if (expandedElement instanceof EObject) {
						URI uri = getURI((EObject) expandedElement);
						if (uri != null) {
							IMemento elementMemento = expandedMemento.createChild(TAG_ELEMENT);
							elementMemento.putString(TAG_URI, uri.toString());
						}
					}
				}
			}

			// Save selection
			Object selectedElements[] = ((IStructuredSelection) viewer.getSelection()).toArray();
			if (selectedElements.length > 0) {
				IMemento selectedMemento = memento.createChild(TAG_SELECTED);
				for (Object selectedElement : selectedElements) {
					if (selectedElement instanceof EObject) {
						URI uri = getURI((EObject) selectedElement);
						if (uri != null) {
							IMemento elementMemento = selectedMemento.createChild(TAG_ELEMENT);
							elementMemento.putString(TAG_URI, uri.toString());
						}
					}
				}
			}
		}

		super.saveState(memento);
	}

	@Override
	public boolean show(ShowInContext context) {
		Object input = context.getInput();
		if (input != null) {
			List<IMarker> markers = retrieveRealMarkerSelection(input);
			List<Object> objects = new ArrayList<Object>();
			for (IMarker marker : markers) {
				Object object = null;
				String uriAttribute = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
				if (uriAttribute != null) {
					object = EcorePlatformUtil.getEObject(URI.createURI(uriAttribute, true));
				}
				if (object != null) {
					objects.add(object);
				}
			}
			if (objects.size() > 0) {
				try {
					StructuredSelection selection = new StructuredSelection(objects);
					selectReveal(selection);
				} catch (RuntimeException ex) {
					// Ignore exception, just return false
					return false;
				}
				return true;
			}
		}
		try {
			return super.show(context);
		} catch (RuntimeException ex) {
			// Ignore exception, just return false
			return false;
		}
	}

	protected List<IMarker> retrieveRealMarkerSelection(Object input) {
		if (input.getClass().toString().indexOf("MarkerAdapter") >= 0) { //$NON-NLS-1$
			try {
				// TODO Switch to using RelectUtil and eliminate call to Class#getDeclaredField()
				Field declaredField = input.getClass().getDeclaredField("view"); //$NON-NLS-1$
				if (declaredField != null) {
					boolean oldAccessible = declaredField.isAccessible();
					declaredField.setAccessible(true);
					Object object = declaredField.get(input);
					declaredField.setAccessible(oldAccessible);
					// TODO Switch to using RelectUtil and eliminate #getMethod()
					Method getViewer = getMethod(object.getClass(), "getViewer"); //$NON-NLS-1$
					if (getViewer == null) {
						return Collections.emptyList();
					}
					oldAccessible = getViewer.isAccessible();
					getViewer.setAccessible(true);
					Object viewer = getViewer.invoke(object);
					getViewer.setAccessible(oldAccessible);

					if (viewer instanceof StructuredViewer) {
						ISelection selection = ((StructuredViewer) viewer).getSelection();
						ArrayList<IMarker> markers = new ArrayList<IMarker>();
						if (selection instanceof IStructuredSelection) {
							Iterator<?> i = ((IStructuredSelection) selection).iterator();
							while (i.hasNext()) {
								Object next = i.next();
								if (next instanceof IMarker) {
									markers.add((IMarker) next);
								} else if (next.getClass().toString().indexOf("Marker") >= 0) { //$NON-NLS-1$
									// TODO Switch to using RelectUtil and eliminate #getMethod()
									Method getMarker = getMethod(next.getClass(), "getMarker"); //$NON-NLS-1$
									if (getMarker == null) {
										break;
									}
									oldAccessible = getMarker.isAccessible();
									getMarker.setAccessible(true);
									Object marker = getMarker.invoke(next);
									getMarker.setAccessible(oldAccessible);
									markers.add((IMarker) marker);
								}

							}
						}
						return markers;
					}
				}
			} catch (SecurityException ex) {
			} catch (NoSuchFieldException ex) {
			} catch (IllegalArgumentException ex) {
			} catch (IllegalAccessException ex) {
			} catch (NoSuchMethodException ex) {
			} catch (InvocationTargetException ex) {
			}
		}
		return Collections.emptyList();
	}

	/**
	 * @param object
	 * @return
	 * @throws NoSuchMethodException
	 * @throws NoSuchMethodException
	 */
	protected Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
		if (clazz == null) {
			throw new NoSuchMethodException(methodName);
		}
		Method declaredMethod = null;
		try {
			declaredMethod = clazz.getDeclaredMethod(methodName);
		} catch (SecurityException ex) {
		} catch (NoSuchMethodException ex) {
			return getMethod(clazz.getSuperclass(), methodName);
		}
		return declaredMethod;
	}

	protected Collection<TransactionalEditingDomain> getEditingDomainsFromSelection() {
		// Editing domains to return
		Set<TransactionalEditingDomain> editingDomains = new HashSet<TransactionalEditingDomain>();

		CommonViewer viewer = getCommonViewer();
		if (viewer != null) {
			ISelection selection = viewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				for (Object selected : ((IStructuredSelection) selection).toList()) {
					if (selected instanceof EObject && ((EObject) selected).eIsProxy()) {
						PlatformLogUtil.logAsWarning(Activator.getPlugin(),
								new RuntimeException(NLS.bind(EMFMessages.warning_selectionContainsUnresolvedModelElement, selected)));
					} else if (selected instanceof IProject || selected instanceof IFolder) {
						editingDomains.addAll(WorkspaceEditingDomainUtil.getEditingDomains((IContainer) selected));
					} else {
						TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(selected);
						if (editingDomain != null) {
							editingDomains.add(editingDomain);
						}
					}
				}
			}
		}
		return editingDomains;
	}

	protected URI getURI(final EObject eObject) {
		if (eObject != null) {
			final TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(eObject);
			if (editingDomain != null) {
				try {
					return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<URI>() {
						@Override
						public void run() {
							setResult(EcoreUtil.getURI(eObject));
						}
					});
				} catch (InterruptedException ex) {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
				}
			}
		}
		return null;
	}

	/**
	 * This creates a new property sheet page instance and manages it in the cache.
	 */
	public IPropertySheetPage getPropertySheetPage() {
		IPropertySheetPage propertySheetPage = new TabbedPropertySheetPage(this);
		propertySheetPages.add(propertySheetPage);
		return propertySheetPage;
	}

	protected IUndoContext getUndoContext() {
		if (undoContext == null) {
			undoContext = new ObjectUndoContext(this, getContributorId() + ".context"); //$NON-NLS-1$
		}
		return undoContext;
	}

	protected boolean isActivePart() {
		IWorkbenchPartSite site = getSite();
		return site != null ? this == site.getWorkbenchWindow().getPartService().getActivePart() : false;
	}

	protected boolean isMyActivePropertySheetPage() {
		IWorkbenchPartSite site = getSite();
		if (site != null) {
			IWorkbenchPart activePart = site.getWorkbenchWindow().getPartService().getActivePart();
			if (activePart instanceof PropertySheet) {
				return propertySheetPages.contains(((PropertySheet) activePart).getCurrentPage());
			}
		}
		return false;
	}

	protected SaveablesProvider createModelSaveablesProvider() {
		return new BasicModelSaveablesProvider();
	}

	/**
	 * Creates an {@link IResourceChangeListener} in order to wake up decoration of IContainer and IResource.
	 */
	protected IResourceChangeListener createResourceMarkerChangeListener() {
		return new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Assert.isNotNull(event);

				IMarkerDelta[] markerDelta = event.findMarkerDeltas(IMarker.PROBLEM, true);
				if (markerDelta != null && markerDelta.length > 0) {
					UIJob job = new UIJob(PlatformMessages.job_updatingLabelDecoration) {
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							updateLabelDecoration();
							return Status.OK_STATUS;
						}

						@Override
						public boolean belongsTo(Object family) {
							return IExtendedPlatformConstants.FAMILY_LABEL_DECORATION.equals(family);
						}
					};

					/*
					 * !! Important Note !! Schedule updating label decoration job only if no such is already underway
					 * because running multiple label decoration updates concurrently causes deadlocks.
					 */
					if (Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_LABEL_DECORATION).length == 0) {
						job.setPriority(Job.BUILD);
						job.setSystem(true);
						job.schedule();
					}
				}
			}
		};
	}

	protected IOperationHistoryListener createAffectedObjectsListener() {
		return new IOperationHistoryListener() {
			@Override
			public void historyNotification(final OperationHistoryEvent event) {
				if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
					handleOperationAboutToExecute(event.getOperation());
				} else if (event.getEventType() == OperationHistoryEvent.DONE || event.getEventType() == OperationHistoryEvent.UNDONE
						|| event.getEventType() == OperationHistoryEvent.REDONE) {
					handleOperationFinished(event.getOperation());
				}
			}

			private void handleOperationAboutToExecute(final IUndoableOperation operation) {
				if (operation.canUndo()) {
					IWorkbenchPartSite site = getSite();
					if (site != null) {
						site.getShell().getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								if (isActivePart() || isMyActivePropertySheetPage()) {
									for (TransactionalEditingDomain editingDomain : getEditingDomainsFromSelection()) {
										// Remove default undo context and add the global undo context
										CommandStack commandStack = editingDomain.getCommandStack();
										if (commandStack instanceof IWorkspaceCommandStack) {
											IUndoContext undoContext = ((IWorkspaceCommandStack) commandStack).getDefaultUndoContext();
											if (undoContext != null) {
												operation.removeContext(undoContext);
											}
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
				if (site != null) {
					site.getShell().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							// Try to show the affected objects in viewer
							if (operation instanceof EMFCommandOperation) {
								Command command = ((EMFCommandOperation) operation).getCommand();
								if (command != null) {
									Collection<?> affectedObjects = command.getAffectedObjects();
									if (affectedObjects.size() > 0) {
										try {
											ISelection selection = new StructuredSelection(affectedObjects.toArray());
											selectReveal(selection);
										} catch (RuntimeException ex) {
											// Ignore exception
										}
									}
								}
							}
						}
					});
				}
			}
		};
	}

	/**
	 * @return
	 */
	protected ISaveablesLifecycleListener createModelSaveablesLifecycleListener() {
		return new SiteNotifyingSaveablesLifecycleListener(this) {
			@Override
			public void handleLifecycleEvent(SaveablesLifecycleEvent event) {
				super.handleLifecycleEvent(event);

				if (event.getEventType() == SaveablesLifecycleEvent.DIRTY_CHANGED) {
					firePropertyChange(PROP_DIRTY);
				}
			}
		};
	}

	protected void updateLabelDecoration() {
		// Update validation label decoration
		PlatformUI.getWorkbench().getDecoratorManager().update("org.eclipse.sphinx.emf.validation.ui.decorator"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postCreateEditingDomain(TransactionalEditingDomain editingDomain) {
		// Register the ResourceListener that detects objects changed by EMF commands
		WorkspaceTransactionUtil.getOperationHistory(editingDomain).addOperationHistoryListener(affectedObjectsListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preDisposeEditingDomain(TransactionalEditingDomain editingDomain) {
		if (affectedObjectsListener != null) {
			WorkspaceTransactionUtil.getOperationHistory(editingDomain).removeOperationHistoryListener(affectedObjectsListener);
		}
	}
}
