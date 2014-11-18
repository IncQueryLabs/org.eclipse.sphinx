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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Severity;
import org.eclipse.sphinx.emf.check.internal.Activator;
import org.eclipse.sphinx.emf.util.BasicWrappingEList;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

public abstract class AbstractCheckValidator implements ICheckValidator {

	private int INSIGNIFICANT_INDEX = -1;

	private volatile Set<MethodWrapper> checkMethods = null;

	private volatile Set<String> filter;

	private final ThreadLocal<State> state;

	private final CheckModelHelper checkModelHelper;

	protected static class State {
		public DiagnosticChain chain = null;
		public EObject currentObject = null;
		public Method currentMethod = null;
		public CheckMode checkMode = null;
		public CheckType currentCheckType = null;
		public boolean hasErrors = false;
		public Map<Object, Object> context;
		public String constraint = null;
	}

	protected static class StateAccess {

		private AbstractCheckValidator validator;

		private StateAccess(AbstractCheckValidator validator) {
			this.validator = validator;
		}

		public State getState() {
			State result = validator.state.get();
			if (result == null) {
				result = new State();
				validator.state.set(result);
			}
			return result;
		}
	}

	public AbstractCheckValidator() {
		state = new ThreadLocal<State>();
		checkModelHelper = new CheckModelHelper(this);
	}

	/*
	 * @see org.eclipse.sphinx.emf.check.ICheckValidator#setFilter(java.util.Set)
	 */
	@Override
	public void setFilter(Set<String> validationSets) {
		filter = validationSets;
	}

	private final SimpleCache<Class<?>, List<MethodWrapper>> methodsForType = new SimpleCache<Class<?>, List<MethodWrapper>>(
			new Function<Class<?>, List<MethodWrapper>>() {
				@Override
				public List<MethodWrapper> apply(Class<?> param) {
					List<MethodWrapper> result = new ArrayList<MethodWrapper>();
					for (MethodWrapper mw : checkMethods) {
						if (mw.isMatching(param)) {
							result.add(mw);
						}
					}
					return result;
				}
			});

	protected final boolean internalValidate(EClass class1, EObject object, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (checkMethods == null) {
			synchronized (this) {
				if (checkMethods == null) {
					Set<MethodWrapper> checkMethods = Sets.newLinkedHashSet();
					checkMethods.addAll(collectMethods(getClass()));
					this.checkMethods = checkMethods;
				}
			}
		}
		CheckMode checkMode = CheckMode.getCheckMode(context);

		State state = new State();
		state.chain = diagnostics;
		state.currentObject = object;
		state.checkMode = checkMode;
		state.context = context;

		for (MethodWrapper method : methodsForType.get(object.getClass())) {
			method.invoke(state);
		}

		return !state.hasErrors;
	}

	private List<MethodWrapper> collectMethods(Class<? extends AbstractCheckValidator> clazz) {
		List<MethodWrapper> checkMethods = new ArrayList<MethodWrapper>();
		Set<Class<?>> visitedClasses = new HashSet<Class<?>>(4);
		collectMethods(this, clazz, visitedClasses, checkMethods);
		return checkMethods;
	}

	private void collectMethods(AbstractCheckValidator instance, Class<? extends AbstractCheckValidator> clazz, Collection<Class<?>> visitedClasses,
			Collection<MethodWrapper> result) {
		if (visitedClasses.contains(clazz)) {
			return;
		}
		collectMethodsImpl(instance, clazz, visitedClasses, result);
		Class<? extends AbstractCheckValidator> k = clazz;
		while (k != null) {
			ComposedChecks checks = k.getAnnotation(ComposedChecks.class);
			if (checks != null) {
				for (Class<? extends AbstractCheckValidator> external : checks.validators()) {
					collectMethods(null, external, visitedClasses, result);
				}
			}
			k = getSuperClass(k);
		}
	}

