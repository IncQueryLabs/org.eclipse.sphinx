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

package org.eclipse.sphinx.tests.emf.check.internal.mocks;

public class ValidatorContribution {

	private String validatorClass;

	private String catalog;

	public ValidatorContribution(String validatorClass, String catalog) {
		this.validatorClass = validatorClass;
		this.catalog = catalog;
	}

	public String getValidatorClass() {
		return validatorClass;
	}

	public String getCatalog() {
		return catalog;
	}
}
