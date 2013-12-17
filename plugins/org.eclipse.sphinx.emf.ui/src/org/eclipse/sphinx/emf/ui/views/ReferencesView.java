/**
 * <copyright>
 * 
 * Copyright (c) 2011 itemis and others.
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
package org.eclipse.sphinx.emf.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.internal.Tracing;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.ui.internal.Activator;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 *
 */
public class ReferencesView extends ViewPart {

	private Object viewInput;
	private IContentProvider contentProvider;
	private ILabelProvider labelProvider;
	private TreeViewer viewer;

	private boolean showInverseReferences = false;

	protected Map<TransactionalEditingDomain, IContentProvider> modelCrossReferenceContentProviders = new WeakHashMap<TransactionalEditingDomain, IContentProvider>();
	protected Map<TransactionalEditingDomain, ILabelProvider> modelLabelProviders = new WeakHashMap<TransactionalEditingDomain, ILabelProvider>();

	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		contentProvider = createModelCrossReferenceContentProvider();
		if (contentProvider != null) {
			viewer.setContentProvider(contentProvider);
		}
		if (labelProvider != null) {
			viewer.setLabelProvider(labelProvider);
		}

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void setViewInput(Object viewInput) {
		this.viewInput = viewInput;
		labelProvider = getModelLabelProvider(viewInput);
		if (labelProvider != null) {
			viewer.setLabelProvider(labelProvider);
		}
		viewer.setInput(viewInput);
	}

	protected IContentProvider createModelCrossReferenceContentProvider() {
		return new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// Do nothing
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public boolean hasChildren(Object element) {
				return getChildren(element).length > 0;
			}

			// TODO Add defer and abort capability
			// TODO Avoid infinite number of children in case of cyclic references
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof EObject) {
					final EObject parentEObject = (EObject) parentElement;

					TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(parentEObject);
					if (editingDomain != null) {
						try {
							return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<Object[]>() {
								@Override
								public void run() {
									Collection<EObject> eCrossReferences = getECrossReferences(parentEObject, showInverseReferences);
									setResult(eCrossReferences.toArray(new EObject[eCrossReferences.size()]));
								}
							});
						} catch (InterruptedException ex) {
							PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
						}
					} else {
						Collection<EObject> eCrossReferences = getECrossReferences(parentEObject, showInverseReferences);
						return eCrossReferences.toArray(new EObject[eCrossReferences.size()]);
					}
				}
				return new Object[0];
			}

			protected Collection<EObject> getECrossReferences(EObject eObject, boolean inverse) {
				if (inverse) {
					List<EObject> eInverseCrossReferences = new ArrayList<EObject>();
					Collection<Setting> inverseReferences = EObjectUtil.getInverseReferences(eObject, true);
					for (Setting inverseReference : inverseReferences) {
						eInverseCrossReferences.add(inverseReference.getEObject());
					}
					return eInverseCrossReferences;
				} else {
					return eObject.eCrossReferences();
				}
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public void dispose() {
				// Do nothing
			}
		};
	}

	protected ILabelProvider getModelLabelProvider(Object element) {
		// Retrieve editing domain behind specified object
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(element);
		if (editingDomain != null) {
			// Retrieve model label provider for given editing domain; create new one if not existing yet
			ILabelProvider modelLabelProvider = modelLabelProviders.get(editingDomain);
			if (modelLabelProvider == null) {
				modelLabelProvider = createModelLabelProvider(editingDomain);
				modelLabelProviders.put(editingDomain, modelLabelProvider);
			}
			return modelLabelProvider;
		} else if (element instanceof EObject && ((EObject) element).eIsProxy()) {
			// Use non-transactional adapter factory label provider to avoid that proxified EObjects end up being
			// represented as empty tree nodes
			return new AdapterFactoryLabelProvider(getAdapterFactory(editingDomain));
		}
		return null;
	}

	protected ILabelProvider createModelLabelProvider(final TransactionalEditingDomain editingDomain) {
		Assert.isNotNull(editingDomain);
		AdapterFactory adapterFactory = getAdapterFactory(editingDomain);
		return new TransactionalAdapterFactoryLabelProvider(editingDomain, adapterFactory) {
			@Override
			// Overridden to avoid the somewhat annoying logging of Eclipse exceptions resulting from event queue
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
	 * Returns the {@link AdapterFactory adapter factory} to be used by this {@link BasicExplorerLabelProvider label
	 * provider} for creating {@link ItemProviderAdapter item provider}s which control the way how {@link EObject model
	 * element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns the {@link AdapterFactory adapter factory} which is embedded in the given
	 * <code>editingDomain</code> by default. Clients which want to use an alternative {@link AdapterFactory adapter
	 * factory} (e.g., an {@link AdapterFactory adapter factory} that creates {@link ItemProviderAdapter item provider}s
	 * which are specifically designed for the {@link IEditorPart editor} in which this
	 * {@link BasicExplorerLabelProvider label provider} is used) may override {@link #getCustomAdapterFactory()} and
	 * return any {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter
	 * factory} will then be returned as result by this method.
	 * </p>
	 * 
	 * @param editingDomain
	 *            The {@link TransactionalEditingDomain editing domain} whose embedded {@link AdapterFactory adapter
	 *            factory} is to be returned as default. May be left <code>null</code> if
	 *            {@link #getCustomAdapterFactory()} has been overridden and returns a non-<code>null</code> result.
	 * @return The {@link AdapterFactory adapter factory} that will be used by this {@link BasicExplorerLabelProvider
	 *         label provider}. <code>null</code> if no custom {@link AdapterFactory adapter factory} is provided
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
	 * Returns a custom {@link AdapterFactory adapter factory} to be used by this {@link BasicExplorerLabelProvider
	 * label provider} for creating {@link ItemProviderAdapter item provider}s which control the way how {@link EObject
	 * model element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns <code>null</code> as default. Clients which want to use their own
	 * {@link AdapterFactory adapter factory} (e.g., an {@link AdapterFactory adapter factory} that creates
	 * {@link ItemProviderAdapter item provider}s which are specifically designed for the {@link IEditorPart editor} in
	 * which this {@link BasicExplorerLabelProvider label provider} is used) may override this method and return any
	 * {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter factory} will
	 * then be returned as result by {@link #getAdapterFactory(TransactionalEditingDomain)}.
	 * </p>
	 * 
	 * @return The custom {@link AdapterFactory adapter factory} that is to be used by this
	 *         {@link BasicExplorerLabelProvider label provider}. <code>null</code> the default {@link AdapterFactory
	 *         adapter factory} returned by {@link #getAdapterFactory(TransactionalEditingDomain)} should be used
	 *         instead.
	 * @see #getAdapterFactory(TransactionalEditingDomain)
	 */
	protected AdapterFactory getCustomAdapterFactory() {
		// FIXME This API does't make sense like this. There must be a way to specify custom adapter factories on a per
		// metamodel basis.
		return null;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ReferencesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
	}

	private void makeActions() {
		action1 = new Action() {
			@Override
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			@Override
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Sample View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}