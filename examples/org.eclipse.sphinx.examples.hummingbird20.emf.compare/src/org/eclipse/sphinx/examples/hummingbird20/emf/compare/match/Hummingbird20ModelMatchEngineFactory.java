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
package org.eclipse.sphinx.examples.hummingbird20.emf.compare.match;

import org.eclipse.sphinx.emf.compare.match.DefaultModelMatchEngineFactory;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;

public class Hummingbird20ModelMatchEngineFactory extends DefaultModelMatchEngineFactory {

	@Override
	protected boolean isMatchEngineFactoryFor(IModelDescriptor modelDescriptor) {
		if (modelDescriptor != null) {
			return Hummingbird20MMDescriptor.INSTANCE == modelDescriptor.getMetaModelDescriptor();
		}
		return false;
	}
}
