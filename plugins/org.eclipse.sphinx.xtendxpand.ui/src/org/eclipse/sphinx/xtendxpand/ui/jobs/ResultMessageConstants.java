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
package org.eclipse.sphinx.xtendxpand.ui.jobs;

/**
 * Constants that indicate when a message dialog should be displayed.
 */
public interface ResultMessageConstants {

	/**
	 * The constant that indicates the message dialog should be displayed only if M2x job ends with errors or check done
	 * before failed. This is the default behavior. If M2x failed then Eclipse automatically opens a message dialog with
	 * error message. If check done before M2x ends with errors then we open explicitly a message dialog.
	 */
	int OPEN_DIALOG_ON_FAILED = 0;

	/**
	 * The constant that indicates the message dialog should be displayed only if M2x job failed or completed.
	 */
	int OPEN_DIALOG_ON_FAILED_OR_COMPLETION = 1;

	/**
	 * The constant that indicate the message dialog should be displayed only if M2x job failed or completed or
	 * cancelled.
	 */
	int OPEN_DIALOG_ON_FAILED_OR_COMPLETION_OR_CANCELLATION = 2;
}
