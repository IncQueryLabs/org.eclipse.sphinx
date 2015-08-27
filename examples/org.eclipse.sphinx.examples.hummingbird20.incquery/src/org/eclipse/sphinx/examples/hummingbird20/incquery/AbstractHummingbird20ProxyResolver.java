/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
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
package org.eclipse.sphinx.examples.hummingbird20.incquery;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.proxymanagment.AbstractProxyResolver;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractHummingbird20ProxyResolver extends AbstractProxyResolver {

	@Override
	protected EObject[] getEObjectCandidates(EObject proxy, Object contextObject, IncQueryEngine engine) {
		Class<?> type = getInstanceClass(proxy);
		String name = getName(proxy);
		if (type != null && !isBlank(name)) {
			try {
				return doGetEObjectCandidates(type, name, engine);
			} catch (IncQueryException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return new EObject[] {};
	}

	@Override
	protected EObject[] getEObjectCandidates(URI proxyURI, Object contextObject, IncQueryEngine engine) {
		return new EObject[] {};
	}

	protected abstract EObject[] doGetEObjectCandidates(Class<?> type, String name, IncQueryEngine engine) throws IncQueryException;

	protected String getName(EObject proxy) {
		InternalEObject internalEObject = (InternalEObject) proxy;
		// Is given AROject a proxy?
		if (internalEObject.eIsProxy()) {
			// Retrieve absolute qualified name of given ARObject from its proxy URI
			String uriFragment = internalEObject.eProxyURI().fragment();
			if (uriFragment != null) {
				int queryStringIndex = uriFragment.indexOf(ExtendedResource.URI_QUERY_SEPARATOR);
				String aqn = queryStringIndex != -1 ? uriFragment.substring(0, queryStringIndex) : uriFragment;
				int segSeparatorLastIndex = aqn.lastIndexOf(ExtendedResource.URI_SEGMENT_SEPARATOR);
				return segSeparatorLastIndex != -1 ? aqn.substring(segSeparatorLastIndex + 1) : aqn;
			}
			return ""; //$NON-NLS-1$
		}
		return ((Identifiable) proxy).getName();
	}

	protected boolean isBlank(String text) {
		return text == null || text.isEmpty();
	}
}
