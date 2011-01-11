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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.apache.xerces.impl.Constants;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.emf.ecore.xmi.XMLDefaultHandler;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLParserPool;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An extended {@link XMLLoad} implementation that provides support for on-the-fly validation of {@link XMLResource
 * resource}s against a {@link Schema schema} and on-the-fly migration of older {@link XMLResource resource}s to
 * instances of newer metamodel implementations using {@link IModelConverter model converter}s.
 * <p>
 * The {@link Schema schema} to be used for on-the-fly validation must be added as
 * <code>http://java.sun.com/xml/jaxp/properties/schemaSource</code> entry to the
 * {@link XMLResource#OPTION_PARSER_PROPERTIES parser properties option} of the affected {@link XMLResource resource}.
 * Example:
 * 
 * <pre>
 * Schema schema = ... // instance of javax.xml.validation.Schema
 * XMLResource resource = ... // instance of org.eclipse.emf.ecore.xmi.XMLResource
 * Map&lt;String, Object&gt; parserProperties = new HashMap&lt;String, Object&gt;();
 * parserProperties.put(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE, schema);
 * resource.getDefaultLoadOptions().put(XMLResource.OPTION_PARSER_PROPERTIES, parserProperties);
 * </pre>
 * 
 * </p>
 * <p>
 * The {@link IModelConverter model converter} to be used for on-the-fly resource migration must be contributed to the
 * <code>org.eclipse.sphinx.emf.modelConverters</code> extension point.
 * </p>
 */
public class ExtendedXMLLoadImpl extends XMLLoadImpl {

	/*
	 * @see XMLLoadImpl#XMLLoadImpl(XMLHelper)
	 */
	public ExtendedXMLLoadImpl(XMLHelper helper) {
		super(helper);
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl#load(org.eclipse.emf.ecore.xmi.XMLResource, java.io.InputStream,
	 * java.util.Map)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void load(XMLResource resource, InputStream inputStream, Map<?, ?> options) throws IOException {
		if (inputStream instanceof URIConverter.Readable) {
			URIConverter.Readable readable = (URIConverter.Readable) inputStream;
			resource.setEncoding(readable.getEncoding());

			InputSource inputSource = new InputSource(readable.asReader());
			if (resource.getURI() != null) {
				String resourceURI = resource.getURI().toString();
				inputSource.setPublicId(resourceURI);
				inputSource.setSystemId(resourceURI);
				inputSource.setEncoding(resource.getEncoding());
			}
			load(resource, inputSource, options);
			return;
		}

		this.resource = resource;
		is = inputStream;
		this.options = options;
		XMLParserPool pool = (XMLParserPool) options.get(XMLResource.OPTION_USE_PARSER_POOL);
		@SuppressWarnings("unchecked")
		Map<String, Boolean> parserFeatures = (Map<String, Boolean>) options.get(XMLResource.OPTION_PARSER_FEATURES);
		@SuppressWarnings("unchecked")
		Map<String, Object> parserProperties = (Map<String, Object>) options.get(XMLResource.OPTION_PARSER_PROPERTIES);
		parserFeatures = parserFeatures == null ? new HashMap<String, Boolean>() : parserFeatures;
		parserProperties = parserProperties == null ? new HashMap<String, Object>() : parserProperties;

		// TODO Make sure that non-validating parsing is performed when a model converter is used
		// parserFeatures.remove(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE, true);
		// parserFeatures.remove(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE, true);

		// Use custom extended error handler wrapper enabling concise distinction between well-formedness, validity and
		// integrity problems
		/*
		 * !! Important Note !! Requires org.apache.xerces parser (but not com.sun.org.apache.xerces parser) to be used.
		 */
		parserProperties.put(Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY, new ExtendedErrorHandlerWrapper());

		// HACK: reading encoding
		String encoding = null;
		if (!Boolean.FALSE.equals(options.get(XMLResource.OPTION_USE_DEPRECATED_METHODS))) {
			encoding = getEncoding();
			resource.setEncoding(encoding);
		}

		SAXParser parser = null;
		DefaultHandler handler = null;
		try {
			if (pool != null) {
				// Remove 'http://java.sun.com/xml/jaxp/properties/schemaSource' property since it is only used for
				// making a new parser and otherwise would cause a {@link SAXException} to be raised (see
				// javax.xml.parsers.SAXParserFactory.setSchema(Schema) for details)
				parserProperties.remove(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE);

				// Use the pool to retrieve the parser
				parser = pool.get(parserFeatures, parserProperties, Boolean.TRUE.equals(options.get(XMLResource.OPTION_USE_LEXICAL_HANDLER)));
				handler = (DefaultHandler) pool.getDefaultHandler(resource, this, helper, options);
			} else {
				Object schemaSource = parserProperties.get(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE);
				if (schemaSource instanceof Schema) {
					parser = makeParser((Schema) schemaSource);
				} else {
					parser = makeParser();
				}
				// Remove 'http://java.sun.com/xml/jaxp/properties/schemaSource' property since it is has accomplished
				// its mission now and otherwise would cause a {@link SAXException} to be raised (see
				// javax.xml.parsers.SAXParserFactory.setSchema(Schema) for details)
				parserProperties.remove(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE);

				handler = makeDefaultHandler(parser);

				// Set features and properties
				if (parserFeatures != null) {
					for (String feature : parserFeatures.keySet()) {
						parser.getXMLReader().setFeature(feature, parserFeatures.get(feature).booleanValue());
					}
				}
				if (parserProperties != null) {
					for (String property : parserProperties.keySet()) {
						parser.getXMLReader().setProperty(property, parserProperties.get(property));
					}
				}
			}

			// If an applicable model converter is around let it migrate the document prior to parsing it
			InputSource inputSource = null;
			IModelConverter applicableConverter = ModelConverterRegistry.INSTANCE.getLoadConverter(resource, options);
			if (applicableConverter != null) {
				try {
					inputSource = applicableConverter.convertLoad(resource, is, options);
				} catch (Exception ex) {
					XMIException exception = new XMIException(ex);
					resource.getErrors().add(exception);
				} finally {
					applicableConverter.dispose();
				}
			}

			if (inputSource == null) {
				inputSource = new InputSource(is);
			}
			if (resource.getURI() != null) {
				String resourceURI = resource.getURI().toString();
				inputSource.setPublicId(resourceURI);
				inputSource.setSystemId(resourceURI);
				inputSource.setEncoding(encoding);
			}

			// Set lexical handler
			if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_USE_LEXICAL_HANDLER))) {
				if (parserProperties == null || parserProperties.get(SAX_LEXICAL_PROPERTY) == null) {
					parser.setProperty(SAX_LEXICAL_PROPERTY, handler);
				}
			}

			parser.parse(inputSource, handler);
		} catch (SAXException exception) {
			// Ignore SAXParseExceptions; they indicate XML well-formedness and validity problems in resource to be
			// loaded and therefore should be handled in handleErrors() rather than giving raise to exceptions
			if (!(exception instanceof SAXParseException)) {
				if (exception.getException() != null) {
					throw new Resource.IOWrappedException(exception.getException());
				} else {
					throw new Resource.IOWrappedException(exception);
				}
			}
		} catch (ParserConfigurationException exception) {
			throw new Resource.IOWrappedException(exception);
		} finally {
			// Release parser back to the pool
			if (pool != null) {
				if (parser != null) {
					pool.release(parser, parserFeatures, parserProperties, Boolean.TRUE.equals(options.get(XMLResource.OPTION_USE_LEXICAL_HANDLER)));
				}
				if (handler != null) {
					pool.releaseDefaultHandler((XMLDefaultHandler) handler, options);
				}
			}

			helper = null;
			handleErrors();
		}
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl#makeParser()
	 */
	@Override
	protected SAXParser makeParser() throws ParserConfigurationException, SAXException {
		/*
		 * !! Important Note !! We must override makeParser() - even if we wouldn't have any functional changes to apply
		 * - in order to make sure that SAXParserFactory.newInstance() gets invoked from this plug-in which has a
		 * dependency to the org.apache.xerces plug-in and all its classes on the classpath. Otherwise we wouldn't
		 * obtain an instance of org.apache.xerces.jaxp.SAXParserFactoryImpl as intended but fall back to the default
		 * implementation com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl.
		 */
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newSAXParser();
	}

	/**
	 * Makes a validating {@link SAXParser parser} using specified {@link Schema schema}.
	 * 
	 * @param schema
	 *            The {@link Schema schema} against which parsed XML documents are to be validated.
	 * @return The newly created validating {@link SAXParser parser}.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	protected SAXParser makeParser(Schema schema) throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		return factory.newSAXParser();
	}

	protected DefaultHandler makeDefaultHandler(SAXParser parser) {
		return makeDefaultHandler();
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl#handleErrors()
	 */
	@Override
	protected void handleErrors() throws IOException {
		/*
		 * !! Important Note !! Don't raise exceptions when errors have been encountered during loading. This would
		 * entail that the resource in question doesn't get loaded at all. Instead we want to make sure that it can get
		 * loaded anyway - or at least as far as possible - and errors/warnings encountered during that should be
		 * reported in a less intrusive way, e.g., by attaching problem markers to the underlying file.
		 */
	}
}
