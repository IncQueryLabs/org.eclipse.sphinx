/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [409458] Enhance ScopingResourceSetImpl#getEObjectInScope() to enable cross-document references between model files with different metamodels
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
 *     
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.scoping.IResourceScope;

/**
 * A scoping {@linkplain ResourceSet resource set} that uses {@linkplain IResourceScope scopes}
 */
public interface ScopingResourceSet extends ExtendedResourceSet {
	/**
	 * Retrieves all {@link Resource resource}s in this {@link ResourceSet resourceset} owned by the model containing
	 * the context object and its referenced models.
	 * 
	 * @param contextObject
	 *            A context object for which a list of resources should be retrieved.
	 * @return A list of resources for the given context object.
	 */
	List<Resource> getResourcesInModel(Object contextObject);

	/**
	 * Retrieves all {@link Resource resource}s in this {@link ResourceSet resourceset} owned by the model containing
	 * the context object.
	 * 
	 * @param contextObject
	 *            A context object for which a list of resources should be retrieved.
	 * @param includeReferencedScopes
	 *            A boolean value that let to decide if resources must be retrieved under depending roots of the
	 *            concerned model
	 * @return A list of resources for the given context object.
	 */
	List<Resource> getResourcesInModel(Object contextObject, boolean includeReferencedScopes);

	/**
	 * Retrieves all {@link Resource resource}s in this {@link ResourceSet resourceset} owned by the
	 * {@link IResourceScope resource scope} containing the context object and its referenced {@link IResourceScope
	 * resource scope}s.
	 * 
	 * @param contextObject
	 *            A context object for which a list of resources should be retrieved.
	 * @return
	 */
	List<Resource> getResourcesInScope(Object contextObject);

	/**
	 * Retrieves all {@link Resource resource}s in this {@link ResourceSet resourceset} owned by the
	 * {@link IResourceScope resource scope} containing the context object.
	 * 
	 * @param contextObject
	 *            A context object for which a list of resources should be retrieved.
	 * @param includeReferencedScopes
	 *            A boolean value that let to decide if resources must be retrieved under depending roots of the
	 *            concerned model.
	 * @return
	 */
	List<Resource> getResourcesInScope(Object contextObject, boolean includeReferencedScopes);
}
