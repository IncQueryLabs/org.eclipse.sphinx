/**
 * <copyright>
 * 
 * Copyright (c) 2013 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     itemis - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.util;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.sphinx.emf.resource.ExtendedResource;

public class ExtendedURIEditorInput extends URIEditorInput {

	public ExtendedURIEditorInput(URI uri) {
		super(uri);
	}

	/*
	 * Overridden to suppress URI scheme in tooltip if URI references a workspace resource. This makes sure that the
	 * tootip displayed for {@link URIEditorInput}s is the same as that for {@link FileEditorInput}s.
	 * @see org.eclipse.emf.common.ui.URIEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		URI uri = getURI();
		// URI pointing at workspace resource?
		if (uri.isPlatformResource()) {
			StringBuilder uriString = new StringBuilder();

			// Take all but URI scheme from underlying URI
			String platformURIString = uri.toPlatformString(true);
			uriString.append(platformURIString.startsWith(ExtendedResource.URI_SEGMENT_SEPARATOR) ? platformURIString.substring(1)
					: platformURIString);

			// Append URI fragment
			String uriFragment = uri.fragment();
			if (uriFragment != null) {
				uriString.append(ExtendedResource.URI_FRAGMENT_SEPARATOR);
				uriString.append(uriFragment);
			}

			return uriString.toString();
		}

		return super.getToolTipText();
	}
}