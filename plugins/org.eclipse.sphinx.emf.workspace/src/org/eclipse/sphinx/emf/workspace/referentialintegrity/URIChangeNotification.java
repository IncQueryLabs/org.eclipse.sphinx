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
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;

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
		return EcoreResourceUtil.getURI(newEObject);
	}
}
