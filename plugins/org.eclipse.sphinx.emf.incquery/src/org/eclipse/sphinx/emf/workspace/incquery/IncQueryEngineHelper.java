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

public class IncQueryEngineHelper implements IIncQueryEngineHelper {

	@Override
	public IncQueryEngine getEngine(EObject contextObject) throws IncQueryException {
		if (contextObject != null) {
			Resource eResource = contextObject.eResource();
			if (eResource != null) {
				return getEngine(eResource);
			}
			return IncQueryEngine.on(contextObject);
		}
		return null;
	}

	@Override
	public IncQueryEngine getEngine(Resource contextResource) throws IncQueryException {
		if (contextResource != null) {
			ResourceSet resourceSet = contextResource.getResourceSet();
			if (resourceSet != null) {
				return IncQueryEngine.on(resourceSet);
			}
			return IncQueryEngine.on(contextResource);
		}
		return null;
	}
}
