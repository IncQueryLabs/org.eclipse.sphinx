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
package org.eclipse.sphinx.emf.splitting.operations;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.splitting.IModelElementSplittingListener;
import org.eclipse.sphinx.emf.splitting.internal.Activator;
import org.eclipse.sphinx.emf.splitting.internal.messages.Messages;
import org.eclipse.sphinx.emf.splitting.util.ModelElementSplittingUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class BasicModelElementSplittingOperation extends AbstractWorkspaceOperation implements IModelElementSplittingOperation {

	private Collection<Resource> resources;
	boolean deleteOriginalResources = true;
	private IModelElementSplittingListener modelElementSplittingListener;
	private Map<EObject, Map<URI, EObject>> copyEObjectsMap = Collections.synchronizedMap(new HashMap<EObject, Map<URI, EObject>>());
	private Map<URI, Set<EObject>> targetResourceRootEObjectsMap = Collections.synchronizedMap(new HashMap<URI, Set<EObject>>());

	public BasicModelElementSplittingOperation(Resource resource) {
		this(Collections.singleton(resource));
	}

	public BasicModelElementSplittingOperation(Collection<Resource> resources) {
		this(resources, null);
	}

	public BasicModelElementSplittingOperation(Collection<Resource> resources, IModelElementSplittingListener modelElementSplittingListener) {
		super(Messages.operation_modelElementSplitting_label);
		Assert.isNotNull(resources);
		this.resources = resources;
		this.modelElementSplittingListener = modelElementSplittingListener;
	}

	@Override
	public IModelElementSplittingListener getModelElementSplittingListener() {
		return modelElementSplittingListener;
	}

	@Override
	public void setModelElementSplittingListener(IModelElementSplittingListener modelElementSplittingListener) {
		this.modelElementSplittingListener = modelElementSplittingListener;
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
		// TODO Compute scheduling rules
		return null;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		if (modelElementSplittingListener != null && !resources.isEmpty()) {
			final SubMonitor progress = SubMonitor.convert(monitor, resources.size() + 10);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			try {
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						for (Resource resource : resources) {
							splitting(resource, progress.newChild(1));
						}

						// Saves new resources
						saveModels(getEditingDomain(), progress.newChild(10));

						// Deletes original resources, if needed
						deleteOriginalResources(resources);

						copyEObjectsMap.clear();
						targetResourceRootEObjectsMap.clear();
					}
				};

				TransactionalEditingDomain editingDomain = getEditingDomain();
				if (editingDomain != null) {
					// Execute in write transaction
					WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, "Model Element Splitting"); //$NON-NLS-1$
				}
			} catch (OperationCanceledException ex) {
				throw ex;
			} catch (Exception ex) {
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

	protected void splitting(Resource resource, IProgressMonitor monitor) {
		Assert.isNotNull(resource);

		if (modelElementSplittingListener != null) {
			List<EObject> eObjects = new UniqueEList.FastCompare<EObject>();
			for (TreeIterator<EObject> iterator = resource.getAllContents(); iterator.hasNext();) {
				EObject eObject = iterator.next();
				URI targetResourceURI = modelElementSplittingListener.getTargetResourceURI(eObject);
				if (targetResourceURI == null) {
					continue;
				}

				eObjects.add(eObject);
				iterator.prune();
			}

			SubMonitor progress = SubMonitor.convert(monitor, eObjects.size());
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			for (EObject eObject : eObjects) {
				URI targetResourceURI = modelElementSplittingListener.getTargetResourceURI(eObject);
				EObject copyEObject = getCopyEObject(eObject, targetResourceURI);
				if (copyEObject == null) {
					clone(eObject, targetResourceURI, progress.newChild(1));
				} else {
					progress.worked(1);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}
				}
			}
		}
	}

	protected EObject clone(EObject modelObject, URI targetResourceURI, IProgressMonitor monitor) {
		Assert.isNotNull(modelObject);
		Assert.isNotNull(targetResourceURI);

		EObject result = null;
		List<EObject> eObjectAncestors = new UniqueEList.FastCompare<EObject>();
		eObjectAncestors.add(modelObject);

		InternalEObject internalEObject = (InternalEObject) modelObject;
		for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container = internalEObject.eInternalContainer()) {
			eObjectAncestors.add(container);
			internalEObject = container;
		}
		EObject rootEObject = internalEObject;

		SubMonitor progress = SubMonitor.convert(monitor, eObjectAncestors.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (int i = 0; i < eObjectAncestors.size(); i++) {
			EObject eObject = eObjectAncestors.get(i);

			EObject copyEObject = getCopyEObject(eObject, targetResourceURI);
			if (copyEObject == null) {
				copyEObject = i == 0 ? eObject : ModelElementSplittingUtil.copy(eObject, false);
				Map<URI, EObject> targets = new HashMap<URI, EObject>();
				targets.put(targetResourceURI, copyEObject);
				copyEObjectsMap.put(eObject, targets);
			}

			if (i == 0) {
				result = copyEObject;
			}

			if (i < eObjectAncestors.size() - 1) {
				EObject container = eObjectAncestors.get(i + 1);
				EStructuralFeature feature = eObject.eContainingFeature();
				if (container != null && feature != null) {
					EObject copyContainer = getCopyEObject(container, targetResourceURI);
					if (copyContainer == null) {
						copyContainer = ModelElementSplittingUtil.copy(container, false);
						Map<URI, EObject> targets = new HashMap<URI, EObject>();
						targets.put(targetResourceURI, copyContainer);
						copyEObjectsMap.put(container, targets);
					}
					ModelElementSplittingUtil.setPropertyValue(copyContainer, feature, copyEObject);
				}
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		Set<EObject> rootEObjects = targetResourceRootEObjectsMap.get(targetResourceURI);
		if (rootEObjects == null) {
			rootEObjects = new HashSet<EObject>();
		}
		EObject copyRootEObject = getCopyEObject(rootEObject, targetResourceURI);
		rootEObjects.add(copyRootEObject);
		targetResourceRootEObjectsMap.put(targetResourceURI, rootEObjects);
		return result;
	}

	protected EObject getCopyEObject(EObject eObject, URI targetResourceURI) {
		Map<URI, EObject> map = copyEObjectsMap.get(eObject);
		return map != null ? map.get(targetResourceURI) : null;
	}

	protected void saveModels(TransactionalEditingDomain editingDomain, IProgressMonitor monitor) {
		Assert.isNotNull(editingDomain);

		Set<Resource> resourcesToSave = new HashSet<Resource>();
		for (Entry<URI, Set<EObject>> entry : targetResourceRootEObjectsMap.entrySet()) {
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
		}
	}

	protected void deleteOriginalResources(Collection<Resource> resources) {
		Assert.isNotNull(resources);

		if (deleteOriginalResources) {
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
			}
		}
	}

	protected Map<?, ?> getSaveOptions() {
		return EcoreResourceUtil.getDefaultSaveOptions();
	}
}
