/**
 * <copyright>
 *
 * Copyright (c) 2012-2013 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [409152] Wrong DiagramRoot is returned in BasicGraphitiDiagramEditorPersistencyBehavior
 *
 * </copyright>
 */
package org.eclipse.sphinx.graphiti.workspace.ui.editors;

import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class BasicGraphitiDiagramEditorPersistencyBehavior extends DefaultPersistencyBehavior {

	/**
	 * The Diagram root that is currently being edited.
	 */
	private Diagram diagramRoot = null;

	public BasicGraphitiDiagramEditorPersistencyBehavior(DiagramEditor diagramEditor) {
		super(diagramEditor);
	}

	@Override
	public Diagram loadDiagram(final URI uri) {
		// diagram root not yet available?
		if (diagramRoot == null || diagramRoot.eIsProxy()) {
			final TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(uri);
			if (editingDomain != null) {
				try {
					diagramRoot = TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<Diagram>() {
						public void run() {
							EObject modelObject = null;
							if (uri.hasFragment()) {
								modelObject = EcoreResourceUtil.loadEObject(editingDomain.getResourceSet(), uri);
							} else {
								modelObject = EcoreResourceUtil.loadModelRoot(editingDomain.getResourceSet(), uri, Collections.emptyMap());
							}
							setResult(modelObject instanceof Diagram ? (Diagram) modelObject : null);
						}
					});
				} catch (InterruptedException ex) {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
				}
			}
		}

		return diagramRoot;
	}
}