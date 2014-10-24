/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.edit;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.StyledString;

public class StyledDelegatingWrapperItemProvider extends DelegatingWrapperItemProvider implements IItemFontProvider, IItemStyledLabelProvider {

	public StyledDelegatingWrapperItemProvider(Object value, Object owner, EStructuralFeature feature, int index, AdapterFactory adapterFactory) {
		super(value, owner, feature, index, adapterFactory);
	}

	@Override
	public Object getStyledText(Object object) {
		return delegateItemProvider instanceof IItemStyledLabelProvider ? ((IItemStyledLabelProvider) delegateItemProvider)
				.getStyledText(getDelegateValue()) : new StyledString(getText(object));
	}

	@Override
	protected IWrapperItemProvider createWrapper(Object value, Object owner, AdapterFactory adapterFactory) {
		return new StyledDelegatingWrapperItemProvider(value, owner, null, CommandParameter.NO_INDEX, adapterFactory);
	}
}
