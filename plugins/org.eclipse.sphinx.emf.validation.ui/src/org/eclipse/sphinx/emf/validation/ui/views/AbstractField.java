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

/**
 * AbstractField is the abstract superclass for fields.
 * 
 * @since 0.7.0
 */
public abstract class AbstractField implements IField {

	boolean visible = true;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.internal.IField#isShowing()
	 */
	public boolean isShowing() {
		return visible;
	}

	/**
	 * Set whether or not the receiver is showing.
	 * 
	 * @param showing
	 */
	public void setShowing(boolean showing) {
		visible = showing;

	}
}
