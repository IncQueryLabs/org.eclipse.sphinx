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
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.serialization.generators.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.serialization.generators.persistencemapping.XMLPersistenceMappingGenerator;
import org.eclipse.sphinx.emf.serialization.generators.ui.internal.Activator;
import org.eclipse.sphinx.emf.serialization.generators.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.serialization.generators.xsd.Ecore2XSDGenerator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xsd.XSDPackage;

public abstract class AbstractGeneratorUIHandler extends AbstractHandler {

	protected String MODEL_FOLDER_NAME = Messages.folder_model;
	protected String NEW_ECORE_PATH = File.separator + MODEL_FOLDER_NAME + File.separator + EcorePackage.eNAME + File.separator;
	protected String NEW_XSD_PATH = File.separator + MODEL_FOLDER_NAME + File.separator + XSDPackage.eNAME + File.separator;

	/*
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		Object firstElement = selection.getFirstElement();

		if (firstElement instanceof IFile) {
			final IFile selectedFile = (IFile) firstElement;
			String fileExtension = selectedFile.getFileExtension();
			if (fileExtension != null && fileExtension.equals(EcorePackage.eNAME)) {

				// new schema file
				final File schemaFile = new File(selectedFile.getProject().getName() + File.separator
						+ selectedFile.getName().replace(EcorePackage.eNAME, XSDPackage.eNAME));
				URI modelURI = URI.createPlatformResourceURI(selectedFile.getFullPath().toString(), true);
				ResourceSet resourceSet = new ResourceSetImpl();
				Resource resource = resourceSet.getResource(modelURI, true);
				final EPackage ecoreModel = (EPackage) resource.getContents().get(0);
				final URI xsdFileURI = getXSDFileURI(selectedFile, schemaFile);
				// progress monitor dialog
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

				try {
					dialog.run(true, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							doRun(selectedFile, schemaFile, ecoreModel, xsdFileURI, monitor);
						}
					});
				} catch (InvocationTargetException e) {
					PlatformLogUtil.logAsError(Activator.getDefault(), e);
				} catch (InterruptedException e) {
					PlatformLogUtil.logAsError(Activator.getDefault(), e);
				}
			}
		} else {
			MessageDialog.openInformation(shell, Messages.info_title, Messages.info_selectEcoreFile);
		}
		return null;
	}

	// To be overridden by users
	protected abstract void doRun(IFile selectedFile, File schemaFile, EPackage ecoreModel, URI xsdFileURI, IProgressMonitor monitor);

	protected URI getXSDFileURI(IFile file, File schemaFile) {
		String filePathName = file.getProject().getName() + NEW_XSD_PATH;
		String xsdFilelocation = schemaFile.getName();
		URI xsdFileURI = null;
		if (xsdFilelocation != null) {
			xsdFileURI = URI.createURI(xsdFilelocation, true, URI.FRAGMENT_NONE);
		}
		URI directoryURI = URI.createPlatformResourceURI(filePathName, true);
		if (directoryURI != null) {
			xsdFileURI = xsdFileURI.resolve(directoryURI);
		}

		return xsdFileURI;
	}

	protected URI getPersistenceMappingEcoreLocationURI(IFile selectedFile) {
		String porjectPlatformPathName = selectedFile.getProject().getName();
		String filelocation = selectedFile.getName();

		URI fileLocationURI = null;
		if (filelocation != null) {
			fileLocationURI = URI.createURI(filelocation, true, URI.FRAGMENT_NONE);
		}
		URI directoryURI = URI.createPlatformResourceURI(porjectPlatformPathName + NEW_ECORE_PATH, true);
		if (directoryURI != null) {
			fileLocationURI = fileLocationURI.resolve(directoryURI);
		}

		return fileLocationURI;
	}

	// Execute XMLPersistenceMappingGenerator to XML Persistence Mapping Ecore model
	protected EPackage executeXMLPersistenceMappingGenerator(IFile selectedFile, EPackage ecoreModel, IProgressMonitor monitor) {
		// execute persistence mapping generator
		XMLPersistenceMappingGenerator xmlPersistenceMappingGenerator = new XMLPersistenceMappingGenerator(ecoreModel);
		EPackage persistenceMappingEcoreModel = (EPackage) xmlPersistenceMappingGenerator.execute(monitor);

		// calculate persistence mapping ecore file URI
		URI fileLocationURI = getPersistenceMappingEcoreLocationURI(selectedFile);

		// save persistence mapping ecore model
		try {
			saveModel(fileLocationURI, persistenceMappingEcoreModel);
		} catch (Exception e) {
			PlatformLogUtil.logAsError(Activator.getDefault(), e);
		}
		return persistenceMappingEcoreModel;
	}

	// Execute Ecore2XSDGenerator to generate XSD schema, where the input ecore must be an XML Persistence Mapping Ecore
	// model
	protected void executeXSDGenerator(File schemaFile, EPackage persistenceMappingEcoreModel, URI xsdFileURI, IProgressMonitor monitor) {
		Ecore2XSDGenerator ecore2XSDGenerator = new Ecore2XSDGenerator(xsdFileURI, schemaFile, persistenceMappingEcoreModel);
		ecore2XSDGenerator.run(monitor);
	}

	protected void saveModel(URI fileLocationURI, EObject rootObject) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(fileLocationURI);
		resource.getContents().add(rootObject);
		resource.save(null);
	}
}
