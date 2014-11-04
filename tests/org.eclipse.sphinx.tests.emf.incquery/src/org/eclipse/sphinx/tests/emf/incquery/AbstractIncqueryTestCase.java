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

import java.lang.management.ManagementFactory;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.services.DefaultMetaModelServiceProvider;
import org.eclipse.sphinx.emf.query.IModelQueryService;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;
import org.eclipse.sphinx.examples.hummingbird20.util.Hummingbird20ResourceFactoryImpl;
import org.eclipse.sphinx.testutils.AbstractTestCase;

public abstract class AbstractIncqueryTestCase extends AbstractTestCase {

	private ScopingResourceSetImpl resourceSet;
	protected Hummingbird20ResourceFactoryImpl resourceFactory;

	public AbstractIncqueryTestCase(Hummingbird20ResourceFactoryImpl resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	protected IModelQueryService getModelQueryService(IMetaModelDescriptor mmDescriptor) {
		return new DefaultMetaModelServiceProvider().getService(mmDescriptor, org.eclipse.sphinx.emf.query.IModelQueryService.class);
	}

	@Override
	protected ScopingResourceSetImpl createDefaultResourceSet() {
		if (resourceSet == null) {
			resourceSet = new ScopingResourceSetImpl();
		}
		return resourceSet;
	}

	protected ScopingResourceSetImpl getResourceSet() {
		if (resourceSet == null) {
			return createDefaultResourceSet();
		}
		return resourceSet;
	}

	protected long getCurrentThreadCpuTime() {
		return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
	}

	protected EObject loadInputFile(String fileName) throws Exception {
		return loadInputFile(fileName, resourceFactory, null);
	}

	@Override
	protected Plugin getTestPlugin() {
		return org.eclipse.sphinx.tests.emf.incquery.internal.Activator.getPlugin();
	}

	protected void saveWorkingFile(String fileName, EObject modelRoot) throws Exception {
		saveWorkingFile(fileName, modelRoot, resourceFactory, null);
	}

	protected EObject loadWorkingFile(String fileName) throws Exception {
		return loadWorkingFile(fileName, resourceFactory, null);
	}
}
