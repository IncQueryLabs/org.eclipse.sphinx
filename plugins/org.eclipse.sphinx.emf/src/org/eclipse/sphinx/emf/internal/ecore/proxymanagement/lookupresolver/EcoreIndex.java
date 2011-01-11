/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.ecore.proxymanagement.lookupresolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.messages.PlatformMessages;

/**
 * Simple implementation of Ecore Index (associating fragmend-based URIs with EObjects). Used at the end of model
 * loading operations in order to optimize proxy resolution for performance.
 * 
 * @deprecated Will be removed as soon as a full-fledged model indexing service is in place and can be used to overcome
 *             performance bottlenecks due to proxy resolution.
 */
@Deprecated
public final class EcoreIndex {

	// TODO Precise the resource for which we have to maintain this map.
	private Map<String, EObject> index = null;

	/**
	 * 
	 */
	private void assertIsAvailable() {
		if (!isAvailable()) {
			throw new RuntimeException(NLS.bind(PlatformMessages.error_mustNotBeNull, "index")); //$NON-NLS-1$
		}
	}

	/**
	 * @return <code>true</code> if index is available; <code>false</code> otherwise.
	 */
	public boolean isAvailable() {
		return index != null;
	}

	public void clear() {
		assertIsAvailable();
		index = null;
	}

	public void init() {
		index = new HashMap<String, EObject>();
	}

	public EObject get(URI uri) {
		assertIsAvailable();
		Assert.isNotNull(uri, NLS.bind(PlatformMessages.arg_mustNotBeNull, "uri")); //$NON-NLS-1$
		return index.get(uri.fragment());
	}

	public void init(Collection<Resource> resources) {
		if (!isAvailable()) {
			init();
		}
		for (Resource resource : resources) {
			for (Iterator<EObject> i = resource.getAllContents(); i.hasNext();) {
				EObject o = i.next();
				String fragment = resource.getURIFragment(o);
				// FIXME A object (from another resource for instance) may already have been put with the same URI.
				index.put(fragment, o);
			}
		}
	}
}
