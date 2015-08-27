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
package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.AbstractHummingbird20ProxyResolver;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;

public class InstanceModelProxyResolver extends AbstractHummingbird20ProxyResolver {

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(Application.class);
		getSupportedTypes().add(Component.class);
		getSupportedTypes().add(Connection.class);
	}

	@Override
	protected EObject[] doGetEObjectCandidates(Class<?> type, String name, IncQueryEngine engine) throws IncQueryException {
		if (Application.class == type) {
			ApplicationsByNameMatcher matcher = ApplicationsByNameMatcher.on(engine);
			Set<Application> candidates = matcher.getAllValuesOfapp(name);
			return candidates.toArray(new EObject[candidates.size()]);

		}
		if (Component.class == type) {
			ComponentsByNameMatcher matcher = ComponentsByNameMatcher.on(engine);
			Set<Component> candidates = matcher.getAllValuesOfcomponent(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Connection.class == type) {
			ConnectionsByNameMatcher matcher = ConnectionsByNameMatcher.on(engine);
			Set<Connection> candidates = matcher.getAllValuesOfconnection(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		return null;
	}
}
