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
	 * An option to be used to provide the categories, as a Set of String, for the validation context.
	 */
	String OPTION_CATEGORIES = "CATEGORIES"; //$NON-NLS-1$

	ThreadLocal<CheckValidatorState> getState();
}