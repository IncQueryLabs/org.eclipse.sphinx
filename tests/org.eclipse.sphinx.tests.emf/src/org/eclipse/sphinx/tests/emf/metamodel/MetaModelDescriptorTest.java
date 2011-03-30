/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.metamodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1MM;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release100;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release101;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release200;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test2Release100;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockEFactory;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockEPackage;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockEPkgRegistry;

@SuppressWarnings("nls")
public class MetaModelDescriptorTest extends TestCase {

	/**
	 * Tests if the constructor for base descriptors (without MetaModelData) is implemented correctly. The correct
	 * checking of the constructor's parameters is tested.
	 */
	public void testBaseCreation() {
		// Test exception throw on null identifier.
		try {
			new NewDescriptor(null, Test1MM.NS);
			fail("An AssertionFailedException was expected as the Identifier shall not be null.");
		} catch (AssertionFailedException afe) {
		}
		// Test exception throw on null namespace.
		try {
			new NewDescriptor(Test1MM.ID, null);
			fail("An AssertionFailedException was expected as the namespace shall not be null.");
		} catch (AssertionFailedException afe) {
		}
		// Test correct creation.
		try {
			new NewDescriptor(Test1MM.ID, Test1MM.NS);
		} catch (Exception e) {
			fail("No exception was expected.");
		}
	}

	/**
	 * Tests if the constructor for version descriptors (with MetaModelData) is implemented correctly. The correct
	 * checking of the constructor's parameters is tested.
	 */
	public void testVersionCreation() {
		// Test exception throw on null identifier.
		try {
			new NewDescriptor(null, Test1MM.NS, new MetaModelVersionData("nspostfix", "pattern", "name", 0));
			fail("An AssertionFailedException was expected as the Identifier shall not be null.");
		} catch (AssertionFailedException afe) {
		}
		// Test exception throw on null namespace.
		try {
			new NewDescriptor(Test1MM.ID, null, new MetaModelVersionData("nspostfix", "pattern", "name", 0));
			fail("An AssertionFailedException was expected as the namespace shall not be null.");
		} catch (AssertionFailedException afe) {
		}
		// Test exception throw on illegal namespace postfix in MetaModelVersion
		try {
			MetaModelVersionData data = new MetaModelVersionData("ILLEGAL NS POSTFIX", "pattern", "Release 1.0.0", 100);
			new NewDescriptor(Test1MM.ID, Test1MM.NS, data);
			fail("A WrappedException was expected as the nsPostfix of the MetamodelReleaseData contains illegal characters.");
		} catch (WrappedException we) {
		}
		// Test creation with null MetaModelVersion
		try {
			new NewDescriptor(Test1MM.ID, Test1MM.NS, null);
		} catch (Exception afe) {
			fail("No Exception was expected as the MetamodelData may be set to null.");
		}
		// Test correct creation.
		try {
			new NewDescriptor(Test1MM.ID, Test1MM.NS, new MetaModelVersionData("nspostfix", "pattern", "name", 0));
		} catch (Exception e) {
			fail("No exception was expected.");
		}
	}

	public void testGetIdentifier() {
		assertEquals(Test1MM.ID, Test1MM.INSTANCE.getIdentifier());
		assertEquals(Test1Release100.ID, Test1Release100.INSTANCE.getIdentifier());
	}

	public void testGetNamespace() throws URISyntaxException {
		assertEquals(new URI("http://testA.sphinx.org/1.0.0"), Test1Release100.INSTANCE.getNamespaceURI());
		assertEquals(new URI("http://testA.sphinx.org/2.0.0"), Test1Release200.INSTANCE.getNamespaceURI());
		assertEquals(new URI("http://testB.sphinx.org/1.0.0"), Test2Release100.INSTANCE.getNamespaceURI());
		assertEquals(new URI(Test1MM.NS), Test1MM.INSTANCE.getNamespaceURI());
	}

	public void testEquals() {
		assertFalse(Test1Release100.INSTANCE.equals(null));
		assertFalse(Test1Release100.INSTANCE.equals(new Object()));
		assertFalse(Test1Release100.INSTANCE.equals(Test1Release200.INSTANCE));
		assertFalse(Test1Release100.INSTANCE.equals(Test2Release100.INSTANCE));
		assertFalse(Test1Release100.INSTANCE.equals(Test1MM.INSTANCE));
		assertTrue(Test1Release100.INSTANCE.equals(Test1Release100.INSTANCE));
		assertTrue(Test1Release100.INSTANCE.equals(new Test1Release100()));
		assertTrue(Test1MM.INSTANCE.equals(Test1MM.INSTANCE));
		assertTrue(Test1MM.INSTANCE.equals(new Test1MM()));
	}

	public void testHashCode() throws URISyntaxException {
		assertFalse(Test1Release100.INSTANCE.hashCode() == Test2Release100.INSTANCE.hashCode());
		assertFalse(Test1Release100.INSTANCE.hashCode() == Test1Release200.INSTANCE.hashCode());
		assertFalse(Test1Release100.INSTANCE.hashCode() == Test1MM.INSTANCE.hashCode());
		assertTrue(Test1Release100.INSTANCE.hashCode() == new Test1Release100().hashCode());
		assertTrue(Test1MM.INSTANCE.hashCode() == Test1MM.INSTANCE.hashCode());
	}

