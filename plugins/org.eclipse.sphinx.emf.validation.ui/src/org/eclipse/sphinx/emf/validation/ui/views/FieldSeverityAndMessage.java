/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     See4sys - added support for problem markers on model objects (rather than 
 *               only on workspace resources). Unfortunately, there was no other 
 *               choice than copying the whole code from 
 *               org.eclipse.ui.views.markers.internal for that purpose because 
 *               many of the relevant classes, methods, and fields are private or
 *               package private.
 *******************************************************************************/
package org.eclipse.sphinx.emf.validation.ui.views;

import org.eclipse.jface.resource.DeviceResourceException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;

/**
 * The FieldSeverityAndMessage is the field that displays severities and messages.
 */
public class FieldSeverityAndMessage extends FieldMessage {

	private String description;

	static final String DESCRIPTION_IMAGE_PATH = "obj16/header_complete.gif"; //$NON-NLS-1$

	static final String COMPLETE_IMAGE_PATH = "obj16/complete_tsk.gif"; //$NON-NLS-1$

	static final String INCOMPLETE_IMAGE_PATH = "obj16/incomplete_tsk.gif"; //$NON-NLS-1$

	/**
	 * Create a new instance of the receiver.
	 */
	public FieldSeverityAndMessage() {
		description = MarkerMessages.problemSeverity_description;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object obj) {
		if (obj == null || !(obj instanceof MarkerNode)) {
			return null;
		}

		MarkerNode node = (MarkerNode) obj;
		if (node.isConcrete()) {
			if (node instanceof ProblemMarker) {
				return Util.getImage(((ProblemMarker) obj).getSeverity());
			}
			return null;
		}

		try {
			return JFaceResources.getResources().createImage(
					IDEInternalWorkbenchImages.getImageDescriptor(IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY));
		} catch (DeviceResourceException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null || !(obj1 instanceof ProblemMarker) || !(obj2 instanceof ProblemMarker)) {
			return 0;
		}

		ProblemMarker marker1 = (ProblemMarker) obj1;
		ProblemMarker marker2 = (ProblemMarker) obj2;

		int severity1 = marker1.getSeverity();
		int severity2 = marker2.getSeverity();
		if (severity1 == severity2) {
			return marker1.getDescriptionKey().compareTo(marker2.getDescriptionKey());
		}
		return severity2 - severity1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getColumnHeaderImage()
	 */
	@Override
	public Image getColumnHeaderImage() {
		return getImage(DESCRIPTION_IMAGE_PATH);
	}

}
