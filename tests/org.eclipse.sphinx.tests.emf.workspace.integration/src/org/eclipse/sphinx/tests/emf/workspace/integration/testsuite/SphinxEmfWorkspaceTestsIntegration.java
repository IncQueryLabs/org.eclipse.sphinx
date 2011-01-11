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
package org.eclipse.sphinx.tests.emf.workspace.integration.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SphinxEmfWorkspaceTestsIntegration {

	@SuppressWarnings("nls")
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.sphinx.tests.emf.workspace.integration.testSuite");
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.domain.mapping.DefaultWorkspaceEditingDomainMappingTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.inmemoryresources.HandleResourcesOnlyInMemoryTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.internal.EditingDomainAdapterFactoryTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.internal.loading.LoadJobTest_LoadFiles.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.internal.loading.LoadJobTest_LoadModel.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.internal.loading.LoadJobTest_LoadProjects.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.loading.ModelLoadManagerTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.referentialintegrity.ReferentialIntegrityTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.saving.ModelSavingTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.saving.ModelSaveManagerTest.class);
		suite.addTestSuite(org.eclipse.sphinx.tests.emf.workspace.integration.syncing.ModelSyncingTest.class);

		return suite;
	}

}
