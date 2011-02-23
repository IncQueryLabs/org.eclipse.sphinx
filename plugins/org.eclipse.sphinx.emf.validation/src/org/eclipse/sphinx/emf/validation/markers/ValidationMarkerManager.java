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
package org.eclipse.sphinx.emf.validation.markers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.resource.IXMLMarker;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.validation.Activator;
import org.eclipse.sphinx.emf.validation.diagnostic.ExtendedDiagnostic;
import org.eclipse.sphinx.emf.validation.markers.util.FeatureAttUtil;
import org.eclipse.sphinx.emf.validation.preferences.IValidationPreferences;
import org.eclipse.sphinx.emf.validation.stats.ValidationPerformanceStats;
import org.eclipse.sphinx.emf.validation.util.Messages;
import org.eclipse.sphinx.emf.validation.util.ValidationUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * This singleton class manage the validation marker
 */
public class ValidationMarkerManager {

	/**
	 * The singleton instance
	 */
	private static ValidationMarkerManager markerManager = null;

	/**
	 * The singleton accessor
	 * 
	 * @return singleton
	 */
	public static ValidationMarkerManager getInstance() {
		if (markerManager == null) {
			markerManager = new ValidationMarkerManager();
		}
		return markerManager;
	}

	protected ListenerList validationProblemMarkersChangeListenerList = new ListenerList();

	/**
	 * constructor
	 */
	private ValidationMarkerManager() {

	}

