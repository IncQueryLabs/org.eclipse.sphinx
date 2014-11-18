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

	private final static String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_OVERRIDE = "override"; //$NON-NLS-1$

	private String id;
	private String className;
	private String override;
	private String contributorName;

	private Class<? extends T> clazz;
	private T instance;

	/**
	 * Create an <code>ExtensionClassDescriptor</code> wrapped around a configuration element
	 *
	 * @param configurationElement
	 *            the configuration element
	 */
	public ExtensionClassDescriptor(IConfigurationElement configurationElement) {
		Assert.isNotNull(configurationElement);

		className = configurationElement.getAttribute(ATTR_CLASS);
		Assert.isNotNull(className);

		contributorName = configurationElement.getContributor().getName();
		Assert.isNotNull(contributorName);

		id = configurationElement.getAttribute(ATTR_ID);
		Assert.isNotNull(id);

		override = configurationElement.getAttribute(ATTR_OVERRIDE);
	}

	public ExtensionClassDescriptor(Class<? extends T> clazz) {
		Assert.isNotNull(clazz);
		this.clazz = clazz;
	}

	public String getId() {
		return id;
	}

	public String getClassName() {
		return className != null ? className : clazz.getName();
	}

	public String getOverride() {
		return override;
	}

	public String getContributorName() {
		return contributorName;
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
		if (clazz != null) {
			return clazz.newInstance();
		}
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
		return otherDescriptor.getId().equals(getOverride());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (className == null ? 0 : className.hashCode());
		result = prime * result + (clazz == null ? 0 : clazz.hashCode());
		result = prime * result + (contributorName == null ? 0 : contributorName.hashCode());
		result = prime * result + (id == null ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
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
		ExtensionClassDescriptor other = (ExtensionClassDescriptor) obj;
		if (className == null) {
			if (other.className != null) {
				return false;
			}
		} else if (!className.equals(other.className)) {
			return false;
		}
		if (clazz == null) {
			if (other.clazz != null) {
				return false;
			}
		} else if (!clazz.equals(other.clazz)) {
			return false;
		}
		if (contributorName == null) {
			if (other.contributorName != null) {
				return false;
			}
		} else if (!contributorName.equals(other.contributorName)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
