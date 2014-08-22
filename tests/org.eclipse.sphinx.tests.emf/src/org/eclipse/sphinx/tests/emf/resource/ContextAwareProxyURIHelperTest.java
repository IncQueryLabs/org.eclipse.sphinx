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

import org.eclipse.emf.common.util.URI;
import org.eclipse.sphinx.emf.resource.ContextAwareProxyURIHelper;
import org.junit.Test;

@SuppressWarnings("nls")
public class ContextAwareProxyURIHelperTest {

	@Test
	public void testAugmentToContextAwareProxy() {
		// TODO To be implemented
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
		// TODO To be implemented
	}

	@Test
	public void testGetContextURI() {
		// TODO To be implemented
	}
}