	/**
	 * Translate diagnostic in markers on the {@link Resource} resource
	 * 
	 * @param resource
	 *            the target resource
	 * @param diagnostic
	 *            the connected {@link Diagnostic}
	 * @throws CoreException
	 */
	public void addMarkers(Resource resource, Diagnostic diagnostic) throws CoreException {
		Assert.isNotNull(resource);
		Assert.isNotNull(diagnostic);

		List<IMarker> markerResultList = new ArrayList<IMarker>();

		IResource iResource = EcorePlatformUtil.getFile(resource);
		if (iResource == null || !iResource.exists()) {
			return;
		}

		// Let's check if the diagnostic data are ok
		List<?> diagnosticData = diagnostic.getData();
		if (diagnosticData == null || diagnosticData.size() == 0) {
			return;
		}

		// The framework is too slow when there are too many markers. Hence we limitate the number of
		// created markers
		int max_err = Platform.getPreferencesService().getInt(org.eclipse.sphinx.emf.validation.Activator.PLUGIN_ID,
				IValidationPreferences.PREF_MAX_NUMBER_OF_ERRORS, IValidationPreferences.PREF_MAX_NUMBER_OF_ERRORS_DEFAULT, null);
		IMarker[] markers = iResource.getWorkspace().getRoot()
				.findMarkers(IValidationMarker.MODEL_VALIDATION_PROBLEM, true, IResource.DEPTH_INFINITE);
		int nb_err = markers.length;

		// compute nb markers to be added
		int to_be_added = 0;
		for (Diagnostic childDiagnostic : diagnostic.getChildren()) {
			if (!childDiagnostic.getData().isEmpty()) {
				++to_be_added;
			}
		}

		// if we are about to overflow, remove oldest markers to ensure that some of the new markers will be added
		if (max_err > 0 && nb_err + to_be_added > max_err) {
			int nb_removed = 0;
			Arrays.sort(markers, new Comparator<IMarker>() {
				public int compare(IMarker o1, IMarker o2) {
					try {
						return Long.valueOf(o1.getCreationTime()).compareTo(Long.valueOf(o2.getCreationTime()));
					} catch (CoreException ex) {
						return 0;
					}
				}
			});
			// do not remove more than half of max_err or total number of err
			int remove_threshold = (nb_err > max_err ? nb_err : max_err) / 2;
			for (int i = 0; i < markers.length && nb_err + to_be_added > max_err; ++i) {
				markers[i].delete();
				--nb_err;
				++nb_removed;
				if (nb_removed >= remove_threshold) {
					break;
				}
			}
		}

		EObject validatedObject = (EObject) diagnosticData.get(0);

		try {
			// Add new markers
			int markerSeverity = 0;

			for (Diagnostic childDiagnostic : diagnostic.getChildren()) {
				if (max_err > 0 && nb_err > max_err) {
					break;
				}

				if (!childDiagnostic.getData().isEmpty()) {

					Map<String, Object> attributes = new HashMap<String, Object>();

					IMarker marker = iResource.createMarker(IValidationMarker.MODEL_VALIDATION_PROBLEM);
					++nb_err;

					EObject tgtObject = (EObject) childDiagnostic.getData().get(0);

					attributes.put(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(tgtObject).toString());

					int severity = childDiagnostic.getSeverity();
					if (severity < Diagnostic.WARNING) {
						markerSeverity = IMarker.SEVERITY_INFO;
					} else if (severity < Diagnostic.ERROR) {
						markerSeverity = IMarker.SEVERITY_WARNING;
					} else {
						markerSeverity = IMarker.SEVERITY_ERROR;
					}

					attributes.put(IMarker.SEVERITY, markerSeverity);

					String message = childDiagnostic.getMessage();
					if (message == null) {
						message = Messages.noMessageAvailableForThisMarker;
					}

					attributes.put(IMarker.MESSAGE, message);

					EObject eTgt = (EObject) childDiagnostic.getData().get(0);
					if (childDiagnostic instanceof ExtendedDiagnostic) {
						String ruleId = ((ExtendedDiagnostic) childDiagnostic).getConstraintId();
						// The features attribute.
						Set<String> features = FeatureAttUtil.getRulesFeaturesForEObj(ruleId, eTgt);
						String packedFeaturesStr = FeatureAttUtil.packFeaturesAsString(marker, features);

						attributes.put(IValidationMarker.FEATURES_ATTRIBUTE, packedFeaturesStr);
						// The rule id attribute.
						attributes.put(IValidationMarker.RULE_ID_ATTRIBUTE, ruleId);
					}

					// Let's set all attributes
					marker.setAttributes(attributes);

					// Finally notify IValidationProblemMarkerChangedListeners
					// SHOULD BE FULLY TESTED, LATER
					// fireValidationProblemMarkersChanged(tgtObject);

					markerResultList.add(marker);
				}
			}

			// Notify IValidationProblemMarkerChangedListeners
			fireValidationProblemMarkersChanged(validatedObject);
		} catch (CoreException ex) {
			PlatformLogUtil.logAsWarning(Activator.getDefault(),
					NLS.bind(Messages.warningProblemWithMarkerOperationOnResource, iResource.getLocationURI().toString()));
		}
	}

	/**
	 * Remove all markers of type {@link IValidationMarker.MODEL_VALIDATION_PROBLEM} present on the resource.
	 * 
	 * @param resource
	 *            the target {@link IResource}
	 * @see IMarker#getType()
	 */
	private void removeSphinxValidationProblemMarker(final IResource resource) throws CoreException {
		Assert.isNotNull(resource);
		resource.deleteMarkers(IValidationMarker.MODEL_VALIDATION_PROBLEM, true, IResource.DEPTH_INFINITE);
	}

	/**
	 * Remove all markers of type {@linkplain IXMLMarker.XML_INTEGRITY_PROBLEM} present on the resource.
	 * 
	 * @param resource
	 *            the target {@link IResource}
	 * @see IMarker#getType()
	 */
	private void removeXMLIntegrityProblemMarker(final IResource resource) throws CoreException {
		Assert.isNotNull(resource);
		resource.deleteMarkers(IXMLMarker.XML_INTEGRITY_PROBLEM, true, IResource.DEPTH_INFINITE);
	}

	/**
	 * Remove all markers of type {@linkplain IXMLMarker.XML_VALIDITY_PROBLEM} present on the resource.
	 * 
	 * @param resource
	 *            the target {@link IResource}
	 * @see IMarker#getType()
	 */
	private void removeXSDDiagnosticMarker(final IResource resource) throws CoreException {
		Assert.isNotNull(resource);
		resource.deleteMarkers(IXMLMarker.XML_VALIDITY_PROBLEM, true, IResource.DEPTH_INFINITE);
	}

