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
package org.eclipse.sphinx.platform.ui.fields;

import org.eclipse.swt.widgets.Control;

/**
 * 
 */
public interface ICComboField extends IField {

	/**
	 * @return
	 */
	Control getCComboControl();

	/**
	 * Gets the combo items.
	 */
	String[] getItems();

	/**
	 * @return
	 */
	int getSelectionIndex();

	/**
	 * Selects an item.
	 */
	boolean selectItem(int index);

	/**
	 * Sets the combo items. Triggers a dialog-changed event.
	 */
	void setItems(String[] items);

	/**
	 * Sets the text. Triggers a dialog-changed event.
	 */
	void setText(String text);
}