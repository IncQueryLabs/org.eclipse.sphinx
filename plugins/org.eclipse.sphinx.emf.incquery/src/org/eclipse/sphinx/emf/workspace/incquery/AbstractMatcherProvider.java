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
package org.eclipse.sphinx.emf.workspace.incquery;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;

@SuppressWarnings("rawtypes")
public abstract class AbstractMatcherProvider extends AbstractIncQueryProvider implements IMatcherProvider {

	public AbstractMatcherProvider() {
		super();
	}

	@Override
	public boolean isProviderForType(EObject eObject) {
		return isProviderForType(eObject.eClass());
	}

	@Override
	public boolean isProviderForType(EClass eType) {
		return isProviderForType(eType.getInstanceClass());
	}

	@Override
	public <T> boolean isProviderForType(Class<T> type) {
		return getSupportedTypes().contains(type);
	}

	@Override
	public IncQueryMatcher getMatcher(IncQueryEngine engine, EObject eObject) throws IncQueryException {
		return getMatcher(engine, eObject.eClass());
	}

	@Override
	public IncQueryMatcher getMatcher(IncQueryEngine engine, EClass eType) throws IncQueryException {
		return getMatcher(engine, eType.getInstanceClass());
	}
}
