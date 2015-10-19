/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.emf.explorer.decorators;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.workspace.ui.decorators.BasicModelProblemDecorator;
import org.eclipse.sphinx.emf.workspace.ui.decorators.DecorationOverlayCalculator;
import org.eclipse.sphinx.emf.workspace.ui.viewers.ITreeContentProviderIterator;

public class BasicExplorerProblemDecorator extends BasicModelProblemDecorator {

	protected static class ModelExplorerItemFilter implements ITreeContentProviderIterator.IItemFilter {
		@Override
		public boolean accept(Object item) {
			return item instanceof IResource || item instanceof EObject || item instanceof TransientItemProvider
					|| item instanceof IWrapperItemProvider;
		}
	}

	@Override
	protected DecorationOverlayCalculator createDeclarationOverlayCalculator() {
		DecorationOverlayCalculator calculator = super.createDeclarationOverlayCalculator();
		calculator.setItemFilter(new ModelExplorerItemFilter());
		return calculator;
	}
}
