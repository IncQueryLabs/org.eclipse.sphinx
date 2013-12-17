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
package org.eclipse.sphinx.tests.emf.metamodel.mocks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;

@SuppressWarnings("nls")
public class MetaModelDescriptorsEP extends AbstractMockExtensionPoint {

	private static final String ID = "org.eclipse.sphinx.emf.metaModelDescriptors";

	public void registerDescriptor(IMetaModelDescriptor versionDesc) {
		MockExtension extension = new MockExtension("org.eclipse.sphinx.tests.emf", false);
		MockConfigElem configElem = new MockConfigElem("descriptor");

		List<MockAttribute> attributes = new ArrayList<MockAttribute>();
		attributes.add(new MockAttribute("id", versionDesc.getIdentifier()));
		attributes.add(new MockClassAttribute("class", versionDesc));
		for (MockAttribute attribute : attributes) {
			configElem.addAttribute(attribute);
		}
		extension.addConfigurationElement(configElem);
		addExtension(extension);
	}

	@Override
	protected String getId() {
		return ID;
	}

	@Override
	public String getLabel(String locale) throws InvalidRegistryObjectException {
		return null;
	}
}
