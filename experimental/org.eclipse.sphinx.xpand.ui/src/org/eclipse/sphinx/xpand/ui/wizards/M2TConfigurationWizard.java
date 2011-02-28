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

import java.util.Collection;
import java.util.Collections;

import org.artop.ecl.emf.util.EcorePlatformUtil;
import org.artop.ecl.platform.ui.util.ExtendedPlatformUI;
import org.artop.ecl.platform.ui.wizards.AbstractWizard;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.mwe.resources.IScopingResourceLoader;
import org.eclipse.sphinx.xpand.jobs.BasicM2TJob;
import org.eclipse.sphinx.xpand.ui.internal.Activator;
import org.eclipse.sphinx.xpand.ui.internal.messages.Messages;
import org.eclipse.sphinx.xpand.ui.wizards.pages.M2TConfigurationPage;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

public class M2TConfigurationWizard extends AbstractWizard {

	private MetaModel metaModel;
	protected EObject modelObject;
	protected URI defaultOutletURI;
	protected IScopingResourceLoader scopingResourceLoader;
	protected M2TConfigurationPage m2TConfigurationPage;

	public M2TConfigurationWizard(EObject modelObject, IScopingResourceLoader scopingResourceLoader, URI defaultOutletURI) {
		super();
		setDialogSettings(Activator.getDefault().getDialogSettings());
		setWindowTitle(Messages.title_codeGen);
		this.modelObject = modelObject;
		this.defaultOutletURI = defaultOutletURI;
		this.scopingResourceLoader = scopingResourceLoader;
	}

	@Override
	protected void doPerformFinish(IProgressMonitor monitor) throws CoreException {
		BasicM2TJob job = createM2TJob();
		ExtendedPlatformUI.showSystemConsole();
		job.schedule();
		m2TConfigurationPage.finish();
	}

	protected BasicM2TJob createM2TJob() {
		BasicM2TJob job = new BasicM2TJob(getM2TJobName(), m2TConfigurationPage.getExecutionContextRequests());
		job.setScopingResourceLoader(scopingResourceLoader);
		job.setDefaultOutletURI(defaultOutletURI);
		job.getOutlets().addAll(getOutlets());
		job.setMetaModel(getMetaModel());
		job.setPriority(Job.BUILD);
		job.setRule(EcorePlatformUtil.getFile(modelObject).getProject());
		return job;
	}

	@Override
	protected void doPerformCancel(IProgressMonitor monitor) throws CoreException {

	}

	protected String getM2TJobName() {
		return Messages.job_generatingCode;
	}

	/**
	 * This is a framework hook method for subclasses to add outlets to the {@link BasicM2TJob}. The default
	 * implementation of this framework method simply return an empty collection, so the {@link BasicM2TJob} will create
	 * a default {@link Outlet} using the {@link BasicM2TJob#DEFAULT_OUTLET_PATH}.
	 * 
	 * @return as specified above
	 * @see BasicM2TJob#getOutlets(Collection)
	 */
	protected Collection<Outlet> getOutlets() {
		return Collections.emptyList();
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
}
