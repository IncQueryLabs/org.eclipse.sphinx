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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;
import org.eclipse.sphinx.xtendxpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.jobs.XpandJob;
import org.eclipse.sphinx.xtendxpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xtendxpand.util.XtendXpandUtil;

public class XpandJobTest extends XtendXpandIntegrationTestCase {

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_NAME };
	}

	/**
	 * Test Xpand code generation.
	 */
	public void testHummingbird20Codegen_workspace() throws Exception {
		// Load Hummingbird resource
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());

		// Load xpt resource
		IFile xptFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_XPT_FILE_PATH);
		assertNotNull(xptFile);
		assertTrue(xptFile.exists());

		// Load resource, create Xpand job and generate code
		Hummingbird20Codegen(hb20InstanceModelFile, XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_DEFINITION_NAME,
				XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_TOHOUTLET_DEFINITION_NAME);
	}

	/**
	 * Test get resource file from plug-ins in the execution.
	 */
	public void testGetResourceFile_plugin() throws Exception {
		// Load Hummingbird resource
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());

		BasicWorkspaceResourceLoader resourceLoader = new BasicWorkspaceResourceLoader();
		// Gets the underlying file associated to "templates::ConfigHP::main" extension defined in the plug-in
		IFile definitionFile = XtendXpandUtil.getUnderlyingFile(XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_DEFINITION_FROM_PLUGIN_NAME,
				IXtendXpandConstants.TEMPLATE_EXTENSION, resourceLoader);
		assertNull(definitionFile);
	}

	/**
	 * Creates Xpand Job. Execute the Xpand job, and generate the code.
	 */
	public void Hummingbird20Codegen(IFile hb20InstanceModelFile, String xPandConfigHDefinitionName, String xPandConfigHToHoutletDefinitionName)
			throws Exception {
		Application application = (Application) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20InstanceModelFile);
		assertNotNull(application);

		// Create Xpand Job
		XpandEvaluationRequest xpandEvaluationRequest = new XpandEvaluationRequest(xPandConfigHDefinitionName, application);
		SphinxManagedEmfMetaModel metaModel = new SphinxManagedEmfMetaModel(hb20InstanceModelFile.getProject());
		XpandJob xpandJob = new XpandJob("Xpand Job", metaModel, xpandEvaluationRequest); //$NON-NLS-1$

		// Xpand execution
		xpandJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());
		IStatus xpandStatus = xpandJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, xpandStatus);

		// Load generated resource from current working directory and verify its content
		File file = new File(XtendXpandTestReferenceWorkspace.CONFIGH_FILE_NAME);
		assertNotNull(file);
		assertTrue(file.exists());
		String contents = readAsString(file);
		assertTrue(contents.indexOf("#define ParamVal1 111") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal2 222") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal3 333") != -1); //$NON-NLS-1$

		/*
		 * Re-execute the xpand job and generate into 'HOUTLET' folder
		 */

		// Load xpt resource
		IFile xptFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_TO_HOUTLET_XPT_FILE_PATH);
		assertNotNull(xptFile);
		assertTrue(xptFile.exists());

		xpandEvaluationRequest = new XpandEvaluationRequest(xPandConfigHToHoutletDefinitionName, application);
		xpandJob = new XpandJob("Xpand Job", metaModel, xpandEvaluationRequest); //$NON-NLS-1$
		xpandJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());

		// Add an outlet named HOUTLET
		ExtendedOutlet outlet = new ExtendedOutlet(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HOUTLET_FOLDER_NAME,
				refWks.codegenXpandProject.getFolder(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HOUTLET_FOLDER_NAME));
		outlet.setOverwrite(true);
		xpandJob.getOutlets().add(outlet);

		xpandStatus = xpandJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, xpandStatus);

		// Load generated resource from 'HOUTLET' folder and verify its content
		IFile genFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_GEN_FILE_PATH);
		assertNotNull(genFile);
		assertTrue(genFile.exists());
		contents = readAsString(genFile);
		assertTrue(contents.indexOf("#define ParamVal1 111") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal2 222") != -1); //$NON-NLS-1$
		assertTrue(contents.indexOf("#define ParamVal3 333") != -1); //$NON-NLS-1$
	}

	private String readAsString(Object file) throws Exception {
		Assert.isTrue(file instanceof IFile || file instanceof File);

		BufferedInputStream inputStream = null;
		if (file instanceof IFile) {
			inputStream = new BufferedInputStream(((IFile) file).getContents());
		} else if (file instanceof File) {
			inputStream = new BufferedInputStream(new FileInputStream((File) file));
		}
		try {
			byte[] buffer = new byte[1024];
			int bufferLength;
			StringBuilder content = new StringBuilder();
			while ((bufferLength = inputStream.read(buffer)) > -1) {
				content.append(new String(buffer, 0, bufferLength));
			}
			return content.toString();
		} finally {
			inputStream.close();
		}
	}

}
