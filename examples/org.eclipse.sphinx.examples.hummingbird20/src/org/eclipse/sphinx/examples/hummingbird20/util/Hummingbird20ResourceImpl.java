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
package org.eclipse.sphinx.examples.hummingbird20.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapter;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.resource.ExtendedSAXXMIHandler;
import org.eclipse.sphinx.emf.resource.ExtendedXMILoadImpl;
import org.eclipse.sphinx.emf.resource.ExtendedXMISaveImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The <b>Resource </b> associated with the package.
 * 
 * @see org.eclipse.sphinx.examples.hummingbird20.util.Hummingbird20ResourceFactoryImpl
 * @generated
 */
public class Hummingbird20ResourceImpl extends XMIResourceImpl {

	/**
	 * {@link Adapter} providing Hummingbird-specific implementation of {@link ExtendedResource extended resource}
	 * services.
	 * 
	 * @see ExtendedResource
	 */
	protected ExtendedResourceAdapter extendedResource;

	/**
	 * Creates an instance of the resource.
	 * 
	 * @param uri
	 *            the URI of the new resource.
	 * @generated NOT
	 */
	public Hummingbird20ResourceImpl(URI uri) {
		super(uri);

		// Install adapter providing Hummingbird-specific implementation of extended resource services
		extendedResource = new ExtendedHummingbirdResourceAdapter();
		eAdapters().add(extendedResource);
	}

	/*
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#unloaded(org.eclipse.emf.ecore.InternalEObject)
	 */
	@Override
	protected void unloaded(InternalEObject internalEObject) {
		// Delegate to implementation provided by extended resource for enabling memory-optimized unloading
		extendedResource.unloaded(internalEObject);
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLLoad()
	 */
	@Override
	protected XMLLoad createXMLLoad() {
		// Use extended XMILoad implementation for XMI to include support for on-the-fly resource migration,
		// resource-centric problem handling, enhanced entity resolution and ignorable whitespace suppression
		return new ExtendedXMILoadImpl(createXMLHelper()) {
			@Override
			protected DefaultHandler makeDefaultHandler() {
				return new ExtendedSAXXMIHandler(resource, helper, options) {
					@Override
					protected void handleProxy(InternalEObject proxy, String uriLiteral) {
						URI proxyURI = ExtendedHummingbirdResourceAdapter.createHummingbirdURI(uriLiteral);
						proxy.eSetProxyURI(proxyURI);
						extendedResource.augmentToContextAwareProxy(proxy);
					}
				};
			}
		};
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLSave()
	 */
	@Override
	protected XMLSave createXMLSave() {
		// Use extended XMISave implementation for XMI to include support for on-the-fly resource migration and enhanced
		// schema location support
		return new ExtendedXMISaveImpl(createXMLHelper());
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
	 */
	@Override
	protected XMLHelper createXMLHelper() {
		// Enable URIs used for serializing cross-document references be customized through extended resource service
		return new XMIHelperImpl(this) {
			/*
			 * @see org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl#getHREF(org.eclipse.emf.ecore.resource.Resource,
			 * org.eclipse.emf.ecore.EObject)
			 */
			@Override
			public String getHREF(EObject obj) {
				ExtendedResource otherExtendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(obj.eResource());
				if (otherExtendedResource != null) {
					String href = otherExtendedResource.getHREF(obj);
					if (href != null) {
						return href;
					}
				}
				return super.getHREF(obj);
			}
		};
	}
} // Hummingbird20ResourceImpl
