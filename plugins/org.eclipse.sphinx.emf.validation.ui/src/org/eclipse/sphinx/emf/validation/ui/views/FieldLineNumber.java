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

import org.eclipse.core.resources.IMarker;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * FieldLineNumber is the field for line numbers.
 */
public class FieldLineNumber extends AbstractField {

	private String description;

	private Image image;

	/**
	 * Create a new instance of the receiver.
	 */
	public FieldLineNumber() {
		description = MarkerMessages.description_lineNumber;
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
	 * @see org.eclipse.ui.views.markers.internal.IField#getDescriptionImage()
	 */
	@Override
	public Image getDescriptionImage() {
		return image;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getColumnHeaderText()
	 */
	@Override
	public String getColumnHeaderText() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getColumnHeaderImage()
	 */
	@Override
	public Image getColumnHeaderImage() {
		return image;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getValue(java.lang.Object)
	 */
	@Override
	public String getValue(Object obj) {
		if (obj == null) {
			return MarkerMessages.FieldMessage_NullMessage;
		}

		if (obj instanceof MarkerNode) {
			MarkerNode node = (MarkerNode) obj;
			if (node.isConcrete()) {
				ConcreteMarker concreteNode = (ConcreteMarker) node;
				if (concreteNode.getLocationString().length() == 0) {
					if (concreteNode.getLine() < 0) {
						return MarkerMessages.Unknown;
					}
					return NLS.bind(MarkerMessages.label_lineNumber, Integer.toString(concreteNode.getLine()));
				}
				return concreteNode.getLocationString();
			}
			return Util.EMPTY_STRING;
		}

		if (obj instanceof IWorkbenchAdapter) {
			return Util.EMPTY_STRING;// Don't show pending
		}

		if (obj instanceof IMarker) {
			return Util.getProperty(IMarker.LINE_NUMBER, (IMarker) obj);
		}

		return NLS.bind(MarkerMessages.FieldMessage_WrongType, obj.toString());

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object obj) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null || !(obj1 instanceof ConcreteMarker) || !(obj2 instanceof ConcreteMarker)) {
			return 0;
		}

		ConcreteMarker marker1 = (ConcreteMarker) obj1;
		ConcreteMarker marker2 = (ConcreteMarker) obj2;

		String location1 = marker1.getLocationString();
		String location2 = marker2.getLocationString();

		if (location1.length() == 0 || location2.length() == 0) {
			return marker1.getLine() - marker2.getLine();
		}

		return location1.compareTo(location2);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getDefaultDirection()
	 */
	@Override
	public int getDefaultDirection() {
		return TableComparator.ASCENDING;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#getPreferredWidth()
	 */
	@Override
	public int getPreferredWidth() {
		return 60;
	}
}
