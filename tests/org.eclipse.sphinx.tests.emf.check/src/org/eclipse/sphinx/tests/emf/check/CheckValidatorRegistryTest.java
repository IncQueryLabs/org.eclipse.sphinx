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

import static org.junit.Assert.assertNotNull;

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
import org.eclipse.sphinx.tests.emf.check.internal.mocks.ValidatorContribution;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("nls")
public class CheckValidatorRegistryTest {

	private static CheckValidatorRegistryMockFactory mockFactory = new CheckValidatorRegistryMockFactory();

	private static TestableCheckValidatorRegistry checkValidatorRegistry;

	private static final ValidatorContribution simpleHummingbird20NamingCheckValidator = new ValidatorContribution(
			"org.eclipse.sphinx.examples.hummingbird20.check.simple.SimpleHummingbird20NamingCheckValidator", null);

	private static final ValidatorContribution hummingbird20NamingAndValuesCheckValidator = new ValidatorContribution(
			"org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20NamingAndValuesCheckValidator",
			"resources/input/Hummingbird20.checkcatalog");

	private static final ValidatorContribution hummingbird20ConnectionsCheckValidator = new ValidatorContribution(
			"org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20ConnectionsCheckValidator",
			"platform:/plugin/org.eclipse.sphinx.examples.hummingbird20.check/model/Hummingbird20.checkcatalog");

	@BeforeClass
	public static void initCheckValidatorRegistry() {
		IExtensionRegistry extensionRegistry = mockFactory.createExtensionRegistryMock(Activator.getPlugin(),
				simpleHummingbird20NamingCheckValidator, hummingbird20NamingAndValuesCheckValidator, hummingbird20ConnectionsCheckValidator);
		checkValidatorRegistry = new TestableCheckValidatorRegistry(extensionRegistry, new org.eclipse.emf.ecore.impl.EValidatorRegistryImpl());
	}

	@Test
	public void testContributedValidators() {
		assertNotNull(checkValidatorRegistry);

		EValidator validator = checkValidatorRegistry.getValidator(InstanceModel20Package.eINSTANCE);
		Assert.assertNotNull(validator);
		Assert.assertTrue(validator instanceof CompositeValidator);

		List<EValidator> children = ((CompositeValidator) validator).getChildren();
		Assert.assertEquals(3, children.size());

	}

	@Test
	public void testCheckCatalogs() {
		assertNotNull(checkValidatorRegistry);
		Collection<Catalog> checkCatalogs = checkValidatorRegistry.getCheckCatalogs();
		Assert.assertEquals(2, checkCatalogs.size());
	}
}
