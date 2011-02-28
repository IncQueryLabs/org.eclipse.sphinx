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
package org.eclipse.sphinx.emf.mwe.resources;

import org.artop.ecl.emf.model.IModelDescriptor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.mwe.core.resources.ResourceLoader;

public interface IScopingResourceLoader extends ResourceLoader {

	IProject getContextProject();

	void setContextProject(IProject contextProject);

	IModelDescriptor getContextModel();

	void setContextModel(IModelDescriptor contextModel);

	void setSearchArchives(boolean searchArchives);

	String getDefinitionName(IFile file, String defineBlockSegment);
}
