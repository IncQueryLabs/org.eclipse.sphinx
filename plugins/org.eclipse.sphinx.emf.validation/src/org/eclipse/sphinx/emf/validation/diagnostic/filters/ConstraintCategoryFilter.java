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
package org.eclipse.sphinx.emf.validation.diagnostic.filters;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.model.Category;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.emf.validation.service.IConstraintFilter;

/**
 * Constraint filter whose criteria is the constraint category id pattern. This filter only accepts constraints whose
 * category id matches the pattern passed to the constructor.
 */
public class ConstraintCategoryFilter implements IConstraintFilter {

	private String categoryIdPattern;

	/**
	 * Constructor.
	 * 
	 * @param categoryIdPattern
	 *            A regular expression for the ids of the constraint categories which make it through this filter.
	 */
	public ConstraintCategoryFilter(String categoryIdPattern) {
		Assert.isNotNull(categoryIdPattern);
		this.categoryIdPattern = categoryIdPattern;
	}

	public boolean accept(IConstraintDescriptor constraint, EObject target) {
		if (constraint != null) {
			for (Category category : constraint.getCategories()) {
				// Iterate over category and its ancestors
				while (category != null) {
					if (category.getId().matches(categoryIdPattern)) {
						return true;
					}
					// Get the parent category
					category = category.getParent();
				}
			}
		}
		return false;
	}
}
