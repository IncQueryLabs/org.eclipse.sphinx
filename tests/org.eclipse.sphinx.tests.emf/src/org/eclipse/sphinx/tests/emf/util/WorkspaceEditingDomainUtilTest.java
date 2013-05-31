/**
 * <copyright>
 *
 * Copyright (c) 2013 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1MM;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test1Release100;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test2MM;
import org.eclipse.sphinx.tests.emf.metamodel.descs.Test2Release100;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MetaModelDescriptorsEP;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockExtensionRegistry;

public class WorkspaceEditingDomainUtilTest extends TestCase {

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

	/**
	 * Test method for {@link EcoreResourceUtil#readSchemaLocationEntries(Resource)}
	 */
	public void testGetEditingDomainsFromScheme() {

		// Register one meta model
		registerWithRegistryUT(Test1MM.INSTANCE);
		List<TransactionalEditingDomain> expectedEditingDomains = new ArrayList<TransactionalEditingDomain>();
		// Get editing domains that use URIs with given "tr1" scheme in cross-document references and as proxy URIs.
		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI("tr1:/#test1Release.sphinx.org/scheme", true); //$NON-NLS-1$
		String uriScheme = uri.scheme();
		assertEquals(uriScheme, "tr1"); //$NON-NLS-1$
		Collection<TransactionalEditingDomain> actualEditingDomains = WorkspaceEditingDomainUtil.getEditingDomains(uriScheme);
		expectedEditingDomains.add(WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(), Test1MM.INSTANCE));
		assertEquals(actualEditingDomains.size(), 1);
		assertEquals(expectedEditingDomains, actualEditingDomains);

		// Register four meta models
		registerWithRegistryUT(Test1MM.INSTANCE, Test1Release100.INSTANCE, Test2MM.INSTANCE, Test2Release100.INSTANCE);
		// Get editing domains that use URIs with given "tr2" scheme in cross-document references and as proxy URIs.
		uri = org.eclipse.emf.common.util.URI.createURI("tr2:/#test2Release100.sphinx.org/scheme", true); //$NON-NLS-1$
		uriScheme = uri.scheme();
		assertEquals(uriScheme, "tr2"); //$NON-NLS-1$
		actualEditingDomains = WorkspaceEditingDomainUtil.getEditingDomains(uriScheme);
		assertEquals(actualEditingDomains.size(), 2);
		expectedEditingDomains.clear();
		expectedEditingDomains.add(WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(), Test2MM.INSTANCE));
		expectedEditingDomains.add(WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(), Test2Release100.INSTANCE));
		assertEquals(expectedEditingDomains, actualEditingDomains);
	}

	private void registerWithRegistryUT(IMetaModelDescriptor... descriptors) {
		fExtPoint.clear();
		for (IMetaModelDescriptor descriptor : descriptors) {
			fExtPoint.registerDescriptor(descriptor);
		}
		fRegistryUT.setExtensionRegistry(fExtensionRegistry);
	}
}
