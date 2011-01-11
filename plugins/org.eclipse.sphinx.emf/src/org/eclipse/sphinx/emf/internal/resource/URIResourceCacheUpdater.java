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
package org.eclipse.sphinx.emf.internal.resource;

import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;

public class URIResourceCacheUpdater extends ResourceSetListenerImpl {

	/**
	 * Constructs a {@link ResourceSetListener} that listens to changes of {@link Resource#getURI()}.
	 */
	public URIResourceCacheUpdater() {
		super(NotificationFilter.createFeatureFilter(EcorePackage.eINSTANCE.getEResource(), Resource.RESOURCE__URI));
	}

	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		for (Notification notification : event.getNotifications()) {
			handleModelResourceMoved((Resource) notification.getNotifier(), (URI) notification.getOldValue(), (URI) notification.getNewValue());
		}
	}

	private void handleModelResourceMoved(Resource resource, URI oldURI, URI newURI) {
		// Update the moved resource's URI in the ResourceSet's uriResourceMap
		ResourceSet resourceSet = resource.getResourceSet();
		if (resourceSet instanceof ResourceSetImpl) {
			Map<URI, Resource> uriResourceMap = ((ResourceSetImpl) resourceSet).getURIResourceMap();
			if (uriResourceMap != null && uriResourceMap.remove(oldURI) != null) {
				uriResourceMap.put(newURI, resource);
			}
		}
	}

	@Override
	public boolean isPostcommitOnly() {
		return true;
	}
}