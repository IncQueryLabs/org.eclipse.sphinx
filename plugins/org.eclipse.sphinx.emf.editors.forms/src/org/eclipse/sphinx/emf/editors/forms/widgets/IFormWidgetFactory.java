/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.emf.editors.forms.widgets;

import org.eclipse.sphinx.platform.ui.widgets.IWidgetFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

public interface IFormWidgetFactory extends IWidgetFactory {

	FormText createFormText(Composite parent);

	FormText createFormText(Composite parent, int colspan, boolean grabHorizontal);
}
