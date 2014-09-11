/**
 * <copyright>
 *
 * Copyright (c) 2008-2014 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [442342] Sphinx doen't trim context information from proxy URIs when serializing proxyfied cross-document references
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.ecore.proxymanagement.blacklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.resource.ExtendedResourceSet;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 *
 */
// TODO Add JavaDoc comments.
// FIXME aakar: make access to index thread safe
public class MapModelIndex implements IResourceChangeListener {

	private Map<String, Object> index;
	private Set<URI> proxyURIs;
	private MapResourceDeltaVisitor resourceDeltaVisitor;

	public MapModelIndex() {
		index = new WeakHashMap<String, Object>();
		proxyURIs = new HashSet<URI>();
		resourceDeltaVisitor = new MapResourceDeltaVisitor();
		startListening();
	}

	public void startListening() {
		if (Platform.isRunning()) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		}
	}

	public void stopListening() {
		if (Platform.isRunning()) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		}
	}

	public void clearAll() {
		index.clear();
		proxyURIs.clear();
	}

	public void dispose() {
		stopListening();
		index.clear();
		proxyURIs.clear();
	}

	public boolean exists(URI fragmentUri) {
		return false;
	}

	public boolean existsProxyURI(URI proxyURI) {
		return proxyURIs.contains(proxyURI);
	}

	/**
	 * @return true if the proxy URIs set did not already contain the specified element.
	 */
	public boolean addProxyURI(URI proxyURI) {
		if (proxyURIs.add(proxyURI)) {
			// System.out.println("Added Proxy: " + proxyURIs.size() + " " + proxyURI); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		} else {
			// System.out.println("Trying to add existing proxy: " + proxyURIs.size() + " " + proxyURI); //$NON-NLS-1$//$NON-NLS-2$
			return false;
		}
	}

	/**
	 * @return true if the proxy URIs set contained the specified element.
	 */
	public boolean removeProxyURI(URI proxyURI) {
		if (proxyURIs.remove(proxyURI)) {
			// System.out.println("Removed Proxy: " + proxyURIs.size() + " " + proxyURI); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		} else {
			// System.out.println("Trying to remove proxy: " + proxyURIs.size() + " " + proxyURI); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
	}

	/**
	 * Removes the proxyURI from the black list on certain conditions.
	 */
	public void updateIndexOnResourceLoaded(Resource resource) {
		if (resource != null && !resource.getContents().isEmpty()) {
			for (URI proxyURI : new ArrayList<URI>(proxyURIs)) {
				// FIXME Potential EMF bug: NumberFormatExeption raised in XMIResourceImpl#getEObject(String) upon
				// unexpected URI fragment format.
				try {
					// If proxy URI is not fragment-based, i.e. includes segments pointing at the target resource, we
					// have to make sure that it matches URI of loaded resource
					URI targetResourceURI = proxyURI.trimFragment();
					ResourceSet resourceSet = resource.getResourceSet();
					if (resourceSet instanceof ExtendedResourceSet) {
						targetResourceURI = ((ExtendedResourceSet) resourceSet).trimProxyContextInfo(targetResourceURI);
					}
					if (proxyURI.segmentCount() == 0 || resource.getURI().equals(targetResourceURI)) {
						// Remove the proxyURI from the list and do not try to resolve it to avoid costly resolution in
						// case of a large number of unresolved proxies
						removeProxyURI(proxyURI);
					}
				} catch (Exception ex) {
					// Ignore exception
				}
			}
		}
	}

	public void updateIndexOnResourceUnloaded(Resource resource) {
		if (resource != null) {
			TreeIterator<EObject> iterator = resource.getAllContents();
			while (iterator.hasNext()) {
				EObject currentObject = iterator.next();
				if (currentObject.eIsProxy() && existsProxyURI(((InternalEObject) currentObject).eProxyURI())) {
					removeProxyURI(((InternalEObject) currentObject).eProxyURI());
				}
			}
		}
	}

	public Collection<URI> findInstances(EClass class1, IProject scope) {
		return null;
	}

	public Collection<URI> findReferencesTo(EObject object, IProject scope) {
		return null;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				delta.accept(resourceDeltaVisitor);
			}
		} catch (CoreException exc) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), "Error indexing models" + exc); //$NON-NLS-1$
		}
	}

	public void reIndex() throws Exception {
		// TODO To be implemented?
	}
}
