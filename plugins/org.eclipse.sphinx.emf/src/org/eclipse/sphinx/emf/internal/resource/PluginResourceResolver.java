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
package org.eclipse.sphinx.emf.internal.resource;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * An {@link LSResourceResolver} implementation enabling external resources referenced by XML documents to be resolved
 * by redirecting them to resources provided within a given plug-in. Can be used e.g., to
 * {@link SchemaFactory#setResourceResolver(LSResourceResolver) configure} a {@link SchemaFactory schema factory} before
 * using it for {@link SchemaFactory#newSchema() loading} a specific schema document.
 */
public class PluginResourceResolver implements LSResourceResolver {

	protected Plugin resourcePlugin;
	protected Map<String, String> nsToResourcePathMap;

	/**
	 * Constructor.
	 * 
	 * @param resourcePlugin
	 *            The {@link Plugin plug-in} which contains the resources that the {@link PluginResourceResolver}
	 *            instance to be created will be able to provide. Must not be <code>null</code>.
	 * @param nsToResourcePathMap
	 *            The namespaces and paths of the resources provided within the <code>resourcePlugin</code>.
	 */
	public PluginResourceResolver(Plugin resourcePlugin, Map<String, String> nsToResourcePathMap) {
		Assert.isNotNull(resourcePlugin);
		Assert.isNotNull(nsToResourcePathMap);

		this.resourcePlugin = resourcePlugin;
		this.nsToResourcePathMap = nsToResourcePathMap;
	}

	/*
	 * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		String resourcePath = nsToResourcePathMap.get(namespaceURI);
		if (resourcePath != null) {
			Bundle bundle = resourcePlugin.getBundle();
			if (bundle != null) {
				URL resourceURL = FileLocator.find(bundle, new Path(resourcePath), null);
				if (resourceURL != null) {
					return new ResourceLSInputImpl(publicId, resourceURL.toString(), baseURI);
				}
			}
		}
		return null;
	}

	/**
	 * A {@link LSInput} implementation used for wrapping resolved external resources in input sources.
	 */
	protected class ResourceLSInputImpl implements LSInput {

		protected String publicId = null;
		protected String systemId = null;
		protected String baseURI = null;

		protected InputStream byteStream = null;
		protected Reader characterStream = null;
		protected String stringData = null;

		protected String encoding = null;
		protected boolean certifiedText = false;

		/**
		 * Constructs an input source from just the public and system identifiers, leaving resolution of the entity and
		 * opening of the input stream up to the caller.
		 * 
		 * @param publicId
		 *            The public identifier, if known.
		 * @param systemId
		 *            The system identifier. This value should always be set, if possible, and can be relative or
		 *            absolute. If the system identifier is relative, then the base system identifier should be set.
		 * @param baseURI
		 *            The base system identifier. This value should always be set to the fully expanded URI of the base
		 *            system identifier, if possible.
		 */
		public ResourceLSInputImpl(String publicId, String systemId, String baseURI) {
			this.publicId = publicId;
			this.systemId = systemId;
			this.baseURI = baseURI;
		}

		public String getPublicId() {
			return publicId;
		}

		public void setPublicId(String publicId) {
			this.publicId = publicId;
		}

		public String getSystemId() {
			return systemId;
		}

		public void setSystemId(String systemId) {
			this.systemId = systemId;
		}

		public String getBaseURI() {
			return baseURI;
		}

		public void setBaseURI(String baseURI) {
			this.baseURI = baseURI;
		}

		public InputStream getByteStream() {
			return byteStream;
		}

		public void setByteStream(InputStream byteStream) {
			this.byteStream = byteStream;
		}

		public void setCharacterStream(Reader characterStream) {
			this.characterStream = characterStream;
		}

		public Reader getCharacterStream() {
			return characterStream;
		}

		public String getStringData() {
			return stringData;
		}

		public void setStringData(String stringData) {
			this.stringData = stringData;
		}

		public String getEncoding() {
			return encoding;
		}

		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		public boolean getCertifiedText() {
			return certifiedText;
		}

		public void setCertifiedText(boolean certifiedText) {
			this.certifiedText = certifiedText;
		}
	}
}
