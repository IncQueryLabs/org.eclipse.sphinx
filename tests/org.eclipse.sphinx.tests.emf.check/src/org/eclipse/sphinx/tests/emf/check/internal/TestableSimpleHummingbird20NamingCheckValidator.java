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

import org.eclipse.sphinx.emf.check.internal.CheckMethodWrapper;
import org.eclipse.sphinx.examples.hummingbird20.check.simple.SimpleHummingbird20NamingCheckValidator;

public class TestableSimpleHummingbird20NamingCheckValidator extends SimpleHummingbird20NamingCheckValidator {

	public TestableSimpleHummingbird20NamingCheckValidator() {
		super(TestableCheckValidatorRegistry.INSTANCE);
	}

	@Override
	public void initCheckMethods() {
		super.initCheckMethods();
	}

	@Override
	public List<CheckMethodWrapper> getCheckMethodsForModelObjectType(Class<?> modelObjectType) {
		return super.getCheckMethodsForModelObjectType(modelObjectType);
	}
}
