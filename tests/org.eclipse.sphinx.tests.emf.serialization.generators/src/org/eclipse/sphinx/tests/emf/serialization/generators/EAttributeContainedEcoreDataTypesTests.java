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
package org.eclipse.sphinx.tests.emf.serialization.generators;

import org.eclipse.sphinx.tests.emf.serialization.generators.model.EAttributeContainedEcoreDataTypesModelBuilder;
import org.eclipse.sphinx.tests.emf.serialization.generators.model.ModelBuilder;

public class EAttributeContainedEcoreDataTypesTests extends AbstractAttributeContainedTestCase {

	@Override
	protected ModelBuilder getModelBuilder(String name, int persistenceMappingStrategy) {
		// TODO Auto-generated method stub
		return new EAttributeContainedEcoreDataTypesModelBuilder(name, persistenceMappingStrategy);
	}

}