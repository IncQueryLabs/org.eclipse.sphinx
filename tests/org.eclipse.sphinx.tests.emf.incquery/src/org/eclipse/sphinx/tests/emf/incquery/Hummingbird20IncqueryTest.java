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
	IModelQueryService modelIndexService;

	public Hummingbird20IncqueryTest() {
		super(new Hummingbird20ResourceFactoryImpl());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loadInputFile(TYPE_MODEL_FILE);
		loadInputFile(INSTANCE_MODEL_FILE);
		modelIndexService = getModelQueryService(Hummingbird20MMDescriptor.INSTANCE);
	}

	@Test
	public void testAllInstanceofPlatform() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<Platform> allPlatforms = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), Platform.class);
		Assert.assertEquals(1, allPlatforms.size());
	}

	@Test
	public void testAllInstanceofApplication() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<Application> allApplications = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), Application.class);
		Assert.assertEquals(1, allApplications.size());
	}

	@Test
	public void testAllInstanceofComponent() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<Component> allComponents = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), Component.class);
		Assert.assertEquals(7, allComponents.size());
	}

	@Test
	public void testAllInstanceofComponentType() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<ComponentType> allComponentTypes = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), ComponentType.class);
		Assert.assertEquals(4, allComponentTypes.size());
	}

	@Test
	public void testAllInstanceofParameter() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<Parameter> allParameters = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), Parameter.class);
		Assert.assertEquals(8, allParameters.size());
	}

	@Test
	public void testAllInstanceofInterface() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<Interface> allInterfaces = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), Interface.class);
		Assert.assertEquals(4, allInterfaces.size());
	}

	@Test
	public void testAllInstanceofConnection() throws Exception {
		Assert.assertNotNull("IModelQueryService is null !", modelIndexService); //$NON-NLS-1$
		List<Connection> allConnections = modelIndexService.getAllInstancesOf(getResourceSet().getResources().get(0), Connection.class);
		Assert.assertEquals(7, allConnections.size());
	}
}
