/**
 * <copyright>
 * 
 * Copyright (c) 2011-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [418005] Add support for model files with multiple root elements
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.xtendxpand.integration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;
import org.eclipse.sphinx.xtendxpand.CheckEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.jobs.CheckJob;

public class CheckJobTest extends XtendXpandIntegrationTestCase {

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_NAME };
	}

	public void testHummingbird20Check() throws Exception {
		// Check existence of Hummingbird 2.0 type model file
		IFile hb20TypeModelFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_TYPE_MODEL_PATH);
		assertNotNull(hb20TypeModelFile);
		assertTrue(hb20TypeModelFile.exists());

		// Load Hummingbird 2.0 type model file
		Platform platform = (Platform) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20TypeModelFile, null);
		assertNotNull(platform);

		// Check existence of Hummingbird 2.0 instance model file
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());

		// Load Hummingbird 2.0 instance model file
		Application application = (Application) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20InstanceModelFile, null);
		assertNotNull(application);

		// Check existence of check file
		IFile checkFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.HB_CHK_FILE_PATH);
		assertNotNull(checkFile);
		assertTrue(checkFile.exists());

		// Check execution
		CheckEvaluationRequest checkEvaluationRequest = new CheckEvaluationRequest(checkFile, application);
		CheckJob checkJob = new CheckJob("Check Job", new SphinxManagedEmfMetaModel(hb20InstanceModelFile.getProject()), checkEvaluationRequest); //$NON-NLS-1$
		checkJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());
		IStatus checkStatus = checkJob.run(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, checkStatus);
	}
}
