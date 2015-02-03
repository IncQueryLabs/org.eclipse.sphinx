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

import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;

public interface IModelEditorInputChangeHandler {

	void handleEditorInputObjectChanged(IEditorInput editorInput);

	void handleEditorInputObjectRemoved(IEditorInput editorInput);

	void handleEditorInputResourceLoaded(IEditorInput editorInput);

	void handleEditorInputResourceMoved(IEditorInput editorInput, URI oldURI, URI newURI);

	void handleEditorInputResourceRemoved(IEditorInput editorInput);
}
