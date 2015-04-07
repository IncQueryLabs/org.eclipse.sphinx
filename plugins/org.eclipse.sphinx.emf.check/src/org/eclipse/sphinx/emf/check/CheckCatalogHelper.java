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
 *     itemis - [460445] CheckCatalogHelper error message when catalog not found
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check;

import java.util.Collections;
import java.util.List;

import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.Category;
import org.eclipse.sphinx.emf.check.catalog.Constraint;
import org.eclipse.sphinx.emf.check.catalog.Severity;

// TODO Move these method to CheckCatalog by using GenModel annotations or operation invocation delegates
public class CheckCatalogHelper {

	private ICheckValidator checkValidator;

	public CheckCatalogHelper(ICheckValidator checkValidator) {
		this.checkValidator = checkValidator;
	}

	public String getMessage(String constraintId) {
		Catalog catalog = getCatalog();
		if (catalog != null) {
			for (Constraint constraint : catalog.getConstraints()) {
				String id = constraint.getId();
				if (id != null && id.equals(constraintId)) {
					return constraint.getMessage();
				}
			}
		}
		return null;
	}

	public List<Category> getCategories() {
		Catalog catalog = getCatalog();
		if (catalog != null) {
			return Collections.unmodifiableList(catalog.getCategories());
		}
		return Collections.emptyList();
	}

	public Severity getSeverity(String constraintId) {
		Catalog catalog = getCatalog();
		if (catalog != null) {
			for (Constraint contraint : catalog.getConstraints()) {
				String id = contraint.getId();
				if (id != null && id.equals(constraintId)) {
					return contraint.getSeverity();
				}
			}
		}
		return Severity.ERROR;
	}

	public Catalog getCatalog() {
		return CheckValidatorRegistry.INSTANCE.getCheckCatalog(checkValidator);
	}
}