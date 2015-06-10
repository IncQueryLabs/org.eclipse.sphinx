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
 *     itemis - [468171] Model element splitting service
 *
 * </copyright>
 */
package org.eclipse.sphinx.platform.ui.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;
import org.eclipse.sphinx.platform.operations.WorkspaceOperationAdapter;
import org.eclipse.sphinx.platform.ui.internal.messages.Messages;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * An {@link IRunnableWithProgress} adapter that can be used to wrap {@link IWorkspaceOperation}s or
 * {@link IWorkspaceRunnable}s and run them as {@link IRunnableWithProgress} in an {@link IRunnableContext} that
 * provides the UI for the progress monitor and cancel button.
 * <p>
 * The {@link RunnableWithProgressAdapter} is in principal very similar to the {@link WorkspaceModifyOperation}. The
 * main difference is that the execution logic is provided by delegating to provided {@link IWorkspaceOperation} or
 * {@link IWorkspaceRunnable} instead of overriding the {@link WorkspaceModifyOperation#execute(IProgressMonitor)}
 * method and implementing it right there.
 * </p>
 *
 * @see IRunnableWithProgress
 * @see IRunnableContext
 * @see IWorkspaceOperation
 * @see IWorkspaceRunnable
 * @see WorkspaceModifyOperation
 */
public class RunnableWithProgressAdapter implements IRunnableWithProgress {

	private IWorkspaceOperation operation;

	public RunnableWithProgressAdapter(String label, ISchedulingRule rule, IWorkspaceRunnable runnable) {
		this(new WorkspaceOperationAdapter(label, rule, runnable));
	}

	public RunnableWithProgressAdapter(IWorkspaceOperation operation) {
		Assert.isNotNull(operation);
		this.operation = operation;
	}

	/*
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			ResourcesPlugin.getWorkspace().run(operation, operation.getRule(), IWorkspace.AVOID_UPDATE, monitor);
		} catch (CoreException ex) {
			throw new InvocationTargetException(ex, NLS.bind(Messages.error_whileRunningOperation, operation.getLabel()));
		} catch (OperationCanceledException ex) {
			throw new InterruptedException(ex.getMessage());
		}
	}
}
