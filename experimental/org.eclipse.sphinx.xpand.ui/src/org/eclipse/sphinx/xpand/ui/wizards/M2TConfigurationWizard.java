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
package org.eclipse.sphinx.xpand.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.artop.ecl.emf.util.EcorePlatformUtil;
import org.artop.ecl.platform.ui.util.ExtendedPlatformUI;
import org.artop.ecl.platform.ui.wizards.AbstractWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.xpand.jobs.BasicM2TJob;
import org.eclipse.sphinx.xpand.preferences.OutletsPreference;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xpand.ui.wizards.pages.M2TConfigurationPage;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

public class M2TConfigurationWizard extends AbstractWizard {

	protected EObject modelObject;
	protected IScopingResourceLoader scopingResourceLoader;
	protected URI defaultOutletURI;

	private MetaModel metaModel;
	private OutletsPreference outletsPreference;

	protected M2TConfigurationPage m2TConfigurationPage;

	public M2TConfigurationWizard(EObject modelObject, IScopingResourceLoader scopingResourceLoader, URI defaultOutletURI) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		setWindowTitle(Messages.title_codeGen);
		this.modelObject = modelObject;
		this.defaultOutletURI = defaultOutletURI;
		this.scopingResourceLoader = scopingResourceLoader;
	}

	public MetaModel getMetaModel() {
		if (metaModel == null) {
			metaModel = createMetaModel();
		}
		return metaModel;
	}

	protected MetaModel createMetaModel() {
		return new EmfRegistryMetaModel();
	}

	public void setMetaModel(MetaModel metaModel) {
		this.metaModel = metaModel;
	}

	public OutletsPreference getOutletsPreference() {
		return outletsPreference;
	}

	public void setOutletsPreference(OutletsPreference outletsPreference) {
		this.outletsPreference = outletsPreference;
	}

	@Override
	public void addPages() {
		m2TConfigurationPage = createM2TConfigurationPage();
		addPage(m2TConfigurationPage);
	}

	protected M2TConfigurationPage createM2TConfigurationPage() {
		M2TConfigurationPage m2TPage = new M2TConfigurationPage(Messages.label_configPageName);
		m2TPage.init(modelObject, getMetaModel(), defaultOutletURI);
		return m2TPage;
	}

	@Override
	protected void doPerformFinish(IProgressMonitor monitor) throws CoreException {
		BasicM2TJob job = createM2TJob();
		ExtendedPlatformUI.showSystemConsole();
		job.schedule();
		m2TConfigurationPage.finish();
	}

	protected BasicM2TJob createM2TJob() {
		BasicM2TJob job = new BasicM2TJob(getM2TJobName(), m2TConfigurationPage.getXpandEvaluationRequests());
		job.setScopingResourceLoader(scopingResourceLoader);
		job.setDefaultOutletURI(defaultOutletURI);
		job.getOutlets().addAll(getOutlets());
		job.setMetaModel(getMetaModel());
		job.setPriority(Job.BUILD);
		job.setRule(EcorePlatformUtil.getFile(modelObject).getProject());
		return job;
	}

	protected String getM2TJobName() {
		return Messages.job_generatingCode;
	}

	protected Collection<Outlet> getOutlets() {
		if (outletsPreference != null) {
			IFile file = EcorePlatformUtil.getFile(modelObject);
			if (file != null && file.getProject() != null) {
				return new ArrayList<Outlet>(outletsPreference.get(file.getProject()));
			}
		}
		return Collections.emptyList();
	}

	@Override
	protected void doPerformCancel(IProgressMonitor monitor) throws CoreException {
	}
}
