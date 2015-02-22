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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.Category;
import org.eclipse.sphinx.emf.check.catalog.Constraint;
import org.eclipse.sphinx.emf.check.catalog.Severity;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;

public class CheckCatalogHelper {

	private ICheckValidator checkValidator;
	private Catalog catalog = null;

	public CheckCatalogHelper(ICheckValidator checkValidator) {
		this.checkValidator = checkValidator;
	}

	public String getMessage(String constraintId) {
		Catalog catalog = getCatalog();
		if (catalog != null) {
			EList<Constraint> constraints = catalog.getConstraints();
			for (Constraint constraint : constraints) {
				String id = constraint.getId();
				if (id.equals(constraintId)) {
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

	public Severity getSeverityType(String constraint) {
		for (Constraint contraintInCatalog : catalog.getConstraints()) {
			String name = contraintInCatalog.getId();
			if (name.equals(constraint)) {
				return contraintInCatalog.getSeverity();
			}
		}
		return Severity.ERROR;
	}

	public Catalog getCatalog() {
		if (catalog == null) {
			URI uri = CheckValidatorRegistry.INSTANCE.getCheckCatalogURI(checkValidator);
			if (uri != null) {
				EObject eObject = EcoreResourceUtil.loadEObject(null, uri.appendFragment("/")); //$NON-NLS-1$
				if (!(eObject instanceof Catalog)) {
					throw new IllegalStateException(
							"Unable to find the check catalog '" + uri.toString() + "' for check validator '" + checkValidator.getClass().getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				catalog = (Catalog) eObject;
			}
		}
		return catalog;
	}
}