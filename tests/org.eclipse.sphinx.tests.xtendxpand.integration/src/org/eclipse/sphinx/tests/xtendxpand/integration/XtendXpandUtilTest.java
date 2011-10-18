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
package org.eclipse.sphinx.tests.xtendxpand.integration;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xtendxpand.util.XtendXpandUtil;

public class XtendXpandUtilTest extends XtendXpandIntegrationTestCase {

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_TRANSFORM_XTEND_PROJECT_NAME };
	}

	public void testGetUnderlyingFile() throws Exception {
		/*
		 * First case: test the XtendXpandUtil.getUnderlyingFile(String, String) method for files that are located in
		 * current workspace.
		 */

		// Loads the "UML2ToHummingbird20.ext" resource contained into transformXtendProject project
		IFile extFile = refWks.transformXtendProject.getFile(XtendXpandTestReferenceWorkspace.UML2_HB20_EXT_FILE_PATH);
		assertNotNull(extFile);
		assertTrue(extFile.exists());

		// Creates the resource loader to be used
		BasicWorkspaceResourceLoader workspaceResourceLoader = new BasicWorkspaceResourceLoader();
		workspaceResourceLoader.setContextProject(refWks.transformXtendProject);

		// Gets the underlying file associated with "extensions::UML2ToHummingbird20::transform" extension defined into
		// the UML2ToHummingbird20 file.
		IFile underlyingFile = XtendXpandUtil.getUnderlyingFile(XtendXpandTestReferenceWorkspace.XTEND_EXTENSION_NAME,
				IXtendXpandConstants.EXTENSION_EXTENSION, workspaceResourceLoader);
		assertNotNull(underlyingFile);
		assertTrue(underlyingFile.exists());
		assertEquals(extFile, underlyingFile);

		/*
		 * Second case: test the XtendXpandUtil.getUnderlyingFile(String, String) method for linked files that are not
		 * located in current workspace.
		 */

		// Copy the "extensionFile.ext" resource to be linked into working directory
		File file = getTestFileAccessor().createWorkingCopyOfInputFile(XtendXpandTestReferenceWorkspace.EXTENSION_FILE_NAME_TO_BE_LINK);
		// Creates a linked file into the transformXtendProject project
		synchronizedCreateLinkFile(file.getAbsolutePath());

		// Gets the underlying file associated to "extensionFile::transform" extension defined into the linked file of
		// the
		// transformXtendProject.
		underlyingFile = XtendXpandUtil.getUnderlyingFile(XtendXpandTestReferenceWorkspace.LINKED_XTEND_EXTENSION_NAME,
				IXtendXpandConstants.EXTENSION_EXTENSION, workspaceResourceLoader);
		assertNotNull(underlyingFile);
		assertTrue(underlyingFile.exists());
		assertEquals(file.getAbsolutePath(), underlyingFile.getLocation().toOSString());
	}

	protected void synchronizedCreateLinkFile(final String fileName) throws Exception {
		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// Create a linked file into the transformXtendProject project
				IFile linkedFile = refWks.transformXtendProject.getFile(XtendXpandTestReferenceWorkspace.EXTENSION_FILE_NAME_TO_BE_LINK);
				linkedFile.createLink(new Path(fileName), IResource.REPLACE, null);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}
}
