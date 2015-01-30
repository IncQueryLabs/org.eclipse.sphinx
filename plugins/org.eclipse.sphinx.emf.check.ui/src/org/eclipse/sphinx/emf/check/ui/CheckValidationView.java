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
 *     itemis - [458862] Navigation from problem markers in Check Validation view to model editors and Model Explorer view broken
 *
 * </copyright>
 */

package org.eclipse.sphinx.emf.check.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.views.markers.MarkerSupportView;

public class CheckValidationView extends MarkerSupportView {

	public CheckValidationView() {
		super(IValidationUIConstants.VALIDATION_CHECK_MARKER_GENERATOR);
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.equals(IShowInSource.class)) {
			return new IShowInSource() {
				@SuppressWarnings("restriction")
				@Override
				public ShowInContext getShowInContext() {
					IMarker[] markers = getSelectedMarkers();
					List<IResource> resources = new ArrayList<IResource>();
					for (IMarker marker : markers) {
						resources.add(marker.getResource());
					}
					return new ShowInContext(new StructuredSelection(getSelectedMarkers()), new StructuredSelection(resources));
				}
			};
		}
		return super.getAdapter(adapter);
	}
}
