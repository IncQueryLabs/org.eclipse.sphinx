/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 BMW Car IT, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BMW Car IT - Initial API and implementation
 *     itemis - [409367] Add a custom URI scheme to metamodel descriptor allowing mapping URI scheme to metamodel descriptor
 *
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.metamodel;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1MM;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release100;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release101;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release200;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test2MM;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test2Release100;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MetaModelDescriptorsEP;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockEPackage;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockExtensionRegistry;

@SuppressWarnings("nls")
public class MetaModelDescriptorRegistryTest extends TestCase {

	private MetaModelDescriptorsEP fExtPoint;

	private MockExtensionRegistry fExtensionRegistry;

	private MetaModelDescriptorRegistry fRegistryUT;

	@Override
	protected void setUp() throws Exception {
		fExtensionRegistry = new MockExtensionRegistry();
		fExtPoint = new MetaModelDescriptorsEP();
		fExtensionRegistry.addExtensionPoint(fExtPoint);
		fRegistryUT = MetaModelDescriptorRegistry.INSTANCE;
		fRegistryUT.setExtensionRegistry(fExtensionRegistry);
	}

	@Override
	protected void tearDown() throws Exception {
		MetaModelDescriptorRegistry.INSTANCE.setExtensionRegistry(null);
		fExtPoint.clear();
	}

	public void testGetDescriptors() {
		registerWithRegistryUT();
		List<? extends IMetaModelDescriptor> descriptors = fRegistryUT.getDescriptors(new Test1MM());
		assertEmptyList(descriptors);

		registerWithRegistryUT(Test1Release100.INSTANCE);
		descriptors = fRegistryUT.getDescriptors(Test1Release100.INSTANCE);
		assertNotNull(descriptors);
		assertEquals(1, descriptors.size());
		assertTrue(descriptors.contains(Test1Release100.INSTANCE));

		descriptors = fRegistryUT.getDescriptors(Test2MM.INSTANCE);
		assertEmptyList(descriptors);

		registerWithRegistryUT(Test2MM.INSTANCE);
		descriptors = fRegistryUT.getDescriptors(Test2MM.INSTANCE);
		assertNotNull(descriptors);
		assertEquals(1, descriptors.size());
		assertTrue(descriptors.contains(Test2MM.INSTANCE));

		registerWithRegistryUT(Test1Release100.INSTANCE, Test1MM.INSTANCE);
		descriptors = fRegistryUT.getDescriptors(Test1MM.INSTANCE);
		assertDescriptors(descriptors, Test1Release100.INSTANCE, Test1MM.INSTANCE);

		registerWithRegistryUT(Test1Release100.INSTANCE, Test1MM.INSTANCE);
		descriptors = fRegistryUT.getDescriptors(Test1Release100.INSTANCE);
		assertDescriptors(descriptors, Test1Release100.INSTANCE);

		registerWithRegistryUT(Test1Release100.INSTANCE, Test2MM.INSTANCE);
		descriptors = fRegistryUT.getDescriptors(MetaModelDescriptorRegistry.ANY_MM);
		assertDescriptors(descriptors, Test1Release100.INSTANCE, Test2MM.INSTANCE);

		registerWithRegistryUT(Test1Release100.INSTANCE, Test1MM.INSTANCE);
		descriptors = fRegistryUT.getDescriptors((IMetaModelDescriptor) null);
		assertEmptyList(descriptors);
	}

	private void assertDescriptors(List<? extends IMetaModelDescriptor> descriptors, IMetaModelDescriptor... expDescriptors) {
		assertNotNull(descriptors);
		assertEquals(expDescriptors.length, descriptors.size());
		for (IMetaModelDescriptor expDescriptor : expDescriptors) {
			descriptors.contains(expDescriptor);
		}
	}

