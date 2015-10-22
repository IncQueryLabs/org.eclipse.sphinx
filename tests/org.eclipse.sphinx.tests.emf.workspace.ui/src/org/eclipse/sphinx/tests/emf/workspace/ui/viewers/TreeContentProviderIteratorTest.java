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
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.sphinx.emf.edit.ExtendedDelegatingWrapperItemProvider;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.workspace.ui.viewers.TreeContentProviderIterator;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Factory;
import org.eclipse.sphinx.examples.hummingbird20.common.Description;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.ParameterValue;
import org.eclipse.sphinx.tests.emf.workspace.ui.viewers.helpers.DefaultTreeContentProvider;
import org.junit.Test;

public class TreeContentProviderIteratorTest {

	private static class SampleModelExplorerTreeContentProvider extends DefaultTreeContentProvider {

		private AdapterFactory adapterFactory = new AdapterFactoryImpl();

		private TransientItemProvider createTransientItemProvider() {
			return new TransientItemProvider(adapterFactory);
		}

		private IWrapperItemProvider createWrapperItemProvider(Object value) {
			return new ExtendedDelegatingWrapperItemProvider(value, null, null, -1, adapterFactory);
		}

		public IProject project1 = createNiceMock(IProject.class);

		public IFile file1 = createNiceMock(IFile.class);
		public Application application1 = InstanceModel20Factory.eINSTANCE.createApplication();
		public Description description1 = Common20Factory.eINSTANCE.createDescription();
		public TransientItemProvider components1 = createTransientItemProvider();
		public Component component11 = InstanceModel20Factory.eINSTANCE.createComponent();
		public TransientItemProvider outgoingConnections11 = createTransientItemProvider();
		public Connection component11ToComponent22Connection = InstanceModel20Factory.eINSTANCE.createConnection();
		public TransientItemProvider parameterValues11 = createTransientItemProvider();
		public ParameterValue parameterValue111 = InstanceModel20Factory.eINSTANCE.createParameterValue();
		public ParameterValue parameterValue112 = InstanceModel20Factory.eINSTANCE.createParameterValue();
		public Component component12 = InstanceModel20Factory.eINSTANCE.createComponent();

		public IFile file2 = createNiceMock(IFile.class);
		public Application application2 = InstanceModel20Factory.eINSTANCE.createApplication();
		public Description description2 = Common20Factory.eINSTANCE.createDescription();
		public TransientItemProvider components2 = createTransientItemProvider();
		public Component component21 = InstanceModel20Factory.eINSTANCE.createComponent();
		public Component component22 = InstanceModel20Factory.eINSTANCE.createComponent();
		public TransientItemProvider outgoingConnections22 = createTransientItemProvider();
		public Connection component22ToComponent11Connection = InstanceModel20Factory.eINSTANCE.createConnection();
		public TransientItemProvider parameterValues22 = createTransientItemProvider();
		public ParameterValue parameterValue221 = InstanceModel20Factory.eINSTANCE.createParameterValue();

		public IWrapperItemProvider component11Ref = createWrapperItemProvider(component11);
		public IWrapperItemProvider outgoingConnections11Ref = createWrapperItemProvider(outgoingConnections11);
		public IWrapperItemProvider component11ToComponent22ConnectionRef = createWrapperItemProvider(component11ToComponent22Connection);
		public IWrapperItemProvider parameterValues11Ref = createWrapperItemProvider(parameterValues11);
		public IWrapperItemProvider parameterValue111Ref = createWrapperItemProvider(parameterValue111);
		public IWrapperItemProvider parameterValue112Ref = createWrapperItemProvider(parameterValue112);

		public IWrapperItemProvider component11RefRef = createWrapperItemProvider(component11Ref);
		public IWrapperItemProvider outgoingConnections11RefRef = createWrapperItemProvider(outgoingConnections11Ref);
		public IWrapperItemProvider component11ToComponent22ConnectionRefRef = createWrapperItemProvider(component11ToComponent22ConnectionRef);
		public IWrapperItemProvider parameterValues11RefRef = createWrapperItemProvider(parameterValues11Ref);
		public IWrapperItemProvider parameterValue111RefRef = createWrapperItemProvider(parameterValue111Ref);
		public IWrapperItemProvider parameterValue112RefRef = createWrapperItemProvider(parameterValue112Ref);

		public IWrapperItemProvider component11RefRefRef = createWrapperItemProvider(component11RefRef);

		public IWrapperItemProvider component22Ref = createWrapperItemProvider(component22);
		public IWrapperItemProvider outgoingConnections22Ref = createWrapperItemProvider(outgoingConnections22);
		public IWrapperItemProvider component22ToComponent11ConnectionRef = createWrapperItemProvider(component22ToComponent11Connection);
		public IWrapperItemProvider parameterValues22Ref = createWrapperItemProvider(parameterValues22);
		public IWrapperItemProvider parameterValue221Ref = createWrapperItemProvider(parameterValue221);

