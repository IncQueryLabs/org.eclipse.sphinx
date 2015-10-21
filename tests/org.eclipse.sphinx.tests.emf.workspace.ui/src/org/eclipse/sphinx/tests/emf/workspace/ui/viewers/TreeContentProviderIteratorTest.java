/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.tests.emf.workspace.ui.viewers;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.workspace.ui.viewers.TreeContentProviderIterator;
import org.eclipse.sphinx.examples.hummingbird20.common.Description;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.ParameterValue;
import org.eclipse.sphinx.tests.emf.workspace.ui.viewers.helpers.DefaultTreeContentProvider;
import org.junit.Test;

public class TreeContentProviderIteratorTest {

	private class SampleModelExplorerTreeContentProvider extends DefaultTreeContentProvider {

		public IProject project = createNiceMock(IProject.class);
		public IFile file = createNiceMock(IFile.class);
		public Application application1 = createNiceMock(Application.class);
		public Description description1 = createNiceMock(Description.class);
		public TransientItemProvider components1 = new TransientItemProvider(null);
		public Component component11 = createNiceMock(Component.class);
		public TransientItemProvider outgoingConnections11 = new TransientItemProvider(null);
		public TransientItemProvider parameterValues11 = new TransientItemProvider(null);
		public ParameterValue parameterValue111 = createNiceMock(ParameterValue.class);
		public ParameterValue parameterValue112 = createNiceMock(ParameterValue.class);
		public Component component12 = createNiceMock(Component.class);

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement == project) {
				return new Object[] { file };
			}
			if (parentElement == file) {
				return new Object[] { application1 };
			}
			if (parentElement == application1) {
				return new Object[] { description1, components1 };
			}
			if (parentElement == application1) {
				return new Object[] { description1, components1 };
			}
			if (parentElement == components1) {
				return new Object[] { component11, component12 };
			}
			if (parentElement == component11) {
				return new Object[] { outgoingConnections11, parameterValues11 };
			}
			if (parentElement == parameterValues11) {
				return new Object[] { parameterValue111, parameterValue112 };
			}
			return new Object[0];
		}
	}

	@Test
	public void testTreeContentProviderIterator() {
		SampleModelExplorerTreeContentProvider provider = new SampleModelExplorerTreeContentProvider();
		TreeContentProviderIterator iter = new TreeContentProviderIterator(provider, provider.project);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.project);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.file);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.application1);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.description1);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.components1);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections11);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues11);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue111);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue112);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component12);

		assertFalse(iter.hasNext());
	}
}
