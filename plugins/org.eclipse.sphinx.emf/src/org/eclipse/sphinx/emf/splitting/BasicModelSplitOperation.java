/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.emf.splitting;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.operations.AbstractLabeledWorkspaceRunnable;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.operations.ILabeledWorkspaceRunnable;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class BasicModelSplitOperation extends AbstractWorkspaceOperation implements IModelSplitOperation {

	private Collection<Resource> resources;
	private IModelSplitPolicy modelSplitPolicy;
	private boolean deleteOriginalResources = false;

	public BasicModelSplitOperation(Resource resource, IModelSplitPolicy modelSplitPolicy) {
		this(Collections.singletonList(resource), modelSplitPolicy);
	}

	public BasicModelSplitOperation(Collection<Resource> resources, IModelSplitPolicy modelSplitPolicy) {
		super(Messages.operation_splitModel_label);
		Assert.isNotNull(resources);
		Assert.isNotNull(modelSplitPolicy);

		this.resources = resources;
		this.modelSplitPolicy = modelSplitPolicy;
	}

	@Override
	public IModelSplitPolicy getModelSplitPolicy() {
		return modelSplitPolicy;
	}

	@Override
	public boolean isDeleteOriginalResources() {
		return deleteOriginalResources;
	}

	@Override
	public void setDeleteOriginalResources(boolean deleteOriginalResources) {
		this.deleteOriginalResources = deleteOriginalResources;
	}

	@Override
	public ISchedulingRule getRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected Map<?, ?> getSaveOptions() {
		return EcoreResourceUtil.getDefaultSaveOptions();
	}

	protected TransactionalEditingDomain getEditingDomain(Collection<Resource> resources) {
		Assert.isNotNull(resources);

		if (!resources.isEmpty()) {
			return TransactionUtil.getEditingDomain(resources.iterator().next());
		}
		return null;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		if (!resources.isEmpty()) {
			try {
				final TransactionalEditingDomain editingDomain = getEditingDomain(resources);

				ILabeledWorkspaceRunnable runnable = new AbstractLabeledWorkspaceRunnable(getLabel()) {
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						final SubMonitor progress = SubMonitor.convert(monitor, 3);
						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}

						ModelSplitProcessor processor = new ModelSplitProcessor();

						// Split given model resources
						splitOriginalResources(resources, processor, progress.newChild(1));

						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}

						// Saves resulting split model resources
						ResourceSet resourceSet = editingDomain != null ? editingDomain.getResourceSet() : new ResourceSetImpl();
						saveSplitResources(resourceSet, processor, progress.newChild(1));

						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}

						// Deletes original model resources, if required
						if (deleteOriginalResources) {
							deleteOriginalResources(resources, progress.newChild(1));
						}

						processor.dispose();
					}
				};

				if (editingDomain != null) {
					WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, monitor);
				} else {
					runnable.run(monitor);
				}
			} catch (OperationCanceledException ex) {
				throw ex;
			} catch (ExecutionException ex) {
				IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
				throw new CoreException(status);
			}
		}
	}

	protected void splitOriginalResources(Collection<Resource> resources, ModelSplitProcessor processor, IProgressMonitor monitor) {
		Assert.isNotNull(resources);
		Assert.isNotNull(processor);

		SubMonitor progress = SubMonitor.convert(monitor, resources.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (Resource resource : resources) {
			// Traverse the resource's contents, present each model object to split policy and collect resulting split
			// directives
			SubMonitor splitResourcesProgress = progress.newChild(1).setWorkRemaining(2);
			for (TreeIterator<EObject> iterator = resource.getAllContents(); iterator.hasNext();) {
				EObject eObject = iterator.next();
				IModelSplitDirective directive = modelSplitPolicy.getSplitDirective(eObject);
				if (directive != null) {
					processor.getModelSplitDirectives().add(directive);
					iterator.prune();
				}
			}
			splitResourcesProgress.worked(1);

			if (splitResourcesProgress.isCanceled()) {
				throw new OperationCanceledException();
			}

			// Process split directives
			processor.run(splitResourcesProgress.newChild(1));

			if (splitResourcesProgress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	protected void saveSplitResources(final ResourceSet resourceSet, final ModelSplitProcessor processor, IProgressMonitor monitor)
			throws CoreException {
		Assert.isNotNull(processor);

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				// Create resources for split models
				Set<Resource> resourcesToSave = new HashSet<Resource>();
				for (URI targetResourceURI : processor.getTargetResourceContents().keySet()) {
					List<EObject> targetResourceContents = processor.getTargetResourceContents().get(targetResourceURI);

					if (targetResourceContents != null && !targetResourceContents.isEmpty()) {
						IMetaModelDescriptor metaModelDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(targetResourceContents
								.iterator().next());
						String contentTypeId = metaModelDescriptor != null ? metaModelDescriptor.getDefaultContentTypeId() : null;
						Resource resource = EcoreResourceUtil.addNewModelResource(resourceSet, targetResourceURI, contentTypeId,
								targetResourceContents);
						resourcesToSave.add(resource);
					}
				}

				// Save split resources
				final SubMonitor progress = SubMonitor.convert(monitor, Messages.subTask_savingModels, resourcesToSave.size());
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				for (Resource resource : resourcesToSave) {
					EcoreResourceUtil.saveModelResource(resource, getSaveOptions());

					progress.worked(1);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}
				}
			}
		};

		// Execute save operation as IWorkspaceRunnable on workspace in order to avoid resource change
		// notifications during transaction execution
		/*
		 * !! Important Note !! Setting the IWorkspace.AVOID_UPDATE flag on the outer workspace job or workspace
		 * runnable from which this method is called doesn't help because the matter of executing a transaction inside
		 * suppresses its effect.
		 */
		/*
		 * !! Important Note !! Only set IWorkspace.AVOID_UPDATE flag but don't define any scheduling restrictions for
		 * the save operation right here (this must only be done on the outer workspace job or workspace runnable from
		 * which this method is called). Otherwise it would be likely to end up in deadlocks with operations which
		 * already have acquired exclusive access to the workspace but are waiting for exclusive access to the model
		 * (i.e. for the transaction).
		 */
		ResourcesPlugin.getWorkspace().run(runnable, null, IWorkspace.AVOID_UPDATE, monitor);
	}

	protected void deleteOriginalResources(final Collection<Resource> resources, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(resources);

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				final SubMonitor progress = SubMonitor.convert(monitor, resources.size());
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				for (Resource resource : resources) {
					// Delete resource if it exists physically, just unload it otherwise
					if (EcoreResourceUtil.exists(resource.getURI())) {
						try {
							resource.delete(Collections.emptyMap());
						} catch (IOException ex) {
							PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
						}
					} else {
						EcoreResourceUtil.unloadResource(resource);
					}

					progress.worked(1);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}
				}
			}
		};

		// Execute save operation as IWorkspaceRunnable on workspace in order to avoid resource change
		// notifications during transaction execution
		/*
		 * !! Important Note !! Setting the IWorkspace.AVOID_UPDATE flag on the outer workspace job or workspace
		 * runnable from which this method is called doesn't help because the matter of executing a transaction inside
		 * suppresses its effect.
		 */
		/*
		 * !! Important Note !! Only set IWorkspace.AVOID_UPDATE flag but don't define any scheduling restrictions for
		 * the save operation right here (this must only be done on the outer workspace job or workspace runnable from
		 * which this method is called). Otherwise it would be likely to end up in deadlocks with operations which
		 * already have acquired exclusive access to the workspace but are waiting for exclusive access to the model
		 * (i.e. for the transaction).
		 */
		ResourcesPlugin.getWorkspace().run(runnable, null, IWorkspace.AVOID_UPDATE, monitor);
	}
}
