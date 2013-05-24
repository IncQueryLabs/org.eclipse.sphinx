/**
 * <copyright>
 * 
 * Copyright (c) 2011-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [406564] BasicWorkspaceResourceLoader#getResource should not delegate to super
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.mwe.core.resources.ResourceLoader;
import org.eclipse.sphinx.emf.model.IModelDescriptor;

public interface IWorkspaceResourceLoader extends ResourceLoader {

	IProject getContextProject();

	void setContextProject(IProject contextProject);

	IModelDescriptor getContextModel();

	void setContextModel(IModelDescriptor contextModel);

	/**
	 * @deprecated Not supported any longer. BasicWorkspaceResourceLoader is supposed to load only resources from
	 *             workspace but not from JAR files or plug-ins.
	 */
	@Deprecated
	void setSearchArchives(boolean searchArchives);
}
