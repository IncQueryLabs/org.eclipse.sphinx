/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.tests.xtendxpand.integration.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SphinxXtendXpandTestsIntegration {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.eclipse.sphinx.tests.xtendxpand.integration"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(org.eclipse.sphinx.tests.xtendxpand.integration.XtendJobTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.xtendxpand.integration.XpandJobTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.xtendxpand.integration.CheckJobTest.class);
		// $JUnit-END$
		return suite;
	}
}