	public void testGetResolvedDescriptors() {
		registerWithRegistryUT();
		List<IMetaModelDescriptor> resolvedReleases = fRegistryUT.getResolvedDescriptors(new Test1MM());
		assertEmptyList(resolvedReleases);

		registerWithRegistryUT(Test1Release100.INSTANCE);
		resolvedReleases = fRegistryUT.getResolvedDescriptors(new Test1MM());
		assertNotNull(resolvedReleases);
		if (Test1Release100.INSTANCE.getRootEPackage() == null) {
			assertEquals(0, resolvedReleases.size());
		} else {
			assertEquals(1, resolvedReleases.size());
			assertTrue(resolvedReleases.contains(Test1Release100.INSTANCE));
		}

		EPackage ePkg = Test1MM.MOCK_EPKG_REGISTRY.getEPackage(Test1Release100.INSTANCE.getNamespaceURI().toString());
		if (ePkg == null) {
			ePkg = new MockEPackage();
			Test1MM.MOCK_EPKG_REGISTRY.registerEPackage(Test1Release100.INSTANCE.getNamespaceURI(), ePkg);
		}
		resolvedReleases = fRegistryUT.getResolvedDescriptors(new Test1MM());
		assertNotNull(resolvedReleases);
		assertEquals(1, resolvedReleases.size());
		assertTrue(resolvedReleases.contains(Test1Release100.INSTANCE));
	}

	public void testGetDescriptorByOrdinal() {
		registerWithRegistryUT();
		IMetaModelDescriptor version = fRegistryUT.getDescriptor(Test1MM.INSTANCE, Test1Release100.ORDINAL);
		assertNull(version);

		registerWithRegistryUT(Test1Release100.INSTANCE);
		version = fRegistryUT.getDescriptor(Test1MM.INSTANCE, Test1Release100.ORDINAL);
		assertEquals(Test1Release100.INSTANCE, version);

		version = fRegistryUT.getDescriptor(Test1MM.INSTANCE, Test1Release200.ORDINAL);
		assertNull(version);
	}

	public void testGetDescriptorByName() {
		registerWithRegistryUT();
		IMetaModelDescriptor version = fRegistryUT.getDescriptor(Test1MM.INSTANCE, Test1Release100.NAME);
		assertNull(version);

		registerWithRegistryUT(Test1Release100.INSTANCE);
		version = fRegistryUT.getDescriptor(Test1MM.INSTANCE, Test1Release100.NAME);
		assertEquals(Test1Release100.INSTANCE, version);

		version = fRegistryUT.getDescriptor(Test1MM.INSTANCE, Test1Release200.NAME);
		assertNull(version);
	}

	public void testGetDescriptorByNamespace() {
		registerWithRegistryUT();
		IMetaModelDescriptor version = fRegistryUT.getDescriptor(Test1Release100.INSTANCE.getNamespaceURI());
		assertNull(version);

		registerWithRegistryUT(Test1Release100.INSTANCE);
		version = fRegistryUT.getDescriptor(Test1Release100.INSTANCE.getNamespaceURI());
		assertEquals(Test1Release100.INSTANCE, version);

		version = fRegistryUT.getDescriptor(Test1MM.INSTANCE.getNamespaceURI());
		assertNull(version);
	}

	public void testGetDescriptorByUri() {
		registerWithRegistryUT(Test1MM.INSTANCE, Test1Release200.INSTANCE, Test1Release100.INSTANCE, Test2MM.INSTANCE, Test2Release100.INSTANCE);
		// Input match MM Namespace
		URI uri = URI.create("http://testA.sphinx.org");
		IMetaModelDescriptor metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Test1MM.INSTANCE, metaModelDescriptor);

