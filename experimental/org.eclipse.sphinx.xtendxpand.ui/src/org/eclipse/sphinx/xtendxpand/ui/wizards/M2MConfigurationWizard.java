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
package org.eclipse.sphinx.xtendxpand.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.mwe.resources.IWorkspaceResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.ui.wizards.AbstractWizard;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xtend.check.jobs.CheckJob;
import org.eclipse.sphinx.xtend.jobs.XtendJob;
import org.eclipse.sphinx.xtendxpand.ui.internal.Activator;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xtendxpand.ui.wizards.pages.CheckConfigurationPage;
import org.eclipse.sphinx.xtendxpand.ui.wizards.pages.XtendConfigurationPage;
import org.eclipse.xtend.typesystem.MetaModel;

public class M2MConfigurationWizard extends AbstractWizard {

	protected EObject modelObject;
	protected MetaModel metaModel;

	private String m2mJobName;
	private IWorkspaceResourceLoader workspaceResourceLoader;

	protected XtendConfigurationPage xtendConfigurationPage;
	protected CheckConfigurationPage checkConfigurationPage;

	public M2MConfigurationWizard(EObject modelObject, MetaModel metaModel) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		setWindowTitle(Messages.title_modelTransformation);
		this.modelObject = modelObject;
		this.metaModel = metaModel;
	}

	public String getM2MJobName() {
		return m2mJobName != null ? m2mJobName : getDefaultM2MJobName();
	}

	protected String getDefaultM2MJobName() {
		return Messages.job_modelTransformation;
	}

	public void setM2MJobName(String m2mJobName) {
		this.m2mJobName = m2mJobName;
	}

	public IWorkspaceResourceLoader getWorkspaceResourceLoader() {
		return workspaceResourceLoader;
	}

	public void setWorkspaceResourceLoader(IWorkspaceResourceLoader resourceLoader) {
		workspaceResourceLoader = resourceLoader;
	}

	@Override
	public void addPages() {
		xtendConfigurationPage = createXtendConfigurationPage();
		addPage(xtendConfigurationPage);
		checkConfigurationPage = createCheckConfigurationPage();
		addPage(checkConfigurationPage);
	}

	protected XtendConfigurationPage createXtendConfigurationPage() {
		XtendConfigurationPage xtendPage = new XtendConfigurationPage(Messages.label_xtendPageName);
		xtendPage.init(modelObject, metaModel);
		return xtendPage;
	}

	protected CheckConfigurationPage createCheckConfigurationPage() {
		CheckConfigurationPage checkPage = new CheckConfigurationPage(Messages.label_xtendPageName);
		checkPage.init(modelObject, metaModel);
		return checkPage;
	}

	@Override
	protected void doPerformFinish(IProgressMonitor monitor) throws CoreException {
		ExtendedPlatformUI.showSystemConsole();

		final CheckJob checkJob = isCheckRequired() ? createCheckJob() : null;
		final XtendJob xtendJob = createXtendJob();

		Job job = new WorkspaceJob(getM2MJobName()) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				SubMonitor progress = SubMonitor.convert(monitor, 100);
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				try {
					IStatus status;

					// Run check if required
					if (checkJob != null) {
						status = checkJob.runInWorkspace(progress.newChild(50));

						if (!status.isOK() || progress.isCanceled()) {
							throw new OperationCanceledException();
						}
					}

					// Run Xtend
					status = xtendJob.runInWorkspace(progress.newChild(50));

					if (!status.isOK() || progress.isCanceled()) {
						throw new OperationCanceledException();
					}

					// TODO Enable to provide JobChangeAdapter that retrieves and handles result from XtendJob; provide
					// default implementation that puts result objects on clipboard of shared editing domain for
					// metamodel behind result objects or saves them into a new file previously selected by the user
					xtendJob.getResultObjects();

					return status;
				} catch (OperationCanceledException ex) {
					return Status.CANCEL_STATUS;
				} catch (Exception ex) {
					return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
				}
			}

			@Override
			public boolean belongsTo(Object family) {
				return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
			}
		};
		job.setPriority(Job.BUILD);
		IFile file = EcorePlatformUtil.getFile(modelObject);
		if (file != null) {
			job.setRule(file.getProject());
		}
		job.schedule();
	}

	protected boolean isCheckRequired() {
		return checkConfigurationPage.isCheckEnabled() && !checkConfigurationPage.getCheckEvaluationRequests().isEmpty();
	}

	protected XtendJob createXtendJob() {
		XtendJob job = new XtendJob(getM2MJobName(), metaModel, xtendConfigurationPage.getXtendEvaluationRequests());
		job.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
		job.setPriority(Job.BUILD);
		IFile file = EcorePlatformUtil.getFile(modelObject);
		if (file != null) {
			job.setRule(file.getProject());
		}
		return job;
	}

	protected CheckJob createCheckJob() {
		CheckJob checkJob = new CheckJob(getM2MJobName(), metaModel, checkConfigurationPage.getCheckEvaluationRequests());
		checkJob.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
		checkJob.setPriority(Job.BUILD);
		IFile file = EcorePlatformUtil.getFile(modelObject);
		if (file != null) {
			checkJob.setRule(file.getProject());
		}
		return checkJob;
	}

	@Override
	protected void doPerformCancel(IProgressMonitor monitor) throws CoreException {
	}
}
