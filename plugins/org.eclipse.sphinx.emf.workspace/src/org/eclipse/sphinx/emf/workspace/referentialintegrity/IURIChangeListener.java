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

/**
 * Listener in charge of handling {@link URIChangeEvent}s.
 */
public interface IURIChangeListener {

	/**
	 * The methods is notified each time an {@link URIChangeEvent} is triggered.
	 * 
	 * @param event
	 *            the event to handle.
	 */
	public void uriChanged(URIChangeEvent event);
}
