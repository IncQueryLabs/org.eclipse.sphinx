/**
 * <copyright>
 *
 * Copyright (c) 2008-2015 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [457704] Integrate EMF compare 3.x in Sphinx
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.compare.ui.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.compare.internal.CompareEditor;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.compare.ui.internal.Activator;
import org.eclipse.sphinx.emf.compare.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.editors.IModelEditorInputChangeAnalyzer;
import org.eclipse.sphinx.emf.editors.IModelEditorInputChangeHandler;
import org.eclipse.sphinx.emf.editors.ModelEditorInputSynchronizer;
import org.eclipse.sphinx.emf.editors.ModelEditorUndoContextManager;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider.SiteNotifyingSaveablesLifecycleListener;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.SaveablesLifecycleEvent;

/**
 * Extends the Eclipse compare editor in order to make model oriented.
 */
@SuppressWarnings("restriction")
public class ModelCompareEditor extends CompareEditor implements IModelEditorInputChangeAnalyzer {

	/**
	 * The identifier of this editor (as contributed).
	 */
	public static String ID = "org.eclipse.sphinx.emf.compare.ui.editors.modelCompareEditor"; //$NON-NLS-1$

	protected ModelEditorUndoContextManager undoContextManager;

	protected ModelEditorInputSynchronizer editorInputSynchronizer;

