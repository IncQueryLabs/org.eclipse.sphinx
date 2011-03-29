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
package org.eclipse.sphinx.xpand.ui.wizards;

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
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.ui.wizards.AbstractWizard;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xpand.jobs.M2TJob;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xpand.ui.wizards.pages.CheckConfigurationPage;
import org.eclipse.sphinx.xpand.ui.wizards.pages.M2TConfigurationPage;
import org.eclipse.sphinx.xtend.check.jobs.CheckJob;
import org.eclipse.xtend.typesystem.MetaModel;

public class M2TConfigurationWizard extends AbstractWizard {

	protected EObject modelObject;
	protected MetaModel metaModel;

	private String m2tJobName;
	private IScopingResourceLoader scopingResourceLoader;
	private OutletsPreference outletsPreference;
	private ExtendedOutlet defaultOutlet;

	protected M2TConfigurationPage m2TConfigurationPage;
	protected CheckConfigurationPage checkConfigurationPage;

	public M2TConfigurationWizard(EObject modelObject, MetaModel metaModel) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		setWindowTitle(Messages.title_codeGen);
		this.modelObject = modelObject;
		this.metaModel = metaModel;
	}

	public String getM2TJobName() {
		return m2tJobName != null ? m2tJobName : getDefaultM2TJobName();
	}

	protected String getDefaultM2TJobName() {
		return Messages.job_generatingCode;
	}

	public void setM2TJobName(String m2tJobName) {
		this.m2tJobName = m2tJobName;
	}

	public IScopingResourceLoader getScopingResourceLoader() {
		return scopingResourceLoader;
	}

	public void setScopingResourceLoader(IScopingResourceLoader scopingResourceLoader) {
		this.scopingResourceLoader = scopingResourceLoader;
	}

	public OutletsPreference getOutletsPreference() {
		return outletsPreference;
	}

	public void setOutletsPreference(OutletsPreference outletsPreference) {
		this.outletsPreference = outletsPreference;
	}

	public ExtendedOutlet getDefaultOutlet() {
		return defaultOutlet;
	}

	public void setDefaultOutlet(ExtendedOutlet defaultOutlet) {
		this.defaultOutlet = defaultOutlet;
	}

	@Override
	public void addPages() {
		m2TConfigurationPage = createM2TConfigurationPage();
		addPage(m2TConfigurationPage);
		checkConfigurationPage = createCheckConfigurationPage();
		addPage(checkConfigurationPage);
	}

	protected M2TConfigurationPage createM2TConfigurationPage() {
		M2TConfigurationPage m2TPage = new M2TConfigurationPage(Messages.label_configPageName);
		m2TPage.init(modelObject, metaModel, getOutletsPreference(), getDefaultOutlet());
		return m2TPage;
	}

	protected CheckConfigurationPage createCheckConfigurationPage() {
		CheckConfigurationPage checkPage = new CheckConfigurationPage(Messages.label_configPageName);
		checkPage.init(modelObject, metaModel);
		return checkPage;
	}

	@Override
	protected void doPerformFinish(IProgressMonitor monitor) throws CoreException {
		Job job = new WorkspaceJob(getM2TJobName()) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				SubMonitor progress = SubMonitor.convert(monitor, 100);
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				try {
					// Run check
					CheckJob checkJob = createCheckJob();
					IStatus status = checkJob.runInWorkspace(progress.newChild(50));

					if (!status.isOK() || progress.isCanceled()) {
						throw new OperationCanceledException();
					}

					// Run Xpand
					M2TJob xpandJob = createM2TJob();
					return xpandJob.runInWorkspace(progress.newChild(50));
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

		ExtendedPlatformUI.showSystemConsole();
		m2TConfigurationPage.finish();
		checkConfigurationPage.finish();
	}

	protected M2TJob createM2TJob() {
		M2TJob job = new M2TJob(getM2TJobName(), metaModel, m2TConfigurationPage.getXpandEvaluationRequests());
		job.setScopingResourceLoader(getScopingResourceLoader());
		job.getOutlets().addAll(m2TConfigurationPage.getOutlets());
		job.setPriority(Job.BUILD);
		IFile file = EcorePlatformUtil.getFile(modelObject);
		if (file != null) {
			job.setRule(file.getProject());
		}
		return job;
	}

	protected CheckJob createCheckJob() {
		CheckJob checkJob = new CheckJob(getM2TJobName(), metaModel, checkConfigurationPage.getCheckEvaluationRequests());
		checkJob.setScopingResourceLoader(getScopingResourceLoader());
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
