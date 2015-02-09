/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [458976] Validators are not singleton when they implement checks for different EPackages
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.ui.markers.fields;

import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

public class URIMarkerField extends MarkerField {

	public URIMarkerField() {
	}

	@Override
	public String getValue(MarkerItem item) {
		IMarker marker = item.getMarker();
		if (marker == null) {
			return ""; //$NON-NLS-1$
		}
		String uri = marker.getAttribute(EValidator.URI_ATTRIBUTE, ""); //$NON-NLS-1$
		return uri;
	}
}