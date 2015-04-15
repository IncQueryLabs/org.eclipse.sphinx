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
 *     itemis - [463895] org.eclipse.sphinx.emf.check.AbstractCheckValidator.validate(EClass, EObject, DiagnosticChain, Map<Object, Object>) throws NPE
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.sphinx.emf.check.Check;
import org.eclipse.sphinx.emf.check.CheckValidatorRegistry;
import org.eclipse.sphinx.emf.check.CheckValidatorState;
import org.eclipse.sphinx.emf.check.ICheckValidationConstants;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.Category;

public class CheckMethodWrapper {

	public static Set<String> getAnnotatedCategories(Check checkAnnotation) {
		if (checkAnnotation != null) {
			String[] categories = checkAnnotation.categories();
			if (categories.length > 0) {
				// Ignore isolate empty categories
				if (categories.length > 1 || !categories[0].isEmpty()) {
					return new HashSet<String>(Arrays.asList(categories));
				}
			}
		}
		return Collections.emptySet();
	}

	private ICheckValidator validator;

	private Method method;
	private String signature;
	private Check checkAnnotation;

	public CheckMethodWrapper(ICheckValidator validator, Method method) {
		Assert.isNotNull(validator);
		Assert.isNotNull(method);

		this.validator = validator;
		this.method = method;
		signature = method.getName() + ":" + method.getParameterTypes()[0].getName(); //$NON-NLS-1$
		checkAnnotation = method.getAnnotation(Check.class);
	}

	public ICheckValidator getValidator() {
		return validator;
	}

	public Method getMethod() {
		return method;
	}

	public String getAnnotatedConstraint() {
		return checkAnnotation.constraint();
	}

	public Set<String> getAnnotatedCategories() {
		return getAnnotatedCategories(checkAnnotation);
	}

	private boolean isOtherCategorySelected(Set<String> selectedCategories) {
		Assert.isNotNull(selectedCategories);

		for (String categoryId : selectedCategories) {
			if (categoryId.equals(ICheckValidationConstants.CATEGORY_ID_OTHER)) {
				return true;
			}
		}
		return false;
	}

	public void invoke(CheckValidatorState state, Set<String> selectedCategories) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		if (validator.getState().get() != null && validator.getState().get() != state) {
			throw new IllegalStateException("State is already assigned."); //$NON-NLS-1$
		}
		boolean wasNull = validator.getState().get() == null;
		if (wasNull) {
			validator.getState().set(state);
		}
		try {

			if (!state.checkValidationMode.shouldCheck(checkAnnotation.value())) {
				return;
			}

			Set<String> categories = new HashSet<String>();
			categories.addAll(selectedCategories);

			// If no categories are specified in the check method's @Check annotation, invoke the check method on all
			// categories as per the underlying constraint in the check catalog; otherwise invoke the check method on
			// the intersection of categories provided by the user, and the categories defined in the check
			// catalog.
			Catalog catalog = CheckValidatorRegistry.INSTANCE.getCheckCatalog(validator);
			Set<String> annotatedCategories = getAnnotatedCategories();
			if (annotatedCategories.isEmpty() && !isOtherCategorySelected(selectedCategories)) {
				return;
			}
			if (!annotatedCategories.isEmpty()) {
				categories.retainAll(annotatedCategories);
			}
			// Make intersection with categories associated with this validator in check catalog
			if (catalog != null && !catalog.getCategories().isEmpty()) {
				retainAll(categories, catalog.getCategories());
			}
			// Go ahead if scope is not empty or if validator has no check catalog
			if (!categories.isEmpty() || catalog == null) {
				state.currentMethod = method;
				state.currentCheckType = checkAnnotation.value();
				state.constraint = getAnnotatedConstraint();
				method.setAccessible(true);
				method.invoke(validator, state.currentObject);
			}
		} finally {
			if (wasNull) {
				validator.getState().set(null);
			}
		}
	}

	private void retainAll(Set<String> categories, EList<Category> categoryList) {
		Set<String> categoryIDs = new HashSet<String>();
		for (Category category : categoryList) {
			categoryIDs.add(category.getId());
		}
		categories.retainAll(categoryIDs);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CheckMethodWrapper)) {
			return false;
		}
		CheckMethodWrapper mw = (CheckMethodWrapper) obj;
		return signature.equals(mw.signature) && validator == mw.validator;
	}

	@Override
	public int hashCode() {
		return signature.hashCode() ^ validator.hashCode();
	}

	@Override
	public String toString() {
		return "CheckMethodWrapper [method=" + method + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
