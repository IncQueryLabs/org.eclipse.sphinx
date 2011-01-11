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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An implementation of SAX {@link ErrorHandler} interface for EMF {@link Resource}s. Converts incoming warnings,
 * errors, and fatal errors to {@link XMIException}s and adds them to the {@link Resource}'s
 * {@link Resource#getWarnings() warning} and {@link Resource#getErrors() error} lists.
 * 
 * @see ErrorHandler
 * @see Resource#getWarnings()
 * @see Resource#getErrors()
 */
public class ResourceErrorHandler implements ErrorHandler {

	private Resource resource;

	public ResourceErrorHandler(Resource resource) {
		Assert.isNotNull(resource);
		this.resource = resource;
	}

	protected XMIException toXMIException(SAXParseException exception) {
		String resourceURI = resource.getURI() == null ? "" : resource.getURI().toString(); //$NON-NLS-1$
		Exception wrappedException = exception.getException() == null ? exception : exception.getException();
		String location = exception.getSystemId() == null ? resourceURI : exception.getSystemId();
		return new XMIException(wrappedException, location, exception.getLineNumber(), exception.getColumnNumber());
	}

	public void warning(SAXParseException exception) throws SAXException {
		resource.getWarnings().add(toXMIException(exception));
	}

	public void error(SAXParseException exception) throws SAXException {
		resource.getErrors().add(toXMIException(exception));
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		resource.getErrors().add(toXMIException(exception));
		throw exception;
	}
}
