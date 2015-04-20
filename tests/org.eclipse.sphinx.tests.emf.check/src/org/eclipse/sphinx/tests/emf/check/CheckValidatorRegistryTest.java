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

import static org.eclipse.sphinx.tests.emf.check.internal.mocks.ValidatorContribution.testableHummingbird20ConnectionsCheckValidator;
import static org.eclipse.sphinx.tests.emf.check.internal.mocks.ValidatorContribution.testableHummingbird20NamingAndValuesCheckValidator;
import static org.eclipse.sphinx.tests.emf.check.internal.mocks.ValidatorContribution.testableSimpleHummingbird20NamingCheckValidator;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.sphinx.emf.check.CompositeValidator;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.tests.emf.check.internal.Activator;
import org.eclipse.sphinx.tests.emf.check.internal.TestableCheckValidatorRegistry;
import org.eclipse.sphinx.tests.emf.check.internal.mocks.CheckValidatorRegistryMockFactory;
import org.junit.Assert;
import org.junit.Test;

public class CheckValidatorRegistryTest {

	private static CheckValidatorRegistryMockFactory mockFactory = new CheckValidatorRegistryMockFactory();

	@Test
	public void testContributedValidators() {
		IExtensionRegistry extensionRegistry = mockFactory.createExtensionRegistryMock(Activator.getPlugin(),
				testableSimpleHummingbird20NamingCheckValidator, testableHummingbird20NamingAndValuesCheckValidator,
				testableHummingbird20ConnectionsCheckValidator);
		EValidator.Registry eValidatorRegistry = new org.eclipse.emf.ecore.impl.EValidatorRegistryImpl();

		TestableCheckValidatorRegistry checkValidatorRegistry = TestableCheckValidatorRegistry.INSTANCE;
		checkValidatorRegistry.clear();
		checkValidatorRegistry.setExtensionRegistry(extensionRegistry);
		checkValidatorRegistry.setEValidatorRegistry(eValidatorRegistry);

		EValidator validator = checkValidatorRegistry.getValidator(InstanceModel20Package.eINSTANCE);
		Assert.assertNotNull(validator);
		Assert.assertTrue(validator instanceof CompositeValidator);

		List<EValidator> children = ((CompositeValidator) validator).getChildren();
		Assert.assertEquals(3, children.size());

	}

	@Test
	public void testCheckCatalogs() {
		IExtensionRegistry extensionRegistry = mockFactory.createExtensionRegistryMock(Activator.getPlugin(),
				testableSimpleHummingbird20NamingCheckValidator, testableHummingbird20NamingAndValuesCheckValidator,
				testableHummingbird20ConnectionsCheckValidator);
		EValidator.Registry eValidatorRegistry = new org.eclipse.emf.ecore.impl.EValidatorRegistryImpl();

		TestableCheckValidatorRegistry checkValidatorRegistry = TestableCheckValidatorRegistry.INSTANCE;
		checkValidatorRegistry.clear();
		checkValidatorRegistry.setExtensionRegistry(extensionRegistry);
		checkValidatorRegistry.setEValidatorRegistry(eValidatorRegistry);

		Collection<Catalog> checkCatalogs = checkValidatorRegistry.getCheckCatalogs();
		Assert.assertEquals(2, checkCatalogs.size());
	}
}
