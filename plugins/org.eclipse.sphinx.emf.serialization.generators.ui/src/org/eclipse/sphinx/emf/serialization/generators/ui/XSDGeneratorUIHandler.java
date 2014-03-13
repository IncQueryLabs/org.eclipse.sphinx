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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.sphinx.emf.serialization.generators.ui.internal.Activator;
import org.eclipse.sphinx.emf.serialization.generators.xsd.Ecore2XSDGenerator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xsd.XSDPackage;

public class XSDGeneratorUIHandler extends AbstractHandler {

	public String MODEL_FOLDER_NAME = "model"; //$NON-NLS-1$
	public String NEW_XSD_PATH = File.separator + MODEL_FOLDER_NAME + File.separator + EcorePackage.eNAME + File.separator;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IFile) {

			IFile selectedFile = (IFile) firstElement;
			String fileExtension = selectedFile.getFileExtension();

			if (fileExtension != null && fileExtension.equals(EcorePackage.eNAME)) {
				File schemaFile = new File(selectedFile.getParent().getName() + File.separator
						+ selectedFile.getName().replace(EcorePackage.eNAME, XSDPackage.eNAME));

				URI genModelURI = URI.createPlatformResourceURI(selectedFile.getFullPath().toString(), true);
				ResourceSet resourceSet = new ResourceSetImpl();
				Resource resource = resourceSet.getResource(genModelURI, true);
				EPackage ecoreModel = (EPackage) resource.getContents().get(0);
				URI xsdFileURI = getXSDFileURI(selectedFile, schemaFile);

				// xtend ecore to xsd generator
				final Ecore2XSDGenerator ecore2XSDGenerator = createEcore2XSDGenerator(xsdFileURI, schemaFile, ecoreModel);

				// progress monitor dialog
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				try {
					dialog.run(true, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							monitor.beginTask("Generating XSD Schema ...", 100);
							monitor.subTask("Generating XSD Schema ...");

							// execute the task ...
							try {
								ecore2XSDGenerator.run(monitor);
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
			MessageDialog.openInformation(shell, "Info", "Please select a Eccore source file");
		}
		return null;
	}

	private URI getXSDFileURI(IFile file, File schemaFile) {
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

	// to be overridden
	public Ecore2XSDGenerator createEcore2XSDGenerator(URI xsdFileURI, File schemaFile, EPackage ecoreModel) {
		return new Ecore2XSDGenerator(xsdFileURI, schemaFile, ecoreModel);
	}
}
