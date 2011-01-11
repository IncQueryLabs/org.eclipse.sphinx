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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IRegistryEventListener;

public class MockExtensionRegistry implements IExtensionRegistry {

	private Map<String, IExtensionPoint> fExtPoints;

	public boolean addContribution(InputStream is, IContributor contributor, boolean persist, String name, ResourceBundle translationBundle,
			Object token) throws IllegalArgumentException {
		return false;
	}

	public void addListener(IRegistryEventListener listener) {
	}

	public void addListener(IRegistryEventListener listener, String extensionPointId) {
	}

	public void addRegistryChangeListener(IRegistryChangeListener listener) {
	}

	public void addRegistryChangeListener(IRegistryChangeListener listener, String namespace) {
	}

	public IConfigurationElement[] getConfigurationElementsFor(String extensionPointId) {
		if (!fExtPoints.containsKey(extensionPointId)) {
			fExtPoints.put(extensionPointId, new NotContributedExtensionPoint());
		}
		return fExtPoints.get(extensionPointId).getConfigurationElements();
	}

	public IConfigurationElement[] getConfigurationElementsFor(String namespace, String extensionPointName) {
		return getConfigurationElementsFor(extensionPointName);
	}

	public IConfigurationElement[] getConfigurationElementsFor(String namespace, String extensionPointName, String extensionId) {
		return null;
	}

	public IExtension getExtension(String extensionId) {
		return null;
	}

	public IExtension getExtension(String extensionPointId, String extensionId) {
		return null;
	}

	public IExtension getExtension(String namespace, String extensionPointName, String extensionId) {
		return null;
	}

	public IExtensionPoint getExtensionPoint(String extensionPointId) {
		return getExtPoints().get(extensionPointId);
	}

	public IExtensionPoint getExtensionPoint(String namespace, String extensionPointName) {
		return null;
	}

	public IExtensionPoint[] getExtensionPoints() {
		return null;
	}

	public IExtensionPoint[] getExtensionPoints(String namespace) {
		return null;
	}

	public IExtensionPoint[] getExtensionPoints(IContributor contributor) {
		return null;
	}

	public IExtension[] getExtensions(String namespace) {
		return null;
	}

	public IExtension[] getExtensions(IContributor contributor) {
		return null;
	}

	public String[] getNamespaces() {
		return null;
	}

	public boolean removeExtension(IExtension extension, Object token) throws IllegalArgumentException {
		return false;
	}

	public boolean removeExtensionPoint(IExtensionPoint extensionPoint, Object token) throws IllegalArgumentException {
		return false;
	}

	public void removeListener(IRegistryEventListener listener) {
	}

	public void removeRegistryChangeListener(IRegistryChangeListener listener) {
	}

	public void stop(Object token) throws IllegalArgumentException {
	}

	public void addExtensionPoint(AbstractMockExtensionPoint extPoint) {
		getExtPoints().put(extPoint.getId(), extPoint);
	}

	private Map<String, IExtensionPoint> getExtPoints() {
		if (fExtPoints == null) {
			fExtPoints = new HashMap<String, IExtensionPoint>();
		}
		return fExtPoints;
	}

	public boolean isMultiLanguage() {
		return false;
	}

}
