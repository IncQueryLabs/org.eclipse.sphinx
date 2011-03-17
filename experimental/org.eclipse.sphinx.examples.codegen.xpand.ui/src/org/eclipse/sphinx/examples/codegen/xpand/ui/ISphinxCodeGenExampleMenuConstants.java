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
package org.eclipse.sphinx.examples.codegen.xpand.ui;

import org.eclipse.sphinx.examples.codegen.xpand.ui.internal.Activator;
import org.eclipse.sphinx.examples.codegen.xpand.ui.internal.messages.Messages;

public interface ISphinxCodeGenExampleMenuConstants {

	/**
	 * Identifier of the Generate sub menu.
	 */
	public static final String MENU_GENERATE_ID = Activator.getPlugin().getSymbolicName() + ".menus.generate"; //$NON-NLS-1$

	/**
	 * Label of the Generate sub menu.
	 */
	public static final String MENU_GENERATE_LABEL = Messages.menu_generate;
}
