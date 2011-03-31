/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.uml2.ide.metamodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.examples.uml2.ide.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * UML2 metamodel descriptor.
 */
public class UML2MMDescriptor extends AbstractMetaModelDescriptor {

	/**
	 * The base namespace of this and all earlier UML2 metamodel implementations.
	 */
	public static final String BASE_NAMESPACE = "http://www.eclipse.org/uml2"; //$NON-NLS-1$

	/**
	 * The id of the base content type for the version-specific UML2 XMI file content types behind this and all earlier
	 * UML2 metamodel implementations.
	 */
	/*
	 * Performance optimization: Don't retrieve content type id with UMLPackage.eCONTENT_TYPE so as to avoid unnecessary
	 * initialization of the UML2 metamodel's EPackage. Clients may want to consult the UML2 metamodel descriptor even
	 * if no UML2 XMI file actually exists, and the initialization of the UML2 metamodel's EPackage in such situations
	 * would entail useless runtime and memory consumption overhead.
	 */
	public static final String XMI_BASE_CONTENT_TYPE_ID = "org.eclipse.uml2.uml"; //$NON-NLS-1$

	/**
	 * The id of the XMI file content type behind the latest UML2 metamodel implementation.
	 */
	public static final String XMI_CONTENT_TYPE_ID = XMI_BASE_CONTENT_TYPE_ID + "_3_0_0"; //$NON-NLS-1$

	/**
	 * Singleton instance.
	 */
	public static final UML2MMDescriptor INSTANCE = new UML2MMDescriptor();

	private static final String ID = "org.eclipse.sphinx.examples.uml2"; //$NON-NLS-1$
	/*
	 * Performance optimization: Don't retrieve namespace with UMLPackage.eNS_URI so as to avoid unnecessary
	 * initialization of the UML2 metamodel's EPackage. Clients may want to consult the UML2 metamodel descriptor even
	 * if no UML2 XMI file actually exists, and the initialization of the UML2 metamodel's EPackage in such situations
	 * would entail useless runtime and memory consumption overhead.
	 */
	private static final String NAMESPACE = BASE_NAMESPACE + "/3.0.0/UML"; //$NON-NLS-1$
	private static final String NAME = "UML2"; //$NON-NLS-1$

	/**
	 * Private default constructor for singleton pattern.
	 */
	private UML2MMDescriptor() {
		super(ID, NAMESPACE, NAME);
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor#getCompatibleNamespaceURIs()
	 */
	@Override
	public List<URI> getCompatibleNamespaceURIs() {
		List<URI> namespaceURIs = new ArrayList<URI>();
		try {
			namespaceURIs.add(new URI(BASE_NAMESPACE + "/2.0.0/UML"));//$NON-NLS-1$
			namespaceURIs.add(new URI(BASE_NAMESPACE + "/2.1.0/UML")); //$NON-NLS-1$
		} catch (URISyntaxException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return namespaceURIs;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor#getDefaultContentTypeId()
	 */
	@Override
	public String getDefaultContentTypeId() {
		return XMI_CONTENT_TYPE_ID;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor#getCompatibleContentTypeIds()
	 */
	@Override
	public List<String> getCompatibleContentTypeIds() {
		List<String> contentTypeIds = new ArrayList<String>();
		contentTypeIds.add(XMI_BASE_CONTENT_TYPE_ID + "_2_0_0"); //$NON-NLS-1$
		contentTypeIds.add(XMI_BASE_CONTENT_TYPE_ID + "_2_1_0"); //$NON-NLS-1$
		return contentTypeIds;
	}
}
