/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Added/Updated javadoc
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sphinx.emf.internal.resource.URIResourceCacheUpdater;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;

/**
 * A ResourceSet which will try to automatically resolve the content type for a Resource when asked to create one and
 * not provided with a content type.
 */
public class ExtendedResourceSetImpl extends ResourceSetImpl {

	protected URIResourceCacheUpdater resourceChangeListener = new URIResourceCacheUpdater();

	public ExtendedResourceSetImpl() {
		uriResourceMap = new WeakHashMap<URI, Resource>();
	}

	/**
	 * Installs a map for caching the resource {@link #getResource(URI, boolean) associated} with a specific URI.
	 */
	@Override
	public Map<URI, Resource> getURIResourceMap() {
		return uriResourceMap;
	}

	// Overridden to remove attempt to find uncached resources by normalizing their URIs and comparing them to the URIs
	// of all existing resources in the ResourceSet. As we always have the URI to resource cache in place this is no
	// longer necessary but would decrease runtime performance when new resources are loaded into resource sets which
	// already contain a big number of files.
	@Override
	public Resource getResource(URI uri, boolean loadOnDemand) {
		Resource resource = getURIResourceMap().get(uri);
		if (resource != null) {
			if (loadOnDemand && !resource.isLoaded()) {
				demandLoadHelper(resource);
			}
			return resource;
		}

		Resource delegatedResource = delegatedGetResource(uri, loadOnDemand);
		if (delegatedResource != null) {
			getURIResourceMap().put(uri, delegatedResource);
			return delegatedResource;
		}

		if (loadOnDemand) {
			resource = demandCreateResource(uri);
			if (resource == null) {
				throw new RuntimeException("Cannot create a resource for '" + uri + "'; a registered resource factory is needed"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			demandLoadHelper(resource);

			getURIResourceMap().put(uri, resource);
			return resource;
		}

		return null;
	}

	// Overridden for retrieving content type id behind given URI and making sure that the resource factory associated
	// with that content type id is used for resource creation
	@Override
	protected Resource demandCreateResource(URI uri) {
		String contentTypeId = EcoreResourceUtil.getContentTypeId(uri);
		return createResource(uri, contentTypeId);
	}

	// Overridden to make sure that errors and warnings encountered during resource creation remain available after the
	// resource has been loaded. They are normally automatically cleared when loading begins (see
	// org.eclipse.emf.ecore.resource.impl.ResourceImpl.load(InputStream, Map<?, ?>) for details)
	@Override
	protected void demandLoad(Resource resource) throws IOException {
		// Capture errors and warnings encountered during resource creation
		List<Diagnostic> creationErrors = new ArrayList<Diagnostic>(resource.getErrors());
		List<Diagnostic> creationWarnings = new ArrayList<Diagnostic>(resource.getWarnings());

		// Load resource
		super.demandLoad(resource);

		// Restore creation time errors and warnings
		resource.getErrors().addAll(creationErrors);
		resource.getWarnings().addAll(creationWarnings);
	}

	@Override
	public EList<Resource> getResources() {
		if (resources == null) {
			resources = new ExtendedResourcesEList<Resource>();
		}
		return resources;
	}

	/**
	 * @return The number of times this {@link ResourceSet} has been <i>structurally modified</i>.
	 * @see AbstractList#modCount
	 */
	public int getModCount() {
		return ((ExtendedResourcesEList<Resource>) getResources()).getModCount();
	}

	/**
	 * A notifying list implementation for supporting {@link ResourceSet#getResources}. Exposes a {@link #getModCount()}
	 * method that clients can use to figure out the number of times this list has been <i>structurally modified</i> and
	 * updates {@link ResourceSetImpl#getURIResourceMap()} when {@link Resource}s are added of removed.
	 * 
	 * @see AbstractList#modCount
	 */
	protected class ExtendedResourcesEList<E extends Object & Resource> extends ResourcesEList<E> {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(E object) {
			return super.add(object);
		};

		@Override
		public void add(int index, E object) {
			super.add(index, object);
		};

		// Overridden to force eager initialization of URI to resource cache as soon as new resources get created and
		// added to the resource set.
		@Override
		protected void didAdd(int index, E newObject) {
			super.didAdd(index, newObject);
			getURIResourceMap().put(newObject.getURI(), newObject);
		};

		// Overridden to force update of URI to resource cache as soon as resources get removed from the resource set.
		@Override
		protected void didRemove(int index, E oldObject) {
			getURIResourceMap().remove(oldObject);
			super.didRemove(index, oldObject);
		};

		@Override
		protected void didSet(int index, E newObject, E oldObject) {
			if (newObject != null) {
				getURIResourceMap().put(newObject.getURI(), newObject);
			}
			if (oldObject != null) {
				getURIResourceMap().remove(oldObject);
			}
		};

		@Override
		protected void didClear(int size, Object[] oldObjects) {
			getURIResourceMap().clear();
		}

		protected int getModCount() {
			return modCount;
		}
	}
}
