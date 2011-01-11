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
package org.eclipse.sphinx.emf.compare.match;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.compare.match.engine.GenericMatchEngine;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.ecore.EObject;

/**
 * Extends the generic {@linkplain GenericMatchEngine match engine} provided by EMF Compare so that this engine becomes
 * really model oriented (instead of resource oriented).
 */
public class ModelMatchEngine extends GenericMatchEngine {

	@Override
	protected void setModelRoots(MatchModel matchModel, EObject left, EObject right, EObject ancestor) {
		Assert.isNotNull(matchModel);

		if (left != null) {
			matchModel.getLeftRoots().add(left);
		}
		if (right != null) {
			matchModel.getRightRoots().add(right);
		}
		if (ancestor != null) {
			matchModel.getAncestorRoots().add(ancestor);
		}
	}
}
