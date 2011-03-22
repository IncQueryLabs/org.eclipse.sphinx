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
package org.eclipse.sphinx.graphiti.workspace.ui.editors;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramEditorBehavior;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.ui.util.EcoreUIUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider.SiteNotifyingSaveablesLifecycleListener;
import org.eclipse.sphinx.graphiti.workspace.ui.BasicDiagramEditorFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.SaveablesLifecycleEvent;
import org.eclipse.ui.navigator.SaveablesProvider;

public class BasicGraphitiDiagramEditor extends DiagramEditor implements ISaveablesSource {

	/**
	 * The Constant DIAGRAM_EDITOR_ID.
	 */
	public static final String BASIC_DIAGRAM_EDITOR_ID = "org.eclipse.sphinx.graphiti.workspace.ui.editors.graphitiDiagram"; //$NON-NLS-1$

	protected SaveablesProvider modelSaveablesProvider;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Eclipse may call us with an IFileEditorInput when a file is to be
		// opened. Try to convert this to a diagram input.
		if (!(input instanceof DiagramEditorInput)) {
			IEditorInput newInput = new BasicDiagramEditorFactory().createEditorInput(input);
			if (!(newInput instanceof DiagramEditorInput)) {
				// give up
				throw new PartInitException("Unknown editor input: " + input); //$NON-NLS-1$
			}
			input = newInput;
		}

		getBehavior().init(site, input, null);
		// In next line GEF calls setSite(), setInput(), getEditingDomain(), ...
		setSite(site);
		setInput(input);
		getCommandStack().addCommandStackListener(this);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		initializeActionRegistry();

		// FIXME (aakar) Delete code below later
		// fwListener = new FWCommandStackListener();
		// getBehavior().getEditingDomain().getCommandStack().addCommandStackListener(fwListener);

		modelSaveablesProvider = createModelSaveablesProvider();
		modelSaveablesProvider.init(createModelSaveablesLifecycleListener());
	}

	public Saveable[] getActiveSaveables() {
		return getSaveables();
	}

	public Saveable[] getSaveables() {
		if (modelSaveablesProvider != null) {
			Saveable saveable = modelSaveablesProvider.getSaveable(getModelDescriptor());
			if (saveable != null) {
				return new Saveable[] { saveable };
			}
		}
		return new Saveable[] {};
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

	protected Object getModelDescriptor() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof DiagramEditorInput) {
			return ModelDescriptorRegistry.INSTANCE.getModel(((DiagramEditorInput) editorInput).getDiagram().eResource());
		} else if (editorInput instanceof IFileEditorInput) {
			return ModelDescriptorRegistry.INSTANCE.getModel(((IFileEditorInput) editorInput).getFile());
		} else if (editorInput instanceof URIEditorInput) {
			URI uri = EcoreUIUtil.getURIFromEditorInput(editorInput);
			EObject modelFragment = EcoreResourceUtil.getModelFragment(getResourceSet(), uri);
			if (modelFragment != null) {
				return ModelDescriptorRegistry.INSTANCE.getModel(modelFragment.eResource());
			}
		}
		return null;
	}

	@Override
	protected IDiagramEditorBehavior createDiagramEditorBehavior() {
		return new BasicGraphitiDiagramEditorBehavior(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (modelSaveablesProvider != null) {
			modelSaveablesProvider.dispose();
		}
	}
}
