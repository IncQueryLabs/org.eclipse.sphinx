/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.examples.hummingbird20.ide.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.examples.hummingbird20.ide.ui.internal.messages.messages"; //$NON-NLS-1$

	public static String label_ComponentTypes_TransientNode;
	public static String label_Interfaces_TansientNode;
	public static String label_OutgoingConnetion_TransientNode;
	public static String label_Parameters_TransientNode;
	public static String label_ParameterValues_TransientNode;
	public static String label_Ports_TransientNode;
	public static String label_Components_TransientNode;

	static {
		// Initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
