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
package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.workspace.incquery.AbstractMatcherProvider;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

public class TypeModelMatcherProvider extends AbstractMatcherProvider {

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(ComponentType.class);
		getSupportedTypes().add(Port.class);
		getSupportedTypes().add(Interface.class);
		getSupportedTypes().add(Parameter.class);
		getSupportedTypes().add(Platform.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> IncQueryMatcher getMatcher(IncQueryEngine engine, Class<T> type) throws IncQueryException {
		if (ComponentType.class == type) {
			return ComponentTypesMatcher.on(engine);
		} else if (Port.class == type) {
			return PortsMatcher.on(engine);
		} else if (Interface.class == type) {
			return InterfacesMatcher.on(engine);
		} else if (Parameter.class == type) {
			return ParametersMatcher.on(engine);
		} else if (Platform.class == type) {
			return PlatformsMatcher.on(engine);
		}
		return null;
	}
}
