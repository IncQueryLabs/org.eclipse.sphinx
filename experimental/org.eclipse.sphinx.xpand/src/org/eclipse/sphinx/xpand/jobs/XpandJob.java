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
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
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

public class XpandJob extends WorkspaceJob {

	protected static final Log log = LogFactory.getLog(XpandJob.class);

	/**
	 * The metamodel to be use for code generation.
	 */
	protected MetaModel metaModel;

	/**
	 * A collection of Xpand evaluation request.
	 * 
	 * @see {@link XpandEvaluationRequest} class.
	 */
	protected Collection<XpandEvaluationRequest> xpandEvaluationRequests;

	/**
	 * The resource loader to be use when loading Xpand template file.
	 */
	private IScopingResourceLoader scopingResourceLoader;

	/**
	 * A collection of outlets to be use when code generation.
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
		this.metaModel = metaModel;
		this.xpandEvaluationRequests = xpandEvaluationRequests;
	}

	/**
	 * Sets the Xpand resource loader.
	 * 
	 * @param resourceLoader
	 *            the resource loader.
	 */
	public void setScopingResourceLoader(IScopingResourceLoader resourceLoader) {
		scopingResourceLoader = resourceLoader;
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
			Assert.isNotNull(metaModel);
			Assert.isNotNull(xpandEvaluationRequests);
			if (xpandEvaluationRequests.isEmpty()) {
				return Status.CANCEL_STATUS;
			}

			// Log start of generation
			log.info("Generating code started..."); //$NON-NLS-1$

			// Set resource loader context to model behind current selection
			IFile file = EcorePlatformUtil.getFile(xpandEvaluationRequests.iterator().next().getTargetObject());
			IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(file);
			setResourceLoaderContext(model);

			// Configure outlets
			OutputImpl output = new OutputImpl();

			// We should add at least one default outlet
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
			final XpandFacade facade = XpandFacade.create(execCtx);
			long startTime = System.currentTimeMillis();
			model.getEditingDomain().runExclusive(new Runnable() {
				public void run() {
					for (XpandEvaluationRequest request : xpandEvaluationRequests) {
						log.info("Generating code for " + request.getTargetObject() + " with '" + request.getDefinitionName()); //$NON-NLS-1$ //$NON-NLS-2$ //);
						facade.evaluate(request.getDefinitionName(), request.getTargetObject(), request.getParameterList().toArray());
					}
				}
			});
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
					container.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
			}
			return Status.OK_STATUS;
		} catch (OperationCanceledException exception) {
			return Status.CANCEL_STATUS;
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
		} finally {
			unsetResourceLoaderContext();
		}
	}

	/**
	 * Sets the resource loader context to the given <code>contextModel</code>.
	 */
	protected void setResourceLoaderContext(IModelDescriptor contextModel) {
		if (ResourceLoaderFactory.getCurrentThreadResourceLoader() instanceof IScopingResourceLoader) {
			scopingResourceLoader = (IScopingResourceLoader) ResourceLoaderFactory.getCurrentThreadResourceLoader();
		} else {
			ResourceLoaderFactory.setCurrentThreadResourceLoader(scopingResourceLoader);
		}
		scopingResourceLoader.setContextModel(contextModel);
	}

	/**
	 * Unsets the resource loader context.
	 */
	protected void unsetResourceLoaderContext() {
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
