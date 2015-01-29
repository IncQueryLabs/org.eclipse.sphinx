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
package org.eclipse.sphinx.emf.compare.match;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.compare.scope.IModelComparisonScope;

public class ModelMatchEngineFactory extends MatchEngineFactoryImpl {

	/**
	 * Default Constructor.
	 */
	public ModelMatchEngineFactory() {
		matchEngine = new ModelMatchEngine();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.compare.match.IMatchEngine.Factory.isMatchEngineFactoryFor(org.eclipse.emf.compare
	 *      .scope.IComparisonScope)
	 */
	@Override
	public boolean isMatchEngineFactoryFor(IComparisonScope scope) {
		if (scope instanceof IModelComparisonScope) {
			return true;
		}

		final Notifier left = scope.getLeft();
		final Notifier right = scope.getRight();
		final Notifier origin = scope.getOrigin();
		if (left instanceof EObject && right instanceof EObject && (origin == null || origin instanceof EObject)) {
			return true;
		}
		if (left instanceof Resource && right instanceof Resource && (origin == null || origin instanceof Resource)) {
			return true;
		}
		return false;
	}
}
