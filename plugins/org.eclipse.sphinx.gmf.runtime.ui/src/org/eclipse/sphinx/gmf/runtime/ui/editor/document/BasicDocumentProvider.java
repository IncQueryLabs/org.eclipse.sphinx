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
package org.eclipse.sphinx.gmf.runtime.ui.editor.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.ResourceUndoContext;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.AbstractDocumentProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.DiagramDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDiagramDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDiagramDocumentProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDocument;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.ui.util.EcoreUIUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.emf.workspace.saving.IModelDirtyChangeListener;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;
import org.eclipse.sphinx.gmf.runtime.ui.internal.Activator;
import org.eclipse.sphinx.gmf.runtime.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class BasicDocumentProvider extends AbstractDocumentProvider implements IDiagramDocumentProvider {

	private long computeModificationStamp(GMFResourceInfo info) {
		int result = 0;
		IFile file = EcorePlatformUtil.getFile(getDiagramResource(info));
		if (file != null) {
			if (file.getLocation() != null) {
				result += file.getLocation().toFile().lastModified();
			} else {
				result += file.getModificationStamp();
			}
		}
		return result;
	}

	private ISchedulingRule computeSchedulingRule(IResource toCreateOrModify) {
		if (toCreateOrModify.exists()) {
			return ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(toCreateOrModify);
		}

		IResource parent = toCreateOrModify;
		do {
			/*
			 * XXX This is a workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=67601
			 * IResourceRuleFactory.createRule should iterate the hierarchy itself.
			 */
			toCreateOrModify = parent;
			parent = toCreateOrModify.getParent();
		} while (parent != null && !parent.exists());

		return ResourcesPlugin.getWorkspace().getRuleFactory().createRule(toCreateOrModify);
	}

	/**
	 * 
	 */
	@Override
	protected ElementInfo createElementInfo(Object element) throws CoreException {
		if (!(element instanceof IEditorInput)) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), 0, NLS.bind(Messages.error_IncorrectInput,
					new Object[] { element, "org.eclipse.ui.part.FileEditorInput", "org.eclipse.emf.common.ui.URIEditorInput" }), //$NON-NLS-1$ //$NON-NLS-2$ 
					null));
		}
		IEditorInput editorInput = (IEditorInput) element;
		IDiagramDocument document = (IDiagramDocument) createDocument(editorInput);

		GMFResourceInfo info = new GMFResourceInfo(document, editorInput);
		info.setModificationStamp(computeModificationStamp(info));
		info.fStatus = null;
		return info;
	}

	/**
	 * 
	 */
	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		if (!(element instanceof IEditorInput)) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), 0, NLS.bind(Messages.error_IncorrectInput,
					new Object[] { element, "org.eclipse.ui.part.FileEditorInput", "org.eclipse.emf.common.ui.URIEditorInput" }), //$NON-NLS-1$ //$NON-NLS-2$ 
					null));
		}
		IEditorInput editorInput = (IEditorInput) element;
		IDocument document = createEmptyDocument(editorInput);

		setDocumentContent(document, editorInput);
		setupDocument(element, document);
		return document;
	}

	/**
	 * 
	 */
	@Override
	protected IDocument createEmptyDocument() {
		return null;
	}

	/**
	 * @param editorInput
	 * @return
	 */
	protected IDocument createEmptyDocument(IEditorInput editorInput) {
		DiagramDocument document = new DiagramDocument();
		try {
			document.setEditingDomain(getEditingDomain(editorInput));
		} catch (CoreException e) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), e);
		}
		return document;
	}

	@Override
	protected void doSynchronize(Object element, IProgressMonitor monitor) throws CoreException {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			handleElementChanged(info, getDiagramResource(info), monitor);
			return;
		}
		super.doSynchronize(element, monitor);
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		if (element instanceof IEditorInput) {
			GMFResourceInfo info = getGMFResourceInfo(element);
			if (info != null) {
				if (!overwrite && !info.isSynchronized()) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), IResourceStatus.OUT_OF_SYNC_LOCAL,
							Messages.error_UnsynchronizedFileSave, null));
				}
				info.stopResourceListening();
				fireElementStateChanging(element);
				try {
					// Saving diagram
					monitor.beginTask(Messages.task_SaveDiagram, info.getResourceSet().getResources().size() + 1);
					ModelSaveManager.INSTANCE.saveModel(getDiagramResource(info), getSaveOptions(), false, monitor);
					monitor.done();
					info.setModificationStamp(computeModificationStamp(info));
				} catch (RuntimeException x) {
					fireElementStateChangeFailed(element);
					throw x;
				} finally {
					info.startResourceListening();
				}
			} else {
				if (false == document instanceof IDiagramDocument) {
					fireElementStateChangeFailed(element);
					throw new CoreException(
							new Status(
									IStatus.ERROR,
									Activator.getPlugin().getSymbolicName(),
									0,
									"Incorrect document used: " + document + " instead of org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDiagramDocument", null)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				URI newResoruceURI = EcoreUIUtil.getURIFromEditorInput((IEditorInput) element);

				IDiagramDocument diagramDocument = (IDiagramDocument) document;
				final Diagram diagramCopy = EcoreUtil.copy(diagramDocument.getDiagram());
				EcorePlatformUtil.saveNewModelResource(diagramDocument.getEditingDomain(), EcorePlatformUtil.getFile(newResoruceURI).getFullPath(),
						getDiagramContentType(), diagramCopy, false, monitor);
			}
		} else {
			fireElementStateChangeFailed(element);
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), 0, NLS.bind(Messages.error_IncorrectInput,
					new Object[] { element, "org.eclipse.ui.part.FileEditorInput", "org.eclipse.emf.common.ui.URIEditorInput" }), //$NON-NLS-1$ //$NON-NLS-2$ 
					null));
		}
	}

	/**
	 * Subclasses should implement this method.
	 * 
	 * @return
	 */
	protected Map<?, ?> getSaveOptions() {
		return Collections.emptyMap();
	}

	/**
	 * 
	 */
	@Override
	protected void doValidateState(Object element, Object computationContext) throws CoreException {
		// Just validate the file behind the editor and do not iterate overall resources in the resource set
		IFile file = EcoreUIUtil.getFileFromEditorInput((IEditorInput) element);
		if (file != null && file.exists()) {
			Collection<org.eclipse.core.resources.IFile> files2Validate = Collections.singletonList(file);
			ResourcesPlugin.getWorkspace().validateEdit(files2Validate.toArray(new IFile[files2Validate.size()]), computationContext);
		}
		super.doValidateState(element, computationContext);
	}

	@Override
	protected void doUpdateStateCache(Object element) throws CoreException {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			info.setUpdateCache(true);
		}
		super.doUpdateStateCache(element);
	}

	/**
	 *
	 */
	@Override
	protected void disposeElementInfo(Object element, ElementInfo info) {
		if (info instanceof GMFResourceInfo) {
			GMFResourceInfo resourceSetInfo = (GMFResourceInfo) info;
			resourceSetInfo.dispose();
		}
		super.disposeElementInfo(element, info);
	}

	protected TransactionalEditingDomain getEditingDomain(IEditorInput editorInput) throws CoreException {
		return WorkspaceEditingDomainUtil.getEditingDomain(EcoreUIUtil.getFileFromEditorInput(editorInput));
	}

	@Override
	protected ISchedulingRule getSaveRule(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			Collection<org.eclipse.core.runtime.jobs.ISchedulingRule> rules = new ArrayList<org.eclipse.core.runtime.jobs.ISchedulingRule>();
			IFile file = EcorePlatformUtil.getFile(getDiagramResource(info));
			if (file != null) {
				rules.add(computeSchedulingRule(file));
			}
			return new MultiRule(rules.toArray(new ISchedulingRule[rules.size()]));
		}
		return null;
	}

	@Override
	protected ISchedulingRule getResetRule(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			Collection<org.eclipse.core.runtime.jobs.ISchedulingRule> rules = new ArrayList<org.eclipse.core.runtime.jobs.ISchedulingRule>();
			IFile file = EcorePlatformUtil.getFile(getDiagramResource(info));
			if (file != null) {
				rules.add(ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(file));
			}
			return new MultiRule(rules.toArray(new ISchedulingRule[rules.size()]));
		}
		return null;
	}

	@Override
	protected ISchedulingRule getSynchronizeRule(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			Collection<org.eclipse.core.runtime.jobs.ISchedulingRule> rules = new ArrayList<org.eclipse.core.runtime.jobs.ISchedulingRule>();
			IFile file = EcorePlatformUtil.getFile(getDomainResource(info));
			if (file != null) {
				rules.add(ResourcesPlugin.getWorkspace().getRuleFactory().refreshRule(file));
			}
			return new MultiRule(rules.toArray(new ISchedulingRule[rules.size()]));
		}
		return null;
	}

	@Override
	protected ISchedulingRule getValidateStateRule(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {

			Collection<org.eclipse.core.runtime.jobs.ISchedulingRule> files = new ArrayList<org.eclipse.core.runtime.jobs.ISchedulingRule>();
			IFile file = EcorePlatformUtil.getFile(getDiagramResource(info));
			if (file != null) {
				files.add(file);
			}
			return ResourcesPlugin.getWorkspace().getRuleFactory().validateEditRule(files.toArray(new IFile[files.size()]));
		}
		return null;
	}

	/**
	 * @param info
	 * @return
	 */
	protected URI getURI(GMFResourceInfo info) {
		IEditorInput editorInput = info.getEditorInput();
		return EcoreUIUtil.getURIFromEditorInput(editorInput);
	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}

	protected void handleElementChanged(GMFResourceInfo info, Resource changedResource, IProgressMonitor monitor) {
		IFile file = WorkspaceSynchronizer.getFile(changedResource);
		if (file != null) {
			try {
				file.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException ex) {
				// Error message to log was initially taken from
				// org.eclipse.gmf.runtime.diagram.ui.resources.editor.ide.internal.l10n.EditorMessages.FileDocumentProvider_handleElementContentChanged
				PlatformLogUtil.logAsError(Activator.getPlugin(), Messages.error_HandleElementContentChanged + ex);
			}
		}
		ModelLoadManager.INSTANCE.reloadFile(file, true, monitor);// changedResource.unload();

		fireElementContentAboutToBeReplaced(info.getEditorInput());
		removeUnchangedElementListeners(info.getEditorInput(), info);
		info.fStatus = null;
		try {
			setDocumentContent(info.fDocument, info.getEditorInput());
		} catch (CoreException e) {
			info.fStatus = e.getStatus();
		}
		if (!info.fCanBeSaved) {
			info.setModificationStamp(computeModificationStamp(info));
		}
		addUnchangedElementListeners(info.getEditorInput(), info);
		fireElementContentReplaced(info.getEditorInput());
	}

	/**
	 * 
	 */
	protected void handleElementMoved(IEditorInput input, URI uri) {
		if (input instanceof FileEditorInput) {
			IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(URI.decode(uri.path())).removeFirstSegments(1));
			fireElementMoved(input, newFile == null ? null : new FileEditorInput(newFile));
			return;
		}
		// TODO: append suffix to the URI! (use diagram as a parameter)
		fireElementMoved(input, new URIEditorInput(uri));
	}

	protected void handleElementRemoved(IEditorInput input) {
		fireElementDeleted(input);
	}

	/**
	 * Sets up the given document as it would be provided for the given element. The content of the document is not
	 * changed. This default implementation is empty. Subclasses may re-implement.
	 * 
	 * @param element
	 *            the blue-print element
	 * @param document
	 *            the document to set up
	 */
	protected void setupDocument(Object element, IDocument document) {
		// for subclasses
	}

	/**
	 *
	 */
	protected void setDocumentContent(IDocument document, IEditorInput element) throws CoreException {
		URI editorInputURI = EcoreUIUtil.getURIFromEditorInput(element);
		if (editorInputURI == null) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), 0, NLS.bind(Messages.error_IncorrectInput,
					new Object[] { element, "org.eclipse.ui.part.FileEditorInput", "org.eclipse.emf.common.ui.URIEditorInput" }), //$NON-NLS-1$ //$NON-NLS-2$ 
					null));
		}

		IDiagramDocument diagramDocument = (IDiagramDocument) document;
		Diagram diagram = loadDiagram(diagramDocument.getEditingDomain(), editorInputURI);
		if (diagram == null) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), 0, Messages.error_NoDiagramInResource,
					new RuntimeException(Messages.error_NoDiagramInResource)));
		}

		document.setContent(diagram);
	}

	protected Diagram loadDiagram(final TransactionalEditingDomain editingDomain, final URI uri) {
		if (editingDomain != null) {
			EObject modelObject = null;
			try {
				modelObject = TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<EObject>() {
					public void run() {
						if (uri.hasFragment()) {
							setResult(EcoreResourceUtil.loadModelFragment(editingDomain.getResourceSet(), uri));
						} else {
							setResult(EcoreResourceUtil.loadModelRoot(editingDomain.getResourceSet(), uri, getLoadOptions()));
						}
					}
				});
			} catch (InterruptedException ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}
			if (modelObject instanceof Diagram) {
				return (Diagram) modelObject;
			}
		}
		return null;
	}

	protected Map<?, ?> getLoadOptions() {
		return Collections.emptyMap();
	}

	protected Resource getDiagramResource(GMFResourceInfo info) {
		IDocument document = info.fDocument;
		if (document instanceof IDiagramDocument) {
			Diagram myDiagram = ((IDiagramDocument) document).getDiagram();
			return myDiagram.eResource();
		}
		return null;
	}

	protected Resource getDomainResource(GMFResourceInfo info) {
		IDocument document = info.fDocument;
		if (document instanceof IDiagramDocument) {
			Diagram myDiagram = ((IDiagramDocument) document).getDiagram();
			return myDiagram.getElement().eResource();
		}
		return null;
	}

	/**
	 * 
	 */
	protected void updateCache(Object element) throws CoreException {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			IFile file = EcorePlatformUtil.getFile(getDiagramResource(info));
			if (file != null && file.isReadOnly()) {
				info.setReadOnly(true);
				info.setModifiable(false);
				return;
			}
			info.setReadOnly(false);
			info.setModifiable(true);
			return;
		}
	}

	@Override
	public boolean canSaveDocument(Object element) {
		Resource diagramResource = getDiagramResource(getGMFResourceInfo(element));
		if (diagramResource != null) {
			return ModelSaveManager.INSTANCE.isDirty(diagramResource);
		}
		return super.canSaveDocument(element);
	}

	public String getDiagramContentType() {
		return null;
	}

	/**
	 * 
	 */
	public IEditorInput createInputWithEditingDomain(IEditorInput editorInput, TransactionalEditingDomain domain) {
		return editorInput;
	}

	/**
	 * 
	 */
	public IDiagramDocument getDiagramDocument(Object element) {
		IDocument doc = getDocument(element);
		if (doc instanceof IDiagramDocument) {
			return (IDiagramDocument) doc;
		}
		return null;
	}

	/**
	 *
	 */
	@Override
	public long getModificationStamp(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			return computeModificationStamp(info);
		}
		return super.getModificationStamp(element);
	}

	/**
	 * 
	 */
	public GMFResourceInfo getGMFResourceInfo(Object editorInput) {
		return (GMFResourceInfo) super.getElementInfo(editorInput);
	}

	/**
	 * 
	 */
	@Override
	public boolean isDeleted(Object element) {
		IDiagramDocument document = getDiagramDocument(element);
		if (document != null) {
			Resource diagramResource = document.getDiagram().eResource();
			if (diagramResource != null) {
				IFile file = WorkspaceSynchronizer.getFile(diagramResource);
				return file == null || file.getLocation() == null || !file.getLocation().toFile().exists();
			}
		}
		return super.isDeleted(element);
	}

	/**
	 * 
	 */
	@Override
	public boolean isReadOnly(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			if (info.isUpdateCache()) {
				try {
					updateCache(element);
				} catch (CoreException ex) {
					// Error message to log was initially taken from
					// org.eclipse.gmf.runtime.diagram.ui.resources.editor.ide.internal.l10n.EditorMessages.StorageDocumentProvider_isModifiable
					PlatformLogUtil.logAsError(Activator.getPlugin(), Messages.error_IsModifiable + ex);
				}
			}
			return info.isReadOnly();
		}
		return super.isReadOnly(element);
	}

	@Override
	public boolean isModifiable(Object element) {
		if (!isStateValidated(element)) {
			if (element instanceof FileEditorInput || element instanceof URIEditorInput) {
				return true;
			}
		}
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			if (info.isUpdateCache()) {
				try {
					updateCache(element);
				} catch (CoreException ex) {
					// Error message to log was initially taken from
					// org.eclipse.gmf.runtime.diagram.ui.resources.editor.ide.internal.l10n.EditorMessages.StorageDocumentProvider_isModifiable
					PlatformLogUtil.logAsError(Activator.getPlugin(), Messages.error_IsModifiable + ex);
				}
			}
			return info.isModifiable();
		}
		return super.isModifiable(element);
	}

	@Override
	public boolean isSynchronized(Object element) {
		GMFResourceInfo info = getGMFResourceInfo(element);
		if (info != null) {
			return info.isSynchronized();
		}
		return super.isSynchronized(element);
	}

	protected class GMFResourceInfo extends ElementInfo {

		private long myModificationStamp = IResource.NULL_STAMP;
		private WorkspaceSynchronizer mySynchronizer;
		private final Collection<Resource> myUnSynchronizedResources = new ArrayList<Resource>();
		private final IDiagramDocument myDocument;
		private final IEditorInput myEditorInput;
		private final URI domainModelResourceURI;
		private boolean myUpdateCache = true;
		private boolean myModifiable = false;
		private boolean myReadOnly = true;
		private IOperationHistoryListener affectedObjectsListener;
		private ResourceSetListener resourceRemovedListener;
		private IModelDirtyChangeListener dirtyChangedListener;

		public GMFResourceInfo(IDiagramDocument document, IEditorInput editorInput) {
			super(document);
			myDocument = document;
			EObject element = document.getDiagram().getElement();
			domainModelResourceURI = element != null ? element.eResource().getURI() : null;
			myEditorInput = editorInput;
			addTransactionalEditingDomainListeners(document.getEditingDomain());
			startResourceListening();
		}

		protected void addTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
			if (editingDomain != null) {
				// Create and register ResourceSetChangedListener that detects resources modification
				// resourceChangedListener = new ResourceSetModificationListener(this);
				// Assert.isNotNull(resourceChangedListener);
				// editingDomain.addResourceSetListener(resourceChangedListener);

				// // Create and register ResourceSetChangedListener that detects renamed or moved resources
				// resourceMovedListener = createResourceMovedListener();
				// Assert.isNotNull(resourceMovedListener);
				// editingDomain.addResourceSetListener(resourceMovedListener);

				// Create and register ResourceSetChangedListener that detects removed resources
				resourceRemovedListener = new ResourceRemovedListener(this);
				Assert.isNotNull(resourceRemovedListener);
				editingDomain.addResourceSetListener(resourceRemovedListener);

				// // FIXME Consider to add an objectMovedListener in case that modelRoot has changed its name or been
				// put
				// // on
				// // another container -> must trigger an input change and a part name change
				//
				// // Create and register ResourceSetChangedListener that detects removed objects
				// objectRemovedListener = createObjectRemovedListener();
				// Assert.isNotNull(objectRemovedListener);
				// editingDomain.addResourceSetListener(objectRemovedListener);

				// Create and register IOperationHistoryListener that detects changed objects
				affectedObjectsListener = new AffectedObjectsListener(this);
				Assert.isNotNull(affectedObjectsListener);
				((IWorkspaceCommandStack) editingDomain.getCommandStack()).getOperationHistory().addOperationHistoryListener(affectedObjectsListener);
			}

			// Create and register IModelDirtyChangeListener
			dirtyChangedListener = new ModelDirtyChangeListener(this);
			Assert.isNotNull(dirtyChangedListener);
			ModelSaveManager.INSTANCE.addModelDirtyChangedListener(dirtyChangedListener);
		}

		protected void removeTransactionalEditingDomainListeners(TransactionalEditingDomain editingDomain) {
			if (editingDomain != null) {
				// if (resourceChangedListener != null) {
				// editingDomain.removeResourceSetListener(resourceChangedListener);
				// }
				// if (resourceMovedListener != null) {
				// editingDomain.removeResourceSetListener(resourceMovedListener);
				// }
				if (resourceRemovedListener != null) {
					editingDomain.removeResourceSetListener(resourceRemovedListener);
				}
				// if (objectRemovedListener != null) {
				// editingDomain.removeResourceSetListener(objectRemovedListener);
				// }
				if (affectedObjectsListener != null) {
					IOperationHistory operationHistory = ((IWorkspaceCommandStack) editingDomain.getCommandStack()).getOperationHistory();
					operationHistory.removeOperationHistoryListener(affectedObjectsListener);
				}
			}
			if (dirtyChangedListener != null) {
				ModelSaveManager.INSTANCE.removeModelDirtyChangedListener(dirtyChangedListener);
			}
		}

		public final void startResourceListening() {
			mySynchronizer = new WorkspaceSynchronizer(getEditingDomain(), new SynchronizerDelegate());
		}

		public final void stopResourceListening() {
			mySynchronizer.dispose();
			mySynchronizer = null;
		}

		public long getModificationStamp() {
			return myModificationStamp;
		}

		public void setModificationStamp(long modificationStamp) {
			myModificationStamp = modificationStamp;
		}

		public TransactionalEditingDomain getEditingDomain() {
			return myDocument.getEditingDomain();
		}

		public ResourceSet getResourceSet() {
			return getEditingDomain().getResourceSet();
		}

		public IEditorInput getEditorInput() {
			return myEditorInput;
		}

		public void dispose() {
			IProgressMonitor monitor = getProgressMonitor();
			try {
				stopResourceListening();
				removeTransactionalEditingDomainListeners(myDocument.getEditingDomain());
				EcorePlatformUtil.unloadFile(myDocument.getEditingDomain(), EcoreUIUtil.getFileFromEditorInput(myEditorInput));
			} finally {
				monitor.done();
			}
		}

		public boolean isSynchronized() {
			return myUnSynchronizedResources.size() == 0;
		}

		public void setUnSynchronized(Resource resource) {
			myUnSynchronizedResources.add(resource);
		}

		public void setSynchronized(Resource resource) {
			myUnSynchronizedResources.remove(resource);
		}

		public boolean isUpdateCache() {
			return myUpdateCache;
		}

		public void setUpdateCache(boolean update) {
			myUpdateCache = update;
		}

		public boolean isModifiable() {
			return myModifiable;
		}

		public void setModifiable(boolean modifiable) {
			myModifiable = modifiable;
		}

		public boolean isReadOnly() {
			return myReadOnly;
		}

		public void setReadOnly(boolean readOnly) {
			myReadOnly = readOnly;
		}

		private class SynchronizerDelegate implements WorkspaceSynchronizer.Delegate {

			public boolean handleResourceChanged(final Resource resource) {
				if (myDocument.getDiagram().eResource() == resource) {
					synchronized (GMFResourceInfo.this) {
						if (GMFResourceInfo.this.fCanBeSaved) {
							setUnSynchronized(resource);
							return true;
						}
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							handleElementChanged(GMFResourceInfo.this, myDocument.getDiagram().eResource(), null);
						}
					});
				}
				return true;
			}

			public boolean handleResourceDeleted(Resource resource) {
				if (myDocument.getDiagram().eResource() == resource) {
					synchronized (GMFResourceInfo.this) {
						if (GMFResourceInfo.this.fCanBeSaved) {
							setUnSynchronized(resource);
							return true;
						}
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							fireElementDeleted(getEditorInput());
						}
					});
				}
				return true;
			}

			public boolean handleResourceMoved(Resource resource, final URI newURI) {
				if (myDocument.getDiagram().getElement().eResource() == resource) {
					synchronized (GMFResourceInfo.this) {
						if (GMFResourceInfo.this.fCanBeSaved) {
							setUnSynchronized(resource);
							return true;
						}
					}
					if (myDocument.getDiagram().eResource() == resource) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								handleElementMoved(getEditorInput(), newURI);
							}
						});
					} else {
						handleResourceDeleted(resource);
					}
				}
				return true;
			}

			public void dispose() {

			}
		}
	}

	// FIXME aakar remove this listener
	private class AffectedObjectsListener implements IOperationHistoryListener {

		private final GMFResourceInfo myInfo;

		public AffectedObjectsListener(GMFResourceInfo info) {
			myInfo = info;
		}

		public void historyNotification(OperationHistoryEvent event) {
			if (event.getEventType() == OperationHistoryEvent.OPERATION_ADDED) {
				handleOperationAdded(event.getOperation());
			} else if (event.getEventType() == OperationHistoryEvent.DONE || event.getEventType() == OperationHistoryEvent.UNDONE
					|| event.getEventType() == OperationHistoryEvent.REDONE) {
				Set<?> affectedResources = ResourceUndoContext.getAffectedResources(event.getOperation());
				if (affectedResources.contains(getDiagramResource(myInfo))) {
					handleOperationFinished(event.getOperation());
				}
			}
		}

		private void handleOperationAdded(final IUndoableOperation operation) {
			// May do something here for undo context
		}

		private void handleOperationFinished(final IUndoableOperation operation) {
			Resource resource = getDiagramResource(myInfo);
			if (resource.isLoaded()) {
				boolean modified = false;
				modified = resource.isModified();
				boolean dirtyStateChanged = false;
				synchronized (myInfo) {
					if (modified != myInfo.fCanBeSaved) {
						myInfo.fCanBeSaved = modified;
						dirtyStateChanged = true;
					}
					if (!resource.isModified()) {
						myInfo.setSynchronized(resource);
					}
				}
				if (dirtyStateChanged) {
					fireElementDirtyStateChanged(myInfo.getEditorInput(), modified);

					if (!modified) {
						myInfo.setModificationStamp(computeModificationStamp(myInfo));
					}
				}
			}
		}
	}

	private class ResourceRemovedListener extends ResourceSetListenerImpl {

		private final GMFResourceInfo myInfo;

		public ResourceRemovedListener(GMFResourceInfo info) {
			super(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResourceSet(), ResourceSet.RESOURCE_SET__RESOURCES).and(
					NotificationFilter.createEventTypeFilter(Notification.REMOVE).or(
							NotificationFilter.createEventTypeFilter(Notification.REMOVE_MANY))));
			myInfo = info;
		}

		@Override
		public void resourceSetChanged(ResourceSetChangeEvent event) {
			// Retrieve removed resources from notification
			Set<Resource> removedResources = new HashSet<Resource>();
			List<?> notifications = event.getNotifications();
			for (Object object : notifications) {
				if (object instanceof Notification) {
					Notification notification = (Notification) object;
					if (notification.getOldValue() instanceof Resource) {
						removedResources.add((Resource) notification.getOldValue());
					}
					if (notification.getOldValue() instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<Resource> resources = (List<Resource>) notification.getOldValue();
						removedResources.addAll(resources);
					}
				}
			}

			// Are diagram resource or/and domain resource part of removed resources?
			// My graphical editor is opened for this resource
			URI diagramResourceURI = EcoreUIUtil.getURIFromEditorInput(myInfo.getEditorInput());

			// My graphical editor is editing this domain model resource
			URI domainModelResourceURI = myInfo.domainModelResourceURI;
			for (Resource removedResource : removedResources) {
				if (removedResource.getURI().equals(diagramResourceURI) || removedResource.getURI().equals(domainModelResourceURI)) {
					handleElementRemoved(myInfo.getEditorInput());
					break;
				}
			}
		}

		@Override
		public boolean isPostcommitOnly() {
			return true;
		}
	}

	private class ModelDirtyChangeListener implements IModelDirtyChangeListener {

		private final GMFResourceInfo myInfo;

		public ModelDirtyChangeListener(GMFResourceInfo info) {
			myInfo = info;
		}

		public void handleDirtyChangedEvent(final IModelDescriptor modelDescriptor) {

			if (modelDescriptor == null) {
				return;
			}
			// The resource of the model root
			Resource modelRootResource = getDiagramResource(myInfo);

			// The underlying file from model root resource
			IFile modelRootFile = EcorePlatformUtil.getFile(modelRootResource);

			// Fires dirty change event if file belongs to that model
			if (modelDescriptor.belongsTo(modelRootFile, true)) {
				fireElementDirtyStateChanged(myInfo.getEditorInput(), true);
			}
		}
	}
}