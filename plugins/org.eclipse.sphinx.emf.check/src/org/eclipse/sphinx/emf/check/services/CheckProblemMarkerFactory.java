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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sphinx.emf.check.DiagnosticLocation;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.validation.markers.IValidationMarker;
import org.eclipse.sphinx.emf.validation.util.Messages;

/**
 * Default check problem errors factory.
 */
public class CheckProblemMarkerFactory implements IProblemMarkerFactory {

	@Override
	public void createMarker(IResource resource, Diagnostic diagnostic) throws CoreException {
		createMarker(resource, diagnostic, IValidationMarker.MODEL_VALIDATION_PROBLEM);
	}

	@Override
	public void createMarker(IResource resource, Diagnostic diagnostic, String markerType) throws CoreException {
		IMarker marker = resource.createMarker(markerType);
		int markerSeverity;
		Map<String, Object> attributes = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<Object> data = (List<Object>) diagnostic.getData();
		DiagnosticLocation location = (DiagnosticLocation) data.get(0);
		EObject targetObject = location.getSource();
		attributes.put(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(targetObject).toString());
		attributes.put(IValidationMarker.HASH_ATTRIBUTE, targetObject.hashCode());
		int severity = diagnostic.getSeverity();
		if (severity < Diagnostic.WARNING) {
			markerSeverity = IMarker.SEVERITY_INFO;
		} else if (severity < Diagnostic.ERROR) {
			markerSeverity = IMarker.SEVERITY_WARNING;
		} else {
			markerSeverity = IMarker.SEVERITY_ERROR;
		}
		attributes.put(IMarker.SEVERITY, markerSeverity);
		String message = diagnostic.getMessage();
		if (message == null) {
			message = Messages.noMessageAvailableForThisMarker;
		}
		attributes.put(IMarker.MESSAGE, message);
		attributes.put(IMarker.SOURCE_ID, diagnostic.getSource());

		marker.setAttributes(attributes);
	}

	@Override
	public void deleteMarkers(Resource resource) throws CoreException {
		deleteMarkers(resource, IValidationMarker.MODEL_VALIDATION_PROBLEM);
	}

	@Override
	public void deleteMarkers(Resource resource, String markerType) throws CoreException {
		IResource file = EcorePlatformUtil.getFile(resource);
		if (file == null || !file.exists()) {
			return;
		}
		IMarker[] markers = file.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
		for (IMarker marker : markers) {
			marker.delete();
		}
	}
}
