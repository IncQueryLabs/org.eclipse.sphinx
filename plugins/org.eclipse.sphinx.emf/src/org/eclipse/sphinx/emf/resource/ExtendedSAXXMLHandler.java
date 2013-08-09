/**
 * <copyright>
 * 
 * Copyright (c) 2008-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMLHandler;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ExtendedSAXXMLHandler extends SAXXMLHandler {

	protected IMetaModelDescriptor resourceVersion = null;

	public ExtendedSAXXMLHandler(XMLResource xmlResource, XMLHelper helper, Map<?, ?> options) {
		super(xmlResource, helper, options);

		Object value = options.get(ExtendedResource.OPTION_RESOURCE_VERSION_DESCRIPTOR);
		if (value instanceof IMetaModelDescriptor) {
			resourceVersion = (IMetaModelDescriptor) value;
		}
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#resolveEntity(java.lang.String, java.lang.String)
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		try {
			// Try to resolve entity as is; this will work out in case that entity has already been resolved by
			// underlying XML parser or is resolvable against resource under load and actually exists at the
			// resolved location
			return super.resolveEntity(publicId, systemId);
		} catch (SAXException ex) {
			// Try to resolve only the last segment of system id in case there is any
			String lastSegment = URI.createURI(systemId).lastSegment();
			if (lastSegment != null) {
				try {
					URI uri = URI.createURI(lastSegment);
					uri = helper.resolve(uri, resourceURI);
					InputStream inputStream;
					inputStream = getURIConverter().createInputStream(uri, null);
					InputSource result = new InputSource(inputStream);
					result.setPublicId(publicId);
					result.setSystemId(systemId);
					return result;
				} catch (Exception ex1) {
					// Ignore exception
				}
			}

			// Try to resolve by relying on resource namespace instead of system id
			String resourceNamespace = null;
			if (resourceVersion != null) {
				resourceNamespace = resourceVersion.getNamespace();
			} else {
				resourceNamespace = EcoreResourceUtil.readModelNamespace(xmlResource);
			}
			if (resourceNamespace != null) {
				try {
					URI uri = URI.createURI(resourceNamespace);
					uri = helper.resolve(uri, resourceURI);
					InputStream inputStream;
					inputStream = getURIConverter().createInputStream(uri, null);
					InputSource result = new InputSource(inputStream);
					result.setPublicId(publicId);
					result.setSystemId(systemId);
					return result;
				} catch (Exception ex1) {
					// Ignore exception (rather than throwing a SAXException and aborting the load process)
				}
			}

			// Ignore exception (rather than re-throwing SAXException and aborting the load process)
			return null;
		}
	}

	/*
	 * Overridden to enable use of workspace-aware URIConverter
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#getURIConverter()
	 */
	@Override
	protected URIConverter getURIConverter() {
		return EcoreResourceUtil.getURIConverter(resourceSet);
	}

	/*
	 * Overridden to make sure that parsing may be continued even in case of fatal errors (typically XML well-formedness
	 * problems or I/O errors) if the underlying SAX parser has been configured to operate that way. In case that Apache
	 * Xerces parser is used the parser feature http://apache.org/xml/features/continue-after-fatal-error needs to be
	 * set to true for this purpose.
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		try {
			super.fatalError(e);
		} catch (SAXException ex) {
			// Ignore exception
		}
	}

	@Override
	protected void processTopObject(EObject object) {
		if (object != null) {
			if (deferredExtent != null) {
				deferredExtent.add(object);
			} else {
				extent.addUnique(object);
			}

			if (extendedMetaData != null && !mixedTargets.isEmpty()) {
				FeatureMap featureMap = mixedTargets.pop();
				EStructuralFeature target = null;

				EList<EAttribute> allAttributes = object.eClass().getEAllAttributes();
				for (Object element : allAttributes) {
					EAttribute attribute = (EAttribute) element;
					if (ExtendedResourceConstants.OUTER_CONTENT_ATTRIBUTE_NAME.equals(attribute.getName())) {
						target = attribute;
						break;
					}
				}

				if (target == null) {
					target = extendedMetaData.getMixedFeature(object.eClass());
				}

				if (target != null) {
					FeatureMap otherFeatureMap = (FeatureMap) object.eGet(target);
					for (FeatureMap.Entry entry : new ArrayList<FeatureMap.Entry>(featureMap)) {
						// Ignore a whitespace only text entry at the beginning.
						//
						if (entry.getEStructuralFeature() != XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT
								|| !"".equals(XMLTypeUtil.normalize(entry.getValue().toString(), true))) { //$NON-NLS-1$
							otherFeatureMap.add(entry.getEStructuralFeature(), entry.getValue());
						}
					}
				}
				text = null;
			}
		}

		processObject(object);
	}

	/*
	 * Overridden to enrich proxy URIs being created with context information required to honor their {@link
	 * IResourceScope resource scope}s when they are being resolved and to support the resolution of proxified
	 * references between objects from different metamodels.
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#handleProxy(org.eclipse.emf.ecore.InternalEObject,
	 * java.lang.String)
	 */
	@Override
	protected void handleProxy(InternalEObject proxy, String uriLiteral) {
		super.handleProxy(proxy, uriLiteral);

		// Augment the proxy's URI to a context-aware proxy URI
		ExtendedResourceSetImpl extendedResourceSet = (ExtendedResourceSetImpl) resourceSet;

		URI contextURI = null;
		IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(xmlResource);
		if (modelDescriptor != null) {
			contextURI = URI.createPlatformResourceURI(modelDescriptor.getRoot().toString(), true);
		}

		extendedResourceSet.augmentToContextAwareURI(proxy, contextURI);
	}
}
