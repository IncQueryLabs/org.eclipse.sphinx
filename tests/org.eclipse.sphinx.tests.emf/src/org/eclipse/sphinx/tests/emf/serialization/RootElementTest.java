/**
 * <copyright>
 * 
 * Copyright (c) 2012 BMW Car IT and others.
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
package org.eclipse.sphinx.tests.emf.serialization;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.util.Hummingbird20ResourceFactoryImpl;
import org.eclipse.sphinx.tests.emf.internal.Activator;
import org.eclipse.sphinx.testutils.AbstractTestCase;

@SuppressWarnings("nls")
public class RootElementTest extends AbstractTestCase {
	public void testComments() throws Exception {
		Hummingbird20ResourceFactoryImpl rf = new Hummingbird20ResourceFactoryImpl();

		Map<Object, Object> options = new HashMap<Object, Object>();
		options.put(XMLResource.OPTION_ENCODING, "UTF-8");
		options.put(XMLResource.OPTION_SUPPRESS_DOCUMENT_ROOT, Boolean.TRUE);

		String wc = "RootElementTest/WithComments.instancemodel";
		Application a = (Application) loadInputFile(wc, rf, options);
		saveWorkingFile(wc, a, rf, options);
		String expected = loadInputFileAsString(wc);
		String actual = loadWorkingFileAsString(wc);

		assertEquals(expected, actual);
	}

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}
}