	private void collectMethodsImpl(AbstractCheckValidator instance, Class<? extends AbstractCheckValidator> clazz,
			Collection<Class<?>> visitedClasses, Collection<MethodWrapper> result) {
		if (!visitedClasses.add(clazz)) {
			return;
		}
		AbstractCheckValidator instanceToUse;
		instanceToUse = instance;
		if (instanceToUse == null) {
			instanceToUse = newInstance(clazz);
		}
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			Check annotation = method.getAnnotation(Check.class);
			if (annotation != null && method.getParameterTypes().length == 1) {
				String constraint = annotation.constraint();
				String[] categories = annotation.categories();
				result.add(createMethodWrapper(instanceToUse, method, constraint, categories));
			}
		}
		Class<? extends AbstractCheckValidator> superClass = getSuperClass(clazz);
		if (superClass != null) {
			collectMethodsImpl(instanceToUse, superClass, visitedClasses, result);
		}
	}

	private MethodWrapper createMethodWrapper(AbstractCheckValidator instanceToUse, Method method, String constraint, String[] categories) {
		return new MethodWrapper(instanceToUse, method, constraint, categories);
	}

	private Class<? extends AbstractCheckValidator> getSuperClass(Class<? extends AbstractCheckValidator> clazz) {
		try {
			Class<? extends AbstractCheckValidator> superClass = clazz.getSuperclass().asSubclass(AbstractCheckValidator.class);
			if (AbstractCheckValidator.class.equals(superClass)) {
				return null;
			}
			return superClass;
		} catch (ClassCastException e) {
			return null;
		}
	}

	private AbstractCheckValidator newInstance(Class<? extends AbstractCheckValidator> clazz) {
		AbstractCheckValidator instanceToUse = null;
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
		return internalValidate(eObject.eClass(), eObject, diagnostics, context);
	}

	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	protected void issue(Object object, EStructuralFeature feature, Object... arguments) {
		if (object instanceof BasicWrappingEList.IWrapper<?>) {
			Object eObject = ((BasicWrappingEList.IWrapper<?>) object).getTarget();
			issue(eObject, feature, INSIGNIFICANT_INDEX, arguments);
		}
		throw new UnsupportedOperationException("Could not recognize type of " + object.toString()); //$NON-NLS-1$
	}

	protected void issue(EObject object, EStructuralFeature feature, Object... arguments) {
		issue(object, feature, INSIGNIFICANT_INDEX, arguments);
	}

	protected void issue(EObject object, EStructuralFeature feature, int index, Object... arguments) {
		State current = getState().get();
		String constraint = current.constraint;
		String severityMessage = MessageFormat.format(checkModelHelper.getMessage(constraint), arguments);
		Severity severity = checkModelHelper.getSeverityType(constraint);
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
		warning(message, object, feature, INSIGNIFICANT_INDEX);
	}

	protected void warning(String message, EObject object, EStructuralFeature feature, int index) {
		Object[] data = new Object[] { new DiagnosticLocation(object, feature), this.getClass().getName() };
		warning(message, object, feature, index, data);
	}

	protected void warning(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		getState().get().chain.add(createDiagnostic(Severity.WARNING, message, object, feature, index, issueData));
	}

	protected void error(String message, EObject object, EStructuralFeature feature) {
		error(message, object, feature, INSIGNIFICANT_INDEX);
	}

	protected void error(String message, EObject object, EStructuralFeature feature, int index) {
		Object[] data = new Object[] { new DiagnosticLocation(object, feature), this.getClass().getName() };
		error(message, object, feature, index, data);
	}

	protected void error(String message, EObject source, EStructuralFeature feature, int index, Object[] issueData) {
		getState().get().hasErrors = true;
		getState().get().chain.add(createDiagnostic(Severity.ERROR, message, source, feature, index, issueData));
	}

	protected void info(String message, EObject object, EStructuralFeature feature) {
		info(message, object, feature, INSIGNIFICANT_INDEX);
	}

	protected void info(String message, EObject object, EStructuralFeature feature, int index) {
		Object[] data = new Object[] { new DiagnosticLocation(object, feature), this.getClass().getName() };
		info(message, object, feature, index, data);
	}

	protected void info(String message, EObject object, EStructuralFeature feature, int index, Object[] issueData) {
		getState().get().chain.add(createDiagnostic(Severity.INFO, message, object, feature, index, issueData));
	}

	protected Diagnostic createDiagnostic(Severity severity, String message, EObject source, EStructuralFeature feature, int index, Object[] issueData) {
		int diagnosticSeverity = toDiagnosticSeverity(severity);
		Diagnostic result = new BasicDiagnostic(diagnosticSeverity, source.toString(), 0, message, issueData);
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

	public ThreadLocal<State> getState() {
		return state;
	}

	/*
	 * @see org.eclipse.sphinx.emf.check.ICheckValidator#getFilter()
	 */
	@Override
	public Set<String> getFilter() {
		return filter;
	}

	/*
	 * @see org.eclipse.sphinx.emf.check.ICheckValidator#getCheckModelHelper()
	 */
	@Override
	public CheckModelHelper getCheckModelHelper() {
		return checkModelHelper;
	}
}