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
package org.eclipse.sphinx.emf.workspace.incquery;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;
import org.eclipse.sphinx.emf.workspace.incquery.internal.DelegatingScopingResourceSetImpl;

public class WorkspaceIncQueryEngineHelper extends IncQueryEngineHelper implements IWorkspaceIncQueryEngineHelper {

	@Override
	public IncQueryEngine getEngine(EObject contextObject) throws IncQueryException {
		if (contextObject != null) {
			return getEngine(contextObject.eResource());
		}
		return null;
	}

	@Override
	public IncQueryEngine getEngine(Resource contextResource) throws IncQueryException {
		if (contextResource != null) {
			return getEngine(ModelDescriptorRegistry.INSTANCE.getModel(contextResource));
		}
		return null;
	}

	@Override
	public IncQueryEngine getEngine(IModelDescriptor contextModelDescriptor) throws IncQueryException {
		if (contextModelDescriptor != null) {
			ResourceSet resourceSet = contextModelDescriptor.getEditingDomain().getResourceSet();
			if (resourceSet instanceof ScopingResourceSet) {
				DelegatingScopingResourceSetImpl delegatingResourceSet = new DelegatingScopingResourceSetImpl((ScopingResourceSet) resourceSet,
						contextModelDescriptor);
				return IncQueryEngine.on(delegatingResourceSet);
			}
			return IncQueryEngine.on(resourceSet);
		}
		return null;
	}
}
