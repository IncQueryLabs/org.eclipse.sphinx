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
package org.eclipse.sphinx.examples.validation.ui.actions.providers;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.validation.ui.actions.providers.AbstractValidationActionProvider;
import org.eclipse.sphinx.examples.common.ui.ISphinxExampleMenuConstants;
import org.eclipse.sphinx.examples.validation.ui.IValidationExampleMenuConstants;
import org.eclipse.sphinx.examples.validation.ui.actions.BasicValidateSelectedCategoriesAction;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * @since 0.7.0
 */
public class BasicValidationExampleActionProvider extends AbstractValidationActionProvider {

	/**
	 * 
	 */
	private BasicValidateSelectedCategoriesAction validateCategoriesAction;

	/*
	 * @see org.eclipse.sphinx.emf.ui.actions.providers.BasicActionProvider#doInit()
	 */
	@Override
	public void doInit() {
		validateCategoriesAction = createValidateCategoriesAction();
	}

	protected BasicValidateSelectedCategoriesAction createValidateCategoriesAction() {
		return new BasicValidateSelectedCategoriesAction();
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.validation.ui.actions.providers.AbstractValidationActionProvider#addSubMenu(org.eclipse.jface
	 * .action.IMenuManager)
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

		IMenuManager validationMenuManager = examplesMenuManager.findMenuUsingPath(IValidationExampleMenuConstants.MENU_VALIDATION_ID);
		if (validationMenuManager == null) {
			validationMenuManager = new MenuManager(IValidationExampleMenuConstants.MENU_VALIDATION_LABEL,
					IValidationExampleMenuConstants.MENU_VALIDATION_ID);
			examplesMenuManager.appendToGroup(ISphinxExampleMenuConstants.GROUP_SPHINX_EXAMPLES, validationMenuManager);
		}
		return validationMenuManager;
	}

	/*
	 * @see
	 * org.eclipse.sphinx.emf.validation.ui.actions.providers.AbstractValidationActionProvider#populateActions(org.eclipse
	 * .jface.action.IMenuManager, org.eclipse.jface.viewers.IStructuredSelection, boolean)
	 */
	@Override
	protected void populateActions(IMenuManager menu, IStructuredSelection selection, boolean enabled) {
		validateCategoriesAction.selectionChanged(selection);
		validateCategoriesAction.setEnabled(enabled);
		menu.add(new ActionContributionItem(validateCategoriesAction));
	}
}
