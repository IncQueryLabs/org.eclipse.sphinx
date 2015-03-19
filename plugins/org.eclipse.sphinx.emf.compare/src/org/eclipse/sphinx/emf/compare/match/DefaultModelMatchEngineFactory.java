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

import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.sphinx.emf.model.IModelDescriptor;

public class DefaultModelMatchEngineFactory extends AbstractModelMatchEngineFactory {

	public DefaultModelMatchEngineFactory() {
		this(new ModelMatchEngine());
	}

	public DefaultModelMatchEngineFactory(IMatchEngine matchEngine) {
		super(matchEngine);
	}

	@Override
	protected boolean isMatchEngineFactoryFor(IModelDescriptor modelDescriptor) {
		return true;
	}
}
