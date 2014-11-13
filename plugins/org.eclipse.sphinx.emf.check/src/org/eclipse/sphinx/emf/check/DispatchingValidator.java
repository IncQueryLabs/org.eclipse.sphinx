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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;

public class DispatchingValidator implements EValidator {

	private List<EValidator> delegates;

	public DispatchingValidator() {
		delegates = new ArrayList<EValidator>();
	}

	public DispatchingValidator(EValidator delegate) {
		this();
		addDelegate(delegate);
	}

	public void addDelegate(EValidator delegate) {
		if (this == delegate) {
			return;
		}
		if (!delegates.contains(delegate)) {
			delegates.add(delegate);
		}
	}

	public void removeDelegate(EValidator delegate) {
		if (this == delegate) {
			return;
		}
		if (delegates.contains(delegate)) {
			delegates.remove(delegate);
		}
	}

	public List<EValidator> getDelegates() {
		return delegates;
	}

	@Override
	public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EValidator validator : getDelegates()) {
			result = result && validator.validate(eObject, diagnostics, context);
		}
		return result;
	}

	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EValidator validator : getDelegates()) {
			result = result && validator.validate(eClass, eObject, diagnostics, context);
		}
		return result;
	}

	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EValidator validator : getDelegates()) {
			result = result && validator.validate(eDataType, value, diagnostics, context);
		}
		return result;
	}
}