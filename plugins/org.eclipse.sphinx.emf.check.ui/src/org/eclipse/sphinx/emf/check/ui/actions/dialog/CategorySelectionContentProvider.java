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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.check.CheckCatalogHelper;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Category;

public class CategorySelectionContentProvider implements IStructuredContentProvider {

	private final Set<CheckCatalogHelper> helpers;

	public CategorySelectionContentProvider(Set<CheckCatalogHelper> helpers) {
		super();
		this.helpers = helpers;
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
		Set<Category> categories = new HashSet<Category>();
		for (CheckCatalogHelper helper : helpers) {
			Set<Category> categoriesSubset = helper.getCategories();
			for (Category category : categoriesSubset) {
				boolean equality = false;
				for (Category c : categories) {
					if (c.equals(category)) {
						equality = true;
					}
				}
				if (!equality) {
					categories.add(category);
				}
			}
		}
		return categories.toArray();
	}

}
