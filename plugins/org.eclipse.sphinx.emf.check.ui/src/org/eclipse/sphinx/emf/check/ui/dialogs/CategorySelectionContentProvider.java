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
 *     itemis - [458982] Improve check validation user experience
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.check.CheckValidatorRegistry;
import org.eclipse.sphinx.emf.check.ICheckValidationConstants;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.Category;
import org.eclipse.sphinx.emf.check.catalog.CheckCatalogFactory;
import org.eclipse.sphinx.emf.check.ui.internal.messages.Messages;

public class CategorySelectionContentProvider implements IStructuredContentProvider {

	private Category otherCategory;
	private Category intrinsicChecksCategory;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Category> elements = new ArrayList<Category>();

		Collection<Catalog> checkCatalogs = CheckValidatorRegistry.INSTANCE.getCheckCatalogs();
		Map<String, Category> categories = new HashMap<String, Category>();
		for (Catalog catalog : checkCatalogs) {
			for (Category category : catalog.getCategories()) {
				categories.put(category.getId(), category);
			}
		}
		elements.addAll(categories.values());

		// Add Intrinsic Model Integrity Checks Category
		elements.add(getIntrinsicChecksCategory());

		// Add Other Category
		elements.add(getOtherCategory());

		return elements.toArray(new Category[elements.size()]);
	}

	@Override
	public void dispose() {
		// Do nothing
	}

	protected Category getOtherCategory() {
		if (otherCategory == null) {
			otherCategory = createCategory(ICheckValidationConstants.CATEGORY_ID_OTHER, Messages.other_category_label, Messages.other_category_desc);
		}
		return otherCategory;
	}

	protected Category getIntrinsicChecksCategory() {
		if (intrinsicChecksCategory == null) {
			intrinsicChecksCategory = createCategory(ICheckValidationConstants.CATEGORY_ID_INTRINSIC,
					Messages.intrinsic_model_integrity_checks_category_label, Messages.intrinsic_model_integrity_checks_category_desc);
		}
		return intrinsicChecksCategory;
	}

	private Category createCategory(String id, String label, String desc) {
		Category category = CheckCatalogFactory.eINSTANCE.createCategory();
		category.setId(id);
		category.setLabel(label);
		category.setDescription(desc);
		return category;
	}
}
