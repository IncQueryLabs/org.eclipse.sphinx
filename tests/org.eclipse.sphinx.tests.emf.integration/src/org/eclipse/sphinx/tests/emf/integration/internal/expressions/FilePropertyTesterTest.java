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
package org.eclipse.sphinx.tests.emf.integration.internal.expressions;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.sphinx.emf.internal.expressions.FilePropertyTester;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.uml2.ide.metamodel.UML2MMDescriptor;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

@SuppressWarnings("restriction")
public class FilePropertyTesterTest extends DefaultIntegrationTestCase {

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A };
	}

	/**
	 * Test method for {@link FilePropertyTester#test(Object, String, Object[], Object)}
	 */
	public void testTest() {
		assertNotNull(refWks.hbProject20_A);
		assertNotNull(refWks.hbProject10_A);
		assertTrue(refWks.hbProject20_A.exists());
		assertTrue(refWks.hbProject10_A.exists());

		FilePropertyTester tester = new FilePropertyTester();
		String validProperty = "metaModelIdMatches"; //$NON-NLS-1$
		String invalidProperty1 = "hummingbirdReleaseProperty"; //$NON-NLS-1$
		String invalidProperty2 = "isHummingbirdContent"; //$NON-NLS-1$
		String anyHummingbirdReleaseDescriptorIdPattern = "org.eclipse.sphinx.examples.hummingbird\\d\\w"; //$NON-NLS-1$
		Object[] args = new Object[] {};

		// No receiver object
		Assert.assertFalse(tester.test(null, validProperty, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(null, invalidProperty1, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(null, invalidProperty2, args, anyHummingbirdReleaseDescriptorIdPattern));

		// Invalid receiver object
		Assert.assertFalse(tester.test(new Object(), validProperty, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(new Object(), invalidProperty1, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(new Object(), invalidProperty2, args, anyHummingbirdReleaseDescriptorIdPattern));

		// HB20 file
		IFile receiver = refWks.hbProject20_A.getFile(DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1);
		assertNotNull(receiver);
		assertTrue(receiver.exists());

		Assert.assertTrue(tester.test(receiver, validProperty, args, Hummingbird20MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertTrue(tester.test(receiver, validProperty, null, Hummingbird20MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertTrue(tester.test(receiver, validProperty, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertTrue(tester.test(receiver, validProperty, null, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(receiver, validProperty, args, Hummingbird10MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertFalse(tester.test(receiver, validProperty, null, Hummingbird10MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertFalse(tester.test(receiver, validProperty, args, UML2MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertFalse(tester.test(receiver, validProperty, null, UML2MMDescriptor.INSTANCE.getIdentifier()));

		Assert.assertFalse(tester.test(receiver, invalidProperty1, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(receiver, invalidProperty1, null, anyHummingbirdReleaseDescriptorIdPattern));

		Assert.assertFalse(tester.test(receiver, invalidProperty2, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(receiver, invalidProperty2, null, anyHummingbirdReleaseDescriptorIdPattern));

		// HB10 file
		receiver = refWks.hbProject10_A.getFile(DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		assertNotNull(receiver);
		assertTrue(receiver.exists());

		Assert.assertTrue(tester.test(receiver, validProperty, args, Hummingbird10MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertTrue(tester.test(receiver, validProperty, null, Hummingbird10MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertTrue(tester.test(receiver, validProperty, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertTrue(tester.test(receiver, validProperty, null, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(receiver, validProperty, args, Hummingbird20MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertFalse(tester.test(receiver, validProperty, null, Hummingbird20MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertFalse(tester.test(receiver, validProperty, args, UML2MMDescriptor.INSTANCE.getIdentifier()));
		Assert.assertFalse(tester.test(receiver, validProperty, null, UML2MMDescriptor.INSTANCE.getIdentifier()));

		Assert.assertFalse(tester.test(receiver, invalidProperty1, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(receiver, invalidProperty1, null, anyHummingbirdReleaseDescriptorIdPattern));

		Assert.assertFalse(tester.test(receiver, invalidProperty2, args, anyHummingbirdReleaseDescriptorIdPattern));
		Assert.assertFalse(tester.test(receiver, invalidProperty2, null, anyHummingbirdReleaseDescriptorIdPattern));
	}
}
