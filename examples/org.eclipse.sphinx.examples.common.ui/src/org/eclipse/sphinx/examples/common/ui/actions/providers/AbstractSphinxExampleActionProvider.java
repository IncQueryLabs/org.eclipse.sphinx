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
package org.eclipse.sphinx.examples.common.ui.actions.providers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider;
import org.eclipse.sphinx.examples.common.ui.ISphinxExampleMenuConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * A {@link BasicActionProvider action provider} implementation for Sphinx examples actions.
 *
 * @since 0.7.0
 */
public abstract class AbstractSphinxExampleActionProvider extends BasicActionProvider {

	/*
	 * @see
	 * org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider#addSubMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected IMenuManager addSubMenu(IMenuManager contextMenuManager) {
		IMenuManager examplesMenuManager = contextMenuManager.findMenuUsingPath(ISphinxExampleMenuConstants.MENU_SPHINX_EXAMPLES_ID);
		if (examplesMenuManager == null) {
			examplesMenuManager = new MenuManager(ISphinxExampleMenuConstants.MENU_SPHINX_EXAMPLES_LABEL,
					ISphinxExampleMenuConstants.MENU_SPHINX_EXAMPLES_ID);
			contextMenuManager.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, examplesMenuManager);

			examplesMenuManager.add(new Separator(ISphinxExampleMenuConstants.GROUP_SPHINX_EXAMPLES));
		}
		return examplesMenuManager;
	}
}