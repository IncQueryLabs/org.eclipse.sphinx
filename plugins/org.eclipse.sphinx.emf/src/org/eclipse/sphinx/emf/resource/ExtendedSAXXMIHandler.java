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
package org.eclipse.sphinx.emf.resource;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMLHandler;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ExtendedSAXXMIHandler extends SAXXMIHandler {

	protected IMetaModelDescriptor resourceVersion = null;

	public ExtendedSAXXMIHandler(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
		super(xmiResource, helper, options);

		Object value = options.get(ExtendedResource.OPTION_RESOURCE_VERSION_DESCRIPTOR);
		if (value instanceof IMetaModelDescriptor) {
			resourceVersion = (IMetaModelDescriptor) value;
		}

		// Workaround for potential bug in org.apache.xerces.impl.xs.XMLSchemaValidator.addDefaultAttributes(QName,
		// XMLAttributes, XSAttributeGroupDecl) (line 3027) which attempts to add xmi:version as default attribute if
		// not present yet but misses to initialize the attribute's prefix field
		notFeatures.add(XMIResource.VERSION_NAME);
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
	 * Overridden to prevent whitespace from being captured in mixed attributes
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) {
		if (!isIgnorableWhitespace(ch, start, length) || text != null && text.length() > 0) {
			super.characters(ch, start, length);
		}
	}

	/**
	 * Returns if the given chunk of character data consists of ignorable whitespace.
	 * <p>
	 * Can be used for filtering character data reported to {@link XMLHandler#characters(char[], int, int)}.
	 * </p>
	 * 
	 * @param ch
	 *            The characters from the XML document.
	 * @param start
	 *            The start position in the array.
	 * @param length
	 *            The number of characters to read from the array.
	 * @return <code>true</code> if given chunk of character data consists of ignorable whitespace, <code>false</code>
	 *         otherwise.
	 */
	protected boolean isIgnorableWhitespace(char[] ch, int start, int length) {
		for (int i = start; i < start + length; i++) {
			if (ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t' && ch[i] != ' ') {
				return false;
			}
		}
		return true;
	}

	/*
	 * Overridden to make sure that parsing is continued even in case of fatal errors (typically XML well-formedness
	 * problems). The idea is to always load XML documents as far as possible rather than not loading the entire
	 * document just because a potentially small part of it is not good.
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
}
