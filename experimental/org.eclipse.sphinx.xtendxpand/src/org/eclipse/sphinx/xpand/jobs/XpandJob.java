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
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xpand.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.sphinx.xpand.XpandEvaluationRequest;
import org.eclipse.sphinx.xpand.internal.Activator;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.xpand2.XpandExecutionContextImpl;
import org.eclipse.xpand2.XpandFacade;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.OutputImpl;
import org.eclipse.xtend.expression.ResourceManagerDefaultImpl;
import org.eclipse.xtend.expression.Variable;
import org.eclipse.xtend.typesystem.MetaModel;

// TODO Add support for advices
public class XpandJob extends WorkspaceJob {

	protected static final Log log = LogFactory.getLog(XpandJob.class);

	/**
	 * The {@link MetaModel metamodel}s behind the model(s) to be generated code from.
	 * 
	 * @see MetaModel
	 */
	protected Collection<MetaModel> metaModels;

	/**
	 * A collection of Xpand evaluation request.
	 * 
	 * @see {@link XpandEvaluationRequest} class.
	 */
	protected Collection<XpandEvaluationRequest> xpandEvaluationRequests;

	/**
	 * The resource loader to be used for loading Xtend/Xpand/Check files.
	 */
	private IWorkspaceResourceLoader workspaceResourceLoader = null;

	/**
	 * A collection of outlets to be used as target for code generation.
	 */
	private Collection<ExtendedOutlet> outlets;

	/**
	 * Creates an {@link XpandJob} that generates code from a model based on given <code>metaModel</code> as specified
	 * by provided <code>xpandEvaluationRequest</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModel
	 *            The {@link MetaModel metamodel} to be used.
	 * @param xpandEvaluationRequest
	 *            The {@link XpandEvaluationRequest Xpand evaluation request} to be processed.
	 * @see MetaModel
	 * @see XpandEvaluationRequest
	 */
	public XpandJob(String name, MetaModel metaModel, XpandEvaluationRequest xpandEvaluationRequest) {
		this(name, Collections.singleton(metaModel), Collections.singleton(xpandEvaluationRequest));
	}

	/**
	 * Creates an {@link XpandJob} that generates code from one or several models based on given <code>metaModel</code>
	 * as specified by provided <code>xpandEvaluationRequests</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModel
	 *            The {@link MetaModel metamodel} to be used.
	 * @param xpandEvaluationRequests
	 *            The {@link XpandEvaluationRequest Xpand evaluation request}s to be processed.
	 * @see MetaModel
	 * @see XpandEvaluationRequest
	 */
	public XpandJob(String name, MetaModel metaModel, Collection<XpandEvaluationRequest> xpandEvaluationRequests) {
		this(name, Collections.singleton(metaModel), xpandEvaluationRequests);
	}

	/**
	 * Creates an {@link XpandJob} that generates code from a model based on given <code>metaModels</code> as specified
	 * by provided <code>xpandEvaluationRequest</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModels
	 *            The {@link MetaModel metamodel}s to be used.
	 * @param xpandEvaluationRequest
	 *            The {@link XpandEvaluationRequest Xpand evaluation request} to be processed.
	 * @see MetaModel
	 * @see XpandEvaluationRequest
	 */
	public XpandJob(String name, Collection<MetaModel> metaModels, XpandEvaluationRequest xpandEvaluationRequest) {
		this(name, metaModels, Collections.singleton(xpandEvaluationRequest));
	}

	/**
	 * Creates an {@link XpandJob} that generates code from one or several models based on given <code>metaModels</code>
	 * as specified by provided <code>xpandEvaluationRequests</code>.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param metaModels
	 *            The {@link MetaModel metamodel}s to be used.
	 * @param xpandEvaluationRequests
	 *            The {@link XpandEvaluationRequest Xpand evaluation request}s to be processed.
	 * @see MetaModel
	 * @see XpandEvaluationRequest
	 */
	public XpandJob(String name, Collection<MetaModel> metaModels, Collection<XpandEvaluationRequest> xpandEvaluationRequests) {
		super(name);

		Assert.isNotNull(metaModels);
		Assert.isNotNull(xpandEvaluationRequests);

		this.metaModels = metaModels;
		this.xpandEvaluationRequests = xpandEvaluationRequests;
	}

	/**
	 * Sets the {@link IWorkspaceResourceLoader resource loader} for resolving resources referenced by Xpand templates.
	 * 
	 * @param resourceLoader
	 *            The resource loader to be used.
	 */
	public void setWorkspaceResourceLoader(IWorkspaceResourceLoader resourceLoader) {
		workspaceResourceLoader = resourceLoader;
	}

