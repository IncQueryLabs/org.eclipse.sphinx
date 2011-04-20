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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.mwe.core.resources.ResourceLoaderFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.mwe.resources.IWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
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

	/**
	 * The metamodel to be used for model transformation.
	 */
	protected MetaModel metaModel;

	/**
	 * A collection of Xtend evaluation request.
	 * 
	 * @see {@link XtendEvaluationRequest} class.
	 */
	protected Collection<XtendEvaluationRequest> xtendEvaluationRequests;

	/**
	 * The resource loader to be used when loading Xpand template files.
	 */
	private IWorkspaceResourceLoader scopingResourceLoader;

	/**
	 * The label for the {@link IUndoableOperation operation} in which the Xtend transformation is executed.
	 */
	private String operationLabel = null;

	/**
	 * The Xtend transformation result.
	 */
	// TODO Rename to result
	protected Collection<Object> xtendResult = new ArrayList<Object>();

	/**
	 * Constructs an Xtend job for execution model transformation for the given <code>xtendEvaluationRequest</code>
	 * using the <code>metaModel</code> metamodel.
	 * 
	 * @param name
	 *            the name of the job.
	 * @param metaModel
	 *            the metamodel to be use.
	 * @param xtendEvaluationRequest
	 *            the Xtend evaluation request.
	 * @see {@link MetaModel} and {@link XtendEvaluationRequest} classes.
	 */
	public XtendJob(String name, MetaModel metaModel, XtendEvaluationRequest xtendEvaluationRequest) {
		this(name, metaModel, Collections.singleton(xtendEvaluationRequest));
	}

	/**
	 * Constructs an Xtend job for execution model transformation for the given <code>xtendEvaluationRequests</code>
	 * using the <code>metaModel</code> metamodel.
	 * 
	 * @param name
	 *            the name of the job.
	 * @param metaModel
	 *            the metamodel to be use.
	 * @param xtendEvaluationRequests
	 *            a collection of Xtend evaluation request.
	 */
	public XtendJob(String name, MetaModel metaModel, Collection<XtendEvaluationRequest> xtendEvaluationRequests) {
		super(name);

		Assert.isNotNull(metaModel);
		Assert.isNotNull(xtendEvaluationRequests);

		this.metaModel = metaModel;
		this.xtendEvaluationRequests = xtendEvaluationRequests;
	}

	/**
	 * Sets the {@link IWorkspaceResourceLoader resource loader} for resolving resources referenced by Xtend extensions.
	 * 
	 * @param resourceLoader
	 *            The resource loader to be used.
	 */
	public void setScopingResourceLoader(IWorkspaceResourceLoader resourceLoader) {
		scopingResourceLoader = resourceLoader;
	}

	protected Map<TransactionalEditingDomain, Collection<XtendEvaluationRequest>> getXtendEvaluationRequests() {
		Map<TransactionalEditingDomain, Collection<XtendEvaluationRequest>> requests = new HashMap<TransactionalEditingDomain, Collection<XtendEvaluationRequest>>();
		for (XtendEvaluationRequest request : xtendEvaluationRequests) {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(request.getTargetObject());
			Collection<XtendEvaluationRequest> requestsInEditingDomain = requests.get(editingDomain);
			if (requestsInEditingDomain == null) {
				requestsInEditingDomain = new HashSet<XtendEvaluationRequest>();
				requests.put(editingDomain, requestsInEditingDomain);
			}
			requestsInEditingDomain.add(request);
		}
		return requests;
	}

	/**
	 * Returns the label for the {@link IUndoableOperation operation} in which the Xtend transformation is executed.
	 * 
	 * @return The operation label for the Xtend transformation.
	 * @see #setOperationLabel(String)
	 */
	protected String getOperationLabel() {
		if (operationLabel == null) {
			// Retrieve operation label from job name
			operationLabel = getName();
		}
		return operationLabel;
	}

	/**
	 * Sets the label for the {@link IUndoableOperation operation} in which the Xtend transformation is executed.
	 * 
	 * @param operationLabel
	 *            The operation label for the Xtend transformation.
	 */
	public void setOperationLabel(String operationLabel) {
		this.operationLabel = operationLabel;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			if (xtendEvaluationRequests.isEmpty()) {
				return Status.CANCEL_STATUS;
			}

			// Log start of transformation
			log.info("Xtend started..."); //$NON-NLS-1$

			// Install resource loader
			installResourceLoader();

			// Create execution context
			final ExecutionContextImpl execCtx = new ExecutionContextImpl(new ResourceManagerDefaultImpl(), new TypeSystemImpl(), null);
			execCtx.registerMetaModel(metaModel);

			// Execute transformation
			long startTime = System.currentTimeMillis();
			final Map<TransactionalEditingDomain, Collection<XtendEvaluationRequest>> requests = getXtendEvaluationRequests();
			for (final TransactionalEditingDomain editingDomain : requests.keySet()) {

				Runnable runnable = new Runnable() {
					public void run() {
						for (XtendEvaluationRequest request : requests.get(editingDomain)) {
							log.info("Xtend transformation for " + request.getTargetObject() + " with '" + request.getExtensionName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

							// Update resource loader context
							updateResourceLoaderContext(request.getTargetObject());

							// Evaluate current request
							XtendFacade facade = XtendFacade.create(execCtx, getExtensionFileBaseName(request.getExtensionName()));
							List<Object> parameterList = new ArrayList<Object>();
							parameterList.add(request.getTargetObject());
							parameterList.addAll(request.getParameterList());
							Object result = facade.call(getFunctionName(request.getExtensionName()), parameterList);
							if (result != null) {
								xtendResult.add(result);
							}
						}
					}
				};

				if (editingDomain != null) {
					WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, getOperationLabel());
				} else {
					runnable.run();
				}
			}
			long duration = System.currentTimeMillis() - startTime;

			// Log end of transformation
			log.info("Xtend completed in " + duration + "ms!"); //$NON-NLS-1$ //$NON-NLS-2$
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
	 * Gets the extension file base name computing from the given <code>extensionName</code>. All "::" characters are
	 * replaced by "/".
	 */
	protected String getExtensionFileBaseName(String extensionName) {
		Assert.isNotNull(extensionName);

		int idx = extensionName.lastIndexOf(IXtendXpandConstants.NS_DELIMITER);
		if (idx != -1) {
			String extensionFileName = extensionName.substring(0, idx);
			return extensionFileName.replaceAll(IXtendXpandConstants.NS_DELIMITER, "/"); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Gets the function name computed from the given <code>extensionName</code>
	 */
	protected String getFunctionName(String extensionName) {
		Assert.isNotNull(extensionName);

		int idx = extensionName.lastIndexOf(IXtendXpandConstants.NS_DELIMITER);
		if (idx != -1 && extensionName.length() > idx + IXtendXpandConstants.NS_DELIMITER.length()) {
			return extensionName.substring(idx + IXtendXpandConstants.NS_DELIMITER.length(), extensionName.length());
		}
		return null;
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

	/**
	 * Gets the Xtend model transformation result.
	 */
	// TODO Rename to getResult()
	public Collection<Object> getXtendResult() {
		return xtendResult;
	}

	@Override
	public boolean belongsTo(Object family) {
		return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
	}
}
