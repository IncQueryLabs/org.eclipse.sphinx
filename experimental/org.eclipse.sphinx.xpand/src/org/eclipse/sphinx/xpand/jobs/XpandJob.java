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
	 * The metamodel to be used for code generation.
	 */
	protected MetaModel metaModel;

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
	 * Constructs an Xpand job for execution code generation for the given <code>xpandEvaluationRequest</code> using the
	 * <code>metaModel</code> metamodel.
	 * 
	 * @param name
	 *            the name of the job.
	 * @param metaModel
	 *            the metamodel to be use.
	 * @param xpandEvaluationRequest
	 *            the Xpand evaluation request.
	 * @see {@link MetaModel} and {@link XpandEvaluationRequest} classes.
	 */
	public XpandJob(String name, MetaModel metaModel, XpandEvaluationRequest xpandEvaluationRequest) {
		this(name, metaModel, Collections.singleton(xpandEvaluationRequest));
	}

	/**
	 * Constructs an Xpand job for execution code generation for the given <code>xpandEvaluationRequests</code> using
	 * the <code>metaModel</code> metamodel.
	 * 
	 * @param name
	 *            the name of the job.
	 * @param metaModel
	 *            the metamodel to be use.
	 * @param xpandEvaluationRequests
	 *            a collection of Xpand evaluation request.
	 */
	public XpandJob(String name, MetaModel metaModel, Collection<XpandEvaluationRequest> xpandEvaluationRequests) {
		super(name);

		Assert.isNotNull(metaModel);
		Assert.isNotNull(xpandEvaluationRequests);

		this.metaModel = metaModel;
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
			execCtx.registerMetaModel(metaModel);

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
