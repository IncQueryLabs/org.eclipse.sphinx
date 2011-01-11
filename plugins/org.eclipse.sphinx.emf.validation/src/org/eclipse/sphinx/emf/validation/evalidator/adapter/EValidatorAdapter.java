/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.validation.evalidator.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.emf.validation.service.IBatchValidator;
import org.eclipse.emf.validation.service.IConstraintFilter;
import org.eclipse.emf.validation.service.ITraversalStrategy;
import org.eclipse.emf.validation.service.ModelValidationService;
import org.eclipse.sphinx.emf.validation.Activator;
import org.eclipse.sphinx.emf.validation.diagnostic.ExtendedDiagnostic;
import org.eclipse.sphinx.emf.validation.preferences.IValidationPreferences;

/**
 * An adapter that plugs the EMF Model Validation Service API into the {@link org.eclipse.emf.ecore.EValidator} API.
 */
public class EValidatorAdapter extends EObjectValidator {

	/**
	 * Model Validation Service interface for batch validation of EMF elements.
	 */
	private final IBatchValidator batchValidator;

	/**
	 * Initializes me.
	 */
	public EValidatorAdapter() {
		super();

		batchValidator = (IBatchValidator) ModelValidationService.getInstance().newValidator(EvaluationMode.BATCH);
		batchValidator.setIncludeLiveConstraints(true);
		batchValidator.setReportSuccesses(false);
		batchValidator.setTraversalStrategy(new ITraversalStrategy.Flat());

	}

	@Override
	public boolean validate(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate(eObject.eClass(), eObject, diagnostics, context);
	}

	/**
	 * Implements validation by delegation to the EMF validation framework using 'context' filter.
	 */
	@SuppressWarnings("unchecked")
	public boolean validate(EClass eClass, final EObject eObject, final DiagnosticChain diagnostics, final Map<Object, Object> context,
			final Set<IConstraintFilter> filters) {

		// first, do whatever the basic EcoreValidator does
		// former call to super.validate(eClass, eObject, diagnostics, context);

		// Let's check if EMF default rules should be checked
		boolean isEMFRulesActivated = Activator.getDefault().getPluginPreferences().getBoolean(IValidationPreferences.PREF_ENABLE_EMF_DEFAULT_RULES);

		if (isEMFRulesActivated) {
			if (eClass.eContainer() == getEPackage()) {
				validate(eClass.getClassifierID(), eObject, diagnostics, context);
			} else {
				List<EClass> eSuperTypes = eClass.getESuperTypes();
				if (eSuperTypes.isEmpty()) {
					validate_EveryDefaultConstraint(eObject, diagnostics, context);
				} else {
					// validate(eSuperTypes.get(0), eObject, diagnostics, context);
				}
			}
		}

		// Ok, Now let's validate our rules.

		IStatus status = Status.OK_STATUS;

		// no point in validating if we can't report results
		if (diagnostics != null) {
			// if EMF Mode Validation Service already covered the sub-tree,
			// which it does for efficient computation and error reporting,
			// then don't repeat (the Diagnostician does the recursion
			// externally). If there is no context map, then we can't
			// help it
			if (!hasProcessed(eObject, context)) {

				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(eObject);
				if (editingDomain != null) {
					final EObject tgt = eObject;
					RunnableWithResult run = new RunnableWithResult.Impl() {
						public void run() {
							IStatus status = Status.OK_STATUS;

							// Add filters, if it exists
							addFilters(filters);
							try {
								status = batchValidator.validate(tgt, new NullProgressMonitor());
							} finally {
								// Remove the icf filter, if it exists
								removeFilters(filters);
							}
							processed(eObject, context, status);
							appendDiagnostics(status, diagnostics);
							setResult(status);
						}
					};

					try {
						status = (IStatus) editingDomain.runExclusive(run);
					} catch (InterruptedException ex) {
					}
				} else {
					status = batchValidator.validate(eObject, new NullProgressMonitor());
				}
			}
		}

		return status.isOK();
	}

