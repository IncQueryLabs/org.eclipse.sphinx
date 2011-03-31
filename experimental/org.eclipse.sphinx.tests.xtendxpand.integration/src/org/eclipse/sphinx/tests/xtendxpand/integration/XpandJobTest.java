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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.tests.xtendxpand.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.jobs.XpandJob;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xtend.typesystem.emf.SphinxManagedEmfMetaModel;

public class XpandJobTest extends XtendXpandIntegrationTestCase {

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_NAME };
	}

	public void testHummingbird20Codegen() throws Exception {
		// Load Hummingbird 2.0 instance model file
		IFile hb20InstanceModelFile = refWks.codegenXpandProject
				.getFile(XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_HB_INSTANCE_MODEL_PATH);
		assertNotNull(hb20InstanceModelFile);
		assertTrue(hb20InstanceModelFile.exists());
		Application application = (Application) EcorePlatformUtil.loadModelRoot(refWks.editingDomain20, hb20InstanceModelFile);
		assertNotNull(application);

		// Load xpt resource
		IFile xptFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_XPT_FILE_PATH);
		assertNotNull(xptFile);
		assertTrue(xptFile.exists());

		// Xpand execution
		XpandEvaluationRequest xpandEvaluationRequest = new XpandEvaluationRequest(XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_DEFINITION_NAME,
				application);
		XpandJob xpandJob = new XpandJob("Xpand Job", new SphinxManagedEmfMetaModel(hb20InstanceModelFile.getProject()), xpandEvaluationRequest); //$NON-NLS-1$
		xpandJob.setScopingResourceLoader(new BasicWorkspaceResourceLoader());
		IStatus xpandStatus = xpandJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, xpandStatus);

		// Load generated resource from java.io.tmpdir and verify its content
		String defaultGenFilePath = System.getProperty("java.io.tmpdir").concat(XtendXpandTestReferenceWorkspace.CONFIGH_FILE_NAME); //$NON-NLS-1$
		File file = new Path(defaultGenFilePath).toFile();
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
		xptFile = refWks.codegenXpandProject.getFile(XtendXpandTestReferenceWorkspace.CONFIGH_TO_HOUTLET_XPT_FILE_PATH);
		assertNotNull(xptFile);
		assertTrue(xptFile.exists());

		xpandEvaluationRequest = new XpandEvaluationRequest(XtendXpandTestReferenceWorkspace.XPAND_CONFIGH_TOHOUTLET_DEFINITION_NAME, application);
		xpandJob = new XpandJob("Xpand Job", new SphinxManagedEmfMetaModel(hb20InstanceModelFile.getProject()), xpandEvaluationRequest); //$NON-NLS-1$
		xpandJob.setScopingResourceLoader(new BasicWorkspaceResourceLoader());

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
