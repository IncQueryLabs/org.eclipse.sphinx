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
package org.eclipse.sphinx.examples.hummingbird20.incquery.common;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.workspace.incquery.AbstractMatcherProvider;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;

public class CommonMatcherProvider extends AbstractMatcherProvider {

	@SuppressWarnings("rawtypes")
	@Override
	public <T> IncQueryMatcher getMatcher(IncQueryEngine engine, Class<T> type) throws IncQueryException {
		if (Identifiable.class == type) {
			return IdentifiablesMatcher.on(engine);
		}
		return null;
	}

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(Identifiable.class);
	}
}
