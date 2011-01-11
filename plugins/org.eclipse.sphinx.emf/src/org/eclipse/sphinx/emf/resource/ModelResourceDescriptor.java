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
package org.eclipse.sphinx.emf.resource;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

public class ModelResourceDescriptor {

	EObject modelRoot;
	IPath path;
	String contentTypeId;

	public ModelResourceDescriptor(EObject modelRoot, IPath path, String contentTypeId) {
		Assert.isNotNull(modelRoot);
		Assert.isNotNull(path);
		this.modelRoot = modelRoot;
		this.path = path;
		this.contentTypeId = contentTypeId;
	}

	public EObject getModelRoot() {
		return modelRoot;
	}

	public IPath getPath() {
		return path;
	}

	public String getContentTypeId() {
		return contentTypeId;
	}
}
