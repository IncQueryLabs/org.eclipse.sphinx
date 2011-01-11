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
package org.eclipse.sphinx.gmfgen.tasks;

import java.util.Collections;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gmf.codegen.gmfgen.GenDiagram;
import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;
import org.eclipse.gmf.internal.common.migrate.ModelLoadHelper;

@SuppressWarnings("restriction")
public abstract class GMFTask extends Task {

	private GenEditorGenerator genModel;

	private Diagnostic loadStatus;

	/*
	 * Initialized via Ant
	 */
	private String gmfgenPath;

	@Override
	public void execute() throws BuildException {
		initializeProcess();
		try {
			doExecute();
			getGenModel().eResource().save(Collections.EMPTY_MAP);
		} catch (Exception ex) {
			throw new BuildException(ex);
		} finally {
			unloadGenModel();
		}
	}

	protected abstract void doExecute();

	protected void initializeProcess() {
		System.out.println("Creating platform URI for path : " + gmfgenPath); //$NON-NLS-1$
		URI gmfGenModelURI = URI.createFileURI(gmfgenPath);
		System.out.println("Created URI : " + gmfGenModelURI); //$NON-NLS-1$

		System.out.println("Loading gen model"); //$NON-NLS-1$
		loadStatus = loadGenModel(gmfGenModelURI);
		System.out.println("Loading Status : " + loadStatus.getMessage()); //$NON-NLS-1$
	}

	public void setGmfgenPath(String gmfgenPath) {
		this.gmfgenPath = gmfgenPath;
	}

	protected Diagnostic getLoadStatus() {
		return loadStatus;
	}

	protected GenEditorGenerator getGenModel() {
		return genModel;
	}

	private Diagnostic loadGenModel(URI genModelURI) {
		ResourceSet srcResSet = new ResourceSetImpl();
		if (genModelURI != null && genModelURI.hasFragment()) {
			srcResSet.getEObject(genModelURI, true);
		}
		// srcResSet.getPackageRegistry().put(GMFGenPackage.eNS_URI, GMFGenPackage.eINSTANCE);
		srcResSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap());
		ModelLoadHelper loadHelper = new ModelLoadHelper(srcResSet, genModelURI);
		Object root = loadHelper.getContentsRoot();
		System.out.println("Loaded root : " + root); //$NON-NLS-1$
		if (root instanceof GenDiagram) {
			genModel = ((GenDiagram) root).getEditorGen();
		} else if (root instanceof GenEditorGenerator) {
			genModel = (GenEditorGenerator) root;
		}

		if (genModel != null && genModel.getDomainGenModel() != null) {
			genModel.getDomainGenModel().reconcile();
		}
		return loadHelper.getDiagnostics();
	}

	private void unloadGenModel() {
		if (genModel != null && genModel.eResource() != null) {
			genModel.eResource().unload();
		}
		genModel = null;
	}
}
