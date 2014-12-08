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
package org.eclipse.sphinx.emf.util;

import java.lang.reflect.Constructor;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.platform.util.StatusUtil;

/**
 * An extensible wrapping delegating list implementation with a wrapping and unwrapping object support.
 */

public class BasicWrappingEList<W extends IWrapper<T>, T> extends AbstractWrappingEList<W, T> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of the BasicWrappingEList delegating list.
	 *
	 * @param wrapperType
	 *            the wrapper type
	 * @param targetType
	 *            the target object type
	 */
	public BasicWrappingEList(List<T> delegateList, Class<W> wrapperType, Class<T> targetType) {
		super(delegateList, wrapperType, targetType);
	}

	/**
	 * Wraps the given object.
	 *
	 * @param object
	 *            object to be wrapped.
	 * @return the wrapped object.
	 * @throws CoreException
	 */
	@Override
	protected W wrap(T object) throws CoreException {
		try {
			Constructor<W> constructor = wrapperType.getDeclaredConstructor(targetType);
			return constructor.newInstance(object);
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}

	/**
	 * Unwraps the given object.
	 *
	 * @param object
	 *            object to be unwrapped.
	 * @return the unwrapped object.
	 */
	@Override
	protected T unwrap(W object) {
		return object.getTarget();
	}
}
