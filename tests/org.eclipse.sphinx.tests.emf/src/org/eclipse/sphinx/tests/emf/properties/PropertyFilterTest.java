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
package org.eclipse.sphinx.tests.emf.properties;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.sphinx.emf.properties.PropertyFilter;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;

@SuppressWarnings("nls")
public class PropertyFilterTest extends TestCase {

	private Application application;
	private ComponentType componentType;

	@Override
	public void setUp() throws Exception {

		application = InstanceModel20Factory.eINSTANCE.createApplication();
		componentType = TypeModel20Factory.eINSTANCE.createComponentType();

	}

	public void testPropertyFilterAcceptShortName() {

		PropertyFilter filter = new PropertyFilter(true);
		filter.setFeatureNames(new String[] { "name" });

		Assert.assertTrue(filter.accept(application, application.eClass().getEStructuralFeature("name")));
		Assert.assertTrue(filter.accept(application, componentType.eClass().getEStructuralFeature("name")));
		Assert.assertFalse(filter.accept(application, Hummingbird10Package.eINSTANCE.getApplication_Components()));

	}

	public void testPropertyFilterAcceptStringArray() {
		PropertyFilter filter = new PropertyFilter(true);
		filter.setFeatureNames(new String[] { "name", "components" });
		Assert.assertTrue(filter.accept(application, application.eClass().getEStructuralFeature("name")));
		Assert.assertTrue(filter.accept(application, InstanceModel20Package.eINSTANCE.getApplication_Components()));
		Assert.assertFalse(filter.accept(application, TypeModel20Package.eINSTANCE.getPlatform_Interfaces()));
		Assert.assertFalse(filter.accept(application, TypeModel20Package.eINSTANCE.getComponentType_Parameters()));
	}

	public void testPropertyFilterAcceptList() {

		PropertyFilter filter = new PropertyFilter(true);

		List<String> list = new LinkedList<String>();
		list.add("name");
		list.add("components");

		filter.setFeatureNames(list);
		Assert.assertTrue(filter.accept(application, application.eClass().getEStructuralFeature("name")));
		Assert.assertTrue(filter.accept(application, InstanceModel20Package.eINSTANCE.getApplication_Components()));
		Assert.assertFalse(filter.accept(application, TypeModel20Package.eINSTANCE.getPlatform_Interfaces()));
	}
}
