/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
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
package org.eclipse.sphinx.graphiti.workspace.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;

public class BasicDiagramEditorFactory implements IElementFactory {

	public IAdaptable createElement(IMemento memento) {

		// Get diagram URI
		final String diagramUriString = memento.getString(DiagramEditorInput.KEY_URI);
		if (diagramUriString == null) {
			return null;
		}

		// Get diagram type provider id
		final String providerID = memento.getString(DiagramEditorInput.KEY_PROVIDER_ID);
		if (providerID == null) {
			return null;
		}

		URI diagramURI = URI.createURI(diagramUriString);
		URI diagramFileURI = diagramURI.trimFragment();
		IFile diagramFile = EcorePlatformUtil.getFile(diagramFileURI);
		if (diagramFile != null) {
			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(diagramFile);
			if (editingDomain != null) {
				return new BasicDiagramEditorInput(diagramUriString, editingDomain, providerID, true);
			}
		}
		return null;
	}

	public DiagramEditorInput createEditorInput(IEditorInput otherInput) {
		if (otherInput instanceof DiagramEditorInput) {
			return (DiagramEditorInput) otherInput;
		}
		if (otherInput instanceof IFileEditorInput) {
			final IFileEditorInput fileInput = (IFileEditorInput) otherInput;
			final IFile file = fileInput.getFile();
			final TransactionalEditingDomain domain = WorkspaceEditingDomainUtil.getEditingDomain(file);
			URI diagramFileUri = GraphitiUiInternal.getEmfService().getFileURI(file, domain.getResourceSet());
			if (diagramFileUri != null) {
				// the file has to contain one base node which has to be a diagram
				diagramFileUri = GraphitiUiInternal.getEmfService().mapDiagramFileUriToDiagramUri(diagramFileUri);
				return new BasicDiagramEditorInput(diagramFileUri, domain, null, true);
			}
		}
		if (otherInput instanceof URIEditorInput) {
			final URIEditorInput uriInput = (URIEditorInput) otherInput;
			final TransactionalEditingDomain domain = WorkspaceEditingDomainUtil.getEditingDomain(uriInput.getURI());
			URI diagramFileUri = uriInput.getURI();
			if (diagramFileUri != null) {
				// the file has to contain one base node which has to be a diagram
				diagramFileUri = GraphitiUiInternal.getEmfService().mapDiagramFileUriToDiagramUri(diagramFileUri);
				return new BasicDiagramEditorInput(diagramFileUri, domain, null, true);
			}
		}
		return null;
	}
}
