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
package org.eclipse.sphinx.emf.check.services;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;

public interface ICheckValidationProblemMarkerFactory {

	void createMarker(IResource resource, Diagnostic diagnostic) throws CoreException;

	void createMarker(IResource resource, Diagnostic diagnostic, String markerType) throws CoreException;

	void deleteMarkers(Resource resource) throws CoreException;

	void deleteMarkers(Resource resource, String markerType) throws CoreException;
}