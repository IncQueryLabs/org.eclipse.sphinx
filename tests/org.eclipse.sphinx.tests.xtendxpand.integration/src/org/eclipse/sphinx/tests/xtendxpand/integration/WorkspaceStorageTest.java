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
 *     itemis - [406564] BasicWorkspaceResourceLoader#getResource should not delegate to super
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.xtendxpand.integration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.xtend.shared.ui.core.internal.ResourceID;

public class WorkspaceStorageTest extends XtendXpandIntegrationTestCase {

	/**
	 * Test method for {@link org.eclipse.xtend.shared.ui.Activator#findStorage(javaProject, resourceID, searchJars)}.
	 * Test find storage for resources that are located in current workspace.
	 */
	public void testFindStorage_workspaceFile() throws Exception {
		// Loads the Hummingbird instance model file
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());

		// First case: test find Storage for resources that are located in current workspace also in referenced JAR
		// files on the classpath
		IStorage storage = findStorage(hb20InstanceModelFile, XtendXpandTestReferenceWorkspace.XPAND_CONFIGH, true);
		assertNotNull(storage);

		// Second case: test find Storage for resources that are located in current workspace but not from JAR files.
		storage = findStorage(hb20InstanceModelFile, XtendXpandTestReferenceWorkspace.XPAND_CONFIGH, false);
		assertNotNull(storage);
	}

	/**
	 * Test method for {@link org.eclipse.xtend.shared.ui.Activator#findStorage(javaProject, resourceID, searchJars)}.
	 * Test find storage for resources that are located in plug-ins.
	 */
	public void testFindStorage_pluginFile() throws Exception {
		// Loads the Hummingbird instance model file
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());

		// First case: test find Storage for resources that are located in plug-in also in referenced Jar files on
		// the classpath
		IStorage storage = findStorage(hb20InstanceModelFile, XtendXpandTestReferenceWorkspace.XPAND_CONFIGHP, true);
		assertNull(storage);

		// Second case: test find Storage for resources that are located in plug-ins but not from JAR files.
		storage = findStorage(hb20InstanceModelFile, XtendXpandTestReferenceWorkspace.XPAND_CONFIGHP, false);
		assertNull(storage);
	}

	/**
	 * Finds an XtendXpand Storage.
	 * 
	 * @param resourceName
	 *            Qualified name of the resource
	 * @param searchJars
	 *            <tt>true</tt> search also in referenced Jar files on the classpath
	 * @return The storage or <code>null</code> if not found
	 */
	private IStorage findStorage(IFile hb20InstanceModelFile, String resourceName, boolean searchJars) throws Exception {
		// Gets the java project of the project that contains the Hummingbird instance model hb20InstanceModelFile
		IJavaProject javaProject = JavaCore.create(hb20InstanceModelFile.getProject());
		ResourceID resourceID = new ResourceID(resourceName, IXtendXpandConstants.TEMPLATE_EXTENSION);

		// Search the resource to find storage
		return org.eclipse.xtend.shared.ui.Activator.findStorage(javaProject, resourceID, searchJars);
	}
}
