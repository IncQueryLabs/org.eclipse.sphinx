/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.integration.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SphinxEmfTestsIntegration {

	@SuppressWarnings("nls")
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.sphinx.tests.emf.integration.testsuite");
		// $JUnit-BEGIN$
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.ModelManagementTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.internal.filessystem.PlatformURIFileStoreTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.internal.expressions.FilePropertyTesterTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.model.ModelDescriptorTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.resource.ModelConverterTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.resource.ScopingResourceSetTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.saving.SaveIndicatorUtilTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.util.EcorePlatformUtilTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.util.EcorePlatformUtilTest2.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.util.EcoreResourceUtilTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.util.EObjectUtilTest.class);
		;
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.integration.util.WorkspaceTransactionUtilTest.class);
		// $JUnit-END$
		return suite;
	}

}
