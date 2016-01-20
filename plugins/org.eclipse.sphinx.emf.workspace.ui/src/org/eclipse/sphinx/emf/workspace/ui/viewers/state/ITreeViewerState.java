/**
 * <copyright>
 *
 * Copyright (c) 2015-2016 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [480105] Occasional ConcurrentModificationException when re-launching Sphinx on previously used workspace
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.viewers.state;

import java.util.List;

import org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers.ITreeElementStateProvider;

// TODO Move this class and all model-independent related classes to org.eclipse.sphinx.platform.ui.viewers.state.
// Split TreeElementStateProviderFactory into BasicTreeElementStateProviderFactory and BasicModelTreeElementStateProviderFactory
// and enable appropriate factory to be set on TreeViewerStateRecorder when instantiating the latter.
public interface ITreeViewerState {

	List<ITreeElementStateProvider> getExpandedElements();

	List<ITreeElementStateProvider> getSelectedElements();

	boolean isEmpty();

	boolean isApplying();

	void setApplying(boolean applying);
}
