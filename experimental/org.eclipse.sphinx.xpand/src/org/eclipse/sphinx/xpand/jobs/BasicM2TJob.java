/**
 * <copyright>
 * 
 * Copyright (c) See4sys and others.
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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.artop.ecl.emf.model.IModelDescriptor;
import org.artop.ecl.emf.model.ModelDescriptorRegistry;
import org.artop.ecl.emf.util.EcorePlatformUtil;
import org.artop.ecl.platform.util.StatusUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitorAdapter;
import org.eclipse.emf.mwe.core.resources.ResourceLoaderFactory;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.xpand.ExecutionContextRequest;
import org.eclipse.sphinx.xpand.internal.Activator;
import org.eclipse.xpand2.XpandExecutionContextImpl;
import org.eclipse.xpand2.XpandFacade;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.OutputImpl;
import org.eclipse.xtend.expression.ResourceManagerDefaultImpl;
import org.eclipse.xtend.expression.Variable;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

public class BasicM2TJob extends WorkspaceJob {

	protected static final Log log = LogFactory.getLog(BasicM2TJob.class);

	private MetaModel metaModel;
	private URI defaultOutletURI;
	private Collection<Outlet> outlets;
	protected IScopingResourceLoader scopingResourceLoader;
	protected Collection<ExecutionContextRequest> executionContextRequests;

	public BasicM2TJob(String name, Collection<ExecutionContextRequest> executionContextRequests) {
		super(name);
		this.executionContextRequests = executionContextRequests;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			Assert.isNotNull(executionContextRequests);
			Assert.isTrue(!executionContextRequests.isEmpty());

			// Log start of generation
			log.info("Generating code started..."); //$NON-NLS-1$

			// Set resource loader context to model behind current selection
			IFile file = EcorePlatformUtil.getFile(executionContextRequests.iterator().next().getTargetObject());
			IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(file);
			setResourceLoaderContext(model);

			// Configure outlets
			OutputImpl output = new OutputImpl();
			// We should add at least one default outlet
			if (!containsDefaultOutlet(getOutlets())) {
				Outlet defaultOutlet;
				if (getDefaultOutletURI() != null) {
					IPath defaultOutletPath = EcorePlatformUtil.createAbsoluteFileLocation(getDefaultOutletURI());
					defaultOutlet = new Outlet(defaultOutletPath.toFile().getAbsolutePath());
				} else {
					defaultOutlet = new Outlet();
				}
				getOutlets().add(defaultOutlet);
			}

			for (Outlet outlet : getOutlets()) {
				// TODO (aakar) add an overwrite column to the table viewer (to be set by the user)
				outlet.setOverwrite(true);
				output.addOutlet(outlet);
			}

			// Create execution context
			Map<String, Variable> globalVarsMap = new HashMap<String, Variable>();
			XpandExecutionContextImpl execCtx = new XpandExecutionContextImpl(new ResourceManagerDefaultImpl(), output, null, globalVarsMap,
					new ProgressMonitorAdapter(monitor), null, null, null);
			execCtx.registerMetaModel(getMetaModel());

			// Execute generation
			final XpandFacade facade = XpandFacade.create(execCtx);
			long startTime = System.currentTimeMillis();
			model.getEditingDomain().runExclusive(new Runnable() {
				public void run() {
					for (ExecutionContextRequest request : executionContextRequests) {
						log.info("Generating code for " + request.getTargetObject() + " with '" + request.getDefinitionName()); //$NON-NLS-1$ //$NON-NLS-2$ //);
						facade.evaluate(request.getDefinitionName(), request.getTargetObject(), request.getParameterList().toArray());
					}
				}
			});
			long duration = System.currentTimeMillis() - startTime;

			// Log end of generation
			for (Outlet outlet : getOutlets()) {
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

			// Refresh outlet container if its in the workspace
			IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(EcorePlatformUtil.createPath(getDefaultOutletURI()));
			if (container != null && container.exists()) {
				container.refreshLocal(IResource.DEPTH_INFINITE, monitor);
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

	public void setScopingResourceLoader(IScopingResourceLoader resourceLoader) {
		scopingResourceLoader = resourceLoader;
	}

	protected boolean containsDefaultOutlet(Collection<Outlet> outlets) {
		for (Outlet outlet : outlets) {
			if (outlet.getName() == null) {
				return true;
			}
		}
		return false;
	}

	public MetaModel getMetaModel() {
		if (metaModel == null) {
			metaModel = createMetaModel();
		}
		return metaModel;
	}

	public void setMetaModel(MetaModel metaModel) {
		this.metaModel = metaModel;
	}

	protected MetaModel createMetaModel() {
		return new EmfRegistryMetaModel();
	}

	protected URI getDefaultOutletURI() {
		return defaultOutletURI;
	}

	public void setDefaultOutletURI(URI defaultOutletURI) {
		this.defaultOutletURI = defaultOutletURI;
	}

	public Collection<Outlet> getOutlets() {
		if (outlets == null) {
			outlets = new ArrayList<Outlet>();
		}
		return outlets;
	}
}
