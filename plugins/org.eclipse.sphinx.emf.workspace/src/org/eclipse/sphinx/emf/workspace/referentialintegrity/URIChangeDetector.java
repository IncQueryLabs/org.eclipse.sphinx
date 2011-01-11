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
package org.eclipse.sphinx.emf.workspace.referentialintegrity;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.platform.resources.DefaultResourceChangeHandler;
import org.eclipse.sphinx.platform.resources.ResourceDeltaVisitor;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * URIChangeDetector is in charge of Handling URI Change Detection.It converts {@link IResourceChangeEvent} and
 * {@link ResourceSetChangeEvent} into {@link URIChangeEvent} and notifies registered {@link URIChangeListener}.see also
 * {@link URIChangeListenerRegistry}.
 */
public class URIChangeDetector extends ResourceSetListenerImpl implements IResourceChangeListener {

	/**
	 * Default constructor.
	 */
	public URIChangeDetector() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	protected void finalize() throws Throwable {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.finalize();
	}

	/*
	 * @see
	 * org.eclipse.emf.transaction.ResourceSetListener#resourceSetChanged(org.eclipse.emf.transaction.ResourceSetChangeEvent
	 * )
	 */
	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		List<?> notifications = event.getNotifications();
		for (Object o : notifications) {
			if (o instanceof Notification) {
				Notification notification = (Notification) o;
				if (notification.getNotifier() instanceof EObject) {
					EObject newEObject = (EObject) notification.getNotifier();
					Resource resource = newEObject.eResource();
					IURIChangeDetectorDelegate delegate = URIChangeDetectorDelegateRegistry.INSTANCE.getDetectorDelegate(resource);
					if (delegate != null) {
						List<URIChangeNotification> uriNotifications = delegate.detectChangedURIs(notification);
						if (!uriNotifications.isEmpty()) {
							fireURIChanged(createURIChangeEvent(resource, uriNotifications));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isPostcommitOnly() {
		return true;
	}

	/*
	 * @seeorg.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.
	 * IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				IResourceDeltaVisitor visitor = new ResourceDeltaVisitor(event.getType(), new DefaultResourceChangeHandler() {

					@Override
					public void handleFileMoved(int eventType, IFile oldFile, IFile newFile) {
						if (eventType == IResourceChangeEvent.POST_CHANGE) {
							Resource resource = EcorePlatformUtil.getResource(oldFile);
							if (resource != null) {
								IURIChangeDetectorDelegate delegate = URIChangeDetectorDelegateRegistry.INSTANCE.getDetectorDelegate(resource);
								if (delegate != null) {
									List<URIChangeNotification> notifications = delegate.detectChangedURIs(oldFile, newFile);
									if (!notifications.isEmpty()) {
										fireURIChanged(createURIChangeEvent(resource, notifications));
									}
								}
							}
						}
					}
				});
				delta.accept(visitor);
			}
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
	}

	// TODO integrate notification creation into Delegates
	protected URIChangeEvent createURIChangeEvent(Resource source, List<URIChangeNotification> notifications) {
		if (source != null && notifications != null && notifications.isEmpty()) {
			return null;
		}

		return new URIChangeEvent(source, notifications);
	}

	private void fireURIChanged(URIChangeEvent event) {
		if (event != null) {
			for (IURIChangeListener listener : URIChangeListenerRegistry.INSTANCE.getListeners()) {
				listener.uriChanged(event);
			}
		}
	}
}
