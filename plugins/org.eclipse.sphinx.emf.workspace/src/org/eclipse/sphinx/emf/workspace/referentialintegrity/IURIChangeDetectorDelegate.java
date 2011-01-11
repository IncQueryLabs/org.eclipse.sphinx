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
import org.eclipse.emf.common.notify.Notification;

/**
 * Interface that determines URIChangeDetectorDelegate API.see also {@link URIChangeDetectorDelegateRegistry}
 */
public interface IURIChangeDetectorDelegate {

	/**
	 * Detects all the model changed {@link URI}s from a given resource set event {@link Notification}
	 * 
	 * @param notification
	 *            The notification from the resource set event to use for computing the changed {@link URI}s
	 * @return A map containing new {@link EObject new EObject} , {@link URI old URI} pairs.
	 */
	public List<URIChangeNotification> detectChangedURIs(Notification notification);

	/**
	 * Detects all the model changed {@link URI}s from a given {@link IFile file} moved event .
	 * 
	 * @param oldFile
	 *            The {@link IFile file} before modification.
	 * @param newFile
	 *            The {@link IFile file} after modification.
	 * @return A map containing {@link EObject new EObject} , {@link URI old URI} pairs.
	 */
	public List<URIChangeNotification> detectChangedURIs(IFile oldFile, IFile newFile);

}
