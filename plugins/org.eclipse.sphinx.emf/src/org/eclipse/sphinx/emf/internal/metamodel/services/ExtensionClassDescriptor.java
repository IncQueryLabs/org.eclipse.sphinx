/**
 * <copyright>
 *
 * Copyright (c) Continental Engineering Services and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Continental Engineering Services - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.metamodel.services;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Allows lazy loading of plug-in classes. Must be used when needed to initialize classes contributed through extension
 * points.
 */
public class ExtensionClassDescriptor<T> {

	private final static String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_OVERRIDE = "override"; //$NON-NLS-1$
	private final String ownerPlugin;
	private final String className;
	protected IConfigurationElement fCfgElem;

	private boolean initialized = false;

	private T instance;

	/**
	 * Create an <code>ExtensionClassWrapper</code> wrapped around a configuration element
	 *
	 * @param configElement
	 *            the configuration element
	 * @param attName
	 *            the attribute from the configuration element that contain the class name
	 */
	public ExtensionClassDescriptor(IConfigurationElement configElement) {
		this.fCfgElem = configElement;
		this.ownerPlugin = configElement.getContributor().getName();
		this.className = configElement.getAttribute(ATT_CLASS);
	}

	protected String getAttribute(String attrName) {
		return fCfgElem.getAttribute(attrName);
	}

	public String getServiceClass() {
		return getAttribute(ATT_CLASS);
	}

	public String getOverrideClassName() {
		return getAttribute(ATT_OVERRIDE);
	}

	/**
	 * Gets a cached instance of the class creating one if necessary
	 *
	 * @return The class instance
	 * @throws Throwable
	 */
	@SuppressWarnings({ "nls", "unchecked" })
	public T getInstance() throws Throwable {
		if (!initialized) {
			synchronized (this) {
				try {
					if (className != null) {
						Bundle bundle = Platform.getBundle(ownerPlugin);
						if (bundle == null) {
							throw new Exception(MessageFormat.format("Cannot locate contributing plugin: {0}", ownerPlugin));
						}
						try {
							Class<?> clazz = bundle.loadClass(className);
							instance = (T) clazz.newInstance();
						} catch (Throwable t) {
							throw t;
						}
					}
				} finally {
					initialized = true;
				}
			}
		}
		if (instance == null) {
			throw new Exception(MessageFormat.format("Failed to construct class {0}", className));
		}
		return instance;
	}

	/**
	 * Create and return a new instance of the wrapped object.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T newInstance() throws Throwable {
		Bundle bundle = Platform.getBundle(ownerPlugin);
		if (bundle == null) {
			throw new Exception(MessageFormat.format("Cannot locate contributing plugin: {0}", ownerPlugin)); //$NON-NLS-1$
		}
		try {
			Class<?> clazz = bundle.loadClass(className);
			return (T) clazz.newInstance();
		} catch (Throwable t) {
			throw t;
		}
	}

	/**
	 * Determines if this service registration dominates another service registration. A service registration dominates
	 * another registration if it overrides the class of the other service registration.
	 *
	 * @param otherService
	 *            the other service registration for which the domination relation is to be determined
	 * @return <code>true</code> if the other service registration is dominated, otherwise <code>false</code>
	 */
	public boolean overrides(ExtensionClassDescriptor<?> otherDescriptor) {
		if (otherDescriptor == null) {
			return true;
		}
		return otherDescriptor.getServiceClass().equals(getOverrideClassName());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExtensionClassDescriptor<?>) {
			ExtensionClassDescriptor<?> classWrapper = (ExtensionClassDescriptor<?>) obj;
			return ownerPlugin.equals(classWrapper.ownerPlugin) && className.equals(classWrapper.className);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return ownerPlugin.hashCode() * 31 + className.hashCode();
	}
}
