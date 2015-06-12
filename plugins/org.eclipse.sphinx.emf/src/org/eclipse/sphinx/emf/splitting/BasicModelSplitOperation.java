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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
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

	private Map<EObject, Map<URI, EObject>> copyEObjectsMap = Collections.synchronizedMap(new HashMap<EObject, Map<URI, EObject>>());
	private Map<URI, Set<EObject>> targetResourceContentsMap = Collections.synchronizedMap(new HashMap<URI, Set<EObject>>());

	public BasicModelSplitOperation(Resource resource, IModelSplitPolicy modelSplitPolicy) {
		this(Collections.singletonList(resource), modelSplitPolicy);
	}

	public BasicModelSplitOperation(Collection<Resource> resources, IModelSplitPolicy modelSplitPolicy) {
		super(Messages.operation_modelElementSplitting_label);
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

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		if (!resources.isEmpty()) {
			try {
				ILabeledWorkspaceRunnable runnable = new AbstractLabeledWorkspaceRunnable(getLabel()) {
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						final SubMonitor progress = SubMonitor.convert(monitor, 3);
						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}

						try {
							// Split given model resources
							splitOriginalResources(resources, progress.newChild(1));

							// Saves resulting splitted model resources
							saveSplittedResources(getEditingDomain(), progress.newChild(1));

							// Deletes original model resources, if required
							deleteOriginalResources(resources, progress.newChild(1));

							copyEObjectsMap.clear();
							targetResourceContentsMap.clear();
						} catch (CoreException ex) {
							throw new RuntimeException(ex);
						}

					}
				};
				TransactionalEditingDomain editingDomain = getEditingDomain();
				if (editingDomain != null) {
					WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, monitor);
				}
			} catch (OperationCanceledException ex) {
				throw ex;
			} catch (ExecutionException ex) {
				IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
				throw new CoreException(status);
			}
		}
	}

	TransactionalEditingDomain getEditingDomain() {
		TransactionalEditingDomain editingDomain = null;
		for (Resource resource : resources) {
			editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(resource);
			if (editingDomain != null) {
				break;
			}
		}
		return editingDomain;
	}

	protected void splitOriginalResources(Collection<Resource> resources, IProgressMonitor monitor) {
		Assert.isNotNull(resources);
		SubMonitor progress = SubMonitor.convert(monitor, resources.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (Resource resource : resources) {
			// Collect model objects to be processed
			SubMonitor splitResourcesProgress = progress.newChild(1).setWorkRemaining(2);
			List<EObject> eObjects = new UniqueEList.FastCompare<EObject>();
			for (TreeIterator<EObject> iterator = resource.getAllContents(); iterator.hasNext();) {
				EObject eObject = iterator.next();
				if (modelSplitPolicy.getSplitDirective(eObject) == null) {
					continue;
				}
				eObjects.add(eObject);
				iterator.prune();
			}
			splitResourcesProgress.worked(1);

			if (splitResourcesProgress.isCanceled()) {
				throw new OperationCanceledException();
			}

			// Process model objects to be split
			SubMonitor splitObjectsProgress = SubMonitor.convert(splitResourcesProgress.newChild(1), eObjects.size());
			for (EObject eObject : eObjects) {
				ModelSplitDirective directive = modelSplitPolicy.getSplitDirective(eObject);
				EObject copyEObject = getCopiedEObject(eObject, directive.getTargetResourceURI());
				if (copyEObject == null) {
					clone(eObject, directive, splitObjectsProgress.newChild(1));
				} else {
					splitObjectsProgress.worked(1);
				}
			}

			if (splitResourcesProgress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	protected EObject clone(EObject eObject, ModelSplitDirective modelSplitDirective, IProgressMonitor monitor) {
		Assert.isNotNull(eObject);
		Assert.isNotNull(modelSplitDirective);

		EObject result = null;
		List<EObject> ancestors = new UniqueEList.FastCompare<EObject>();
		ancestors.add(eObject);

		InternalEObject internalEObject = (InternalEObject) eObject;
		for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container = internalEObject.eInternalContainer()) {
			ancestors.add(container);
			internalEObject = container;
		}
		EObject rootEObject = internalEObject;

		SubMonitor progress = SubMonitor.convert(monitor, ancestors.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		URI targetResourceURI = modelSplitDirective.getTargetResourceURI();
		for (int i = 0; i < ancestors.size(); i++) {
			EObject ancestor = ancestors.get(i);

			EObject copyEObject = getCopiedEObject(ancestor, targetResourceURI);
			if (copyEObject == null) {
				copyEObject = i == 0 ? ancestor : ModelSplitUtil.copy(ancestor, false);
				Map<URI, EObject> targets = new HashMap<URI, EObject>();
				targets.put(targetResourceURI, copyEObject);
				copyEObjectsMap.put(ancestor, targets);
			}

			if (i == 0) {
				result = copyEObject;
			}

			if (i < ancestors.size() - 1) {
				EObject container = ancestors.get(i + 1);
				EStructuralFeature feature = ancestor.eContainingFeature();
				if (container != null && feature != null) {
					EObject copyContainer = getCopiedEObject(container, targetResourceURI);
					if (copyContainer == null) {
						copyContainer = ModelSplitUtil.copy(container, false);
						Map<URI, EObject> targets = new HashMap<URI, EObject>();
						targets.put(targetResourceURI, copyContainer);
						copyEObjectsMap.put(container, targets);
					}
					ModelSplitUtil.setPropertyValue(copyContainer, feature, copyEObject);
				}
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		Set<EObject> targetResourceContents = targetResourceContentsMap.get(targetResourceURI);
		if (targetResourceContents == null) {
			targetResourceContents = new HashSet<EObject>();
		}
		EObject copyRootEObject = getCopiedEObject(rootEObject, targetResourceURI);
		targetResourceContents.add(copyRootEObject);
		targetResourceContentsMap.put(targetResourceURI, targetResourceContents);
		return result;
	}

	protected EObject getCopiedEObject(EObject eObject, URI targetResourceURI) {
		Assert.isNotNull(eObject);
		Assert.isNotNull(targetResourceURI);

		Map<URI, EObject> map = copyEObjectsMap.get(eObject);
		return map != null ? map.get(targetResourceURI) : null;
	}

	protected Map<?, ?> getSaveOptions() {
		return EcoreResourceUtil.getDefaultSaveOptions();
	}

	protected void saveSplittedResources(final TransactionalEditingDomain editingDomain, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(editingDomain);

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				Set<Resource> resourcesToSave = new HashSet<Resource>();
				for (Entry<URI, Set<EObject>> entry : targetResourceContentsMap.entrySet()) {
					URI resourceURI = entry.getKey();
					Set<EObject> rootEObjects = entry.getValue();

					IMetaModelDescriptor metaModelDescriptor = rootEObjects != null && !rootEObjects.isEmpty() ? MetaModelDescriptorRegistry.INSTANCE
							.getDescriptor(rootEObjects.iterator().next()) : null;
					Resource resource = EcoreResourceUtil.addNewModelResource(editingDomain.getResourceSet(), resourceURI,
							metaModelDescriptor != null ? metaModelDescriptor.getDefaultContentTypeId() : "", rootEObjects); //$NON-NLS-1$
					resourcesToSave.add(resource);
				}

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

		if (deleteOriginalResources) {
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					final SubMonitor progress = SubMonitor.convert(monitor, resources.size());
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}

					for (Resource resource : resources) {
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
			 * runnable from which this method is called doesn't help because the matter of executing a transaction
			 * inside suppresses its effect.
			 */
			/*
			 * !! Important Note !! Only set IWorkspace.AVOID_UPDATE flag but don't define any scheduling restrictions
			 * for the save operation right here (this must only be done on the outer workspace job or workspace
			 * runnable from which this method is called). Otherwise it would be likely to end up in deadlocks with
			 * operations which already have acquired exclusive access to the workspace but are waiting for exclusive
			 * access to the model (i.e. for the transaction).
			 */
			ResourcesPlugin.getWorkspace().run(runnable, null, IWorkspace.AVOID_UPDATE, monitor);
		}
	}
}
