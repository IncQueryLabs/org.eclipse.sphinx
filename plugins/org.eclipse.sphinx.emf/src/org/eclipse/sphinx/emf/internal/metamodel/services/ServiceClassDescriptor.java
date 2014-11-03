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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.services.IMetaModelService;

public class ServiceClassDescriptor extends ExtensionClassDescriptor<IMetaModelService> {

	private final static String MMS_ATT_TYPE = "type"; //$NON-NLS-1$

	public ServiceClassDescriptor(IConfigurationElement cfgElem) {
		super(cfgElem);
		assertServiceTypeNotNull();
		assertServiceClassNotNull();
	}

	private void assertServiceTypeNotNull() {
		assertNotNull(getType(), Messages.metamodelservice_MissingServiceName);
	}

	private void assertServiceClassNotNull() {
		assertNotNull(getServiceClass(), Messages.metamodelservice_MissingServiceClass);
	}

	private void assertNotNull(String value, String msgId) {
		if (value == null) {
			throwException(msgId);
		}
	}

	private void throwException(String msgId) {
		throw new IllegalArgumentException(NLS.bind(msgId, fCfgElem.getContributor().getName()));
	}

	public String getType() {
		return getAttribute(MMS_ATT_TYPE);
	}
}
