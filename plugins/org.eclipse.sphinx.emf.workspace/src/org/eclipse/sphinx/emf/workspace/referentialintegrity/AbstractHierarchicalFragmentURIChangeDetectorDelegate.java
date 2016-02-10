/**
 * <copyright>
 *
 * Copyright (c) 2016 itemis and others.
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
package org.eclipse.sphinx.emf.workspace.referentialintegrity;

import static org.eclipse.sphinx.emf.util.URIExtensions.replaceBaseURI;
import static org.eclipse.sphinx.emf.util.URIExtensions.replaceLastFragmentSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.internal.referentialintegrity.IntermittentRemoveTracker;

/**
 * An abstract {@link IURIChangeDetectorDelegate} implementation the detection of changes in URIs with
 * {@link URI#isHierarchical() hierarchical} {@link URI#fragment() fragments}.
 *
 * @see IURIChangeDetectorDelegate
 */
public abstract class AbstractHierarchicalFragmentURIChangeDetectorDelegate implements IURIChangeDetectorDelegate {

	// Used for tracking contents being removed and added back elsewhere later on
	protected final IntermittentRemoveTracker removedContentsTracker;

	public AbstractHierarchicalFragmentURIChangeDetectorDelegate() {
		removedContentsTracker = createIntermittentRemoveTracker();
	}

	protected IntermittentRemoveTracker createIntermittentRemoveTracker() {
		return new IntermittentRemoveTracker();
	}

	protected abstract boolean affectsURIFragmentSegmentOfChangedObject(Notification notification);

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.referencialintegrity.IURIChangeDetectorDelegate#detectChangedURIs(org.eclipse
	 * .emf .common.notify.Notification)
	 */
	@Override
	public List<URIChangeNotification> detectChangedURIs(Notification notification) {
		List<URIChangeNotification> notifications = new ArrayList<URIChangeNotification>();
		if (notification.getNotifier() instanceof EObject) {
			EObject eObject = (EObject) notification.getNotifier();
			EStructuralFeature feature = (EStructuralFeature) notification.getFeature();

			// Detect object URI changes due to modifications that affect the URIs of the modified object and its
			// contents
			if (affectsURIFragmentSegmentOfChangedObject(notification)) {
				URI newURI = EcoreResourceUtil.getURI(eObject);
				URI oldURI = replaceLastFragmentSegment(newURI, notification.getNewStringValue(), notification.getOldStringValue());
				if (oldURI != null && !oldURI.equals(newURI)) {
					addURIChangeNotification(notifications, eObject, oldURI, newURI);
				}
			}

			// Detect object URI changes due to contents being removed and added back elsewhere
			else if (feature instanceof EReference && ((EReference) feature).isContainment()) {
				removedContentsTracker.clearObsoleteEntries();

				if (Notification.REMOVE == notification.getEventType()) {
					URI containerURI = EcoreResourceUtil.getURI(eObject);
					handleRemovedContent(eObject, containerURI, feature, (EObject) notification.getOldValue());
				} else if (Notification.ADD == notification.getEventType()) {
					handleAddedContent(notifications, (EObject) notification.getNewValue());
				} else if (Notification.REMOVE_MANY == notification.getEventType()) {
					URI containerURI = EcoreResourceUtil.getURI(eObject);
					@SuppressWarnings("unchecked")
					List<EObject> oldValues = (List<EObject>) notification.getOldValue();
					for (EObject oldValue : oldValues) {
						handleRemovedContent(eObject, containerURI, feature, oldValue);
					}
				} else if (Notification.ADD_MANY == notification.getEventType()) {
					@SuppressWarnings("unchecked")
					List<EObject> newValues = (List<EObject>) notification.getNewValue();
					for (EObject newValue : newValues) {
						handleAddedContent(notifications, newValue);
					}
				}
			}
		}
		return notifications;
	}

	protected void handleRemovedContent(EObject oldContainer, URI oldContainerURI, EStructuralFeature oldFeature, EObject oldContent) {
		URI oldContentURI = null;

		// Old content just removed but not yet added back elsewhere?
		if (oldContent.eResource() == null) {
			// Restore old content URI using old container and old containing feature
			oldContentURI = EcoreResourceUtil.getURI(oldContainer, oldFeature, oldContent);
		} else {
			// Restore old content URI by replacing the URI of the new container at the beginning of the old
			// content's new URI with the URI of its old container
			URI newContentURI = EcoreResourceUtil.getURI(oldContent);
			URI newContainerURI = EcoreResourceUtil.getURI(oldContent.eContainer());
			oldContentURI = replaceBaseURI(newContentURI, newContainerURI, oldContainerURI);
		}

		// Keep track of the removed object and its old URI so that we can issue an appropriately initialized URI
		// change notification in case it will get or already has been added back somewhere else
		if (oldContentURI != null) {
			removedContentsTracker.put(oldContent, oldContentURI);
		}
	}

	protected void handleAddedContent(List<URIChangeNotification> notifications, EObject newContent) {
		URI oldContentURI = removedContentsTracker.get(newContent);
		if (oldContentURI != null) {
			URI newContentURI = EcoreResourceUtil.getURI(newContent);
			if (!oldContentURI.equals(newContentURI)) {
				addURIChangeNotification(notifications, newContent, oldContentURI, newContentURI);
			}
		}
	}

	protected void addURIChangeNotification(List<URIChangeNotification> notifications, EObject eObject, URI oldURI, URI newURI) {
		// Add URI change notification for given EObject
		notifications.add(new URIChangeNotification(eObject, oldURI));

		// Add URI change notifications for all EObjects that are directly or indirectly contained by given EObject and
		// have URIs that are affected by the change on given EObject
		TreeIterator<EObject> eAllContents = eObject.eAllContents();
		while (eAllContents.hasNext()) {
			EObject contentObject = eAllContents.next();
			URI newContentURI = EcoreResourceUtil.getURI(contentObject);
			URI oldContentURI = replaceBaseURI(newContentURI, newURI, oldURI);
			if (oldContentURI != null && !oldContentURI.equals(newContentURI)) {
				notifications.add(new URIChangeNotification(contentObject, oldContentURI));
			}
		}
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.referencialintegrity.IURIChangeDetectorDelegate#detectChangedURIs(org.eclipse
	 * .core .resources.IFile, org.eclipse.core.resources.IFile)
	 */
	@Override
	public List<URIChangeNotification> detectChangedURIs(IFile oldFile, IFile newFile) {
		return Collections.emptyList();
	}
}
