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
package org.eclipse.sphinx.emf.check;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Category;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Constraint;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Severity;
import org.eclipse.sphinx.emf.check.registry.CheckValidatorRegistry;

public class CheckCatalogHelper {

	private Catalog catalog;

	public CheckCatalogHelper(ICheckValidator checkValidator) {
		String fqn = checkValidator.getClass().getName();
		URI uri = CheckValidatorRegistry.INSTANCE.getCheckModelURI(fqn);
		if (uri != null) {
			Resource checkResource = new ResourceSetImpl().getResource(uri, true);
			EObject eObject = checkResource.getContents().get(0);
			Assert.isNotNull(eObject);
			if (!(eObject instanceof Catalog)) {
				throw new RuntimeException("Could not find the check model Catalogue!"); //$NON-NLS-1$
			}
			setCatalog((Catalog) eObject);
		}
	}

	public String getMessage(String constraint) {
		if (catalog != null) {
			EList<Constraint> constraints = catalog.getConstraints();
			for (Constraint c : constraints) {
				String name = c.getId();
				if (name.equals(constraint)) {
					return c.getMessage();
				}
			}
		}
		return null;
	}

	public Set<Category> getCategories() {
		if (catalog != null) {
			return new HashSet<Category>(catalog.getCategories());
		}
		return new HashSet<Category>();
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
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
}