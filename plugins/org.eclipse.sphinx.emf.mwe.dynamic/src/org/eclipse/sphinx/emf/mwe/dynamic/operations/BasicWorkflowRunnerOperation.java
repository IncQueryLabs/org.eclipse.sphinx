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
 *     itemis - [463980] org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation.run(IProgressMonitor) should not be final
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic.operations;

import java.util.ArrayList;
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
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflow;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.emf.mwe2.runtime.workflow.Workflow;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowHandler;
import org.eclipse.sphinx.emf.mwe.dynamic.ModelWorkflowContext;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkflowContributorRegistry;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkflowHandlerRegistry;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkspaceWorkflow;
import org.eclipse.sphinx.emf.mwe.dynamic.components.IModelWorkflowComponent;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;
import org.eclipse.sphinx.emf.resource.ScopingResourceSetImpl;
import org.eclipse.sphinx.emf.saving.SaveIndicatorUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.jdt.loaders.ProjectClassLoader;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class BasicWorkflowRunnerOperation extends AbstractWorkspaceOperation implements IWorkflowRunnerOperation {

	private Object model;
	private Object workflow;
	private Workflow workflowInstance = null;

	private boolean autoSave = false;

	private List<URI> modelURIs = new ArrayList<URI>();
	private Set<IModelDescriptor> modelDescriptors = new HashSet<IModelDescriptor>();
	private Set<Resource> emfModelResources = new HashSet<Resource>();

	public BasicWorkflowRunnerOperation(String label, Object workflow) {
		super(label);
		this.workflow = workflow;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#getWorkflow()
	 */
	@Override
	public Object getWorkflow() {
		return workflow;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#getModelURIs()
	 */
	@Override
	public List<URI> getModelURIs() {
		return modelURIs;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#setAutoSave(boolean)
	 */
	@Override
	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#isAutoSave()
	 */
	@Override
	public boolean isAutoSave() {
		return autoSave;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation#getModel()
	 */
	@Override
	public Object getModel() {
		return model;
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
	 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(final IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		try {
			SubMonitor progress = SubMonitor.convert(monitor, 100);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			final Workflow workflowInstance = getWorkflowInstance();
			if (workflowInstance == null) {
				return;
			}

			// Load selected Sphinx/EMF model file (if any)
			// FIXME Don't pass model around - just let methods directly access the field
			model = loadModel(progress.newChild(5));

			final IWorkflowContext context = createWorkflowContext(model, progress.newChild(90));

			// Pre-run handers sorted by their priority
			List<IWorkflowHandler> sortedHandlers = WorkflowHandlerRegistry.INSTANCE.getSortedHandlers(workflowInstance.getClass().asSubclass(
					IWorkflow.class));
			for (IWorkflowHandler workflowHandler : sortedHandlers) {
				workflowHandler.preRun(workflowInstance, context);
			}

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					workflowInstance.run(context);
				}
			};

			// Workflow dealing with some model?
			TransactionalEditingDomain editingDomain = getEditingDomain(model);
			if (editingDomain != null && hasModelWorkflowComponents(workflowInstance)) {
				// Workflow intending to modify the model?
				if (isModifyingModel(workflowInstance)) {
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

			// Post-run handlers
			for (IWorkflowHandler workflowHandler : sortedHandlers) {
				workflowHandler.postRun(workflowInstance, context);
			}

			// Save model if needed
			if (isAutoSave()) {
				saveModel(progress.newChild(5));
			} else {
				progress.worked(5);
			}
		} catch (OperationCanceledException ex) {
			throw ex;
		} catch (CoreException ex) {
			throw ex;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}

	protected IWorkflowContext createWorkflowContext(Object model, IProgressMonitor monitor) {
		return new ModelWorkflowContext(model, monitor);
	}

	protected Workflow getWorkflowInstance() throws CoreException {
		try {
			if (workflowInstance == null) {
				Class<Workflow> workflowClass = null;

				IType workflowType = getWorkflowType();
				if (workflowType != null) {
					workflowClass = loadWorkflowClass(workflowType);
				} else {
					workflowClass = getWorkflowClass();
				}

				if (workflowClass != null) {
					workflowInstance = workflowClass.newInstance();
				}
			}
			return workflowInstance;
		} catch (CoreException ex) {
			throw ex;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
	}

	protected IType getWorkflowType() {
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

	protected Class<Workflow> getWorkflowClass() throws CoreException {
		if (workflow instanceof Class<?>) {
			Class<?> clazz = (Class<?>) workflow;
			if (!Workflow.class.isAssignableFrom(clazz)) {
				Exception ex = new IllegalStateException("Workflow class '" + clazz.getName() + "' is not a subclass of " + Workflow.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$
				IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
				throw new CoreException(status);
			}
			@SuppressWarnings("unchecked")
			Class<Workflow> workflowClass = (Class<Workflow>) clazz;
			return workflowClass;
		}
		return null;
	}

	protected Class<Workflow> loadWorkflowClass(IType workflowType) throws CoreException {
		Assert.isNotNull(workflowType);

		try {
			// Find out where given workflow type originates from
			if (!workflowType.isBinary()) {
				// Workflow type refers to an on-the-fly compiled Java class in the runtime workspace

				// Create project class loader capable of loading Java class behind workflow type from underlying Java
				// or plug-in project in runtime workspace
				ProjectClassLoader projectClassLoader = new ProjectClassLoader(workflowType.getJavaProject());

				// TODO Surround with appropriate tracing option
				// ClassLoaderExtensions.printHierarchy(projectClassLoader);

				// Use project class loader to load Java class behind workflow type
				Class<?> clazz = projectClassLoader.loadClass(workflowType.getFullyQualifiedName());
				if (!Workflow.class.isAssignableFrom(clazz)) {
					throw new IllegalStateException("Workflow class '" + clazz.getName() + "' is not a subclass of " + Workflow.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				@SuppressWarnings("unchecked")
				Class<Workflow> workflowClass = (Class<Workflow>) clazz;
				return workflowClass;
			} else {
				// Workflow type refers to a binary Java class from the running Eclipse instance

				// Load Java class behind workflow type from underlying contributor plug-in
				return WorkflowContributorRegistry.INSTANCE.loadContributedWorkflowClass(workflowType);
			}
		} catch (CoreException ex) {
			throw ex;
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
			throw new CoreException(status);
		}
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

	protected Object loadModel(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		if (modelURIs.isEmpty()) {
			return null;
		}

		final SubMonitor progress = SubMonitor.convert(monitor, modelURIs.size() * 2);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		List<EObject> modelObjects = new ArrayList<EObject>();

		// Loads Sphinx integrated models
		// FIXME Pass appropriate SubMonitor instance rather than monitor directly
		ModelLoadManager.INSTANCE.loadURIs(modelURIs, false, progress.newChild(modelURIs.size()));

		// FIXME Use a regular ResourceSetImpl rather than a ScopingResourceSetImpl
		// FIXME ResourceSet for regular EMF model files must not go out of scope and resources must be unloaded after
		// workflow execution; implement this ResourceSet as field and use it as replacement for emfModelResources
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
					IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(eObject.eResource());
					modelDescriptors.add(modelDescriptor);
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
						IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(modelResource);
						modelDescriptors.add(modelDescriptor);
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
					emfModelResources.add(eObject.eResource());
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
						emfModelResources.add(modelResource);
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
		final SubMonitor progress = SubMonitor.convert(monitor, modelDescriptors.size() + emfModelResources.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Save Sphinx integrated models
		for (IModelDescriptor modelDescriptor : modelDescriptors) {
			if (SaveIndicatorUtil.isDirty(modelDescriptor)) {
				EcorePlatformUtil.saveModel(modelDescriptor, false, progress.newChild(1));

				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}
			} else {
				progress.worked(1);
			}
		}

		// Save regular EMF models
		for (Resource modelResource : emfModelResources) {
			EcoreResourceUtil.saveModelResource(modelResource, null);
			progress.worked(1);

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}
}