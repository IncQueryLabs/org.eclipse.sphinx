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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sphinx.emf.check.AbstractCheckValidator.CheckValidatorState;
import org.eclipse.sphinx.emf.check.internal.Activator;
import org.eclipse.sphinx.emf.check.util.Exceptions;
import org.eclipse.sphinx.emf.check.util.GuardException;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class MethodWrapper {

	private final ICheckValidator validator;

	private final Method method;
	private final String signature;
	private final Check checkAnnotation;
	private final String[] selectedCategories;

	protected MethodWrapper(ICheckValidator validator, Method method, String[] selectedCategories) {
		this.validator = validator;
		this.method = method;
		this.selectedCategories = selectedCategories;
		signature = method.getName() + ":" + method.getParameterTypes()[0].getName(); //$NON-NLS-1$
		checkAnnotation = method.getAnnotation(Check.class);
	}

	public ICheckValidator getValidator() {
		return validator;
	}

	public Method getMethod() {
		return method;
	}

	public String getConstraint() {
		return checkAnnotation.constraint();
	}

	public String[] getCategories() {
		return checkAnnotation.categories();
	}

	public boolean matches(Class<?> param) {
		return method.getParameterTypes()[0].isAssignableFrom(param);
	}

	public void invoke(CheckValidatorState state) {
		if (validator.getState().get() != null && validator.getState().get() != state) {
			throw new IllegalStateException("State is already assigned."); //$NON-NLS-1$
		}
		boolean wasNull = validator.getState().get() == null;
		if (wasNull) {
			validator.getState().set(state);
		}
		try {
			if (!state.checkMode.shouldCheck(checkAnnotation.value())) {
				return;
			}

			Set<String> annotatedCategoriesSet = null;
			String[] categories = getCategories();
			if (categories.length == 1 && categories[0].isEmpty()) {
				annotatedCategoriesSet = new HashSet<String>();
			} else {
				annotatedCategoriesSet = new HashSet<String>(Arrays.asList(categories));
			}
			Set<String> selectedCategoriesSet = new HashSet<String>(Arrays.asList(selectedCategories));

			// If no categories are specified in the check method's @Check annotation, invoke the check method on all
			// categories as per the underlying constraint in the check catalog; otherwise invoke the check method on
			// the intersection of categories provided by the user, and the categories defined in the check
			// catalog.
			if (!annotatedCategoriesSet.isEmpty()) {
				selectedCategoriesSet.retainAll(annotatedCategoriesSet);
			}
			// go ahead if scope is not empty or if validator has no check catalog
			if (!selectedCategoriesSet.isEmpty() || validator.getCheckCatalogHelper().getCatalog() == null) {
				try {
					state.currentMethod = method;
					state.currentCheckType = checkAnnotation.value();
					state.constraint = getConstraint();
					method.setAccessible(true);
					method.invoke(validator, state.currentObject);

				} catch (IllegalArgumentException e) {
					PlatformLogUtil.logAsError(Activator.getPlugin(), e);
				} catch (IllegalAccessException e) {
					PlatformLogUtil.logAsError(Activator.getPlugin(), e);
				} catch (InvocationTargetException e) {
					Throwable targetException = e.getTargetException();
					handleInvocationTargetException(targetException, state);
				}
			}
		} finally {
			if (wasNull) {
				validator.getState().set(null);
			}
		}
	}

	protected void handleInvocationTargetException(Throwable targetException, CheckValidatorState state) {
		// ignore GuardException, check is just not evaluated if guard is false
		// ignore NullPointerException, as not having to check for NPEs all the time is a convenience feature
		if (!(targetException instanceof GuardException) && !(targetException instanceof NullPointerException)) {
			Exceptions.throwUncheckedException(targetException);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MethodWrapper)) {
			return false;
		}
		MethodWrapper mw = (MethodWrapper) obj;
		return signature.equals(mw.signature) && validator == mw.validator;
	}

	@Override
	public int hashCode() {
		return signature.hashCode() ^ validator.hashCode();
	}
}
