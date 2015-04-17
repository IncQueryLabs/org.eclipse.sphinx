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

package org.eclipse.sphinx.tests.emf.check.internal;

import java.util.List;

import org.eclipse.sphinx.emf.check.Check;
import org.eclipse.sphinx.emf.check.internal.CheckMethodWrapper;
import org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20NamingAndValuesCheckValidator;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;

public class TestableHummingbird20NamingAndValuesCheckValidator extends Hummingbird20NamingAndValuesCheckValidator {

	@Override
	public void initCheckMethods() {
		super.initCheckMethods();
	}

	@Override
	public List<CheckMethodWrapper> getCheckMethodsForModelObjectType(Class<?> modelObjectType) {
		return super.getCheckMethodsForModelObjectType(modelObjectType);
	}

	@Check(constraint = "ApplicationNameNotValid")
	void checkIdentifiableName(Identifiable identifiable) {
		issue(identifiable, Common20Package.Literals.IDENTIFIABLE__NAME,
				"(#test: part of Category1 and Category2 as per check catalog and blank check annotation)"); //$NON-NLS-1$
	}
}
