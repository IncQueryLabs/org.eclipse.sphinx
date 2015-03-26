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

public class IValidationConstants {

	/**
	 * Validation problem marker type.
	 */
	public static final String MODEL_VALIDATION_PROBLEM = "sphinx.emf.validation.problem.marker"; //$NON-NLS-1$

	/**
	 * Label of the Problem markers handler job
	 */
	public static final String HANDLE_PROBLEM_MARKERS_JOB_LABEL = "Handling diagnostics"; //$NON-NLS-1$

	/**
	 * Id of the category named "Other" that enable users to switch on or off validators that are not associated with
	 * any category.
	 */
	public static String other_category_id = "other"; //$NON-NLS-1$

	/**
	 * Id of the category named "Intrinsic Model Integrity Checks" that enable users to switch on or off EMF-provided
	 * basic validators.
	 */
	public static final String intrinsic_model_integrity_checks_category_id = "intrinsic.model.integrity.checks"; //$NON-NLS-1$

}
