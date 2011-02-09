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
package org.eclipse.sphinx.tests.emf.integration.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.examples.hummingbird10.Component;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Factory;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.uml2.ide.metamodel.UML2MMDescriptor;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.tests.emf.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

@SuppressWarnings("nls")
public class EcoreResourceUtilTest extends DefaultIntegrationTestCase {
	private static final String SAMPLE_RESOURCE_NAME = "sampleResource.uml";
	private static final String SAMPLE_RESOURCE_URI_PLATFORM_META = "platform:/meta/" + Activator.getPlugin().getSymbolicName() + "/"
			+ SAMPLE_RESOURCE_NAME;

	List<String> hbProject10AResources10;
	int resources10FromHbProject10_A;

	List<String> hbProject20AResources20;
	int resources20FromHbProject20_A;

	List<String> hbProject20DResources20;
	int resources20FromHbProject20_D;

	List<String> hbProject20DResourcesUml2;
	int resourcesUml2FromHbProject20_D;

	org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedOutsideWorkspaceHb20Root = null;

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		hbProject10AResources10 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A, Hummingbird10MMDescriptor.INSTANCE);
		resources10FromHbProject10_A = hbProject10AResources10.size();

		hbProject20AResources20 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, Hummingbird20MMDescriptor.INSTANCE);
		resources20FromHbProject20_A = hbProject20AResources20.size();

		hbProject20DResources20 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D, Hummingbird20MMDescriptor.INSTANCE);
		resources20FromHbProject20_D = hbProject20DResources20.size();

		hbProject20DResourcesUml2 = refWks.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D, UML2MMDescriptor.INSTANCE);
		resourcesUml2FromHbProject20_D = hbProject20DResourcesUml2.size();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteExternalResource(SAMPLE_RESOURCE_URI_PLATFORM_META);

		// Unload outside workspace resources manually (only resources inside workspace will be unloaded automatically)
		if (retrievedOutsideWorkspaceHb20Root != null) {
			EcoreResourceUtil.unloadResource(retrievedOutsideWorkspaceHb20Root.eResource());
		}

		super.tearDown();
	}

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D,
				DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A };
	}

	/**
	 * Test method for {@link EcoreResourceUtil#exists(URI uri)} .
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void testExists() throws IOException, URISyntaxException {
		// Test for Resource URI
		assertTrue(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true)));
		assertTrue(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, true)));
		assertTrue(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, true)));
		assertTrue(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true)));
		assertTrue(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2, true)));
		assertTrue(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3, true)));
		assertFalse(EcoreResourceUtil.exists(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true)));

		// Given URI is FileURI
		assertTrue(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject20_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1)));
		assertTrue(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject20_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2)));
		assertTrue(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject20_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3)));
		assertTrue(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject10_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1)));
		assertTrue(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject10_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2)));
		assertTrue(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject10_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3)));
		assertFalse(EcoreResourceUtil.exists(URI.createFileURI(refWks.hbProject10_A.getLocation().toString() + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1)));

		// Given URI is Meta URI
		URI uri_meta = URI.createURI(SAMPLE_RESOURCE_URI_PLATFORM_META, true);
		Resource resource_meta = new XMIResourceImpl(uri_meta);
		String modelName_2 = "Uml2Model_2";
		Model uml2Model_2 = UMLFactory.eINSTANCE.createModel();
		uml2Model_2.setName(modelName_2);
		resource_meta.getContents().add(uml2Model_2);

		// check if content successfully added
		assertEquals(1, resource_meta.getContents().size());
		// But the resource is not exist
		assertFalse(EcoreResourceUtil.exists(uri_meta));
		// resource saved to uri
		try {
			resource_meta.save(Collections.emptyMap());
		} catch (IOException e) {
			fail("Error when save resource");
		}
		assertTrue(EcoreResourceUtil.exists(uri_meta));

		// Other kind
		java.net.URI existingResourceURI = getTestFileAccessor().getInputFileURI("hbFile20.instancemodel");
		URI emfExistingResourceUri = getTestFileAccessor().convertToEMFURI(existingResourceURI);
		assertTrue(existingResourceURI.toString() + " is not exist", EcoreResourceUtil.exists(emfExistingResourceUri));

		URI unexistingResourceURI = URI.createURI(Activator.getPlugin().getBundle().getLocation() + "dummy.txt");
		assertFalse(unexistingResourceURI.toString(), EcoreResourceUtil.exists(unexistingResourceURI));
		// Given URI is null
		assertFalse(EcoreResourceUtil.exists(null));
	}

	/**
	 * Test method for {@link EcoreResourceUtil#getContentTypeId(URI uri)} .
	 * 
	 * @throws Exception
	 */
	public void testGetContentTypeId() throws Exception {
		URI uri = null;
		String contentTypeId = "";
		File outsideWorkspaceFile;

		// Given URI is null
		{
			contentTypeId = EcoreResourceUtil.getContentTypeId(null);
			assertNull(contentTypeId);
		}

		// Given URI is URI of a container (project of folder)
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);
		}

		//
		// ==============================================================================================
		// Scheme-less URI

		// HB20
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/" + DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1,
					true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);

			uri = URI.createURI(refWks.hbProject20_A.getLocation().toString() + "/" + DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// HB10
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/" + DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1,
					true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird10MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);

			uri = URI.createURI(refWks.hbProject10_A.getLocation().toString() + "/" + DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird10MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// UML2
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1,
					true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(UML2MMDescriptor.XMI_BASE_CONTENT_TYPE_ID + "_2_1_0", contentTypeId);

			uri = URI.createURI(refWks.hbProject20_D.getLocation().toString() + "/" + DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(UML2MMDescriptor.XMI_BASE_CONTENT_TYPE_ID + "_2_1_0", contentTypeId);
		}
		// Given URI is URI of XML file
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + ".project", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.runtime.xml", contentTypeId);

			uri = URI.createURI(refWks.hbProject20_D.getLocation().toString() + "/" + ".project", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.runtime.xml", contentTypeId);
		}
		// Given URI is URI of preference file
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + ".settings" + "/"
					+ "org.eclipse.sphinx.examples.hummingbird.ide.prefs", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.resources.preferences", contentTypeId);

			uri = URI.createURI(refWks.hbProject20_D.getLocation().toString() + "/" + ".settings" + "/"
					+ "org.eclipse.sphinx.examples.hummingbird.ide.prefs", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.resources.preferences", contentTypeId);
		}
		// Given URI is URI of non-existing resource
		{
			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + "dummy.hummingbird", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createURI(refWks.hbProject20_D.getLocation().toString() + "/" + "dummy.hummingbird", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + "dummy.xml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createURI(refWks.hbProject20_D.getLocation().toString() + "/" + "dummy.xml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + "dummy.uml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createURI(refWks.hbProject20_D.getLocation().toString() + "/" + "dummy.uml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);
		}

		//
		// ==============================================================================================
		// Platform Resource URI

		// HB20
		{
			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
					+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// HB10
		{
			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
					+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird10MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// UML2
		{
			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
					+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(UML2MMDescriptor.XMI_BASE_CONTENT_TYPE_ID + "_2_1_0", contentTypeId);
		}
		// Given URI is URI of XML file
		{
			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + ".project", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.runtime.xml", contentTypeId);
		}
		// Given URI is URI of preference file
		{
			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + ".settings" + "/"
					+ "org.eclipse.sphinx.examples.hummingbird.ide.prefs", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.resources.preferences", contentTypeId);
		}
		// Given URI is URI of non-existing resource
		{
			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + "dummy.hummingbird", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + "dummy.xml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/" + "dummy.uml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);
		}

		//
		// ==============================================================================================
		// File URI

		// Hb20
		{
			uri = URI.createFileURI(refWks.hbProject20_A.getLocation().toString() + "/" + DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// HB10
		{
			uri = URI.createFileURI(refWks.hbProject10_A.getLocation().toString() + "/" + DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird10MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// UML2
		{
			uri = URI.createFileURI(refWks.hbProject20_D.getLocation().toString() + "/" + DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(UML2MMDescriptor.XMI_BASE_CONTENT_TYPE_ID + "_2_1_0", contentTypeId);
		}
		// Given URI is URI of XML file
		{
			uri = URI.createFileURI(refWks.hbProject20_D.getLocation().toString() + "/" + ".project");
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.runtime.xml", contentTypeId);
		}
		// Given URI is URI of preference file
		{
			uri = URI.createFileURI(refWks.hbProject20_D.getLocation().toString() + "/" + ".settings" + "/"
					+ "org.eclipse.sphinx.examples.hummingbird.ide.prefs");
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals("org.eclipse.core.resources.preferences", contentTypeId);
		}
		// Given URI is URI of existing file outside workspace
		{
			outsideWorkspaceFile = getTestFileAccessor().createWorkingCopyOfInputFile("hbFile20.instancemodel");
			assertTrue(outsideWorkspaceFile.exists());

			uri = URI.createFileURI(outsideWorkspaceFile.getAbsolutePath());
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// Given URI s URI of non-existing resource
		{
			uri = URI.createFileURI(refWks.hbProject20_D.getLocation().toString() + "/" + "dummy.hummingbird");
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createFileURI(refWks.hbProject20_D.getLocation().toString() + "/" + "dummy.xml");
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createFileURI(refWks.hbProject20_D.getLocation().toString() + "/" + "dummy.uml");
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			String outsideWorkspacePath = outsideWorkspaceFile.getAbsolutePath().replace("hbFile20.instancemodel", "dummy.hummingbird");
			assertFalse(new File(outsideWorkspacePath).exists());

			uri = URI.createFileURI(outsideWorkspacePath);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);
		}

		//
		// ==============================================================================================
		// Platform Plug-in URI

		// HB20
		{
			uri = URI.createPlatformPluginURI("/" + Activator.getPlugin().getSymbolicName() + "/" + "resources/input/hbFile20.instancemodel", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNotNull(contentTypeId);
			assertEquals(Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), contentTypeId);
		}
		// Given URI is URI of non-existing resource
		{
			uri = URI.createPlatformPluginURI("/" + Activator.getPlugin().getSymbolicName() + "/" + "dummy.hummingbird", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createPlatformPluginURI("/" + Activator.getPlugin().getSymbolicName() + "/" + "dummy.xml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);

			uri = URI.createPlatformPluginURI("/" + Activator.getPlugin().getSymbolicName() + "/" + "dummy.uml", true);
			contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
			assertNull(contentTypeId);
		}
	}

	/**
	 * Test method for {@link EcoreResourceUtil#loadModelFragment(ResourceSet, URI)}
	 */
	public void testLoadModelFragment() throws Exception {
		Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
		assertNotNull(resource10);
		EObject root = resource10.getContents().get(0);
		assertNotNull(root);
		assertTrue(root instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application application = (org.eclipse.sphinx.examples.hummingbird10.Application) root;
		assertFalse(application.getComponents().isEmpty());
		Component expectedComponent = application.getComponents().get(0);
		final String expectedComponentName = expectedComponent.getName();

		// URI uri = expectedArPackage;
		URI uri = EcoreUtil.getURI(expectedComponent);

		// 1 loadModelFragment must return previously retrieved
		EObject objectRetrieved = EcoreResourceUtil.loadModelFragment(refWks.editingDomain10.getResourceSet(), uri);
		assertNotNull(objectRetrieved);
		assertTrue(objectRetrieved instanceof Component);
		Component retrievedComponent = (Component) objectRetrieved;
		assertEquals(expectedComponent, retrievedComponent);

		// ===============================================
		// unload resource in order to check if the method loadModelFragmentcorrectly load the resource
		// Verify that test resource is Loaded in editing domain
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		// Unload rest resource
		EcoreResourceUtil.unloadResource(resource10);
		// Verify that test resource was unloaded
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		// Retrieved object in unloaded resource via uri
		objectRetrieved = EcoreResourceUtil.loadModelFragment(refWks.editingDomain10.getResourceSet(), uri);

		// Verify that test resource is re-loaded in editing domain
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		assertNotNull(objectRetrieved);
		assertTrue(objectRetrieved instanceof Component);
		retrievedComponent = (Component) objectRetrieved;
		assertEquals(expectedComponentName, retrievedComponent.getName());

		// 2 loading an uri pointing to an non existing resource must return null
		uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/" + "unexistingFile.unknow", true);
		assertNull(EcoreResourceUtil.loadModelFragment(refWks.editingDomain10.getResourceSet(), uri));

	}

	/**
	 * Test method for {@link EcoreResourceUtil#loadModelRoot(ResourceSet, URI, java.util.Map)} with default loading
	 * options.
	 */
	public void testLoadModelRootWithDefaultOptions() throws Exception {
		int resourcesInEditingDomain20 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird20MMDescriptor.INSTANCE);
		int resourcesInEditingDomain10 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird10MMDescriptor.INSTANCE);
		int resourcesInEditingDomainUml2 = refWks.getInitialResourcesInReferenceEditingDomainCount(UML2MMDescriptor.INSTANCE);

		// HB20 Resource
		{
			URI uri20 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
					+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true);

			Resource resource20 = refWks.editingDomain20.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
							+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);

			assertNotNull(resource20);
			assertFalse(resource20.getContents().isEmpty());
			EObject object = resource20.getContents().get(0);
			assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application);
			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application expectedApplication = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) object;
			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
					.loadModelRoot(refWks.editingDomain20.getResourceSet(), uri20, EcoreResourceUtil.getDefaultLoadOptions());

			assertSame(expectedApplication, retrievedApplication);
			// Unload project
			ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			resourcesInEditingDomain20 = resourcesInEditingDomain20 - resources20FromHbProject20_A;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);

			EcoreResourceUtil.loadModelRoot(refWks.editingDomain20.getResourceSet(), uri20, EcoreResourceUtil.getDefaultLoadOptions());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
			resourcesInEditingDomain20++;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		}
		// HB10 Resource
		{
			URI uri10 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
					+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true);

			Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
							+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
			assertNotNull(resource10);
			assertFalse(resource10.getContents().isEmpty());
			EObject object = resource10.getContents().get(0);
			assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application expectedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) object;
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) EcoreResourceUtil
					.loadModelRoot(refWks.editingDomain10.getResourceSet(), uri10, EcoreResourceUtil.getDefaultLoadOptions());

			assertSame(expectedApplication, retrievedApplication);
			// Unload Project
			ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
			resourcesInEditingDomain10 = resourcesInEditingDomain10 - resources10FromHbProject10_A;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourcesInEditingDomain10);

			EcoreResourceUtil.loadModelRoot(refWks.editingDomain10.getResourceSet(), uri10, EcoreResourceUtil.getDefaultLoadOptions());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
			resourcesInEditingDomain10++;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourcesInEditingDomain10);
		}
		// Uml2 Resource
		{
			URI uriUml2 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
					+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true);

			Resource resourceUml2 = refWks.editingDomainUml2.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
							+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, false), false);
			assertNotNull(resourceUml2);
			assertFalse(resourceUml2.getContents().isEmpty());
			EObject object21 = resourceUml2.getContents().get(0);
			assertTrue(object21 instanceof Model);
			Model expectedUml2ModelRoot = (Model) object21;
			Model retrievedUml2ModelRoot = (Model) EcoreResourceUtil.loadModelRoot(refWks.editingDomainUml2.getResourceSet(), uriUml2,
					EcoreResourceUtil.getDefaultLoadOptions());

			assertSame(expectedUml2ModelRoot, retrievedUml2ModelRoot);

			ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject20_D, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
			resourcesInEditingDomainUml2 = resourcesInEditingDomainUml2 - resourcesUml2FromHbProject20_D;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourcesInEditingDomainUml2);

			EcoreResourceUtil.loadModelRoot(refWks.editingDomainUml2.getResourceSet(), uriUml2, EcoreResourceUtil.getDefaultLoadOptions());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			resourcesInEditingDomainUml2++;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourcesInEditingDomainUml2);
		}
	}

	/**
	 * Test method for {@link EcoreResourceUtil#loadModelRoot(ResourceSet, URI, java.util.Map)} with a file URI pointing
	 * to a non existing file
	 */
	public void testLoadModelRootWithNonExistingFileUri() throws Exception {
		URI uri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1 + ".txt", true);
		org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
				.loadModelRoot(refWks.editingDomain20.getResourceSet(), uri, null);
		assertNull(retrievedApplication);
	}

	/**
	 * Test method for {@link EcoreResourceUtil#loadModelRoot(ResourceSet, URI, java.util.Map)} with null loading
	 * options.
	 */
	public void testLoadModelRootWithNullOptions() throws Exception {
		Integer resourcesInEditingDomain20 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird20MMDescriptor.INSTANCE);
		Integer resourcesInEditingDomain10 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird10MMDescriptor.INSTANCE);
		Integer resourcesInEditingDomainUml2 = refWks.getInitialResourcesInReferenceEditingDomainCount(UML2MMDescriptor.INSTANCE);

		URI uri_20A_1 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true);
		Resource resource20 = refWks.editingDomain20.getResourceSet().getResource(uri_20A_1, false);
		assertNotNull(resource20);
		assertFalse(resource20.getContents().isEmpty());
		EObject object = resource20.getContents().get(0);
		assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application);
		org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application expectedApplication_20A_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) object;

		org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication_20A_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
				.loadModelRoot(refWks.editingDomain20.getResourceSet(), uri_20A_1, null);

		assertSame(expectedApplication_20A_1, retrievedApplication_20A_1);

		// Unload project
		ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		resourcesInEditingDomain20 = resourcesInEditingDomain20 - resources20FromHbProject20_A;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);

		retrievedApplication_20A_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil.loadModelRoot(
				refWks.editingDomain20.getResourceSet(), uri_20A_1, null);
		assertNotNull(retrievedApplication_20A_1);
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		resourcesInEditingDomain20++;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		assertEquals(1, retrievedApplication_20A_1.getComponents().size());
		// ======================================

		URI uri_10A_1 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true);
		Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(uri_10A_1, false);
		assertNotNull(resource10);
		assertFalse(resource10.getContents().isEmpty());
		object = resource10.getContents().get(0);
		assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application expectedApplication_10A_1 = (org.eclipse.sphinx.examples.hummingbird10.Application) object;
		org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication_10A_1 = (org.eclipse.sphinx.examples.hummingbird10.Application) EcoreResourceUtil
				.loadModelRoot(refWks.editingDomain10.getResourceSet(), uri_10A_1, null);

		assertSame(expectedApplication_10A_1, retrievedApplication_10A_1);

		ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
		resourcesInEditingDomain10 = resourcesInEditingDomain10 - resources10FromHbProject10_A;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, 0);

		retrievedApplication_10A_1 = (org.eclipse.sphinx.examples.hummingbird10.Application) EcoreResourceUtil.loadModelRoot(
				refWks.editingDomain10.getResourceSet(), uri_10A_1, null);
		assertNotNull(retrievedApplication_10A_1);
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
		resourcesInEditingDomain10++;
		assertEquals(1, refWks.editingDomain10.getResourceSet().getResources().size());
		assertEquals(1, retrievedApplication_10A_1.getComponents().size());
		assertEquals(1, retrievedApplication_10A_1.getInterfaces().size());
		// ==================================
		URI uriUml2 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
				+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true);
		Resource resourceUml2 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, false), false);
		assertNotNull(resourceUml2);
		assertFalse(resourceUml2.getContents().isEmpty());
		EObject objectUml2 = resourceUml2.getContents().get(0);
		assertTrue(objectUml2 instanceof Model);
		Model expectedUml2ModelRoot = (Model) objectUml2;
		Model retrievedUml2ModelRoot = (Model) EcoreResourceUtil.loadModelRoot(refWks.editingDomainUml2.getResourceSet(), uriUml2, null);

		assertSame(expectedUml2ModelRoot, retrievedUml2ModelRoot);

		ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject20_D, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		resourcesInEditingDomainUml2 = resourcesInEditingDomainUml2 - resourcesUml2FromHbProject20_D;
		resourcesInEditingDomain20 = resourcesInEditingDomain20 - resources20FromHbProject20_D;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourcesInEditingDomainUml2);

		retrievedUml2ModelRoot = (Model) EcoreResourceUtil.loadModelRoot(refWks.editingDomainUml2.getResourceSet(), uriUml2, null);
		assertNotNull(retrievedUml2ModelRoot);
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
		resourcesInEditingDomainUml2++;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourcesInEditingDomainUml2);
		assertEquals(2, retrievedUml2ModelRoot.getPackagedElements().size());

		// External
		// Given Uri is NULL
		{
			// TODO NULLPointerException
			// assertNull(EcoreResourceUtil.loadModelRoot(refWks.editingDomain20.getResourceSet(), null, null));
		}

		// Given ResourceSet is NULL

		// resource20_A_1 is loaded
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);

		retrievedApplication_20A_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil.loadModelRoot(null,
				uri_20A_1, null);
		assertNotNull(retrievedApplication_20A_1);

		assertEquals(uri_20A_1, retrievedApplication_20A_1.eResource().getURI());
		assertEquals(1, retrievedApplication_20A_1.getComponents().size());

		// Given ResourceSet and URI is not the same MetaModelDescriptor

		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);

		retrievedApplication_20A_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil.loadModelRoot(null,
				uri_20A_1, null);
		assertNotNull(retrievedApplication_20A_1);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);

		// Verify that the retrieved Model Root is Contents of resource 20A_1
		assertEquals(uri_20A_1, retrievedApplication_20A_1.eResource().getURI());
		assertEquals(1, retrievedApplication_20A_1.getComponents().size());

		// Resource is Unloaded

		// Only resource20_A_1 is loaded
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		URI uri_20A_2 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, true);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2);

		object = EcoreResourceUtil.loadModelRoot(refWks.editingDomain20.getResourceSet(), uri_20A_2, null);
		assertNotNull(object);

		// resource20_A_2 is loaded
		resourcesInEditingDomain20++;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform retrievedPlatform = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) object;
		assertNotNull(retrievedPlatform);
		// Verify that the retrieved Model Root is Contents of resource 20A_2
		assertEquals(uri_20A_2, retrievedPlatform.eResource().getURI());
		assertEquals(2, retrievedPlatform.getComponentTypes().size());

		// Resource is Unloaded and Given ResourceSet is null

		// Only resource20_A_1, resource20_A_2 are loaded
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		URI uri_20A_3 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, true);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3);

		object = EcoreResourceUtil.loadModelRoot(null, uri_20A_3, null);
		assertNotNull(object);

		org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication_20_3 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) object;
		assertNotNull(retrievedApplication_20_3);

		// resource20_A_3 is not loaded to editingDomain20. It was loaded in a new resourceSet
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		assertTrue(retrievedApplication_20_3.eResource().isLoaded());

		// Verify that the retrieved Model Root is Contents of resource 3xA_3
		assertEquals(uri_20A_3, retrievedApplication_20_3.eResource().getURI());
		assertEquals(2, retrievedApplication_20_3.getComponents().size());

	}

	/**
	 * Test method for {@link EcoreResourceUtil#getModelRoot(Resource)} .
	 */
	public void testGetModelRootFromResource() {
		// HB20 Resource
		{
			Resource resource20 = refWks.editingDomain20.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
							+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);
			assertNotNull(resource20);
			assertFalse(resource20.getContents().isEmpty());
			EObject object = resource20.getContents().get(0);
			assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application);
			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application expectedApplication = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) object;
			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
					.getModelRoot(resource20);
			assertSame(expectedApplication, retrievedApplication);
		}
		// =========================================
		// HB10 Resource
		{
			Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
							+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
			assertNotNull(resource10);
			assertFalse(resource10.getContents().isEmpty());
			EObject object21 = resource10.getContents().get(0);
			assertTrue(object21 instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application expectedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) object21;
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) EcoreResourceUtil
					.getModelRoot(resource10);
			assertSame(expectedApplication, retrievedApplication);
		}
		// =========================================
		// Uml2 Resource
		{
			Resource resourceUml2 = refWks.editingDomainUml2.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
							+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, false), false);
			assertNotNull(resourceUml2);
			assertFalse(resourceUml2.getContents().isEmpty());
			EObject objectUml2 = resourceUml2.getContents().get(0);
			assertTrue(objectUml2 instanceof Model);
			Model expectedModelRoot = (Model) objectUml2;
			Model retrievedModelRoot = (Model) EcoreResourceUtil.getModelRoot(resourceUml2);
			assertSame(expectedModelRoot, retrievedModelRoot);
		}
		// =========================================
		// Null Resource
		{
			Resource nullResource = null;
			try {
				assertNull(EcoreResourceUtil.getModelRoot(nullResource));
			} catch (Exception ex) {
				fail("Exception while input resource is NULL: " + ex.getCause() + " " + ex.getLocalizedMessage());

			}

		}
		// =========================================
		// Unloaded resource

		{
			int resourceInEditingDomain10 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird10MMDescriptor.INSTANCE);
			Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(
					URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
							+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
			assertNotNull(resource10);
			assertFalse(resource10.getContents().isEmpty());
			// Unload resource
			synchronizedUnloadProject(refWks.hbProject10_A, false);
			resourceInEditingDomain10 = resourceInEditingDomain10 - resources10FromHbProject10_A;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);

			assertNotNull(resource10);
			assertFalse(resource10.isLoaded());
			assertTrue(resource10.getContents().isEmpty());
			assertNull(EcoreResourceUtil.getModelRoot(resource10));
		}
	}

	/**
	 * Test method for {@link EcoreResourceUtil#getModelRoot(ResourceSet, URI)} .
	 */
	public void testGetModelRootFromURI() {
		URI uri20 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true);
		URI uri10 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true);
		URI uriUml2 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
				+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true);

		// HB20 Resource
		{
			Resource resource20 = refWks.editingDomain20.getResourceSet().getResource(uri20, false);
			assertNotNull(resource20);
			assertFalse(resource20.getContents().isEmpty());
			EObject object = resource20.getContents().get(0);
			assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application);
			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application expectedApplicationt = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) object;
			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
					.getModelRoot(refWks.editingDomain20.getResourceSet(), uri20);
			assertSame(expectedApplicationt, retrievedApplication);
		}
		// =========================================
		// HB120 Resource
		{
			Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(uri10, false);
			assertNotNull(resource10);
			assertFalse(resource10.getContents().isEmpty());
			EObject object = resource10.getContents().get(0);
			assertTrue(object instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application expectedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) object;
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplicationt = (org.eclipse.sphinx.examples.hummingbird10.Application) EcoreResourceUtil
					.getModelRoot(refWks.editingDomain10.getResourceSet(), uri10);
			assertSame(expectedApplication, retrievedApplicationt);
		}
		// =========================================
		// Uml2 Resource
		{
			Resource resourceUml2 = refWks.editingDomainUml2.getResourceSet().getResource(uriUml2, false);
			assertNotNull(resourceUml2);
			assertFalse(resourceUml2.getContents().isEmpty());
			EObject objectUml2 = resourceUml2.getContents().get(0);
			assertTrue(objectUml2 instanceof Model);
			Model expectedModelRoot = (Model) objectUml2;
			Model retrievedModelRoot = (Model) EcoreResourceUtil.getModelRoot(refWks.editingDomainUml2.getResourceSet(), uriUml2);
			assertSame(expectedModelRoot, retrievedModelRoot);
		}

		// =========================================
		// Given ResourceSet is NULL
		assertNull(EcoreResourceUtil.getModelRoot(null, uri20));
		// =========================================
		// Resource's uri does not exist in the given resourceSet
		{
			assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomain20.getResourceSet(), uri10));
			assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomainUml2.getResourceSet(), uri10));
			assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomain20.getResourceSet(), uriUml2));
			// Unload resource10
			synchronizedUnloadAllProjects();
			assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomain10.getResourceSet(), uri10));
			assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomain20.getResourceSet(), uri20));
			assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomainUml2.getResourceSet(), uriUml2));

		}
		// =========================================
		// Given Uri is NULL
		{
			// URI nullUri = null;
			// FIXME NULL POINTER EXCEPTION
			// try {
			// assertNull(EcoreResourceUtil.getModelRoot(refWks.editingDomain10.getResourceSet(), nullUri));
			// } catch (Exception ex) {
			// fail("Exception while input resource is NULL: " + ex.getCause() + " " + ex.getLocalizedMessage());
			// }
		}
	}

	/**
	 * Test method for {@link EcoreResourceUtil#isResourceLoaded(ResourceSet resourceSet,URI modelURI)} .
	 * 
	 * @throws Exception
	 */
	public void testIsResourceLoaded() throws Exception {
		int resourcesInEditingDomain20 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird20MMDescriptor.INSTANCE);
		int resourcesInEditingDomain10 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird10MMDescriptor.INSTANCE);
		int resourcesInEditingDomainUml2 = refWks.getInitialResourcesInReferenceEditingDomainCount(UML2MMDescriptor.INSTANCE);

		URI uri20 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true);

		URI uri10 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true);

		URI uriUml2 = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
				+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true);

		assertTrue(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain20.getResourceSet(), uri20));
		assertTrue(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain10.getResourceSet(), uri10));
		assertTrue(EcoreResourceUtil.isResourceLoaded(refWks.editingDomainUml2.getResourceSet(), uriUml2));
		// Unload project20_A
		ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		resourcesInEditingDomain20 -= resources20FromHbProject20_A;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourcesInEditingDomainUml2);

		assertFalse(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain20.getResourceSet(), uri20));
		assertTrue(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain10.getResourceSet(), uri10));
		assertTrue(EcoreResourceUtil.isResourceLoaded(refWks.editingDomainUml2.getResourceSet(), uriUml2));

		// Unload project10_A
		ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		resourcesInEditingDomain10 -= resources10FromHbProject10_A;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourcesInEditingDomain10);
		assertFalse(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain20.getResourceSet(), uri20));
		assertFalse(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain10.getResourceSet(), uri10));
		assertTrue(EcoreResourceUtil.isResourceLoaded(refWks.editingDomainUml2.getResourceSet(), uriUml2));

		// Unload project20_D
		ModelLoadManager.INSTANCE.unloadProject(refWks.hbProject20_D, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		resourcesInEditingDomain20 -= resources20FromHbProject20_D;
		resourcesInEditingDomainUml2 -= resourcesUml2FromHbProject20_D;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourcesInEditingDomain10);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourcesInEditingDomain20);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourcesInEditingDomainUml2);
		assertFalse(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain20.getResourceSet(), uri20));
		assertFalse(EcoreResourceUtil.isResourceLoaded(refWks.editingDomain10.getResourceSet(), uri10));
		assertFalse(EcoreResourceUtil.isResourceLoaded(refWks.editingDomainUml2.getResourceSet(), uriUml2));
	}

	/**
	 * Test method for {@link EcoreResourceUtil#unloadResource(Resource)}
	 * 
	 * @throws Exception
	 */
	public void testUnloadResource() throws Exception {
		int resourceInEditingDomain20 = refWks.editingDomain20.getResourceSet().getResources().size();
		int resourceInEditingDomain10 = refWks.editingDomain10.getResourceSet().getResources().size();
		int resourceInEditingDomainUml2 = refWks.editingDomainUml2.getResourceSet().getResources().size();

		// HB20 Resource

		Resource resource20_A_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true), false);
		Resource resource20_A_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, true), false);
		Resource resource20_A_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, true), false);
		assertNotNull(resource20_A_1);
		assertNotNull(resource20_A_2);
		assertNotNull(resource20_A_3);

		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2);
		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3);

		EcoreResourceUtil.unloadResource(resource20_A_1);
		resourceInEditingDomain20--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2);
		assertEditingDomainContainsResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3);

		EcoreResourceUtil.unloadResource(resource20_A_2);
		resourceInEditingDomain20--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, resource20_A_2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, resource20_A_2);
		assertEditingDomainContainsResource(refWks.editingDomain20, resource20_A_3);

		EcoreResourceUtil.unloadResource(resource20_A_3);
		resourceInEditingDomain20--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain20, DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);

		// Hb10 Resource

		Resource resource10_A_1 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
		Resource resource10_A_2 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2, false), false);
		Resource resource10_A_3 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3, false), false);
		assertNotNull(resource10_A_1);
		assertNotNull(resource10_A_2);
		assertNotNull(resource10_A_3);

		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2);
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3);

		EcoreResourceUtil.unloadResource(resource10_A_1);
		resourceInEditingDomain10--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2);
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3);

		EcoreResourceUtil.unloadResource(resource10_A_2);
		resourceInEditingDomain10--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2);
		assertEditingDomainContainsResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3);

		EcoreResourceUtil.unloadResource(resource10_A_3);
		resourceInEditingDomain10--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomain10, DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3);

		// Uml2 Resource

		Resource resourceUml2_1 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, false), false);
		Resource resourceUml2_2 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_2, false), false);
		Resource resourceUml2_3 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_3, false), false);
		assertNotNull(resourceUml2_1);
		assertNotNull(resourceUml2_2);
		assertNotNull(resourceUml2_3);

		assertEditingDomainContainsResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1);
		assertEditingDomainContainsResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_2);
		assertEditingDomainContainsResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_3);

		EcoreResourceUtil.unloadResource(resourceUml2_1);
		resourceInEditingDomainUml2--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourceInEditingDomainUml2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1);
		assertEditingDomainContainsResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_2);
		assertEditingDomainContainsResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_3);

		EcoreResourceUtil.unloadResource(resourceUml2_2);
		resourceInEditingDomainUml2--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourceInEditingDomainUml2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1);
		assertEditingDomainDoesNotContainResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_2);
		assertEditingDomainContainsResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_3);

		EcoreResourceUtil.unloadResource(resourceUml2_3);
		resourceInEditingDomainUml2--;
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourceInEditingDomainUml2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1);
		assertEditingDomainDoesNotContainResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_2);
		assertEditingDomainDoesNotContainResource(refWks.editingDomainUml2, DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_3);
		// ============================================
		// Resource was unloaded
		assertNotNull(resourceUml2_3);
		EcoreResourceUtil.unloadResource(resourceUml2_3);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourceInEditingDomainUml2);
		assertNotNull(resource10_A_3);
		EcoreResourceUtil.unloadResource(resource10_A_3);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
		// ============================================
		// Given Resource was null

		try {
			EcoreResourceUtil.unloadResource(null);
		} catch (Exception ex) {
			fail("Exception while input resource is NULL: " + ex.getCause() + " " + ex.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link EcoreResourceUtil#unloadResource(Resource, boolean)} with second input is TRUE
	 */
	// TODO not yet implemented
	public void testUnloadResourceWithMemoryOptimized() {

	}

	/**
	 * Test method for {@link EcoreResourceUtil#readTargetNamespace(URIConverter, URI)} .
	 */
	public void testReadTargetNamespaceFromModelUri() {
		// Read target NameSpace from HB20 xsd file
		String expectedTargetNamespace_hb20InstanceModel = "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel";
		String expectedTargetNamespace_hb20TypeModel = "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel";
		// String expectedTargetHBNameSpace = "http://www.omg.org/XMI";
		// =========================================================
		URL schemaURLHb20InstanceModel = FileLocator.find(org.eclipse.sphinx.examples.hummingbird20.Activator.getPlugin().getBundle(), new Path(
				"model/InstanceModel20XMI.xsd"), null); //$NON-NLS-1$
		URI testUri = URI.createURI(schemaURLHb20InstanceModel.toString(), true);
		assertEquals(expectedTargetNamespace_hb20InstanceModel, EcoreResourceUtil.readTargetNamespace(null, testUri));

		URL schemaURLHb20TypeModel = FileLocator.find(org.eclipse.sphinx.examples.hummingbird20.Activator.getPlugin().getBundle(), new Path(
				"model/TypeModel20XMI.xsd"), null); //$NON-NLS-1$
		testUri = URI.createURI(schemaURLHb20TypeModel.toString(), true);
		assertEquals(expectedTargetNamespace_hb20TypeModel, EcoreResourceUtil.readTargetNamespace(null, testUri));

		// =====================================================
		// Read Target NameSpace from HB10 Resource and HB20 Resource
		Resource resource20_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true), false);
		assertNotNull(resource20_1);

		Resource resource20_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, true), false);
		assertNotNull(resource20_2);

		Resource resource10 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true), false);
		assertNotNull(resource10);
		// TODO the local part of QName is XMI instead of xmi-> return not null
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain20.getResourceSet()),
				resource20_1.getURI()));
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain20.getResourceSet()),
				resource20_2.getURI()));
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain10.getResourceSet()),
				resource10.getURI()));
		// =====================================================
		// Read Target Namespace form UML resource
		Resource resourceUml2 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true), false);
		assertNotNull(resourceUml2);
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomainUml2.getResourceSet()),
				resourceUml2.getURI()));

		// =====================================================
		// Read Target NameSpace from Non XML Resource- Result: Null
		IFile nonXMLFile = refWks.hbProject20_A.getFile(".project");
		assertNull(EcoreResourceUtil.readTargetNamespace(null, URI.createURI(nonXMLFile.getLocation().toString(), true)));
		// =====================================================
		// Given Resource's uri does not belong to the given resourceSet
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain20.getResourceSet()),
				resourceUml2.getURI()));

		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain20.getResourceSet()),
				resource10.getURI()));
		// =====================================================
		// ResourceSet is NULL
		assertNull(EcoreResourceUtil.readTargetNamespace(null, resource10.getURI()));
		// =====================================================
		// URI is NULL
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain20.getResourceSet()), null));
		// =====================================================
		// Given URI is invalid. No resource with this uri is exist
		URI unexistUri = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20D_1, true);
		assertNull(EcoreResourceUtil.readTargetNamespace(EcoreResourceUtil.getURIConverter(refWks.editingDomain20.getResourceSet()), unexistUri));

	}

	/**
	 * Test method for {@link EcoreResourceUtil#convertToPlatformResourceURI(URI)}
	 */
	public void testConvertToPlatformResourceURI() throws Exception {

		// =====================================================
		// The given URI references a location inside the workspace
		String projectsDir = refWks.hbProject10_A.getLocation().removeLastSegments(1).toString();
		URI resourceInsideWrkURI = URI.createFileURI(projectsDir + "/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_B + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10B_1);
		URI expectedPlatformResourceURI = URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_B + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10B_1, true);
		assertEquals(expectedPlatformResourceURI, EcoreResourceUtil.convertToPlatformResourceURI(resourceInsideWrkURI));
		// =====================================================
		// The given URI references to a location outside the workspace
		File file = File.createTempFile("test", null);
		String outsideWorkspaceLocation = file.getAbsolutePath();
		URI outsideWorkspaceURI = URI.createFileURI(outsideWorkspaceLocation);
		assertEquals(outsideWorkspaceURI, EcoreResourceUtil.convertToPlatformResourceURI(outsideWorkspaceURI));
	}

	/**
	 * Test method for {@link EcoreResourceUtil#isReadOnly(URI)}
	 */
	public void testIsReadOnly() {
		// =====================================================
		// URI pointing to platform:/plugin resource
		String pluginId = Activator.getPlugin().getSymbolicName();
		String path = "/" + "plugin.xml";
		assertTrue(EcoreResourceUtil.isReadOnly(URI.createPlatformPluginURI(pluginId + path, true)));
		// =====================================================
		// URI pointing to platform:/resource resource
		assertFalse(EcoreResourceUtil.isReadOnly(URI.createPlatformResourceURI("/" + DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
				+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true)));

	}

	/**
	 * Test method for {@link EcoreResourceUtil#saveModelResource(Resource, java.util.Map)}
	 */
	public void testSaveModelResource() throws Exception {
		final String changedObjectName = "changedObjectName";
		// ==============================================
		// HB10 Resource
		// Modify an existing resources
		// Changed Resource to save
		Resource testResource10_1 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true), false);
		Resource testResource10_2 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2, true), false);

		// Changed Resource but not be saved
		Resource testResource10_3 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3, true), false);
		assertNotNull(testResource10_1);
		assertNotNull(testResource10_2);
		assertNotNull(testResource10_3);

		assertFalse(testResource10_1.getContents().isEmpty());
		assertTrue(testResource10_1.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application hb10Application_1 = (org.eclipse.sphinx.examples.hummingbird10.Application) testResource10_1
				.getContents().get(0);
		assertFalse(hb10Application_1.getComponents().isEmpty());
		final Component testComponent10_1 = hb10Application_1.getComponents().get(0);

		assertFalse(testResource10_2.getContents().isEmpty());
		assertTrue(testResource10_2.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application hb10Application_2 = (org.eclipse.sphinx.examples.hummingbird10.Application) testResource10_2
				.getContents().get(0);
		assertFalse(hb10Application_2.getComponents().isEmpty());
		final Component testComponent10_2 = hb10Application_2.getComponents().get(0);

		assertFalse(testResource10_3.getContents().isEmpty());
		assertTrue(testResource10_3.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application hb10Application_3 = (org.eclipse.sphinx.examples.hummingbird10.Application) testResource10_3
				.getContents().get(0);
		assertFalse(hb10Application_3.getComponents().isEmpty());
		final Component testComponent10_3 = hb10Application_3.getComponents().get(0);

		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
				public void run() {
					testComponent10_1.setName(changedObjectName);
					testComponent10_2.setName(changedObjectName);
					testComponent10_3.setName(changedObjectName);

				}
			}, "Modify resources");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		assertEquals(changedObjectName, testComponent10_1.getName());
		assertEquals(changedObjectName, testComponent10_2.getName());
		assertEquals(changedObjectName, testComponent10_3.getName());

		EcoreResourceUtil.saveModelResource(testResource10_1, Collections.emptyMap());
		EcoreResourceUtil.saveModelResource(testResource10_2, Collections.emptyMap());
		// Reload project
		ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		// Verify saved resource set
		Resource retrievedSavedResource10_1 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, true), false);
		Resource retrievedSavedResource10_2 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2, true), false);
		Resource retrievedUnSavedResource10_3 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3, true), false);

		assertNotNull(retrievedSavedResource10_1);
		assertNotNull(retrievedSavedResource10_2);
		assertNotNull(retrievedUnSavedResource10_3);
		// Verify the changed resource to save
		String message1 = "Resource \"{0}\" was not saved";
		String message2 = "Resource \"{0}\" was saved while save another resource";

		assertEquals(1, retrievedSavedResource10_1.getContents().size());
		assertTrue(retrievedSavedResource10_1.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application retrievedSavedApplication10_1 = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedSavedResource10_1
				.getContents().get(0);
		assertEquals(NLS.bind(message1, retrievedSavedResource10_1.getURI().toString()), changedObjectName, retrievedSavedApplication10_1
				.getComponents().get(0).getName());

		// Verify the changed resource to save
		assertEquals(1, retrievedSavedResource10_2.getContents().size());
		assertTrue(retrievedSavedResource10_2.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application retrievedSavedApplication10_2 = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedSavedResource10_2
				.getContents().get(0);
		assertEquals(NLS.bind(message1, retrievedSavedResource10_2.getURI().toString()), changedObjectName, retrievedSavedApplication10_2
				.getComponents().get(0).getName());

		// Verify the changed resource but not to be saved
		assertEquals(1, retrievedUnSavedResource10_3.getContents().size());
		assertTrue(retrievedUnSavedResource10_3.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
		org.eclipse.sphinx.examples.hummingbird10.Application retrievedUnSavedApplication10_3 = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedUnSavedResource10_3
				.getContents().get(0);
		assertFalse(NLS.bind(message2, retrievedSavedResource10_1.getURI().toString()),
				changedObjectName.equals(retrievedUnSavedApplication10_3.getComponents().get(0).getName()));
		// ===========================================================
		// HB20 resource with Null Options
		Resource testResource20_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true), false);
		assertNotNull(testResource20_1);

		assertFalse(testResource20_1.getContents().isEmpty());
		assertTrue(testResource20_1.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application);
		org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application hb20Application_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) testResource20_1
				.getContents().get(0);
		assertFalse(hb20Application_1.getComponents().isEmpty());
		final org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component testComponent20_1 = hb20Application_1.getComponents().get(0);

		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, new Runnable() {
				public void run() {
					testComponent20_1.setName(changedObjectName);

				}
			}, "Modify resources");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		assertEquals(changedObjectName, testComponent20_1.getName());

		EcoreResourceUtil.saveModelResource(testResource20_1, null);
		// Reload project
		ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		// Verify saved resource set
		Resource retrievedSavedResource20_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, true), false);

		assertNotNull(retrievedSavedResource20_1);
		// Verify the changed resource to save

		assertEquals(1, retrievedSavedResource20_1.getContents().size());
		assertTrue(retrievedSavedResource20_1.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application);
		org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedSavedApplication20_1 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) retrievedSavedResource20_1
				.getContents().get(0);
		assertEquals(NLS.bind(message1, retrievedSavedResource20_1.getURI().toString()), changedObjectName, retrievedSavedApplication20_1
				.getComponents().get(0).getName());
		// ===========================================================
		// Uml2 resource

		Resource testResourceUml2_1 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true), false);

		assertNotNull(testResourceUml2_1);

		assertFalse(testResourceUml2_1.getContents().isEmpty());
		assertTrue(testResourceUml2_1.getContents().get(0) instanceof Model);
		Model model = (Model) testResourceUml2_1.getContents().get(0);
		assertFalse(model.getPackagedElements().isEmpty());
		final PackageableElement testPackageElement = model.getPackagedElements().get(0);

		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomainUml2, new Runnable() {
				public void run() {
					testPackageElement.setName(changedObjectName);

				}
			}, "Modify resources");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		EcoreResourceUtil.saveModelResource(testResource20_1, Collections.emptyMap());
		assertEquals(changedObjectName, testPackageElement.getName());
		// Reload project
		ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		// Verify saved resource set
		Resource retrievedSavedResourceUml2_1 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1, true), false);

		assertNotNull(retrievedSavedResourceUml2_1);
		// Verify the changed resource to save

		assertEquals(1, retrievedSavedResourceUml2_1.getContents().size());
		assertTrue(retrievedSavedResourceUml2_1.getContents().get(0) instanceof Model);
		Model retrievedSavedModelUml2_1 = (Model) retrievedSavedResourceUml2_1.getContents().get(0);
		assertEquals(NLS.bind(message1, retrievedSavedResourceUml2_1.getURI().toString()), changedObjectName, retrievedSavedModelUml2_1
				.getPackagedElements().get(0).getName());
	}

	/**
	 * Test method for {@link EcoreResourceUtil#saveNewModelResource(ResourceSet, URI, String, EObject, java.util.Map)}
	 * 
	 * @throws Exception
	 */

	private org.eclipse.sphinx.examples.hummingbird10.Application createApplicationHB10() {
		org.eclipse.sphinx.examples.hummingbird10.Application application = Hummingbird10Factory.eINSTANCE.createApplication();
		application.setName("Application");

		Component component1 = Hummingbird10Factory.eINSTANCE.createComponent();
		component1.setName("Component1");

		Component component2 = Hummingbird10Factory.eINSTANCE.createComponent();
		component2.setName("Component2");

		org.eclipse.sphinx.examples.hummingbird10.Interface interface1 = Hummingbird10Factory.eINSTANCE.createInterface();
		interface1.setName("Interface");

		application.getComponents().add(component1);
		application.getComponents().add(component2);
		application.getInterfaces().add(interface1);

		return application;
	}

	public void testSaveNewHummingbirdResource() throws Exception {
		final ResourceSet resourceSet10 = refWks.editingDomain10.getResourceSet();
		int resourceInEditingDomain10 = resourceSet10.getResources().size();
		final ResourceSet resourceSet20 = refWks.editingDomain20.getResourceSet();
		int resourceInEditingDomain20 = resourceSet20.getResources().size();

		{
			final EObject modelToSave = createApplicationHB10();
			String newResoureName1 = "NewHbResource_1.hummingbird";
			IPath newFilePath1 = refWks.hbProject10_A.getFullPath().append(newResoureName1);
			final URI newResourceURI1 = URI.createPlatformResourceURI(newFilePath1.toString(), true);

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(newFilePath1);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, newResourceURI1, Hummingbird10Package.eCONTENT_TYPE,
										modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
			resourceInEditingDomain10++;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);

			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResoureName1);
			assertTrue(newFile.isAccessible());
			// Verify resource content was saved correctly
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI1, false);
			assertNotNull(retrievedResource);

			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());
		}
		// ------------------------------------------------------
		// External
		// Used wrong ResourceSet: ResourceSet of another MetaModelDescriptor
		{
			String newResoureName2 = "NewHbResource_2.hummingbird";
			IPath newFilePath2 = refWks.hbProject10_A.getFullPath().append(newResoureName2);
			final URI newResourceURI2 = URI.createPlatformResourceURI(newFilePath2.toString(), true);

			final EObject modelToSave = createApplicationHB10();

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(newFilePath2);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet20, newResourceURI2, Hummingbird10Package.eCONTENT_TYPE,
										modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			resourceInEditingDomain20++;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			resourceInEditingDomain10++;
			resourceInEditingDomain20--;
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResoureName2);
			assertTrue(newFile.isAccessible());
			// Verify resource content was not saved
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI2, false);
			assertNotNull(retrievedResource);
			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());
		}
		// Save to an existing URI
		{
			IPath existingFilePath = refWks.hbProject10_A.getFullPath().append(DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
			final URI existingResourceURI = URI.createPlatformResourceURI(existingFilePath.toString(), true);

			final EObject modelToSave = createApplicationHB10();

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(existingFilePath);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, existingResourceURI, Hummingbird10Package.eCONTENT_TYPE,
										modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
			assertTrue(newFile.isAccessible());
			// Verify resource content was saved correctly
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(existingResourceURI, false);
			assertNotNull(retrievedResource);
			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());
		}
		// Save Resource to a project that has MetaModelDescitor different from resource's
		{
			String newResourceName = "NewResource_3.hummingbird";
			IPath existingFilePath = refWks.hbProject20_A.getFullPath().append(newResourceName);
			final URI newResourceURI = URI.createPlatformResourceURI(existingFilePath.toString(), true);

			final EObject modelToSave = createApplicationHB10();

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(existingFilePath);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, newResourceURI, Hummingbird10Package.eCONTENT_TYPE,
										modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			waitForModelLoading();
			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, ++resourceInEditingDomain10);
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
			waitForModelLoading();
			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, --resourceInEditingDomain10);
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject20_A.getFile(newResourceName);
			assertTrue(newFile.isAccessible());
			// Verify resource content was saved correctly
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI, false);
			assertNull(retrievedResource);
			retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI, true);
			assertNotNull(retrievedResource);
			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());

			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject20_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
		}
		// Used wrong ContenTypeID
		{
			String newResourceName = "NewResource_4.hummingbird";
			IPath newResourcePath = refWks.hbProject10_A.getFullPath().append(newResourceName);
			final URI newResourceURI = URI.createPlatformResourceURI(newResourcePath.toString(), true);

			final EObject modelToSave = createApplicationHB10();

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(newResourcePath);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, newResourceURI,
										Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, ++resourceInEditingDomain10);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResourceName);
			assertTrue(newFile.isAccessible());
			// Verify resource content was saved correctly
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI, false);
			assertNotNull(retrievedResource);
			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());

		}
		// Given ResourceSet is Null
		{
			String newResourceName = "NewResource_5.hummingbird";
			IPath existingFilePath = refWks.hbProject10_A.getFullPath().append(newResourceName);
			final URI newResourceURI = URI.createPlatformResourceURI(existingFilePath.toString(), true);

			final EObject modelToSave = createApplicationHB10();

			EcoreResourceUtil.saveNewModelResource(null, newResourceURI, Hummingbird10Package.eCONTENT_TYPE, modelToSave, Collections.emptyMap());
			// ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, ++resourceInEditingDomain10);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResourceName);
			assertTrue(newFile.isAccessible());
			// Verify resource content was saved correctly
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI, false);
			assertNotNull(retrievedResource);
			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());
		}
		// Given URI is NULL
		{
			String newResourceName = "NewResource_6.hummingbird";
			IPath existingFilePath = refWks.hbProject10_A.getFullPath().append(newResourceName);

			final EObject modelToSave = createApplicationHB10();

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(existingFilePath);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, null, Hummingbird10Package.eCONTENT_TYPE, modelToSave,
										Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResourceName);
			assertFalse(newFile.isAccessible());

		}
		// ContenTypeId is NULL
		{
			String newResourceName = "NewResource_7.hummingbird";
			IPath existingFilePath = refWks.hbProject10_A.getFullPath().append(newResourceName);
			final URI newResourceURI = URI.createPlatformResourceURI(existingFilePath.toString(), true);

			final EObject modelToSave = createApplicationHB10();

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(existingFilePath);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, newResourceURI, null, modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, ++resourceInEditingDomain10);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResourceName);
			assertTrue(newFile.isAccessible());
			// Verify resource content was not saved
			Resource retrievedResource = refWks.editingDomain10.getResourceSet().getResource(newResourceURI, false);
			assertNotNull(retrievedResource);
			assertEquals(1, retrievedResource.getContents().size());
			assertTrue(retrievedResource.getContents().get(0) instanceof org.eclipse.sphinx.examples.hummingbird10.Application);
			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication = (org.eclipse.sphinx.examples.hummingbird10.Application) retrievedResource
					.getContents().get(0);

			assertEquals(2, retrievedApplication.getComponents().size());
			assertEquals(1, retrievedApplication.getInterfaces().size());
		}
		// ModelRoot is NULL
		{
			String newResourceName = "NewResource_8.hummingbird";
			IPath existingFilePath = refWks.hbProject10_A.getFullPath().append(newResourceName);
			final URI newResourceURI = URI.createPlatformResourceURI(existingFilePath.toString(), true);

			final EObject modelToSave = null;

			ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(existingFilePath);

			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain10, new Runnable() {
							public void run() {
								EcoreResourceUtil.saveNewModelResource(resourceSet10, newResourceURI, Hummingbird10Package.eCONTENT_TYPE,
										modelToSave, Collections.emptyMap());
							}
						}, "Save new model resource");

					} catch (Exception ex) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						throw new CoreException(status);
					}
				}
			};
			ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Reload project
			ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject10_A, false, false, new NullProgressMonitor());
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

			// Verify saved resource set
			assertEditingDomainResourcesSizeEquals(refWks.editingDomain10, resourceInEditingDomain10);
			// Verify that there is an underlying file
			IFile newFile = refWks.hbProject10_A.getFile(newResourceName);
			assertFalse(newFile.isAccessible());

		}
	}

	public void testSaveNewUml2Resource() throws Exception {
		final ResourceSet resourceSetUml2 = refWks.editingDomainUml2.getResourceSet();
		int resourceInEditingDomainUml2 = refWks.getInitialResourcesInReferenceEditingDomainCount(UML2MMDescriptor.INSTANCE);
		int resourceInEditingDomain20 = refWks.getInitialResourcesInReferenceEditingDomainCount(Hummingbird20MMDescriptor.INSTANCE);

		final String newResoureName = "NewUml2Model.uml";
		IPath newFilePath = refWks.hbProject20_D.getFullPath().append(newResoureName);
		final URI newResourceURI = URI.createPlatformResourceURI(newFilePath.toString(), true);
		final Model Uml2Model = UMLFactory.eINSTANCE.createModel();
		final String packageName = "package1";

		// Transaction to save new model
		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomainUml2, new Runnable() {
				public void run() {
					Package Uml2Pack = UMLFactory.eINSTANCE.createPackage();
					Uml2Pack.setName(packageName);
					Uml2Model.getPackagedElements().add(Uml2Pack);

				}
			}, "Save model resource");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		ISchedulingRule rule = ExtendedPlatform.createSaveNewSchedulingRule(newFilePath);

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomainUml2, new Runnable() {
						public void run() {
							EcoreResourceUtil.saveNewModelResource(resourceSetUml2, newResourceURI, UMLPackage.eCONTENT_TYPE, Uml2Model,
									Collections.emptyMap());
						}
					}, "Save new model resource");

				} catch (Exception ex) {
					IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					throw new CoreException(status);
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, rule, 0, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		// Reload project
		ModelLoadManager.INSTANCE.reloadProject(refWks.hbProject20_D, false, false, new NullProgressMonitor());
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());

		// Verify saved resource set
		assertEditingDomainResourcesSizeEquals(refWks.editingDomain20, resourceInEditingDomain20);
		assertEditingDomainResourcesSizeEquals(refWks.editingDomainUml2, resourceInEditingDomainUml2 + 1);

		IFile newFile = refWks.hbProject20_D.getFile(newResoureName);
		assertTrue(newFile.isAccessible());

		Resource retrievedResource = refWks.editingDomainUml2.getResourceSet().getResource(newResourceURI, false);
		assertNotNull(retrievedResource);
		assertEquals(1, retrievedResource.getContents().size());
		assertTrue(retrievedResource.getContents().get(0) instanceof Model);
		Model retrievedUml2Model = (Model) retrievedResource.getContents().get(0);
		assertEquals(1, retrievedUml2Model.getPackagedElements().size());
		assertEquals(packageName, retrievedUml2Model.getPackagedElements().get(0).getName());
	}

	/**
	 * Test method for {@link EcoreResourceUtil#loadModelRoot(ResourceSet, File, java.util.Map)}. This method is equal
	 * {@link EcoreResourceUtil#loadModelRoot(ResourceSet, URI, java.util.Map)}-> almost test cases were covered in
	 * tests: <li>{@link EcoreResourceUtilTest#testLoadModelRootWithDefaultOptions()}</li> <li>
	 * {@link EcoreResourceUtilTest#testLoadModelRootWithNullOptions()}</li> <li>
	 * {@link EcoreResourceUtilTest#testLoadModelRootWithNonExistingFileUri()}</li>
	 * 
	 * @throws InterruptedException
	 * @throws OperationCanceledException
	 * @throws IOException
	 */
	public void testLoadModelRootFromFile() throws OperationCanceledException, InterruptedException, IOException {
		{
			IFile hb20IFile = refWks.hbProject20_A.getFile(DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
			IPath hb20FilePath = hb20IFile.getLocation();
			File hb20File = hb20FilePath.toFile();
			assertNotNull(hb20File);
			assertTrue(hb20File.exists());

			org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application retrievedApplication20 = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
					.loadModelRoot(refWks.editingDomain20.getResourceSet(), hb20File, null);
			assertNotNull(retrievedApplication20);
			assertEquals(1, retrievedApplication20.getComponents().size());

		}
		{
			IPath hb10FilePath = refWks.hbProject10_A.getFile(DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1).getLocation();
			File hb10File = hb10FilePath.toFile();
			assertNotNull(hb10File);
			assertTrue(hb10File.exists());

			org.eclipse.sphinx.examples.hummingbird10.Application retrievedApplication10 = (org.eclipse.sphinx.examples.hummingbird10.Application) EcoreResourceUtil
					.loadModelRoot(refWks.editingDomain10.getResourceSet(), hb10File, null);
			assertNotNull(retrievedApplication10);
			assertEquals(1, retrievedApplication10.getComponents().size());
			assertEquals(1, retrievedApplication10.getInterfaces().size());

		}
		{
			IPath uml2FilePath = refWks.hbProject20_D.getFile(DefaultTestReferenceWorkspace.UML2_FILE_NAME_20D_1).getLocation();
			File uml2File = uml2FilePath.toFile();
			assertNotNull(uml2File);
			assertTrue(uml2File.exists());

			Model retrievedUml2ModelRoot = (Model) EcoreResourceUtil.loadModelRoot(refWks.editingDomainUml2.getResourceSet(), uml2File, null);
			assertNotNull(retrievedUml2ModelRoot);

			assertEquals(2, retrievedUml2ModelRoot.getPackagedElements().size());
			assertEquals(2, retrievedUml2ModelRoot.getPackagedElements().get(1).getOwnedElements().size());
		}
		// Given File does not exist
		{
			IPath nonExistingFilePath = refWks.hbProject20_D.getLocation().append(DefaultTestReferenceWorkspace.UML2_FILE_NAME_20E_1);
			File nonExistingFile = nonExistingFilePath.toFile();
			assertNotNull(nonExistingFile);
			assertFalse(nonExistingFile.exists());

			assertNull(EcoreResourceUtil.loadModelRoot(refWks.editingDomain20.getResourceSet(), nonExistingFile, null));
		}
		// Given File is Null
		{
			File nullFile = null;
			try {
				assertNull(EcoreResourceUtil.loadModelRoot(refWks.editingDomain20.getResourceSet(), nullFile, null));
			} catch (Exception ex) {
				if (!(ex instanceof AssertionFailedException)) {
					fail("Exception while input File is Null : " + ex.getLocalizedMessage());
				}
			}
		}
		// Given File is outside workspace
		{
			File outsideWorkspaceFile = getTestFileAccessor().createWorkingCopyOfInputFile("hbFile20.instancemodel");
			assertTrue(outsideWorkspaceFile.exists());

			assertNotNull(outsideWorkspaceFile);
			assertTrue("Resource outside workspace does not exsit: " + outsideWorkspaceFile.getPath(), outsideWorkspaceFile.exists());

			retrievedOutsideWorkspaceHb20Root = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) EcoreResourceUtil
					.loadModelRoot(refWks.editingDomain20.getResourceSet(), outsideWorkspaceFile, null);

			assertEquals(1, retrievedOutsideWorkspaceHb20Root.getComponents().size());

		}
	}
}
