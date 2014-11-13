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

import org.eclipse.sphinx.emf.check.AbstractCheckValidator.State;
import org.eclipse.sphinx.emf.check.internal.Activator;
import org.eclipse.sphinx.emf.check.util.Exceptions;
import org.eclipse.sphinx.emf.check.util.GuardException;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class MethodWrapper {

	private final AbstractCheckValidator instance;

	private final Method method;
	private final String signature;
	private final String constraint;
	private final String[] categories;

	protected MethodWrapper(AbstractCheckValidator instance, Method method, String constraint, String[] categories) {
		this.instance = instance;
		this.method = method;
		this.constraint = constraint;
		this.categories = categories;
		signature = method.getName() + ":" + method.getParameterTypes()[0].getName(); //$NON-NLS-1$
	}

	@Override
	public int hashCode() {
		return signature.hashCode() ^ instance.hashCode();
	}

	public boolean isMatching(Class<?> param) {
		return method.getParameterTypes()[0].isAssignableFrom(param);
	}

	public void invoke(State state) {
		if (instance.getState().get() != null && instance.getState().get() != state) {
			throw new IllegalStateException("State is already assigned."); //$NON-NLS-1$
		}
		boolean wasNull = instance.getState().get() == null;
		if (wasNull) {
			instance.getState().set(state);
		}
		try {
			Check annotation = method.getAnnotation(Check.class);
			if (!state.checkMode.shouldCheck(annotation.value())) {
				return;
			}

			String[] categories = annotation.categories();
			Set<String> categoriesSet = new HashSet<String>(Arrays.asList(categories));
			Set<String> filter = instance.getFilter();
			Set<String> intersection = new HashSet<String>(filter);
			intersection.retainAll(categoriesSet);
			if (!intersection.isEmpty()) {
				try {
					state.currentMethod = method;
					state.currentCheckType = annotation.value();
					state.constraint = annotation.constraint();
					method.setAccessible(true);
					method.invoke(instance, state.currentObject);

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
				instance.getState().set(null);
			}
		}
	}

	protected void handleInvocationTargetException(Throwable targetException, State state) {
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
		return signature.equals(mw.signature) && instance == mw.instance;
	}

	public AbstractCheckValidator getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}

	public String getConstraint() {
		return constraint;
	}

	public String[] getCategories() {
		return categories;
	}
}
