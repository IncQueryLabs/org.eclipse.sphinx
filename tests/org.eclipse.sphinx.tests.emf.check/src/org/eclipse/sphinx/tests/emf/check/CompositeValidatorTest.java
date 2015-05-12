package org.eclipse.sphinx.tests.emf.check;

import static org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20ConnectionsCheckValidator.ISSUE_MSG_1;
import static org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20NamingAndValuesCheckValidator.ISSUE_MSG_CASE1;
import static org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20NamingAndValuesCheckValidator.ISSUE_MSG_CASE2;
import static org.eclipse.sphinx.examples.hummingbird20.check.withcatalog.Hummingbird20NamingAndValuesCheckValidator.ISSUE_MSG_CASE3;
import static org.eclipse.sphinx.tests.emf.check.internal.TestableHummingbird20NamingAndValuesCheckValidator.ISSUE_MSG_TEST1;
import static org.eclipse.sphinx.tests.emf.check.internal.mocks.ValidatorContribution.testableHummingbird20ConnectionsCheckValidator;
import static org.eclipse.sphinx.tests.emf.check.internal.mocks.ValidatorContribution.testableHummingbird20NamingAndValuesCheckValidator;

import java.util.Collection;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.sphinx.emf.check.catalog.Catalog;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;
import org.eclipse.sphinx.tests.emf.check.internal.Activator;
import org.eclipse.sphinx.tests.emf.check.internal.TestableCheckValidatorRegistry;
import org.eclipse.sphinx.tests.emf.check.internal.mocks.CheckValidatorRegistryMockFactory;
import org.eclipse.sphinx.tests.emf.check.util.CheckTestUtil;
import org.junit.Assert;
import org.junit.Test;

public class CompositeValidatorTest {

	private static final String ERROR_MSG_CIRCULAR_CONTAINMENT = "An object may not circularly contain itself"; //$NON-NLS-1$

	private static CheckValidatorRegistryMockFactory mockFactory = new CheckValidatorRegistryMockFactory();

	@Test
	public void testNoCirculaContainmentError() {

		EValidator.Registry eValidatorRegistry = new org.eclipse.emf.ecore.impl.EValidatorRegistryImpl();
		Diagnostician diagnostician = new Diagnostician(eValidatorRegistry);

		IExtensionRegistry extensionRegistry = mockFactory.createExtensionRegistryMock(Activator.getPlugin(),
				testableHummingbird20NamingAndValuesCheckValidator, testableHummingbird20ConnectionsCheckValidator);
		TestableCheckValidatorRegistry checkValidatorRegistry = TestableCheckValidatorRegistry.INSTANCE;
		checkValidatorRegistry.clear();
		checkValidatorRegistry.setExtensionRegistry(extensionRegistry);
		checkValidatorRegistry.setEValidatorRegistry(eValidatorRegistry);

		Collection<Catalog> checkCatalogs = checkValidatorRegistry.getCheckCatalogs();
		Assert.assertNotNull(checkCatalogs);
		Assert.assertEquals(2, checkCatalogs.size());

		Diagnostic diagnostic = diagnostician.validate(InstanceModel20Factory.eINSTANCE.createApplication());
		Assert.assertEquals(7, diagnostic.getChildren().size());

		// Expected messages
		Assert.assertEquals(1, CheckTestUtil.findDiagnositcsWithMsg(diagnostic.getChildren(), ISSUE_MSG_CASE1).size());
		Assert.assertEquals(1, CheckTestUtil.findDiagnositcsWithMsg(diagnostic.getChildren(), ISSUE_MSG_CASE2).size());
		Assert.assertEquals(1, CheckTestUtil.findDiagnositcsWithMsg(diagnostic.getChildren(), ISSUE_MSG_CASE3).size());
		Assert.assertEquals(1, CheckTestUtil.findDiagnositcsWithMsg(diagnostic.getChildren(), ISSUE_MSG_TEST1).size());
		Assert.assertEquals(1, CheckTestUtil.findDiagnositcsWithMsg(diagnostic.getChildren(), ISSUE_MSG_1).size());

		Assert.assertEquals(0, CheckTestUtil.findDiagnositcsWithMsg(diagnostic.getChildren(), ERROR_MSG_CIRCULAR_CONTAINMENT).size());

	}
}
