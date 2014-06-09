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
package org.eclipse.sphinx.emf.internal.resource;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.resource.Resource;

public interface IResourceProblemMarkerFactory {

	void createProblemMarker(IResource resource, Resource.Diagnostic diagnostic, int severity, Map<Object, Object> problemHandlingOptions);

	void createProblemMarker(IResource resource, Exception exception, int severity);

	void deleteMarkers(IResource resource);

}
