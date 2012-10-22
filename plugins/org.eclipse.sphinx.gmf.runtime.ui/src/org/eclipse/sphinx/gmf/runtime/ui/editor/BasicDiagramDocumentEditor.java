/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [392464] Finish up Sphinx editor socket for GMF-based graphical editors
 * 
 * </copyright>
 */
package org.eclipse.sphinx.gmf.runtime.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider.SiteNotifyingSaveablesLifecycleListener;
import org.eclipse.sphinx.gmf.runtime.ui.internal.editor.ModelEditorUndoContextManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.SaveablesLifecycleEvent;
import org.eclipse.ui.navigator.SaveablesProvider;

public class BasicDiagramDocumentEditor extends DiagramDocumentEditor implements ISaveablesSource {

	protected SaveablesProvider modelSaveablesProvider;
	protected ModelEditorUndoContextManager undoContextManager;

	public BasicDiagramDocumentEditor() {
		super(true);
	}

	public BasicDiagramDocumentEditor(boolean hasFlyoutPalette) {
		super(hasFlyoutPalette);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		modelSaveablesProvider = createModelSaveablesProvider();
		modelSaveablesProvider.init(createModelSaveablesLifecycleListener());

		undoContextManager = new ModelEditorUndoContextManager(site, this, getEditingDomain());
	}

	protected SaveablesProvider createModelSaveablesProvider() {
		return new BasicModelSaveablesProvider();
	}

	/**
	 * Creates an {@linkplain ISaveablesLifecycleListener}
	 * 
	 * @return
	 */
	protected ISaveablesLifecycleListener createModelSaveablesLifecycleListener() {
		return new SiteNotifyingSaveablesLifecycleListener(this) {
			@Override
			public void handleLifecycleEvent(SaveablesLifecycleEvent event) {
				super.handleLifecycleEvent(event);

				if (event.getEventType() == SaveablesLifecycleEvent.DIRTY_CHANGED) {
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
		};
	}

	// FIXME Make sure that diagram changes don't get lost when closing the editor; either by user prompt for saving or
	// by not unloading diagram resource when closing diagram document editor
	@Override
	public boolean isSaveOnCloseNeeded() {
		// Model-based editors don't need to be saved when being closed even if the model is dirty, because they don't
		// own the model. The model is loaded, managed, and saved globally, i.e. it is not destroyed but stays there
		// when editors are being closed.
		return false;
	}

	/*
	 * @see org.eclipse.ui.ISaveablesSource#getActiveSaveables()
	 */
	public Saveable[] getActiveSaveables() {
		return getSaveables();
	}

	/*
	 * @see org.eclipse.ui.ISaveablesSource#getSaveables()
	 */
	public Saveable[] getSaveables() {
		if (modelSaveablesProvider != null) {
			List<Saveable> saveables = new ArrayList<Saveable>(2);

			// Add saveable of diagram
			Diagram diagram = getDiagram();
			Saveable diagramSaveable = modelSaveablesProvider.getSaveable(diagram);
			if (diagramSaveable != null) {
				saveables.add(diagramSaveable);
			}

			// Add saveable of domain model
			Saveable domainModelSaveable = modelSaveablesProvider.getSaveable(diagram.getElement());
			if (domainModelSaveable != null) {
				saveables.add(domainModelSaveable);
			}

			return saveables.toArray(new Saveable[saveables.size()]);
		}
		return new Saveable[0];
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		if (key.equals(IUndoContext.class)) {
			// Used by undo/redo actions to get their undo context
			return undoContextManager.getUndoContext();
		} else {
			return super.getAdapter(key);
		}
	}

	/*
	 * Overridden to deactivate sanity checking and avoid that diagram file can be synchronized/reloaded by this diagram
	 * editor (see #sanityCheckState() and #handleEditorInputChanged() for details). This is actually not necessary as
	 * reloading of diagram and domain model files is already taken in charge by Sphinx model synchronizer.
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor#enableSanityChecking(boolean)
	 */
	@Override
	protected void enableSanityChecking(boolean enable) {
		super.enableSanityChecking(false);
	}

	/*
	 * @see org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor#dispose()
	 */
	@Override
	public void dispose() {
		if (undoContextManager != null) {
			undoContextManager.dispose();
		}
		if (modelSaveablesProvider != null) {
			modelSaveablesProvider.dispose();
		}
		super.dispose();
	}
}
