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
package org.eclipse.sphinx.testutils.integration.internal;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.testutils.integration.IReferenceWorkspace;
import org.eclipse.sphinx.testutils.integration.ReferenceWorkspaceChangeListener;
import org.eclipse.sphinx.testutils.integration.ResourceProblemListener;

public interface IInternalReferenceWorkspace extends IReferenceWorkspace {

	Plugin getReferenceWorkspacePlugin();

	String getReferenceWorkspaceArchiveFileName();

	ReferenceProjectDescriptor getReferenceProjectDescriptor(String projectName);

	Set<ReferenceProjectDescriptor> getReferenceProjectDescriptors();

	ReferenceEditingDomainDescriptor getReferenceEditingDomainDescriptor(IMetaModelDescriptor metaModeldescriptor);

	Set<String> getReferenceFileNames(IMetaModelDescriptor metaModelDescriptor);

	Map<IMetaModelDescriptor, ReferenceEditingDomainDescriptor> getReferenceEditingDomainDescritpors();

	void addResourceSetProblemListener(ResourceProblemListener resourceProblemListener);

	void addReferenceWorkspaceChangeListener(ReferenceWorkspaceChangeListener referenceWorkspaceChangeListener);

	void removeResourceSetProblemListener(ResourceProblemListener resourceProblemListener);

	void removeReferenceWorkspaceChangeListener(ReferenceWorkspaceChangeListener referenceWorkspaceChangeListener);

}
