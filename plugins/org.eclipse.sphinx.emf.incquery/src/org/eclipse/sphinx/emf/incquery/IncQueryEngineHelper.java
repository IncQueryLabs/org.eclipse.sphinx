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
package org.eclipse.sphinx.emf.incquery;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public class IncQueryEngineHelper implements IIncQueryEngineHelper {

	@Override
	public IncQueryEngine getEngine(EObject contextObject) throws IncQueryException {
		if (contextObject != null) {
			Resource contextResource = contextObject.eResource();
			if (contextResource != null) {
				return getEngine(contextResource);
			}
			EObject rootContainer = EcoreUtil.getRootContainer(contextObject);
			return IncQueryEngine.on(rootContainer);
		}
		return null;
	}

	@Override
	public IncQueryEngine getEngine(Resource contextResource) throws IncQueryException {
		return getEngine(contextResource, false);
	}

	@Override
	public IncQueryEngine getEngine(Resource resource, boolean strict) throws IncQueryException {
		if (resource != null) {
			ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet != null && !strict) {
				return IncQueryEngine.on(resourceSet);
			}
			return IncQueryEngine.on(resource);
		}
		return null;
	}

	@Override
	public IncQueryEngine getEngine(ResourceSet resourceSet) throws IncQueryException {
		if (resourceSet != null) {
			return IncQueryEngine.on(resourceSet);
		}
		return null;
	}
}
