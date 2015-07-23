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
 *     itemis - [473260] Progress indication of check framework
 *     itemis - [473261] Check Validation: Cancel button unresponsive
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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.sphinx.emf.check.util.CheckValidationContextHelper;
import org.eclipse.sphinx.emf.check.util.ExtendedEObjectValidator;

/**
 * A delegating validator. This validator is automatically created when multiple check {@link EValidator validators} are
 * contributed for the same {@link EPackage ePackage}. In this case, the composite validator is set as the root
 * validator, it delegates the validation to all its children and returns the logical <em>AND</em> of the delegated
 * diagnostics results.
 */
public class CompositeValidator implements EValidator {

	protected class CompositeValidatorHandler {

		private ExtendedEObjectValidator eObjectValidator = new ExtendedEObjectValidator();
		private Map<Object, Object> context;
		private CheckValidationContextHelper helper;

		private Boolean oldEnableIntrinsicModelIntegrityConstraintsOption = null;

		public CompositeValidatorHandler(Map<Object, Object> context) {
			this.context = context;
			helper = new CheckValidationContextHelper(context);
		}

		public boolean preValidate(int classifierID, Object object, DiagnosticChain diagnostics) {
			// Validate intrinsic model integrity constraints if required
			boolean result = true;
			if (helper.areIntrinsicModelIntegrityConstraintsEnabled() && !containsEObjectValidator()) {
				result = eObjectValidator.validate(classifierID, object, diagnostics, context);

				// Remove enablement option for intrinsic model integrity constraints from context to prevent child
				// validators from validating them on same data type over and over again
				oldEnableIntrinsicModelIntegrityConstraintsOption = helper.removeEnableIntrinsicModelIntegrityConstraintsOption();
			}
			return result;
		}

		public void postValidate() {
			// Restore previous enablement option for intrinsic model integrity constraints in context, if any
			helper.addEnableIntrinsicModelIntegrityConstraintsOption(oldEnableIntrinsicModelIntegrityConstraintsOption);
		}
	}

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

	protected boolean containsEObjectValidator() {
		for (EValidator validator : getChildren()) {
			if (validator instanceof EObjectValidator) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		CompositeValidatorHandler handler = new CompositeValidatorHandler(context);
		boolean result = handler.preValidate(eObject.eClass().getClassifierID(), eObject, diagnostics);

		// Let child validators validate given model object
		for (EValidator validator : getChildren()) {
			result &= validator.validate(eObject, diagnostics, context);
		}

		handler.postValidate();
		return result;
	}

	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		CompositeValidatorHandler handler = new CompositeValidatorHandler(context);
		boolean result = handler.preValidate(eClass.getClassifierID(), eObject, diagnostics);

		// Let child validators validate given model object
		for (EValidator validator : getChildren()) {
			result &= validator.validate(eClass, eObject, diagnostics, context);
		}

		handler.postValidate();
		return result;
	}

	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		CompositeValidatorHandler handler = new CompositeValidatorHandler(context);
		boolean result = handler.preValidate(eDataType.getClassifierID(), value, diagnostics);

		// Let child validators validate given data type
		for (EValidator validator : getChildren()) {
			result &= validator.validate(eDataType, value, diagnostics, context);
		}

		handler.postValidate();
		return result;
	}
}