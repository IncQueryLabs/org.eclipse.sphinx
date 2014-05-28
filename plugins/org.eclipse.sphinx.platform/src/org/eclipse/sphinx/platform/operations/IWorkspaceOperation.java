/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Enhancement of {@link IWorkspaceRunnable} to provide support for simple (i.e., non-undoable) operations in the
 * workspace that define their own {@link ISchedulingRule scheduling rule} and need to expose an operation label.
 *
 * @see IWorkspaceRunnable
 * @see ISchedulingRule
 */
public interface IWorkspaceOperation extends IWorkspaceRunnable {

	/**
	 * Return the label that should be used to show the name of the operation to the user (e.g., in error messages).
	 *
	 * @return The operation label. Should never be <code>null</code>.
	 */
	String getLabel();

	/**
	 * Returns the {@link ISchedulingRule scheduling rule} required by this operation.
	 *
	 * @return The scheduling rule or <code>null</code> if no such is required.
	 */
	ISchedulingRule getRule();
}
