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
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.services.IMetaModelService;

public class ServiceClassDescriptor extends ExtensionClassDescriptor<IMetaModelService> {

	private final static String ATTR_TYPE = "type"; //$NON-NLS-1$

	public ServiceClassDescriptor(IConfigurationElement configurationElement) {
		super(configurationElement);
		Assert.isNotNull(getType(), Messages.metamodelservice_MissingServiceType);
	}

	public String getType() {
		return getAttribute(ATTR_TYPE);
	}
}
