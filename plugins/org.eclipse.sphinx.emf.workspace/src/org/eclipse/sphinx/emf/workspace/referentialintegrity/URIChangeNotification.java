/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.referentialintegrity;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;

/**
 * 
 */
public class URIChangeNotification {

	URI oldURI;
	EObject newEObject;

	public URIChangeNotification(EObject newEObject, URI oldURI) {
		Assert.isNotNull(newEObject);
		Assert.isNotNull(oldURI);

		this.newEObject = newEObject;
		this.oldURI = oldURI;

	}

	/**
	 * @return
	 */
	public EObject getNewEObject() {
		return newEObject;
	}

	/**
	 * @return
	 */
	public URI getOldURI() {
		return oldURI;
	}

	/**
	 * @return
	 */
	public URI getNewURI() {
		URI uri = null;
		Resource resource = newEObject.eResource();
		ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource);
		if (extendedResource != null) {
			uri = extendedResource.createProxyURI((InternalEObject) newEObject);
		} else {
			uri = EcoreUtil.getURI(newEObject);
		}
		return uri;
	}
}
