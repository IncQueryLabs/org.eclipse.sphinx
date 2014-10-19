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
package org.eclipse.sphinx.emf.mwe.dynamic.operations;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;
import org.eclipse.emf.mwe2.runtime.workflow.Workflow;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.sphinx.emf.mwe.dynamic.ModelWorkflowContext;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkspaceWorkflow;
import org.eclipse.sphinx.emf.mwe.dynamic.components.IModelWorkflowComponent;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.jdt.loaders.DelegatingClassLoader;
import org.eclipse.sphinx.jdt.loaders.ProjectClassLoader;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class BasicWorkflowRunnerOperation extends AbstractWorkspaceOperation implements IWorkflowRunnerOperation {

	private Object model;

	private Object workflow;

	private Workflow workflowInstance = null;

	public BasicWorkflowRunnerOperation(String label) {
		super(label);
	}

	/*
	 * @see org.eclipse.sphinx.platform.operations.IWorkspaceOperation#getRule()
	 */
	@Override
	public ISchedulingRule getRule() {
		// TODO Compute scheduling rule by combining scheduling rules from workflow components
		return null;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#setModel(java.lang.Object)
	 */
	@Override
	public void setModel(Object model) {
		this.model = model;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#getModel()
	 */
	@Override
	public Object getModel() {
		return model;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#getWorkflow()
	 */
	@Override
	public Object getWorkflow() {
		return workflow;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#setWorkflow(java.lang.Object)
	 */
	@Override
	public void setWorkflow(Object workflow) {
		this.workflow = workflow;
	}

	protected ClassLoader getParentClassLoader(Object model) {
		ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
		if (model != null) {
			parentClassLoader = new DelegatingClassLoader(parentClassLoader, model.getClass().getClassLoader());
		}
		return parentClassLoader;
	}

	protected IType getWorkflowType() throws CoreException {
		if (workflow instanceof IType) {
			return (IType) workflow;
		}
		if (workflow instanceof ICompilationUnit) {
			return ((ICompilationUnit) workflow).findPrimaryType();
		}
		if (workflow instanceof IFile) {
			IJavaElement workflowJavaElement = XtendUtil.getJavaElement((IFile) workflow);
			if (workflowJavaElement instanceof ICompilationUnit) {
				return ((ICompilationUnit) workflowJavaElement).findPrimaryType();
			}
		}
		return null;
	}

	protected Class<?> getWorkflowClass() {
		if (workflow instanceof Class<?>) {
			return (Class<?>) workflow;
		}
		return null;
	}

	protected Class<?> loadWorkflowClass(IType workflowType, Object model) throws ClassNotFoundException {
		ClassLoader parentClassLoader = getParentClassLoader(model);
		ProjectClassLoader projectClassLoader = new ProjectClassLoader(workflowType.getJavaProject(), parentClassLoader);

		// TODO Surround with appropriate tracing option
		// ClassLoaderExtensions.printHierarchy(projectClassLoader);

		return projectClassLoader.loadClass(workflowType.getFullyQualifiedName());
	}

	protected Workflow createWorkflowInstance(IType workflowType, Object model) throws CoreException {
		Assert.isNotNull(workflowType);

		try {
			Class<?> workflowClass = loadWorkflowClass(workflowType, model);

			return createWorkflowInstance(workflowClass);
		} catch (CoreException ex) {
			throw ex;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}

	protected Workflow createWorkflowInstance(Class<?> clazz) throws CoreException {
		Assert.isNotNull(clazz);

		try {
			Object instance = clazz.newInstance();

			if (!(instance instanceof Workflow)) {
				IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), new IllegalStateException(
						"Workflow to be executed must be an instance of " + Workflow.class.getName())); //$NON-NLS-1$
				throw new CoreException(status);
			}

			return (Workflow) instance;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}

	protected Workflow getWorkflowInstance() throws CoreException {
		if (workflowInstance == null) {
			IType workflowType = getWorkflowType();
			if (workflowType != null) {
				// Workflow is a Java or Xtend source file
				workflowInstance = createWorkflowInstance(workflowType, getModel());
			} else {
				Class<?> workflowClass = getWorkflowClass();
				if (workflowClass != null) {
					// Workflow is a binary Java class
					workflowInstance = createWorkflowInstance(workflowClass);
				}
			}
		}
		return workflowInstance;
	}

	protected boolean hasModelWorkflowComponents(Workflow workflow) {
		if (workflow instanceof WorkspaceWorkflow) {
			List<IWorkflowComponent> children = ((WorkspaceWorkflow) workflow).getChildren();
			for (IWorkflowComponent component : children) {
				if (component instanceof IModelWorkflowComponent) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isModifyingModel(Workflow workflow) {
		if (workflow instanceof WorkspaceWorkflow) {
			List<IWorkflowComponent> children = ((WorkspaceWorkflow) workflow).getChildren();
			for (IWorkflowComponent component : children) {
				if (component instanceof IModelWorkflowComponent) {
					if (((IModelWorkflowComponent) component).isModifyingModel()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(final IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		try {
			final Workflow workflow = getWorkflowInstance();
			if (workflow == null) {
				return;
			}

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					ModelWorkflowContext context = new ModelWorkflowContext(model, monitor);
					workflow.run(context);
				}
			};

			// Workflow dealing with some model?
			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(model);
			if (editingDomain != null && hasModelWorkflowComponents(workflow)) {
				// Workflow intending to modify the model?
				if (isModifyingModel(workflow)) {
					// Execute in write transaction
					WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, getLabel());
				} else {
					// Execute in read transaction
					editingDomain.runExclusive(runnable);
				}
			} else {
				// Execute right away
				runnable.run();
			}
		} catch (OperationCanceledException ex) {
			throw ex;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}
}