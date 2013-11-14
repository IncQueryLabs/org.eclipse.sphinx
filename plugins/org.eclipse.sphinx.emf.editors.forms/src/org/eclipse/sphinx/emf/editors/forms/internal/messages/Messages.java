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
 *     itemis - [421585] Form Editor silently closes if model is not loaded via Sphinx
 *     
 * </copyright>
 */
package org.eclipse.sphinx.emf.editors.forms.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sphinx.emf.editors.forms.internal.messages.messages"; //$NON-NLS-1$

	public static String msg_waitingForModelObjectToBeLoaded;

	public static String page_contentsTree_title;
	public static String section_genericContentsTree_title;
	public static String section_genericContentsTree_description;

	public static String error_editorInitialization_title;
	public static String error_editorInitialization_modelNotLoaded;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
