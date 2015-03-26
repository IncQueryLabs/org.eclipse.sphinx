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

/**
 * A delegating validator. This validator is automatically created when multiple check
 * {@link org.eclipse.sphinx.emf.check.AbstractCheckValidator validators} are contributed for the same
 * {@link org.eclipse.emf.ecore.EPackage ePackage}. In this case, the composite validator is set as the root validator,
 * it delegates the validation to all its children and returns the logical <em>AND</em> of the delegated diagnostics
 * results.
 */
public class CompositeValidator implements EValidator {

	private List<EValidator> children;

	public CompositeValidator() {
		children = new ArrayList<EValidator>();
	}

	public CompositeValidator(EValidator delegate) {
		this();
		addChild(delegate);
	}

	public void addChild(EValidator delegate) {
		if (this == delegate) {
			return;
		}
		if (!children.contains(delegate)) {
			children.add(delegate);
		}
	}

	public void removeChild(EValidator delegate) {
		if (this == delegate) {
			return;
		}
		if (children.contains(delegate)) {
			children.remove(delegate);
		}
	}

	public List<EValidator> getChildren() {
		return children;
	}

	@Override
	public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EValidator validator : getChildren()) {
			result = result && validator.validate(eObject, diagnostics, context);
		}
		return result;
	}

	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EValidator validator : getChildren()) {
			boolean validate = validator.validate(eClass, eObject, diagnostics, context);
			result = result && validate;
		}
		return result;
	}

	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EValidator validator : getChildren()) {
			boolean validate = validator.validate(eDataType, value, diagnostics, context);
			result = result && validate;
		}
		return result;
	}
}