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
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;

public class CheckValidatorState {
	public DiagnosticChain chain = null;
	public Object currentObject = null;
	public Method currentMethod = null;
	public CheckValidationMode checkValidationMode = null;
	public CheckType currentCheckType = null;
	public boolean hasErrors = false;
	public Map<Object, Object> context;
	public String constraint = null;
}