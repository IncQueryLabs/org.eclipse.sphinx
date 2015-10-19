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
package org.eclipse.sphinx.emf.workspace.ui.viewers;

import org.eclipse.emf.common.util.TreeIterator;

public interface ITreeContentProviderIterator extends TreeIterator<Object> {

	/**
	 * An filter allowing the items being included in the tree iteration to be narrowed down to a subset of all tree
	 * items.
	 */
	public interface IItemFilter {

		boolean accept(Object item);
	}

	boolean isRecurrent();
}
