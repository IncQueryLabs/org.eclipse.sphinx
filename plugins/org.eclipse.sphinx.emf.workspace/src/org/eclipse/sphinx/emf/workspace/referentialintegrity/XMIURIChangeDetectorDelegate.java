/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [418005] Add support for model files with multiple root elements
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.referentialintegrity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 *
 */
public class XMIURIChangeDetectorDelegate implements IURIChangeDetectorDelegate {

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.referencialintegrity.IURIChangeDetectorDelegate#detectChangedURIs(org.eclipse
	 * .emf .common.notify.Notification)
	 */
	@Override
	public List<URIChangeNotification> detectChangedURIs(Notification notification) {
		List<URIChangeNotification> uriChangeNotifications = new ArrayList<URIChangeNotification>();

		Object notifier = notification.getNotifier();
		if (notifier instanceof EObject) {
			EObject eObject = (EObject) notifier;
			uriChangeNotifications.add(new URIChangeNotification(eObject, EcoreResourceUtil.getURI(eObject)));
		}

		return uriChangeNotifications;
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.workspace.referencialintegrity.IURIChangeDetectorDelegate#detectChangedURIs(org.eclipse
	 * .core .resources.IFile, org.eclipse.core.resources.IFile)
	 */
	@Override
	public List<URIChangeNotification> detectChangedURIs(IFile oldFile, IFile newFile) {
		if (!oldFile.getFullPath().equals(newFile.getFullPath())) {
			final Resource resource = EcorePlatformUtil.getResource(oldFile);
			if (resource != null) {
				final TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resource);
				if (editingDomain != null) {
					try {
						return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<List<URIChangeNotification>>() {
							@Override
							public void run() {
								List<URIChangeNotification> uriChangeNotifications = new ArrayList<URIChangeNotification>();

								TreeIterator<EObject> allContents = resource.getAllContents();
								while (allContents.hasNext()) {
									EObject eObject = allContents.next();
									uriChangeNotifications.add(new URIChangeNotification(eObject, EcoreResourceUtil.getURI(eObject)));
								}

								setResult(uriChangeNotifications);
							}
						});
					} catch (InterruptedException ex) {
						PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
					}
				}
			}
		}
		return Collections.emptyList();
	}
}
