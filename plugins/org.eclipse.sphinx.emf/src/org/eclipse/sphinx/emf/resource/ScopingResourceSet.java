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
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.scoping.IResourceScope;

/**
 * A scoping {@linkplain ResourceSet resource set} that uses {@linkplain IResourceScope scopes}
 */
public interface ScopingResourceSet extends ResourceSet {
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

	/**
	 * @deprecated Use {@link #getEObjectInScope(URI, IMetaModelDescriptor, EObject, boolean)} instead.
	 */
	@Deprecated
	EObject getEObjectInScope(URI uri, boolean loadOnDemand, EObject contextObject);

	/**
	 * Retrieves the {@linkplain EObject object} from specified {@link IMetaModelDescriptor metamodel} that corresponds
	 * to given {@linkplain URI}. Uses provided {@link EObject context object} to limit the search scope to the subset
	 * of {@link Resource resource}s that are in the same {@link IResourceScope scope} as the resource that contains the
	 * context object.
	 * 
	 * @param uri
	 *            The {@linkplain URI} to be resolved.
	 * @param metaModelDescriptor
	 *            The {@link IMetaModelDescriptor meta model descriptor} of the object that is referenced by given URI.
	 * @param contextObject
	 *            The context object that is used to limit the search scope.
	 * @param loadOnDemand
	 *            Whether to load the resource or model containing the object that is referenced by given URI if it is
	 *            not already loaded.
	 * @return The object that corresponds to given URI or <code>null</code> if given URI cannot be resolved.
	 */
	EObject getEObjectInScope(URI uri, IMetaModelDescriptor metaModelDescriptor, EObject contextObject, boolean loadOnDemand);
}
