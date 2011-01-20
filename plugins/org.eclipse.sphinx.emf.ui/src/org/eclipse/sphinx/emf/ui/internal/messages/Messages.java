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
package org.eclipse.sphinx.emf.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.emf.ui.internal.messages.Messages"; //$NON-NLS-1$

	public static String OpenInEditor_label;
	public static String label_renameDialogTitle;
	public static String label_renameDialogMessage;

	public static String menuItem_rename;
	public static String menuItem_move;

	public static String label_noObjectSelected;
	public static String label_multiObjectSelected;

	public static String label_openWithMenu;

	public static String label_editProxyURI;
	public static String message_proxyURIReferencesElementInList;

	static {
		// Initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
