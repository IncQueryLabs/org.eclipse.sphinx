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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.osgi.framework.Bundle;

@SuppressWarnings("deprecation")
public class MockExtension implements IExtension {

	private List<IConfigurationElement> fConfigElems;
	private IContributor fContributor;

	public MockExtension(String contributorId) {
		this(contributorId, false);
	}

	public MockExtension(String contributorId, boolean isFragment) {
		Bundle bundle = Platform.getBundle(contributorId);
		String bundleId = Long.toString(bundle.getBundleId());
		String bundleName = bundle.getSymbolicName();
		String hostId;
		String hostName;
		if (isFragment) {
			Bundle host = Platform.getHosts(bundle)[0];
			hostId = Long.toString(host.getBundleId());
			hostName = host.getSymbolicName();
		} else {
			hostId = null;
			hostName = null;
		}
		fContributor = new RegistryContributor(bundleId, bundleName, hostId, hostName);
	}

	public IConfigurationElement[] getConfigurationElements() throws InvalidRegistryObjectException {
		return getConfigElemsList().toArray(new IConfigurationElement[] {});
	}

	private List<IConfigurationElement> getConfigElemsList() {
		if (fConfigElems == null) {
			fConfigElems = new ArrayList<IConfigurationElement>();
		}
		return fConfigElems;
	}

	public void addConfigurationElement(MockConfigElem configElem) {
		configElem.setContributor(getContributor());
		getConfigElemsList().add(configElem);
	}

	public IContributor getContributor() throws InvalidRegistryObjectException {
		return fContributor;
	}

	public IPluginDescriptor getDeclaringPluginDescriptor() throws InvalidRegistryObjectException {
		return null;
	}

	public String getExtensionPointUniqueIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	public String getLabel() throws InvalidRegistryObjectException {
		return null;
	}

	public String getNamespace() throws InvalidRegistryObjectException {
		return null;
	}

	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	public String getSimpleIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	public String getUniqueIdentifier() throws InvalidRegistryObjectException {
		return null;
	}

	public boolean isValid() {
		return false;
	}

	public String getLabel(String locale) throws InvalidRegistryObjectException {
		return null;
	}
}