	/**
	 * Default constructor.
	 */
	public ModelCompareEditor() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		undoContextManager = getModelEditorUndoContextManager();
		TransactionalEditingDomain editingDomain = getEditingDomains()[0] != null ? getEditingDomains()[0] : getEditingDomains()[1];
		editorInputSynchronizer = new ModelEditorInputSynchronizer(input, editingDomain, this, new ModelCompareEditorInputChangeHandler());
	}

	protected ModelEditorUndoContextManager getModelEditorUndoContextManager() {
		if (undoContextManager == null) {
			TransactionalEditingDomain editingDomain = getEditingDomains()[0] != null ? getEditingDomains()[0] : getEditingDomains()[1];
			undoContextManager = new ModelEditorUndoContextManager(getSite(), this, editingDomain);
		}
		return undoContextManager;
	}

	@Override
	public void dispose() {
		if (undoContextManager != null) {
			undoContextManager.dispose();
		}
		super.dispose();
	}

	/**
	 * @return
	 */
	public ISaveablesLifecycleListener createModelSaveablesLifecycleListener() {
		return new SiteNotifyingSaveablesLifecycleListener(this) {
			@Override
			public void handleLifecycleEvent(SaveablesLifecycleEvent event) {
				super.handleLifecycleEvent(event);

				if (event.getEventType() == SaveablesLifecycleEvent.DIRTY_CHANGED) {
					firePropertyChange(PROP_DIRTY);
				}
			}
		};
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		// Model-based editors don't need to be saved when being closed even if the model is dirty, because they don't
		// own the model. The model is loaded, managed, and saved globally, i.e. it is not destroyed but stays there
		// when editors are being closed.
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Modify implementation as soon as saving as diff model would be available.
		return false;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if (key.equals(IUndoContext.class)) {
			// Used by undo/redo actions to get their undo context
			return getModelEditorUndoContextManager().getUndoContext();
		}
		return super.getAdapter(key);
	}

	/**
	 * @return An array of {@linkplain TransactionalEditingDomain editing domain}s which size is 2 and where at index 0
	 *         is editing domain from left, at index 1 is editing domain from right.
	 */
	public TransactionalEditingDomain[] getEditingDomains() {
		TransactionalEditingDomain[] editingDomains = new TransactionalEditingDomain[2];
		Object[] modelRoots = getModelRoots();
		if (modelRoots[0] != null) {
			editingDomains[0] = WorkspaceEditingDomainUtil.getEditingDomain(modelRoots[0]);
		}
		if (modelRoots[1] != null) {
			editingDomains[1] = WorkspaceEditingDomainUtil.getEditingDomain(modelRoots[1]);
		}
		if (editingDomains[0] == null && editingDomains[1] == null) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException("No editing domain found")); //$NON-NLS-1$
		}
		return editingDomains;
	}

	/**
	 * @return The root objects of the model part that are currently being compared in this editor or an empty array if
	 *         no such objects are available. Anyway, always returns an array of {@linkplain Object}s which size is 2
	 *         and where at index 0 is the left root and at index 1 is the right root.
	 */
	public Object[] getModelRoots() {
		Object[] modelRoots = new Object[2];
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof ModelComparisonScopeEditorInput) {
			modelRoots[0] = ((ModelComparisonScopeEditorInput) editorInput).getLeftObject();
			modelRoots[1] = ((ModelComparisonScopeEditorInput) editorInput).getRightObject();
		}
		return modelRoots;
	}

	protected AdapterFactory[] getAdapterFactories() {
		AdapterFactory[] adapterFactories = new AdapterFactory[2];
		TransactionalEditingDomain[] editingDomains = getEditingDomains();
		for (int i = 0; i < 2; i++) {
			TransactionalEditingDomain editingDomain = editingDomains[i];
			if (editingDomain != null) {
				adapterFactories[i] = ((AdapterFactoryEditingDomain) editingDomain).getAdapterFactory();
			}
		}
		return adapterFactories;
	}

	protected AdapterFactoryItemDelegator[] getItemDelegators() {
		AdapterFactoryItemDelegator[] itemDelegators = new AdapterFactoryItemDelegator[2];
		AdapterFactory[] adapterFactories = getAdapterFactories();
		for (int i = 0; i < 2; i++) {
			AdapterFactory adapterFactory = adapterFactories[i];
			if (adapterFactory != null) {
				itemDelegators[i] = new AdapterFactoryItemDelegator(adapterFactory);
			}
		}
		return itemDelegators;
	}

	protected boolean isActivePart() {
		return this == getSite().getWorkbenchWindow().getPartService().getActivePart();
	}

	@Override
	public void setInput(IEditorInput input) {
		if (input instanceof ModelComparisonScopeEditorInput) {
			super.setInput(input);
		} else {
			PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException(Messages.error_invalidEditorInput));
		}
	}

	@Override
	public boolean containEditorInputObject(IEditorInput editorInput, Set<EObject> removedObjects) {
		if (editorInput instanceof ModelComparisonScopeEditorInput && removedObjects != null) {
			Object leftObject = ((ModelComparisonScopeEditorInput) editorInput).getLeftObject();
			Object rightObject = ((ModelComparisonScopeEditorInput) editorInput).getRightObject();
			return removedObjects.contains(leftObject) || removedObjects.contains(rightObject);
		}
		return false;
	}

	@Override
	public boolean containEditorInputResourceURI(IEditorInput editorInput, Set<URI> resourceURIs) {
		if (editorInput instanceof ModelComparisonScopeEditorInput) {
			Set<URI> editorInputResourceURIs = new HashSet<URI>();
			Resource leftResource = EcorePlatformUtil.getResource(((ModelComparisonScopeEditorInput) editorInput).getLeftObject());
			if (leftResource != null) {
				editorInputResourceURIs.add(leftResource.getURI());
			}
			Resource rightResource = EcorePlatformUtil.getResource(((ModelComparisonScopeEditorInput) editorInput).getRightObject());
			if (rightResource != null) {
				editorInputResourceURIs.add(rightResource.getURI());
			}

			for (URI editorInputResourceURI : editorInputResourceURIs) {
				if (resourceURIs.contains(editorInputResourceURI)) {
					return true;
				}
			}
		}
		return false;
	}

	public class ModelCompareEditorInputChangeHandler implements IModelEditorInputChangeHandler {

		public ModelCompareEditorInputChangeHandler() {
		}

		@Override
		public void handleEditorInputObjectChanged(IEditorInput editorInput) {
			// Do nothing
		}

		@Override
		public void handleEditorInputObjectRemoved(IEditorInput editorInput) {
			// Close the editor
			close(false);
		}

		@Override
		public void handleEditorInputResourceLoaded(IEditorInput editorInput) {
			// Do nothing
		}

		@Override
		public void handleEditorInputResourceMoved(IEditorInput editorInput, URI oldURI, URI newURI) {
			// TODO update
		}

		@Override
		public void handleEditorInputResourceRemoved(IEditorInput editorInput) {
			// Close the editor
			close(false);
		}
	}

	/**
	 * Closes the editor programmatically.
	 *
	 * @param save
	 *            if <code>true</code>, the content should be saved before closing.
	 */
	protected void close(final boolean save) {
		Display display = getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				getSite().getPage().closeEditor(ModelCompareEditor.this, save);
			}
		});
	}
}
