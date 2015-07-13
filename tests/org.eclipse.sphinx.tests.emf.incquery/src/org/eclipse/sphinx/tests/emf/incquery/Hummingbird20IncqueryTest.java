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
package org.eclipse.sphinx.tests.emf.incquery;

import java.util.List;

import org.eclipse.sphinx.emf.query.IModelQueryService;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.util.Hummingbird20ResourceFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

public class Hummingbird20IncqueryTest extends AbstractIncqueryTestCase {

	String INSTANCE_MODEL_FILE = "hb20.instancemodel"; //$NON-NLS-1$
	String TYPE_MODEL_FILE = "hb20.typemodel"; //$NON-NLS-1$
	IModelQueryService modelQueryService;

	public Hummingbird20IncqueryTest() {
		super(new Hummingbird20ResourceFactoryImpl());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loadInputFile(TYPE_MODEL_FILE);
		loadInputFile(INSTANCE_MODEL_FILE);
		modelQueryService = getModelQueryService(Hummingbird20MMDescriptor.INSTANCE);
	}

	@Test
	public void testAllInstancesofPlatform() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<Platform> allPlatforms = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), Platform.class);
		Assert.assertEquals(1, allPlatforms.size());
	}

	@Test
	public void testAllInstancesofApplication() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<Application> allApplications = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), Application.class);
		Assert.assertEquals(1, allApplications.size());
	}

	@Test
	public void testAllInstancesofComponent() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<Component> allComponents = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), Component.class);
		Assert.assertEquals(7, allComponents.size());
	}

	@Test
	public void testAllInstancesofComponentType() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<ComponentType> allComponentTypes = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), ComponentType.class);
		Assert.assertEquals(4, allComponentTypes.size());
	}

	@Test
	public void testAllInstancesofParameter() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<Parameter> allParameters = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), Parameter.class);
		Assert.assertEquals(8, allParameters.size());
	}

	@Test
	public void testAllInstancesofInterface() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<Interface> allInterfaces = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), Interface.class);
		Assert.assertEquals(4, allInterfaces.size());
	}

	@Test
	public void testAllInstancesofConnection() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelQueryService); //$NON-NLS-1$
		List<Connection> allConnections = modelQueryService.getAllInstancesOf(getResourceSet().getResources().get(0), Connection.class);
		Assert.assertEquals(7, allConnections.size());
	}
}
