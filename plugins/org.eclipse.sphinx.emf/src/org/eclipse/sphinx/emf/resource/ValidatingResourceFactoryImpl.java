/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys, OpenSynergy and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     OpenSynergy - Enhancement for supporting usage of resource version 
 *                   specific schemas instead of all the time applying schema 
 *                   corresponding to version of metamodel implementation with 
 *                   which the different resource versions are loaded
 *     See4sys - Generalization for being able to use on-the-fly schema validation
 *               for arbitrary metamodels
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xerces.impl.Constants;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.internal.resource.PluginResourceResolver;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A reusable implementation of {@link Resource.Factory} providing support for creating {@link XMLResource resource}s
 * which get validated against an applicable {@link Schema schema} while being loaded.
 */
public class ValidatingResourceFactoryImpl extends ResourceFactoryImpl {

	/**
	 * The {@link Plugin plug-in} containing built-in schema files.
	 */
	private Plugin schemaPlugin;

	/**
	 * The namespaces and {@link #getSchemaPlugin() schema plug-in} relative paths of built-in schema files.
	 */
	private Map<String, String> nsToSchemaPathMap;

	/**
	 * The namespaces and {@link #getSchemaPlugin() schema plug-in} relative paths of external schema resources.
	 */
	private Map<String, String> nsToExternalSchemaResourcePathMap;

	/**
	 * The cached location to {@link Schema schema} mappings.
	 */
	private Map<String, Schema> locationToSchemaCache = new WeakHashMap<String, Schema>();

	/**
	 * Returns the {@link Plugin plug-in} which contains built-in {@link Schema schema}s to be used for validating the
	 * {@link XMLResource resource} to be loaded if no custom {@link Schema schema} is specified by the XSI schema
	 * location attribute on the {@link XMLResource resource}'s root element.
	 * 
	 * @return The {@link Plugin plug-in} containing built-in schema files.
	 * @see #setSchemaPlugin(Plugin)
	 */
	public Plugin getSchemaPlugin() {
		return schemaPlugin;
	}

	/**
	 * Sets the {@link Plugin plug-in} which contains built-in {@link Schema schema}s to be used for validating the
	 * {@link XMLResource resource} to be loaded if no custom {@link Schema schema} is specified by the XSI schema
	 * location attribute on the {@link XMLResource resource}'s root element.
	 * 
	 * @param schemaPlugin
	 *            The {@link Plugin plug-in} containing built-in schema files.
	 * @see #getSchemaPlugin()
	 */
	public void setSchemaPlugin(Plugin schemaPlugin) {
		this.schemaPlugin = schemaPlugin;
	}

	/**
	 * Returns the namespaces and paths of built-in {@link Schema schema}s provided within the
	 * {@link #getSchemaPlugin() schema plug-in}.
	 * 
	 * @return The namespaces and {@link #getSchemaPlugin() schema plug-in} relative paths of built-in schema files.
	 */
	public Map<String, String> getNsToSchemaPathMap() {
		if (nsToSchemaPathMap == null) {
			nsToSchemaPathMap = new HashMap<String, String>();
		}
		return nsToSchemaPathMap;
	}

	/**
	 * Returns the namespaces and paths of external resources provided within the {@link #getSchemaPlugin() schema
	 * plug-in} which may be <include>d or <import>ed, or otherwise referenced from built-in {@link Schema schema}s.
	 * 
	 * @return The namespaces and {@link #getSchemaPlugin() schema plug-in} relative paths of external schema resources.
	 */
	public Map<String, String> getNsToExternalSchemaResourcePathMap() {
		if (nsToExternalSchemaResourcePathMap == null) {
			nsToExternalSchemaResourcePathMap = new HashMap<String, String>();
		}
		return nsToExternalSchemaResourcePathMap;
	}

