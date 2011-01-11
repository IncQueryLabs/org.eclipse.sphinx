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
package org.eclipse.sphinx.platform.ui.fields.adapters;

import org.eclipse.sphinx.platform.ui.fields.IField;

/**
 * <p align=center>
 * <b><em>String Button Adapter</em></b>
 * </p>
 * <p align=justify>
 * Change listener used by <code>BasicField</code>.
 * </p>
 */
public interface IButtonAdapter {

	/**
	 * A control of a dialog field has been pressed.
	 * 
	 * @param field
	 *            The dialog field whose one of its controls was pressed.
	 */
	void changeControlPressed(IField field);
}
