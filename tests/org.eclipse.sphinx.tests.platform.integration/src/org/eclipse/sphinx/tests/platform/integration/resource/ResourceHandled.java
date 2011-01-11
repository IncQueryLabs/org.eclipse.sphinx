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
package org.eclipse.sphinx.tests.platform.integration.resource;

import org.eclipse.core.runtime.IPath;

public class ResourceHandled {
	protected IPath resourceChangedPath;
	protected String eventType;

	public IPath getResourceChangedPath() {
		return resourceChangedPath;
	}

	public String getEventType() {
		return eventType;
	}

	public ResourceHandled(IPath path, String type) {
		resourceChangedPath = path;
		eventType = type;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ResourceHandled) {
			ResourceHandled compareObject = (ResourceHandled) object;
			if (!compareObject.getResourceChangedPath().toString().equals(resourceChangedPath.toString())) {
				return false;
			}
			if (!compareObject.getEventType().equals(eventType)) {
				return false;
			}
			return true;

		}
		return false;
	}
}