		public IWrapperItemProvider component22RefRef = createWrapperItemProvider(component22Ref);
		public IWrapperItemProvider outgoingConnections22RefRef = createWrapperItemProvider(outgoingConnections22Ref);
		public IWrapperItemProvider component22ToComponent11ConnectionRefRef = createWrapperItemProvider(component22ToComponent11ConnectionRef);
		public IWrapperItemProvider parameterValues22RefRef = createWrapperItemProvider(parameterValues22Ref);
		public IWrapperItemProvider parameterValue221RefRef = createWrapperItemProvider(parameterValue221Ref);

		public IWrapperItemProvider component22RefRefRef = createWrapperItemProvider(component22RefRef);

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement == project1) {
				return new Object[] { file1, file2 };
			}

			if (parentElement == file1) {
				return new Object[] { application1 };
			}
			{
				if (parentElement == application1) {
					return new Object[] { description1, components1 };
				}
				{
					if (parentElement == components1) {
						return new Object[] { component11, component12 };
					}
					{
						if (parentElement == component11) {
							return new Object[] { outgoingConnections11, parameterValues11 };
						}
						{
							if (parentElement == outgoingConnections11) {
								return new Object[] { component11ToComponent22Connection };
							}
							{
								if (parentElement == component11ToComponent22Connection) {
									return new Object[] { component22Ref };
								}
								{
									if (parentElement == component22Ref) {
										return new Object[] { outgoingConnections22Ref, parameterValues22Ref };
									}
									{
										if (parentElement == outgoingConnections22Ref) {
											return new Object[] { component22ToComponent11ConnectionRef };
										}
										{
											if (parentElement == component22ToComponent11ConnectionRef) {
												return new Object[] { component11RefRef };
											}
											{
												if (parentElement == component11RefRef) {
													return new Object[] { outgoingConnections11RefRef, parameterValues11RefRef };
												}
												{
													if (parentElement == outgoingConnections11RefRef) {
														return new Object[] { component11ToComponent22ConnectionRefRef };
													}
													{
														if (parentElement == component11ToComponent22ConnectionRefRef) {
															return new Object[] { component22RefRefRef };
														}
													}
													if (parentElement == parameterValues11RefRef) {
														return new Object[] { parameterValue111RefRef, parameterValue112RefRef };
													}
												}
											}
										}
										if (parentElement == parameterValues22Ref) {
											return new Object[] { parameterValue221Ref };
										}
									}
								}
							}
							if (parentElement == parameterValues11) {
								return new Object[] { parameterValue111, parameterValue112 };
							}
						}
					}
				}
			}

			if (parentElement == file2) {
				return new Object[] { application2 };
			}
			{
				if (parentElement == application2) {
					return new Object[] { description2, components2 };
				}
				{
					if (parentElement == components2) {
						return new Object[] { component21, component22 };
					}
					{
						if (parentElement == component22) {
							return new Object[] { outgoingConnections22, parameterValues22 };
						}
						{
							if (parentElement == outgoingConnections22) {
								return new Object[] { component22ToComponent11Connection };
							}
							{
								if (parentElement == component22ToComponent11Connection) {
									return new Object[] { component11Ref };
								}
								{
									if (parentElement == component11Ref) {
										return new Object[] { outgoingConnections11Ref, parameterValues11Ref };
									}
									{
										if (parentElement == outgoingConnections11Ref) {
											return new Object[] { component11ToComponent22ConnectionRef };
										}
										{
											if (parentElement == component11ToComponent22ConnectionRef) {
												return new Object[] { component22RefRef };
											}
											{
												if (parentElement == component22RefRef) {
													return new Object[] { outgoingConnections22RefRef, parameterValues22RefRef };
												}
												{
													if (parentElement == outgoingConnections22RefRef) {
														return new Object[] { component22ToComponent11ConnectionRefRef };
													}
													{
														if (parentElement == component22ToComponent11ConnectionRefRef) {
															return new Object[] { component11RefRefRef };
														}
													}
													if (parentElement == parameterValues22RefRef) {
														return new Object[] { parameterValue221RefRef };
													}
												}
											}
										}
										if (parentElement == parameterValues11Ref) {
											return new Object[] { parameterValue111Ref, parameterValue112Ref };
										}
									}
								}
							}
							if (parentElement == parameterValues22) {
								return new Object[] { parameterValue221 };
							}
						}
					}
				}
			}
			return new Object[0];
		}
	}

	@Test
	public void testTreeContentProviderIteratorOnProject1() {
		SampleModelExplorerTreeContentProvider provider = new SampleModelExplorerTreeContentProvider();
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
		SampleModelExplorerTreeContentProvider provider = new SampleModelExplorerTreeContentProvider();
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
		SampleModelExplorerTreeContentProvider provider = new SampleModelExplorerTreeContentProvider();
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
		SampleModelExplorerTreeContentProvider provider = new SampleModelExplorerTreeContentProvider();
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
