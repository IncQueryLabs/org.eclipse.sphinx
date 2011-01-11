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
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
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

		AdapterFactory adapterFactory = ((AdapterFactoryEditingDomain) editingDomain).getAdapterFactory();
		return new TransactionalAdapterFactoryContentProvider(editingDomain, adapterFactory) {
			@Override
			protected IPropertySource createPropertySource(Object object, IItemPropertySource itemPropertySource) {
				return new PropertySource(object, itemPropertySource) {
					@Override
					protected IPropertyDescriptor createPropertyDescriptor(IItemPropertyDescriptor itemPropertyDescriptor) {
						return new PropertyDescriptor(object, itemPropertyDescriptor) {
							@Override
							public CellEditor createPropertyEditor(final Composite composite) {
								if (object instanceof EObject) {
									Object feature = itemPropertyDescriptor.getFeature(object);
									if (feature instanceof EReference) {
										final EReference reference = (EReference) feature;
										InternalEObject internalEObject = (InternalEObject) object;
										if (!reference.isMany()) {
											EObject value = (EObject) internalEObject.eGet(reference);
											if (value != null && value.eIsProxy()) {
												return new ProxyURICellEditor(composite, (EObject) object, (InternalEObject) value);
											}
										} else {
											@SuppressWarnings("unchecked")
											List<EObject> values = (List<EObject>) internalEObject.eGet(reference);
											for (EObject value : values) {
												if (value.eIsProxy()) {
													final ILabelProvider editLabelProvider = getEditLabelProvider();
													final Collection<?> choiceOfValues = itemPropertyDescriptor.getChoiceOfValues(object);
													return new ExtendedDialogCellEditor(composite, editLabelProvider) {
														@Override
														protected Object openDialogBox(Control cellEditorWindow) {
															ProxyURIFeatureEditorDialog dialog = new ProxyURIFeatureEditorDialog(
																	cellEditorWindow.getShell(), editLabelProvider, object, reference.getEType(),
																	(List<?>) doGetValue(), getDisplayName(), new ArrayList<Object>(choiceOfValues),
																	false, itemPropertyDescriptor.isSortChoices(object), reference.isUnique());
															dialog.open();
															return dialog.getResult();
														}
													};
												}

											}

										}
									}
								}
								return super.createPropertyEditor(composite);
							}
						};
					}
				};

			}
		};
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
