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
package org.eclipse.sphinx.emf.ui.properties.descriptors;

import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;

public class BasicAdvancedTabDescriptor extends AbstractTabDescriptor {

	private String id;

	private String label;

	private String category;

	@SuppressWarnings("unchecked")
	public BasicAdvancedTabDescriptor(String id, String label, String category) {
		this.id = id;
		this.label = label;
		this.category = category;
		getSectionDescriptors().add(new BasicAdvancedSectionDescriptor(id + ".sectionDesc", id)); //$NON-NLS-1$
	}

	public String getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
