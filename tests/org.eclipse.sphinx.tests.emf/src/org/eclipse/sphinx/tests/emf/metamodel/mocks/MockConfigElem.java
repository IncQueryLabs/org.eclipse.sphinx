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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

public class MockConfigElem implements IConfigurationElement {

	private String fName;
	private IContributor fContributor;
	private List<MockAttribute> fAttributes;

	public MockConfigElem(String name) {
		fName = name;
	}

	@Override
	public Object createExecutableExtension(String propertyName) throws CoreException {
		MockAttribute attribute = getMockAttribute(propertyName);
		if (attribute != null && attribute instanceof MockClassAttribute) {
			return ((MockClassAttribute) attribute).createExecutableExtension();
		}
		return null;
	}

	@Override
	public String getAttribute(String name) throws InvalidRegistryObjectException {
		MockAttribute attribute = getMockAttribute(name);
		if (attribute != null) {
			return attribute.getValue();
		}
		return null;
	}

	private MockAttribute getMockAttribute(String name) {
		for (MockAttribute attribute : getAttributeList()) {
			if (name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}

	@Override
	public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getNamespace() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public Object getParent() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getValue() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getValueAsIs() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return fContributor;
	}

	@Override
	public String getName() throws InvalidRegistryObjectException {
		return fName;
	}

	public void setContributor(IContributor contributor) {
		fContributor = contributor;
	}

	public void addAttribute(MockAttribute attribute) {
		getAttributeList().add(attribute);
	}

	private List<MockAttribute> getAttributeList() {
		if (fAttributes == null) {
			fAttributes = new ArrayList<MockAttribute>();
		}
		return fAttributes;
	}

	@Override
	public String getAttribute(String attrName, String locale) throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getValue(String locale) throws InvalidRegistryObjectException {
		return null;
	}
}
