/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.workspace.loading.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.BasicEList.UnmodifiableEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.loading.SchedulingRuleFactory;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class UnresolveUnreachableCrossProjectReferencesOperation extends AbstractWorkspaceOperation {

	private Collection<IProject> projectsWithUnreachableCrossRefrencesToUnresolve;

	public UnresolveUnreachableCrossProjectReferencesOperation(Collection<IProject> projectsWithUnreachableCrossRefrencesToUnresolve) {
		super(Messages.job_unresolvingUnreachableCrossProjectReferences);
		this.projectsWithUnreachableCrossRefrencesToUnresolve = projectsWithUnreachableCrossRefrencesToUnresolve;
	}

	@Override
	public ISchedulingRule getRule() {
		return new SchedulingRuleFactory().createLoadSchedulingRule(projectsWithUnreachableCrossRefrencesToUnresolve, false);
	}

	public Collection<IProject> getProjectsWithUnreachableCrossRefrencesToUnresolve() {
		return projectsWithUnreachableCrossRefrencesToUnresolve;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		runUnresolveUnreachableCrossProjectReferences(projectsWithUnreachableCrossRefrencesToUnresolve, monitor);
	}

	private void runUnresolveUnreachableCrossProjectReferences(final Collection<IProject> projects, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(projects);

		for (IProject project : projects) {
			Collection<IModelDescriptor> modelsInProject = ModelDescriptorRegistry.INSTANCE.getModels(project);
			SubMonitor progress = SubMonitor.convert(monitor,
					NLS.bind(Messages.task_unresolvingUnreachableCrossProjectReferencesInProject, project.getName()), modelsInProject.size());
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			for (IModelDescriptor modelDescriptor : modelsInProject) {
				unresolveUnreachableCrossProjectReferencesInModel(modelDescriptor, progress.newChild(1));

				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		}
	}

	private void unresolveUnreachableCrossProjectReferencesInModel(final IModelDescriptor modelDescriptor, final IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(modelDescriptor);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Collection<Resource> resources = modelDescriptor.getLoadedResources(true);
				SubMonitor progress = SubMonitor.convert(monitor, resources.size());
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				for (Resource resource : resources) {
					progress.subTask(NLS.bind(Messages.subtask_unresolvingUnreachableCrossProjectReferencesInResource, resource.getURI()));
					try {
						TreeIterator<EObject> allContents = resource.getAllContents();
						while (allContents.hasNext()) {
							EObject object = allContents.next();
							for (EReference reference : object.eClass().getEAllReferences()) {
								if (!reference.isContainment() && !reference.isContainer()) {
									if (reference.isMany()) {
										@SuppressWarnings("unchecked")
										EList<EObject> referencedObjects = (EList<EObject>) object.eGet(reference);
										// Be sure that referenced objects are not contained in an unmodifiable list
										if (!(referencedObjects instanceof UnmodifiableEList<?>)) {
											List<EObject> safeReferencedObjects = new ArrayList<EObject>(referencedObjects);
											for (EObject referencedObject : safeReferencedObjects) {
												if (referencedObject != null && !referencedObject.eIsProxy()) {
													// Referenced object no longer part of same model as given object?
													if (!modelDescriptor.getScope().belongsTo(referencedObject.eResource(), true)) {
														referencedObjects.remove(referencedObject);
														referencedObjects.add(EObjectUtil.createProxyFrom(referencedObject, object.eResource()));
													}
												}
											}
										}
									} else {
										EObject referencedObject = (EObject) object.eGet(reference);
										if (referencedObject != null && !referencedObject.eIsProxy()) {
											// Referenced object no longer part of same model as given object?
											if (!modelDescriptor.getScope().belongsTo(referencedObject.eResource(), true)) {
												object.eSet(reference, EObjectUtil.createProxyFrom(referencedObject, object.eResource()));
											}
										}
									}
								}
							}

							if (progress.isCanceled()) {
								throw new OperationCanceledException();
							}
						}
					} catch (Exception ex) {
						PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
					}
					progress.worked(1);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}
					modelDescriptor.getEditingDomain().yield();
				}
			}
		};

		try {
			/*
			 * !! Important note !! There seems to be a highly critical bug in EMF Transaction: We have observed that
			 * when a write transaction and a read transaction are started at the same time on the same editing domain
			 * in two different threads, the editing domain is accessed concurrently but not exclusively by both
			 * transactions. As a consequence, they both try to start and shut down the editing domain's change recorder
			 * which may lead to unpredictable results ranging from unexpected rollbacks to deadlocks. As a workaround,
			 * we run the write transaction with a OPTION_NO_UNDO which avoids that the write transaction accesses the
			 * editing domain's change recorder. We would do so even in absence of this potential EMF Transaction bug
			 * because we don't want enable users to undo the unresolving of unreachable cross project references
			 * anyway.
			 */
			// FIXME File bug to EMF Transaction: Editing domain accessed concurrently when a write transaction and a
			// read transaction are started at the same time by two different threads
			IOperationHistory operationHistory = WorkspaceTransactionUtil.getOperationHistory(modelDescriptor.getEditingDomain());
			Map<String, Object> options = WorkspaceTransactionUtil.getDefaultTransactionOptions();
			options.put(Transaction.OPTION_NO_UNDO, Boolean.TRUE);
			WorkspaceTransactionUtil.executeInWriteTransaction(modelDescriptor.getEditingDomain(), runnable,
					Messages.operation_unresolvingUnreachableCrossProjectReferencesInModel, operationHistory, options, null);
		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}
}
