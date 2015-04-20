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
 *     itemis - [457521] Check MethodWrapper should be null safe wrt instance.getFilter()
 *     itemis - [458976] Validators are not singleton when they implement checks for different EPackages
 *     itemis - [463895] org.eclipse.sphinx.emf.check.AbstractCheckValidator.validate(EClass, EObject, DiagnosticChain, Map<Object, Object>) throws NPE
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.catalog.Severity;
import org.eclipse.sphinx.emf.check.internal.Activator;
import org.eclipse.sphinx.emf.check.internal.CheckMethodWrapper;
import org.eclipse.sphinx.emf.check.util.CheckUtil;
import org.eclipse.sphinx.emf.check.util.DiagnosticLocation;
import org.eclipse.sphinx.emf.check.util.ExtendedEObjectValidator;
import org.eclipse.sphinx.emf.check.util.SourceLocation;
import org.eclipse.sphinx.emf.util.IWrapper;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * An abstract implementation of a check-based validator. Depending on the type of the object under validation, the set
 * of methods annotated with @Check, whose argument type equals (or is subclass of) the type of the object, are
 * automatically triggered. To be sub-classed by custom check validators.
 */
public abstract class AbstractCheckValidator implements ICheckValidator {

	private int NO_INDEX = -1;

	private boolean initialized = false;

	private final ThreadLocal<CheckValidatorState> state;

	private ExtendedEObjectValidator extendedEObjectValidator;

	private Map<Class<?>, List<CheckMethodWrapper>> collectedModelObjectTypeToCheckMethodsMap;

	private Map<Class<?>, List<CheckMethodWrapper>> actualModelObjectTypeToCheckMethodsMap;

	private CheckValidatorRegistry checkValidatorRegistry;

	protected static class CheckValidatorStateAccess {

		private ICheckValidator validator;

		private CheckValidatorStateAccess(ICheckValidator validator) {
			this.validator = validator;
		}

		public CheckValidatorState getState() {
			CheckValidatorState result = validator.getState().get();
			if (result == null) {
				result = new CheckValidatorState();
				validator.getState().set(result);
			}
			return result;
		}
	}

	public AbstractCheckValidator() {
		this(CheckValidatorRegistry.INSTANCE);
	}

	public AbstractCheckValidator(CheckValidatorRegistry checkValidatorRegistry) {
		state = new ThreadLocal<CheckValidatorState>();
		extendedEObjectValidator = new ExtendedEObjectValidator();
		collectedModelObjectTypeToCheckMethodsMap = new HashMap<Class<?>, List<CheckMethodWrapper>>();
		actualModelObjectTypeToCheckMethodsMap = new HashMap<Class<?>, List<CheckMethodWrapper>>();
		this.checkValidatorRegistry = checkValidatorRegistry;
	}

	protected Class<?> getModelObjectType(EObject eObject) {
		// Ensure backward compatibility
		Class<?> parameterType = getMethodWrapperType(eObject);
		if (parameterType != null) {
			return parameterType;
		}

		Assert.isNotNull(eObject);
		// Return eObject.eClass().getInstanceClass() which is the interface and it's used as the key in the
		// modelObjectTypeToCheckMethodsMap
		return eObject.eClass().getInstanceClass();
	}

	/**
	 * @deprecated Use {@link #getModelObjectType(EObject)} instead.
	 */
	@Deprecated
	protected Class<?> getMethodWrapperType(EObject eObject) {
		return null;
	}

	protected void setCurrentObject(CheckValidatorState state, Object object) {
		Assert.isNotNull(state);
		state.currentObject = object;
	}

	protected synchronized void initCheckMethods() {
		if (!initialized) {
			initialized = true;
			Collection<Method> declaredCheckMethods = CheckUtil.getDeclaredCheckMethods(getClass());
			for (Method method : declaredCheckMethods) {
				addCheckMethod(this, method);
			}
		}
	}

	protected void addCheckMethod(ICheckValidator validator, Method method) {
		Class<?> modelObjectType = method.getParameterTypes()[0];
		List<CheckMethodWrapper> value = collectedModelObjectTypeToCheckMethodsMap.get(modelObjectType);
		if (value == null) {
			value = new ArrayList<CheckMethodWrapper>();
			collectedModelObjectTypeToCheckMethodsMap.put(modelObjectType, value);
		}
		value.add(createCheckMethodWrapper(validator, method));
	}