	public void testGetRootEPackage() {
		assertNull(Test1Release100.INSTANCE.getRootEPackage());

		MockEPkgRegistry ePkgRegistry = Test1MM.MOCK_EPKG_REGISTRY;
		EPackage ePkg = ePkgRegistry.getEPackage(Test1Release100.INSTANCE.getNamespaceURI().toString());
		if (ePkg == null) {
			ePkg = new MockEPackage();
			ePkgRegistry.registerEPackage(Test1Release100.INSTANCE.getNamespaceURI(), ePkg);
		}
		assertEquals(ePkg, Test1Release100.INSTANCE.getRootEPackage());
		// Get EPackage from the compatible MetaModelDescriptors
		assertEquals(ePkg, Test1Release101.INSTANCE.getRootEPackage());

		assertNull(Test1MM.INSTANCE.getRootEPackage());

		ePkg = ePkgRegistry.getEPackage(Test1MM.INSTANCE.getNamespaceURI().toString());
		if (ePkg == null) {
			ePkg = new MockEPackage();
			ePkgRegistry.registerEPackage(Test1MM.INSTANCE.getNamespaceURI(), ePkg);
		}
		assertEquals(ePkg, Test1MM.INSTANCE.getRootEPackage());
	}

	public void testGetEPackages() {
		Collection<EPackage> ePackages = Hummingbird10MMDescriptor.INSTANCE.getEPackages();
		assertEquals(1, ePackages.size());
		assertTrue(ePackages.contains(Hummingbird10Package.eINSTANCE));

		ePackages = Hummingbird20MMDescriptor.INSTANCE.getEPackages();
		assertEquals(3, ePackages.size());
		assertTrue(ePackages.contains(Common20Package.eINSTANCE));
		assertTrue(ePackages.contains(TypeModel20Package.eINSTANCE));
		assertTrue(ePackages.contains(InstanceModel20Package.eINSTANCE));
	}

	public void testGetOrdinal() {
		assertEquals(Test1Release100.ORDINAL, Test1Release100.INSTANCE.getOrdinal());
		assertEquals(Test1Release200.ORDINAL, Test1Release200.INSTANCE.getOrdinal());
		assertEquals(-1, Test1MM.INSTANCE.getOrdinal());
	}

	public void testGetName() {
		assertEquals(Test1Release100.NAME, Test1Release100.INSTANCE.getName());
		assertEquals(Test1Release200.NAME, Test1Release200.INSTANCE.getName());
		assertEquals(Test1MM.ID, Test1MM.INSTANCE.getName());
	}

	public void testGetRootEFactory() {
		assertNull(Test1Release100.INSTANCE.getRootEFactory());

		MockEPkgRegistry ePkgRegistry = Test1MM.MOCK_EPKG_REGISTRY;
		EPackage ePkg = ePkgRegistry.getEPackage(Test1Release100.INSTANCE.getNamespaceURI().toString());
		if (ePkg == null) {
			ePkg = new MockEPackage();
			ePkgRegistry.registerEPackage(Test1Release100.INSTANCE.getNamespaceURI(), ePkg);
		}
		EFactory eFactory = new MockEFactory();
		ePkg.setEFactoryInstance(eFactory);
		assertEquals(ePkg, Test1Release100.INSTANCE.getRootEPackage());
		assertEquals(eFactory, Test1Release100.INSTANCE.getRootEFactory());

		assertNull(Test1MM.INSTANCE.getRootEFactory());

		ePkg = ePkgRegistry.getEPackage(Test1MM.INSTANCE.getNamespaceURI().toString());
		if (ePkg == null) {
			ePkg = new MockEPackage();
			ePkgRegistry.registerEPackage(Test1MM.INSTANCE.getNamespaceURI(), ePkg);
		}
		eFactory = new MockEFactory();
		ePkg.setEFactoryInstance(eFactory);
		assertEquals(eFactory, Test1MM.INSTANCE.getRootEFactory());
	}

	public void testGetEPackageNsURIPattern() {
		assertEquals("http://testA.sphinx.org/1\\.0\\.0/\\d+", Test1Release100.INSTANCE.getEPackageNsURIPattern());
		assertEquals("http://testA.sphinx.org/2\\.0\\.0/\\d+", Test1Release200.INSTANCE.getEPackageNsURIPattern());
		assertEquals("http://testB.sphinx.org/1\\.0\\.0/\\d+", Test2Release100.INSTANCE.getEPackageNsURIPattern());
		assertEquals("http://testA.sphinx.org", Test1MM.INSTANCE.getEPackageNsURIPattern());
	}

	private class NewDescriptor extends AbstractMetaModelDescriptor {

		public NewDescriptor(String identifier, String namespace) {
			super(identifier, namespace);
		}

		public NewDescriptor(String identifier, String namespaceBase, MetaModelVersionData versionData) {
			super(identifier, namespaceBase, versionData);
		}
	}

}
