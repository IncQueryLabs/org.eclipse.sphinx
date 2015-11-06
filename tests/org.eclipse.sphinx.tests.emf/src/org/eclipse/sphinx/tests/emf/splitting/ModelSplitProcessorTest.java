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
package org.eclipse.sphinx.tests.emf.splitting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.resource.ModelResourceDescriptor;
import org.eclipse.sphinx.emf.splitting.ModelSplitProcessor;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.common.Description;
import org.eclipse.sphinx.examples.hummingbird20.common.LanguageCultureName;
import org.eclipse.sphinx.examples.hummingbird20.common.Translation;
import org.eclipse.sphinx.examples.hummingbird20.splitting.Hummingbird20TypeModelSplitPolicy;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;
import org.eclipse.sphinx.examples.hummingbird20.util.Hummingbird20ResourceFactoryImpl;
import org.eclipse.sphinx.tests.emf.internal.Activator;
import org.eclipse.sphinx.testutils.AbstractTestCase;

@SuppressWarnings("nls")
public class ModelSplitProcessorTest extends AbstractTestCase {

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	public void testHummingbirdTypeModelSplit() throws Exception {
		EObject platform = loadInputFile("hbFile20.typemodel", new Hummingbird20ResourceFactoryImpl(), null);
		assertTrue(platform instanceof Platform);

		// Split hbFile20.typemodel into Interfaces.typemodel and ComponentTypes.typemodel
		ModelSplitProcessor processor = new ModelSplitProcessor(new Hummingbird20TypeModelSplitPolicy());
		processor.getEObjectsToSplit().add(platform);
		processor.run(null);

		// Retrieve and very split resource contents
		List<ModelResourceDescriptor> splitResourceDescriptors = new ArrayList<ModelResourceDescriptor>(processor.getSplitResourceDescriptors());
		assertNotNull(splitResourceDescriptors);
		assertEquals(2, splitResourceDescriptors.size());

		// Interfaces.typemodel
		ModelResourceDescriptor splitResourceDescriptor1 = splitResourceDescriptors.get(0);
		assertSame(Hummingbird20MMDescriptor.XMI_CONTENT_TYPE_ID, splitResourceDescriptor1.getContentTypeId());
		assertEquals(URI.createPlatformResourceURI(Activator.getPlugin().getSymbolicName() + "/resources/input/Interfaces.typemodel", true),
				splitResourceDescriptor1.getURI());

		List<EObject> splitResource1Contents = splitResourceDescriptor1.getContents();
		assertEquals(1, splitResource1Contents.size());
		platform = splitResource1Contents.get(0);
		assertTrue(platform instanceof Platform);

		// Make sure that ancestor object features of interfaces have been replicated in depth
		Description description = ((Platform) platform).getDescription();
		assertNotNull(description);
		assertEquals(LanguageCultureName.EN_US, description.getLanguage());
		assertEquals("Description1", EObjectUtil.getMixedText(description.getMixed()));
		EList<Translation> translations = description.getTranslations();
		assertEquals(2, translations.size());
		Translation translation1 = translations.get(0);
		assertNotNull(translation1);
		assertEquals(LanguageCultureName.DE_DE, translation1.getLanguage());
		assertEquals(URI.createURI("file:/deutsch.txt"), translation1.getResourceURI());
		Translation translation2 = translations.get(1);
		assertNotNull(translation2);
		assertEquals(LanguageCultureName.FR_FR, translation2.getLanguage());
		assertEquals(URI.createURI("file:/français.txt"), translation2.getResourceURI());

		// Mare sure that all interfaces are present and complete
		List<Interface> interfaces = ((Platform) platform).getInterfaces();
		assertEquals(2, interfaces.size());
		Interface interface1 = interfaces.get(0);
		assertNotNull(interface1);
		assertEquals("Interface1", interface1.getName());
		Interface interface2 = interfaces.get(1);
		assertNotNull(interface2);
		assertEquals("Interface2", interface2.getName());

		// Mare sure that no component types are present
		List<ComponentType> componentTypes = ((Platform) platform).getComponentTypes();
		assertEquals(0, componentTypes.size());

		// ComponentTypes.typemodel
		ModelResourceDescriptor splitResourceDescriptor2 = splitResourceDescriptors.get(1);
		assertSame(Hummingbird20MMDescriptor.XMI_CONTENT_TYPE_ID, splitResourceDescriptor2.getContentTypeId());
		assertEquals(URI.createPlatformResourceURI(Activator.getPlugin().getSymbolicName() + "/resources/input/ComponentTypes.typemodel", true),
				splitResourceDescriptor2.getURI());

		List<EObject> splitResource2Contents = splitResourceDescriptor2.getContents();
		assertEquals(1, splitResource2Contents.size());
		platform = splitResource2Contents.get(0);
		assertTrue(platform instanceof Platform);

		// Make sure that ancestor object features of interfaces have NOT been replicated
		description = ((Platform) platform).getDescription();
		assertNull(description);

		// Mare sure that no interfaces are present
		interfaces = ((Platform) platform).getInterfaces();
		assertEquals(0, interfaces.size());

		// Mare sure that all component types are present and complete
		componentTypes = ((Platform) platform).getComponentTypes();
		assertEquals(2, componentTypes.size());
		ComponentType componentType1 = componentTypes.get(0);
		assertNotNull(componentType1);
		assertEquals("ComponentType1", componentType1.getName());
		EList<Port> componentType1Ports = componentType1.getPorts();
		assertEquals(2, componentType1Ports.size());
		Port componentType1port1 = componentType1Ports.get(0);
		assertNotNull(componentType1port1);
		assertEquals("Port1", componentType1port1.getName());
		Port componentType1port2 = componentType1Ports.get(1);
		assertNotNull(componentType1port2);
		assertEquals("Port2", componentType1port2.getName());
		ComponentType componentType2 = componentTypes.get(1);
		assertNotNull(componentType2);
		assertEquals("ComponentType2", componentType2.getName());
	}
}
