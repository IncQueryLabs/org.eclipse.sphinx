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
package org.eclipse.sphinx.emf.metamodel.services;

import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.providers.IMetaModelDescriptorProvider;

/**
 * A plug-in extension registry based {@link IMetaModelServiceProvider}.
 */
public class DefaultMetaModelServiceProvider implements IMetaModelServiceProvider {

	@Override
	public <T extends IMetaModelService> T getService(IMetaModelDescriptor mmDescriptor, Class<T> mmServiceType) {
		return MetaModelServiceRegistry.INSTANCE.getService(mmDescriptor, mmServiceType);
	}

	@Override
	public <T extends IMetaModelService> T getService(IMetaModelDescriptorProvider mmDesciptorProvider, Class<T> mmServiceType) {
		return getService(mmDesciptorProvider.getMetaModelDescriptor(), mmServiceType);
	}
}
