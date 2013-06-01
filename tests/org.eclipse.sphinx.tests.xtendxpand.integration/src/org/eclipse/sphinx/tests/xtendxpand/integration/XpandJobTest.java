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
 *     itemis - [343844] Enable multiple Xtend MetaModels to be configured on BasicM2xAction, M2xConfigurationWizard, and Xtend/Xpand/CheckJob
 *     itemis - [406564] BasicWorkspaceResourceLoader#getResource should not delegate to super
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.xtendxpand.integration;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.testutils.TestFileAccessor;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;
import org.eclipse.sphinx.xtendxpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.jobs.XpandJob;
import org.eclipse.sphinx.xtendxpand.outlet.ExtendedOutlet;

public class XpandJobTest extends XtendXpandIntegrationTestCase {

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_NAME };
	}

	/**
	 * Test Xpand code generation.
	 */
	public void testHummingbird20Codegen() throws Exception {
		// Check existence of Hummingbird 2.0 instance model file
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());

		/*
		 * Execute an Xpand job that generates current working directory
		 */

		// Check existence of template file
		IFile xptFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_XPT_FILE_PATH);
		assertNotNull(xptFile);
		assertTrue(xptFile.exists());

		// Load Hummingbird 2.0 instance model file
		Application application = (Application) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20InstanceModelFile);
		assertNotNull(application);

		// Xpand execution
		XpandEvaluationRequest xpandEvaluationRequest = new XpandEvaluationRequest(XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_DEFINITION_NAME,
				application);
		SphinxManagedEmfMetaModel metaModel = new SphinxManagedEmfMetaModel(hb20InstanceModelFile.getProject());
		XpandJob xpandJob = new XpandJob("Xpand Job", metaModel, xpandEvaluationRequest); //$NON-NLS-1$
		xpandJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());
		IStatus xpandStatus = xpandJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, xpandStatus);

		// Load generated file from current working directory and verify its content
		File file = new File(XtendXpandTestReferenceWorkspace.CONFIGH_FILE_NAME);
		assertNotNull(file);
		assertTrue(file.exists());
		String contents = TestFileAccessor.readAsString(file);
		assertTrue(contents.indexOf("#define ParamVal1 111") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal2 222") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal3 333") != -1); //$NON-NLS-1$

		/*
		 * Execute another Xpand job that generates into 'HOUTLET' folder
		 */

		// Check existence of template file
		IFile xptFile1 = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_TO_HOUTLET_XPT_FILE_PATH);
		assertNotNull(xptFile1);
		assertTrue(xptFile1.exists());

		// Create outlet
		ExtendedOutlet outlet = new ExtendedOutlet(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HOUTLET_FOLDER_NAME,
				refWks.codegenXpandProject.getFolder(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HOUTLET_FOLDER_NAME));
		outlet.setOverwrite(true);

		// Xpand execution
		xpandEvaluationRequest = new XpandEvaluationRequest(XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_TOHOUTLET_DEFINITION_NAME, application);
		xpandJob = new XpandJob("Xpand Job", metaModel, xpandEvaluationRequest); //$NON-NLS-1$
		xpandJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());
		xpandJob.getOutlets().add(outlet);
		xpandStatus = xpandJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, xpandStatus);

		// Load generated resource from 'HOUTLET' folder and verify its content
		IFile genFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_GEN_FILE_PATH);
		assertNotNull(genFile);
		assertTrue(genFile.exists());
		contents = TestFileAccessor.readAsString(genFile);
		assertTrue(contents.indexOf("#define ParamVal1 111") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal2 222") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal3 333") != -1); //$NON-NLS-1$
	}
}
