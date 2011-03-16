/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
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
package org.eclipse.sphinx.graphiti.workspace.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class GraphitiResourceFactory extends XMIResourceFactoryImpl {

	public GraphitiResourceFactory() {
		super();
	}

	public final static String XMI_ENCODING = "UTF-8"; //$NON-NLS-1$

	// default load options.
	private static final Map<Object, Object> loadOptions = new HashMap<Object, Object>();

	// default save options.
	private static final Map<Object, Object> saveOptions = new HashMap<Object, Object>();

	static {

		XMIResource resource = new XMIResourceImpl();

		// default load options.
		loadOptions.putAll(resource.getDefaultLoadOptions());

		// default save options.
		saveOptions.putAll(resource.getDefaultSaveOptions());
		saveOptions.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
		saveOptions.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		saveOptions.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.TRUE);
		saveOptions.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);
		saveOptions.put(XMLResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
		saveOptions.put(XMLResource.OPTION_ENCODING, XMI_ENCODING);
		saveOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
	}

	/**
	 * Get default load options.
	 */
	public static Map<Object, Object> getDefaultLoadOptions() {
		return loadOptions;
	}

	/**
	 * Get default save options.
	 */
	public static Map<Object, Object> getDefaultSaveOptions() {
		return saveOptions;
	}

	@Override
	public Resource createResource(URI uri) {

		XMIResource resource = new GraphitiResource(uri);

		resource.getDefaultLoadOptions().putAll(loadOptions);
		resource.getDefaultSaveOptions().putAll(saveOptions);

		if (!resource.getEncoding().equals(XMI_ENCODING)) {
			resource.setEncoding(XMI_ENCODING);
		}

		return resource;
	}
}
