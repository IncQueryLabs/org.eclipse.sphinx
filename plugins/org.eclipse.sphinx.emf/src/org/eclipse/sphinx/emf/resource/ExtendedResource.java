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
package org.eclipse.sphinx.emf.resource;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;

/**
 * A set of additional services for EMF {@link Resource resources} including memory-optimized unloading, proxy creation
 * with custom URI formats, and caching of problem marker attributes.
 */
public interface ExtendedResource {

	/**
	 * Separator separating the scheme portion from the rest of a URI.
	 */
	String URI_SCHEME_SEPARATOR = ":"; //$NON-NLS-1$

	/**
	 * Separator separating individual segments within a URI.
	 */
	String URI_SEGMENT_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * Separator separating a query within a URI.
	 */
	String URI_QUERY_SEPARATOR = "?"; //$NON-NLS-1$

	/**
	 * Separator separating a keys from values within the query portion of a URI.
	 */
	String URI_KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

	/**
	 * Separator separating the fragment from the segments portion within a URI.
	 */
	String URI_FRAGMENT_SEPARATOR = "#"; //$NON-NLS-1$

	/**
	 * Specifies whether unloading of this resource is to be performed in a limiting but memory-optimized way. The
	 * default is <code>Boolean.FALSE</code>.
	 * <p>
	 * This option involves the following implications:
	 * <ul>
	 * <li>Suppression of proxy creation for unloaded {@link EObject}s (for saving non negligible amounts of memory
	 * consumption for proxy URIs required otherwise)</li>
	 * <li>Clearing all fields on unloaded {@link EObject}s (to make sure that they get garbage collected as fast as
	 * possible)</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Note that this kind of unload is not appropriate under all circumstances. More specifically, it must not be used
	 * when a resource is to be reloaded (lazily or eagerly). In this case proxies are needed for being able to resolve
	 * incoming cross-document references from other resources. However, when the complete ResourceSet is unloaded, or a
	 * self-contained set of resources with no outgoing and incoming cross-document references (which typically happens
	 * when a project or the entire workbench is closed), proxies are not needed and not creating them can reduce memory
	 * consumption quite dramatically.
	 * <p>
	 */
	String OPTION_UNLOAD_MEMORY_OPTIMIZED = "UNLOAD_MEMORY_OPTIMIZED"; //$NON-NLS-1$

	/**
	 * Specifies the target {@link IMetaModelDescriptor metamodel descriptor} identifier for this resource.
	 */
	String OPTION_TARGET_METAMODEL_DESCRIPTOR_ID = "TARGET_METAMODEL_DESCRIPTOR_ID"; //$NON-NLS-1$

	/**
	 * Specifies the maximum number of errors and warnings that are to be converted to problem markers on underlying
	 * {@link IFile} after this resource has been loaded or saved. May be an arbitrary positive integer value or
	 * {@link #OPTION_MAX_PROBLEM_MARKER_COUNT_UNLIMITED}. The default is
	 * <code>{@link #OPTION_MAX_PROBLEM_MARKER_COUNT_DEFAULT}</code>.
	 * <p>
	 * Note that a high number of problem markers being generated for many files may have a negative impact on overall
	 * load and save performance.
	 * <p>
	 * 
	 * @see Resource#getErrors()
	 * @see Resource#getWarnings()
	 */
	String OPTION_MAX_PROBLEM_MARKER_COUNT = "MAX_PROBLEM_MARKER_COUNT"; //$NON-NLS-1$
	Integer OPTION_MAX_PROBLEM_MARKER_COUNT_DEFAULT = 10;
	Integer OPTION_MAX_PROBLEM_MARKER_COUNT_UNLIMITED = -1;

	/**
	 * Specifies the format string to be used for creating problem marker messages for XML well-formedness problems.
	 * Should include substitution location (<code>{0}</code>) where the actual problem message can be inserted. The
	 * default is {@link #OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING_DEFAULT}.
	 */
	String OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING = "XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING"; //$NON-NLS-1$
	String OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING_DEFAULT = Messages.msg_xmlWellformednessProblemFormatString;

