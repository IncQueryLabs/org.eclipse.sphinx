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
package org.eclipse.sphinx.emf.messages;

import org.eclipse.osgi.util.NLS;

public class EMFMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.emf.messages.EMFMessages"; //$NON-NLS-1$

	public static String warning_selectionContainsUnresolvedModelElement;
	public static String error_unexpectedImplementationOfElementAttributeInContribution;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EMFMessages.class);
	}

	private EMFMessages() {
	}
}
