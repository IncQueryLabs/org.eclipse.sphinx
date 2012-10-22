/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.gmf.runtime.ui.internal.editor;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorInput;

public interface IModelEditorInputChangeAnalyzer {

	boolean containEditorInputObject(IEditorInput editorInput, Set<EObject> removedObjects);

	boolean containEditorInputResourceURI(IEditorInput editorInput, Set<URI> resourceURIs);
}
