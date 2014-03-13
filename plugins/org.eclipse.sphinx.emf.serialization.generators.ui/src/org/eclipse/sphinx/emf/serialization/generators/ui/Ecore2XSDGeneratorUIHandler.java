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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
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
import org.eclipse.sphinx.emf.serialization.generators.xsd.Ecore2XSDGenerator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xsd.XSDPackage;

public class Ecore2XSDGeneratorUIHandler extends AbstractHandler {

	public String MODEL_FOLDER_NAME = "model"; //$NON-NLS-1$
	public String NEW_ECORE_PATH = File.separator + MODEL_FOLDER_NAME + File.separator + EcorePackage.eNAME + File.separator;
	public String NEW_XSD_PATH = File.separator + MODEL_FOLDER_NAME + File.separator + EcorePackage.eNAME + File.separator;

	public EPackage executeXMLPersistenceMapping(IFile file, IProgressMonitor monitor) throws IOException {

		URI modelURI = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(modelURI, true);
		String projectPlatformPathName = file.getProject().getName();
		EPackage model = (EPackage) resource.getContents().get(0);

		// xtend persistence mapping generator
		XMLPersistenceMappingGenerator xmlPersistenceMappingGenerator = createXMLPersistenceMappingGenerator(model);
		EPackage ecoreRootModel = (EPackage) xmlPersistenceMappingGenerator.execute(monitor);

		String xsdlocation = file.getName();
		org.eclipse.emf.common.util.URI schemaLocationURI = null;
		if (xsdlocation != null) {
			schemaLocationURI = URI.createURI(xsdlocation, true, URI.FRAGMENT_NONE);
		}
		URI directoryURI = URI.createPlatformResourceURI(projectPlatformPathName + NEW_ECORE_PATH, true);
		if (directoryURI != null) {
			schemaLocationURI = schemaLocationURI.resolve(directoryURI);
		}
		saveModel(schemaLocationURI, ecoreRootModel);
		return ecoreRootModel;
	}

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

				// progress monitor dialog
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				try {
					dialog.run(true, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							monitor.beginTask("Generating XSD Schema ...", 100);
							monitor.subTask("Generating XSD Schema ...");

							SubMonitor progress = SubMonitor.convert(monitor, 100);
							if (progress.isCanceled()) {
								throw new OperationCanceledException();
							}

							// execute the task ...
							try {
								// generate XML persistence mapping ecore model
								EPackage ecoreModel = executeXMLPersistenceMapping(selectedFile, progress.newChild(40));
								URI xsdFileURI = getXSDFileURI(selectedFile, schemaFile);

								// xtend ecore to xsd generator
								Ecore2XSDGenerator ecore2XSDGenerator = createEcore2XSDGenerator(xsdFileURI, schemaFile, ecoreModel);
								ecore2XSDGenerator.run(progress.newChild(60));

							} catch (Exception e) {
								PlatformLogUtil.logAsError(Activator.getDefault(), e);
							}
						}
					});
				} catch (InvocationTargetException e) {
					PlatformLogUtil.logAsError(Activator.getDefault(), e);
				} catch (InterruptedException e) {
					PlatformLogUtil.logAsError(Activator.getDefault(), e);
				}
			}

		} else {
			MessageDialog.openInformation(shell, "Info", "Please select a genModel source file");
		}
		return null;
	}

	/**
	 * calculate XSD file URI
	 *
	 * @param selectedFile
	 * @param schemaFile
	 * @return
	 */
	private URI getXSDFileURI(IFile selectedFile, File schemaFile) {
		String xsdFilePathName = selectedFile.getProject().getName() + NEW_XSD_PATH;
		String xsdlocation = schemaFile.getName();
		URI xsdFileURI = null;
		if (xsdlocation != null) {
			xsdFileURI = URI.createURI(xsdlocation, true, URI.FRAGMENT_NONE);
		}
		URI directoryURI = URI.createPlatformResourceURI(xsdFilePathName, true);
		if (directoryURI != null) {
			xsdFileURI = xsdFileURI.resolve(directoryURI);
		}

		return xsdFileURI;
	}

	// to be overridden
	public XMLPersistenceMappingGenerator createXMLPersistenceMappingGenerator(EPackage rootEPackageModel) {
		return new XMLPersistenceMappingGenerator(rootEPackageModel);
	}

	// to be overridden
	public Ecore2XSDGenerator createEcore2XSDGenerator(URI xsdFileURI, File schemaFile, EPackage ecoreModel) {
		return new Ecore2XSDGenerator(xsdFileURI, schemaFile, ecoreModel);
	}

	public void saveModel(org.eclipse.emf.common.util.URI schemaLocationURI, EObject rootObject) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(schemaLocationURI);
		resource.getContents().add(rootObject);
		resource.save(null);
	}
}
