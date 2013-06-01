/**
 * <copyright>
 * 
 * Copyright (c) 2013  itemis and others.
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
package org.eclipse.sphinx.tests.xtendxpand.integration;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.tests.xtendxpand.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;

public class BasicWorkspaceResourceLoaderTest extends XtendXpandIntegrationTestCase {

	public void testGetResource_inWorkspace() throws Exception {
		// Check existence of template file
		IFile xptFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_XPT_FILE_PATH);
		assertNotNull(xptFile);
		assertTrue(xptFile.exists());

		// Setup workspace resource loader
		BasicWorkspaceResourceLoader workspaceResourceLoader = new BasicWorkspaceResourceLoader();
		workspaceResourceLoader.setContextProject(refWks.codegenXpandProject);

		// Try to retrieve template file from workspace using workspace resource loader
		URL url = workspaceResourceLoader.getResource(XtendXpandTestReferenceWorkspace.CONFIGH_XPT_FILE_PATH);

		// Make sure that this is supported
		assertNotNull(url);
		assertEquals(xptFile.getLocationURI().toURL(), url);
	}

	public void testGetResource_inPlugin() throws Exception {
		// Check existence of template file
		assertTrue(FileLocator.find(Activator.getPlugin().getBundle(), new Path(XtendXpandTestTemplatesInPlugin.CONFIGH_XPT_FILE_PATH), null) != null);

		// Setup workspace resource loader
		BasicWorkspaceResourceLoader workspaceResourceLoader = new BasicWorkspaceResourceLoader();
		workspaceResourceLoader.setContextProject(refWks.codegenXpandProject);

		// Try to retrieve template file from plug-in using workspace resource loader
		URL url = workspaceResourceLoader.getResource(XtendXpandTestTemplatesInPlugin.CONFIGH_XPT_FILE_PATH);

		// Make sure that this is not supported
		assertNull(url);
	}
}
