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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;

/**
 * @param <T>
 */
@SuppressWarnings("nls")
public abstract class AbstractTestCase extends TestCase {

	protected TestFileAccessor fFileAccessor = null;

	protected final TestFileAccessor getTestFileAccessor() {
		if (fFileAccessor == null) {
			fFileAccessor = new TestFileAccessor(getTestPlugin());
		}
		return fFileAccessor;
	}

	protected abstract Plugin getTestPlugin();

	protected ScopingResourceSetImpl createDefaultResourceSet() {
		return new ScopingResourceSetImpl();
	}

	/**
	 * Sets up the test workspace by importing the reference workspace given in parameter.
	 */
	@Override
	protected void setUp() throws Exception {
		if (fFileAccessor == null) {
			fFileAccessor = new TestFileAccessor(getTestPlugin());
		}
	}

	/**
	 * Tears down the test workspace by deleting all projects.
	 */
	@Override
	protected void tearDown() throws Exception {
	}

	protected EObject loadInputFile(String fileName, TestFileAccessor fFileAccessor, ResourceFactoryImpl resourceFactory, EPackage ePackage)
			throws Exception {
		ResourceSet resourceSet = createDefaultResourceSet();

		// Register the package
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

		java.net.URI uri = fFileAccessor.getInputFileURI(fileName);
		URI fileURI = fFileAccessor.convertToEMFURI(uri);
		XMLResource resource = (XMLResource) resourceFactory.createResource(fileURI);

		resource.load(null);
		resourceSet.getResources().add(resource);

		return resource.getContents().get(0);
	}

	protected EObject loadInputFile(String fileName, XMLParserPoolImpl parserPool, TestFileAccessor fFileAccessor,
			ResourceFactoryImpl resourceFactory, EPackage ePackage) throws Exception {
		ResourceSet resourceSet = createDefaultResourceSet();

		// Register the package
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

		java.net.URI uri = fFileAccessor.getInputFileURI(fileName);
		URI fileURI = fFileAccessor.convertToEMFURI(uri);
		XMLResource resource = (XMLResource) resourceFactory.createResource(fileURI);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put(XMLResource.OPTION_USE_PARSER_POOL, parserPool);
		resource.load(options);

		return resource.getContents().get(0);
	}

	protected EObject loadWorkingFile(String fileName, TestFileAccessor fFileAccessor, ResourceFactoryImpl arResourceFactory, EPackage ePackage)
			throws Exception {
		ResourceSet resourceSet = createDefaultResourceSet();

		// Register the package
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

		java.net.URI uri = fFileAccessor.getWorkingFileURI(fileName);
		URI fileURI = fFileAccessor.convertToEMFURI(uri);
		XMLResource resource = (XMLResource) arResourceFactory.createResource(fileURI);

		resource.load(null);
		resourceSet.getResources().add(resource);

		return resource.getContents().get(0);
	}

	protected EObject loadWorkingFile(String fileName, XMLParserPoolImpl parserPool, ResourceFactoryImpl resourceFactory, EPackage ePackage)
			throws Exception {
		ResourceSet resourceSet = createDefaultResourceSet();

		// Register the package
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

		java.net.URI uri = fFileAccessor.getWorkingFileURI(fileName);
		URI fileURI = fFileAccessor.convertToEMFURI(uri);
		XMLResource resource = (XMLResource) resourceFactory.createResource(fileURI);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put(XMLResource.OPTION_USE_PARSER_POOL, parserPool);
		resource.load(options);

		return resource.getContents().get(0);
	}

	protected void saveInputFile(String fileName, EObject modelRoot, ResourceFactoryImpl resourceFactory) throws Exception {
		java.net.URI uri = fFileAccessor.getInputFileURI(fileName);
		URI emfURI = fFileAccessor.convertToEMFURI(uri);
		XMLResource resource = (XMLResource) resourceFactory.createResource(emfURI);
		resource.getContents().add(modelRoot);
		resource.save(null);
	}

	protected void saveWorkingFile(String fileName, EObject modelRoot, TestFileAccessor fileAccessor, ResourceFactoryImpl resourceFactory)
			throws Exception {
		java.net.URI uri = fileAccessor.getWorkingFileURI(fileName);
		URI emfURI = fileAccessor.convertToEMFURI(uri);
		XMLResource resource = (XMLResource) resourceFactory.createResource(emfURI);
		resource.getContents().add(modelRoot);
		EcoreResourceUtil.readModelNamespace(resource);
		resource.save(null);
	}

	public void assertEquals(EObject eObject1, EObject eObject2) {
		EcoreEqualityAssert.assertEquals(eObject1, eObject2);
	}
}
