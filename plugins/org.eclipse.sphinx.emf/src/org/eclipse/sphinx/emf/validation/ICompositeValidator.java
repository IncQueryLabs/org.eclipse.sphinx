/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.emf.validation;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;

/**
 * Allows multiple {@link EValidator}s for the same {@link EPackage} to be combined and commonly registered upon the
 * {@link EValidator.Registry}.
 */
public interface ICompositeValidator extends EValidator {

	List<EValidator> getValidators();

	void removeValidator(EValidator validator);

	void addValidator(EValidator validator);

}