	protected CheckMethodWrapper createCheckMethodWrapper(ICheckValidator validator, Method method) {
		return new CheckMethodWrapper(validator, method, checkValidatorRegistry);
	}

	protected boolean isIntrinsicCategorySelected(Set<String> selectedCategories) {
		Assert.isNotNull(selectedCategories);

		for (String categoryId : selectedCategories) {
			if (categoryId.equals(ICheckValidationConstants.CATEGORY_ID_INTRINSIC)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate(eObject.eClass(), eObject, diagnostics, context);
	}

	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		Set<String> selectedCategories = getCategoriesFromContext(context);

		initCheckMethods();

		CheckValidationMode mode = CheckValidationMode.getFromContext(context);

		CheckValidatorState state = new CheckValidatorState();
		state.chain = diagnostics;
		setCurrentObject(state, eObject);
		state.checkValidationMode = mode;
		state.context = context;

		if (isIntrinsicCategorySelected(selectedCategories)) {
			extendedEObjectValidator.validate(eObject.eClass().getClassifierID(), eObject, diagnostics, context);
		}

		for (CheckMethodWrapper method : getCheckMethodsForModelObjectType(getModelObjectType(eObject))) {
			try {
				method.invoke(state, selectedCategories);
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}

		return !state.hasErrors;
	}

	protected List<CheckMethodWrapper> getCheckMethodsForModelObjectType(Class<?> modelObjectType) {
		List<CheckMethodWrapper> actualCheckMethodsForModelObjectType = actualModelObjectTypeToCheckMethodsMap.get(modelObjectType);
		if (actualCheckMethodsForModelObjectType == null) {
			actualCheckMethodsForModelObjectType = new ArrayList<CheckMethodWrapper>();
			// Add Check method for current model object type and its super types
			for (Class<?> collectedModelObjectType : collectedModelObjectTypeToCheckMethodsMap.keySet()) {
				if (collectedModelObjectType.isAssignableFrom(modelObjectType)) {
					actualCheckMethodsForModelObjectType.addAll(collectedModelObjectTypeToCheckMethodsMap.get(collectedModelObjectType));
				}
			}
			actualModelObjectTypeToCheckMethodsMap.put(modelObjectType, actualCheckMethodsForModelObjectType);
		}
		return actualCheckMethodsForModelObjectType;
	}

	protected Set<String> getCategoriesFromContext(Map<Object, Object> context) {
		if (context != null) {
			Object categories = context.get(ICheckValidator.OPTION_CATEGORIES);
			if (categories instanceof Set<?>) {
				@SuppressWarnings("unchecked")
				Set<String> castedCategories = (Set<String>) categories;
				return castedCategories;
			}
		}
		return Collections.emptySet();
	}

	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	protected void issue(Object object, EStructuralFeature feature, Object... arguments) {
		if (object instanceof IWrapper<?>) {
			Object eObject = ((IWrapper<?>) object).getTarget();
			issue(eObject, feature, NO_INDEX, arguments);
		} else if (object instanceof EObject) {
			issue((EObject) object, feature, NO_INDEX, arguments);
		} else {
			throw new UnsupportedOperationException("Could not recognize type of " + object.toString()); //$NON-NLS-1$
		}
	}

	/**
	 * Use this method only if a check catalog is contributed.
	 *
	 * @param object
	 * @param feature
	 * @param arguments
	 */
	protected void issue(EObject object, EStructuralFeature feature, Object... arguments) {
		issue(object, feature, NO_INDEX, arguments);
	}

	/**
	 * Use this method only if a check catalog is contributed.
	 *
	 * @param object
	 * @param feature
	 * @param index
	 * @param arguments
	 */
	protected void issue(EObject object, EStructuralFeature feature, int index, Object... arguments) {
		String constraint = getState().get().constraint;
		Catalog checkCatalog = getCheckCatalog();
		if (checkCatalog == null) {
			return;
		}
		String severityMessage = MessageFormat.format(checkCatalog.getMessage(constraint), arguments);
		Severity severity = checkCatalog.getSeverity(constraint);
		switch (severity) {
		case ERROR:
			error(severityMessage, object, feature, index);
			break;
		case WARNING:
			warning(severityMessage, object, feature, index);
			break;
		case INFO:
			info(severityMessage, object, feature, index);
			break;
		default:
			throw new IllegalArgumentException("Unknow severity " + severity); //$NON-NLS-1$
		}
	}

	protected void error(String message, EObject object, EStructuralFeature feature) {
		error(message, object, feature, NO_INDEX);
	}

	protected void error(String message, EObject object, EStructuralFeature feature, int index) {
		error(message, object, feature, index, createIssueData(object, feature, index));
	}

	protected void error(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		getState().get().hasErrors = true;
		if (!containsDiagnosticLocation(issueData)) {
			getState().get().chain.add(createDiagnostic(Severity.INFO, message, addDiagnosticLocation(object, feature, index, issueData)));
		} else {
			getState().get().chain.add(createDiagnostic(Severity.ERROR, message, issueData));
		}
	}

	protected void warning(String message, EObject object, EStructuralFeature feature) {
		warning(message, object, feature, NO_INDEX);
	}

	protected void warning(String message, EObject object, EStructuralFeature feature, int index) {
		warning(message, object, feature, index, createIssueData(object, feature, index));
	}

	protected void warning(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		if (!containsDiagnosticLocation(issueData)) {
			getState().get().chain.add(createDiagnostic(Severity.INFO, message, addDiagnosticLocation(object, feature, index, issueData)));
		} else {
			getState().get().chain.add(createDiagnostic(Severity.WARNING, message, issueData));
		}
	}

	protected void info(String message, EObject object, EStructuralFeature feature) {
		info(message, object, feature, NO_INDEX);
	}

	protected void info(String message, EObject object, EStructuralFeature feature, int index) {
		info(message, object, feature, index, createIssueData(object, feature, index));
	}

	protected void info(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		if (!containsDiagnosticLocation(issueData)) {
			getState().get().chain.add(createDiagnostic(Severity.INFO, message, addDiagnosticLocation(object, feature, index, issueData)));
		} else {
			getState().get().chain.add(createDiagnostic(Severity.INFO, message, issueData));
		}
	}

	protected Object[] addDiagnosticLocation(EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		Object[] result = Arrays.copyOf(issueData, issueData.length + 1);
		result[issueData.length] = new DiagnosticLocation(object, feature, index);
		return result;
	}

	protected boolean containsDiagnosticLocation(Object[] issueData) {
		for (Object obj : issueData) {
			if (obj instanceof DiagnosticLocation) {
				return true;
			}
		}
		return false;
	}

	protected Object[] createIssueData(EObject object, EStructuralFeature feature, int index) {
		Object[] data = new Object[] { new DiagnosticLocation(object, feature, index),
				new SourceLocation(this.getClass(), getState().get().currentMethod, getState().get().constraint) };
		return data;
	}

	protected Diagnostic createDiagnostic(Severity severity, String message, Object[] issueData) {
		int diagnosticSeverity = toDiagnosticSeverity(severity);
		Diagnostic result = new BasicDiagnostic(diagnosticSeverity, this.getClass().getName(), 0, message, issueData);
		return result;
	}

	protected int toDiagnosticSeverity(Severity severity) {
		int diagnosticSeverity = -1;
		switch (severity) {
		case ERROR:
			diagnosticSeverity = Diagnostic.ERROR;
			break;
		case WARNING:
			diagnosticSeverity = Diagnostic.WARNING;
			break;
		case INFO:
			diagnosticSeverity = Diagnostic.INFO;
			break;
		default:
			throw new IllegalArgumentException("Unknow severity " + severity); //$NON-NLS-1$
		}
		return diagnosticSeverity;
	}

	@Override
	public ThreadLocal<CheckValidatorState> getState() {
		return state;
	}

	public Catalog getCheckCatalog() {
		return checkValidatorRegistry.getCheckCatalog(this);
	}
}
