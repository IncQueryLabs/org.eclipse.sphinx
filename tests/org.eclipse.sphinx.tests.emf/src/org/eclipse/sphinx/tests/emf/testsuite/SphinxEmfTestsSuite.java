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
package org.eclipse.sphinx.tests.emf.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SphinxEmfTestsSuite {

	@SuppressWarnings("nls")
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.sphinx.tests.emf.testsuite");
		// $JUnit-BEGIN$
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.comparators.EcoreComparatorTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.properties.PropertyFilterTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.internal.expressions.EMFObjectPropertyTesterTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.metamodel.MetaModelDescriptorRegistryTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.metamodel.MetaModelDescriptorTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.metamodel.MetaModelVersionDataTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.util.EObjectUtilTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.util.EcoreResourceUtilTest.class);
		// $JUnit-END$
		return suite;
	}

}
