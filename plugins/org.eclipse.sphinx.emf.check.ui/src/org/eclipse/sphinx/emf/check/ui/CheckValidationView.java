/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */

package org.eclipse.sphinx.emf.check.ui;

import org.eclipse.ui.views.markers.MarkerSupportView;

public class CheckValidationView extends MarkerSupportView {

	/**
	 * Open a new instance of the receiver.
	 */
	public CheckValidationView() {
		super(IValidationUIConstants.VALIDATION_CHECK_MARKER_GENERATOR);
	}
}
