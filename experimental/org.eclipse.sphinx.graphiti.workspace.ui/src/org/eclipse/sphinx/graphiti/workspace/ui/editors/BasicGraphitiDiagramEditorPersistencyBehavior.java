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
package org.eclipse.sphinx.graphiti.workspace.ui.editors;

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.ui.util.EcoreUIUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;

public class BasicGraphitiDiagramEditorPersistencyBehavior extends DefaultPersistencyBehavior {

	/**
	 * The Diagram root that is currently being edited.
	 */
	private EObject diagramRoot = null;

	public BasicGraphitiDiagramEditorPersistencyBehavior(DiagramEditor diagramEditor) {
		super(diagramEditor);
	}

	@Override
	public Diagram loadDiagram(URI uri) {
		// diagram root not yet available?
		if (diagramRoot == null) {
			// Close editor if file behind diagram root is out of scope
			IFile file = EcoreUIUtil.getFileFromEditorInput(diagramEditor.getEditorInput());
			IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(file);
			if (modelDescriptor == null) {
				return null;
			} else {
				// Request asynchronous loading of diagram behind editor input
				ModelLoadManager.INSTANCE.loadModel(modelDescriptor, true, null);
			}
		}

		diagramRoot = getDiagramRoot();
		return diagramRoot instanceof Diagram ? (Diagram) diagramRoot : null;
	}

	protected EObject getEObject(final URI uri) {
		final TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(uri);
		final boolean loadOnDemand = diagramEditor.getEditorInput() instanceof FileStoreEditorInput ? true : false;
		if (editingDomain != null) {
			try {
				return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<EObject>() {
					public void run() {
						if (uri.hasFragment()) {
							// TODO : remove that!
							setResult(EcoreResourceUtil.getModelFragment(editingDomain.getResourceSet(), uri, loadOnDemand));
						} else {
							// TODO: Get back to getModelRoot and add support for asynchronous loading and refresh
							setResult(EcoreResourceUtil.loadModelRoot(editingDomain.getResourceSet(), uri, Collections.emptyMap()));
						}
					}
				});
			} catch (InterruptedException ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}
		}
		return null;
	}

	/**
	 * @return The root object of the model part that is currently being edited in this editor or <code>null</code> if
	 *         no such is available.
	 */
	// TODO Return actual type
	public EObject getDiagramRoot() {
		if (diagramRoot == null || diagramRoot.eIsProxy() || diagramRoot.eResource() == null || !diagramRoot.eResource().isLoaded()) {
			URI editorInputURI = EcoreUIUtil.getURIFromEditorInput(diagramEditor.getEditorInput());
			IFile diagramFile = (IFile) diagramEditor.getEditorInput().getAdapter(IFile.class);
			if (diagramFile != null) {
				diagramRoot = getEObject(editorInputURI);
			}
		}
		return diagramRoot;
	}

	public Resource getDiagramRootResource() {
		Object diagramRoot = getDiagramRoot();
		if (diagramRoot instanceof EObject) {
			return ((EObject) diagramRoot).eResource();
		}
		return null;
	}
}