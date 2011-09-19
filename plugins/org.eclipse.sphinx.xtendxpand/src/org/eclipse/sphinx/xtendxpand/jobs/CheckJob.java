/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [343844] Enable multiple Xtend MetaModels to be configured on BasicM2xAction, M2xConfigurationWizard, and Xtend/Xpand/CheckJob
 *     itemis - [357813] Risk of NullPointerException when transforming models using M2MConfigurationWizard
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xtendxpand.jobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.issues.IssuesImpl;
import org.eclipse.emf.mwe.core.issues.MWEDiagnostic;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitorAdapter;
import org.eclipse.emf.mwe.core.resources.ResourceLoaderFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.resources.IWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xtendxpand.CheckEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.XtendEvaluationRequest;
import org.eclipse.sphinx.xtendxpand.internal.Activator;
import org.eclipse.xtend.check.CheckFacade;
import org.eclipse.xtend.expression.ExecutionContextImpl;
import org.eclipse.xtend.expression.ResourceManagerDefaultImpl;
import org.eclipse.xtend.expression.TypeSystem;
import org.eclipse.xtend.expression.TypeSystemImpl;
import org.eclipse.xtend.expression.Variable;
import org.eclipse.xtend.typesystem.MetaModel;

public class CheckJob extends Job {

	protected static final Log log = LogFactory.getLog(CheckJob.class);

	/**
	 * The {@link MetaModel metamodel}s behind the model(s) to be checked.
	 * 
	 * @see MetaModel
	 */
	protected Collection<MetaModel> metaModels = null;

	/**
	 * The {@link TypeSystem} including the {@link MetaModel metamodel}s behind the model(s) to be transformed.
	 */
	protected TypeSystem typeSystem = null;

	/**
	 * A collection of Check evaluation request.
	 * 
	 * @see {@link CheckEvaluationRequest} class.
	 */
	protected Collection<CheckEvaluationRequest> checkEvaluationRequests;

	/**
	 * The resource loader to be used when loading check files.
	 */
	private IWorkspaceResourceLoader workspaceResourceLoader;

	/**
	 * Creates a {@link CheckJob} that validates a model based on given <code>metaModel</code> as specified by provided
	 * <code>checkEvaluationRequest</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModel
	 *            The {@link MetaModel metamodel} to be used.
	 * @param checkEvaluationRequest
	 *            The {@link CheckEvaluationRequest Check evaluation request} to be processed.
	 * @see MetaModel
	 * @see CheckEvaluationRequest
	 */
	public CheckJob(String name, MetaModel metaModel, CheckEvaluationRequest checkEvaluationRequest) {
		this(name, Collections.singleton(metaModel), Collections.singleton(checkEvaluationRequest));
	}

	/**
	 * Creates a {@link CheckJob} that validates one or several models based on given <code>metaModel</code> as
	 * specified by provided <code>checkEvaluationRequests</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModel
	 *            The {@link MetaModel metamodel} to be used.
	 * @param checkEvaluationRequests
	 *            The {@link CheckEvaluationRequest Check evaluation request}s to be processed.
	 * @see MetaModel
	 * @see CheckEvaluationRequest
	 */
	public CheckJob(String name, MetaModel metaModel, Collection<CheckEvaluationRequest> checkEvaluationRequests) {
		this(name, Collections.singleton(metaModel), checkEvaluationRequests);
	}

	/**
	 * Creates a {@link CheckJob} that validates a model based on given <code>metaModels</code> as specified by provided
	 * <code>checkEvaluationRequest</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModels
	 *            The {@link MetaModel metamodel}s to be used.
	 * @param checkEvaluationRequest
	 *            The {@link CheckEvaluationRequest Check evaluation request} to be processed.
	 * @see MetaModel
	 * @see CheckEvaluationRequest
	 */
	public CheckJob(String name, Collection<MetaModel> metaModels, CheckEvaluationRequest checkEvaluationRequest) {
		this(name, metaModels, Collections.singleton(checkEvaluationRequest));
	}

