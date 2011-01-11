/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.platform.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sphinx.platform.ui.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * 
 */
public abstract class AbstractWizard extends Wizard implements IWizard {

	protected AbstractWizard() {
		setNeedsProgressMonitor(true);
	}

	@Override
	public final boolean performFinish() {
		return performExit(getFinishRunnable());
	}

	@Override
	public final boolean performCancel() {
		return performExit(getCancelRunnable());
	}

	private boolean performExit(IRunnableWithProgress operation) {
		try {
			/* Performs the execution of the runnable. */
			getContainer().run(false, true, operation);
		} catch (Exception e) {
			/* Handles rightly the exception that just occurred. */
			PlatformLogUtil.logAsError(Activator.getDefault(), e);
		}
		return true;
	}

	/**
	 * @return
	 */
	private final IRunnableWithProgress getFinishRunnable() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doPerformFinish(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
	}

	/**
	 * @return
	 */
	private final IRunnableWithProgress getCancelRunnable() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doPerformCancel(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
	}

	/**
	 * @param monitor
	 * @throws CoreException
	 */
	protected abstract void doPerformFinish(IProgressMonitor monitor) throws CoreException;

	/**
	 * @param monitor
	 * @throws CoreException
	 */
	protected abstract void doPerformCancel(IProgressMonitor monitor) throws CoreException;

}
