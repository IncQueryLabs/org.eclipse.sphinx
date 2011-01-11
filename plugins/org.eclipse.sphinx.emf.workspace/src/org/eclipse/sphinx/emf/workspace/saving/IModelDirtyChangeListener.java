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
package org.eclipse.sphinx.emf.workspace.saving;

import org.eclipse.sphinx.emf.model.IModelDescriptor;

/**
 * Listener for model dirty state change events. {@linkplain ModelSaveManager} notifies such listeners when the dirty
 * state of a {@linkplain IModelDescriptor model} changes (after a modification or a save).
 */
public interface IModelDirtyChangeListener {

	/**
	 * Handles the dirty state changed event that deals with the model whose corresponding descriptor is given in
	 * parameter.
	 * <p>
	 * <table>
	 * <tr valign=top>
	 * <td><b>Note</b>&nbsp;&nbsp;</td>
	 * <td>This method must be called on the UI thread.</td>
	 * </table>
	 * </tr>
	 * 
	 * @param modelDescriptor
	 *            The descriptor of the model whose dirty state changed.
	 * @since 0.7.0
	 */
	void handleDirtyChangedEvent(IModelDescriptor modelDescriptor);
}
