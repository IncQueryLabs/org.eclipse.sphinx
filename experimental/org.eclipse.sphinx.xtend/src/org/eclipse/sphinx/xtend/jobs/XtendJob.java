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
package org.eclipse.sphinx.xtend.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import org.eclipse.emf.mwe.core.resources.ResourceLoaderFactory;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xtend.XtendEvaluationRequest;
import org.eclipse.sphinx.xtend.internal.Activator;
import org.eclipse.xtend.XtendFacade;
import org.eclipse.xtend.expression.ExecutionContextImpl;
import org.eclipse.xtend.expression.ResourceManagerDefaultImpl;
import org.eclipse.xtend.expression.TypeSystemImpl;
import org.eclipse.xtend.typesystem.MetaModel;

public class XtendJob extends WorkspaceJob {

	protected static final Log log = LogFactory.getLog(XtendJob.class);

	protected MetaModel metaModel;

	protected Collection<XtendEvaluationRequest> xtendEvaluationRequests;

	protected Collection<Object> xtendResult = new ArrayList<Object>();

	private IScopingResourceLoader scopingResourceLoader;

	public XtendJob(String name, MetaModel metaModel, XtendEvaluationRequest xtendEvaluationRequests) {
		this(name, metaModel, Collections.singleton(xtendEvaluationRequests));
	}

	public XtendJob(String name, MetaModel metaModel, Collection<XtendEvaluationRequest> xtendEvaluationRequests) {
		super(name);
		this.metaModel = metaModel;
		this.xtendEvaluationRequests = xtendEvaluationRequests;
	}

	public void setScopingResourceLoader(IScopingResourceLoader resourceLoader) {
		scopingResourceLoader = resourceLoader;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			Assert.isNotNull(metaModel);

			// Log start of Xtend.
			log.info("Xtend started..."); //$NON-NLS-1$

			// Set resource loader context to model behind current selection.
			IFile file = EcorePlatformUtil.getFile(xtendEvaluationRequests.iterator().next().getTargetObject());
			IModelDescriptor model = ModelDescriptorRegistry.INSTANCE.getModel(file);
			setResourceLoaderContext(model);

			// Create execution context.
			final ExecutionContextImpl execCtx = new ExecutionContextImpl(new ResourceManagerDefaultImpl(), new TypeSystemImpl(), null);
			execCtx.registerMetaModel(metaModel);

			// Execute XTend.
			long startTime = System.currentTimeMillis();
			model.getEditingDomain().runExclusive(new Runnable() {
				public void run() {
					for (final XtendEvaluationRequest request : xtendEvaluationRequests) {
						log.info("Xtend transformation for " + request.getTargetObject() + " with '" + request.getExtensionName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						final XtendFacade facade = XtendFacade.create(execCtx, getExtensionFileBaseName(request.getExtensionName()));
						List<Object> parameterList = new ArrayList<Object>();
						parameterList.add(request.getTargetObject());
						parameterList.addAll(request.getParameterList());
						Object result = facade.call(getFunctionName(request.getExtensionName()), parameterList);
						if (result != null) {
							xtendResult.add(result);
						}
					}
				}
			});
			long duration = System.currentTimeMillis() - startTime;
			// Log end of Xtend.
			log.info("Xtend completed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
			return Status.OK_STATUS;
		} catch (OperationCanceledException exception) {
			return Status.CANCEL_STATUS;
		} catch (Exception ex) {
			return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
		} finally {
			unsetResourceLoaderContext();
		}
	}

	protected String getExtensionFileBaseName(String extensionName) {
		Assert.isNotNull(extensionName);

		int idx = extensionName.lastIndexOf(IXtendXpandConstants.NS_DELIMITER);
		if (idx != -1) {
			String extensionFileName = extensionName.substring(0, idx);
			return extensionFileName.replaceAll(IXtendXpandConstants.NS_DELIMITER, "/"); //$NON-NLS-1$
		}
		return null;
	}

	protected String getFunctionName(String extensionName) {
		Assert.isNotNull(extensionName);

		int idx = extensionName.lastIndexOf(IXtendXpandConstants.NS_DELIMITER);
		if (idx != -1 && extensionName.length() > idx + IXtendXpandConstants.NS_DELIMITER.length()) {
			return extensionName.substring(idx + IXtendXpandConstants.NS_DELIMITER.length(), extensionName.length());
		}
		return null;
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

	public Collection<Object> getXtendResult() {
		return xtendResult;
	}

}