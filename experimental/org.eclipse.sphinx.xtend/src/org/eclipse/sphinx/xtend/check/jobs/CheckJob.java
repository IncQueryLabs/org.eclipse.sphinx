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
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
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

	protected MetaModel metaModel;

	protected Collection<CheckEvaluationRequest> checkEvaluationRequests;

	private IScopingResourceLoader scopingResourceLoader;

	public CheckJob(String name, MetaModel metaModel, CheckEvaluationRequest checkEvaluationRequest) {
		this(name, metaModel, Collections.singleton(checkEvaluationRequest));
	}

	public CheckJob(String name, MetaModel metaModel, Collection<CheckEvaluationRequest> checkEvaluationRequests) {
		super(name);
		this.metaModel = metaModel;
		this.checkEvaluationRequests = checkEvaluationRequests;
	}

	public void setScopingResourceLoader(IScopingResourceLoader resourceLoader) {
		scopingResourceLoader = resourceLoader;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			Assert.isNotNull(metaModel);

			// Log start of check model.
			log.info("Check model started..."); //$NON-NLS-1$

			// Set resource loader context to model behind current selection.
			IFile file = EcorePlatformUtil.getFile(checkEvaluationRequests.iterator().next().getModelRootObject());
			IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(file);
			setResourceLoaderContext(model);

			// Create execution context.
			Map<String, Variable> variables = new HashMap<String, Variable>();
			Map<String, Variable> globalVarsMap = new HashMap<String, Variable>();
			final ExecutionContextImpl execCtx = new ExecutionContextImpl(new ResourceManagerDefaultImpl(), null, new TypeSystemImpl(), variables,
					globalVarsMap, new ProgressMonitorAdapter(monitor), null, null, null, null, null, null, null);
			execCtx.registerMetaModel(metaModel);

			// Execute check model.
			long startTime = System.currentTimeMillis();
			final Issues issues = new IssuesImpl();
			model.getEditingDomain().runExclusive(new Runnable() {
				public void run() {
					for (CheckEvaluationRequest checkEvaluationRequest : checkEvaluationRequests) {
						for (IFile file : checkEvaluationRequest.getCheckFiles()) {
							String path = file.getProjectRelativePath().removeFileExtension().toString();
							log.info("Check model with '" + file.getFullPath().makeRelative() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //);
							CheckFacade.checkAll(path, checkEvaluationRequest.getModelObjects(), execCtx, issues, false);
						}
					}
				}
			});
			long duration = System.currentTimeMillis() - startTime;

			// If check file fails then returns error status.
			if (issues.hasErrors()) {
				// Log fail of check model.
				log.error("Check model failed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
				for (MWEDiagnostic error : issues.getErrors()) {
					log.error(error.getMessage());
				}
				return StatusUtil.createErrorStatus(Activator.getPlugin(), "Check model failed"); //$NON-NLS-1$
			}

			// Log end of check model.
			log.info("Check model completed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
			return Status.OK_STATUS;
		} catch (OperationCanceledException exception) {
			return Status.CANCEL_STATUS;
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
		} finally {
			unsetResourceLoaderContext();
		}
	}

	protected void setResourceLoaderContext(IModelDescriptor contextModel) {
		if (ResourceLoaderFactory.getCurrentThreadResourceLoader() instanceof IScopingResourceLoader) {
			scopingResourceLoader = (IScopingResourceLoader) ResourceLoaderFactory.getCurrentThreadResourceLoader();
		} else {
			ResourceLoaderFactory.setCurrentThreadResourceLoader(scopingResourceLoader);
		}
		scopingResourceLoader.setContextModel(contextModel);
	}

	protected void unsetResourceLoaderContext() {
		ResourceLoaderFactory.setCurrentThreadResourceLoader(null);
	}

}
