/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Added/Updated javadoc
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.SAXParser;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLString;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

/**
 * A base implementation which provides default implementations of the IModelConverter methods.
 */
public abstract class AbstractModelConverter implements IModelConverter {

	public abstract Object getResourceVersionFromPreferences(IProject project);

	public boolean isLoadConverterFor(XMLResource resource, Map<?, ?> options) {
		if (resource == null) {
			return false;
		}

		IMetaModelDescriptor mmVersionDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resource);
		if (!getMetaModelVersionDescriptor().equals(mmVersionDescriptor)) {
			return false;
		}

		String resourceNamespace = EcoreResourceUtil.readModelNamespace(resource);
		if (resourceNamespace == null) {
			return false;
		}
		boolean resourceNamespaceMatch = resourceNamespace.matches(getResourceVersionDescriptor().getNamespace());
		if (!resourceNamespaceMatch) {
			resourceNamespaceMatch = resourceNamespace.matches(getResourceVersionDescriptor().getEPackageNsURIPattern());
		}
		return resourceNamespaceMatch;
	}

	public boolean isSaveConverterFor(XMLResource resource, Map<?, ?> options) {
		if (resource == null) {
			return false;
		}

		// Check if meta-model version matches
		IMetaModelDescriptor mmVersionDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resource);
		if (!getMetaModelVersionDescriptor().equals(mmVersionDescriptor)) {
			return false;
		}

		// Check if resource version is available in project properties or save options and matches; make sure that
		// project property is given preference over resource save option
		Object value = null;
		IFile file = EcorePlatformUtil.getFile(resource);
		if (file != null) {
			value = getResourceVersionFromPreferences(file.getProject());
		}
		if (!(value instanceof IMetaModelDescriptor)) {
			value = options.get(OPTION_RESOURCE_VERSION_DESCRIPTOR);
			if (!(value instanceof IMetaModelDescriptor)) {
				return false;
			}
		}
		IMetaModelDescriptor resourceVersionDescriptor = (IMetaModelDescriptor) value;
		if (!getResourceVersionDescriptor().equals(resourceVersionDescriptor)) {
			return false;
		}

		return true;
	}

	public InputSource convertLoad(XMLResource resource, InputStream inputStream, Map<?, ?> options) {
		InputStream converted = doConvertLoad(resource, inputStream, options);
		if (converted != inputStream) {
			// This save option will enable us to detect that we have to convert back to another version
			resource.getDefaultSaveOptions().put(OPTION_RESOURCE_VERSION_DESCRIPTOR, getResourceVersionDescriptor());
		}
		return new InputSource(converted);
	}

	protected InputStream doConvertLoad(XMLResource resource, InputStream inputStream, Map<?, ?> options) {
		try {
			// Set features and properties
			SAXBuilder builder = new SAXBuilder(SAXParser.class.getName());
			@SuppressWarnings("unchecked")
			Map<String, Boolean> parserFeatures = (Map<String, Boolean>) options.get(XMLResource.OPTION_PARSER_FEATURES);
			if (parserFeatures != null) {
				for (String feature : parserFeatures.keySet()) {
					builder.setFeature(feature, parserFeatures.get(feature).booleanValue());
				}
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> parserProperties = (Map<String, Object>) options.get(XMLResource.OPTION_PARSER_PROPERTIES);
			if (parserProperties != null) {
				for (String property : parserProperties.keySet()) {
					builder.setProperty(property, parserProperties.get(property));
				}
			}

			// Use custom extended error handler wrapper enabling concise distinction between well-formedness, validity
			// and integrity problems
			/*
			 * !! Important Note !! Requires org.apache.xerces parser (but not com.sun.org.apache.xerces parser) to be
			 * used.
			 */
			builder.setProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY, new ExtendedErrorHandlerWrapper());

			// Use custom error handler enabling anomalies to be redirected to resource errors and warnings rather than
			// ending up in exceptions being raised
			builder.setErrorHandler(new ResourceErrorHandler(resource));

			final Document document = builder.build(inputStream);
			List<Element> elementsToConvert = new ArrayList<Element>();
			for (Iterator<?> iterator = document.getRootElement().getDescendants(); iterator.hasNext();) {
				Object next = iterator.next();
				if (next instanceof Element) {
					Element element = (Element) next;
					elementsToConvert.add(element);
				}
			}
			for (Element element : elementsToConvert) {
				convertLoadElement(element, options);
			}

			PipedInputStream pipedInputStream = new PipedInputStream();
			final PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
			final XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						out.output(document, pipedOutputStream);
					} catch (IOException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					} finally {
						try {
							pipedOutputStream.close();
						} catch (IOException ex) {
							PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
						}
					}
				}
			}, "Converting " + resource.getURI().toString()); //$NON-NLS-1$
			thread.start();

			return pipedInputStream;
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}

		// Try to survive without model conversion
		return inputStream;
	}

	protected abstract void convertLoadElement(Element element, Map<?, ?> options);

	public void convertSave(final XMLString xml, final int flushThreshold, URI uri, OutputStream outputStream, final String encoding,
			final XMLHelper helper, Map<?, ?> options) {
		try {
			PipedInputStream pipedInputStream = new PipedInputStream();
			final PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						if ("US-ASCII".equals(encoding) || "ASCII".equals(encoding)) { //$NON-NLS-1$ //$NON-NLS-2$
							xml.writeAscii(pipedOutputStream, flushThreshold);
							pipedOutputStream.flush();
						} else {
							OutputStreamWriter outputStreamWriter = new OutputStreamWriter(pipedOutputStream, helper.getJavaEncoding(encoding));
							xml.write((Writer) outputStreamWriter, flushThreshold);
							outputStreamWriter.flush();
						}
					} catch (IOException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					} finally {
						try {
							pipedOutputStream.close();
						} catch (IOException ex) {
							PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
						}
					}
				}
			}, "Converting " + uri.toString()); //$NON-NLS-1$
			thread.start();

			final Document document = new SAXBuilder().build(pipedInputStream);
			List<Element> elementsToConvert = new ArrayList<Element>();
			for (Iterator<?> iterator = document.getRootElement().getDescendants(); iterator.hasNext();) {
				Object next = iterator.next();
				if (next instanceof Element) {
					Element element = (Element) next;
					elementsToConvert.add(element);
				}
			}
			for (Element element : elementsToConvert) {
				convertSaveElement(element, options);
			}
			new XMLOutputter(Format.getPrettyFormat()).output(document, outputStream);
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	protected abstract void convertSaveElement(Element element, Map<?, ?> options);

	public void addExtraAttributesToSavedRootElement(XMLString rootElement, Map<?, ?> options) {
		// Do nothing by default
	}

	public void dispose() {
		// Do nothing by default
	}
}
