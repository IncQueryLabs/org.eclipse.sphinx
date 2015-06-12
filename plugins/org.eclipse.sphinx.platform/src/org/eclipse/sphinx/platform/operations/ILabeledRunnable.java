/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.platform.operations;

/**
 * Enhancement of {@link Runnable} to provide support for simple (i.e., non-undoable) operations in the workspace that
 * don't need to report progress but want to expose an operation label.
 *
 * @see Runnable
 */
public interface ILabeledRunnable extends Runnable {

	/**
	 * Return the label that should be used to show the name of the operation to the user (e.g., in error messages).
	 *
	 * @return The operation label. Should never be <code>null</code>.
	 */
	String getLabel();
}