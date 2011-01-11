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
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.impl.PlatformContentHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.PlatformResourceURIHandlerImpl;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

/**
 * Performance-optimized replacement for {@link PlatformContentHandlerImpl}.
 */
public class ExtendedPlatformContentHandlerImpl extends PlatformContentHandlerImpl {

	/**
	 * Performance optimization: This implementation delegates to the performance-optimized content type id detection
	 * support provided by {@link ExtendedPlatform}. If and only if other properties are requested it delegates
	 * additionally to the platform's (slow) native content description support just as the original super
	 * implementation does.
	 */
	@Override
	public Map<String, Object> contentDescription(URI uri, InputStream inputStream, Map<?, ?> options, Map<Object, Object> context)
			throws IOException {
		String contentTypeId = null;
		if (uri.isPlatformResource()) {
			try {
				IFile file = EcorePlatformUtil.getFile(uri);
				contentTypeId = ExtendedPlatform.getContentTypeId(file);
			} catch (CoreException ex) {
				throw new IOException(ex.getMessage());
			}
		} else {
			/*
			 * !! Important Note !! Don't attempt to determine content description from input streams of apparently
			 * inaccessible resources. Otherwise Platform.getContentTypeManager().getDescriptionFor() would try to
			 * deduce it from the file extension on the URI which is very likely to produce inadequate results (e.g., in
			 * case of a model file with an .xml extension and a metamodel-specifc content type which existed before but
			 * got deleted prior to calling this method).
			 */
			if (inputStream.available() > 0) {
				IContentDescription contentDescription = Platform.getContentTypeManager().getDescriptionFor(inputStream, uri.lastSegment(),
						IContentDescription.ALL);
				if (contentDescription != null) {
					IContentType contentType = contentDescription.getContentType();
					if (contentType != null) {
						contentTypeId = contentType.getId();
					}
				}
			}
		}

		/*
		 * !! Important Note !! Return valid content type description even if no content type could be established in
		 * order to avoid that other content handlers (e.g., PlatformContentHandlerImpl) override result computed by
		 * this one (see org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#contentDescription(URI, Map<?, ?>) for
		 * details).
		 */
		Map<String, Object> result = createContentDescription(ContentHandler.Validity.VALID);
		result.put(ContentHandler.CONTENT_TYPE_PROPERTY, contentTypeId);
		if (contentTypeId != null) {
			Set<String> requestedProperties = getRequestedProperties(options);
			if (requestedProperties != null) {
				IContentDescription contentDescription;
				if (uri.isPlatformResource()) {
					contentDescription = PlatformResourceURIHandlerImpl.WorkbenchHelper.getContentDescription(uri.toPlatformString(true), options);
				} else {
					contentDescription = Platform.getContentTypeManager().getDescriptionFor(inputStream, uri.lastSegment(), IContentDescription.ALL);
				}
				if (contentDescription == null) {
					for (String property : requestedProperties) {
						QualifiedName qualifiedName = getQualifiedName(property);
						if (qualifiedName != null) {
							Object value = getDescriptionValue(qualifiedName, contentDescription.getProperty(qualifiedName));
							if (value != null) {
								result.put(property, value);
							}
						}
					}
				}
			}
		}
		return result;
	}
}
