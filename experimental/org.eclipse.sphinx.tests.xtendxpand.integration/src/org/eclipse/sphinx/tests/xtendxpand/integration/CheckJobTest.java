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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.tests.xtendxpand.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xtend.check.CheckEvaluationRequest;
import org.eclipse.sphinx.xtend.check.jobs.CheckJob;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;

public class CheckJobTest extends XtendXpandIntegrationTestCase {

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_NAME };
	}

	public void testHummingbird20ModelCheck() throws Exception {
		// Load Hummingbird 2.0 type model file
		IFile hb20TypeModelFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_TYPE_MODEL_PATH);
		assertNotNull(hb20TypeModelFile);
		assertTrue(hb20TypeModelFile.exists());
		Platform platform = (Platform) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20TypeModelFile);
		assertNotNull(platform);

		// Load Hummingbird 2.0 instance model file
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());
		Application application = (Application) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20InstanceModelFile);
		assertNotNull(application);

		// Load check resource
		IFile checkFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_CHECK_FILE_PATH);
		assertNotNull(checkFile);
		assertTrue(checkFile.exists());

		// Check execution
		CheckEvaluationRequest checkEvaluationRequest = new CheckEvaluationRequest(checkFile, application);
		CheckJob checkJob = new CheckJob("Check Job", new SphinxManagedEmfMetaModel(hb20InstanceModelFile.getProject()), checkEvaluationRequest); //$NON-NLS-1$
		checkJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());
		IStatus checkStatus = checkJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, checkStatus);
	}
}
