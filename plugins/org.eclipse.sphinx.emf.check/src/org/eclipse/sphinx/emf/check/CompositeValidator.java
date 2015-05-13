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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.sphinx.emf.check.util.ExtendedEObjectValidator;

/**
 * A delegating validator. This validator is automatically created when multiple check
 * {@link org.eclipse.sphinx.emf.check.AbstractCheckValidator validators} are contributed for the same
 * {@link org.eclipse.emf.ecore.EPackage ePackage}. In this case, the composite validator is set as the root validator,
 * it delegates the validation to all its children and returns the logical <em>AND</em> of the delegated diagnostics
 * results.
 */
public class CompositeValidator implements EValidator {

	private List<EValidator> children;
	private ExtendedEObjectValidator extendedEObjectValidator;

	public CompositeValidator() {
		children = new ArrayList<EValidator>();
		extendedEObjectValidator = new ExtendedEObjectValidator();
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
		Object oldIntrinsicModelIntegrityConstraintsEnabled = null;
		if (needsToValidateIntrinsicConstraints(context)) {
			result &= extendedEObjectValidator.validate(eObject.eClass().getClassifierID(), eObject, diagnostics, context);
			oldIntrinsicModelIntegrityConstraintsEnabled = context.remove(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS);
		}

		// Delegate to children with modified context if intrinsic are enabled so that children will not invoke
		// intrinsic validation.
		for (EValidator validator : getChildren()) {
			result &= validator.validate(eObject, diagnostics, context);
		}
		// Restore OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS state
		if (oldIntrinsicModelIntegrityConstraintsEnabled != null) {
			context.put(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS, oldIntrinsicModelIntegrityConstraintsEnabled);
		}
		return result;
	}

	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		Object oldIntrinsicModelIntegrityConstraintsEnabled = null;
		if (needsToValidateIntrinsicConstraints(context)) {
			result &= extendedEObjectValidator.validate(eClass.getClassifierID(), eObject, diagnostics, context);
			oldIntrinsicModelIntegrityConstraintsEnabled = context.remove(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS);
		}
		// Delegate to children with modified context if intrinsic are enabled so that children will not invoke
		// intrinsic validation.
		for (EValidator validator : getChildren()) {
			result &= validator.validate(eClass, eObject, diagnostics, context);
		}
		// Restore OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS state
		if (oldIntrinsicModelIntegrityConstraintsEnabled != null) {
			context.put(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS, oldIntrinsicModelIntegrityConstraintsEnabled);
		}
		return result;
	}

	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		Object oldIntrinsicModelIntegrityConstraintsEnabled = null;
		if (needsToValidateIntrinsicConstraints(context)) {
			result &= extendedEObjectValidator.validate(eDataType, value, diagnostics, context);
			oldIntrinsicModelIntegrityConstraintsEnabled = context.remove(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS);
		}
		// Delegate to children with modified context if intrinsic are enabled so that children will not invoke
		// intrinsic validation.
		for (EValidator validator : getChildren()) {
			result &= validator.validate(eDataType, value, diagnostics, context);
		}
		// Restore OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS state
		if (oldIntrinsicModelIntegrityConstraintsEnabled != null) {
			context.put(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS, oldIntrinsicModelIntegrityConstraintsEnabled);
		}
		return result;
	}

	protected boolean needsToValidateIntrinsicConstraints(Map<Object, Object> context) {
		return isIntrinsicModelIntegrityConstraintsEnabled(context) && !containsEObjectValidator();
	}

	protected boolean isIntrinsicModelIntegrityConstraintsEnabled(Map<Object, Object> context) {
		Assert.isNotNull(context);

		Object value = context.get(ICheckValidator.OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS);
		return value != null && Boolean.parseBoolean(value.toString());
	}

	protected boolean containsEObjectValidator() {
		for (EValidator validator : getChildren()) {
			if (validator instanceof EObjectValidator) {
				return true;
			}
		}
		return false;
	}
}