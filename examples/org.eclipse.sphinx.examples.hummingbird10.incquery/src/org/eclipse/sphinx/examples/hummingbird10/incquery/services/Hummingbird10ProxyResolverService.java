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
 *     itemis - 475954: Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird10.incquery.services;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.sphinx.emf.ecore.proxymanagement.AbstractProxyResolverService;
import org.eclipse.sphinx.examples.hummingbird10.incquery.Hummingbird10ProxyResolver;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.Hummingbird10IncQueryHelper;

public class Hummingbird10ProxyResolverService extends AbstractProxyResolverService {

	private Hummingbird10IncQueryHelper helper = new Hummingbird10IncQueryHelper();

	@Override
	protected void initProxyResolvers() {
		getProxyResolvers().add(new Hummingbird10ProxyResolver());
	}

	@Override
	protected EClass getTargetEClass(URI uri) {
		return helper.getEClass(uri);
	}
}
