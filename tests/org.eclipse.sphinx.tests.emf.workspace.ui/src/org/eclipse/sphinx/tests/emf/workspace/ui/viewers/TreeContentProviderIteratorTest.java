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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.sphinx.emf.workspace.ui.viewers.TreeContentProviderIterator;
import org.eclipse.sphinx.tests.emf.workspace.ui.scenarios.Hummingbird20ModelExplorerScenarioTreeContentProvider;
import org.junit.Test;

public class TreeContentProviderIteratorTest {

	@Test
	public void testTreeContentProviderIteratorOnProject1() {
		Hummingbird20ModelExplorerScenarioTreeContentProvider provider = new Hummingbird20ModelExplorerScenarioTreeContentProvider();
		TreeContentProviderIterator iter = new TreeContentProviderIterator(provider, provider.project1);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.project1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.file1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.application1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.description1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.components1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections11);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11ToComponent22Connection);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22ToComponent11ConnectionRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11RefRef);
		assertTrue(iter.isRecurrent());

		// As component11RefRef is recurrent from the project1's perspective, its outgoingConnections11RefRef and
		// parameterValues11RefRef children are skipped

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue221Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues11);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue111);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue112);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component12);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.file2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.application2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.description2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.components2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component21);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22);
		assertTrue(iter.isRecurrent());

		// As component22 is recurrent from the project1's perspective, its outgoingConnections22 and parameterValue221
		// children are skipped and the tree iteration terminates right here
		assertFalse(iter.hasNext());
	}

	@Test
	public void testTreeContentProviderIteratorOnFile1() {
		Hummingbird20ModelExplorerScenarioTreeContentProvider provider = new Hummingbird20ModelExplorerScenarioTreeContentProvider();
		TreeContentProviderIterator iter = new TreeContentProviderIterator(provider, provider.file1);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.file1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.application1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.description1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.components1);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections11);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11ToComponent22Connection);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22ToComponent11ConnectionRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11RefRef);
		assertTrue(iter.isRecurrent());

		// As component11RefRef is recurrent from the file1's perspective, its outgoingConnections11RefRef and
		// parameterValues11RefRef children are skipped

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue221Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues11);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue111);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue112);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component12);
		assertFalse(iter.isRecurrent());

		assertFalse(iter.hasNext());
	}

	@Test
	public void testTreeContentProviderIteratorOnFile2() {
		Hummingbird20ModelExplorerScenarioTreeContentProvider provider = new Hummingbird20ModelExplorerScenarioTreeContentProvider();
		TreeContentProviderIterator iter = new TreeContentProviderIterator(provider, provider.file2);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.file2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.application2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.description2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.components2);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component21);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections22);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22ToComponent11Connection);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections11Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11ToComponent22ConnectionRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22RefRef);
		assertTrue(iter.isRecurrent());

		// As component22RefRef is recurrent from the file2's perspective, its outgoingConnections22RefRef and
		// parameterValues22RefRef children are skipped

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues11Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue111Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue112Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues22);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue221);
		assertFalse(iter.isRecurrent());

		assertFalse(iter.hasNext());
	}

	@Test
	public void testTreeContentProviderIteratorOnComponent11ToComponent22Connection() {
		Hummingbird20ModelExplorerScenarioTreeContentProvider provider = new Hummingbird20ModelExplorerScenarioTreeContentProvider();
		TreeContentProviderIterator iter = new TreeContentProviderIterator(provider, provider.component11ToComponent22Connection);

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11ToComponent22Connection);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component22ToComponent11ConnectionRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11RefRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.outgoingConnections11RefRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.component11ToComponent22ConnectionRefRef);
		assertTrue(iter.isRecurrent());

		// As component11ToComponent22ConnectionRefRef is recurrent from the component11ToComponent22Connection's
		// perspective, its component22RefRefRef child is skipped

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues11RefRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue111RefRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue112RefRef);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValues22Ref);
		assertFalse(iter.isRecurrent());

		assertTrue(iter.hasNext());
		assertSame(iter.next(), provider.parameterValue221Ref);
		assertFalse(iter.isRecurrent());

		assertFalse(iter.hasNext());
	}
}
