/**
 * <copyright>
 *
 * Copyright (c) 2008-2016 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [408533] Inner commands of Copy/Cut/Delete/Paste commands not created correctly when using ExtendedXxxAction with custom adapter factory
 *     itemis - [481581] Improve refresh behavior of BasicModelContentProvider to avoid performance problems due to needlessly repeated tree state restorations
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.actions;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.CopyAction;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ExtendedCopyAction extends CopyAction {

	protected AdapterFactory customAdapterFactory;

	public ExtendedCopyAction(EditingDomain domain, AdapterFactory customAdapterFactory) {
		super(domain);
		this.customAdapterFactory = customAdapterFactory;
	}

	public ExtendedCopyAction(AdapterFactory customAdapterFactory) {
		this(null, customAdapterFactory);
	}

	/*
	 * @see org.eclipse.emf.edit.ui.action.CommandActionHandler#updateSelection(org.eclipse.jface.viewers.
	 * IStructuredSelection )
	 */
	@Override
	public boolean updateSelection(IStructuredSelection selection) {
		if (domain != null) {
			AdapterFactory oldAdapterFactory = null;
			if (customAdapterFactory != null) {
				oldAdapterFactory = ((AdapterFactoryEditingDomain) domain).getAdapterFactory();
				((AdapterFactoryEditingDomain) domain).setAdapterFactory(customAdapterFactory);
			}
			boolean result = super.updateSelection(selection);
			if (oldAdapterFactory != null) {
				((AdapterFactoryEditingDomain) domain).setAdapterFactory(oldAdapterFactory);
			}
			return result;
		}
		return false;
	}
}