	/**
	 * Remove all markers of type {@link IXMLMarker.XML_WELLFORMEDNESS_PROBLEM} present on the resource.
	 * 
	 * @param resource
	 *            the target {@link IResource}
	 * @see IMarker#getType()
	 */
	private void removeXMLWellFormednessProblemMarker(final IResource resource) throws CoreException {
		Assert.isNotNull(resource);
		resource.deleteMarkers(IXMLMarker.XML_WELLFORMEDNESS_PROBLEM, true, IResource.DEPTH_INFINITE);
	}

	/**
	 * remove all markers of Type {@link IValidationMarker#MODEL_VALIDATION_PROBLEM} directly attached to this eObject
	 * (depth set to IValidationMarkerManager.DEPTH_ZERO), to its direct children (depth set to
	 * IValidationMarkerManager.DEPTH_ONE), or to itself and all its children (depth set to
	 * IValidationMarkerManager.DEPTH_INFINITE)
	 * 
	 * @param eObject
	 *            the target eObject
	 * @param depth
	 *            see {@link IValidationMarkerManager}
	 * @throws CoreException
	 */
	public void removeMarkers(EObject eObject, int depth) throws CoreException {
		removeMarkers(eObject, depth, IValidationMarker.MODEL_VALIDATION_PROBLEM);
	}

	/**
	 * Remove all markers directly attached to this eObject (depth set to IValidationMarkerManager.DEPTH_ZERO), to its
	 * direct children (depth set to IValidationMarkerManager.DEPTH_ONE), or to itself and all its children (depth set
	 * to IValidationMarkerManager.DEPTH_INFINITE)
	 * 
	 * @param eObject
	 *            the target eObject
	 * @param depth
	 *            see {@link IValidationMarkerManager}
	 * @param markerType
	 *            type of marker to remove
	 * @throws CoreException
	 */
	public void removeMarkers(EObject eObject, int depth, String markerType) throws CoreException {

		IResource file = EcorePlatformUtil.getFile(eObject);

		// Let's test is the target eObject is a root object of the resource => we can use
		// a faster algorithm.
		Resource resource = eObject.eResource();
		if (resource != null && resource.getContents() != null && resource.getContents().size() >= 1) {
			EObject rootObject = resource.getContents().get(0);
			if (rootObject == eObject && depth == EObjectUtil.DEPTH_INFINITE) {
				removeSphinxValidationProblemMarker(file);
				removeXMLWellFormednessProblemMarker(file);
				removeXSDDiagnosticMarker(file);
				removeXMLIntegrityProblemMarker(file);

				// Notify IValidationProblemMarkerChangedListeners
				fireValidationProblemMarkersChanged(rootObject);
				return;
			}
		}

		// Let's get the model validation problem markers
		IMarker[] markers = getValidationMarkersList(eObject, depth, markerType);
		if (markers == null || markers.length == 0) {
			return;
		}

		for (IMarker marker : markers) {
			marker.delete();
		}

		// Notify IValidationProblemMarkerChangedListeners
		fireValidationProblemMarkersChanged(eObject);
	}

