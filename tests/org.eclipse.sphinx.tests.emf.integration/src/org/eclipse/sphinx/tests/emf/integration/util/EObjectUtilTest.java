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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.impl.ComponentTypeImpl;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.impl.PortImpl;
import org.eclipse.sphinx.examples.uml2.ide.metamodel.UML2MMDescriptor;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;

@SuppressWarnings("nls")
public class EObjectUtilTest extends DefaultIntegrationTestCase {
	int hbProject10_A_HummingbirdObjectCount = 0;
	int hbProject20_B_Uml2ObjectCount = 0;

	List<String> hbProject10AResources10;
	int resources10FromHbProject10_A;

	List<String> hbProject20AResources20;
	int resources20FromHbProject20_A;

	List<String> hbProject20AResourcesUml2;
	int resourcesUml2FromHbProject20_A;

	List<String> hbProject20BResources20;
	int resources20FromHbProject20_B;

	List<String> hbProject20BResourcesUml2;
	int resourcesUml2FromHbProject20_B;

	List<String> hbProject20CResources20;
	int resources20FromHbProject20_C;

	ComponentType componentType20A_2_1;
	ComponentType componentType20A_2_2;
	Port port20A_2_1;
	Port port20A_2_2;

	Component component20A_3_1;
	Component component20A_3_2;
	Component component21A_4_1;

