/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
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
 * Default implementation of {@link ILabeledRunnable}.
 *
 * @see ILabeledWorkspaceRunnable
 */
public abstract class AbstractLabeledRunnable implements ILabeledRunnable {

	private String label;

	public AbstractLabeledRunnable(String label) {
		this.label = label;
	}

	/*
	 * @see org.eclipse.sphinx.platform.operations.ILabeledRunnable#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}
}
