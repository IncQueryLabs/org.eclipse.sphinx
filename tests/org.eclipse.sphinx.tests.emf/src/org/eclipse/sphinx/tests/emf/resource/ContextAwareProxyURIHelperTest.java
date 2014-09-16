/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.tests.emf.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sphinx.emf.resource.ContextAwareProxyURIHelper;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Factory;
import org.junit.Test;

@SuppressWarnings("nls")
public class ContextAwareProxyURIHelperTest {

	@Test
	public void testAugmentToContextAwareProxy() throws IOException {
		ContextAwareProxyURIHelper helper = new ContextAwareProxyURIHelper();

		// Set resource
		URI emfURI = URI.createURI("working-dir/hb20test.typemodel", true);
		XMIResource resource = new XMIResourceImpl(emfURI);

		// Expected proxy
		Port expectedProxy = TypeModel20Factory.eINSTANCE.createPort();
		URI uri = URI.createURI("working-dir/hb20test.typemodel#//@componentTypes.0/@ports.0");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);

		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("working-dir/hb20test.typemodel?ctxURI=working-dir/hb20test.typemodel#//@componentTypes.0/@ports.0",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		// One other key/value query field
		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&key1=value1");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&key1=value1&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		// Two other key/value query fields
		uri = URI.createURI("file:/?key1=value1&key2=value2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?key1=value1&key2=value2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&?key1=value1&key2=value2");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&?key1=value1&key2=value2&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		// One other key-only query field
		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		// Two other key-only query fields
		uri = URI.createURI("file:/?key1&key2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?key1&key2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&?key1&key2");
		((InternalEObject) expectedProxy).eSetProxyURI(uri);
		helper.augmentToContextAwareProxy(expectedProxy, resource);
		assertEquals("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&?key1&key2&ctxURI=working-dir/hb20test.typemodel",
				((InternalEObject) expectedProxy).eProxyURI().toString());
	}

	@Test
	public void testTrimProxyContextInfo() {
		ContextAwareProxyURIHelper helper = new ContextAwareProxyURIHelper();

		// No other query fields
		URI uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		URI resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertNull(resultURI.query());

		// One other key/value query field
		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1=value1", resultURI.query());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key1=value1");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1=value1", resultURI.query());

		// Two other key/value query fields
		uri = URI.createURI("file:/?key1=value1&key2=value2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1=value1&key2=value2", resultURI.query());

		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key2=value2");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1=value1&key2=value2", resultURI.query());

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&?key1=value1&key2=value2");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1=value1&key2=value2", resultURI.query());

		// One other key-only query field
		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1", resultURI.query());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key1");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1", resultURI.query());

		// Two other key-only query fields
		uri = URI.createURI("file:/?key1&key2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1&key2", resultURI.query());

		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key2");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1&key2", resultURI.query());

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&?key1&key2");
		resultURI = helper.trimProxyContextInfo(uri);

		assertNotNull(resultURI);
		assertEquals("key1&key2", resultURI.query());
	}

	@Test
	public void testGetTargetMetaModelDescriptorId() {
		ContextAwareProxyURIHelper helper = new ContextAwareProxyURIHelper();
		String HB_METAMODEL_DESCRIPTOR_ID = "org.eclipse.sphinx.examples.hummingbird20";

		// No other query fields
		URI uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		String metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/?key1=value1&ctxURI=platform:/resource/aProject");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNull(metamodelDescriptorId);

		// One other key/value query field
		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key1=value1");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		// Two other key/value query fields
		uri = URI.createURI("file:/?key1=value1&key2=value2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key2=value2");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&?key1=value1&key2=value2");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNull(metamodelDescriptorId);

		// One other key-only query field
		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key1");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		// Two other key-only query fields
		uri = URI.createURI("file:/?key1&key2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key2");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNotNull(metamodelDescriptorId);
		assertEquals(HB_METAMODEL_DESCRIPTOR_ID, metamodelDescriptorId);

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&?key1&key2");
		metamodelDescriptorId = helper.getTargetMetaModelDescriptorId(uri);
		assertNull(metamodelDescriptorId);
	}

	@Test
	public void testGetContextURI() {
		ContextAwareProxyURIHelper helper = new ContextAwareProxyURIHelper();
		String WORKSPACE_ROOT_URI = "platform:/resource/";
		String PROJECT_A_URI = "platform:/resource/aProject";

		// No other query fields
		URI uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		URI contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(WORKSPACE_ROOT_URI, contextURI.toString());

		// One other key/value query field
		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key1=value1");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		// Two other key/value query fields
		uri = URI.createURI("file:/?key1=value1&key2=value2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/?key1=value1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key2=value2");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&?key1=value1&key2=value2");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(WORKSPACE_ROOT_URI, contextURI.toString());

		// One other key-only query field
		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/?tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key1");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		// Two other key-only query fields
		uri = URI.createURI("file:/?key1&key2&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/?key1&tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&key2");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(PROJECT_A_URI, contextURI.toString());

		uri = URI.createURI("file:/tgtMMD=org.eclipse.sphinx.examples.hummingbird20&ctxURI=platform:/resource/aProject&?key1&key2");
		contextURI = helper.getContextURI(uri);
		assertNotNull(contextURI);
		assertEquals(WORKSPACE_ROOT_URI, contextURI.toString());
	}
}
