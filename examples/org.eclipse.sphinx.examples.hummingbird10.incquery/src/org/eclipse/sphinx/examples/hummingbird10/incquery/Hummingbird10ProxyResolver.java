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
 *     itemis - [475954] Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird10.incquery;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.workspace.incquery.proxymanagement.AbstractScopingIncQueryProxyResolver;
import org.eclipse.sphinx.examples.hummingbird10.Application;
import org.eclipse.sphinx.examples.hummingbird10.Component;
import org.eclipse.sphinx.examples.hummingbird10.Connection;
import org.eclipse.sphinx.examples.hummingbird10.Interface;
import org.eclipse.sphinx.examples.hummingbird10.Parameter;
import org.eclipse.sphinx.examples.hummingbird10.incquery.internal.Activator;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.Hummingbird10IncQueryHelper;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class Hummingbird10ProxyResolver extends AbstractScopingIncQueryProxyResolver {

	private Hummingbird10IncQueryHelper helper = new Hummingbird10IncQueryHelper();

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(Application.class);
		getSupportedTypes().add(Component.class);
		getSupportedTypes().add(Connection.class);
		getSupportedTypes().add(Interface.class);
		getSupportedTypes().add(Parameter.class);
	}

	@Override
	protected EObject[] getEObjectCandidates(EObject proxy, Object contextObject, IncQueryEngine engine) {
		return new EObject[] {};
	}

	@Override
	protected EObject[] getEObjectCandidates(URI proxyURI, Object contextObject, IncQueryEngine engine) {

		Class<?> type = getInstanceClass(proxyURI);
		if (type != null) {
			try {
				return doGetEObjectCandidates(type, engine);
			} catch (IncQueryException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return new EObject[] {};
	}

	private EObject[] doGetEObjectCandidates(Class<?> type, IncQueryEngine engine) throws IncQueryException {
		if (Application.class == type) {
			ApplicationsMatcher matcher = ApplicationsMatcher.on(engine);
			Set<Application> candidates = matcher.getAllValuesOfapp();
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Component.class == type) {
			ComponentsMatcher matcher = ComponentsMatcher.on(engine);
			Set<Component> candidates = matcher.getAllValuesOfcomponent();
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Connection.class == type) {
			ConnectionsMatcher matcher = ConnectionsMatcher.on(engine);
			Set<Connection> candidates = matcher.getAllValuesOfconnection();
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Interface.class == type) {
			InterfacesMatcher matcher = InterfacesMatcher.on(engine);
			Set<Interface> candidates = matcher.getAllValuesOfinterface();
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Parameter.class == type) {
			ParametersMatcher matcher = ParametersMatcher.on(engine);
			Set<Parameter> candidates = matcher.getAllValuesOfparameter();
			return candidates.toArray(new EObject[candidates.size()]);
		}
		return null;
	}

	private Class<?> getInstanceClass(URI proxyURI) {
		EClass eClass = helper.getEClass(proxyURI);
		return eClass != null ? eClass.getInstanceClass() : null;
	}
}