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
package org.eclipse.sphinx.emf.editors;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorInput;

public interface IModelEditorInputChangeHandler {

	void handleEditorInputObjectAdded(IEditorInput editorInput, Set<EObject> addedObjects);

	void handleEditorInputObjectRemoved(IEditorInput editorInput, Set<EObject> removedObjects);

	void handleEditorInputObjectChanged(IEditorInput editorInput, Set<EObject> changedObjects);

	void handleEditorInputObjectMoved(IEditorInput editorInput, Set<EObject> movedObjects);

	void handleEditorInputResourceLoaded(IEditorInput editorInput);

	void handleEditorInputResourceUnloaded(IEditorInput editorInput);

	void handleEditorInputResourceMoved(IEditorInput editorInput, URI oldURI, URI newURI);

	void handleEditorInputResourceRemoved(IEditorInput editorInput);
}