	ComponentType componentType20D_2_1;
	ComponentType componentType20D_2_2;
	Port port20D_2_1;
	Port port20D_2_2;
	Component component20D_3_1;
	Component component20E_1_1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		hbProject10AResources10 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A, Hummingbird10MMDescriptor.INSTANCE);
		resources10FromHbProject10_A = hbProject10AResources10.size();

		hbProject20AResources20 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, Hummingbird20MMDescriptor.INSTANCE);
		resources20FromHbProject20_A = hbProject20AResources20.size();

		hbProject20AResourcesUml2 = refWks.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, UML2MMDescriptor.INSTANCE);
		resourcesUml2FromHbProject20_A = hbProject20AResourcesUml2.size();

		hbProject20BResources20 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B, Hummingbird20MMDescriptor.INSTANCE);
		resources20FromHbProject20_B = hbProject20BResources20.size();

		hbProject20BResourcesUml2 = refWks.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B, UML2MMDescriptor.INSTANCE);
		resourcesUml2FromHbProject20_B = hbProject20BResourcesUml2.size();

		hbProject20CResources20 = refWks
				.getReferenceFileNames(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C, Hummingbird20MMDescriptor.INSTANCE);
		resources20FromHbProject20_C = hbProject20CResources20.size();

		hbProject10_A_HummingbirdObjectCount = resources10FromHbProject10_A;
		hbProject20_B_Uml2ObjectCount = resourcesUml2FromHbProject20_B;

	}

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C,
				DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_E };
	}

	@Override
	protected String[][] getProjectReferences() {
		return new String[][] { { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_E, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D } };
	}

	private int getNumberOfHB20fApplicationInstances(Collection<String> resourceNames) {
		int res = 0;
		for (Resource resource : refWks.editingDomain20.getResourceSet().getResources()) {
			if (resourceNames.contains(resource.getURI().lastSegment())) {
				assertFalse(resource.getContents().isEmpty());
				EObject modelRoot = resource.getContents().get(0);
				if (modelRoot instanceof Application) {
					res++;
				}
			}
		}
		return res;
	}

	private int getNumberOfHB20ComponentInstances(Collection<String> resourceNames) {
		int res = 0;
		for (Resource resource : refWks.editingDomain20.getResourceSet().getResources()) {
			if (resourceNames.contains(resource.getURI().lastSegment())) {
				assertFalse(resource.getContents().isEmpty());
				EObject modelRoot = resource.getContents().get(0);
				if (modelRoot instanceof Application) {
					Application app = (Application) modelRoot;
					res += app.getComponents().size();
				}
			}
		}
		return res;
	}

	private int getNumberOfHB20InterfaceInstances(Collection<String> resourceNames) {
		int res = 0;
		for (Resource resource : refWks.editingDomain20.getResourceSet().getResources()) {
			if (resourceNames.contains(resource.getURI().lastSegment())) {
				assertFalse(resource.getContents().isEmpty());
				EObject modelRoot = resource.getContents().get(0);
				if (modelRoot instanceof Platform) {
					Platform platform = (Platform) modelRoot;
					res += platform.getInterfaces().size();
				}
			}
		}
		return res;
	}

	private int getNumberOfHB20PlatfromInstances(Collection<String> resourceNames) {
		int res = 0;
		for (Resource resource : refWks.editingDomain20.getResourceSet().getResources()) {
			if (resourceNames.contains(resource.getURI().lastSegment())) {
				assertFalse(resource.getContents().isEmpty());
				EObject modelRoot = resource.getContents().get(0);
				if (modelRoot instanceof Platform) {
					res++;
				}
			}
		}
		return res;
	}

	private int getNumberOfHB10ComponentInstances(Collection<String> resourceNames) {
		int res = 0;
		for (Resource resource : refWks.editingDomain10.getResourceSet().getResources()) {
			if (resourceNames.contains(resource.getURI().lastSegment())) {
				assertFalse(resource.getContents().isEmpty());
				EObject modelRoot = resource.getContents().get(0);
				if (modelRoot instanceof org.eclipse.sphinx.examples.hummingbird10.Application) {
					org.eclipse.sphinx.examples.hummingbird10.Application app = (org.eclipse.sphinx.examples.hummingbird10.Application) modelRoot;
					res += app.getComponents().size();
				}
			}
		}
		return res;
	}

	private int getNumberOfHB10ApplicationiInstances(Collection<String> resourceNames) {
		int res = 0;
		for (Resource resource : refWks.editingDomain10.getResourceSet().getResources()) {
			if (resourceNames.contains(resource.getURI().lastSegment())) {
				assertFalse(resource.getContents().isEmpty());
				EObject modelRoot = resource.getContents().get(0);
				if (modelRoot instanceof org.eclipse.sphinx.examples.hummingbird10.Application) {
					res++;
				}
			}
		}
		return res;
	}

	/**
	 * Test for method {@link EObjectUtil#getAllInstancesOf(EObject, EReference, boolean)}
	 */
	public void testGetAllInstancesOf_EObject_EReference() {
		// HB20 Object
		Resource resource20_A_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);

		assertNotNull(resource20_A_1);
		assertFalse(resource20_A_1.getContents().isEmpty());

		EObject hb20ModelRoot_20A_1 = resource20_A_1.getContents().get(0);

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20A_1, InstanceModel20Package.eINSTANCE.getApplication_Components(), true).size());

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20A_1, InstanceModel20Package.eINSTANCE.getApplication().getInstanceClass(), true).size());

		assertEquals(getNumberOfHB20InterfaceInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20A_1, TypeModel20Package.eINSTANCE.getPlatform_Interfaces(), true).size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20A_1, TypeModel20Package.eINSTANCE.getPlatform().getInstanceClass(), true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot_20A_1, InstanceModel20Package.eINSTANCE.getComponent_IncomingConnections(), true)
				.size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot_20A_1, Hummingbird10Package.eINSTANCE.getApplication_Components(), true).size());
		// --------------------------------
		Resource resource20_C_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20C_1, false), false);

		assertNotNull(resource20_C_1);
		assertFalse(resource20_C_1.getContents().isEmpty());
		EObject hb20ModelRoot_20_C_1 = resource20_C_1.getContents().get(0);

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20CResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20_C_1, InstanceModel20Package.eINSTANCE.getApplication_Components(), true).size());

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20CResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20_C_1, InstanceModel20Package.eINSTANCE.getApplication().getInstanceClass(), true)
						.size());

		assertEquals(getNumberOfHB20InterfaceInstances(hbProject20CResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20_C_1, TypeModel20Package.eINSTANCE.getPlatform_Interfaces(), true).size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20CResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot_20_C_1, TypeModel20Package.eINSTANCE.getPlatform().getInstanceClass(), true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot_20_C_1, TypeModel20Package.eINSTANCE.getComponentType_Parameters(), true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot_20_C_1, Hummingbird10Package.eINSTANCE.getApplication().getInstanceClass(), true)
				.size());
		// =================================================
		// HB10 Object
		Resource resource10_A = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);

		assertNotNull(resource10_A);
		assertFalse(resource10_A.getContents().isEmpty());

		EObject hb10ModelRoot_10A_1 = resource10_A.getContents().get(0);

		assertEquals(getNumberOfHB10ComponentInstances(hbProject10AResources10),
				EObjectUtil.getAllInstancesOf(hb10ModelRoot_10A_1, Hummingbird10Package.eINSTANCE.getApplication_Components(), true).size());

		assertEquals(getNumberOfHB10ApplicationiInstances(hbProject10AResources10),
				EObjectUtil.getAllInstancesOf(hb10ModelRoot_10A_1, Hummingbird10Package.eINSTANCE.getApplication().getInstanceClass(), true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb10ModelRoot_10A_1, TypeModel20Package.eINSTANCE.getComponentType().getInstanceClass(), true)
				.size());

	}

	/**
	 * Test method for {@link EObjectUtil#getAllInstancesOf(EObject, Class, boolean)}
	 */
	public void testGetAllInstancesOf_EObject_Class() {

		Resource resource20 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);

		assertNotNull(resource20);
		assertFalse(resource20.getContents().isEmpty());
		EObject hb20ModelRoot = resource20.getContents().get(0);
		assertTrue(hb20ModelRoot instanceof Application);
		Application contextApplication = (Application) hb20ModelRoot;
		assertFalse(contextApplication.getComponents().isEmpty());
		Component contextComponent = contextApplication.getComponents().get(0);

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(hb20ModelRoot, Application.class, true).size());

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(hb20ModelRoot, Component.class, true)
				.size());
		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(contextComponent, Component.class, true).size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(hb20ModelRoot, Platform.class, true)
				.size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		// ------------------------------------------------------------
		resource20 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20C_1, false), false);

		assertNotNull(resource20);
		assertFalse(resource20.getContents().isEmpty());
		hb20ModelRoot = resource20.getContents().get(0);

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20CResources20), EObjectUtil.getAllInstancesOf(hb20ModelRoot, Platform.class, true)
				.size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		// =========================================================
		Resource resource10_A = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
		assertNotNull(resource10_A);
		assertFalse(resource10_A.getContents().isEmpty());

		EObject hb10ModelRoot = resource10_A.getContents().get(0);
		assertTrue(hb10ModelRoot instanceof org.eclipse.sphinx.examples.hummingbird10.Application);

		assertEquals(hbProject10_A_HummingbirdObjectCount,
				EObjectUtil.getAllInstancesOf(hb10ModelRoot, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(hb10ModelRoot, Application.class, true).size());
		// ===========================================================
		// UML2 resource
		Resource resourceUml2_20C = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_1, false), false);

		assertNotNull(resourceUml2_20C);
		assertFalse(resourceUml2_20C.getContents().isEmpty());
		EObject uml2ModelRoot = resourceUml2_20C.getContents().get(0);

		assertEquals(hbProject20_B_Uml2ObjectCount, EObjectUtil.getAllInstancesOf(uml2ModelRoot, Model.class, true).size());

		assertEquals(hbProject20_B_Uml2ObjectCount * 2, EObjectUtil.getAllInstancesOf(uml2ModelRoot, Package.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(uml2ModelRoot, Application.class, true).size());

		// ==============================================================
		// Context Object doesn't belong to any resource
		Application hb20Application = createHb20Application();

		assertEquals(2, EObjectUtil.getAllInstancesOf(hb20Application, Component.class, true).size());

		contextComponent = hb20Application.getComponents().get(0);
		assertEquals(2, EObjectUtil.getAllInstancesOf(contextComponent, Component.class, true).size());
		// ==============================================================
		// Not exact match
		assertEquals(3, EObjectUtil.getAllInstancesOf(hb20Application, Identifiable.class, false).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(hb20Application.getComponents().get(0), Identifiable.class, false).size());
		// ==============================================================
		// Context Object belongs to resource without resourceSet
		Resource contextResource = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);
		assertNotNull(contextResource);
		EObject contextObject = EcoreResourceUtil.getModelRoot(contextResource);
		assertNotNull(contextObject);
		// Remove contextResource from ResourceSet
		refWks.editingDomain20.getResourceSet().getResources().remove(contextResource);
		assertNull(contextResource.getResourceSet());
		hbProject20AResources20.remove(DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
		hb20ModelRoot = EcoreResourceUtil.getModelRoot(contextResource);

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(hb20ModelRoot, Component.class, true)
				.size());
		// ==============================================================
		// Context Object belong to resource in ResourceSet without EditingDomain
		ResourceSetImpl testResourceSet = new ScopingResourceSetImpl();

		URI newResourceUri1 = URI.createURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/1.instancemodel", true);
		URI newResourceUri2 = URI.createURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/2.instancemodel", true);
		URI newResourceUri3 = URI.createURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C + "/3.instancemodel", true);
		Resource newRes1 = testResourceSet.createResource(newResourceUri1);
		Resource newRes2 = testResourceSet.createResource(newResourceUri2);
		Resource newRes3 = testResourceSet.createResource(newResourceUri3);

		newRes1.getContents().add(createHb20Application());
		newRes2.getContents().add(createHb20Application());
		newRes3.getContents().add(createHb20Application());
		EObject contextEObject = EcoreResourceUtil.getModelRoot(newRes1);

		assertEquals(6, EObjectUtil.getAllInstancesOf(contextEObject, Component.class, true).size());
		// ==============================================================
		// Input context Object is NULL
		try {
			EObject nullObject = null;
			assertEquals(0, EObjectUtil.getAllInstancesOf(nullObject, Component.class, true).size());
		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				ex.printStackTrace();
				// TODO Null Pointer Exception
				fail("Exception when contextObject is NULL" + ex.getClass().getSimpleName() + " " + ex.getMessage());
			}
		}
		// ==============================================================
		// Given class is null
		try {
			Class<?> nullClass = null;
			assertEquals(0, EObjectUtil.getAllInstancesOf(hb20ModelRoot, nullClass, true).size());
		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				fail("Exception when given type is NULL" + ex.getClass().getSimpleName() + " " + ex.getMessage());
			}
		}

	}

	/**
	 * Test method for {@link EObjectUtil#getAllInstancesOf(Resource, Class, boolean)}
	 */
	public void testGetAllInstancesOf_Resource() {

		Resource resource20_A = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);
		assertNotNull(resource20_A);

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(resource20_A, Application.class, true).size());

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(resource20_A, Component.class, true)
				.size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(resource20_A, Platform.class, true)
				.size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(resource20_A, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		// ------------------------------------------------------------
		Resource resource20_C = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20C_1, false), false);

		assertNotNull(resource20_C);

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20CResources20), EObjectUtil.getAllInstancesOf(resource20_C, Platform.class, true)
				.size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(resource20_C, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		// =========================================================
		Resource resource10_A = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
		assertNotNull(resource10_A);

		assertEquals(getNumberOfHB10ComponentInstances(hbProject10AResources10),
				EObjectUtil.getAllInstancesOf(resource10_A, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(resource10_A, Application.class, true).size());
		// ===========================================================
		// UML2 resource
		Resource resourceUml2_20C = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_1, false), false);

		assertNotNull(resourceUml2_20C);

		assertEquals(hbProject20_B_Uml2ObjectCount, EObjectUtil.getAllInstancesOf(resourceUml2_20C, Model.class, true).size());

		assertEquals(hbProject20_B_Uml2ObjectCount * 2, EObjectUtil.getAllInstancesOf(resourceUml2_20C, Package.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(resourceUml2_20C, Application.class, true).size());
		// ==============================================================
		// Resource without ResourceSet
		ResourceSet resourceSet = resource20_A.getResourceSet();
		resourceSet.getResources().remove(resource20_A);
		hbProject20AResources20.remove(DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(resource20_A, Application.class, true).size());

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(resource20_A, Component.class, true)
				.size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(resource20_A, Platform.class, true)
				.size());
		// ==============================================================
		// Resource in ResourceSet without EditingDomain

		ResourceSetImpl testResourceSet = new ScopingResourceSetImpl();

		URI newResourceUri1 = URI.createURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/1.instancemodel", true);
		URI newResourceUri2 = URI.createURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/2.instancemodel", true);
		URI newResourceUri3 = URI.createURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C + "/3.instancemodel", true);
		Resource newRes1 = testResourceSet.createResource(newResourceUri1);
		Resource newRes2 = testResourceSet.createResource(newResourceUri2);
		Resource newRes3 = testResourceSet.createResource(newResourceUri3);

		newRes1.getContents().add(createHb20Application());
		newRes2.getContents().add(createHb20Application());
		newRes3.getContents().add(createHb20Application());

		assertEquals(6, EObjectUtil.getAllInstancesOf(newRes1, Component.class, true).size());

		// ==============================================================
		// Not exact match
		assertEquals(9, EObjectUtil.getAllInstancesOf(newRes1, Identifiable.class, false).size());
		// ==============================================================
		try {
			Resource nullResource = null;
			assertEquals(0, EObjectUtil.getAllInstancesOf(nullResource, Component.class, true).size());
		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				ex.printStackTrace();
				fail("Exception when contextResource is NULL" + ex.getClass().getSimpleName() + " " + ex.getMessage());
			}
		}
		// ==============================================================
		// Given class is null
		try {
			Class<?> nullClass = null;
			assertEquals(0, EObjectUtil.getAllInstancesOf(resource20_A, nullClass, true).size());
		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				fail("Exception when given type is NULL" + ex.getClass().getSimpleName() + " " + ex.getMessage());
			}
		}

	}

	private Application createHb20Application() {
		Application hb20Application = InstanceModel20Factory.eINSTANCE.createApplication();
		Component component1 = InstanceModel20Factory.eINSTANCE.createComponent();
		component1.setName("Component1");
		Component component2 = InstanceModel20Factory.eINSTANCE.createComponent();
		component2.setName("Component2");

		hb20Application.getComponents().add(component1);
		hb20Application.getComponents().add(component2);
		return hb20Application;
	}

	/**
	 * Test method for {@link EObjectUtil#getAllInstancesOf(IModelDescriptor, Class, boolean)}
	 */
	public void testGetAllInstancesOf_IModelDescriptor() {
		// ModelDescriptor of hbProject20_A
		Collection<IModelDescriptor> hbProjectModels = ModelDescriptorRegistry.INSTANCE.getModels(refWks.hbProject20_A);
		assertNotNull(hbProjectModels);
		assertEquals(1, hbProjectModels.size());

		IModelDescriptor modelDescriptor = hbProjectModels.iterator().next();

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20AResources20),
				EObjectUtil.getAllInstancesOf(modelDescriptor, Application.class, true).size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20AResources20), EObjectUtil.getAllInstancesOf(modelDescriptor, Platform.class, true)
				.size());

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20AResources20), EObjectUtil
				.getAllInstancesOf(modelDescriptor, Component.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, Identifiable.class, true).size());
		// Not exact match
		assertTrue(EObjectUtil.getAllInstancesOf(modelDescriptor, Identifiable.class, false).size() > 0);
		// ---------------------------------------
		// ModelDescriptor of hbProject20C
		hbProjectModels = ModelDescriptorRegistry.INSTANCE.getModels(refWks.hbProject20_C);
		assertNotNull(hbProjectModels);
		assertEquals(1, hbProjectModels.size());
		modelDescriptor = hbProjectModels.iterator().next();

		assertEquals(getNumberOfHB20fApplicationInstances(hbProject20CResources20),
				EObjectUtil.getAllInstancesOf(modelDescriptor, Application.class, true).size());

		assertEquals(getNumberOfHB20PlatfromInstances(hbProject20CResources20), EObjectUtil.getAllInstancesOf(modelDescriptor, Platform.class, true)
				.size());

		assertEquals(getNumberOfHB20ComponentInstances(hbProject20CResources20), EObjectUtil
				.getAllInstancesOf(modelDescriptor, Component.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, Identifiable.class, true).size());
		// Not exact match
		assertTrue(EObjectUtil.getAllInstancesOf(modelDescriptor, Identifiable.class, false).size() > 0);
		// ==================================================
		// Model Descriptor of HB10
		// Model of hbProject10_A
		hbProjectModels = ModelDescriptorRegistry.INSTANCE.getModels(refWks.hbProject10_A);
		assertNotNull(hbProjectModels);
		assertEquals(1, hbProjectModels.size());
		modelDescriptor = hbProjectModels.iterator().next();

		assertEquals(hbProject10_A_HummingbirdObjectCount,
				EObjectUtil.getAllInstancesOf(modelDescriptor, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());

		assertEquals(getNumberOfHB10ComponentInstances(hbProject10AResources10),
				EObjectUtil.getAllInstancesOf(modelDescriptor, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, Application.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, EObject.class, true).size());
		// Not exact match
		assertTrue(EObjectUtil.getAllInstancesOf(modelDescriptor, EObject.class, false).size() > 0);
		// ==============================================================
		// Uml2 model
		Collection<IModelDescriptor> uml2ProjectModels = ModelDescriptorRegistry.INSTANCE.getModels(refWks.hbProject20_B, UML2MMDescriptor.INSTANCE);
		assertNotNull(hbProjectModels);
		assertEquals(1, hbProjectModels.size());

		modelDescriptor = uml2ProjectModels.iterator().next();

		assertEquals(hbProject20_B_Uml2ObjectCount, EObjectUtil.getAllInstancesOf(modelDescriptor, Model.class, true).size());

		assertEquals(hbProject20_B_Uml2ObjectCount * 2, EObjectUtil.getAllInstancesOf(modelDescriptor, Package.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());

		assertEquals(0, EObjectUtil.getAllInstancesOf(modelDescriptor, EObject.class, true).size());
		// Not exact match
		assertTrue(EObjectUtil.getAllInstancesOf(modelDescriptor, EObject.class, false).size() > 0);
		// ==============================================================
		// Input model is NULL
		ModelDescriptor nullModel = null;
		assertEquals(0, EObjectUtil.getAllInstancesOf(nullModel, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
	}

	/**
	 * Test method for {@link EObjectUtil#getAllInstancesOf(List, Class, boolean)}
	 */
	public void testGetAllInstancesOf_Resources() {
		/* Test variable settings */
		Resource resource20_A_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1, false), false);
		Resource resource20_A_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, false), false);
		Resource resource20_A_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, false), false);

		assertNotNull(resource20_A_1);
		assertFalse(resource20_A_1.getContents().isEmpty());
		assertNotNull(resource20_A_2);
		assertFalse(resource20_A_2.getContents().isEmpty());
		assertNotNull(resource20_A_3);
		assertFalse(resource20_A_3.getContents().isEmpty());
		// --------------
		Resource resource10_1 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1, false), false);
		Resource resource10_2 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2, false), false);
		Resource resource10_3 = refWks.editingDomain10.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3, false), false);

		assertNotNull(resource10_1);
		assertFalse(resource10_1.getContents().isEmpty());
		assertNotNull(resource10_2);
		assertFalse(resource10_2.getContents().isEmpty());
		assertNotNull(resource10_3);
		assertFalse(resource10_3.getContents().isEmpty());
		// --------------------
		Resource resourceUml2_1 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_1, false), false);
		Resource resourceUml2_2 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_2, false), false);
		Resource resourceUml2_3 = refWks.editingDomainUml2.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B + "/"
						+ DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_3, false), false);

		assertNotNull(resourceUml2_1);
		assertFalse(resourceUml2_1.getContents().isEmpty());
		assertNotNull(resourceUml2_2);
		assertFalse(resourceUml2_2.getContents().isEmpty());
		assertNotNull(resourceUml2_3);
		assertFalse(resourceUml2_3.getContents().isEmpty());
		/* Tests cases execution */
		LinkedList<Resource> resources = new LinkedList<Resource>();
		// Add resource20_A_1
		resources.add(resource20_A_1);
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		// Add resource20_A_2
		resources.add(resource20_A_2);
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		// Add resource20_A_3
		resources.add(resource20_A_3);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());

		// Add resource10_A_1
		resources.add(resource10_1);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());

		// Add resource10_A_2
		resources.add(resource10_2);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());

		// Add resource10_A_3
		resources.add(resource10_3);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, Model.class, true).size());
		assertEquals(0, EObjectUtil.getAllInstancesOf(resources, Package.class, true).size());

		// Add resourceUml2_1
		resources.add(resourceUml2_1);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Model.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Package.class, true).size());

		// Add resourceUml2_2
		resources.add(resourceUml2_2);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Model.class, true).size());
		assertEquals(4, EObjectUtil.getAllInstancesOf(resources, Package.class, true).size());
		// Add resourceUml2_3
		resources.add(resourceUml2_3);
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Component.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, ComponentType.class, true).size());
		assertEquals(1, EObjectUtil.getAllInstancesOf(resources, Platform.class, true).size());
		assertEquals(2, EObjectUtil.getAllInstancesOf(resources, Interface.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Application.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, org.eclipse.sphinx.examples.hummingbird10.Component.class, true).size());
		assertEquals(3, EObjectUtil.getAllInstancesOf(resources, Model.class, true).size());
		assertEquals(6, EObjectUtil.getAllInstancesOf(resources, Package.class, true).size());

	}

	/**
	 * Test method for {@link EObjectUtil#createProxyFrom(EObject)}
	 */
	public void testCreateProxyFromEObject() {
		// Context: EObject is Hummingbird Object
		Resource resource20_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, false), false);

		Resource resource20_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, false), false);

		assertNotNull(resource20_2);
		assertFalse(resource20_2.getContents().isEmpty());
		assertEquals(1, resource20_2.getContents().size());
		Platform hb20Platform_A_2 = (Platform) resource20_2.getContents().get(0);
		assertNotNull(hb20Platform_A_2);
		assertEquals(2, hb20Platform_A_2.getComponentTypes().size());
		assertEquals(2, hb20Platform_A_2.getInterfaces().size());

		ComponentType componentType1 = hb20Platform_A_2.getComponentTypes().get(0);
		// Port port1 = componentType1.getPorts().get(0);
		// Port port2 = componentType1.getPorts().get(1);

		URI compTypeUri1 = URI.createURI("hb:/#//@componentTypes.0");
		// URI portUri1 = EcoreUtil.getURI(port1);
		// URI portUri2 = EcoreUtil.getURI(port2);

		ComponentType componentType2 = hb20Platform_A_2.getComponentTypes().get(1);
		URI compTypeUri2 = URI.createURI("hb:/#//@componentTypes.1");

		Interface interface1 = hb20Platform_A_2.getInterfaces().get(0);
		Interface interface2 = hb20Platform_A_2.getInterfaces().get(1);

		assertFalse(interface1.getProvidingComponentTypes().isEmpty());
		for (ComponentType componentType : interface1.getProvidingComponentTypes()) {
			assertFalse(componentType.eIsProxy());
			assertTrue(hb20Platform_A_2.getComponentTypes().contains(componentType));
		}

		assertFalse(interface2.getRequiringPorts().isEmpty());
		for (Port port : interface2.getRequiringPorts()) {
			assertFalse(port.eIsProxy());
			assertTrue(componentType1.getPorts().contains(port));
		}

		assertFalse(resource20_3.getContents().isEmpty());
		Application hb20Application = (Application) resource20_3.getContents().get(0);
		assertNotNull(hb20Application);
		assertEquals(2, hb20Application.getComponents().size());
		Component referringComponent_1 = hb20Application.getComponents().get(0);
		assertNotNull(referringComponent_1.getType());
		assertFalse(referringComponent_1.getType().eIsProxy());
		assertTrue(hb20Platform_A_2.getComponentTypes().contains(referringComponent_1.getType()));

		Component referringComponent_2 = hb20Application.getComponents().get(1);
		assertNotNull(referringComponent_2.getType());
		assertFalse(referringComponent_2.getType().eIsProxy());
		assertTrue(hb20Platform_A_2.getComponentTypes().contains(referringComponent_2.getType()));

		// Proxify referred objects
		EObject proxy_1 = EObjectUtil.createProxyFrom(componentType1);
		assertNotNull(proxy_1);
		assertTrue(proxy_1.eIsProxy());
		assertTrue(proxy_1 instanceof InternalEObject);
		assertEquals(compTypeUri1, ((InternalEObject) proxy_1).eProxyURI());

		EObject proxy_2 = EObjectUtil.createProxyFrom(componentType2);
		assertNotNull(proxy_2);
		assertTrue(proxy_2.eIsProxy());
		assertTrue(proxy_2 instanceof InternalEObject);
		assertEquals(compTypeUri2, ((InternalEObject) proxy_2).eProxyURI());

		assertNotNull(interface1);
		assertFalse(interface1.getProvidingComponentTypes().isEmpty());
		for (ComponentType componentType : interface1.getProvidingComponentTypes()) {
			assertFalse(componentType.eIsProxy());
			// assertTrue(compTypeUri1.equals(EcoreUtil.getURI(componentType)) ||
			// compTypeUri2.equals(EcoreUtil.getURI(componentType)));
		}

		assertNotNull(interface2);
		assertFalse(interface2.getRequiringPorts().isEmpty());
		for (Port port : interface2.getRequiringPorts()) {
			assertFalse(port.eIsProxy());
			// assertTrue(portUri1.equals(EcoreUtil.getURI(port)) || portUri2.equals(EcoreUtil.getURI(port)));
		}

		assertNotNull(referringComponent_1);
		assertNotNull(referringComponent_1.getType());
		assertFalse(referringComponent_1.getType().eIsProxy());
		// assertTrue(compTypeUri1.equals(EcoreUtil.getURI(referringComponent_1.getType()))
		// || compTypeUri2.equals(EcoreUtil.getURI(referringComponent_1.getType())));

		assertNotNull(referringComponent_2);
		assertNotNull(referringComponent_2.getType());
		assertFalse(referringComponent_2.getType().eIsProxy());
		// assertTrue(compTypeUri1.equals(EcoreUtil.getURI(referringComponent_2.getType()))
		// || compTypeUri2.equals(EcoreUtil.getURI(referringComponent_2.getType())));

		// ==================================================================
		// Context: EObject is Uml2 object
		for (String fileName : hbProject20BResourcesUml2) {
			IFile file = refWks.hbProject20_B.getFile(fileName);
			Resource resource = EcorePlatformUtil.getResource(file);
			assertNotNull(resource);
			ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource);
			assertTrue(extendedResource instanceof ExtendedResourceAdapter);
			assertNotNull(resource.getContents());
			assertEquals(1, resource.getContents().size());

			Model uml2Model = (Model) resource.getContents().get(0);
			assertNotNull(uml2Model);

			for (PackageableElement element : uml2Model.getPackagedElements()) {

				EObject proxy = EObjectUtil.createProxyFrom(element);
				assertNotNull(proxy);
				assertTrue(proxy.eIsProxy());
				assertEquals(EcoreUtil.getURI(element), ((InternalEObject) proxy).eProxyURI());

			}
		}

		// Other context
		// Context: EObject is NULL-> It should have a null assertion
		boolean flag = false;
		try {
			EObjectUtil.createProxyFrom(null);
		} catch (AssertionFailedException ex) {
			flag = true;
		}
		assertTrue("No assertion when given Object is NULL", flag);

	}

	/**
	 * Test method for
	 * {@link EObjectUtil#getReferencedInstancesOf(EObject, org.eclipse.emf.ecore.EReference, Class, boolean)}
	 */
	public void testGetReferencedInstancesOf() {
		// Test data
		Resource resource20_A_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, false), false);
		assertNotNull(resource20_A_2);
		assertFalse(resource20_A_2.getContents().isEmpty());
		Platform hb20ModelRoot = (Platform) resource20_A_2.getContents().get(0);
		assertNotNull(hb20ModelRoot);
		assertEquals(2, hb20ModelRoot.getComponentTypes().size());
		assertEquals(2, hb20ModelRoot.getInterfaces().size());
		ComponentType hb20Component = hb20ModelRoot.getComponentTypes().get(0);

		Interface hb20Interface = hb20ModelRoot.getInterfaces().get(0);
		assertFalse(hb20Interface.getRequiringPorts().isEmpty());
		// ------------
		Resource resource20_A_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, false), false);
		assertNotNull(resource20_A_3);
		assertFalse(resource20_A_3.getContents().isEmpty());
		Application hb20Application = (Application) resource20_A_3.getContents().get(0);
		assertNotNull(hb20Application);
		assertFalse(hb20Application.getComponents().isEmpty());
		Component component = hb20Application.getComponents().get(0);
		// *****************************************************
		// Context: EReference is Many
		EReference testEReferenceMany_components = (EReference) hb20ModelRoot.eClass().getEStructuralFeature("componentTypes");
		EReference testEReferenceMany_requiringports = (EReference) hb20Interface.eClass().getEStructuralFeature("requiringPorts");

		// --EReference is aggregate
		assertNotNull(testEReferenceMany_components);
		assertTrue(testEReferenceMany_components.isMany());
		List<ComponentType> components = EObjectUtil
				.getReferencedInstancesOf(hb20ModelRoot, testEReferenceMany_components, ComponentType.class, true);
		assertEquals(2, components.size());
		assertEquals(components, hb20ModelRoot.getComponentTypes());
		// --EReference is reference
		assertNotNull(testEReferenceMany_requiringports);
		assertTrue(testEReferenceMany_requiringports.isMany());
		List<Port> rerquiringports = EObjectUtil.getReferencedInstancesOf(hb20Interface, testEReferenceMany_requiringports, Port.class, true);
		assertEquals(1, rerquiringports.size());
		assertTrue(hb20Component.getPorts().containsAll(rerquiringports));

		assertEquals(2, EObjectUtil.getReferencedInstancesOf(hb20ModelRoot, testEReferenceMany_components, ComponentTypeImpl.class, true).size());
		assertEquals(1, EObjectUtil.getReferencedInstancesOf(hb20Interface, testEReferenceMany_requiringports, PortImpl.class, true).size());

		// --Get from accurate class
		assertEquals(0, EObjectUtil.getReferencedInstancesOf(hb20Interface, testEReferenceMany_requiringports, Identifiable.class, true).size());
		assertEquals(0, EObjectUtil.getReferencedInstancesOf(hb20ModelRoot, testEReferenceMany_components, Identifiable.class, true).size());

		// *****************************************************
		// Context: EReference is Unique
		EReference refUnique_Component_Type = InstanceModel20Package.eINSTANCE.getComponent_Type();
		assertFalse(refUnique_Component_Type.isMany());
		assertTrue(EObjectUtil.getReferencedInstancesOf(component, refUnique_Component_Type, ComponentType.class, true).isEmpty());

		// *****************************************************
		// Context: exactMatch is FALSE
		assertEquals(1, EObjectUtil.getReferencedInstancesOf(hb20Interface, testEReferenceMany_requiringports, Identifiable.class, false).size());
		assertEquals(2, EObjectUtil.getReferencedInstancesOf(hb20ModelRoot, testEReferenceMany_components, Identifiable.class, false).size());

		// *****************************************************
		// Context: Given object is NULL
		boolean flag = true;
		String message = "";
		try {
			EObjectUtil.getReferencedInstancesOf(null, testEReferenceMany_requiringports, Port.class, true);

		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				flag = false;
				message = ex.getMessage();
			}
		}
		assertTrue("Exception while owner EObject is Null:" + message, flag);

		flag = true;
		try {
			EObjectUtil.getReferencedInstancesOf(hb20Interface, null, Port.class, true);

		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				flag = false;
				message = ex.getMessage();
			}
		}
		assertTrue("Exception while EReference is Null:" + message, flag);

		flag = true;
		try {
			EObjectUtil.getReferencedInstancesOf(hb20Interface, testEReferenceMany_requiringports, null, true);

		} catch (Exception ex) {
			if (!(ex instanceof AssertionFailedException)) {
				flag = false;
				message = ex.getMessage();
			}
		}
		assertTrue("Exception while specified return Class is Null:" + message, flag);

	}

	/**
	 * Test method for {@link EObjectUtil#proxify(EObject, EStructuralFeature, EObject)}
	 */
	public void testProxify() {
		Resource resource20_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, false), false);

		Resource resource20_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, false), false);

		assertNotNull(resource20_2);
		assertFalse(resource20_2.getContents().isEmpty());
		assertEquals(1, resource20_2.getContents().size());
		Platform hb20Platform_A_2 = (Platform) resource20_2.getContents().get(0);
		assertNotNull(hb20Platform_A_2);
		assertEquals(2, hb20Platform_A_2.getComponentTypes().size());
		assertEquals(2, hb20Platform_A_2.getInterfaces().size());

		ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource20_2);

		ComponentType componentType1 = hb20Platform_A_2.getComponentTypes().get(0);
		URI compTypeUri1 = extendedResource.getURI(componentType1);
		Port port1 = componentType1.getPorts().get(0);
		URI portUri1 = extendedResource.getURI(port1);
		Port port2 = componentType1.getPorts().get(1);
		URI portUri2 = extendedResource.getURI(port2);

		ComponentType componentType2 = hb20Platform_A_2.getComponentTypes().get(1);
		URI compTypeUri2 = extendedResource.getURI(componentType2);

		Interface interface1 = hb20Platform_A_2.getInterfaces().get(0);
		Interface interface2 = hb20Platform_A_2.getInterfaces().get(1);

		assertFalse(interface1.getProvidingComponentTypes().isEmpty());
		for (ComponentType componentType : interface1.getProvidingComponentTypes()) {
			assertFalse(componentType.eIsProxy());
			assertTrue(hb20Platform_A_2.getComponentTypes().contains(componentType));
		}

		assertFalse(interface2.getRequiringPorts().isEmpty());
		for (Port port : interface2.getRequiringPorts()) {
			assertFalse(port.eIsProxy());
			assertTrue(componentType1.getPorts().contains(port));
		}

		assertFalse(resource20_3.getContents().isEmpty());
		Application hb20Application = (Application) resource20_3.getContents().get(0);
		assertNotNull(hb20Application);
		assertEquals(2, hb20Application.getComponents().size());
		Component referringComponent_1 = hb20Application.getComponents().get(0);
		assertNotNull(referringComponent_1.getType());
		assertFalse(referringComponent_1.getType().eIsProxy());
		assertTrue(hb20Platform_A_2.getComponentTypes().contains(referringComponent_1.getType()));

		Component referringComponent_2 = hb20Application.getComponents().get(1);
		assertNotNull(referringComponent_2.getType());
		assertFalse(referringComponent_2.getType().eIsProxy());
		assertTrue(hb20Platform_A_2.getComponentTypes().contains(referringComponent_2.getType()));

		// Proxify ComponentType
		EObject proxy = EObjectUtil.proxify(componentType1.eContainer(), componentType1.eContainmentFeature(), componentType1);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());
		proxy = EObjectUtil.proxify(componentType2.eContainer(), componentType2.eContainmentFeature(), componentType2);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());

		for (ComponentType componentType : interface1.getProvidingComponentTypes()) {
			assertTrue(componentType.eIsProxy());
			assertTrue(componentType instanceof InternalEObject);
			URI proxyUri = ((InternalEObject) componentType).eProxyURI();
			assertTrue(proxyUri.toString(), compTypeUri1.equals(proxyUri) || compTypeUri2.equals(proxyUri));
		}

		assertFalse(interface2.getRequiringPorts().isEmpty());
		for (Port port : interface2.getRequiringPorts()) {
			assertTrue(port.eIsProxy());
			assertTrue(port instanceof InternalEObject);
			URI proxyUri = ((InternalEObject) port).eProxyURI();
			assertTrue(portUri1.equals(proxyUri) || portUri2.equals(proxyUri));
		}
		// Verify resource20_3 contents
		assertNotNull(referringComponent_1.getType());
		assertTrue(referringComponent_1.getType().eIsProxy());
		assertTrue(referringComponent_1.getType() instanceof InternalEObject);
		URI proxyUri = ((InternalEObject) referringComponent_1.getType()).eProxyURI();
		assertTrue(proxyUri.toString(), compTypeUri1.equals(proxyUri) || compTypeUri2.equals(proxyUri));

		assertNotNull(referringComponent_2.getType());
		assertTrue(referringComponent_2.getType().eIsProxy());
		assertTrue(referringComponent_2.getType() instanceof InternalEObject);
		proxyUri = ((InternalEObject) referringComponent_2.getType()).eProxyURI();
		assertTrue(proxyUri.toString(), compTypeUri1.equals(proxyUri) || compTypeUri2.equals(proxyUri));

		// Context: container is Null
		proxy = EObjectUtil.proxify(null, componentType1.eContainingFeature(), componentType1);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());
		// Context: feature is NULL
		proxy = EObjectUtil.proxify(hb20Platform_A_2, null, componentType2);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());

		// Proxify object with valid arguments
		URI expectedUri = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource20_3).getURI(referringComponent_1);
		proxy = EObjectUtil.proxify(referringComponent_1.eContainer(), referringComponent_1.eContainingFeature(), referringComponent_1);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());
		assertTrue(referringComponent_1.eIsProxy());
		proxyUri = ((InternalEObject) referringComponent_1).eProxyURI();
		assertEquals(expectedUri, proxyUri);

		// Context: eobject is null
		proxy = EObjectUtil.proxify(componentType2.eContainer(), componentType2.eContainmentFeature(), null);
		assertNull(proxy);
	}

	/**
	 * Test method for {@link EObjectUtil#deproxify(EObject)}
	 * 
	 * @throws InterruptedException
	 * @throws OperationCanceledException
	 */
	public void testDeproxify() throws OperationCanceledException, InterruptedException {
		Resource resource20_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, false), false);

		Resource resource20_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, false), false);

		assertNotNull(resource20_2);
		assertFalse(resource20_2.getContents().isEmpty());
		assertEquals(1, resource20_2.getContents().size());
		Platform hb20Platform_A_2 = (Platform) resource20_2.getContents().get(0);
		assertNotNull(hb20Platform_A_2);
		assertEquals(2, hb20Platform_A_2.getComponentTypes().size());
		assertEquals(2, hb20Platform_A_2.getInterfaces().size());

		ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource20_2);

		ComponentType componentType1 = hb20Platform_A_2.getComponentTypes().get(0);
		URI compTypeUri1 = extendedResource.getURI(componentType1);
		Port port1 = componentType1.getPorts().get(0);
		URI portUri1 = extendedResource.getURI(port1);
		Port port2 = componentType1.getPorts().get(1);
		URI portUri2 = extendedResource.getURI(port2);

		ComponentType componentType2 = hb20Platform_A_2.getComponentTypes().get(1);
		URI compTypeUri2 = extendedResource.getURI(componentType2);

		Interface interface1 = hb20Platform_A_2.getInterfaces().get(0);
		Interface interface2 = hb20Platform_A_2.getInterfaces().get(1);

		assertFalse(resource20_3.getContents().isEmpty());
		Application hb20Application = (Application) resource20_3.getContents().get(0);
		assertNotNull(hb20Application);
		assertEquals(2, hb20Application.getComponents().size());
		Component referringComponent_1 = hb20Application.getComponents().get(0);
		assertNotNull(referringComponent_1.getType());

		Component referringComponent_2 = hb20Application.getComponents().get(1);
		assertNotNull(referringComponent_2.getType());

		// Proxify ComponentType
		EObject proxy = EObjectUtil.proxify(componentType1.eContainer(), componentType1.eContainmentFeature(), componentType1);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());
		proxy = EObjectUtil.proxify(componentType2.eContainer(), componentType2.eContainmentFeature(), componentType2);
		assertNotNull(proxy);
		assertTrue(proxy.eIsProxy());

		for (ComponentType componentType : interface1.getProvidingComponentTypes()) {
			assertTrue(componentType.eIsProxy());
			assertTrue(componentType instanceof InternalEObject);
			URI proxyUri = ((InternalEObject) componentType).eProxyURI();
			assertTrue(proxyUri.toString(), compTypeUri1.equals(proxyUri) || compTypeUri2.equals(proxyUri));
		}

		assertFalse(interface2.getRequiringPorts().isEmpty());
		for (Port port : interface2.getRequiringPorts()) {
			assertTrue(port.eIsProxy());
			assertTrue(port instanceof InternalEObject);
			URI proxyUri = ((InternalEObject) port).eProxyURI();
			assertTrue(portUri1.equals(proxyUri) || portUri2.equals(proxyUri));
		}
		// Verify resource20_3 contents
		assertNotNull(referringComponent_1.getType());
		assertTrue(referringComponent_1.getType().eIsProxy());
		assertTrue(referringComponent_1.getType() instanceof InternalEObject);
		URI proxyUri = ((InternalEObject) referringComponent_1.getType()).eProxyURI();
		assertTrue(proxyUri.toString(), compTypeUri1.equals(proxyUri) || compTypeUri2.equals(proxyUri));

		assertNotNull(referringComponent_2.getType());
		assertTrue(referringComponent_2.getType().eIsProxy());
		assertTrue(referringComponent_2.getType() instanceof InternalEObject);
		proxyUri = ((InternalEObject) referringComponent_2.getType()).eProxyURI();
		assertTrue(proxyUri.toString(), compTypeUri1.equals(proxyUri) || compTypeUri2.equals(proxyUri));

		// Deproxify
		assertNotNull(EObjectUtil.deproxify(componentType1));
		assertNotNull(EObjectUtil.deproxify(componentType2));

		assertFalse(interface1.getProvidingComponentTypes().isEmpty());
		for (ComponentType componentType : interface1.getProvidingComponentTypes()) {
			assertFalse(componentType.eIsProxy());
			assertTrue(hb20Platform_A_2.getComponentTypes().contains(componentType));
		}
		assertFalse(interface2.getRequiringPorts().isEmpty());
		for (Port port : interface2.getRequiringPorts()) {
			assertFalse(port.eIsProxy());
			assertTrue(componentType1.getPorts().contains(port));
		}

		assertNotNull(referringComponent_2.getType());
		assertFalse(referringComponent_2.getType().eIsProxy());
		assertTrue(hb20Platform_A_2.getComponentTypes().contains(referringComponent_2.getType()));

		assertNotNull(referringComponent_1.getType());
		assertFalse(referringComponent_1.getType().eIsProxy());
		assertTrue(hb20Platform_A_2.getComponentTypes().contains(referringComponent_1.getType()));
		waitForModelLoading();

	}

	/**
	 * Test methods for {@link EObjectUtil#getInverseReferences(EObject, boolean)}
	 * 
	 * @throws ExecutionException
	 * @throws OperationCanceledException
	 */
	public void initTestData() {
		Resource resource20A_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_2, false), false);

		assertNotNull(resource20A_2);
		assertFalse(resource20A_2.getContents().isEmpty());
		assertEquals(1, resource20A_2.getContents().size());
		Platform platform20A_2 = (Platform) resource20A_2.getContents().get(0);
		assertNotNull(platform20A_2);
		assertEquals(2, platform20A_2.getComponentTypes().size());
		assertEquals(2, platform20A_2.getInterfaces().size());

		componentType20A_2_1 = platform20A_2.getComponentTypes().get(0);
		assertFalse(componentType20A_2_1.getPorts().isEmpty());
		port20A_2_1 = componentType20A_2_1.getPorts().get(0);
		port20A_2_2 = componentType20A_2_1.getPorts().get(1);

		componentType20A_2_2 = platform20A_2.getComponentTypes().get(1);
		// ---------------------------------
		Resource resource20_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_3, false), false);

		assertNotNull(resource20_3);
		assertFalse(resource20_3.getContents().isEmpty());
		assertEquals(1, resource20_3.getContents().size());
		Application application_20A_3 = (Application) resource20_3.getContents().get(0);
		assertNotNull(application_20A_3);
		assertEquals(2, application_20A_3.getComponents().size());

		component20A_3_1 = application_20A_3.getComponents().get(0);
		component20A_3_2 = application_20A_3.getComponents().get(1);
		// ---------------------------------
		Resource resource21A_4 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_21_20A_4, false), false);

		assertNotNull(resource21A_4);
		assertFalse(resource21A_4.getContents().isEmpty());
		assertEquals(1, resource21A_4.getContents().size());
		Application application_21A_4 = (Application) resource21A_4.getContents().get(0);
		assertNotNull(application_21A_4);
		assertEquals(2, application_21A_4.getComponents().size());

		component21A_4_1 = application_21A_4.getComponents().get(0);
		// ---------------------------------
		// hbfile20D_2
		Resource resource20D_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20D_2, false), false);

		assertNotNull(resource20D_2);
		assertFalse(resource20D_2.getContents().isEmpty());
		assertEquals(1, resource20D_2.getContents().size());
		Platform platform20D_2 = (Platform) resource20D_2.getContents().get(0);
		assertNotNull(platform20D_2);
		assertEquals(2, platform20D_2.getComponentTypes().size());

		componentType20D_2_1 = platform20D_2.getComponentTypes().get(0);
		assertFalse(componentType20D_2_1.getPorts().isEmpty());
		port20D_2_1 = componentType20D_2_1.getPorts().get(0);
		port20D_2_2 = componentType20D_2_1.getPorts().get(1);

		componentType20D_2_2 = platform20D_2.getComponentTypes().get(1);
		// -----------------
		// hbfile20D_3
		Resource resource20D_3 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20D_3, false), false);

		assertNotNull(resource20D_3);
		assertFalse(resource20D_3.getContents().isEmpty());
		assertEquals(1, resource20D_3.getContents().size());
		Application application_20D_3 = (Application) resource20D_3.getContents().get(0);
		assertNotNull(application_20D_3);
		assertEquals(2, application_20D_3.getComponents().size());

		component20D_3_1 = application_20D_3.getComponents().get(0);
		// ----------------------
		// hbfile20E_1
		Resource resource20E_1 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_E + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20E_1, false), false);

		assertNotNull(resource20E_1);
		assertFalse(resource20E_1.getContents().isEmpty());
		assertEquals(1, resource20E_1.getContents().size());
		Application application_20E_1 = (Application) resource20E_1.getContents().get(0);
		assertNotNull(application_20E_1);
		assertEquals(2, application_20E_1.getComponents().size());

		component20E_1_1 = application_20E_1.getComponents().get(0);
	}

	public void testGetInverseReferences_StandAloneProject() throws OperationCanceledException, ExecutionException {
		initTestData();

		Collection<EStructuralFeature.Setting> references;

		references = EObjectUtil.getInverseReferences(componentType20A_2_1, true);
		assertFalse(references.isEmpty());
		List<EObject> objects = getEObjects(references);
		assertEquals(5, objects.size());
		assertTrue(objects.contains(componentType20A_2_1.eContainer()));
		assertTrue(objects.contains(component21A_4_1));
		assertTrue(objects.contains(component20A_3_1));
		assertFalse(objects.contains(component20A_3_2));
		assertTrue(objects.contains(port20A_2_1));
		assertTrue(objects.contains(port20A_2_2));

		assertFalse(objects.contains(component20D_3_1));
		assertFalse(objects.contains(componentType20D_2_1));
		assertFalse(objects.contains(componentType20D_2_2));
		assertFalse(objects.contains(component20E_1_1));
		assertFalse(objects.contains(port20D_2_2));
		assertFalse(objects.contains(port20D_2_1));

		assertFalse(objects.contains(componentType20A_2_2));
		// ---------------------
		references.clear();
		references = EObjectUtil.getInverseReferences(port20A_2_1, true);
		assertFalse(references.isEmpty());
		objects = getEObjects(references);
		assertTrue(objects.contains(componentType20A_2_1));

		assertFalse(objects.contains(componentType20A_2_1.eContainer()));
		assertFalse(objects.contains(componentType20A_2_2));
		assertFalse(objects.contains(component21A_4_1));
		assertFalse(objects.contains(component20A_3_1));
		assertFalse(objects.contains(component20A_3_2));
		assertFalse(objects.contains(port20A_2_1));
		assertFalse(objects.contains(port20A_2_2));

	}

	public void testGetInverseReferences_ProjectWithReferences() throws OperationCanceledException, ExecutionException {
		initTestData();
		Collection<EStructuralFeature.Setting> references;

		assertNotNull(componentType20D_2_1.eContainer());
		assertNotNull(componentType20D_2_1.eResource());

		references = EObjectUtil.getInverseReferences(componentType20D_2_1, true);
		assertFalse(references.isEmpty());
		List<EObject> objects = getEObjects(references);

		assertFalse(objects.contains(componentType20D_2_2));

		assertFalse(objects.contains(component21A_4_1));
		assertFalse(objects.contains(component20A_3_1));
		assertFalse(objects.contains(port20A_2_1));
		assertFalse(objects.contains(port20A_2_2));

		EObject container = componentType20D_2_1.eContainer();
		assertNotNull(container);
		assertTrue(objects.contains(container));
		assertTrue(objects.contains(component20D_3_1));
		assertTrue(objects.contains(component20E_1_1));
		assertTrue(objects.contains(port20D_2_2));
		assertTrue(objects.contains(port20D_2_1));
		assertEquals(5, objects.size());

	}

	public void testGetInverseReferences_Proxy() throws OperationCanceledException, ExecutionException {
		Collection<EStructuralFeature.Setting> references;

		initTestData();
		// ==============================================
		// Containment
		Runnable runnable1 = new Runnable() {
			public void run() {
				EcoreUtil.delete(port20D_2_1);
			}
		};
		WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, runnable1, "remove componentType");
		waitForModelLoading();

		assertTrue(port20D_2_1.eIsProxy());
		// deleted element was removed

		// with resolved = false
		references = EObjectUtil.getInverseReferences(componentType20D_2_1, true);
		List<EObject> objects = getEObjects(references);
		assertEquals(4, objects.size());
		EObject container = componentType20D_2_1.eContainer();
		assertNotNull(container);
		assertTrue(objects.contains(container));
		assertTrue(objects.contains(component20D_3_1));
		assertTrue(objects.contains(component20E_1_1));
		assertFalse(objects.contains(port20D_2_1));
		assertTrue(objects.contains(port20D_2_2));

		// with resolved = false
		references = EObjectUtil.getInverseReferences(componentType20D_2_1, false);
		objects = getEObjects(references);
		assertTrue(objects.contains(component20D_3_1));
		assertTrue(objects.contains(component20E_1_1));
		assertFalse(objects.contains(port20D_2_1));
		assertTrue(objects.contains(port20D_2_2));
		// ----------------------
		// References
		Runnable runnable2 = new Runnable() {
			public void run() {
				EcoreUtil.delete(component20D_3_1);
				EcoreUtil.delete(component20E_1_1);
			}
		};
		WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, runnable2, "remove component referecing to context object");
		waitForModelLoading();

		assertTrue(component20D_3_1.eIsProxy());
		assertTrue(component20E_1_1.eIsProxy());

		// with resolved = true
		references.clear();
		references = EObjectUtil.getInverseReferences(componentType20D_2_1, true);
		objects = getEObjects(references);
		assertEquals(4, objects.size());
		assertTrue(objects.contains(container));
		assertTrue(objects.contains(component20D_3_1));
		assertTrue(objects.contains(component20E_1_1));
		assertFalse(objects.contains(port20D_2_1));
		assertTrue(objects.contains(port20D_2_2));

		// with resolved = false
		references.clear();
		references = EObjectUtil.getInverseReferences(componentType20D_2_1, false);
		objects = getEObjects(references);
		assertEquals(4, objects.size());
		assertTrue(objects.contains(container));
		assertTrue(objects.contains(component20D_3_1));
		assertTrue(objects.contains(component20E_1_1));
		assertFalse(objects.contains(port20D_2_1));
		assertTrue(objects.contains(port20D_2_2));

	}

	public void testGetInverseReferences_ResourceSetIsNull() throws OperationCanceledException, ExecutionException {
		initTestData();

		// -------------------------------------------
		// Context Object is NULL
		Collection<EStructuralFeature.Setting> references = EObjectUtil.getInverseReferences(null, true);
		assertNotNull(references);
		assertTrue(references.isEmpty());

		// -------------------------------------------
		// Resource is NOT NULL, but it does not belong to any ResourceSet
		final Resource resource20D_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20D_2, false), false);

		assertNotNull(resource20D_2);
		assertFalse(resource20D_2.getContents().isEmpty());

		final ResourceSet resourceSet = refWks.editingDomain20.getResourceSet();
		final EObject modelRoot = componentType20D_2_1.eContainer();
		assertNotNull(modelRoot);

		Runnable runnable0 = new Runnable() {
			public void run() {
				resourceSet.getResources().remove(resource20D_2);
			}
		};

		WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, runnable0, "remove componentType");
		waitForModelLoading();

		assertNotNull(componentType20D_2_1.eResource());
		assertNull(componentType20D_2_1.eResource().getResourceSet());

		references = EObjectUtil.getInverseReferences(componentType20D_2_1, true);
		List<EObject> objects = getEObjects(references);
		assertEquals(5, objects.size());

		assertFalse(objects.contains(componentType20D_2_2));
		assertTrue(objects.contains(modelRoot));
		assertTrue(objects.contains(port20D_2_2));
		assertTrue(objects.contains(port20D_2_1));

		assertFalse(objects.contains(component21A_4_1));
		assertFalse(objects.contains(component20A_3_1));
		assertFalse(objects.contains(port20A_2_1));
		assertFalse(objects.contains(port20A_2_2));

		assertTrue(objects.contains(component20D_3_1));
		assertTrue(objects.contains(component20E_1_1));
		assertFalse(objects.contains(componentType20D_2_2));
	}

	public void testGetInverseReferences_ModelRootIsNull() throws OperationCanceledException, ExecutionException {
		initTestData();
		Collection<EStructuralFeature.Setting> references;
		Resource resource20D_2 = refWks.editingDomain20.getResourceSet().getResource(
				URI.createPlatformResourceURI(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D + "/"
						+ DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20D_2, false), false);

		assertNotNull(resource20D_2);

		// Context Object is NOT NULL. But Model Root is NULL;
		final EObject modelRoot = componentType20D_2_1.eContainer();
		assertNotNull(modelRoot);

		Runnable runnable2 = new Runnable() {
			public void run() {
				EcoreUtil.delete(componentType20D_2_1);
			}
		};

		WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, runnable2, "remove componentType");
		waitForModelLoading();

		references = EObjectUtil.getInverseReferences(componentType20D_2_1, true);
		assertFalse(references.isEmpty());
		List<EObject> objects = getEObjects(references);
		assertEquals(2, objects.size());
		assertFalse(objects.contains(componentType20D_2_2));
		assertFalse(objects.contains(modelRoot));
		assertTrue(objects.contains(port20D_2_2));
		assertTrue(objects.contains(port20D_2_1));

		assertFalse(objects.contains(component21A_4_1));
		assertFalse(objects.contains(component20A_3_1));
		assertFalse(objects.contains(port20A_2_1));
		assertFalse(objects.contains(port20A_2_2));

		assertFalse(objects.contains(component20D_3_1));
		assertFalse(objects.contains(componentType20D_2_2));
		assertFalse(objects.contains(component20E_1_1));

		EcoreResourceUtil.unloadResource(resource20D_2);

	}

	public List<EObject> getEObjects(Collection<EStructuralFeature.Setting> settings) {
		List<EObject> objects = new ArrayList<EObject>();
		if (settings != null) {
			for (Setting setting : settings) {
				if (setting.getEObject() != null) {
					objects.add(setting.getEObject());
				}
			}
		}
		return objects;
	}
}