/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.metamodel.descs;

import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockEPkgRegistry;

@SuppressWarnings("nls")
public class Test2MM extends AbstractMetaModelDescriptor {

	public static final String ID = "org.eclipse.sphinx.emf.internal.tests.test2mm";

	public static final String NS = "http://testB.sphinx.org";

	public static final MockEPkgRegistry MOCK_EPKG_REGISTRY = new MockEPkgRegistry();

	public static final Test2MM INSTANCE = new Test2MM();

	public Test2MM() {
		this(ID, null);
	}

	protected Test2MM(String identifier, MetaModelVersionData versionData) {
		super(identifier, NS, versionData);
		setEPackageRegistry(MOCK_EPKG_REGISTRY);
	}

	@Override
	public String getDefaultContentTypeId() {
		return "";
	}
}
