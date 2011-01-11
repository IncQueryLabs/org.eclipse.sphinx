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
package org.eclipse.sphinx.examples.common.ui.providers;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.ui.properties.BasicTabbedPropertySheetTitleProvider;

public class AppearanceExampleTabbedPropertySheetTitleProvider extends BasicTabbedPropertySheetTitleProvider {

	private final TypeNameLabelDecorator typeNameLabelDecorator;

	public AppearanceExampleTabbedPropertySheetTitleProvider() {
		typeNameLabelDecorator = new TypeNameLabelDecorator();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) element;
			element = selection.getFirstElement();
		}

		String text = super.getText(element);
		return typeNameLabelDecorator.decorateText(text, element);
	}
}
