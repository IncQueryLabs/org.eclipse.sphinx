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

package org.eclipse.sphinx.emf.check.ui.actions.dialog;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.check.CheckModelHelper;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Category;

public class CategorySelectionContentProvider implements IStructuredContentProvider {

	private final CheckModelHelper helper;

	public CategorySelectionContentProvider(CheckModelHelper helper) {
		super();
		this.helper = helper;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Set<Category> categories = helper.getCategories();
		return categories.toArray();
	}
}
