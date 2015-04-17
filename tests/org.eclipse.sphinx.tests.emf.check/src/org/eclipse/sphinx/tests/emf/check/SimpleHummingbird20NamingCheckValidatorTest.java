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

import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.emf.check.internal.CheckMethodWrapper;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.tests.emf.check.internal.TestableSimpleHummingbird20NamingCheckValidator;
import org.junit.Assert;
import org.junit.Test;

public class SimpleHummingbird20NamingCheckValidatorTest {

	@Test
	public void testInitCheckMethods() {
		TestableSimpleHummingbird20NamingCheckValidator validator = new TestableSimpleHummingbird20NamingCheckValidator();
		validator.initCheckMethods();

		List<CheckMethodWrapper> checkMethodsForApplication = validator.getCheckMethodsForModelObjectType(Application.class);
		Assert.assertEquals(1, checkMethodsForApplication.size());

		Catalog catalog = validator.getCheckCatalog();
		Assert.assertNull(catalog);
	}
}
