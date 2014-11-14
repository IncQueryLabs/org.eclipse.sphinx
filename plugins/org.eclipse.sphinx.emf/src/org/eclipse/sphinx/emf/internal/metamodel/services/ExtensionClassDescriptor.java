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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Allows lazy loading of plug-in classes. Must be used when needed to initialize classes contributed through extension
 * points.
 */
// TODO Provide this class for general usage (e.g. in org.eclipse.sphinx.platform)
public class ExtensionClassDescriptor<T> {

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_OVERRIDE = "override"; //$NON-NLS-1$

	private final String contributorName;
	private final String className;

	protected IConfigurationElement configurationElement;

	private T instance;

	/**
	 * Create an <code>ExtensionClassDescriptor</code> wrapped around a configuration element
	 *
	 * @param configurationElement
	 *            the configuration element
	 * @param attName
	 *            the attribute from the configuration element that contain the class name
	 */
	public ExtensionClassDescriptor(IConfigurationElement configurationElement) {
		Assert.isNotNull(configurationElement);
		this.configurationElement = configurationElement;

		contributorName = configurationElement.getContributor().getName();
		Assert.isNotNull(contributorName);

		className = getClassName();
		Assert.isNotNull(className);
	}

	protected String getAttribute(String attrName) {
		return configurationElement.getAttribute(attrName);
	}

	public String getClassName() {
		return getAttribute(ATTR_CLASS);
	}

	public String getOverrideClassName() {
		return getAttribute(ATTR_OVERRIDE);
	}

	/**
	 * Gets a cached instance of the class creating one if necessary
	 *
	 * @return The class instance
	 * @throws Throwable
	 */
	public T getInstance() throws Throwable {
		if (instance == null) {
			synchronized (this) {
				instance = newInstance();
			}
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
		Bundle bundle = Platform.getBundle(contributorName);
		if (bundle == null) {
			throw new Exception(MessageFormat.format("Cannot locate contributing plugin: {0}", contributorName)); //$NON-NLS-1$
		}
		Class<?> clazz = bundle.loadClass(className);
		return (T) clazz.newInstance();
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
		return otherDescriptor.getClassName().equals(getOverrideClassName());
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExtensionClassDescriptor<?> other = (ExtensionClassDescriptor<?>) obj;
		if (!className.equals(other.className)) {
			return false;
		}
		if (!contributorName.equals(other.contributorName)) {
			return false;
		}
		return true;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + className.hashCode();
		result = prime * result + contributorName.hashCode();
		return result;
	}
}