	/**
	 * Creates a {@link CheckJob} that validates one or several models based on given <code>metaModels</code> as
	 * specified by provided <code>checkEvaluationRequests</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModels
	 *            The {@link MetaModel metamodel}s to be used.
	 * @param checkEvaluationRequests
	 *            The {@link CheckEvaluationRequest Check evaluation request}s to be processed.
	 * @see MetaModel
	 * @see CheckEvaluationRequest
	 */
	public CheckJob(String name, Collection<MetaModel> metaModels, Collection<CheckEvaluationRequest> checkEvaluationRequests) {
		super(name);

		Assert.isNotNull(metaModels);
		Assert.isNotNull(checkEvaluationRequests);

		this.metaModels = metaModels;
		this.checkEvaluationRequests = checkEvaluationRequests;
	}

	/**
	 * Creates an {@link CheckJob} that validates a model based on given <code>typeSystem</code> as specified by
	 * provided <code>checkEvaluationRequest</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param typeSystem
	 *            The {@link TypeSystem type system} that includes the {@link MetaModel metamodel}s to be used.
	 * @param checkEvaluationRequest
	 *            The {@link CheckEvaluationRequest Check evaluation request} to be processed.
	 * @see TypeSystem
	 * @see XtendEvaluationRequest
	 */
	public CheckJob(String name, TypeSystem typeSystem, CheckEvaluationRequest checkEvaluationRequest) {
		this(name, typeSystem, Collections.singleton(checkEvaluationRequest));
	}

	/**
	 * Creates an {@link CheckJob} that validates one or several models based on given <code>typeSystem</code> as
	 * specified by provided <code>checkEvaluationRequests</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param typeSystem
	 *            The {@link TypeSystem type system} that includes the {@link MetaModel metamodel}s to be used.
	 * @param checkEvaluationRequests
	 *            The {@link CheckEvaluationRequest Check evaluation request}s to be processed.
	 * @see TypeSystem
	 * @see XtendEvaluationRequest
	 */
	public CheckJob(String name, TypeSystem typeSystem, Collection<CheckEvaluationRequest> checkEvaluationRequests) {
		super(name);

		Assert.isNotNull(typeSystem);
		Assert.isNotNull(checkEvaluationRequests);

		this.typeSystem = typeSystem;
		this.checkEvaluationRequests = checkEvaluationRequests;
	}

	protected Map<TransactionalEditingDomain, Collection<CheckEvaluationRequest>> getCheckEvaluationRequests() {
		Map<TransactionalEditingDomain, Collection<CheckEvaluationRequest>> requests = new HashMap<TransactionalEditingDomain, Collection<CheckEvaluationRequest>>();
		for (CheckEvaluationRequest request : checkEvaluationRequests) {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(request.getModelRootObject());
			Collection<CheckEvaluationRequest> requestsInEditingDomain = requests.get(editingDomain);
			if (requestsInEditingDomain == null) {
				requestsInEditingDomain = new HashSet<CheckEvaluationRequest>();
				requests.put(editingDomain, requestsInEditingDomain);
			}
			requestsInEditingDomain.add(request);
		}
		return requests;
	}

