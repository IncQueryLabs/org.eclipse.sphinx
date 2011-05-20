/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4Sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4Sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.properties;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.edit.ui.celleditor.FeatureEditorDialog;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * This class extends FeatureEditorDialog and is used in
 * {@link BasicTransactionalAdvancedPropertySection#createModelPropertySourceProvider(org.eclipse.emf.transaction.TransactionalEditingDomain)}
 * to enable the edition of the proxy URI of proxy elements inside a reference feature many. This add an text field to
 * the general FeatureEditorDialog that let edit the proxy URI of a proxified element within the list of value of a
 * reference feature many.
 */
public class ProxyURIFeatureEditorDialog extends FeatureEditorDialog {

	protected EStructuralFeature feature;
	protected List<?> currentValues;

	public ProxyURIFeatureEditorDialog(Shell parent, ILabelProvider labelProvider, EObject owner, EStructuralFeature feature, String displayName,
			List<?> choiceOfValues, boolean multiLine, boolean sortChoices) {
		// FIXME Use commented constructor once we don't need to support Eclipse 3.5 any longer.
		// super(parent, labelProvider, eObject, eStructuralFeature.getEType(), (List<?>)
		// eObject.eGet(eStructuralFeature), displayName, choiceOfValues,
		// multiLine, sortChoices, eStructuralFeature.isUnique());
		super(parent, labelProvider, owner, feature.getEType(), (List<?>) owner.eGet(feature), displayName, choiceOfValues, multiLine, sortChoices);
		this.feature = feature;
		currentValues = (List<?>) owner.eGet(feature);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		/*
		 * Dialog widgets copied from super
		 */
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		compositeLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		compositeLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		compositeLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		contents.setLayout(compositeLayout);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(contents);
		GridLayout contentsGridLayout = (GridLayout) contents.getLayout();
		contentsGridLayout.numColumns = 3;

		GridData contentsGridData = (GridData) contents.getLayoutData();
		contentsGridData.horizontalAlignment = SWT.FILL;
		contentsGridData.verticalAlignment = SWT.FILL;

		Text patternText = null;

		if (choiceOfValues != null) {
			Group filterGroupComposite = new Group(contents, SWT.NONE);
			filterGroupComposite.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Choices_pattern_group")); //$NON-NLS-1$
			filterGroupComposite.setLayout(new GridLayout(2, false));
			filterGroupComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 3, 1));

			Label label = new Label(filterGroupComposite, SWT.NONE);
			label.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Choices_pattern_label")); //$NON-NLS-1$

			patternText = new Text(filterGroupComposite, SWT.BORDER);
			patternText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		Composite choiceComposite = new Composite(contents, SWT.NONE);
		{
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.horizontalAlignment = SWT.END;
			choiceComposite.setLayoutData(data);

			GridLayout layout = new GridLayout();
			data.horizontalAlignment = SWT.FILL;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 1;
			choiceComposite.setLayout(layout);
		}

		Label choiceLabel = new Label(choiceComposite, SWT.NONE);
		choiceLabel.setText(choiceOfValues == null ? EMFEditUIPlugin.INSTANCE.getString("_UI_Value_label") : EMFEditUIPlugin.INSTANCE //$NON-NLS-1$
				.getString("_UI_Choices_label")); //$NON-NLS-1$
		GridData choiceLabelGridData = new GridData();
		choiceLabelGridData.verticalAlignment = SWT.FILL;
		choiceLabelGridData.horizontalAlignment = SWT.FILL;
		choiceLabel.setLayoutData(choiceLabelGridData);

		final Table choiceTable = choiceOfValues == null ? null : new Table(choiceComposite, SWT.MULTI | SWT.BORDER);
		if (choiceTable != null) {
			GridData choiceTableGridData = new GridData();
			choiceTableGridData.widthHint = Display.getCurrent().getBounds().width / 5;
			choiceTableGridData.heightHint = Display.getCurrent().getBounds().height / 3;
			choiceTableGridData.verticalAlignment = SWT.FILL;
			choiceTableGridData.horizontalAlignment = SWT.FILL;
			choiceTableGridData.grabExcessHorizontalSpace = true;
			choiceTableGridData.grabExcessVerticalSpace = true;
			choiceTable.setLayoutData(choiceTableGridData);
		}

		final TableViewer choiceTableViewer = choiceOfValues == null ? null : new TableViewer(choiceTable);
		if (choiceTableViewer != null) {
			choiceTableViewer.setContentProvider(new AdapterFactoryContentProvider(new AdapterFactoryImpl()));
			choiceTableViewer.setLabelProvider(labelProvider);
			final PatternFilter filter = new PatternFilter() {
				@Override
				protected boolean isParentMatch(Viewer viewer, Object element) {
					return viewer instanceof AbstractTreeViewer && super.isParentMatch(viewer, element);
				}
			};
			choiceTableViewer.addFilter(filter);
			assert patternText != null;
			patternText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					filter.setPattern(((Text) e.widget).getText());
					choiceTableViewer.refresh();
				}
			});
			choiceTableViewer.setInput(new ItemProvider(choiceOfValues));
		}

		// We use multi even for a single line because we want to respond to the enter key.
		int style = multiLine ? SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER : SWT.MULTI | SWT.BORDER;
		final Text choiceText = choiceOfValues == null ? new Text(choiceComposite, style) : null;
		if (choiceText != null) {
			GridData choiceTextGridData = new GridData();
			choiceTextGridData.widthHint = Display.getCurrent().getBounds().width / 5;
			choiceTextGridData.verticalAlignment = SWT.BEGINNING;
			choiceTextGridData.horizontalAlignment = SWT.FILL;
			choiceTextGridData.grabExcessHorizontalSpace = true;
			if (multiLine) {
				choiceTextGridData.verticalAlignment = SWT.FILL;
				choiceTextGridData.grabExcessVerticalSpace = true;
			}
			choiceText.setLayoutData(choiceTextGridData);
		}

		Composite controlButtons = new Composite(contents, SWT.NONE);
		GridData controlButtonsGridData = new GridData();
		controlButtonsGridData.verticalAlignment = SWT.FILL;
		controlButtonsGridData.horizontalAlignment = SWT.FILL;
		controlButtons.setLayoutData(controlButtonsGridData);

		GridLayout controlsButtonGridLayout = new GridLayout();
		controlButtons.setLayout(controlsButtonGridLayout);

		new Label(controlButtons, SWT.NONE);

		final Button addButton = new Button(controlButtons, SWT.PUSH);
		addButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Add_label")); //$NON-NLS-1$
		GridData addButtonGridData = new GridData();
		addButtonGridData.verticalAlignment = SWT.FILL;
		addButtonGridData.horizontalAlignment = SWT.FILL;
		addButton.setLayoutData(addButtonGridData);

		final Button removeButton = new Button(controlButtons, SWT.PUSH);
		removeButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Remove_label")); //$NON-NLS-1$
		GridData removeButtonGridData = new GridData();
		removeButtonGridData.verticalAlignment = SWT.FILL;
		removeButtonGridData.horizontalAlignment = SWT.FILL;
		removeButton.setLayoutData(removeButtonGridData);

		Label spaceLabel = new Label(controlButtons, SWT.NONE);
		GridData spaceLabelGridData = new GridData();
		spaceLabelGridData.verticalSpan = 2;
		spaceLabel.setLayoutData(spaceLabelGridData);

		final Button upButton = new Button(controlButtons, SWT.PUSH);
		upButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Up_label")); //$NON-NLS-1$
		GridData upButtonGridData = new GridData();
		upButtonGridData.verticalAlignment = SWT.FILL;
		upButtonGridData.horizontalAlignment = SWT.FILL;
		upButton.setLayoutData(upButtonGridData);

		final Button downButton = new Button(controlButtons, SWT.PUSH);
		downButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Down_label")); //$NON-NLS-1$
		GridData downButtonGridData = new GridData();
		downButtonGridData.verticalAlignment = SWT.FILL;
		downButtonGridData.horizontalAlignment = SWT.FILL;
		downButton.setLayoutData(downButtonGridData);

		Composite featureComposite = new Composite(contents, SWT.NONE);
		{
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.horizontalAlignment = SWT.END;
			featureComposite.setLayoutData(data);

			GridLayout layout = new GridLayout();
			data.horizontalAlignment = SWT.FILL;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 1;
			featureComposite.setLayout(layout);
		}

		Label featureLabel = new Label(featureComposite, SWT.NONE);
		featureLabel.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Feature_label")); //$NON-NLS-1$
		GridData featureLabelGridData = new GridData();
		featureLabelGridData.horizontalSpan = 2;
		featureLabelGridData.horizontalAlignment = SWT.FILL;
		featureLabelGridData.verticalAlignment = SWT.FILL;
		featureLabel.setLayoutData(featureLabelGridData);

		final Table featureTable = new Table(featureComposite, SWT.MULTI | SWT.BORDER);
		GridData featureTableGridData = new GridData();
		featureTableGridData.widthHint = Display.getCurrent().getBounds().width / 5;
		featureTableGridData.heightHint = Display.getCurrent().getBounds().height / 3;
		featureTableGridData.verticalAlignment = SWT.FILL;
		featureTableGridData.horizontalAlignment = SWT.FILL;
		featureTableGridData.grabExcessHorizontalSpace = true;
		featureTableGridData.grabExcessVerticalSpace = true;
		featureTable.setLayoutData(featureTableGridData);

		final TableViewer featureTableViewer = new TableViewer(featureTable);
		featureTableViewer.setContentProvider(contentProvider);
		featureTableViewer.setLabelProvider(labelProvider);
		featureTableViewer.setInput(values);
		if (!values.getChildren().isEmpty()) {
			featureTableViewer.setSelection(new StructuredSelection(values.getChildren().get(0)));
		}

		if (choiceTableViewer != null) {
			choiceTableViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					if (addButton.isEnabled()) {
						addButton.notifyListeners(SWT.Selection, null);
					}
				}
			});

			featureTableViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					if (removeButton.isEnabled()) {
						removeButton.notifyListeners(SWT.Selection, null);
					}
				}
			});
		}

		if (choiceText != null) {
			choiceText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent event) {
					if (!multiLine && (event.character == '\r' || event.character == '\n')) {
						try {
							Object value = EcoreUtil.createFromString((EDataType) eClassifier, choiceText.getText());
							values.getChildren().add(value);
							choiceText.setText(""); //$NON-NLS-1$
							featureTableViewer.setSelection(new StructuredSelection(value));
							event.doit = false;
						} catch (RuntimeException exception) {
							// Ignore
						}
					} else if (event.character == '\33') {
						choiceText.setText(""); //$NON-NLS-1$
						event.doit = false;
					}
				}
			});
		}

		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTableViewer.getSelection();
				int minIndex = 0;
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					int index = values.getChildren().indexOf(value);
					values.getChildren().move(Math.max(index - 1, minIndex++), value);
				}
			}
		});

		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTableViewer.getSelection();
				int maxIndex = values.getChildren().size() - selection.size();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					int index = values.getChildren().indexOf(value);
					values.getChildren().move(Math.min(index + 1, maxIndex++), value);
				}
			}
		});

		addButton.addSelectionListener(new SelectionAdapter() {
			// event is null when choiceTableViewer is double clicked
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (choiceTableViewer != null) {
					IStructuredSelection selection = (IStructuredSelection) choiceTableViewer.getSelection();
					for (Iterator<?> i = selection.iterator(); i.hasNext();) {
						Object value = i.next();
						// FIXME Use commented code once we don't need to support Eclipse 3.5 any longer
						// if (!unique || !values.getChildren().contains(value)) {
						if (!values.getChildren().contains(value)) {
							values.getChildren().add(value);
						}
					}
					featureTableViewer.setSelection(selection);
				} else if (choiceText != null) {
					try {
						Object value = EcoreUtil.createFromString((EDataType) eClassifier, choiceText.getText());
						// FIXME Use commented code once we don't need to support Eclipse 3.5 any longer
						// if (!unique || !values.getChildren().contains(value)) {
						if (!values.getChildren().contains(value)) {
							values.getChildren().add(value);
							choiceText.setText(""); //$NON-NLS-1$
						}
						featureTableViewer.setSelection(new StructuredSelection(value));
					} catch (RuntimeException exception) {
						// Ignore
					}
				}
			}
		});

		removeButton.addSelectionListener(new SelectionAdapter() {
			// event is null when featureTableViewer is double clicked
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTableViewer.getSelection();
				Object firstValue = null;
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					if (firstValue == null) {
						firstValue = value;
					}
					values.getChildren().remove(value);
				}

				if (!values.getChildren().isEmpty()) {
					featureTableViewer.setSelection(new StructuredSelection(values.getChildren().get(0)));
				}

				if (choiceTableViewer != null) {
					choiceTableViewer.setSelection(selection);
				} else if (choiceText != null) {
					if (firstValue != null) {
						String value = EcoreUtil.convertToString((EDataType) eClassifier, firstValue);
						choiceText.setText(value);
					}
				}
			}
		});

		/*
		 * Additional dialog widgets for editing proxy URI of proxified objects in feature table
		 */
		final Label proxyURILabel = new Label(featureComposite, SWT.NONE);
		proxyURILabel.setText(Messages.label_editProxyURI);

		final Text proxyURIText = new Text(featureComposite, SWT.BORDER);
		proxyURIText.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Text proxyURIErrorText = new Text(featureComposite, SWT.NONE);
		proxyURIErrorText.setLayoutData(new GridData(GridData.FILL_BOTH));
		proxyURIErrorText.setEditable(false);

		proxyURIText.setVisible(false);
		proxyURILabel.setVisible(false);
		proxyURIErrorText.setVisible(false);

		proxyURIText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text = proxyURIText.getText();
				String errorMessage = null;
				if (object instanceof EObject) {
					EObject eObject = (EObject) object;
					ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(eObject.eResource());
					if (extendedResource != null) {
						Diagnostic diagnostic = extendedResource.validateURI(text);
						errorMessage = Diagnostic.OK_INSTANCE.equals(diagnostic) ? null : diagnostic.getMessage();
					}
				}

				if (errorMessage == null) {
					proxyURIErrorText.setVisible(false);

					IStructuredSelection selection = (IStructuredSelection) featureTableViewer.getSelection();
					if (selection != null) {
						Object selected = selection.getFirstElement();
						if (selected instanceof EObject) {
							InternalEObject internalValue = (InternalEObject) selected;
							if (internalValue.eIsProxy()) {
								URI oldProxyURI = internalValue.eProxyURI();
								if (!text.equals(oldProxyURI.toString())) {
									URI newProxyURI = URI.createURI(text);
									internalValue.eSetProxyURI(newProxyURI);
									boolean didChangeProxyURI = true;

									// Try to resolve value proxy
									EObject newValue = EcoreUtil.resolve(internalValue, (EObject) object);

									// Value proxy resolution successful?
									if (!newValue.eIsProxy()) {
										// Remove value proxy
										values.getChildren().remove(internalValue);

										// Resolved value not already contained in values list?
										if (!values.getChildren().contains(newValue)) {
											// Add resolved value
											values.getChildren().add(newValue);
										} else {
											// Restore value proxy with previous proxy URI
											values.getChildren().add(internalValue);
											internalValue.eSetProxyURI(oldProxyURI);
											didChangeProxyURI = false;

											proxyURIErrorText.setVisible(true);
											proxyURIErrorText.setText(Messages.message_proxyURIReferencesElementInList);
										}
									}

									// Notify adapters about value change arising from value proxy URI change if
									// required
									/*
									 * !! Important Note !! Don't raise notification with value object as notifier and
									 * eProxyURI as "feature". The change of the value object's proxy URI is
									 * semantically equivalent with removing the value object with the old proxy URI
									 * from the owner and adding another value object with the new proxy URI. Therefore
									 * notification must happen wrt owner object and feature of value object.
									 */
									InternalEObject internalOwner = (InternalEObject) object;
									if (didChangeProxyURI && internalOwner.eNotificationRequired()) {
										// Restore old value proxy
										EFactory eFactoryInstance = newValue.eClass().getEPackage().getEFactoryInstance();
										InternalEObject internalOldValue = (InternalEObject) eFactoryInstance.create(newValue.eClass());
										internalOldValue.eSetProxyURI(oldProxyURI);

										// Retrieve position of old and new value
										int index = currentValues.indexOf(newValue);

										// Deliver remove notification for old value proxy and add notification for new
										// value
										NotificationChain notifications = new ENotificationImpl(internalOwner, Notification.REMOVE, feature,
												internalOldValue, null, index);
										notifications.add(new ENotificationImpl(internalOwner, Notification.ADD, feature, null, newValue, index));
										notifications.dispatch();
									}

									// Refresh the featureTableViewer to see new object appear in selections
									featureTableViewer.refresh();
								}
							}
						}
					}
				} else {
					proxyURIErrorText.setVisible(true);
					proxyURIErrorText.setText(errorMessage);
				}
			}
		});

		// Listener that disables/enables proxy URI widgets when feature table selection changes
		featureTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (!selection.isEmpty()) {
					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof EObject) {
						InternalEObject eObjectElement = (InternalEObject) firstElement;
						proxyURILabel.setVisible(eObjectElement.eIsProxy());
						proxyURIErrorText.setVisible(eObjectElement.eIsProxy());
						if (eObjectElement.eIsProxy()) {
							proxyURIText.setText(eObjectElement.eProxyURI().toString());
						}
						proxyURIText.setVisible(eObjectElement.eIsProxy());
						return;
					}
				}
				proxyURIText.setVisible(false);
				proxyURILabel.setVisible(false);
				proxyURIErrorText.setVisible(false);
			}
		});

		// Listener that disables proxy URI widgets when choice table selection changes
		choiceTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				proxyURIText.setVisible(false);
				proxyURILabel.setVisible(false);
				proxyURIErrorText.setVisible(false);
			}
		});

		return contents;
	}
}
