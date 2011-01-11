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
package org.eclipse.sphinx.testutils.integration.referenceworkspace;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Factory;
import org.eclipse.sphinx.testutils.integration.AbstractIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.internal.Activator;
import org.eclipse.uml2.uml.UMLFactory;

/**
 * 
 */
public class DefaultIntegrationTestCase extends AbstractIntegrationTestCase<DefaultTestReferenceWorkspace> {

	public DefaultIntegrationTestCase() {
		super(DefaultTestReferenceWorkspace.class.getName());
	}

	@Override
	protected DefaultTestReferenceWorkspace doCreateReferenceWorkspace(String[] referenceProjectNames) {
		return new DefaultTestReferenceWorkspace(referenceProjectNames);
	}

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	@Override
	protected String[][] getProjectReferences() {
		return new String[][] { { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_D },
				{ DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_E, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D },
				{ DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E } };
	}

	protected EObject createHummingbird20InstanceModelRoot() {
		return InstanceModel20Factory.eINSTANCE.createApplication();
	}

	protected EObject createHummingbird20TypeModelRoot() {
		return TypeModel20Factory.eINSTANCE.createPlatform();

	}

	protected EObject createUML2ModelRoot() {
		return UMLFactory.eINSTANCE.createModel();
	}
}