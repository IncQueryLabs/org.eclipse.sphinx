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

import org.eclipse.sphinx.platform.internal.messages.Messages;

/**
 * Default implementation of {@link IWorkspaceOperation}.
 *
 * @see IWorkspaceOperation
 */
public abstract class AbstractWorkspaceOperation implements IWorkspaceOperation {

	private String label;

	public AbstractWorkspaceOperation(String label) {
		this.label = label;
	}

	/*
	 * @see org.eclipse.sphinx.platform.operations.IWorkspaceOperation#getLabel()
	 */
	@Override
	public String getLabel() {
		return label != null ? label : Messages.operation_unnamed_label;
	}
}
