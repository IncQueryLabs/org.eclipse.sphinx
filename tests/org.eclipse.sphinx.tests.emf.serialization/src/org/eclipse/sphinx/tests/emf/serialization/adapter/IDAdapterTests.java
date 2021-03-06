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
package org.eclipse.sphinx.tests.emf.serialization.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.serialization.XMLPersistenceMappingResource;
import org.eclipse.sphinx.tests.emf.serialization.model.nodes.Node;
import org.eclipse.sphinx.tests.emf.serialization.model.nodes.NodesFactory;
import org.eclipse.sphinx.tests.emf.serialization.model.nodes.NodesPackage;
import org.eclipse.sphinx.tests.emf.serialization.model.nodes.serialization.NodesResourceFactoryImpl;
import org.eclipse.sphinx.tests.emf.serialization.model.nodes.serialization.NodesResourceImpl;
import org.eclipse.sphinx.tests.emf.serialization.util.AbstractTestCase;
import org.junit.Before;
import org.junit.Test;

// Junit 3.8 test
@SuppressWarnings("nls")
public class IDAdapterTests extends AbstractTestCase {

	static final String INPUT_PATH = "org.eclipse.sphinx.tests.emf.serialization.adapter/";

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		EPackage.Registry.INSTANCE.put(NodesPackage.eNS_URI, NodesPackage.eINSTANCE);
	}

	@Test
	public void testIdOnLoad() {
		String inputFileName = INPUT_PATH + "minimal.xml";
		try {
			EObject modelRoot = loadInputFile(inputFileName, new NodesResourceFactoryImpl(), null);
			assertTrue(modelRoot instanceof Node);
			Node rootNode = (Node) modelRoot;
			assertTrue(rootNode.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(ex.getMessage(), false);
		}
	}

	@Test
	public void testIdOnLoadNotModified() {
		String inputFileName = INPUT_PATH + "minimalWithId.xml";
		try {
			EObject modelRoot = loadInputFile(inputFileName, new NodesResourceFactoryImpl(), null);
			assertTrue(modelRoot instanceof Node);
			Node rootNode = (Node) modelRoot;
			assertEquals("root", rootNode.getName());

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(ex.getMessage(), false);
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIdForNewObjectsAfterLoad1() {
		String inputFileName = INPUT_PATH + "minimal.xml";
		try {
			EObject modelRoot = loadInputFile(inputFileName, new NodesResourceFactoryImpl(), null);
			assertTrue(modelRoot instanceof Node);
			Node rootNode = (Node) modelRoot;
			assertTrue(rootNode.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertSame(1, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

			// test set
			Node subNode = NodesFactory.eINSTANCE.createNode();
			rootNode.setEReference_Contained0100Single(subNode);
			assertTrue(subNode.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertSame(2, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

			// test add single
			rootNode.getEReference_Contained1100Many().add(NodesFactory.eINSTANCE.createNode());
			assertTrue(5 < rootNode.getEReference_Contained1100Many().get(0).getName().length());
			assertSame(3, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

			// test add many
			List<Node> newNodes = new ArrayList<Node>();
			for (int i = 0; i < 5; i++) {
				newNodes.add(NodesFactory.eINSTANCE.createNode());
			}
			rootNode.getEReference_Contained0100Many().addAll(newNodes);
			for (int i = 0; i < 5; i++) {
				assertTrue(5 < rootNode.getEReference_Contained0100Many().get(i).getName().length());
			}
			assertSame(8, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

		} catch (Exception ex) {
			assertTrue(ex.getMessage(), false);
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIdForNewObjectsAfterLoad2() {
		String inputFileName = INPUT_PATH + "minimal.xml";
		try {
			EObject modelRoot = loadInputFile(inputFileName, new NodesResourceFactoryImpl(), null);
			assertTrue(modelRoot instanceof Node);
			Node rootNode = (Node) modelRoot;
			assertTrue(rootNode.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertSame(1, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

			// test set
			Node sub1Node = NodesFactory.eINSTANCE.createNode();
			Node sub2Node = NodesFactory.eINSTANCE.createNode();
			Node sub3Node = NodesFactory.eINSTANCE.createNode();
			Node sub4Node = NodesFactory.eINSTANCE.createNode();
			sub4Node.setName("sub4Node");

			sub1Node.setEReference_Contained0101Single(sub2Node);
			sub2Node.setEReference_Contained0101Single(sub3Node);
			sub3Node.setEReference_Contained0101Single(sub4Node);

			assertFalse(sub1Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertFalse(sub2Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertFalse(sub3Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertTrue(sub4Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertEquals("sub4Node", sub4Node.getName());
			assertSame(1, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

			// set
			rootNode.setEReference_Contained0100Single(sub1Node);

			assertTrue(sub1Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertTrue(sub2Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertTrue(sub3Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertTrue(sub4Node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
			assertEquals("sub4Node", sub4Node.getName());
			assertSame(5, ((XMLResource) modelRoot.eResource()).getEObjectToIDMap().size());

		} catch (Exception ex) {
			assertTrue(ex.getMessage(), false);
		}
	}

	@Test
	public void testAddNewElementToEmptyResource() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();
		Node node = NodesFactory.eINSTANCE.createNode();
		Node subNode = NodesFactory.eINSTANCE.createNode();
		node.getEReference_Contained0100Many().add(subNode);
		assertFalse(node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
		assertFalse(subNode.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));

		resource.getContents().add(node);
		assertTrue(node.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
		assertTrue(subNode.eIsSet(NodesPackage.eINSTANCE.getNode_Name()));
	}

	@Test
	public void testIdMapSet() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();
		String nodeName = "root";
		String subNodeName = "subNode";
		Node node = NodesFactory.eINSTANCE.createNode();
		node.setName(nodeName);
		Node subNode = NodesFactory.eINSTANCE.createNode();
		subNode.setName(subNodeName);
		node.setEReference_Contained0100Single(subNode);

		assertNull(resource.getEObject(nodeName));
		assertNull(resource.getID(node));
		assertNull(resource.getEObject(subNodeName));
		assertNull(resource.getID(subNode));

		resource.getContents().add(node);
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));

		assertSame(subNode, resource.getEObject(subNodeName));
		assertSame(subNodeName, resource.getID(subNode));
	}

	@Test
	public void testIdMapAdd() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();
		String nodeName = "root";
		String subNodeName = "subNode";
		Node node = NodesFactory.eINSTANCE.createNode();
		node.setName(nodeName);
		Node subNode = NodesFactory.eINSTANCE.createNode();
		subNode.setName(subNodeName);
		node.getEReference_Contained0100Many().add(subNode);

		assertNull(resource.getEObject(nodeName));
		assertNull(resource.getID(node));
		assertNull(resource.getEObject(subNodeName));
		assertNull(resource.getID(subNode));

		resource.getContents().add(node);
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));

		assertSame(subNode, resource.getEObject(subNodeName));
		assertSame(subNodeName, resource.getID(subNode));
	}

	@Test
	public void testIdMapAddMany() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();
		String node1Name = "node1";
		String node2Name = "node2";
		Node node1 = NodesFactory.eINSTANCE.createNode();
		node1.setName(node1Name);
		Node node2 = NodesFactory.eINSTANCE.createNode();
		node2.setName(node2Name);
		List<Node> nodes = new ArrayList<Node>(2);
		nodes.add(node1);
		nodes.add(node2);

		assertNull(resource.getEObject(node1Name));
		assertNull(resource.getID(node1));
		assertNull(resource.getEObject(node2Name));
		assertNull(resource.getID(node2));

		resource.getContents().addAll(nodes);

		assertSame(node1, resource.getEObject(node1Name));
		assertSame(node1Name, resource.getID(node1));

		assertSame(node2, resource.getEObject(node2Name));
		assertSame(node2Name, resource.getID(node2));
	}

	@Test
	public void testIdMapMove() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();
		String nodeName = "root";
		String subNodeName = "subNode";
		Node node = NodesFactory.eINSTANCE.createNode();
		node.setName(nodeName);
		Node subNode = NodesFactory.eINSTANCE.createNode();
		subNode.setName(subNodeName);
		node.getEReference_Contained0100Many().add(subNode);

		assertNull(resource.getEObject(nodeName));
		assertNull(resource.getID(node));
		assertNull(resource.getEObject(subNodeName));
		assertNull(resource.getID(subNode));

		resource.getContents().add(node);
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));
		assertSame(subNode, resource.getEObject(subNodeName));
		assertSame(subNodeName, resource.getID(subNode));

		node.setEReference_Contained0100Single(subNode);
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));
		assertSame(subNode, resource.getEObject(subNodeName));
		assertSame(subNodeName, resource.getID(subNode));
	}

	@Test
	public void testIdMapRemove() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();

		String nodeName = "root";
		String subNodeName = "subNode";
		Node node = NodesFactory.eINSTANCE.createNode();
		node.setName(nodeName);
		Node subNode = NodesFactory.eINSTANCE.createNode();
		subNode.setName(subNodeName);
		node.getEReference_Contained0100Many().add(subNode);

		assertNull(resource.getEObject(nodeName));
		assertNull(resource.getID(node));
		assertNull(resource.getEObject(subNodeName));
		assertNull(resource.getID(subNode));

		resource.getContents().add(node);
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));
		assertSame(subNode, resource.getEObject(subNodeName));
		assertSame(subNodeName, resource.getID(subNode));

		node.getEReference_Contained0100Many().remove(subNode);
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));
		assertNull(resource.getEObject(subNodeName));
		assertNull(resource.getID(subNode));

		resource.getContents().remove(node);
		assertNull(resource.getEObject(nodeName));
		assertNull(resource.getID(node));
		assertNull(resource.getEObject(subNodeName));
		assertNull(resource.getID(subNode));
	}

	@Test
	public void testIdMapRename() {
		XMLPersistenceMappingResource resource = new NodesResourceImpl();
		String nodeName = "root";
		String newNodeName = "new_root";
		Node node = NodesFactory.eINSTANCE.createNode();
		node.setName(nodeName);

		assertNull(resource.getEObject(nodeName));
		assertNull(resource.getID(node));

		resource.getContents().add(node);
		assertNull(resource.getEObject(newNodeName));
		assertSame(node, resource.getEObject(nodeName));
		assertSame(nodeName, resource.getID(node));

		node.setName(newNodeName);
		assertNull(resource.getEObject(nodeName));
		assertSame(node, resource.getEObject(newNodeName));
		assertSame(newNodeName, resource.getID(node));
	}

}
