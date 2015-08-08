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
 *     itemis - [475954] Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird20.incquery;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.workspace.incquery.proxymanagement.AbstractScopingIncQueryProxyResolver;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;

public class Hummingbird20ProxyResolver extends AbstractScopingIncQueryProxyResolver {

	@Override
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

	@Override
	protected EStructuralFeature getNameFeature(EClass eclass) {
		return Common20Package.Literals.IDENTIFIABLE__NAME;
	}

	@Override
	protected boolean isTypeSupported(EClass eType) {
		return Common20Package.eINSTANCE.getIdentifiable().isSuperTypeOf(eType);
	}

}
