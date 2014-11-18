/**
 * <copyright>
 *
 * Copyright (c) BMW Car IT and others.
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
package org.eclipse.sphinx.emf.internal.metamodel.services;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.services.IMetaModelService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ServiceClassDescriptor extends ExtensionClassDescriptor<IMetaModelService> {

	private final static String ATTR_TYPE = "type"; //$NON-NLS-1$

	private String typeName;
	private Class<IMetaModelService> serviceType;

	public ServiceClassDescriptor(IConfigurationElement configurationElement) {
		super(configurationElement);

		typeName = configurationElement.getAttribute(ATTR_TYPE);
		Assert.isNotNull(typeName, Messages.metamodelservice_MissingServiceType);
	}

	public ServiceClassDescriptor(Class<IMetaModelService> serviceType, Class<? extends IMetaModelService> serviceClass) {
		super(serviceClass);
		this.serviceType = serviceType;
	}

	public String getTypeName() {
		return typeName;
	}

	@SuppressWarnings("unchecked")
	public Class<IMetaModelService> getServiceType() {
		if (serviceType == null) {
			try {
				serviceType = (Class<IMetaModelService>) Platform.getBundle(getContributorName()).loadClass(typeName);
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return serviceType;
	}
}
