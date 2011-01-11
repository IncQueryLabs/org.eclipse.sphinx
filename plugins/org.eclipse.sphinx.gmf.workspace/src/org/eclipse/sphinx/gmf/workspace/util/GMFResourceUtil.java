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
package org.eclipse.sphinx.gmf.workspace.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;

public class GMFResourceUtil {

	// Prevent from instantiation
	private GMFResourceUtil() {
	}

	public static final String eCONTENT_TYPE = "org.eclipse.sphinx.gmf.diagramFile"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static Map<Object, Object> getSaveOptions(IMetaModelDescriptor mmDescriptor) {
		Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		return saveOptions;
	}
}
