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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * 
 */
public class XMIURIChangeDetectorDelegate implements IURIChangeDetectorDelegate {

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.referencialintegrity.IURIChangeDetectorDelegate#detectChangedURIs(org.eclipse.emf
	 * .common.notify.Notification)
	 */
	public List<URIChangeNotification> detectChangedURIs(Notification notification) {
		notification.getNotifier();
		List<URIChangeNotification> notifications = new ArrayList<URIChangeNotification>();

		if (notification.getNotifier() instanceof EObject) {
			EObject eObject = (EObject) notification.getNotifier();

			Class<? extends Object> eOBjectClass = notification.getNotifier().getClass();
			try {
				Object newInstance = eOBjectClass.newInstance();

			} catch (InstantiationException ex) {
				// TODO Auto-generated catch block
			} catch (IllegalAccessException ex) {
				// TODO Auto-generated catch block
			}

			eObject.eResource().getURIFragment(eObject);
			URIChangeNotification uriNotification = new URIChangeNotification(eObject, getURI(eObject));
			notifications.add(uriNotification);
		}

		return notifications;
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.referencialintegrity.IURIChangeDetectorDelegate#detectChangedURIs(org.eclipse.core
	 * .resources.IFile, org.eclipse.core.resources.IFile)
	 */
	public List<URIChangeNotification> detectChangedURIs(IFile oldFile, IFile newFile) {
		List<URIChangeNotification> notifications = new ArrayList<URIChangeNotification>();
		URI oldUri = EcorePlatformUtil.createURI(oldFile.getFullPath());
		URI newUri = EcorePlatformUtil.createURI(newFile.getFullPath());
		if (!oldUri.equals(newUri)) {
			EObject modelRoot = EcorePlatformUtil.getModelRoot(oldFile);
			if (modelRoot != null) {
				TreeIterator<EObject> eAllContents = modelRoot.eAllContents();
				notifications.add(new URIChangeNotification(modelRoot, getURI(modelRoot)));
				while (eAllContents.hasNext()) {
					EObject containedEObject = eAllContents.next();
					notifications.add(new URIChangeNotification(containedEObject, getURI(containedEObject)));
				}
			}
		}
		return notifications;
	}

	protected static URI getURI(final EObject eObject) {
		if (eObject != null) {
			final TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(eObject);
			if (editingDomain != null) {
				try {
					return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<URI>() {
						public void run() {
							Resource resource = eObject.eResource();
							ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource);
							if (extendedResource != null) {
								setResult(extendedResource.createProxyURI((InternalEObject) eObject));
							} else {
								setResult(EcoreUtil.getURI(eObject));
							}
						}
					});
				} catch (InterruptedException ex) {
					PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
				}
			}
		}
		return null;
	}
}
