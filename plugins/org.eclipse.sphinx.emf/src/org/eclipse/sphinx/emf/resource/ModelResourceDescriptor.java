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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

public class ModelResourceDescriptor {

	private List<EObject> contents;
	private IPath path;
	private String contentTypeId;

	public ModelResourceDescriptor(EObject contents, IPath path, String contentTypeId) {
		this(Collections.singleton(contents), path, contentTypeId);
	}

	/**
	 * @deprecated Use {@link #ModelResourceDescriptor(List, IPath, String)} instead.
	 */
	@Deprecated
	public ModelResourceDescriptor(Collection<EObject> contents, IPath path, String contentTypeId) {
		this(new ArrayList<EObject>(contents), path, contentTypeId);
	}

	public ModelResourceDescriptor(List<EObject> contents, IPath path, String contentTypeId) {
		Assert.isNotNull(contents);
		Assert.isNotNull(path);
		this.contents = contents;
		this.path = path;
		this.contentTypeId = contentTypeId;
	}

	/**
	 * @deprecated Use {@link #getContents()} instead.
	 */
	@Deprecated
	public Collection<EObject> getModelRoots() {
		return getContents();
	}

	public List<EObject> getContents() {
		return contents;
	}

	public IPath getPath() {
		return path;
	}

	public String getContentTypeId() {
		return contentTypeId;
	}
}
