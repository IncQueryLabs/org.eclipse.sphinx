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
package org.eclipse.sphinx.emf.metamodel.providers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;

/**
 * Provides a metamodel descriptor based on a given context EMF object.
 */
public class EObjectMetaModelDescriptorProvider implements IMetaModelDescriptorProvider {

	private EObject fContextEObject;

	private EObjectMetaModelDescriptorProvider(EObject contextEObject) {
		fContextEObject = contextEObject;
	}

	/**
	 * Returns an instance of this class for the given context EMF object.
	 * 
	 * @param contextEObject
	 *            the context EMF object
	 * @return the instance of this class
	 */
	public static IMetaModelDescriptorProvider createMetaModelDescriptorProviderFor(EObject contextEObject) {
		return new EObjectMetaModelDescriptorProvider(contextEObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMetaModelDescriptor getMetaModelDescriptor() {
		return MetaModelDescriptorRegistry.INSTANCE.getDescriptor(fContextEObject);
	}

}
