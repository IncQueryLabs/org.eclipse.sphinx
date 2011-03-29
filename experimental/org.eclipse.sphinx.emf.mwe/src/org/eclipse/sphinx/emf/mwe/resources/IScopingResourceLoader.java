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
package org.eclipse.sphinx.emf.mwe.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.mwe.core.resources.ResourceLoader;
import org.eclipse.sphinx.emf.model.IModelDescriptor;

public interface IScopingResourceLoader extends ResourceLoader {

	IProject getContextProject();

	void setContextProject(IProject contextProject);

	IModelDescriptor getContextModel();

	void setContextModel(IModelDescriptor contextModel);

	void setSearchArchives(boolean searchArchives);

	String getQualifiedName(IFile underlyingFile, String statementName);

	IFile getUnderlyingFile(String qualifiedName);
}
