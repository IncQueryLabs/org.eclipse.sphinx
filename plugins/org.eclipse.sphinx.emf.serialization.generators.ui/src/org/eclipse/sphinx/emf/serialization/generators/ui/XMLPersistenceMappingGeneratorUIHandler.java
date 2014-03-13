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
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class XMLPersistenceMappingGeneratorUIHandler extends AbstractHandler {

	public String MODEL_FOLDER_NAME = "model"; //$NON-NLS-1$
	public String NEW_ECORE_PATH = File.separator + MODEL_FOLDER_NAME + File.separator + EcorePackage.eNAME + File.separator;

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

				org.eclipse.emf.common.util.URI modelURI = org.eclipse.emf.common.util.URI.createPlatformResourceURI(selectedFile.getFullPath()
						.toString(), true);
				ResourceSet resourceSet = new ResourceSetImpl();
				Resource resource = resourceSet.getResource(modelURI, true);
				final EPackage model = (EPackage) resource.getContents().get(0);

				// progress monitor dialog
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				try {
					dialog.run(true, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							monitor.beginTask("Generating persistence mapping Ecore file ...", 100);
							monitor.subTask("Generating persistence mapping Ecore file ...");

							// execute the task ...
							try {
								XMLPersistenceMappingGenerator xmlPersistenceMappingGenerator = createXMLPersistenceMappingGenerator(model);
								EObject ecoreRootModel = xmlPersistenceMappingGenerator.execute(monitor);
								org.eclipse.emf.common.util.URI schemaLocationURI = getSchemaLocationURI(selectedFile);
								saveModel(schemaLocationURI, ecoreRootModel);
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
			MessageDialog.openInformation(shell, "Info", "Please select a Ecore source file");
		}
		return null;
	}

	private URI getSchemaLocationURI(IFile selectedFile) {
		String porjectPlatformPathName = selectedFile.getProject().getName();
		String xsdlocation = selectedFile.getName();

		org.eclipse.emf.common.util.URI schemaLocationURI = null;
		if (xsdlocation != null) {
			schemaLocationURI = URI.createURI(xsdlocation, true, URI.FRAGMENT_NONE);
		}
		org.eclipse.emf.common.util.URI directoryURI = URI.createPlatformResourceURI(porjectPlatformPathName + NEW_ECORE_PATH, true);
		if (directoryURI != null) {
			schemaLocationURI = schemaLocationURI.resolve(directoryURI);
		}

		return schemaLocationURI;
	}

	public void saveModel(org.eclipse.emf.common.util.URI schemaLocationURI, EObject rootObject) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(schemaLocationURI);
		resource.getContents().add(rootObject);
		resource.save(null);
	}

	public XMLPersistenceMappingGenerator createXMLPersistenceMappingGenerator(EPackage rootEPackageModel) {
		return new XMLPersistenceMappingGenerator(rootEPackageModel);
	}
}
