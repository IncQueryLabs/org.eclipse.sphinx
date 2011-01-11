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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

@SuppressWarnings("deprecation")
public abstract class AbstractMockExtensionPoint implements IExtensionPoint {

	private List<IExtension> fExtensions;

	public void clear() {
		getExtensionsList().clear();
	}

	protected abstract String getId();

	public IConfigurationElement[] getConfigurationElements() throws InvalidRegistryObjectException {
		List<IConfigurationElement> allConfigElems = new ArrayList<IConfigurationElement>();
		for (IExtension extension : getExtensionsList()) {
			IConfigurationElement[] configElems = extension.getConfigurationElements();
			allConfigElems.addAll(Arrays.asList(configElems));
		}
		return allConfigElems.toArray(new IConfigurationElement[] {});
	}

	public IContributor getContributor() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public IPluginDescriptor getDeclaringPluginDescriptor() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtension getExtension(String extensionId) throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtension[] getExtensions() throws InvalidRegistryObjectException {
		return getExtensionsList().toArray(new IExtension[] {});
	}

	public String getLabel() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespace() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSchemaReference() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSimpleIdentifier() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUniqueIdentifier() throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void addExtension(IExtension extension) {
		getExtensionsList().add(extension);
	}

	private List<IExtension> getExtensionsList() {
		if (fExtensions == null) {
			fExtensions = new ArrayList<IExtension>();
		}
		return fExtensions;
	}

}
