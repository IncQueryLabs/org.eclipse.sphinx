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

import org.eclipse.emf.ecore.EValidator;

public interface ICheckValidator extends EValidator {

	/**
	 * An option to be used, for the validation context, to restrict the scope of the validated categories. If the
	 * option is not present, all the categories will be validated.
	 */
	String OPTION_CATEGORIES = "CATEGORIES"; //$NON-NLS-1$

	/**
	 * Specifies whether to validate the intrinsic EMF default constraints or not. The default value of this option is
	 * <code>Boolean.FALSE</code>.
	 */
	String OPTION_ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS = "ENABLE_INTRINSIC_MODEL_INTEGRITY_CONSTRAINTS"; //$NON-NLS-1$

	ThreadLocal<CheckValidatorState> getState();
}