	/**
	 * Activates parser validation during loading of given {@link XMLResource resource}. Tries to use custom
	 * {@link Schema schema} specified by the XSI schema location attribute on the {@link XMLResource resource}'s root
	 * element, if any, or uses matching built-in {@link Schema schema} from {@link #getSchemaPlugin() schema plug-in}
	 * otherwise.
	 * 
	 * @param resource
	 *            The {@link XMLResource resource} on act upon. Must not be <code>null</code>.
	 * @see #getSchemaPlugin()
	 * @see #getNsToSchemaPathMap()
	 * @see #getNsToExternalSchemaResourcePathMap()
	 */
	protected void activateSchemaValidation(XMLResource resource) {
		// Do nothing if resource has just been created in memory but not yet saved or if it is a permanent in-memory
		// resource
		if (!EcoreResourceUtil.exists(resource.getURI())) {
			return;
		}

		// Load custom schema specified by XSI schema location if available
		Schema schema = null;
		String namespace = EcoreResourceUtil.readModelNamespace(resource);
		Map<String, String> schemaLocationEntries = EcoreResourceUtil.readSchemaLocationEntries(resource);
		String schemaLocation = schemaLocationEntries.get(namespace);
		if (schemaLocation != null) {
			schema = getSchemaFromLocation(resource, schemaLocation);
		}

		// Load matching built-in schema if custom schema could not be loaded or no such has been specified
		if (schema == null) {
			Map<String, String> nsToSchemaPathMap = getNsToSchemaPathMap();
			if (nsToSchemaPathMap != null) {
				String schemaPath = nsToSchemaPathMap.get(namespace);
				if (schemaPath != null) {
					schema = getSchemaFromPlugin(getSchemaPlugin(), schemaPath, getNsToExternalSchemaResourcePathMap());
				} else {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), new RuntimeException("No schema path found for namespace '" + namespace //$NON-NLS-1$
							+ "'. Skipping activation of parser validation during resource loading.")); //$NON-NLS-1$
				}

				// TODO Replace Schema-based by features/properties-based XSD validation configuration as follows; this
				// way it will also automatically become available when opting for parser pool (see ExtendedXMLLoadImpl
				// for details)
				// parserFeatures.put(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE, true);
				// parserFeatures.put(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE, true);

				// URL schemaLocationURL = FileLocator.find(getSchemaPlugin().getBundle(), new Path(schemaPath), null);
				// if (schemaLocationURL != null) {
				// Map<String, Object> parserProperties = new HashMap<String, Object>();
				// parserProperties
				// .put(Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION, namespace + " " +
				// schemaLocationURL.toString());
				// // TODO Check if this can help to avoid that we have to override
				// // org.eclipse.emf.ecore.xmi.impl.XMLHandler.resolveEntity(String, String); is seems to work somehow
				// // but schemas including other schemas report errors on valid documents
				// // for (String ns : getNsToExternalSchemaResourcePathMap().keySet()) {
				// // URL schemaResourceURL = FileLocator.find(getSchemaPlugin().getBundle(), new
				// // Path(getNsToExternalSchemaResourcePathMap().get(
				// // ns)), null);
				// // if (schemaResourceURL != null) {
				// // parserProperties.put(Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION, ns + " "
				// // + schemaResourceURL.toString());
				// // }
				// // }
				// resource.getDefaultLoadOptions().put(XMLResource.OPTION_PARSER_PROPERTIES, parserProperties);
				// }
			}
		}

		// Configure parser to perform validation using given schema
		if (schema != null) {
			installSchema(resource, schema);
		}
	}

	/**
	 * Retrieves {@link Schema schema} from specified location.
	 * 
	 * @param resource
	 *            The {@link XMLResource resource} to be used for logging problems that might occur during
	 *            {@link Schema schema} retrieval. Must not be <code>null</code>.
	 * @param schemaLocation
	 *            The location of the {@link Schema schema} to be retrieved. Must not be <code>null</code>.
	 * @return The {@link Schema schema} behind specified location, or <code>null</code> if no such could be retrieved.
	 */
	protected Schema getSchemaFromLocation(XMLResource resource, String schemaLocation) {
		Assert.isNotNull(resource);
		Assert.isNotNull(schemaLocation);

		Schema schema = locationToSchemaCache.get(schemaLocation);
		if (schema == null) {
			// Create schema location URI
			URI schemaLocationURI = URI.createURI(schemaLocation, true);
			// Try to convert to platform:/resource URI
			schemaLocationURI = EcoreResourceUtil.convertToPlatformResourceURI(schemaLocationURI);
			if (!schemaLocationURI.isPlatformResource()) {
				// Try to convert to file: URI
				schemaLocationURI = EcoreResourceUtil.convertToAbsoluteFileURI(schemaLocationURI);
				if (!schemaLocationURI.isFile() || schemaLocationURI.isRelative()) {
					// Try to resolve URI against given resource
					if (schemaLocationURI.isRelative()) {
						schemaLocationURI = schemaLocationURI.resolve(resource.getURI());
					}
				}
			}

			// Load schema behind schema location URI
			InputStream inputStream = null;
			try {
				URIConverter uriConverter = EcoreResourceUtil.getURIConverter(resource.getResourceSet());
				inputStream = uriConverter.createInputStream(schemaLocationURI);
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				schema = factory.newSchema(new StreamSource(inputStream));
				locationToSchemaCache.put(schemaLocation, schema);
			} catch (IOException ex) {
				// FIXME Improve exception handling along with replacement of Schema-based by features/properties-based
				// XSD validation configuration (the current algorithm is inadequate: we get a
				// org.eclipse.core.internal.resources.ResourceException wrapped into a Resource$IOWrappedException if
				// the schema file doesn't exist)
				if (ex instanceof FileNotFoundException) {
					String msg = NLS.bind(Messages.error_schemaFileReferencedByXsiSchemaLocationNotFound, schemaLocation);
					resource.getWarnings().add(new XMLIntegrityException(msg, resource.getURI().toString(), 2, 0));
				} else {
					String msg = NLS.bind(Messages.error_schemaFileReferencedByXsiSchemaLocationNotAccessible, schemaLocation);
					resource.getWarnings().add(new XMLIntegrityException(msg, resource.getURI().toString(), 2, 0));
				}
			} catch (SAXException ex) {
				if (ex instanceof SAXParseException) {
					String msg = NLS.bind(Messages.error_locatableProblemWhileParsingSchemaFileReferencedByXsiSchemaLocation, new Object[] {
							schemaLocation, ((SAXParseException) ex).getLineNumber(), ((SAXParseException) ex).getColumnNumber() });
					resource.getWarnings().add(new XMLIntegrityException(msg, resource.getURI().toString(), 2, 0));
				} else {
					String msg = NLS.bind(Messages.error_generalProblemWhileParsingSchemaFileReferencedByXsiSchemaLocation, schemaLocation);
					resource.getWarnings().add(new XMLIntegrityException(msg, resource.getURI().toString(), 2, 0));
				}
			} finally {
				ExtendedPlatform.safeClose(inputStream);
			}
		}
		return schema;
	}

	/**
	 * Retrieves {@link Schema schema} from specified {@link Plugin plug-in} and path. Uses provided map of namespaces
	 * and paths of external resources to resolve resources which may be <include>d or <import>ed, or otherwise
	 * referenced from the {@link Schema schema} in question.
	 * 
	 * @param schemaPlugin
	 *            The {@link Plugin plug-in} which contains the {@link Schema schema} to be retrieved. Must not be
	 *            <code>null</code>.
	 * @param schemaPath
	 *            The path of the schema file provided within the <code>schemaPlugin</code>. Must not be
	 *            <code>null</code>. Must not be <code>null</code>.
	 * @param nsToExternalSchemaResourcePathMap
	 *            The namespaces and paths of external resources provided within the <code>schemaPlugin</code> which may
	 *            be <include>d or <import>ed, or otherwise referenced from the schema file specified by
	 *            <code>schemaPlugin</code> and <code>schemaPath</code>.
	 * @return The {@link Schema schema} from specified {@link Plugin plug-in} and path, or <code>null</code> if no such
	 *         could be retrieved.
	 */
	protected Schema getSchemaFromPlugin(Plugin schemaPlugin, String schemaPath, Map<String, String> nsToExternalSchemaResourcePathMap) {
		Assert.isNotNull(schemaPlugin);
		Assert.isNotNull(schemaPath);

		// Use schema location alias for schema caching
		Bundle bundle = schemaPlugin.getBundle();
		String schemaLocationAlias = schemaPath.concat("@").concat(bundle.getSymbolicName()); //$NON-NLS-1$
		Schema schema = locationToSchemaCache.get(schemaLocationAlias);
		if (schema == null) {
			// Create URL of real schema location
			URL schemaLocationURL = FileLocator.find(bundle, new Path(schemaPath), null);
			if (schemaLocationURL != null) {
				// Load schema behind real schema location URL
				InputStream inputStream = null;
				try {
					inputStream = schemaLocationURL.openStream();
					SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					if (nsToExternalSchemaResourcePathMap != null && nsToExternalSchemaResourcePathMap.size() > 0) {
						factory.setResourceResolver(new PluginResourceResolver(schemaPlugin, nsToExternalSchemaResourcePathMap));
					}
					schema = factory.newSchema(new StreamSource(inputStream));
					locationToSchemaCache.put(schemaLocationAlias, schema);
				} catch (IOException ex) {
					String msg = NLS.bind(Messages.error_schemaFileInPluginNotAccessible, schemaPath, schemaPlugin);
					PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException(msg));
				} catch (SAXException ex) {
					if (ex instanceof SAXParseException) {
						String msg = NLS.bind(Messages.error_locatableProblemWhileParsingSchemaFileInPlugin, new Object[] { schemaPath, schemaPlugin,
								((SAXParseException) ex).getLineNumber(), ((SAXParseException) ex).getColumnNumber() });
						PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException(msg));
					} else {
						String msg = NLS.bind(Messages.error_generalProblemWhileParsingSchemaFileInPlugin, schemaPath, schemaPlugin);
						PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException(msg));
					}
				} finally {
					ExtendedPlatform.safeClose(inputStream);
				}
			} else {
				String msg = NLS.bind(Messages.error_schemaFileInPluginNotFound, schemaPath, schemaPlugin);
				PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException(msg));
			}
		}
		return schema;
	}

	/**
	 * Configures given {@link XMLResource resource} to be validated against given {@link Schema schema} while being
	 * loaded.
	 * 
	 * @param resource
	 *            The {@link XMLResource resource} to act upon.
	 * @param schema
	 *            The {@link Schema schema} to be used for validating the {@link XMLResource resource}.
	 */
	protected void installSchema(XMLResource resource, Schema schema) {
		Assert.isNotNull(resource);
		Assert.isNotNull(schema);

		@SuppressWarnings("unchecked")
		Map<String, Object> parserProperties = (Map<String, Object>) resource.getDefaultLoadOptions().get(XMLResource.OPTION_PARSER_PROPERTIES);
		if (parserProperties == null) {
			parserProperties = new HashMap<String, Object>();
			resource.getDefaultLoadOptions().put(XMLResource.OPTION_PARSER_PROPERTIES, parserProperties);

		}
		parserProperties.put(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE, schema);
	}
}
