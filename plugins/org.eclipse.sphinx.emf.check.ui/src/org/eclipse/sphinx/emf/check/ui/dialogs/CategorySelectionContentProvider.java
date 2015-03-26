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
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.check.CheckValidatorRegistry;
import org.eclipse.sphinx.emf.check.IValidationConstants;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.Category;
import org.eclipse.sphinx.emf.check.catalog.CheckCatalogFactory;
import org.eclipse.sphinx.emf.check.ui.internal.messages.Messages;

public class CategorySelectionContentProvider implements IStructuredContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Category> elements = new ArrayList<Category>();

		Collection<Catalog> checkCatalogs = CheckValidatorRegistry.INSTANCE.getCheckCatalogs();
		for (Catalog catalog : checkCatalogs) {
			elements.addAll(catalog.getCategories());
		}

		// Add Intrinsic Model Integrity Checks Category
		elements.add(createCategory(IValidationConstants.intrinsic_model_integrity_checks_category_id,
				Messages.intrinsic_model_integrity_checks_category_label, Messages.intrinsic_model_integrity_checks_category_desc));

		// Add Other Category
		elements.add(createCategory(IValidationConstants.other_category_id, Messages.other_category_label, Messages.other_category_desc));

		return elements.toArray(new Category[elements.size()]);
	}

	@Override
	public void dispose() {
		// Do nothing
	}

	private Category createCategory(String id, String label, String desc) {
		Category category = CheckCatalogFactory.eINSTANCE.createCategory();
		category.setId(id);
		category.setLabel(label);
		category.setDescription(desc);
		return category;
	}
}
