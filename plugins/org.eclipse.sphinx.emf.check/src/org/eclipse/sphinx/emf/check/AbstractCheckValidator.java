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
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import org.eclipse.sphinx.emf.check.catalog.Severity;
import org.eclipse.sphinx.emf.check.internal.Activator;
import org.eclipse.sphinx.emf.check.util.ExtendedEObjectValidator;
import org.eclipse.sphinx.emf.util.IWrapper;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * An abstract implementation of a check-based validator. Depending on the type of the object under validation, the set
 * of methods annotated with @Check, whose argument type equals (or is subclass of) the type of the object, are
 * automatically triggered. To be sub-classed by custom check validators.
 */
public abstract class AbstractCheckValidator implements ICheckValidator {

	private int NO_INDEX = -1;

	private volatile Set<MethodWrapper> checkMethods = null;

	private final ThreadLocal<CheckValidatorState> state;

	private final CheckCatalogHelper checkCatalogHelper;

	private ExtendedEObjectValidator extendedEObjectValidator;

	protected static class CheckValidatorState {
		public DiagnosticChain chain = null;
		public Object currentObject = null;
		public Method currentMethod = null;
		public CheckMode checkMode = null;
		public CheckType currentCheckType = null;
		public boolean hasErrors = false;
		public Map<Object, Object> context;
		public String constraint = null;
	}

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
		state = new ThreadLocal<CheckValidatorState>();
		checkCatalogHelper = new CheckCatalogHelper(this);
		extendedEObjectValidator = new ExtendedEObjectValidator();
	}

	private final SimpleCache<Class<?>, List<MethodWrapper>> methodsForType = new SimpleCache<Class<?>, List<MethodWrapper>>(
			new Function<Class<?>, List<MethodWrapper>>() {
				@Override
				public List<MethodWrapper> apply(Class<?> param) {
					List<MethodWrapper> result = new ArrayList<MethodWrapper>();
					for (MethodWrapper mw : checkMethods) {
						if (mw.matches(param)) {
							result.add(mw);
						}
					}
					return result;
				}
			});

	protected Class<?> getMethodWrapperType(EObject eObject) {
		Assert.isNotNull(eObject);
		return eObject.getClass();
	}

	protected void setCurrentObject(CheckValidatorState state, Object object) {
		Assert.isNotNull(state);
		state.currentObject = object;
	}

	private List<MethodWrapper> collectMethods(String[] selectedCategories) {
		List<MethodWrapper> checkMethods = new ArrayList<MethodWrapper>();
		Set<Class<?>> visitedClasses = new HashSet<Class<?>>(4);
		collectMethods(this, getClass(), selectedCategories, visitedClasses, checkMethods);
		return checkMethods;
	}

	private void collectMethods(ICheckValidator validator, Class<? extends ICheckValidator> clazz, String[] selectedCategories,
			Collection<Class<?>> visitedClasses, Collection<MethodWrapper> result) {
		if (visitedClasses.contains(clazz)) {
			return;
		}
		collectMethodsImpl(validator, clazz, selectedCategories, visitedClasses, result);
		Class<?> k = clazz;
		while (k.isAssignableFrom(ICheckValidator.class)) {
			ComposedChecks checks = k.getAnnotation(ComposedChecks.class);
			if (checks != null) {
				for (Class<? extends ICheckValidator> external : checks.validators()) {
					collectMethods(null, external, selectedCategories, visitedClasses, result);
				}
			}
			k = k.getSuperclass();
		}
	}

	@SuppressWarnings("unchecked")
	private void collectMethodsImpl(ICheckValidator instance, Class<? extends ICheckValidator> clazz, String[] selectedCategories,
			Collection<Class<?>> visitedClasses, Collection<MethodWrapper> result) {
		if (!visitedClasses.add(clazz)) {
			return;
		}
		ICheckValidator instanceToUse;
		instanceToUse = instance;
		if (instanceToUse == null) {
			instanceToUse = newInstance(clazz);
		}
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			Check annotation = method.getAnnotation(Check.class);
			if (annotation != null && method.getParameterTypes().length == 1) {
				String[] categories = annotation.categories();
				// If no categories for the check annotation, Other category should be selected to add this method to
				// the result.(https://bugs.eclipse.org/bugs/show_bug.cgi?id=458982)
				if (categories.length == 1 && categories[0].isEmpty()) {
					if (isOtherCategorySelected(selectedCategories)) {
						result.add(createMethodWrapper(instanceToUse, method, selectedCategories));
					}
				} else {
					result.add(createMethodWrapper(instanceToUse, method, selectedCategories));
				}
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && superClass.isAssignableFrom(ICheckValidator.class)) {
			collectMethodsImpl(instanceToUse, (Class<ICheckValidator>) superClass, selectedCategories, visitedClasses, result);
		}
	}

	private boolean isIntrinsicCategorySelected(String[] selectedCategories) {
		for (String categoryId : selectedCategories) {
			if (categoryId.equals(IValidationConstants.CATEGORY_ID_INTRINSIC)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOtherCategorySelected(String[] selectedCategories) {
		for (String categoryId : selectedCategories) {
			if (categoryId.equals(IValidationConstants.CATEGORY_ID_OTHER)) {
				return true;
			}
		}
		return false;
	}

	private MethodWrapper createMethodWrapper(ICheckValidator instanceToUse, Method method, String[] selectedCategories) {
		return new MethodWrapper(instanceToUse, method, selectedCategories);
	}

	private ICheckValidator newInstance(Class<? extends ICheckValidator> clazz) {
		ICheckValidator instanceToUse = null;
		try {
			instanceToUse = clazz.newInstance();
		} catch (InstantiationException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		} catch (IllegalAccessException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return instanceToUse;
	}

	@Override
	public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate(eObject.eClass(), eObject, diagnostics, context);
	}

	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		String[] selectedCategories = (String[]) context.get(ICheckValidator.OPTION_CATEGORIES);
		if (checkMethods == null) {
			synchronized (this) {
				if (checkMethods == null) {
					Set<MethodWrapper> checkMethods = Sets.newLinkedHashSet();
					checkMethods.addAll(collectMethods(selectedCategories));
					this.checkMethods = checkMethods;
				}
			}
		}
		CheckMode checkMode = CheckMode.getCheckMode(context);

		CheckValidatorState state = new CheckValidatorState();
		state.chain = diagnostics;
		setCurrentObject(state, eObject);
		state.checkMode = checkMode;
		state.context = context;

		if (isIntrinsicCategorySelected(selectedCategories)) {
			extendedEObjectValidator.validate(eObject.eClass().getClassifierID(), eObject, diagnostics, context);
		}

		for (MethodWrapper method : methodsForType.get(getMethodWrapperType(eObject))) {
			method.invoke(state);
		}

		return !state.hasErrors;
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
		if (checkCatalogHelper == null) {
			return;
		}
		String severityMessage = MessageFormat.format(checkCatalogHelper.getMessage(constraint), arguments);
		Severity severity = checkCatalogHelper.getSeverity(constraint);
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

	protected void warning(String message, EObject object, EStructuralFeature feature) {
		warning(message, object, feature, NO_INDEX);
	}

	protected void warning(String message, EObject object, EStructuralFeature feature, int index) {
		// FIXME Add index to DiagnosticLocation
		Object[] data = new Object[] { new DiagnosticLocation(object, feature),
				new SourceLocation(this.getClass(), getState().get().currentMethod, getState().get().constraint) };
		warning(message, object, feature, index, data);
	}

	protected void warning(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		// FIXME Test if issueData contains DiagnosticLocation and create one from object/feature/index and add it to
		// issueData if not so
		getState().get().chain.add(createDiagnostic(Severity.WARNING, message, index, issueData));
	}

	protected void error(String message, EObject object, EStructuralFeature feature) {
		error(message, object, feature, NO_INDEX);
	}

	protected void error(String message, EObject object, EStructuralFeature feature, int index) {
		// FIXME Add index to DiagnosticLocation
		Object[] data = new Object[] { new DiagnosticLocation(object, feature),
				new SourceLocation(this.getClass(), getState().get().currentMethod, getState().get().constraint) };
		error(message, object, feature, index, data);
	}

	protected void error(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		// FIXME Test if issueData contains DiagnosticLocation and create one from object/feature/index and add it to
		// issueData if not so
		getState().get().hasErrors = true;
		getState().get().chain.add(createDiagnostic(Severity.ERROR, message, index, issueData));
	}

	protected void info(String message, EObject object, EStructuralFeature feature) {
		info(message, object, feature, NO_INDEX);
	}

	protected void info(String message, EObject object, EStructuralFeature feature, int index) {
		// FIXME Add index to DiagnosticLocation
		Object[] data = new Object[] { new DiagnosticLocation(object, feature),
				new SourceLocation(this.getClass(), getState().get().currentMethod, getState().get().constraint) };
		info(message, object, feature, index, data);
	}

	protected void info(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		// FIXME Test if issueData contains DiagnosticLocation and create one from object/feature/index and add it to
		// issueData if not so
		getState().get().chain.add(createDiagnostic(Severity.INFO, message, index, issueData));
	}

	// FIXME Remove index from parameter list
	protected Diagnostic createDiagnostic(Severity severity, String message, int index, Object[] issueData) {
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

	/*
	 * @see org.eclipse.sphinx.emf.check.ICheckValidator#getCheckModelHelper()
	 */
	@Override
	public CheckCatalogHelper getCheckCatalogHelper() {
		return checkCatalogHelper;
	}
}
