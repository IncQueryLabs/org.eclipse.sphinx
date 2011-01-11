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
package org.eclipse.sphinx.gmf.workspace.domain.factory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.AbstractTransactionalCommandStack;
import org.eclipse.emf.workspace.EMFCommandOperation;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;
import org.eclipse.emf.workspace.internal.Tracing;
import org.eclipse.gmf.runtime.diagram.core.DiagramEditingDomainFactory;
import org.eclipse.sphinx.emf.domain.factory.EditingDomainFactoryListenerRegistry;
import org.eclipse.sphinx.emf.domain.factory.ITransactionalEditingDomainFactoryListener;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.domain.factory.IExtendedTransactionalEditingDomainFactory;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;

/**
 * Enables adaptable WorkspaceEditingDomains to be created which can be extended with additional behaviors by clients.
 * Adds an IEditingDomainProvider adapter the the WorkspaceEditingDomain's ResourceSet so that it can be accessed from
 * EObjects or Resources by using AdapterFactoryEditingDomain.getEditingDomainFor(). Includes runtime support for GMF
 * diagrams.
 */
public class ExtendedDiagramEditingDomainFactory extends DiagramEditingDomainFactory implements IExtendedTransactionalEditingDomainFactory {

	/**
	 * <p align=center>
	 * <b><em> Model Extended Diagram Editing Domain </em></b>
	 * </p>
	 */
	protected class ExtendedDiagramEditingDomain extends DiagramEditingDomain implements IAdaptable {

		private Collection<IMetaModelDescriptor> fMetaModelDescriptors = new HashSet<IMetaModelDescriptor>();

		public ExtendedDiagramEditingDomain(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		public ExtendedDiagramEditingDomain(AdapterFactory adapterFactory, ResourceSet resourceSet) {
			super(adapterFactory, resourceSet);
		}

		public ExtendedDiagramEditingDomain(AdapterFactory adapterFactory, TransactionalCommandStack commandStack) {
			super(adapterFactory, commandStack);
		}

		public ExtendedDiagramEditingDomain(AdapterFactory adapterFactory, TransactionalCommandStack commandStack, ResourceSet resourceSet) {
			super(adapterFactory, commandStack, resourceSet);
		}

		/**
		 * Returns an object which is an instance of the given class associated with this object. Returns
		 * <code>null</code> if no such object can be found.
		 * <p>
		 * This implementation of the method declared by <code>IAdaptable</code> passes the request along to the
		 * platform's adapter manager; roughly <code>Platform.getAdapterManager().getAdapter(this, adapter)</code>.
		 * Subclasses may override this method (however, if they do so, they should invoke the method on their
		 * superclass to ensure that the Platform's adapter manager is consulted).
		 * </p>
		 * 
		 * @param adapter
		 *            the class to adapt to
		 * @return the adapted object or <code>null</code>
		 * @see IAdaptable#getAdapter(Class)
		 * @see Platform#getAdapterManager()
		 */
		@Override
		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapterType) {
			Object adapter = super.getAdapter(adapterType);
			if (adapter != null) {
				return adapter;
			}
			return Platform.getAdapterManager().getAdapter(this, adapterType);
		}

		public Collection<IMetaModelDescriptor> getMetaModelDescriptors() {
			return fMetaModelDescriptors;
		}

		@Override
		public boolean isReadOnly(Resource resource) {
			// FIXME File bug to EMF: NPE is raised when opening a platform:/plugin resource and resolving its URL in an
			// editor while workspace is closing
			try {
				return super.isReadOnly(resource);
			} catch (NullPointerException ex) {
				return true;
			}
		}

		@Override
		protected boolean isReadOnlyURI(URI uri) {
			// FIXME File bug to EMF: NPE is raised when calling this method for an URI that doesn't exist
			if (!EcoreResourceUtil.exists(uri)) {
				return true;
			}
			return super.isReadOnlyURI(uri);
		}

		@Override
		public void dispose() {
			/*
			 * !! Important Note !! First unload all resources in order to give a chance to registered
			 * ResourceSetListeners to act upon as needed. Wait until all of them are done and only then proceed with
			 * unregistering and disposing editing domain itself.
			 */
			EcorePlatformUtil.unloadAllResources(this, null);
			try {
				Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, null);
			} catch (Exception ex) {
				// Ignore exception
			}
			firePreDisposeEditingDomain(fMetaModelDescriptors, this);
			super.dispose();
		}

