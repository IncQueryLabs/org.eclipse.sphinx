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

import java.util.Collection;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.ui.internal.messages.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.eclipse.ui.navigator.INavigatorContentService;

/**
 * Defines a {@link ILabelProvider label provider} and a {@link IDescriptionProvider description provider} for the title
 * bar in tabbed properties views.
 * <p>
 * In contrast to Eclipse's built-in
 * {@link org.eclipse.ui.internal.navigator.resources.workbench.TabbedPropertySheetTitleProvider} this implementation
 * doesn't initialize the {@link ILabelProvider label provider} and the {@link IDescriptionProvider description
 * provider} in its constructor but lazily when the {@link #getImage(Object)} and {@link #getText(Object)} methods are
 * called. This makes sure that {@link ILabelProvider label provider} and {@link IDescriptionProvider description
 * provider} have a chance to get properly initialized by the time where they are really needed and don't remain
 * <code>null</code> when their initialization fails by the time where this class is instantiated.
 * 
 * @since 0.7.0
 */
public class BasicTabbedPropertySheetTitleProvider extends LabelProvider {

	private ILabelProvider labelProvider;

	private IDescriptionProvider descriptionProvider;

	protected ILabelProvider getLabelProvider() {
		if (labelProvider == null) {
			initProviders();
		}
		return labelProvider;
	}

	protected IDescriptionProvider getDescriptionProvider() {
		if (descriptionProvider == null) {
			initProviders();
		}
		return descriptionProvider;
	}

	protected void initProviders() {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		INavigatorContentService contentService = (INavigatorContentService) part.getAdapter(INavigatorContentService.class);
		if (contentService != null) {
			labelProvider = contentService.createCommonLabelProvider();
			descriptionProvider = contentService.createCommonDescriptionProvider();
		} else {
			IEditingDomainProvider editingDomainProvider = (IEditingDomainProvider) part.getAdapter(IEditingDomainProvider.class);
			EditingDomain editingDomain = editingDomainProvider.getEditingDomain();
			if (editingDomain instanceof TransactionalEditingDomain) {
				AdapterFactory adapterFactory = ((AdapterFactoryEditingDomain) editingDomain).getAdapterFactory();
				if (adapterFactory != null) {
					labelProvider = new TransactionalAdapterFactoryLabelProvider((TransactionalEditingDomain) editingDomain, adapterFactory);
					descriptionProvider = new IDescriptionProvider() {
						public String getDescription(Object anElement) {
							if (anElement instanceof IStructuredSelection) {
								Collection<?> collection = ((IStructuredSelection) anElement).toList();
								switch (collection.size()) {
								case 0: {
									return Messages.label_noObjectSelected;
								}
								case 1: {
									Object object = collection.iterator().next();
									return labelProvider.getText(object);
								}
								default: {
									return NLS.bind(Messages.label_multiObjectSelected, Integer.toString(collection.size()));
								}
								}
							} else {
								// Don't return empty String because otherwise the tabbed property sheet's title bar
								// looses its background color and becomes entirely blank
								return " "; //$NON-NLS-1$
							}
						}
					};
				}
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object anElement) {
		ILabelProvider labelProvider = getLabelProvider();
		if (labelProvider != null) {
			if (anElement instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) anElement;
				if (structuredSelection.size() == 1) {
					return labelProvider.getImage(structuredSelection.getFirstElement());
				}
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object anElement) {
		IDescriptionProvider descriptionProvider = getDescriptionProvider();
		return descriptionProvider != null ? descriptionProvider.getDescription(anElement) : null;
	}
}