	/**
	 * Modify the URI of markers connected with the given {@link IResource} and its children
	 * 
	 * @param resource
	 *            the target {@link IResource}
	 * @param oldUri
	 *            the old URI
	 * @param newUri
	 *            the new URI
	 * @throws CoreException
	 */
	public void modifyMarkersURI(IResource resource, String oldUri, String newUri) throws CoreException {
		if (resource == null) {
			return;
		}

		String[] oldInfo = ValidationUtil.splitURI(oldUri);
		String[] newInfo = ValidationUtil.splitURI(newUri);
		if (oldInfo != null && oldInfo.length == 2 && newInfo != null && newInfo.length == 2) {

			IMarker[] markers = resource.findMarkers(IValidationMarker.MODEL_VALIDATION_PROBLEM, true, IResource.DEPTH_INFINITE);
			if (markers == null || markers.length == 0) {
				return;
			}

			String currentUri = null;

			try {
				for (IMarker marker : markers) {
					currentUri = marker.getAttribute(EValidator.URI_ATTRIBUTE, "--"); //$NON-NLS-1$
					if (currentUri.contains(oldInfo[0])) {
						String newURI = currentUri.replace(oldInfo[0], newInfo[0]);
						marker.setAttribute(EValidator.URI_ATTRIBUTE, newURI);
					}
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsWarning(Activator.getDefault(),
						NLS.bind(Messages.warningProblemWithMarkerOperationOnResource, resource.getLocationURI().toString()));
			}
		}
	}

	/**
	 * Update the uri attribute of the problem marker with the URI of the file
	 * 
	 * @param resource
	 *            an IResource
	 * @throws CoreException
	 */
	public void updateMarkersURI(IResource resource) throws CoreException {
		if (resource.isAccessible()) {
			ValidationPerformanceStats.INSTANCE.startNewEvent(ValidationPerformanceStats.ValidationEvent.EVENT_UPDATE_PROBLEM_MARKERS,
					resource.getFullPath());
			IMarker[] markers = resource.findMarkers(IValidationMarker.MODEL_VALIDATION_PROBLEM, true, IResource.DEPTH_INFINITE);

			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

			String currentUri = null;
			String fragment = null;

			String newURIPath = resource.getLocation().toString().replace(workspacePath, "platform:/resource"); //$NON-NLS-1$

			// final ISchedulingRule rule = workspace.getRuleFactory().markerRule(resource);
			final ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(resource);

			Workspace workspace = (Workspace) resource.getWorkspace();
			try {

				workspace.prepareOperation(rule, null);
				// ResourceInfo info = workspace.getResourceInfo(resource.getFullPath(), false, false);
				workspace.beginOperation(true);

				for (IMarker marker : markers) {
					currentUri = marker.getAttribute(EValidator.URI_ATTRIBUTE, "#"); //$NON-NLS-1$
					fragment = currentUri.substring(currentUri.lastIndexOf("#")); //$NON-NLS-1$
					marker.setAttribute(EValidator.URI_ATTRIBUTE, newURIPath + fragment);
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsWarning(Activator.getDefault(),
						NLS.bind(Messages.warningProblemWithMarkerOperationOnResource, resource.getLocationURI().toString()));
			}

			finally {
				try {
					workspace.endOperation(rule, false, null);
				} catch (CoreException ex) {
					PlatformLogUtil.logAsWarning(Activator.getDefault(), Messages.warningProblemWithWorkspaceOperation);
				}
			}
			ValidationPerformanceStats.INSTANCE.endEvent(ValidationPerformanceStats.ValidationEvent.EVENT_UPDATE_PROBLEM_MARKERS,
					resource.getFullPath());
		}
	}

	/**
	 * Return an array of {IMarker}. This one is composed with markers of Type
	 * {@link IValidationMarker#MODEL_VALIDATION_PROBLEM} directly attached to this eObject, from its direct children
	 * only, or itself and also to its children, according to the depth value (respectively
	 * IValidationMarkerManager.DEPTH_ZERO, IValidationMarkerManager.DEPTH_ONE and
	 * IValidationMarkerManager.DET_INFINITE.)
	 * 
	 * @param eObject
	 * @param depth
	 *            see {@link IValidationMarkerManager}
	 * @return an array of {@link IMarker}
	 * @throws CoreException
	 */
	public IMarker[] getValidationMarkersList(EObject eObject, int depth) throws CoreException {

		return getValidationMarkersList(eObject, depth, IValidationMarker.MODEL_VALIDATION_PROBLEM);
	}

	/**
	 * Return an array of {IMarker}. This one is composed with markers directly attached to this eObject, from its
	 * direct children only, or itself and also to its children, according to the depth value (respectively
	 * IValidationMarkerManager.DEPTH_ZERO, IValidationMarkerManager.DEPTH_ONE and
	 * IValidationMarkerManager.DET_INFINITE.)
	 * 
	 * @param eObject
	 * @param depth
	 *            see {@link IValidationMarkerManager}
	 * @param markerType
	 *            : type of marker
	 * @return an array of {@link IMarker}
	 * @throws CoreException
	 */
	public IMarker[] getValidationMarkersList(EObject eObject, int depth, String markerType) throws CoreException {

		IResource resource = EcorePlatformUtil.getFile(eObject);
		if (resource == null || !resource.exists()) {
			return new IMarker[0];
		}

		// All the Markers connected with this resource
		IMarker[] allMarkers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);

		// URI of eObj
		String[] tmp = ValidationUtil.splitURI(eObject);
		if (tmp.length < 2) {
			return new IMarker[0];
		}
		String eObjURI = tmp[0];
		String eObjType = tmp[1];

		String markerURI, markerEObjType;
		List<IMarker> result = new ArrayList<IMarker>();
		for (IMarker current : allMarkers) {
			if (current != null && current.exists()) {
				tmp = ValidationUtil.splitURI(((String) current.getAttribute(EValidator.URI_ATTRIBUTE)));
				if (tmp != null && tmp.length == 2) {
					markerURI = tmp[0];
					markerEObjType = tmp[1];

					switch (depth) {
					case EObjectUtil.DEPTH_ZERO:
						if (markerURI.equals(eObjURI) && eObjType.equals(markerEObjType)) {
							result.add(current);
						}
						break;
					case EObjectUtil.DEPTH_ONE: // same treatment, let's modify it later if necessary
						boolean shouldbeDeleted = true;
						shouldbeDeleted &= markerURI.contains(eObjURI);
						if (shouldbeDeleted) {
							String subPart = markerURI.substring(eObjURI.length());
							shouldbeDeleted &= subPart.indexOf('/') == subPart.lastIndexOf('/');
						}

						if (shouldbeDeleted) {
							result.add(current);
						}
						break;
					case EObjectUtil.DEPTH_INFINITE:
						if (markerURI.contains(eObjURI)) {
							result.add(current);
						}
						break;
					default:
						// Something is rotten in my kingdom, do nothing
						break;
					}
				}
			} else {
				String msg = NLS.bind(Messages.warningNoSuchMarker, (current == null ? "???" : current.getId())); //$NON-NLS-1$
				PlatformLogUtil.logAsWarning(Activator.getDefault(), msg);
			}
		}

		return result.toArray(new IMarker[result.size()]);

	}

	/**
	 * check if an array of IMarker contains a marker with the status IStatus.ERROR
	 * 
	 * @param markers
	 *            array of {@link IMarker}
	 * @return true if the status IMarker.SEVERITY_ERROR is found, false otherwise
	 */
	public boolean isError(IMarker[] markers) {
		return isCodePresent(IMarker.SEVERITY_ERROR, markers);
	}

	/**
	 * check if an array of IMarker contains a marker with the status IStatus.WARNING
	 * 
	 * @param markers
	 *            array of {@link IMarker}
	 * @return true if the status IMarker.SEVERITY_WARNING is found, false otherwise
	 */
	public boolean isWarning(IMarker[] markers) {
		return isCodePresent(IMarker.SEVERITY_WARNING, markers);
	}

	/**
	 * look for markers with severity code
	 * 
	 * @param status
	 * @see {@link org.eclipse.core.runtime.IStatus}
	 * @param markers
	 * @return true if the status is found, false otherwise
	 */
	public boolean isCodePresent(int status, IMarker[] markers) {
		boolean result = false;

		int i = -1;
		int len = markers.length;

		while (++i < len) {
			if (markers[i].getAttribute(IMarker.SEVERITY, -1) == status) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * Get the greater severity code from a set of {@link org.eclipse.core.resources.IMarker IMarkers}.
	 * 
	 * @param markers
	 *            An array of {@link org.eclipse.core.resources.IMarker IMarkers}.
	 * @return The greater severity code from the {@link org.eclipse.core.resources.IMarker IMarker} array
	 * @see org.eclipse.core.resources.IMarker
	 */
	public int getUpperSeverity(IMarker[] markers) {
		int currentSeverity = -1;

		int i = -1;
		int len = markers.length;

		while (++i < len) {
			if (markers[i].getAttribute(IMarker.SEVERITY, -1) > currentSeverity) {
				currentSeverity = markers[i].getAttribute(IMarker.SEVERITY, -1);
			}
			if (currentSeverity == IMarker.SEVERITY_ERROR) {
				break;
			}
		}

		return currentSeverity;
	}

	/**
	 * return the validation severity code of the {@link EObject} {@link EObject}.
	 * 
	 * @param eObject
	 *            the target {@link EObject}
	 * @param depth
	 *            of the search
	 * @return the Marker severity Status code associated to this {@link EObject}
	 * @throws CoreException
	 * @see {@link IMarker}
	 */
	public int getEObjectErrorStatus(EObject eObject, int depth) throws CoreException {

		IMarker[] markers = getValidationMarkersList(eObject, depth, IValidationMarker.MODEL_VALIDATION_PROBLEM);

		return getUpperSeverity(markers);
	}

	/**
	 * Handles the given diagnostic and update markers on the concerned {@link EObject} with infinite depth
	 * 
	 * @param diagnostic
	 *            The validation diagnostic to handle.
	 */
	public void handleDiagnostic(Diagnostic diagnostic) {
		handleDiagnostic(diagnostic, EObjectUtil.DEPTH_INFINITE);
	}

	/**
	 * Handles the given diagnostic and update markers on the concerned {@link EObject}.
	 * 
	 * @param diagnostic
	 *            The validation diagnostic to handle.
	 * @param depth
	 *            depth of the diagnostic, see {@link EObjectUtil}
	 */
	public void handleDiagnostic(Diagnostic diagnostic, int depth) {
		Assert.isNotNull(diagnostic);

		if (diagnostic.getData() == null) {
			return;
		}

		List<?> diagnosticData = diagnostic.getData();
		if (diagnosticData.isEmpty() || !(diagnosticData.get(0) instanceof EObject)) {
			return;
		}

		/* The top level object validated. */
		EObject eObject = (EObject) diagnosticData.get(0);

		if (diagnostic.getSeverity() == Diagnostic.OK) {
			// Everything is OK; no error nor warning raised during validation. Let's clean old markers if any.
			try {
				// Remove every existing markers.
				markerManager.removeMarkers(eObject, depth);
			} catch (CoreException ex) {
				PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
			}
		} else {
			/*
			 * At least one error or warning has been found during validation. Existing markers must be updated or
			 * removed and new ones must be created.
			 */
			Resource resource = eObject.eResource();
			if (resource != null) {
				try {
					// On a first hand, Let's remove every existing markers.
					markerManager.removeMarkers(eObject, depth);
					// Then, Let's add the new markers from the given diagnostic.
					markerManager.addMarkers(resource, diagnostic);

				} catch (CoreException ex) {
					PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
				}
			}
		}
	}

	public void addValidationProblemMarkersChangeListener(IValidationProblemMarkersChangeListener listener) {
		validationProblemMarkersChangeListenerList.add(listener);
	}

	public void removeValidationProblemMarkersChangeListener(IValidationProblemMarkersChangeListener listener) {
		validationProblemMarkersChangeListenerList.remove(listener);
	}

	protected void fireValidationProblemMarkersChanged(EObject object) {
		for (Object listener : validationProblemMarkersChangeListenerList.getListeners()) {
			if (listener instanceof IValidationProblemMarkersChangeListener) {
				((IValidationProblemMarkersChangeListener) listener).validationProblemMarkersChanged(new EventObject(object));
			}
		}
	}
}
