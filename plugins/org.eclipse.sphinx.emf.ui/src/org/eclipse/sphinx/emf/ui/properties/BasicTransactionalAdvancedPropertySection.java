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
package org.eclipse.sphinx.emf.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.ui.celleditor.ExtendedDialogCellEditor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class BasicTransactionalAdvancedPropertySection extends AdvancedPropertySection {

	protected TabbedPropertySheetPage tabbedPropertySheetPage;

	protected IPropertySourceProvider lastPropertySourceProviderDelegate = null;

	protected ResourceSetListenerImpl selectedObjectChangedListener = null;

	protected Object lastSelectedObject = null;

	/**
	 * Required to make sure that Properties view gets refreshed when attributes of currently selected model element are
	 * changed (because the {@link TabbedPropertySheetPage}'s
	 * {@link org.eclipse.jface.viewers.ISelectionChangedListener} does nothing in this case)
	 */
	protected ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (tabbedPropertySheetPage != null) {
				IStructuredSelection structuredSelection = SelectionUtil.getStructuredSelection(selection);
				if (!structuredSelection.isEmpty()) {
					Object selectedObject = structuredSelection.getFirstElement();
					if (selectedObject != lastSelectedObject) {
						// Remove existing selected object changed listener for previously selected object if any
						/*
						 * !! Important Note !! Don't use WorkspaceEditingDomainUtil for retrieving editing domain here
						 * because we only want to handle objects which are eligible to EMF Edit rather than just any
						 * object from which an editing domain can be retrieved.
						 */
						Object unwrapped = AdapterFactoryEditingDomain.unwrap(lastSelectedObject);
						if (unwrapped != null) {
							TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(unwrapped);
							if (editingDomain != null) {
								editingDomain.removeResourceSetListener(selectedObjectChangedListener);
							}
						}

						// Remember currently selected object as for future removals of selected object changed listener
						lastSelectedObject = selectedObject;

						// Install new selected object changed listener for currently selected object
						/*
						 * !! Important Note !! Don't use WorkspaceEditingDomainUtil for retrieving editing domain here
						 * because we only want to handle objects which are eligible to EMF Edit rather than just any
						 * object from which an editing domain can be retrieved.
						 */
						unwrapped = AdapterFactoryEditingDomain.unwrap(selectedObject);
						if (unwrapped != null) {
							TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(unwrapped);
							if (editingDomain != null) {
								selectedObjectChangedListener = createSelectedObjectChangedListener(selectedObject);
								editingDomain.addResourceSetListener(selectedObjectChangedListener);
							}
						}
					}
				}
			}

			// Connect property sheet to current selection's workbench part
			// IActionBars partActionBars = null;
			// if (part instanceof IEditorPart) {
			// IEditorPart editorPart = (IEditorPart) part;
			// partActionBars = editorPart.getEditorSite().getActionBars();
			// } else if (part instanceof IViewPart) {
			// IViewPart viewPart = (IViewPart) part;
			// partActionBars = viewPart.getViewSite().getActionBars();
			// }
			// if (partActionBars.getStatusLineManager() instanceof SubStatusLineManager) {
			// SubStatusLineManager statusLineManager = (SubStatusLineManager)
			// partActionBars.getStatusLineManager();
			// statusLineManager.setVisible(true);
			// }
			// TODO Report enhancement bug to Eclipse Platform an enable this code fragment when fix is
			// available
			// page.setStatusLineManager(partActionBars.getStatusLineManager());
		}
	};

	protected ResourceSetListenerImpl createSelectedObjectChangedListener(Object selectedObject) {
		Assert.isNotNull(selectedObject);

		return new ResourceSetListenerImpl(NotificationFilter.createNotifierFilter(selectedObject)) {
			/*
			 * @see org.eclipse.emf.transaction.ResourceSetListenerImpl#resourceSetChanged(ResourceSetChangeEvent)
			 */
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				if (tabbedPropertySheetPage != null) {
					IPageSite site = tabbedPropertySheetPage.getSite();
					if (site != null) {
						site.getShell().getDisplay().asyncExec(new Runnable() {
							public void run() {
								// Refresh property section content
								if (page != null) {
									refresh();
									// Refresh property sheet title through this indirect call to private
									// TabbedPropertySheetPage#refreshTitleBar() method
									if (tabbedPropertySheetPage != null) {
										tabbedPropertySheetPage.labelProviderChanged(null);
									}
								}

							}
						});
					}
				}
			}
		};
	}

	@Override
	public void createControls(Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		this.tabbedPropertySheetPage = tabbedPropertySheetPage;

		// Setup transactional IPropertySourceProvider
		IPropertySourceProvider propertySourceProvider = new IPropertySourceProvider() {
			public IPropertySource getPropertySource(Object object) {
				// Let EMF Edit try to find an IPropertySource adapter
				/*
				 * !! Important Note !! Don't use WorkspaceEditingDomainUtil for retrieving editing domain here because
				 * we only want to handle objects which are eligible to EMF Edit rather than just any object from which
				 * an editing domain can be retrieved.
				 */
				Object unwrapped = AdapterFactoryEditingDomain.unwrap(object);
				if (unwrapped != null) {
					TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(unwrapped);
					if (editingDomain != null) {
						lastPropertySourceProviderDelegate = createModelPropertySourceProvider(editingDomain);
						IPropertySource propertySource = lastPropertySourceProviderDelegate.getPropertySource(object);
						if (propertySource != null) {
							return new FilteringPropertySource(propertySource);
						}
					} else if (lastPropertySourceProviderDelegate != null) {
						// Assume that object is from same editing domain as the last object that was handled in here
						IPropertySource propertySource = lastPropertySourceProviderDelegate.getPropertySource(object);
						if (propertySource != null) {
							return new FilteringPropertySource(propertySource);
						}
					}
				}

				// Let Eclipse Platform try to find an IPropertySource adapter for objects that are not supported by EMF
				// Edit
				if (object instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) object;
					return (IPropertySource) adaptable.getAdapter(IPropertySource.class);
				}

				return null;
			}
		};
		page.setPropertySourceProvider(propertySourceProvider);

		// Install selection listener and invoke it once in order to make sure that we are in phase with current
		// selection
		ISelectionService selectionService = tabbedPropertySheetPage.getSite().getWorkbenchWindow().getSelectionService();
		selectionService.addSelectionListener(selectionListener);
		selectionListener.selectionChanged(getPart(), selectionService.getSelection());
	}

	protected IPropertySourceProvider createModelPropertySourceProvider(TransactionalEditingDomain editingDomain) {
		Assert.isNotNull(editingDomain);

		AdapterFactory adapterFactory = getAdapterFactory(editingDomain);
		return new TransactionalAdapterFactoryContentProvider(editingDomain, adapterFactory) {
			/**
			 * Overridden to enable implantation of custom cell editor that will be used to edit the value of the given
			 * property.
			 */
			@Override
			protected IPropertySource createPropertySource(final Object object, final IItemPropertySource itemPropertySource) {
				return wrap(run(new RunnableWithResult.Impl<IPropertySource>() {
					public void run() {
						setResult(new PropertySource(object, itemPropertySource) {
							@Override
							protected IPropertyDescriptor createPropertyDescriptor(IItemPropertyDescriptor itemPropertyDescriptor) {
								return new PropertyDescriptor(object, itemPropertyDescriptor) {
									@Override
									public CellEditor createPropertyEditor(final Composite composite) {
										CellEditor editor = BasicTransactionalAdvancedPropertySection.this.createPropertyEditor(composite, object,
												itemPropertyDescriptor, this);
										if (editor != null) {
											return editor;
										}
										return super.createPropertyEditor(composite);
									}
								};
							}
						});
					}
				}));
			}
		};
	}

	/**
	 * Return a custom {@link CellEditor cell editor} to be used for editing the value of given property.
	 * 
	 * @param composite
	 *            The parent control of the {@link CellEditor cell editor} to be created.
	 * @param object
	 *            The owner of the {@link IItemPropertyDescriptor property} to be edited.
	 * @param itemPropertyDescriptor
	 *            The {@link IItemPropertyDescriptor item descriptor} of the property to be edited.
	 * @param propertyDescriptor
	 *            The {@link PropertyDescriptor descriptor} of the property to be edited.
	 * @return A newly created custom {@link CellEditor cell editor} to be used or <code>null</code> to indicate that
	 *         default {@link CellEditor cell editor} created by EMF.Edit should be used.
	 */
	protected CellEditor createPropertyEditor(Composite composite, Object object, final IItemPropertyDescriptor itemPropertyDescriptor,
			final PropertyDescriptor propertyDescriptor) {
		if (object instanceof EObject) {
			final EObject eObject = (EObject) object;
			Object feature = itemPropertyDescriptor.getFeature(eObject);
			if (feature instanceof EReference) {
				final EReference reference = (EReference) feature;
				InternalEObject internalEObject = (InternalEObject) eObject;
				if (!reference.isMany()) {
					EObject value = (EObject) internalEObject.eGet(reference);
					if (value != null && value.eIsProxy()) {
						return new ProxyURICellEditor(composite, eObject, reference, value);
					}
				} else {
					@SuppressWarnings("unchecked")
					List<EObject> values = (List<EObject>) internalEObject.eGet(reference);
					for (EObject value : values) {
						if (value.eIsProxy()) {
							final ILabelProvider editLabelProvider = propertyDescriptor.getLabelProvider();
							final Collection<?> choiceOfValues = itemPropertyDescriptor.getChoiceOfValues(eObject);
							return new ExtendedDialogCellEditor(composite, editLabelProvider) {
								@Override
								protected Object openDialogBox(Control cellEditorWindow) {
									ProxyURIFeatureEditorDialog dialog = new ProxyURIFeatureEditorDialog(cellEditorWindow.getShell(),
											editLabelProvider, eObject, reference, propertyDescriptor.getDisplayName(), new ArrayList<Object>(
													choiceOfValues), false, itemPropertyDescriptor.isSortChoices(eObject));
									dialog.open();
									return dialog.getResult();
								}
							};
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns the {@link AdapterFactory adapter factory} to be used by this {@link BasicModelEditActionProvider action
	 * provider} for creating {@link ItemProviderAdapter item provider}s which control the way how {@link EObject model
	 * element}s from given <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns the {@link AdapterFactory adapter factory} which is embedded in the given
	 * <code>editingDomain</code> by default. Clients which want to use an alternative {@link AdapterFactory adapter
	 * factory} (e.g., an {@link AdapterFactory adapter factory} that creates {@link ItemProviderAdapter item provider}s
	 * which are specifically designed for the {@link IEditorPart editor} in which this
	 * {@link BasicModelEditActionProvider action provider} is used) may override {@link #getCustomAdapterFactory()} and
	 * return any {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter
	 * factory} will then be returned as result by this method.
	 * </p>
	 * 
	 * @param editingDomain
	 *            The {@link TransactionalEditingDomain editing domain} whose embedded {@link AdapterFactory adapter
	 *            factory} is to be returned as default. May be left <code>null</code> if
	 *            {@link #getCustomAdapterFactory()} has been overridden and returns a non-<code>null</code> result.
	 * @return The {@link AdapterFactory adapter factory} that will be used by this {@link BasicModelEditActionProvider
	 *         action provider}. <code>null</code> if no custom {@link AdapterFactory adapter factory} is provided
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
	 * Returns a custom {@link AdapterFactory adapter factory} to be used by this
	 * {@link BasicTransactionalAdvancedPropertySection advanced property section} for creating
	 * {@link ItemProviderAdapter item provider}s which control the way how {@link EObject model element}s from given
	 * <code>editingDomain</code> are displayed and can be edited.
	 * <p>
	 * This implementation returns <code>null</code> as default. Clients which want to use their own
	 * {@link AdapterFactory adapter factory} (e.g., an {@link AdapterFactory adapter factory} that creates
	 * {@link ItemProviderAdapter item provider}s which are specifically designed for the {@link IEditorPart editor} in
	 * which this {@link BasicModelEditActionProvider action provider} is used) may override this method and return any
	 * {@link AdapterFactory adapter factory} of their choice. This custom {@link AdapterFactory adapter factory} will
	 * then be returned as result by {@link #getAdapterFactory(TransactionalEditingDomain)}.
	 * </p>
	 * 
	 * @return The custom {@link AdapterFactory adapter factory} that is to be used by this
	 *         {@link BasicModelEditActionProvider action provider}. <code>null</code> the default
	 *         {@link AdapterFactory adapter factory} returned by {@link #getAdapterFactory(TransactionalEditingDomain)}
	 *         should be used instead.
	 * @see #getAdapterFactory(TransactionalEditingDomain)
	 */
	protected AdapterFactory getCustomAdapterFactory() {
		return null;
	}

	@Override
	public void dispose() {
		// Unregister remaining selected object changed listener from all editing domains
		if (selectedObjectChangedListener != null) {
			for (TransactionalEditingDomain editingDomain : WorkspaceEditingDomainUtil.getAllEditingDomains()) {
				editingDomain.removeResourceSetListener(selectedObjectChangedListener);
			}
		}
		// Uninstall selection listener
		if (selectionListener != null) {
			ISelectionService selectionService = null;
			if (tabbedPropertySheetPage != null) {
				IPageSite site = tabbedPropertySheetPage.getSite();
				if (site != null) {
					selectionService = site.getWorkbenchWindow().getSelectionService();
				}
			}
			if (selectionService == null) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null) {
					IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
					if (windows.length > 0) {
						window = windows[0];
					}
				}
				if (window != null) {
					selectionService = window.getSelectionService();
				}
			}
			if (selectionService != null) {
				selectionService.removeSelectionListener(selectionListener);
			}
		}
		super.dispose();
	}
}
