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
package org.eclipse.sphinx.emf.scoping;

import org.eclipse.core.resources.IResource;
import org.eclipse.sphinx.emf.internal.model.ModelDescriptorSynchronizer;
import org.eclipse.sphinx.emf.internal.model.ProjectScopeModelDescriptorSynchronizerDelegate;

public class ProjectResourceScopeProvider extends AbstractResourceScopeProvider {

	public ProjectResourceScopeProvider() {
		ModelDescriptorSynchronizer.INSTANCE.addDelegate(new ProjectScopeModelDescriptorSynchronizerDelegate());
	}

	/*
	 * @see org.eclipse.sphinx.emf.scoping.AbstractResourceScopeProvider#createScope(org.eclipse.core.resources.IResource)
	 */
	@Override
	protected ProjectResourceScope createScope(IResource resource) {
		return new ProjectResourceScope(resource);
	}
}
