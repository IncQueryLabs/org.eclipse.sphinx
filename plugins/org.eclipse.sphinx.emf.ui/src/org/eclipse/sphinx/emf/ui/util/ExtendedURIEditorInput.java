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

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.sphinx.emf.resource.ExtendedResource;

public class ExtendedURIEditorInput extends URIEditorInput {

	public ExtendedURIEditorInput(URI uri) {
		super(uri);
	}

	/*
	 * Overridden to arrange for that tool tip shows a workspace-relative path (but a not a full platform:/resource URI)
	 * in case that model object referenced by this {@link URIEditorInput URI editor input} is contained in a workspace
	 * resource. This makes sure that the tootip displayed for {@link URIEditorInput}s is the same as that for {@link
	 * FileEditorInput}s.
	 * @see org.eclipse.emf.common.ui.URIEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		URI uri = getURI();
		// URI pointing at workspace resource?
		if (uri.isPlatformResource()) {
			StringBuilder uriString = new StringBuilder();

			// Retrieve workspace-relative path to underlying resource
			String path = uri.toPlatformString(true);
			uriString.append(new Path(path).makeRelative().toString());

			// Append URI fragment if any
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