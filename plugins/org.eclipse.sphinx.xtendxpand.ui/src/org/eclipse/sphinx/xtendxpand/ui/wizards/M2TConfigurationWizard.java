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
package org.eclipse.sphinx.xtendxpand.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.eclipse.sphinx.xtendxpand.jobs.CheckJob;
import org.eclipse.sphinx.xtendxpand.jobs.XpandJob;
import org.eclipse.sphinx.xtendxpand.outlet.ExtendedOutlet;
import org.eclipse.sphinx.xtendxpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xtendxpand.preferences.PrDefaultExcludesPreference;
import org.eclipse.sphinx.xtendxpand.preferences.PrExcludesPreference;
import org.eclipse.sphinx.xtendxpand.ui.internal.Activator;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xtendxpand.ui.wizards.pages.CheckConfigurationPage;
import org.eclipse.sphinx.xtendxpand.ui.wizards.pages.XpandConfigurationPage;
import org.eclipse.xtend.typesystem.MetaModel;

public class M2TConfigurationWizard extends AbstractWizard {

	protected EObject modelObject;
	protected Collection<MetaModel> metaModels;

	private String m2tJobName;
	private IWorkspaceResourceLoader workspaceResourceLoader;
	private OutletsPreference outletsPreference;
	private ExtendedOutlet defaultOutlet;

	protected XpandConfigurationPage xpandConfigurationPage;
	protected CheckConfigurationPage checkConfigurationPage;

	public M2TConfigurationWizard(EObject modelObject, Collection<MetaModel> metaModels) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		setWindowTitle(Messages.title_codeGen);
		this.modelObject = modelObject;
		this.metaModels = metaModels;
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

	public IWorkspaceResourceLoader getWorkspaceResourceLoader() {
		return workspaceResourceLoader;
	}

	public void setWorkspaceResourceLoader(IWorkspaceResourceLoader workspaceResourceLoader) {
		this.workspaceResourceLoader = workspaceResourceLoader;
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
		xpandConfigurationPage = createXpandConfigurationPage();
		addPage(xpandConfigurationPage);
		checkConfigurationPage = createCheckConfigurationPage();
		addPage(checkConfigurationPage);
	}

	protected XpandConfigurationPage createXpandConfigurationPage() {
		XpandConfigurationPage xpandPage = new XpandConfigurationPage(Messages.label_configPageName);
		xpandPage.init(modelObject, metaModels, getOutletsPreference(), getDefaultOutlet());
		return xpandPage;
	}

	protected CheckConfigurationPage createCheckConfigurationPage() {
		CheckConfigurationPage checkPage = new CheckConfigurationPage(Messages.label_configPageName);
		checkPage.init(modelObject);
		return checkPage;
	}

	@Override
	protected void doPerformFinish(IProgressMonitor monitor) throws CoreException {
		ExtendedPlatformUI.showSystemConsole();

		final CheckJob checkJob = isCheckRequired() ? createCheckJob() : null;
		final XpandJob xpandJob = createXpandJob();

		Job job = new WorkspaceJob(getM2TJobName()) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				SubMonitor progress = SubMonitor.convert(monitor, 100);
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				try {
					// Run check if required
					if (checkJob != null) {
						IStatus status = checkJob.run(progress.newChild(50));

						// Abort if check job ends with errors or is cancelled; continue when there are no errors or
						// only warnings
						if (status.getSeverity() != IStatus.ERROR || progress.isCanceled()) {
							throw new OperationCanceledException();
						}
					}

					// Run Xpand
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
	}

	protected boolean isCheckRequired() {
		return checkConfigurationPage.isCheckEnabled() && !checkConfigurationPage.getCheckEvaluationRequests().isEmpty();
	}

	protected XpandJob createXpandJob() {
		XpandJob job = new XpandJob(getM2TJobName(), metaModels, xpandConfigurationPage.getXpandEvaluationRequests());
		job.setWorkspaceResourceLoader(getWorkspaceResourceLoader());
		job.getOutlets().addAll(xpandConfigurationPage.getOutlets());
		job.setPriority(Job.BUILD);
		IFile file = EcorePlatformUtil.getFile(modelObject);
		if (file != null) {
			job.configureProtectedRegionResolver(getPrSrcPaths(xpandConfigurationPage.getOutlets()),
					PrDefaultExcludesPreference.INSTANCE.get(file.getProject()), PrExcludesPreference.INSTANCE.get(file.getProject()));
			job.setRule(file.getProject());
		}
		return job;
	}

	protected String getPrSrcPaths(Collection<? extends ExtendedOutlet> outlets) {
		// Use a set drop outlet pointing to same physical path
		Set<String> paths = new HashSet<String>();
		StringBuilder builder = new StringBuilder();
		List<ExtendedOutlet> allOutlets = new ArrayList<ExtendedOutlet>(outlets);
		for (ExtendedOutlet outlet : allOutlets) {
			if (outlet.isProtectedRegion()) {
				paths.add(outlet.getPath());
			}
		}
		for (Iterator<String> iter = paths.iterator(); iter.hasNext();) {
			builder.append(iter.next());
			if (iter.hasNext()) {
				builder.append(","); //$NON-NLS-1$
			}
		}
		return builder.toString();
	}

	protected CheckJob createCheckJob() {
		CheckJob checkJob = new CheckJob(getM2TJobName(), metaModels, checkConfigurationPage.getCheckEvaluationRequests());
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
