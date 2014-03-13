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
package org.eclipse.sphinx.tests.emf.serialization.generators.model;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

@SuppressWarnings("nls")
public class EReferenceReferencedWithSubtypesEmptyNsPrefixModelBuilder extends EReferenceReferencedWithSubtypesModelBuilder {

	public EReferenceReferencedWithSubtypesEmptyNsPrefixModelBuilder(String name, int persistenceMappingStrategy) {
		super(name, persistenceMappingStrategy);
	}

	@Override
	protected List<EPackage> createMetaModel(String name, boolean isMany, int persistenceMappingStrategy) {
		List<EPackage> ePackages = super.createMetaModel(name, isMany, persistenceMappingStrategy);
		EPackage ePackage = ePackages.get(0);
		ePackage.setNsPrefix("");
		return ePackages;
	}

}
