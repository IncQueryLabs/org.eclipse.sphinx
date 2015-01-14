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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;
import org.eclipse.emf.mwe2.runtime.workflow.Workflow;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.dynamic.ModelWorkflowContext;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkflowContributorRegistry;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkspaceWorkflow;
import org.eclipse.sphinx.emf.mwe.dynamic.components.IModelWorkflowComponent;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;
import org.eclipse.sphinx.emf.saving.SaveIndicatorUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.jdt.loaders.DelegatingClassLoader;
import org.eclipse.sphinx.jdt.loaders.ProjectClassLoader;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class BasicWorkflowRunnerOperation extends AbstractWorkspaceOperation implements IWorkflowRunnerOperation {

	private Object model;

	private Object workflow;

	private Workflow workflowInstance = null;

	private Collection<URI> modelURIs;
	private Set<Resource> modelResources = new HashSet<Resource>();

	public BasicWorkflowRunnerOperation(String label, URI modelURI) {
		this(label, Collections.singletonList(modelURI));
	}

	public BasicWorkflowRunnerOperation(String label, Collection<URI> modelURIs) {
		super(label);
		this.modelURIs = modelURIs;
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

	protected TransactionalEditingDomain getEditingDomain(Object model) {
		if (model instanceof EObject || model instanceof Resource) {
			return TransactionUtil.getEditingDomain(model);
		}
		if (model instanceof List) {
			List<?> models = (List<?>) model;
			if (!models.isEmpty()) {
				return getEditingDomain(models.get(0));
			}
		}
		return null;
	}

	protected ClassLoader getParentClassLoader(Object model) {
		ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
		if (model != null) {
			ClassLoader modelClassLoader = getModelClassLoader(model);
			if (modelClassLoader != null) {
				parentClassLoader = new DelegatingClassLoader(parentClassLoader, modelClassLoader);
			}
		}
		return parentClassLoader;
	}

	protected ClassLoader getModelClassLoader(Object model) {
		if (model instanceof EObject) {
			return model.getClass().getClassLoader();
		}
		if (model instanceof Resource) {
			Resource resource = (Resource) model;
			if (!resource.getContents().isEmpty()) {
				return getModelClassLoader(resource.getContents().get(0));
			}
		}
		if (model instanceof List) {
			List<?> models = (List<?>) model;
			if (!models.isEmpty()) {
				return getModelClassLoader(models.get(0));
			}
		}
		return null;
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
		Assert.isNotNull(workflowType);

		// Find out where given workflow type originates from
		if (workflowType.getParent() instanceof ICompilationUnit) {
			// Workflow type refers to an on-the-fly compiled Java class in the runtime workspace

			ClassLoader parentClassLoader = getParentClassLoader(model);
			ProjectClassLoader projectClassLoader = new ProjectClassLoader(workflowType.getJavaProject(), parentClassLoader);

			// TODO Surround with appropriate tracing option
			// ClassLoaderExtensions.printHierarchy(projectClassLoader);

			return projectClassLoader.loadClass(workflowType.getFullyQualifiedName());
		} else {
			// Workflow type refers to a binary Java class from the running Eclipse instance

			// Load Java class behind workflow type from underlying contributor plug-in
			return WorkflowContributorRegistry.INSTANCE.loadContributedClass(workflowType);
		}
	}

	protected Workflow createWorkflowInstance(IType workflowType, Object model) throws CoreException {
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
			final SubMonitor progress = SubMonitor.convert(monitor, 100);
			final Workflow workflow = getWorkflowInstance();
			if (workflow == null) {
				return;
			}

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Load selected Sphinx/EMF model file (if any)
					try {
						model = loadModel(progress.newChild(5));
					} catch (OperationCanceledException ex) {
						throw ex;
					} catch (CoreException ex) {
						throw new RuntimeException(ex);
					}

					ModelWorkflowContext context = new ModelWorkflowContext(model, progress.newChild(90));
					workflow.run(context);

					// Save model if needed
					saveModel(progress.newChild(5));
				}
			};

			// Workflow dealing with some model?
			TransactionalEditingDomain editingDomain = getEditingDomain(model);
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

	protected Object loadModel(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		if (modelURIs == null || modelURIs.isEmpty()) {
			return null;
		}

		final SubMonitor progress = SubMonitor.convert(monitor, modelURIs.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		List<EObject> modelObjects = new ArrayList<EObject>();

		// Loads Sphinx integrated models
		ModelLoadManager.INSTANCE.loadURIs(modelURIs, false, monitor);

		ResourceSet resouceSet = new ScopingResourceSetImpl();
		for (URI modelURI : modelURIs) {
			IFile file = EcorePlatformUtil.getFile(modelURI.trimFragment());
			// Sphinx integrated model
			if (ModelDescriptorRegistry.INSTANCE.isModelFile(file)) {
				String fragment = modelURI.fragment();
				if (fragment != null) {
					// URI refers to a model object
					EObject eObject = EcorePlatformUtil.getEObject(modelURI);
					if (eObject == null) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(),
								NLS.bind(Messages.modelResourceContainsNoMatchingElementError, modelURI.toPlatformString(true), fragment));
						throw new CoreException(status);
					}
					modelObjects.add(eObject);
					modelResources.add(eObject.eResource());
				} else {
					// URI refers to a model resource
					// Exclude Xtend files from being considered as workflow models
					if (!XtendUtil.isXtendResource(modelURI)) {
						Resource modelResource = EcorePlatformUtil.getResource(modelURI);
						if (modelResource == null) {
							IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(),
									NLS.bind(Messages.modelResourceCouldNotBeLoadedError, modelURI.toPlatformString(true)));
							throw new CoreException(status);
						}
						modelObjects.addAll(modelResource.getContents());
						modelResources.add(modelResource);
					}
				}
			} else {
				// Regular EMF model
				String fragment = modelURI.fragment();
				if (fragment != null) {
					// URI refers to a model object
					EObject eObject = EcoreResourceUtil.loadEObject(resouceSet, modelURI);
					if (eObject == null) {
						IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(),
								NLS.bind(Messages.modelResourceContainsNoMatchingElementError, modelURI.toPlatformString(true), fragment));
						throw new CoreException(status);
					}
					modelObjects.add(eObject);
					modelResources.add(eObject.eResource());
				} else {
					// URI refers to a model resource
					// Exclude Xtend files from being considered as workflow models
					if (!XtendUtil.isXtendResource(modelURI)) {
						Resource modelResource = EcoreResourceUtil.loadResource(resouceSet, modelURI, null);
						if (modelResource == null) {
							IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(),
									NLS.bind(Messages.modelResourceCouldNotBeLoadedError, modelURI.toPlatformString(true)));
							throw new CoreException(status);
						}
						modelObjects.addAll(modelResource.getContents());
						modelResources.add(modelResource);
					}
				}
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		return !modelObjects.isEmpty() ? modelObjects : null;
	}

	protected void saveModel(IProgressMonitor monitor) {
		final SubMonitor progress = SubMonitor.convert(monitor, modelResources.size());
		for (Resource modelResource : modelResources) {
			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(modelResource);
			if (SaveIndicatorUtil.isDirty(editingDomain, modelResource)) {
				EcorePlatformUtil.saveModel(modelResource, false, progress.newChild(1));
			} else {
				progress.worked(1);
			}
		}
	}
}