/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [475954] Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.incquery;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ScopingResourceSet;
import org.eclipse.sphinx.emf.workspace.incquery.internal.DelegatingScopingResourceSetImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public class WorkspaceIncQueryEngineHelper extends IncQueryEngineHelper implements IWorkspaceIncQueryEngineHelper {

	@Override
	public ViatraQueryEngine getEngine(Resource contextResource) throws ViatraQueryException {
		IModelDescriptor contextModelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(contextResource);
		if (contextModelDescriptor != null) {
			return getEngine(contextModelDescriptor);
		}
		return getEngine(contextResource, false);
	}

	@Override
	public ViatraQueryEngine getEngine(IModelDescriptor contextModelDescriptor) throws ViatraQueryException {
		if (contextModelDescriptor != null) {
			ResourceSet resourceSet = contextModelDescriptor.getEditingDomain().getResourceSet();
			if (resourceSet instanceof ScopingResourceSet) {
				DelegatingScopingResourceSetImpl delegatingResourceSet = new DelegatingScopingResourceSetImpl((ScopingResourceSet) resourceSet,
						contextModelDescriptor);
				return ViatraQueryEngine.on(new EMFScope(delegatingResourceSet));
			}
			return ViatraQueryEngine.on(new EMFScope(resourceSet));
		}
		return null;
	}
}
