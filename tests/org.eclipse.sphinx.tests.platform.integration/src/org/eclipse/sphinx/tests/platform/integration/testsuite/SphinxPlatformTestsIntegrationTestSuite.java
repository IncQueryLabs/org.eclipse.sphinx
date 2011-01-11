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
package org.eclipse.sphinx.tests.platform.integration.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SphinxPlatformTestsIntegrationTestSuite {

	@SuppressWarnings("nls")
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.sphinx.platform.tests.testsuite");
		// $JUnit-BEGIN$
		suite.addTestSuite(org.eclipse.sphinx.tests.platform.integration.util.ExtendedPlatformTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.platform.integration.resource.ResourceDeltaVisitorTest.class);
		// $JUnit-END$
		return suite;
	}

}