	/**
	 * Specifies the format string to be used for creating problem marker messages for XML validity problems. The
	 * default is {@link #OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING_DEFAULT}.
	 */
	String OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING = "XML_VALIDITY_PROBLEM_FORMAT_STRING"; //$NON-NLS-1$
	String OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING_DEFAULT = Messages.msg_xmlValidityProblemFormatString;

	/**
	 * Specifies the severity to be used for reporting XML validity problems. May be one of
	 * {@link IMarker#SEVERITY_ERROR}, {@link IMarker#SEVERITY_WARNING}, {@link IMarker#SEVERITY_INFO} or undefined. The
	 * default is undefined.
	 */
	String OPTION_XML_VALIDITY_PROBLEM_SEVERITY = "XML_VALIDITY_PROBLEM_SEVERITY"; //$NON-NLS-1$

	/**
	 * Returns the map of options that, in addition to the overriding options specified during load, are used to to
	 * control load behavior.
	 */
	Map<Object, Object> getDefaultLoadOptions();

	/**
	 * Returns the map of options that, in addition to the overriding options specified during save, are used to to
	 * control save behavior.
	 */
	Map<Object, Object> getDefaultSaveOptions();

	/**
	 * Returns the map of options that are used to to control the handling of problems encountered while the
	 * {@link Resource resource} has been loaded or saved.
	 */
	Map<Object, Object> getProblemHandlingOptions();

	/**
	 * Improved implementation of org.eclipse.emf.ecore.resource.impl.ResourceImpl#unloaded(InternalEObject) enabling
	 * memory-optimized unloading of {@link Resource resource}s and proxy creation with custom {@link URI} formats
	 * during regular unload.
	 * 
	 * @param internalEObject
	 *            The {@link InternalEObject} that has just been removed from the resource and is to be further
	 *            processed by this method.
	 * @see #OPTION_UNLOAD_MEMORY_OPTIMIZED
	 * @see #getURI(InternalEObject)
	 */
	void unloaded(EObject eObject);

	/**
	 * Returns a {@link URI} representing given {@link InternalEObject}. Clients may implement/override this method when
	 * they require URIs with custom formats to be created.
	 * 
	 * @param InternalEObject
	 *            The {@link InternalEObject} for which the URI is to be created.
	 * @return The URI for given {@link InternalEObject}, or <code>null</code> if no such could be created.
	 */
	URI getURI(EObject eObject);

	/**
	 * Returns a {@link URI} representing given {@link EObject eObject} owned by {@link EObject owner} through provided
	 * {@link EStructuralFeature feature}.If the {@link EObject eObject} is stand-alone (i.e freshly removed and without
	 * attached resource) the {@link URI} is determine using the {@link EObject owner} and the
	 * {@link EStructuralFeature feature}, if the {@link EObject eObject} is still attached to a {@link Resource} the
	 * {@link URI} is calculated using same implementation as in {@link ResourceImpl#unload()} . Clients may
	 * implement/override this method when they require URIs with custom formats to be created.
	 * 
	 * @param oldOwner
	 *            The {@link EObject} owning the {@link EObject} before it was deleted.
	 * @param oldFeature
	 *            The {@link EStructuralFeature} of the owner containing the {@link EObject eObject} before it was
	 *            deleted.
	 * @param eObject
	 *            The {@link EObject} for which the URI is to be created.
	 * @return The URI for given {@link EObject eObject}, or <code>null</code> if no such could be created.If the
	 *         provided {@link EObject} has no {@link Resource eResource} and no {@link EObject owner}, the returned
	 *         value is null.
	 */
	URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject);

	/**
	 * Determines whether or not the given string represents a valid URI.
	 * 
	 * @param uri
	 *            The string to be validated.
	 * @return {@link Diagnostic#OK_INSTANCE} if given string is a valid URI or a {@link Diagnostic} with complementary
	 *         information on error otherwise.
	 */
	Diagnostic validateURI(String uri);
}
