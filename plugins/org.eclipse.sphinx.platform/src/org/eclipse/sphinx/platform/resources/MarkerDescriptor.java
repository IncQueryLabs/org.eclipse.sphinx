/**
 * <copyright>
 * 
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.platform.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

public class MarkerDescriptor {

	private String type;

	private Map<String, Object> attributes;

	public MarkerDescriptor() {
		super();
	}

	public MarkerDescriptor(String type) {
		Assert.isNotNull(type);
		this.type = type;
	}

	public MarkerDescriptor(String type, Map<String, Object> attributes) {
		Assert.isNotNull(type);
		Assert.isNotNull(attributes);
		this.type = type;
		this.attributes = attributes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getAttributes() {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		return attributes;
	}
}
