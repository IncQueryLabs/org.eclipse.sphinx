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
package org.eclipse.sphinx.examples.hummingbird10.incquery.service;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.sphinx.emf.incquery.proxymanagment.AbstractProxyResolverService;
import org.eclipse.sphinx.examples.hummingbird10.incquery.Hb10ProxyResolver;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.Hb10IncQueryHelper;

public class Hummingbird10ProxyResolverService extends AbstractProxyResolverService {

	private Hb10IncQueryHelper helper = new Hb10IncQueryHelper();

	@Override
	protected void initProxyResolvers() {
		getProxyResolvers().add(new Hb10ProxyResolver());
	}

	@Override
	protected EClass getTargetEClass(URI uri) {
		return helper.getEClass(uri);
	}
}
