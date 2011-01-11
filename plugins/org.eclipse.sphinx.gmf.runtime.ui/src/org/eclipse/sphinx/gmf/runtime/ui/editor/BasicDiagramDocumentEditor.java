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
package org.eclipse.sphinx.gmf.runtime.ui.editor;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider;
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
		ISaveablesLifecycleListener saveablesLifecycleListener = new ISaveablesLifecycleListener() {
			ISaveablesLifecycleListener siteSaveablesLifecycleListener = (ISaveablesLifecycleListener) getSite().getService(
					ISaveablesLifecycleListener.class);

			public void handleLifecycleEvent(SaveablesLifecycleEvent event) {
				if (event.getEventType() == SaveablesLifecycleEvent.DIRTY_CHANGED) {
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
				// Create new event as in org.eclipse.ui.internal.navigator.NavigatorSaveablesService.LifecycleListener
				event = new SaveablesLifecycleEvent(BasicDiagramDocumentEditor.this, event.getEventType(), event.getSaveables(), event.isForce());
				siteSaveablesLifecycleListener.handleLifecycleEvent(event);
			}
		};
		return saveablesLifecycleListener;
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		// Model-based editors don't need to be saved when being closed even if the model is dirty, because they don't
		// own the model. The model is loaded, managed, and saved globally, i.e. it is not destroyed but stays there
		// when editors are being closed.
		return false;
	}

	public Saveable[] getActiveSaveables() {
		return getSaveables();
	}

	public Saveable[] getSaveables() {
		if (modelSaveablesProvider != null) {
			Saveable saveable = modelSaveablesProvider.getSaveable(getDiagram().eResource());
			if (saveable != null) {
				return new Saveable[] { saveable };
			}
		}
		return new Saveable[0];
	}
}
