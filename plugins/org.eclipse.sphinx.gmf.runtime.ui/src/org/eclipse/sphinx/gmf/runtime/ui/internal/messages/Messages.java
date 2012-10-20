/**
 * <copyright>
 * 
 * Copyright (c) 2008-2012 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - Fixed wrong BUNDLE_NAME
 *     itemis - [392464] Finish up Sphinx editor socket for GMF-based graphical editors
 * 
 * </copyright>
 */
package org.eclipse.sphinx.gmf.runtime.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.gmf.runtime.ui.internal.messages.messages"; //$NON-NLS-1$

	public static String error_IncorrectInput;
	public static String error_NoDiagramInResource;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
