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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.mwe2.runtime.workflow.Workflow;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sphinx.emf.mwe.dynamic.ModelWorkflowContext;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.jdt.loaders.DelegatingClassLoader;
import org.eclipse.sphinx.jdt.loaders.ProjectClassLoader;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class BasicWorkflowRunnerOperation extends AbstractWorkspaceOperation implements IWorkflowRunnerOperation {

	private Object input;

	private Object workflow;

	public BasicWorkflowRunnerOperation(String label) {
		super(label);
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object object) {
		input = object;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#getInput()
	 */
	@Override
	public Object getInput() {
		return input;
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

	protected IJavaProject getWorkflowProject() {
		if (workflow instanceof IFile) {
			return JavaCore.create(((IFile) workflow).getProject());
		}

		if (workflow instanceof IJavaElement) {
			return ((IJavaElement) workflow).getJavaProject();
		}

		return null;
	}

	protected IJavaElement getWorkflowJavaElement() {
		if (workflow instanceof IFile) {
			IFile workflowFile = (IFile) workflow;

			IFile workflowJavaFile = XtendUtil.getJavaFile(workflowFile);
			IJavaProject project = getWorkflowProject();
			if (project != null) {
				try {
					return project.findElement(workflowJavaFile.getProjectRelativePath().removeFirstSegments(1));
				} catch (JavaModelException e) {
					PlatformLogUtil.logAsError(Activator.getDefault(), e);
				}
			}
		}

		if (workflow instanceof IJavaElement) {
			return (IJavaElement) workflow;
		}

		return null;
	}

	/*
	 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		IJavaElement workflowJavaElement = getWorkflowJavaElement();
		if (workflowJavaElement instanceof ICompilationUnit) {
			run((ICompilationUnit) workflowJavaElement, getInput(), monitor);
		}
		if (workflowJavaElement instanceof IClassFile) {
			run((IClassFile) workflowJavaElement, getInput(), monitor);
		}
	}

	protected void run(ICompilationUnit compilationUnit, Object input, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(compilationUnit);

		try {
			ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
			if (input != null) {
				parentClassLoader = new DelegatingClassLoader(parentClassLoader, input.getClass().getClassLoader());
			}

			IType workflowType = compilationUnit.findPrimaryType();
			ProjectClassLoader projectClassLoader = new ProjectClassLoader(workflowType.getJavaProject(), parentClassLoader);

			// TODO Surround with appropriate tracing option
			// ClassLoaderExtensions.printHierarchy(projectClassLoader);

			Class<?> workflowClass = projectClassLoader.loadClass(workflowType.getFullyQualifiedName());
			Object instance = workflowClass.newInstance();
			if (!(instance instanceof Workflow)) {
				IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), new IllegalStateException(
						"Workflow to be executed must be an instance of " + Workflow.class.getName())); //$NON-NLS-1$
				throw new CoreException(status);
			}
			Workflow workflow = (Workflow) instance;

			ModelWorkflowContext context = new ModelWorkflowContext(input, monitor);
			workflow.run(context);
		} catch (OperationCanceledException ex) {

		} catch (CoreException ex) {
			throw ex;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}

	protected void run(IClassFile classFile, Object input, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Running workflows from Java *.class files in the workspace is not yet supported"); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.sphinx.platform.operations.IWorkspaceOperation#getRule()
	 */
	@Override
	public ISchedulingRule getRule() {
		// TODO Auto-generated method stub
		return null;
	}
}