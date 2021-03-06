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
package org.eclipse.sphinx.emf.explorer.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.emf.explorer.internal.messages.Messages"; //$NON-NLS-1$

	public static String label_EmptyFeatureMapEntryLabel;
	public static String label_confirmCopy;
	public static String label_OKToCopy;
	public static String label_confirmMove;
	public static String label_OKToMove;
	public static String label_confirmDuplicate;
	public static String label_OKToHaveDuplicate;

	public static String info_targetObjectType;
	public static String info_dropCommandCannotExecute;

	public static String error_transferTypeNotSupported;
	public static String error_targetNoEditingDomain;
	public static String error_failedToSaveModelsInWorkbench;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