	/**
	 * Sets the {@link IWorkspaceResourceLoader resource loader} for resolving resources referenced by Check
	 * constraints.
	 * 
	 * @param resourceLoader
	 *            The resource loader to be used.
	 */
	public void setWorkspaceResourceLoader(IWorkspaceResourceLoader resourceLoader) {
		workspaceResourceLoader = resourceLoader;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			// Log start of validation
			log.info("Check model started..."); //$NON-NLS-1$

			// Install resource loader
			installResourceLoader();

			// Create execution context
			Map<String, Variable> variables = new HashMap<String, Variable>();
			Map<String, Variable> globalVarsMap = new HashMap<String, Variable>();
			final ExecutionContextImpl execCtx = new ExecutionContextImpl(new ResourceManagerDefaultImpl(), null,
					typeSystem instanceof TypeSystemImpl ? (TypeSystemImpl) typeSystem : new TypeSystemImpl(), variables, globalVarsMap,
					new ProgressMonitorAdapter(monitor), null, null, null, null, null, null, null);
			if (metaModels != null) {
				for (MetaModel metaModel : metaModels) {
					execCtx.registerMetaModel(metaModel);
				}
			}

			// Execute validation
			long startTime = System.currentTimeMillis();
			final Issues issues = new IssuesImpl();
			final Map<TransactionalEditingDomain, Collection<CheckEvaluationRequest>> requests = getCheckEvaluationRequests();
			for (final TransactionalEditingDomain editingDomain : requests.keySet()) {

				Runnable runnable = new Runnable() {
					public void run() {
						for (CheckEvaluationRequest request : requests.get(editingDomain)) {
							// Update resource loader context
							updateResourceLoaderContext(request.getModelRootObject());

							// Evaluate current request
							for (IFile file : request.getCheckFiles()) {
								log.info("Check model with '" + file.getFullPath().makeRelative() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //);

								String path = file.getProjectRelativePath().removeFileExtension().toString();
								CheckFacade.checkAll(path, request.getModelObjects(), execCtx, issues, false);
							}
						}
					}
				};

				if (editingDomain != null) {
					editingDomain.runExclusive(runnable);
				} else {
					runnable.run();
				}
			}
			long duration = System.currentTimeMillis() - startTime;

			// Log errors and warnings encountered during validation and return appropriate status
			if (issues.hasErrors()) {
				log.error("Check model failed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
				for (MWEDiagnostic error : issues.getErrors()) {
					log.error(error.getMessage());
				}
				return StatusUtil.createErrorStatus(Activator.getPlugin(), "Check model failed with errors"); //$NON-NLS-1$
			}
			if (issues.hasWarnings()) {
				for (MWEDiagnostic warning : issues.getWarnings()) {
					log.warn(warning.getMessage());
				}
				return StatusUtil.createWarningStatus(Activator.getPlugin(), "Check model failed with warnings"); //$NON-NLS-1$
			}

			log.info("Check model completed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
			return Status.OK_STATUS;
		} catch (OperationCanceledException exception) {
			return Status.CANCEL_STATUS;
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
		} finally {
			// Always uninstall resource loader again
			uninstallResourceLoader();
		}
	}

	/**
	 * Installs a {@link IWorkspaceResourceLoader resource loader}.
	 */
	protected void installResourceLoader() {
		if (workspaceResourceLoader == null) {
			if (ResourceLoaderFactory.getCurrentThreadResourceLoader() instanceof IWorkspaceResourceLoader) {
				workspaceResourceLoader = (IWorkspaceResourceLoader) ResourceLoaderFactory.getCurrentThreadResourceLoader();
			}
		} else {
			ResourceLoaderFactory.setCurrentThreadResourceLoader(workspaceResourceLoader);
		}
	}

	/**
	 * Updates context of current {@link IWorkspaceResourceLoader resource loader} according to given
	 * <code>contextObject</code>.
	 */
	protected void updateResourceLoaderContext(Object contextObject) {
		if (workspaceResourceLoader != null) {
			IFile contextFile = EcorePlatformUtil.getFile(contextObject);
			IModelDescriptor contextModel = ModelDescriptorRegistry.INSTANCE.getModel(contextFile);
			if (contextModel != null) {
				workspaceResourceLoader.setContextModel(contextModel);
			}
		}
	}

	/**
	 * Uninstalls current {@link IWorkspaceResourceLoader resource loader}.
	 */
	protected void uninstallResourceLoader() {
		ResourceLoaderFactory.setCurrentThreadResourceLoader(null);
	}

	@Override
	public boolean belongsTo(Object family) {
		return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
	}
}
