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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

@SuppressWarnings("deprecation")
public class NotContributedExtensionPoint implements IExtensionPoint {

	@Override
	public IConfigurationElement[] getConfigurationElements() throws InvalidRegistryObjectException {
		return new IConfigurationElement[] {};
	}

	@Override
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public IPluginDescriptor getDeclaringPluginDescriptor() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public IExtension getExtension(String extensionId) throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public IExtension[] getExtensions() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getLabel() throws InvalidRegistryObjectException {
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
	public String getSchemaReference() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getSimpleIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public String getUniqueIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public String getLabel(String locale) throws InvalidRegistryObjectException {
		return null;
	}
}
