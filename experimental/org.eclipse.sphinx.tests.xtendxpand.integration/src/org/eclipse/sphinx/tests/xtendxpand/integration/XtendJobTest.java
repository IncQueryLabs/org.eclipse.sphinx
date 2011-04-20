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

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.tests.xtendxpand.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.XtendXpandTestReferenceWorkspace;
import org.eclipse.sphinx.xtend.XtendEvaluationRequest;
import org.eclipse.sphinx.xtend.jobs.XtendJob;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.xtend.typesystem.uml2.UML2MetaModel;

public class XtendJobTest extends XtendXpandIntegrationTestCase {

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { XtendXpandTestReferenceWorkspace.HB_TRANSFORM_XTEND_PROJECT_NAME };
	}

	public void testUML20ToHummingbird20() throws Exception {
		// Load UML resource
		IFile umlModelFile = refWks.transformXtendProject.getFile(XtendXpandTestReferenceWorkspace.HB_TRANSFORM_XTEND_PROJECT_UML_MODEL_PATH);
		assertNotNull(umlModelFile);
		assertTrue(umlModelFile.exists());

		// Load extension resource
		IFile extFile = refWks.transformXtendProject.getFile(XtendXpandTestReferenceWorkspace.UML2_HB20_EXT_FILE_PATH);
		assertNotNull(extFile);
		assertTrue(extFile.exists());

		// Get the first package of the UML2 file
		Model umlModel = (Model) EcorePlatformUtil.loadModelRoot(refWks.editingDomainUml2, umlModelFile);
		assertNotNull(umlModel);
		PackageableElement fistPackage = umlModel.getPackagedElements().get(0);
		assertNotNull(fistPackage);

		// Xtend execution
		XtendEvaluationRequest xtendEvaluationRequest = new XtendEvaluationRequest(XtendXpandTestReferenceWorkspace.XTEND_EXTENSION_NAME, umlModel);
		XtendJob xtendJob = new XtendJob("Xtend Job", new UML2MetaModel(), xtendEvaluationRequest); //$NON-NLS-1$
		xtendJob.setWorkspaceResourceLoader(new BasicWorkspaceResourceLoader());
		IStatus xtendStatus = xtendJob.runInWorkspace(new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, xtendStatus);

		// Xtend result verification
		Collection<Object> xtendResult = xtendJob.getXtendResult();
		assertEquals(1, xtendResult.size());
		Object object = xtendResult.iterator().next();
		assertNotNull(object);
		assertTrue(object instanceof Platform);
		assertEquals(fistPackage.getName(), ((Platform) object).getName());
	}
}
