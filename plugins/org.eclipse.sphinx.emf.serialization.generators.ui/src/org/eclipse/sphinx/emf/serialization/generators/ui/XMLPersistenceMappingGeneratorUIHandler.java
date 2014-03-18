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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.serialization.generators.ui.internal.messages.Messages;

/**
 * This UIHandler generates XML Persistence Mapping Ecore model from Ecore model
 */
public class XMLPersistenceMappingGeneratorUIHandler extends AbstractGeneratorUIHandler {

	@Override
	protected void doRun(IFile selectedFile, File schemaFile, EPackage ecoreModel, URI xsdFileURI, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_generatePersistenceMappingEcore, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// generate XML Persistence Mapping Ecore from a Ecore model
		executeXMLPersistenceMappingGenerator(selectedFile, ecoreModel, monitor);
	}
}
