/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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

import java.lang.reflect.Method;

public class SourceLocation {

	private Class<?> checkValidator;

	private Method checkMethod;

	public SourceLocation(Class<?> checkValidator, Method checkMethod) {
		this.checkValidator = checkValidator;
		this.checkMethod = checkMethod;
	}

	public Class<?> getCheckValidator() {
		return checkValidator;
	}

	public void setCheckValidator(Class<?> checkValidator) {
		this.checkValidator = checkValidator;
	}

	public Method getCheckMethod() {
		return checkMethod;
	}

	public void setCheckMethod(Method checkMethod) {
		this.checkMethod = checkMethod;
	}
}
