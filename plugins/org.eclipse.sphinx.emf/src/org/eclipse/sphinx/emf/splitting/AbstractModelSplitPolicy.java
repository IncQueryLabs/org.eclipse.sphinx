/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.splitting;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;

public abstract class AbstractModelSplitPolicy implements IModelSplitPolicy {

	/*
	 * @see org.eclipse.sphinx.emf.splitting.IModelSplitPolicy#getContentTypeId(java.util.List)
	 */
	@Override
	public String getContentTypeId(List<EObject> resourceContents) {
		Assert.isNotNull(resourceContents);

		if (!resourceContents.isEmpty()) {
			IMetaModelDescriptor metaModelDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resourceContents.get(0));
			if (metaModelDescriptor != null) {
				return metaModelDescriptor.getDefaultContentTypeId();
			}
		}
		return null;
	}
}
