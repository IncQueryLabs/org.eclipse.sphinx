/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.testutils;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;

public abstract class AbstractTestCase extends TestCase {

	protected TestFileAccessor fileAccessor = null;

	private boolean ignoreLoadProblems;
	private boolean ignoreSaveProblems;

	@Override
	protected void setUp() throws Exception {
		if (fileAccessor == null) {
			fileAccessor = new TestFileAccessor(getTestPlugin());
		}
		ignoreLoadProblems = false;
		ignoreSaveProblems = false;
	}

	protected abstract Plugin getTestPlugin();

	protected boolean isIgnoreLoadProblems() {
		return ignoreLoadProblems;
	}

	public void setIgnoreLoadProblems(boolean ignoreLoadProblems) {
		this.ignoreLoadProblems = ignoreLoadProblems;
	}

	protected boolean isIgnoreSaveProblems() {
		return ignoreSaveProblems;
	}

	public void setIgnoreSaveProblems(boolean ignoreSaveProblems) {
		this.ignoreSaveProblems = ignoreSaveProblems;
	}

	// TODO Remove fileAccessor from parameter list and use fFileAccessor instead
	protected EObject loadInputFile(String inputFileName, TestFileAccessor fileAccessor, ResourceFactoryImpl resourceFactory, EPackage ePackage,
			Map<?, ?> options) throws Exception {
		return loadFile(fileAccessor.getInputFileURI(inputFileName), resourceFactory, ePackage, options);
	}

	// TODO Remove fileAccessor from parameter list and use fFileAccessor instead
	protected EObject loadWorkingFile(String workingFileName, TestFileAccessor fileAccessor, ResourceFactoryImpl resourceFactory, EPackage ePackage,
			Map<?, ?> options) throws Exception {
		return loadFile(fileAccessor.getWorkingFileURI(workingFileName), resourceFactory, ePackage, options);
	}

	// TODO Enable external resourceSet to be handed in
	private EObject loadFile(java.net.URI fileURI, ResourceFactoryImpl resourceFactory, EPackage ePackage, Map<?, ?> options) throws Exception {
		URI emfURI = fileAccessor.convertToEMFURI(fileURI);
		XMLResource resource = (XMLResource) resourceFactory.createResource(emfURI);
		resource.load(options);

		// TODO Check if this is still needed and remove ePackage from parameter list if not
		ResourceSet resourceSet = createDefaultResourceSet();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		resourceSet.getResources().add(resource);

		assertHasNoLoadProblems(resource);

		return resource.getContents().get(0);
	}

	protected void saveWorkingFile(String fileName, EObject modelRoot, TestFileAccessor fileAccessor, ResourceFactoryImpl resourceFactory)
			throws Exception {
		saveFile(fileAccessor.getWorkingFileURI(fileName), modelRoot, resourceFactory, null);
	}

	// TODO Enable external resourceSet to be handed in
	private void saveFile(java.net.URI fileURI, EObject modelRoot, ResourceFactoryImpl resourceFactory, Map<?, ?> options) throws Exception {
		URI emfURI = fileAccessor.convertToEMFURI(fileURI);
		XMLResource resource = (XMLResource) resourceFactory.createResource(emfURI);
		resource.getContents().add(modelRoot);
		resource.save(options);

		assertHasNoSaveProblems(resource);
	}

	protected ScopingResourceSetImpl createDefaultResourceSet() {
		return new ScopingResourceSetImpl();
	}

	public void assertEquals(EObject eObject1, EObject eObject2) {
		EcoreEqualityAssert.assertEquals(eObject1, eObject2);
	}

	@SuppressWarnings("nls")
	protected void assertHasNoLoadProblems(Resource resource) {
		assertNotNull(resource);
		if (!isIgnoreLoadProblems()) {
			assertTrue("Errors encountered during resource loading: " + formatDiagnosticMessages(resource.getErrors()),
					resource.getErrors().size() == 0);
			assertTrue("Warnings encountered during resource loading: " + formatDiagnosticMessages(resource.getWarnings()), resource.getWarnings()
					.size() == 0);
		}
	}

	@SuppressWarnings("nls")
	protected void assertHasNoSaveProblems(Resource resource) {
		assertNotNull(resource);
		if (!isIgnoreSaveProblems()) {
			assertTrue("Errors encountered during resource saving: " + formatDiagnosticMessages(resource.getErrors()),
					resource.getErrors().size() == 0);
			assertTrue("Warnings encountered during resource saving: " + formatDiagnosticMessages(resource.getWarnings()), resource.getWarnings()
					.size() == 0);
		}
	}

	protected String formatDiagnosticMessages(EList<Diagnostic> diagnostics) {
		StringBuilder msg = new StringBuilder();
		for (Diagnostic diagnostic : diagnostics) {
			if (msg.length() > 0) {
				msg.append("; "); //$NON-NLS-1$
			}
			msg.append(diagnostic.getMessage());
		}
		return msg.toString();
	}
}
