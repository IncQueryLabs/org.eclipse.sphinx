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

import org.eclipse.core.resources.IContainer;
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

	/*
	 * First case: test XtendXpandUtil.getUnderlyingFile(String, String) method for files that are located in current
	 * workspace.
	 */
	public void testGetUnderlyingFile_inWorkspace() throws Exception {
		// Check existence of "UML2ToHummingbird20.ext" file
		IFile extFile = refWks.transformXtendProject.getFile(XtendXpandTestReferenceWorkspace.UML2_HB20_EXT_FILE_PATH);
		assertNotNull(extFile);
		assertTrue(extFile.exists());

		// Create the resource loader to be used
		BasicWorkspaceResourceLoader workspaceResourceLoader = new BasicWorkspaceResourceLoader();
		workspaceResourceLoader.setContextProject(refWks.transformXtendProject);

		// Get the underlying file for "extensions::UML2ToHummingbird20::transform" extension
		IFile underlyingFile = XtendXpandUtil.getUnderlyingFile(XtendXpandTestReferenceWorkspace.XTEND_UML2_HB20_EXTENSION_NAME,
				IXtendXpandConstants.EXTENSION_EXTENSION, workspaceResourceLoader);
		assertNotNull(underlyingFile);
		assertTrue(underlyingFile.exists());
		assertEquals(extFile, underlyingFile);
	}

	/*
	 * Second case: test XtendXpandUtil.getUnderlyingFile(String, String) method for files that are linked to but
	 * actually located outside of the current workspace.
	 */
	public void testGetUnderlyingFile_linkedToWorkspace() throws Exception {
		// Copy "UML2ToHummingbird20_linked.ext" file into working directory
		File linkedExtFile = getTestFileAccessor().createWorkingCopyOfInputFile(XtendXpandTestReferenceWorkspace.LINKED_UML2_HB20_EXT_FILE_NAME);

		// Create a linked file for copied "UML2ToHummingbird20_linked.ext" file in the transformXtendProject project
		synchronizedCreateLinkFile(refWks.transformXtendProject, linkedExtFile);

		// Create the resource loader to be used
		BasicWorkspaceResourceLoader workspaceResourceLoader = new BasicWorkspaceResourceLoader();
		workspaceResourceLoader.setContextProject(refWks.transformXtendProject);

		// Get the underlying file for "UML2ToHummingbird20_linked::transform" extension
		IFile underlyingFile = XtendXpandUtil.getUnderlyingFile(XtendXpandTestReferenceWorkspace.LINKED_XTEND_UML2_HB20_EXTENSION_NAME,
				IXtendXpandConstants.EXTENSION_EXTENSION, workspaceResourceLoader);
		assertNotNull(underlyingFile);
		assertTrue(underlyingFile.exists());
		assertEquals(linkedExtFile.getAbsolutePath(), underlyingFile.getLocation().toOSString());
	}

	private void synchronizedCreateLinkFile(final IContainer targetContainer, final File fileToBeLinked) throws Exception {
		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IFile linkedFile = targetContainer.getFile(new Path(fileToBeLinked.getName()));
				linkedFile.createLink(new Path(fileToBeLinked.getAbsolutePath()), IResource.REPLACE, null);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}
}
