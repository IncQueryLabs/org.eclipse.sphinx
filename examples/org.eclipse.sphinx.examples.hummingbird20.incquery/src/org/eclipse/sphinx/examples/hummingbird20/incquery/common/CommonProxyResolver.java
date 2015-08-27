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

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.AbstractHummingbird20ProxyResolver;

public class CommonProxyResolver extends AbstractHummingbird20ProxyResolver {

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(Identifiable.class);
	}

	@Override
	protected EObject[] doGetEObjectCandidates(Class<?> type, String name, IncQueryEngine engine) throws IncQueryException {
		if (Identifiable.class == type) {
			IdentifiablesByNameMatcher matcher = IdentifiablesByNameMatcher.on(engine);
			Set<Identifiable> candidates = matcher.getAllValuesOfidentifiable(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		return null;
	}
}
