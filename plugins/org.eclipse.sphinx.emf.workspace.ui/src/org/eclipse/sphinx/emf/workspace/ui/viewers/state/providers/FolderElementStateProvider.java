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
package org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IMemento;

public class FolderElementStateProvider extends AbstractTreeElementStateProvider {

	private IFolder folder = null;

	public FolderElementStateProvider(TreeViewer viewer, IMemento memento) {
		super(viewer);

		Assert.isNotNull(memento);
		String pathAsString = memento.getString(TreeElementStateProviderFactory.MEMENTO_KEY_PATH);
		if (pathAsString != null) {
			folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(pathAsString));
		}
	}

	public FolderElementStateProvider(TreeViewer viewer, IFolder folder) {
		super(viewer);
		this.folder = folder;
	}

	@Override
	public boolean hasUnderlyingModel() {
		return false;
	}

	@Override
	public boolean canUnderlyingModelBeLoaded() {
		return false;
	}

	@Override
	public boolean isUnderlyingModelLoaded() {
		return false;
	}

	@Override
	public void loadUnderlyingModel() {
		// Do nothing
	}

	@Override
	public boolean isStale() {
		if (folder != null) {
			return !folder.exists();
		}
		return true;
	}

	@Override
	public Object getTreeElement() {
		return folder;
	}

	@Override
	public void appendToMemento(IMemento parentMemento) {
		if (folder != null) {
			IMemento memento = parentMemento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_ELEMENT_FOLDER);
			memento.putString(TreeElementStateProviderFactory.MEMENTO_KEY_PATH, folder.getFullPath().toString());
		}
	}

	@Override
	public String toString() {
		return "FolderElementProvider [folder=" + folder + "]"; //$NON-NLS-1$//$NON-NLS-2$
	}
}