	/**
	 * Implements validation by delegation to the EMF validation framework.
	 */
	@Override
	public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate(eClass, eObject, diagnostics, context, null);
	}

	/**
	 * Direct validation of {@link EDataType}s is not supported by the EMF validation framework; they are validated
	 * indirectly via the {@link EObject}s that hold their values.
	 */
	@Override
	public boolean validate(EDataType eDataType, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return super.validate(eDataType, value, diagnostics, context);
	}

	/**
	 * If we have a context map, record this object's <code>status</code> in it so that we will know later that we have
	 * processed it and its sub-tree.
	 * 
	 * @param eObject
	 *            an element that we have validated
	 * @param context
	 *            the context (may be <code>null</code>)
	 * @param status
	 *            the element's validation status
	 */
	private void processed(EObject eObject, Map<Object, Object> context, IStatus status) {
		if (context != null) {
			context.put(eObject, status);
		}
	}

	/**
	 * Determines whether we have processed this <code>eObject</code> before, by automatic recursion of the EMF Model
	 * Validation Service. This is only possible if we do, indeed, have a context.
	 * 
	 * @param eObject
	 *            an element to be validated (we hope not)
	 * @param context
	 *            the context (may be <code>null</code>)
	 * @return <code>true</code> if the context is not <code>null</code> and the <code>eObject</code> or one of its
	 *         containers has already been validated; <code>false</code>, otherwise
	 */
	private boolean hasProcessed(EObject eObject, Map<Object, Object> context) {
		boolean result = false;

		if (context != null) {
			// this is O(NlogN) but there's no helping it
			while (eObject != null) {
				if (context.containsKey(eObject)) {
					result = true;
					eObject = null;
				} else {
					eObject = eObject.eContainer();
				}
			}
		}

		return result && false;
	}

	/**
	 * Converts a status result from the EMF validation service to diagnostics.
	 * 
	 * @param status
	 *            the EMF validation service's status result
	 * @param diagnostics
	 *            a diagnostic chain to accumulate results on
	 */
	private void appendDiagnostics(IStatus status, DiagnosticChain diagnostics) {

		// Registry registry = EValidator.Registry.INSTANCE;
		// registry.toString();
		// MarkerFilter[] userFilters = super.getAllFilters();
		// Collection declaredFilters = MarkerSupportRegistry.getInstance().getRegisteredFilters();
		// Iterator iterator = declaredFilters.iterator();
		//
		// MarkerFilter[] allFilters = new MarkerFilter[userFilters.length + declaredFilters.size()];
		// System.arraycopy(userFilters, 0, allFilters, 0, userFilters.length);
		// int index = userFilters.length;
		//
		// while (iterator.hasNext()) {
		// allFilters[index] = (MarkerFilter) iterator.next();
		// index++;
		// }

		if (status.isMultiStatus()) {
			IStatus[] children = status.getChildren();

			for (IStatus element : children) {
				appendDiagnostics(element, diagnostics);
			}
		} else if (status instanceof IConstraintStatus) {
			/* <<< */
			// diagnostics.add(new BasicDiagnostic(status.getSeverity(), status.getPlugin(), status.getCode(),
			// status.getMessage(),
			// ((IConstraintStatus) status).getResultLocus().toArray()));
			/* --- */
			diagnostics.add(new ExtendedDiagnostic(status.getSeverity(), status.getPlugin(), ((IConstraintStatus) status).getConstraint(), status
					.getCode(), status.getMessage(), ((IConstraintStatus) status).getResultLocus().toArray()));
			/* >>> */
		} else {
			diagnostics
					.add(new BasicDiagnostic(status.getSeverity(), status.getPlugin(), status.getCode(), status.getMessage(), status.getChildren()));
		}

	}

	/**
	 * add filters to the batchValidator
	 * 
	 * @param filters
	 * @see IConstraintFilter
	 */
	private void addFilters(Set<IConstraintFilter> filters) {
		if (filters != null) {
			for (IConstraintFilter icf : filters) {
				batchValidator.addConstraintFilter(icf);
			}
		}

		return;
	}

	/**
	 * remove filters to the batch validator
	 * 
	 * @param filters
	 * @see IConstraintFilter
	 */
	private void removeFilters(Set<IConstraintFilter> filters) {
		if (filters != null) {
			for (IConstraintFilter icf : filters) {
				batchValidator.removeConstraintFilter(icf);
			}
		}

		return;
	}

}
