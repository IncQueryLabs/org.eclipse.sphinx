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

package org.eclipse.sphinx.tests.emf.check;

import java.util.List;

import org.eclipse.sphinx.emf.check.internal.CheckMethodWrapper;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.tests.emf.check.internal.TestableHummingbird20NamingAndValuesCheckValidator;
import org.junit.Assert;
import org.junit.Test;

public class Hummingbird20NamingAndValuesCheckValidatorTest {

	@Test
	public void testInitCheckMethods() {
		TestableHummingbird20NamingAndValuesCheckValidator validator = new TestableHummingbird20NamingAndValuesCheckValidator();
		validator.initCheckMethods();

		List<CheckMethodWrapper> checkMethodsForApplication = validator.getCheckMethodsForModelObjectType(Application.class);
		Assert.assertEquals(6, checkMethodsForApplication.size());

		List<CheckMethodWrapper> checkMethodsForComponent = validator.getCheckMethodsForModelObjectType(Component.class);
		Assert.assertEquals(3, checkMethodsForComponent.size());

		List<CheckMethodWrapper> checkMethodsForIdentifiable = validator.getCheckMethodsForModelObjectType(Identifiable.class);
		Assert.assertEquals(1, checkMethodsForIdentifiable.size());
	}
}
