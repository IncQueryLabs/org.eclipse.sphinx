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
package org.eclipse.sphinx.emf.workspace.ui.viewers.state;

import java.util.List;

import org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers.ITreeElementStateProvider;

public interface ITreeViewerState {

	List<ITreeElementStateProvider> getExpandedElements();

	List<ITreeElementStateProvider> getSelectedElements();

	boolean isEmpty();

	boolean isApplying();

	void setApplying(boolean applying);
}
