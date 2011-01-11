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

import org.eclipse.sphinx.platform.ui.fields.ListField;

/**
 * Change listener used by <code>ListField</code> and <code>CheckedListDialogField</code>
 */
public interface IListAdapter {

	/**
	 * A button from the button bar has been pressed.
	 * 
	 * @param field
	 *            The list field inside which a button has been pressed.
	 * @param index
	 *            The index of the button that has been pressed.
	 */
	void customButtonPressed(ListField field, int index);

	/**
	 * The selection of the list has changed.
	 */
	void selectionChanged(ListField field);

	/**
	 * An entry in the list has been double clicked
	 */
	void doubleClicked(ListField field);
}