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
package org.eclipse.sphinx.testutils.integration;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;

public interface IReferenceWorkspace {

	IFile getReferenceFile(String projectName, String fileName);

	Set<IFile> getReferenceFiles(String projectName);

	List<String> getReferenceFileNames(String projectName);

	Set<IFile> getReferenceFiles(IMetaModelDescriptor metaModelDescriptor);

	Set<IFile> getReferenceFiles(String projectName, IMetaModelDescriptor metaModelDescriptor);

	List<String> getReferenceFileNames(String projectName, IMetaModelDescriptor metamodeldescriptor);

	Set<IFile> getAllReferenceFiles();

	IProject getReferenceProject(String projectName);

	int getInitialReferenceEditingDomainsCount();

	int getInitialResourcesInReferenceEditingDomainCount(IMetaModelDescriptor metaModeldescriptor);

	int getInitialResourcesInAllReferenceEditingDomainsCount();
}
