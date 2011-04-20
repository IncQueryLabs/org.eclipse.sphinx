/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtend.check.jobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.sphinx.xtend.check.CheckEvaluationRequest;
import org.eclipse.sphinx.xtend.internal.Activator;
import org.eclipse.xtend.check.CheckFacade;
import org.eclipse.xtend.expression.ExecutionContextImpl;
import org.eclipse.xtend.expression.ResourceManagerDefaultImpl;
import org.eclipse.xtend.expression.TypeSystemImpl;
import org.eclipse.xtend.expression.Variable;
import org.eclipse.xtend.typesystem.MetaModel;

public class CheckJob extends WorkspaceJob {

	protected static final Log log = LogFactory.getLog(CheckJob.class);

	/**
	 * The metamodel to be used for checking model.
	 */
	protected MetaModel metaModel;

	/**
	 * A collection of Check evaluation request.
	 * 
	 * @see {@link CheckEvaluationRequest} class.
	 */
	protected Collection<CheckEvaluationRequest> checkEvaluationRequests;

	/**
	 * The resource loader to be used when loading check files.
	 */
	private IWorkspaceResourceLoader scopingResourceLoader;

	/**
	 * Constructs a Check job for checking model for the given <code>checkEvaluationRequest</code> using the
	 * <code>metaModel</code> metamodel.
	 * 
	 * @param name
	 *            the name of the job.
	 * @param metaModel
	 *            the metamodel to be use.
	 * @param checkEvaluationRequest
	 *            the check evaluation request to be use.
	 */
	public CheckJob(String name, MetaModel metaModel, CheckEvaluationRequest checkEvaluationRequest) {
		this(name, metaModel, Collections.singleton(checkEvaluationRequest));
	}

	/**
	 * Constructs a Check job for checking model for the given <code>checkEvaluationRequests</code> using the
	 * <code>metaModel</code> metamodel.
	 * 
	 * @param name
	 *            the name of the job.
	 * @param metaModel
	 *            the metamodel to be use.
	 * @param checkEvaluationRequests
	 *            a collection of check evaluation requests to be use.
	 */
	public CheckJob(String name, MetaModel metaModel, Collection<CheckEvaluationRequest> checkEvaluationRequests) {
		super(name);

		Assert.isNotNull(metaModel);
		Assert.isNotNull(checkEvaluationRequests);

		this.metaModel = metaModel;
		this.checkEvaluationRequests = checkEvaluationRequests;
	}

	/**
	 * Sets the {@link IWorkspaceResourceLoader resource loader} for resolving resources referenced by Check constraints.
	 * 
	 * @param resourceLoader
	 *            The resource loader to be used.
	 */
	public void setScopingResourceLoader(IWorkspaceResourceLoader resourceLoader) {
		scopingResourceLoader = resourceLoader;
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

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			// Log start of validation
			log.info("Check model started..."); //$NON-NLS-1$

			// Install resource loader
			installResourceLoader();

			// Create execution context
			Map<String, Variable> variables = new HashMap<String, Variable>();
			Map<String, Variable> globalVarsMap = new HashMap<String, Variable>();
			final ExecutionContextImpl execCtx = new ExecutionContextImpl(new ResourceManagerDefaultImpl(), null, new TypeSystemImpl(), variables,
					globalVarsMap, new ProgressMonitorAdapter(monitor), null, null, null, null, null, null, null);
			execCtx.registerMetaModel(metaModel);

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

			// Log end of validation and return appropriate status
			if (issues.hasErrors()) {
				log.error("Check model failed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
				for (MWEDiagnostic error : issues.getErrors()) {
					log.error(error.getMessage());
				}
				return StatusUtil.createErrorStatus(Activator.getPlugin(), "Check model failed"); //$NON-NLS-1$
			} else {
				log.info("Check model completed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
				return Status.OK_STATUS;
			}
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
		if (scopingResourceLoader == null) {
			if (ResourceLoaderFactory.getCurrentThreadResourceLoader() instanceof IWorkspaceResourceLoader) {
				scopingResourceLoader = (IWorkspaceResourceLoader) ResourceLoaderFactory.getCurrentThreadResourceLoader();
			}
		} else {
			ResourceLoaderFactory.setCurrentThreadResourceLoader(scopingResourceLoader);
		}
	}

	/**
	 * Updates context of current {@link IWorkspaceResourceLoader resource loader} according to given
	 * <code>contextObject</code>.
	 */
	protected void updateResourceLoaderContext(Object contextObject) {
		if (scopingResourceLoader != null) {
			IFile contextFile = EcorePlatformUtil.getFile(contextObject);
			IModelDescriptor contextModel = ModelDescriptorRegistry.INSTANCE.getModel(contextFile);
			if (contextModel != null) {
				scopingResourceLoader.setContextModel(contextModel);
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
