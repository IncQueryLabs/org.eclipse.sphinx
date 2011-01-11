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
package org.eclipse.sphinx.emf.workspace.syncing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sphinx.platform.resources.syncing.IResourceSyncRequest;

public interface IModelSyncRequest extends IResourceSyncRequest{

	void addProjectToLoad(IProject project);

	void addProjectToUnload(IProject project);

	void addProjectToReload(IProject project);

	void addProjectToUnresolveUnreachableCrossReferencesFor(IProject project);

	void addFileToLoad(IFile file);

	void addFileToUnload(IFile file);

	void addFileToReload(IFile file);

	void addFileToUpdateResourceURIFor(IFile oldFile, IPath newPath);

}
