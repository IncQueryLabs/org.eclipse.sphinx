/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [393479] Enable BasicTabbedPropertySheetTitleProvider to retrieve same AdapterFactory as underlying IWorkbenchPart is using
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.editors.forms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.EMFCommandOperation;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.ResourceUndoContext;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.editors.forms.internal.Activator;
import org.eclipse.sphinx.emf.editors.forms.internal.DefaultSaveable;
import org.eclipse.sphinx.emf.editors.forms.internal.messages.Messages;
import org.eclipse.sphinx.emf.editors.forms.pages.GenericContentsTreePage;
import org.eclipse.sphinx.emf.editors.forms.pages.MessagePage;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.ui.util.EcoreUIUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.domain.WorkspaceEditingDomainManager;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider.SiteNotifyingSaveablesLifecycleListener;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.SaveablesLifecycleEvent;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.navigator.SaveablesProvider;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * A basic Eclipse Forms-based model editor.
 */
public class BasicTransactionalFormEditor extends FormEditor implements IEditingDomainProvider, ISelectionProvider, IMenuListener, IViewerProvider,
		IGotoMarker, IPersistableEditor, ITabbedPropertySheetPageContributor, ISaveablesSource {

	private static final String TAG_EDITOR_DIRTY_ON_WORKBENCH_CLOSE = "editorDirtyOnWorkbenchClose"; //$NON-NLS-1$

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

	/**
	 * This is the content outline page.
	 */
	protected IContentOutlinePage contentOutlinePage;

	/**
	 * This is a kludge...
	 */
	protected IStatusLineManager contentOutlineStatusLineManager;

	/**
	 * This is the content outline page's viewer.
	 */
	protected TreeViewer contentOutlineViewer;

	/**
	 * This is collection of the property sheet pages.
	 */
	protected Set<IPropertySheetPage> propertySheetPages = new HashSet<IPropertySheetPage>();

	/**
	 * This is the selection provider that shadows the selection in the content outline. The parent relation must be
	 * correctly defined for this to work.
	 */
	protected ISelectionProvider selectionProvider;

	/**
	 * This keeps track of the active selection provider, which may be either one of the viewers in the pages or the
	 * content outline viewer.
	 */
	protected ISelectionProvider currentSelectionProvider;

	/**
	 * This listens to which ever viewer is active.
	 */
	protected ISelectionChangedListener selectionChangedListener;

	/**
	 * This keeps track of all the {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to
	 * this editor.
	 */
	protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * This keeps track of the selection of the editor as a whole.
	 */
	protected ISelection editorSelection = StructuredSelection.EMPTY;

	/**
	 * Indicated if this editor needs to be refreshed upon activation due to changed model root resource while editor
	 * has been inactive.
	 */
	protected boolean refreshOnActivation = false;

	protected IFormPage loadingEditorInputPage;

	protected SaveablesProvider modelSaveablesProvider;

	protected AdapterFactoryItemDelegator itemDelegator;

	/**
	 * This listens for when Outline and Properties view become active/inactive
	 */
	protected IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof ContentOutline) {
				if (((ContentOutline) part).getCurrentPage() == contentOutlinePage) {
					getActionBarContributor().setActiveEditor(BasicTransactionalFormEditor.this);
					setCurrentSelectionProvider(contentOutlineViewer);
				}
			} else if (part instanceof PropertySheet) {
				if (propertySheetPages.contains(((PropertySheet) part).getCurrentPage())) {
					getActionBarContributor().setActiveEditor(BasicTransactionalFormEditor.this);
					handleActivate();
				}
			} else if (part == BasicTransactionalFormEditor.this) {
				setCurrentSelectionProvider(selectionProvider);
				handleActivate();
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part instanceof PropertySheet) {
				((PropertySheet) part).getCurrentPage().dispose();
				propertySheetPages.remove(((PropertySheet) part).getCurrentPage());
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	private ResourceSetListener resourceLoadedListener;

	private ResourceSetListener resourceMovedListener;

	private ResourceSetListener resourceRemovedListener;

	private IOperationHistoryListener affectedObjectsListener;

	private ResourceSetListener objectRemovedListener;

	private CommandStackListener commandStackListener;

	/**
	 * Handles activation of the editor or it's associated views.
	 */
	protected void handleActivate() {

		// Recompute the read only state
		EditingDomain editingDomain = getEditingDomain();
		if (editingDomain instanceof AdapterFactoryEditingDomain) {
			if (((AdapterFactoryEditingDomain) editingDomain).getResourceToReadOnlyMap() != null) {
				((AdapterFactoryEditingDomain) editingDomain).getResourceToReadOnlyMap().clear();
			}
		}

		if (refreshOnActivation) {
			refreshActivePage();
			refreshOnActivation = false;
		}

		// Refresh any actions that may become enabled or disabled
		setSelection(getSelection());
	}

	/**
	 * This creates a model editor.
	 */
	public BasicTransactionalFormEditor() {
		// Create undo context
		undoContext = new ObjectUndoContext(this);

		// Ensures that this editor will only display the page's tab area if there is more than one page
		addPageChangedListener(new IPageChangedListener() {
			public void pageChanged(PageChangedEvent event) {
				if (getPageCount() <= 1) {
					hideTabs();
				} else {
					showTabs();
				}
			}
		});
	}

	/**
	 * This sets the selection into whichever viewer is active.
	 */
	public void setSelectionToViewer(Collection<?> collection) {
		final Collection<?> theSelection = collection;
		// Make sure it's okay.
		if (theSelection != null && !theSelection.isEmpty()) {
			// I don't know if this should be run this deferred because we might have to give the editor a chance to
			// process the viewer update events and hence to update the views first
			Runnable runnable = new Runnable() {
				public void run() {
					// Try to select the items in the current content viewer of the editor
					try {
						if (currentSelectionProvider != null) {
							if (currentSelectionProvider instanceof Viewer) {
								((Viewer) currentSelectionProvider).setSelection(new StructuredSelection(theSelection.toArray()), true);
							} else {
								currentSelectionProvider.setSelection(new StructuredSelection(theSelection.toArray()));
							}
						}
					} catch (RuntimeException ex) {
						// Ignore exception
					}
				}
			};
			runnable.run();
		}
	}

	/**
	 * This is to make sure that one of the selection providers in one of the pages can shadows the selection in the
	 * content outline. The parent relation must be correctly defined for this to work.
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
		setCurrentSelectionProvider(selectionProvider);
	}

	/**
	 * This makes sure that one selection provider, either for the current page or the outline view, if it has focus, is
	 * the current one.
	 */
	protected void setCurrentSelectionProvider(ISelectionProvider selectionProvider) {
		// If it is changing...
		if (currentSelectionProvider != selectionProvider) {
			if (selectionChangedListener == null) {
				// Create the listener on demand
				selectionChangedListener = new ISelectionChangedListener() {
					// This just notifies those things that are affected by the section
					public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
						setSelection(selectionChangedEvent.getSelection());
					}
				};
			}

			// Stop listening to the old one
			if (currentSelectionProvider != null) {
				currentSelectionProvider.removeSelectionChangedListener(selectionChangedListener);
			}

			// Start listening to the new one
			if (selectionProvider != null) {
				selectionProvider.addSelectionChangedListener(selectionChangedListener);
			}

			// Remember it
			currentSelectionProvider = selectionProvider;

			// Set the editors selection based on the current viewer's selection
			setSelection(currentSelectionProvider != null ? currentSelectionProvider.getSelection() : StructuredSelection.EMPTY);
		}
	}

	/**
	 * This returns the viewer as required by the {@link IViewerProvider} interface.
	 */
	public Viewer getViewer() {
		if (currentSelectionProvider instanceof Viewer) {
			return (Viewer) currentSelectionProvider;
		}
		return null;
	}

	/**
	 * This creates a context menu for the viewer and adds a listener as well registering the menu for extension.
	 */
	public void createContextMenuFor(StructuredViewer viewer) {
		MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
		contextMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(this);
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(contextMenu, new UnwrappingSelectionProvider(viewer));

		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance() };
		viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
		EditingDomain editingDomain = getEditingDomain();
		if (editingDomain != null) {
			viewer.addDropSupport(dndOperations, transfers, new EditingDomainViewerDropAdapter(editingDomain, viewer));
		}
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

	protected Map<?, ?> getLoadOptions() {
		return EcoreResourceUtil.getDefaultLoadOptions();
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
	 * If there is just one page in the multi-page editor part, this hides the single tab at the bottom.
	 */
	protected void hideTabs() {
		if (getPageCount() <= 1) {
			if (getContainer() instanceof CTabFolder) {
				((CTabFolder) getContainer()).setTabHeight(0);
				((CTabFolder) getContainer()).layout();
			}
		}
	}

	/**
	 * If there is more than one page in the multi-page editor part, this shows the tabs at the bottom.
	 */
	protected void showTabs() {
		if (getPageCount() > 1) {
			if (getContainer() instanceof CTabFolder) {
				((CTabFolder) getContainer()).setTabHeight(SWT.DEFAULT);
				((CTabFolder) getContainer()).layout();
			}
		}
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		if (key.equals(AdapterFactory.class)) {
			return getAdapterFactory();
		} else if (key.equals(IContentOutlinePage.class)) {
			return showOutlineView() ? getContentOutlinePage() : null;
		} else if (key.equals(IPropertySheetPage.class)) {
			return getPropertySheetPage();
		} else if (key.equals(IGotoMarker.class)) {
			return this;
		} else if (key.equals(IUndoContext.class)) {
			// used by undo/redo actions to get their undo context
			return undoContext;
		} else {
			return super.getAdapter(key);
		}
	}

	/**
	 * This accesses a cached version of the content outliner.
	 */
	public IContentOutlinePage getContentOutlinePage() {
		if (contentOutlinePage == null) {
			// The content outline is just a tree.
			//
			class MyContentOutlinePage extends ContentOutlinePage {
				@Override
				public void createControl(Composite parent) {
					super.createControl(parent);
					contentOutlineViewer = getTreeViewer();
					contentOutlineViewer.addSelectionChangedListener(this);

					// Set up the tree viewer
					Object modelRoot = getModelRoot();
					EditingDomain editingDomain = getEditingDomain();
					AdapterFactory adapterFactory = getAdapterFactory();
					if (editingDomain != null && adapterFactory != null) {
						contentOutlineViewer.setContentProvider(new TransactionalAdapterFactoryContentProvider(
								(TransactionalEditingDomain) editingDomain, adapterFactory));
						contentOutlineViewer.setLabelProvider(new TransactionalAdapterFactoryLabelProvider(
								(TransactionalEditingDomain) editingDomain, adapterFactory));
						contentOutlineViewer.setInput(modelRoot);
					} else {
						if (contentOutlineViewer.getContentProvider() != null) {
							contentOutlineViewer.setInput(null);
						}
					}

					// Make sure our popups work
					createContextMenuFor(contentOutlineViewer);

					if (modelRoot != null) {
						// Select the root object in the view
						contentOutlineViewer.setSelection(new StructuredSelection(modelRoot), true);
					}
				}

				@Override
				public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
					super.makeContributions(menuManager, toolBarManager, statusLineManager);
					contentOutlineStatusLineManager = statusLineManager;
				}

				@Override
				public void setActionBars(IActionBars actionBars) {
					super.setActionBars(actionBars);
					getActionBarContributor().shareGlobalActions(this, actionBars);
				}
			}

			contentOutlinePage = new MyContentOutlinePage();

			// Listen to selection so that we can handle it is a special way.
			//
			contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
				// This ensures that we handle selections correctly.
				//
				public void selectionChanged(SelectionChangedEvent event) {
					handleContentOutlineSelection(event.getSelection());
				}
			});
		}

		return contentOutlinePage;
	}

	/**
	 * This creates a new property sheet page instance and manages it in the cache.
	 */
	public IPropertySheetPage getPropertySheetPage() {
		IPropertySheetPage propertySheetPage = new TabbedPropertySheetPage(this);
		propertySheetPages.add(propertySheetPage);
		return propertySheetPage;
	}

	public String getContributorId() {
		return getSite().getId();
	}

	/**
	 * This deals with how we want selection in the outliner to affect the other views.
	 */
	public void handleContentOutlineSelection(ISelection selection) {
		if (selectionProvider != null) {
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				Iterator<?> selectedElements = ((IStructuredSelection) selection).iterator();
				if (selectedElements.hasNext()) {
					// Get the first selected element
					Object selectedElement = selectedElements.next();
					ArrayList<Object> selectionList = new ArrayList<Object>();
					selectionList.add(selectedElement);
					while (selectedElements.hasNext()) {
						selectionList.add(selectedElements.next());
					}

					// Set the selection to the widget
					selectionProvider.setSelection(new StructuredSelection(selectionList));
				}
			}
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

	@Override
	public boolean isSaveOnCloseNeeded() {
		// In this case the opened editor own the file
		if (getEditorInput() instanceof FileStoreEditorInput && ((FileStoreEditorInput) getEditorInput()).exists()) {
			return isDirty();
		}
		// Model-based editors don't need to be saved when being closed even if the model is dirty, because they don't
		// own the model. The model is loaded, managed, and saved globally, i.e. it is not destroyed but stays there
		// when editors are being closed.
		return false;
	}

	/**
	 * This is for implementing {@link IEditorPart} and simply saves the model file.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getEditorInput() instanceof FileStoreEditorInput) {
			saveResource();
		} else {
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
				 * because we have observed the progress bar stays frozen at 100% after completion of the save
				 * operation. In order to avoid that we notify the progress monitor that the save work is done right
				 * here.
				 */
				if (monitor != null) {
					monitor.done();
				}
			}
		}
	}

	/**
	 * Saves the resource on which the editor is opened.
	 */
	protected void saveResource() {
		final EditingDomain editingDomain = getEditingDomain();
		// Save only if resource changed.
		//
		final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

		// Do the work within an operation because this is a long running activity that modifies the workbench.
		//
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			// This is the method that gets invoked when the operation runs.
			//
			@Override
			public void execute(IProgressMonitor monitor) {
				// Save the resources to the file system.
				//
				if ((!getModelRootResource().getContents().isEmpty() || isPersisted(getModelRootResource()))
						&& !editingDomain.isReadOnly(getModelRootResource())) {
					try {
						getModelRootResource().save(saveOptions);

					} catch (Exception exception) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), exception);
					}
				}
			}
		};

		try {
			// This runs the operation, and shows progress.
			//
			new ProgressMonitorDialog(getSite().getShell()).run(true, false, operation);

			// Refresh the necessary state.
			//
			((BasicCommandStack) editingDomain.getCommandStack()).saveIsDone();
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (Exception exception) {
			// Something went wrong that shouldn't.
			//
			PlatformLogUtil.logAsError(Activator.getPlugin(), exception);
		}
	}

	/**
	 * This returns whether something has been persisted to the URI of the specified resource. The implementation uses
	 * the URI converter from the editor's resource set to try to open an input stream.
	 */
	protected boolean isPersisted(Resource resource) {
		boolean result = false;
		try {
			InputStream stream = getEditingDomain().getResourceSet().getURIConverter().createInputStream(resource.getURI());
			if (stream != null) {
				result = true;
				stream.close();
			}
		} catch (IOException e) {
			// Ignore
		}
		return result;
	}

	protected Map<?, ?> getSaveOptions() {
		return EcoreResourceUtil.getDefaultSaveOptions();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * This also changes the editor's input.
	 */
	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		saveAsDialog.open();
		IPath path = saveAsDialog.getResult();
		if (path != null) {
			// Create URI of new model root resource
			final URI newResourceURI = EcorePlatformUtil.createURI(path);

			// Changing the URI is, conceptually, a write operation. However, it does not affect the abstract state
			// of the model, so we only need exclusive (read) access
			try {
				EditingDomain editingDomain = getEditingDomain();
				if (editingDomain != null) {
					((TransactionalEditingDomain) editingDomain).runExclusive(new Runnable() {
						public void run() {
							// Change saved resource's URI
							Resource rootResource = getModelRootResource();
							if (rootResource != null) {
								rootResource.setURI(newResourceURI);
							}

							// Update editor input
							updateEditorInput(newResourceURI);
						}
					});
				}
			} catch (InterruptedException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);

				// Don't follow through with the save because we were interrupted while trying to start the
				// transaction, so our URI is not actually changed
				return;
			}

			IStatusLineManager statusLineManager = getActionBars().getStatusLineManager();
			IProgressMonitor monitor = statusLineManager != null ? statusLineManager.getProgressMonitor() : new NullProgressMonitor();
			doSave(monitor);
		}
	}

	public void gotoMarker(IMarker marker) {
		try {
			if (marker.isSubtypeOf(EValidator.MARKER)) {
				final String uriAttribute = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
				if (uriAttribute != null) {
					Notifier notifier = getEObject(URI.createURI(uriAttribute, true));
					if (notifier != null) {
						EditingDomain editingDomain = getEditingDomain();
						if (editingDomain != null) {
							setSelectionToViewer(Collections.singleton(((AdapterFactoryEditingDomain) editingDomain).getWrapper(notifier)));
						}
					}
				}
			}
		} catch (CoreException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	/**
	 * This is called during startup.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) {
		setSite(site);
		setInputWithNotify(editorInput);

		setPartName(getEditorInputName());
		setTitleImage(getEditorInputImage());

		site.setSelectionProvider(this);
		site.getPage().addPartListener(partListener);
		addTransactionalEditingDomainListeners((TransactionalEditingDomain) getEditingDomain());

		modelSaveablesProvider = createModelSaveablesProvider();
		modelSaveablesProvider.init(createModelSaveablesLifecycleListener());
	}

	@Override
	public void setFocus() {
		int pageIndex = getActivePage();
		if (pageIndex != -1) {
			Control control = getControl(pageIndex);
			if (control != null) {
				control.setFocus();
			}
		}
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
	 */
	public ISelection getSelection() {
		return editorSelection;
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection.
	 * Calling this result will notify the listeners.
	 */
	public void setSelection(ISelection selection) {
		editorSelection = !SelectionUtil.getStructuredSelection(selection).isEmpty() ? selection : getDefaultSelection();

		for (ISelectionChangedListener listener : new ArrayList<ISelectionChangedListener>(selectionChangedListeners)) {
			listener.selectionChanged(new SelectionChangedEvent(this, editorSelection));
		}
		setStatusLineManager(editorSelection);
	}

	public ISelection getDefaultSelection() {
		// Try to return model root object as default selection
		Object modelRoot = getModelRoot();
		if (modelRoot != null) {
			return new StructuredSelection(modelRoot);
		}
		return StructuredSelection.EMPTY;
	}

	public void setStatusLineManager(ISelection selection) {
		IStatusLineManager statusLineManager = currentSelectionProvider != null && currentSelectionProvider == contentOutlineViewer ? contentOutlineStatusLineManager
				: getActionBars().getStatusLineManager();
		if (statusLineManager != null) {
			if (selection instanceof IStructuredSelection) {
				Collection<?> collection = ((IStructuredSelection) selection).toList();
				switch (collection.size()) {
				case 0: {
					statusLineManager.setMessage(getString("_UI_NoObjectSelected")); //$NON-NLS-1$
					break;
				}
				case 1: {
					Object object = collection.iterator().next();
					String text = getItemDelegator() != null ? getItemDelegator().getText(object) : ""; //$NON-NLS-1$
					Object image = getItemDelegator() != null ? getItemDelegator().getImage(object) : null;
					statusLineManager.setMessage(ExtendedImageRegistry.getInstance().getImage(image), text);
					break;
				}
				default: {
					statusLineManager.setMessage(getString("_UI_MultiObjectSelected", Integer.toString(collection.size()))); //$NON-NLS-1$
					break;
				}
				}
			} else {
				statusLineManager.setMessage(""); //$NON-NLS-1$
			}
		}
	}

	/**
	 * This looks up a string in the plugin's plugin.properties file.
	 */
	private static String getString(String key) {
		return Activator.INSTANCE.getString(key);
	}

	/**
	 * This looks up a string in plugin.properties, making a substitution.
	 */
	private static String getString(String key, Object s1) {
		return Activator.INSTANCE.getString(key, new Object[] { s1 });
	}

	/**
	 * This implements {@link org.eclipse.jface.action.IMenuListener} to help fill the context menus with contributions
	 * from the Edit menu.
	 */
	public void menuAboutToShow(IMenuManager menuManager) {
		((IMenuListener) getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
	}

	public EditingDomainActionBarContributor getActionBarContributor() {
		return (EditingDomainActionBarContributor) getEditorSite().getActionBarContributor();
	}

	public IActionBars getActionBars() {
		return getActionBarContributor().getActionBars();
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

		getSite().getPage().removePartListener(partListener);

		if (getActionBarContributor() != null && getActionBarContributor().getActiveEditor() == this) {
			getActionBarContributor().deactivate();
			getActionBarContributor().setActiveEditor(null);
		}

		for (IPropertySheetPage propertySheetPage : propertySheetPages) {
			propertySheetPage.dispose();
		}
		propertySheetPages.clear();

		if (contentOutlinePage != null) {
			contentOutlinePage.dispose();
		}

		if (modelSaveablesProvider != null) {
			modelSaveablesProvider.dispose();
		}

		// Unload the resource when disposing the editor if resource is outside the workspace
		if (getEditorInput() instanceof FileStoreEditorInput) {
			EcoreResourceUtil.unloadResource(getModelRootResource(), true);
		}

		super.dispose();
	}

	/**
	 * Tests if this editor has already been disposed.
	 * <p>
	 * This implementation determines the disposed state of the editor by checking if the {@link FormEditor#pages} field
	 * is <code>null</code> or not. When the editor has been disposed, it is an error to invoke any other method using
	 * the editor.
	 * </p>
	 * 
	 * @return <code>true</code> when the editor is disposed and <code>false</code> otherwise.
	 */
	protected boolean isDisposed() {
		return pages == null;
	}

	/**
	 * Returns whether the outline view should be presented to the user.
	 */
	protected boolean showOutlineView() {
		return true;
	}

	protected void addTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
		if (editingDomain != null) {
			// This commandStackListener is enough when the resource resides outside the workspace
			if (getEditorInput() instanceof FileStoreEditorInput) {
				commandStackListener = createCommandStackListener();
				Assert.isNotNull(commandStackListener);
				editingDomain.getCommandStack().addCommandStackListener(commandStackListener);
			} else {
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
			if (commandStackListener != null) {
				IWorkspaceCommandStack commandStack = (IWorkspaceCommandStack) editingDomain.getCommandStack();
				commandStack.removeCommandStackListener(commandStackListener);
			}
		}
	}

	/**
	 * A {@link CommandStackListener} which is used just in the case when the resource resides outside the workspace.
	 * 
	 * @return
	 */
	protected CommandStackListener createCommandStackListener() {
		return new CommandStackListener() {
			public void commandStackChanged(final EventObject event) {
				ExtendedPlatformUI.getDisplay().asyncExec(new Runnable() {
					public void run() {
						firePropertyChange(IEditorPart.PROP_DIRTY);
						// Try to select the affected objects.
						//
						Command mostRecentCommand = ((CommandStack) event.getSource()).getMostRecentCommand();
						if (mostRecentCommand != null) {
							setSelectionToViewer(mostRecentCommand.getAffectedObjects());
						}

						for (IPropertySheetPage propertySheetPage : propertySheetPages) {
							if (propertySheetPage != null && !propertySheetPage.getControl().isDisposed()) {
								if (propertySheetPage instanceof PropertySheetPage) {
									((PropertySheetPage) propertySheetPage).refresh();
								} else if (propertySheetPage instanceof TabbedPropertySheetPage) {
									((TabbedPropertySheetPage) propertySheetPage).refresh();
								}
							}
						}
					}
				});
			}
		};
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

							// Refresh editor if its currently active or schedule refresh when it gets
							// activated next time
							if (getActionBarContributor().getActiveEditor() == BasicTransactionalFormEditor.this) {
								refreshActivePage();
							} else {
								refreshOnActivation = true;
							}
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
							if (isActivePart() || isMyActivePropertySheetPage()) {
								// Try to select the affected objects
								if (operation instanceof EMFCommandOperation) {
									Command command = ((EMFCommandOperation) operation).getCommand();
									if (command != null) {
										setSelectionToViewer(command.getAffectedObjects());
									}
								}
							}

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

	protected boolean isActivePart() {
		return this == getSite().getWorkbenchWindow().getPartService().getActivePart();
	}

	protected boolean isMyActivePropertySheetPage() {
		IWorkbenchPart activePart = getSite().getWorkbenchWindow().getPartService().getActivePart();
		if (activePart instanceof PropertySheet) {
			return getPropertySheetPage() == ((PropertySheet) activePart).getCurrentPage();
		}
		return false;
	}

	protected SaveablesProvider createModelSaveablesProvider() {
		return new BasicModelSaveablesProvider();
	}

	/**
	 * Creates an {@linkplain ISaveablesLifecycleListener}
	 * 
	 * @return
	 */
	protected ISaveablesLifecycleListener createModelSaveablesLifecycleListener() {
		return new SiteNotifyingSaveablesLifecycleListener(this) {
			@Override
			public void handleLifecycleEvent(SaveablesLifecycleEvent event) {
				super.handleLifecycleEvent(event);

				if (event.getEventType() == SaveablesLifecycleEvent.DIRTY_CHANGED) {
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
		};
	}

	/**
	 * This returns the editing domain as required by the {@link IEditingDomainProvider} interface. This is important
	 * for implementing the static methods of {@link AdapterFactoryEditingDomain} and for supporting
	 * {@link org.eclipse.emf.edit.ui.action.CommandAction}.
	 */
	public EditingDomain getEditingDomain() {
		URI uri = EcoreUIUtil.getURIFromEditorInput(getEditorInput());
		return getEditingDomain(uri);
	}

	/**
	 * Returns the {@link AdapterFactory adapter factory} to be used by this {@link BasicTransactionalFormEditor form
	 * editor} for creating {@link ItemProviderAdapter item provider}s which control the way how {@link EObject model
	 * element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns the {@link AdapterFactory adapter factory} which is embedded in the given
	 * <code>editingDomain</code> by default. Clients which want to use an alternative {@link AdapterFactory adapter
	 * factory} (e.g., an {@link AdapterFactory adapter factory} that creates {@link ItemProviderAdapter item provider}s
	 * which are specifically designed for the {@link IEditorPart editor} in which this
	 * {@link BasicTransactionalFormEditor form editor} is used) may override {@link #getCustomAdapterFactory()} and
	 * return any {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter
	 * factory} will then be returned as result by this method.
	 * </p>
	 * 
	 * @param editingDomain
	 *            The {@link TransactionalEditingDomain editing domain} whose embedded {@link AdapterFactory adapter
	 *            factory} is to be returned as default. May be left <code>null</code> if
	 *            {@link #getCustomAdapterFactory()} has been overridden and returns a non-<code>null</code> result.
	 * @return The {@link AdapterFactory adapter factory} that will be used by this {@link BasicTransactionalFormEditor
	 *         form editor}. <code>null</code> if no custom {@link AdapterFactory adapter factory} is provided through
	 *         {@link #getCustomAdapterFactory()} and no <code>editingDomain</code> has been specified.
	 * @see #getCustomAdapterFactory()
	 */
	public AdapterFactory getAdapterFactory() {
		EditingDomain editingDomain = getEditingDomain();
		AdapterFactory customAdapterFactory = getCustomAdapterFactory();
		if (customAdapterFactory != null) {
			return customAdapterFactory;
		} else if (editingDomain != null) {
			return ((AdapterFactoryEditingDomain) editingDomain).getAdapterFactory();
		}
		return null;
	}

	/**
	 * Returns a custom {@link AdapterFactory adapter factory} to be used by this {@link BasicTransactionalFormEditor
	 * form editor} for creating {@link ItemProviderAdapter item provider}s which control the way how {@link EObject
	 * model element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns <code>null</code> as default. Clients which want to use their own
	 * {@link AdapterFactory adapter factory} (e.g., an {@link AdapterFactory adapter factory} that creates
	 * {@link ItemProviderAdapter item provider}s which are specifically designed for the {@link IEditorPart editor} in
	 * which this {@link BasicTransactionalFormEditor form editor} is used) may override this method and return any
	 * {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter factory} will
	 * then be returned as result by {@link #getAdapterFactory(TransactionalEditingDomain)}.
	 * </p>
	 * 
	 * @return The custom {@link AdapterFactory adapter factory} that is to be used by this
	 *         {@link BasicTransactionalFormEditor form editor}. <code>null</code> the default {@link AdapterFactory
	 *         adapter factory} returned by {@link #getAdapterFactory(TransactionalEditingDomain)} should be used
	 *         instead.
	 * @see #getAdapterFactory(TransactionalEditingDomain)
	 */
	protected AdapterFactory getCustomAdapterFactory() {
		return null;
	}

	public AdapterFactoryItemDelegator getItemDelegator() {
		if (itemDelegator == null) {
			itemDelegator = createItemDelegator();
		}
		return itemDelegator;
	}

	protected AdapterFactoryItemDelegator createItemDelegator() {
		AdapterFactory adapterFactory = getAdapterFactory();
		if (adapterFactory != null) {
			return new AdapterFactoryItemDelegator(adapterFactory);
		}
		return null;
	}

	/**
	 * Creates the common toolkit for this editor and adds pages to the editor.
	 */
	@Override
	protected void createPages() {
		// Model root not yet available?
		if (getModelRoot() == null) {
			// Close editor if file behind model root is out of scope
			IFile file = EcoreUIUtil.getFileFromEditorInput(getEditorInput());
			IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(file);
			if (modelDescriptor == null) {
				close(false);
				return;
			} else {
				// Request asynchronous loading of model behind editor input
				ModelLoadManager.INSTANCE.loadModel(modelDescriptor, true, null);

				// Create temporary page indicating that editor input is being loaded
				loadingEditorInputPage = createLoadingEditorInputPage();
				if (loadingEditorInputPage != null) {
					try {
						addPage(loadingEditorInputPage);
					} catch (PartInitException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					}
					return;
				}
			}
		}

		// Create editor pages normally
		super.createPages();
	}

	protected IFormPage createLoadingEditorInputPage() {
		return new MessagePage(this, NLS.bind(Messages.label_waitingForModelElementBeingLoaded, getPartName()));
	}

	/**
	 * This is the method used by the framework to install your own pages.
	 */
	@Override
	protected void addPages() {
		try {
			addPage(new GenericContentsTreePage(this));
		} catch (PartInitException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	protected synchronized void refreshActivePage() {
		if (!isDisposed()) {
			// Loading editor input indication page present?
			if (loadingEditorInputPage != null) {
				// Remove loading editor input indication page and try to create actual pages
				removePage(loadingEditorInputPage.getIndex());
				loadingEditorInputPage = null;
				createPages();
				if (getActivePage() == -1) {
					setActivePage(0);
				}
			} else {
				// Refresh by reselecting previously active page
				pageChange(getActivePage());
			}
		}
	}

	@Override
	public IFormPage getActivePageInstance() {
		// FIXME File bug to Eclipse Platform: Calling getActivePageInstance() causes NullPointerException if this
		// editor has already been disposed
		if (!isDisposed()) {
			return super.getActivePageInstance();
		}
		return null;
	}

	@Override
	protected void setActivePage(int pageIndex) {
		// FIXME File bug to Eclipse Platform: Calling setActivePage(0) causes AssertionFailedException if for some
		// reason no pages have been added yet or all pages have already been removed
		if (pageIndex >= 0 && pageIndex < getPageCount()) {
			super.setActivePage(pageIndex);
		}
	}

	@Override
	protected IEditorPart getEditor(int pageIndex) {
		// FIXME File bug to Eclipse Platform: Calling setEditor(0) causes SWT error if for some reason no pages have
		// been added yet or all pages have already been removed
		if (pageIndex >= 0 && pageIndex < ((CTabFolder) getContainer()).getItemCount()) {
			return super.getEditor(pageIndex);
		}
		return null;
	}

	public void saveState(IMemento memento) {
		// Save editor dirty state; required upon editor restoration
		memento.putBoolean(TAG_EDITOR_DIRTY_ON_WORKBENCH_CLOSE, isDirty());
	}

	public void restoreState(IMemento memento) {
		// Close editor if it has been left dirty upon last workbench close; in this case the editor input URI might be
		// pointing at some model element that hasn't been saved and therefore doesn't exist upon editor restoration
		if (memento.getBoolean(TAG_EDITOR_DIRTY_ON_WORKBENCH_CLOSE)) {
			close(false);
		}
	}

	public Saveable[] getActiveSaveables() {
		return getSaveables();
	}

	public Saveable[] getSaveables() {
		// As Saveables management is based on ModelDescriptors & no ModelDescriptor for files outside the workspace, we
		// return here a default Saveable
		if (getEditorInput() instanceof FileStoreEditorInput) {
			return new Saveable[] { new DefaultSaveable(this) };
		}
		if (modelSaveablesProvider != null) {
			Saveable saveable = modelSaveablesProvider.getSaveable(getModelRootResource());
			if (saveable != null) {
				return new Saveable[] { saveable };
			}
		}
		return new Saveable[] {};
	}

	@Override
	public String toString() {
		Object modelRoot = getModelRoot();
		if (modelRoot instanceof EObject) {
			URI uri = EcoreUtil.getURI((EObject) modelRoot);
			if (uri != null) {
				return uri.toString();
			}
		}
		return super.toString();
	}
}