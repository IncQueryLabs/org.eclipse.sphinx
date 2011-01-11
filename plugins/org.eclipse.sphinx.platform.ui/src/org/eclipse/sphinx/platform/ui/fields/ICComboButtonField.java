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
public interface ICComboButtonField extends ICComboField {

	/**
	 * Sets the label of the button.
	 */
	void setButtonLabel(String label);

	/**
	 * @return
	 */
	Control getButtonControl();
}