	protected Map<TransactionalEditingDomain, Collection<XpandEvaluationRequest>> getXpandEvaluationRequests() {
		Map<TransactionalEditingDomain, Collection<XpandEvaluationRequest>> requests = new HashMap<TransactionalEditingDomain, Collection<XpandEvaluationRequest>>();
		for (XpandEvaluationRequest request : xpandEvaluationRequests) {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(request.getTargetObject());
			Collection<XpandEvaluationRequest> requestsInEditingDomain = requests.get(editingDomain);
			if (requestsInEditingDomain == null) {
				requestsInEditingDomain = new HashSet<XpandEvaluationRequest>();
				requests.put(editingDomain, requestsInEditingDomain);
			}
			requestsInEditingDomain.add(request);
		}
		return requests;
	}

	/**
	 * Gets defined outlets.
	 */
	public Collection<ExtendedOutlet> getOutlets() {
		if (outlets == null) {
			outlets = new ArrayList<ExtendedOutlet>();
		}
		return outlets;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			if (xpandEvaluationRequests.isEmpty()) {
				return Status.CANCEL_STATUS;
			}

			// Log start of generation
			log.info("Generating code started..."); //$NON-NLS-1$

			// Install resource loader
			installResourceLoader();

			// Configure outlets
			OutputImpl output = new OutputImpl();

			// Add at least one default outlet
			if (!containsDefaultOutlet(getOutlets())) {
				getOutlets().add(createDefaultOutlet());
			}

			for (ExtendedOutlet outlet : getOutlets()) {
				outlet.setOverwrite(true);
				output.addOutlet(outlet);
			}

			// Create execution context
			Map<String, Variable> globalVarsMap = new HashMap<String, Variable>();
			XpandExecutionContextImpl execCtx = new XpandExecutionContextImpl(new ResourceManagerDefaultImpl(), output, null, globalVarsMap,
					new ProgressMonitorAdapter(monitor), null, null, null);
			for (MetaModel metaModel : metaModels) {
				execCtx.registerMetaModel(metaModel);
			}

			// Execute generation
			long startTime = System.currentTimeMillis();
			final XpandFacade facade = XpandFacade.create(execCtx);
			final Map<TransactionalEditingDomain, Collection<XpandEvaluationRequest>> requests = getXpandEvaluationRequests();
			for (final TransactionalEditingDomain editingDomain : requests.keySet()) {

				Runnable runnable = new Runnable() {
					public void run() {
						for (XpandEvaluationRequest request : requests.get(editingDomain)) {
							log.info("Generating code for " + request.getTargetObject() + " with '" + request.getDefinitionName()); //$NON-NLS-1$ //$NON-NLS-2$ //);

							// Update resource loader context
							updateResourceLoaderContext(request.getTargetObject());

							// Evaluate current request
							facade.evaluate(request.getDefinitionName(), request.getTargetObject(), request.getParameterList().toArray());
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

			// Log end of generation
			for (ExtendedOutlet outlet : getOutlets()) {
				String outletLabel = (outlet.getName() == null ? "[default]" : outlet.getName()) + "(" + outlet.getPath() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (outlet.getFilesWrittenAndClosed() > 0) {
					log.info("Written " + outlet.getFilesWrittenAndClosed() + " files to outlet " + outletLabel); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (outlet.getFilesCreated() > outlet.getFilesWrittenAndClosed()) {
					log.info("Skipped writing of " + (outlet.getFilesCreated() - outlet.getFilesWrittenAndClosed()) + " files to outlet " //$NON-NLS-1$ //$NON-NLS-2$
							+ outletLabel);
				}
			}
			log.info("Generation completed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$

			// Refresh outlet containers if they are in the workspace
			for (ExtendedOutlet outlet : getOutlets()) {
				IContainer container = outlet.getContainer();
				if (container != null) {
					container.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			}
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

	/**
	 * Returns true if default outlet is defined, else false.
	 */
	protected boolean containsDefaultOutlet(Collection<? extends Outlet> outlets) {
		for (Outlet outlet : outlets) {
			if (outlet.getName() == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a default outlet pointing at current working directory.
	 */
	protected ExtendedOutlet createDefaultOutlet() {
		return new ExtendedOutlet();
	}

	@Override
	public boolean belongsTo(Object family) {
		return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
	}
}