		uri = URI.create("http://testA.sphinx.org/2.0.0");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Test1Release200.INSTANCE, metaModelDescriptor);

		uri = URI.create("http://testA.sphinx.org/1.0.0");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Test1Release100.INSTANCE, metaModelDescriptor);
		// Input match EPackage NS URI pattern
		uri = URI.create("http://testA.sphinx.org/2.0.0/1");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Test1Release200.INSTANCE, metaModelDescriptor);

		uri = URI.create("http://testA.sphinx.org/2.0.1");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNull(metaModelDescriptor);

		uri = URI.create("http://testA.sphinx.org/2.0.0/1a");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNull(metaModelDescriptor);

		uri = URI.create("2.0.0");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNull(metaModelDescriptor);
		// Input match compatible namespace
		// Input matches compatible resource version desciptors
		uri = URI.create("http://testA.sphinx.org/1.0.1");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Test1Release100.INSTANCE, metaModelDescriptor);

		uri = URI.create("http://testA.sphinx.org/1.0.2");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Test1Release100.INSTANCE, metaModelDescriptor);

		// Input Uri is address of ecore model without modelDescriptor
		// New metamodel descriptor is created and add to MetaModelDescriptor
		uri = URI.create(Hummingbird10Package.eNS_URI);
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNotNull(metaModelDescriptor);
		assertEquals(Hummingbird10MMDescriptor.INSTANCE.getNamespaceURI(), metaModelDescriptor.getNamespaceURI());

		// Input Uri is un-existing
		uri = URI.create("http://testA.sphinx.org/3.0.0");
		metaModelDescriptor = fRegistryUT.getDescriptor(uri);
		assertNull(metaModelDescriptor);
		// ====================
		// Null input
		URI nullUri = null;
		assertNull(fRegistryUT.getDescriptor(nullUri));
	}

	public void testCompareDescriptor() {

		Test1MM version100 = Test1Release100.INSTANCE;
		Test1MM version101 = Test1Release101.INSTANCE;
		Test1MM version200 = Test1Release200.INSTANCE;

		int result;
		result = version100.compareTo(version101);
		assertEquals(-1, result);
		result = version101.compareTo(version200);
		assertEquals(-1, result);
		result = version200.compareTo(version100);
		assertEquals(1, result);

	}

	public void testGetDescriptorsFromURIScheme() {
		List<IMetaModelDescriptor> targetMMDescriptors = new ArrayList<IMetaModelDescriptor>();

		// Register two meta model descriptors
		registerWithRegistryUT(Test1MM.INSTANCE, Test2MM.INSTANCE);
		// Get meta model descriptors that use URIs with given scheme "tr1" as proxy URIs.
		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI("tr1:/#test1Release.sphinx.org/scheme", true);
		String uriScheme = uri.scheme();
		assertEquals(uriScheme, "tr1");
		List<IMetaModelDescriptor> mmDescriptors = fRegistryUT.getDescriptorsFromURIScheme(uriScheme);
		targetMMDescriptors.add(Test1MM.INSTANCE);
		assertEquals(targetMMDescriptors, mmDescriptors);

		// Get meta model descriptors that use URIs with given scheme "tr2" as proxy URIs.
		uri = org.eclipse.emf.common.util.URI.createURI("tr2:/#test2Release.sphinx.org/scheme", true);
		uriScheme = uri.scheme();
		assertEquals(uriScheme, "tr2");
		mmDescriptors = fRegistryUT.getDescriptorsFromURIScheme(uriScheme);
		targetMMDescriptors.clear();
		targetMMDescriptors.add(Test2MM.INSTANCE);
		assertEquals(targetMMDescriptors, mmDescriptors);

		// Register three meta model descriptors
		registerWithRegistryUT(Test1MM.INSTANCE, Test1Release100.INSTANCE, Test2MM.INSTANCE);
		// Get meta model descriptors that use URIs with given scheme "tr1" as proxy URIs.
		uri = org.eclipse.emf.common.util.URI.createURI("tr1:/#test1Release100.sphinx.org/scheme", true);
		uriScheme = uri.scheme();
		assertEquals(uriScheme, "tr1");
		mmDescriptors = fRegistryUT.getDescriptorsFromURIScheme(uriScheme);
		targetMMDescriptors.clear();
		targetMMDescriptors.add(Test1MM.INSTANCE);
		targetMMDescriptors.add(Test1Release100.INSTANCE);
		assertEquals(targetMMDescriptors, mmDescriptors);
	}

	private void registerWithRegistryUT(IMetaModelDescriptor... descriptors) {
		fExtPoint.clear();
		for (IMetaModelDescriptor descriptor : descriptors) {
			fExtPoint.registerDescriptor(descriptor);
		}
		fRegistryUT.setExtensionRegistry(fExtensionRegistry);
	}

	private void assertEmptyList(List<? extends IMetaModelDescriptor> versions) {
		assertNotNull(versions);
		assertTrue(versions.isEmpty());
	}
}