		@Override
		public String toString() {
			return getID();
		}
	}

	protected ResourceSet createResourceSet() {
		return new ScopingResourceSetImpl();
	}

	protected IOperationHistory createOperationHistory() {
		return OperationHistoryFactory.getOperationHistory();
	}

	public TransactionalEditingDomain createEditingDomain(Collection<IMetaModelDescriptor> metaModelDescriptors) {
		return createEditingDomain(metaModelDescriptors, createResourceSet(), createOperationHistory());
	}

	public TransactionalEditingDomain createEditingDomain(Collection<IMetaModelDescriptor> metaModelDescriptors, IOperationHistory history) {
		return createEditingDomain(metaModelDescriptors, createResourceSet(), history);
	}

	public TransactionalEditingDomain createEditingDomain(Collection<IMetaModelDescriptor> metaModelDescriptors, ResourceSet resourceSet) {
		return createEditingDomain(metaModelDescriptors, resourceSet, createOperationHistory());
	}

	public TransactionalEditingDomain createEditingDomain(Collection<IMetaModelDescriptor> metaModelDescriptors, ResourceSet resourceSet,
			IOperationHistory history) {

		// Create new WorkspaceCommandStack and TransactionalEditingDomain using given IOperationHistory and ResourceSet
		WorkspaceCommandStackImpl stack = new WorkspaceCommandStackImpl(history) {
			/**
			 * Redefines the inherited method by forwarding to the
			 * {@link TransactionalCommandStack#execute(Command, Map)} method. Any checked exception thrown by that
			 * method is handled by {@link #handleError(Exception)} but is not propagated.
			 */
			@Override
			@SuppressWarnings("restriction")
			public void execute(Command command) {
				try {
					execute(command, WorkspaceTransactionUtil.getDefaultTransactionOptions());
				} catch (InterruptedException e) {
					// just log it. Note that the transaction is already rolled back,
					// so handleError() will not find an active transaction
					org.eclipse.emf.transaction.internal.Tracing.catching(AbstractTransactionalCommandStack.class, "execute", e); //$NON-NLS-1$
					handleError(e);
				} catch (RollbackException e) {
					// just log it. Note that the transaction is already rolled back,
					// so handleError() will not find an active transaction
					org.eclipse.emf.transaction.internal.Tracing.catching(AbstractTransactionalCommandStack.class, "execute", e); //$NON-NLS-1$
					handleError(e);
				}
			}

			/**
			 * Overridden to make sure that only last segment of command label is used as operation label in case that
			 * the former is a qualified label (see GenModel option "Editor > Creation Sub-menus" for details).
			 */
			@SuppressWarnings("restriction")
			@Override
			protected void doExecute(Command command, Map<?, ?> options) throws InterruptedException, RollbackException {
				EMFCommandOperation oper = new EMFCommandOperation(getDomain(), command, options) {
					@Override
					protected void improveLabel(Command cmd) {
						super.improveLabel(cmd);

						// Extract and use only last command label segment in case that it is a create child command and
						// has a qualified label
						if (cmd instanceof CreateChildCommand) {
							String label = getLabel();
							if (label != null) {
								int index = label.lastIndexOf("|"); //$NON-NLS-1$
								if (index != -1 && label.length() > index + 1) {
									label = label.substring(index + 1).trim();
									setLabel(EMFEditPlugin.INSTANCE.getString("_UI_CreateChildCommand_label", new Object[] { label })); //$NON-NLS-1$
								}
							}
						}
					}
				};

				// add the appropriate context
				oper.addContext(getDefaultUndoContext());

				try {
					IStatus status = getOperationHistory().execute(oper, new NullProgressMonitor(), null);

					if (status.getSeverity() >= IStatus.ERROR) {
						// the transaction must have rolled back if the status was
						// error or worse
						RollbackException exc = new RollbackException(status);
						Tracing.throwing(WorkspaceCommandStackImpl.class, "execute", exc); //$NON-NLS-1$
						throw exc;
					}

					notifyListeners();
				} catch (ExecutionException e) {
					Tracing.catching(WorkspaceCommandStackImpl.class, "execute", e); //$NON-NLS-1$
					command.dispose();

					if (e.getCause() instanceof RollbackException) {
						// throw the rollback
						RollbackException exc = (RollbackException) e.getCause();
						Tracing.throwing(WorkspaceCommandStackImpl.class, "execute", exc); //$NON-NLS-1$
						throw exc;
					} else if (e.getCause() instanceof RuntimeException) {
						// throw the programming error
						RuntimeException exc = (RuntimeException) e.getCause();
						Tracing.throwing(WorkspaceCommandStackImpl.class, "execute", exc); //$NON-NLS-1$
						throw exc;
					} else {
						// log the problem. We can't rethrow whatever it was
						handleError(e);
					}
				}
			}
		};
		TransactionalEditingDomain result = new ExtendedDiagramEditingDomain(new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE), stack, resourceSet);

		// Do default initialization
		mapResourceSet(result);
		configure(result);

		// Add IEditingDomainProvider adapter which EMF.Edit needs for retrieving EditingDomain from ResourceSet
		resourceSet.eAdapters().add(new AdapterFactoryEditingDomain.EditingDomainProvider(result));

		// Give the specified meta-model descriptors to the newly created editing domain
		((ExtendedDiagramEditingDomain) result).getMetaModelDescriptors().addAll(metaModelDescriptors);

		firePostCreateEditingDomain(metaModelDescriptors, result);
		return result;
	}

	protected void firePostCreateEditingDomain(Collection<IMetaModelDescriptor> metaModelDescriptors, TransactionalEditingDomain editingDomain) {
		for (IMetaModelDescriptor mmd : metaModelDescriptors) {
			// Retrieve and notify TEDFactoryListeners contributed to
			// 'org.eclipse.sphinx.emf.editingDomainFactoryListeners'
			for (ITransactionalEditingDomainFactoryListener listener : EditingDomainFactoryListenerRegistry.INSTANCE.getListeners(mmd)) {
				listener.postCreateEditingDomain(editingDomain);
			}
		}
	}

	protected void firePreDisposeEditingDomain(Collection<IMetaModelDescriptor> metaModelDescriptors, TransactionalEditingDomain editingDomain) {
		for (IMetaModelDescriptor mmd : metaModelDescriptors) {
			// Retrieve and notify TEDFactoryListeners contributed to
			// 'org.eclipse.sphinx.emf.editingDomainFactoryListeners'
			for (ITransactionalEditingDomainFactoryListener listener : EditingDomainFactoryListenerRegistry.INSTANCE.getListeners(mmd)) {
				listener.preDisposeEditingDomain(editingDomain);
			}
		}
	}
}
