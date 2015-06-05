/**
 * <copyright>
 *
 * Copyright (c) 2008-2015 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [468171] Model element splitting service
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

public class ModelResourceDescriptor {

	Collection<EObject> modelRoots;
	IPath path;
	String contentTypeId;

	public ModelResourceDescriptor(EObject modelRoot, IPath path, String contentTypeId) {
		this(Collections.singleton(modelRoot), path, contentTypeId);
	}

	public ModelResourceDescriptor(Collection<EObject> modelRoots, IPath path, String contentTypeId) {
		Assert.isNotNull(modelRoots);
		Assert.isNotNull(path);
		this.modelRoots = modelRoots;
		this.path = path;
		this.contentTypeId = contentTypeId;
	}

	public Collection<EObject> getModelRoots() {
		return modelRoots;
	}

	public IPath getPath() {
		return path;
	}

	public String getContentTypeId() {
		return contentTypeId;
	}
}
