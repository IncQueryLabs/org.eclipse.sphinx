/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [458976] Validators are not singleton when they implement checks for different EPackages
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.ui.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.check.CheckCatalogHelper;
import org.eclipse.sphinx.emf.check.catalog.Category;

public class CategorySelectionContentProvider implements IStructuredContentProvider {

	private final Set<CheckCatalogHelper> helpers;

	public CategorySelectionContentProvider(Set<CheckCatalogHelper> helpers) {
		this.helpers = helpers;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Map<String, Category> categories = new HashMap<String, Category>();
		for (CheckCatalogHelper helper : helpers) {
			for (Category category : helper.getCategories()) {
				categories.put(category.getId(), category);
			}
		}
		return categories.values().toArray(new Category[categories.size()]);
	}

	@Override
	public void dispose() {
		// Do nothing
	}
}
