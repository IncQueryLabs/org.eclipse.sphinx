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
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
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
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;

/**
 * A set of additional services for EMF {@link Resource resources} including memory-optimized unloading, proxy creation
 * with custom URI formats, and caching of problem marker attributes.
 */
public interface ExtendedResource {

	/**
	 * Special character signaling the end of the scheme of an URI.
	 */
	String URI_SCHEME_SEPARATOR = ":"; //$NON-NLS-1$

	/**
	 * Special character separating individual segments within an URI.
	 */
	String URI_SEGMENT_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * Special character signaling the start of the query of an URI.
	 */
	String URI_QUERY_SEPARATOR = "?"; //$NON-NLS-1$

	/**
	 * Special character separating a keys/value pairs within the query of an URI.
	 */
	String URI_KEY_VALUE_PAIR_SEPARATOR = "&"; //$NON-NLS-1$

	/**
	 * Special character separating keys from values within keys/value pairs in the query of an URI.
	 */
	String URI_KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

	/**
	 * Special character signaling the start of the fragment of an URI.
	 */
	String URI_FRAGMENT_SEPARATOR = "#"; //$NON-NLS-1$

	/**
	 * Used for indicating the {@link IMetaModelDescriptor version} of specified {@link Resource resource}. Can be
	 * either identical with the {@link IMetaModelDescriptor version} of the metamodel behind the resource content or
	 * one of the {@link IMetaModelDescriptor#getCompatibleResourceVersionDescriptors() resource version descriptors
	 * that are compatible with the metamodel version}.
	 */
	String OPTION_RESOURCE_VERSION_DESCRIPTOR = "RESOURCE_VERSION_DESCRIPTOR"; //$NON-NLS-1$

	/**
	 * Specifies whether to use context-aware proxy URIs when creating proxy objects or not. The default of this option
	 * is <code>Boolean.TRUE</code> .
	 */
	String OPTION_USE_CONTEXT_AWARE_PROXY_URIS = "USE_CONTEXT_AWARE_PROXY_URIS"; //$NON-NLS-1$

	/**
	 * Specifies the target {@link IMetaModelDescriptor metamodel descriptor} identifier for this resource.
	 */
	String OPTION_TARGET_METAMODEL_DESCRIPTOR_ID = "TARGET_METAMODEL_DESCRIPTOR_ID"; //$NON-NLS-1$

	/**
	 * Specifies a string to string map with namespace and system identifier pairs that are allowed to be written in the
	 * resource's xsi:schemaLocation/xsi:noNamespaceSchemaLocation during saving. Implies that
	 * {@link XMLResource#OPTION_SCHEMA_LOCATION} is enabled and requires that {@link ExtendedXMLSaveImpl} or
	 * {@link ExtendedXMISaveImpl}, or a subtype of them, is used as {@link XMLResourceImpl#createXMLSave() serializer}
	 * for this resource.
	 * 
	 * @see XMLResource#OPTION_SCHEMA_LOCATION
	 * @see XMLResourceImpl#createXMLSave()
	 * @see ExtendedXMLSaveImpl#addNamespaceDeclarations()
	 * @see ExtendedXMISaveImpl
	 */
	String OPTION_SCHEMA_LOCATION_CATALOG = "SCHEMA_LOCATION_CATALOG"; //$NON-NLS-1$

	/**
	 * Specifies weather the resource should be validated with a schema during loading. Requires that
	 * {@link ExtendedXMLLoadImpl} or {@link ExtendedXMILoadImpl}, or a subtype of them, is used as
	 * {@link XMLResourceImpl#createXMLLoad() loader} for this resource. The default of this option is
	 * <code>Boolean.FALSE</code>.
	 * <p>
	 * The schema used for validation is expected to be defined by a xsi:schemaLocation or xsi:noNamespaceSchemaLocation
	 * attribute on the resource's root element. The default strategy for retrieving the schema is to resolve the schema
	 * location system identifier relative to the resource's URI. Other resolution strategies can be supported by
	 * providing a {@link SchemaLocationURIHandler} as {@link XMLResource#OPTION_URI_HANDLER} as load option. In the
	 * latter case it is recommended to provide an adequately initialized {@link #OPTION_RESOURCE_VERSION_DESCRIPTOR}
	 * along with that. It enables the {@link SchemaLocationURIHandler} to resolve unknown schema location system
	 * identifiers by falling back to a known system identifier corresponding to the resource's
	 * {@link IMetaModelDescriptor#getNamespace() namespace}.
	 * </p>
	 * 
	 * @see XMLResource#OPTION_URI_HANDLER
	 * @see SchemaLocationURIHandler
	 * @see XMLResourceImpl#createXMLLoad()
	 * @see ExtendedXMLLoadImpl
	 * @see ExtendedXMILoadImpl
	 */
	String OPTION_ENABLE_SCHEMA_VALIDATION = "ENABLE_SCHEMA_VALIDATION"; //$NON-NLS-1$

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
	 * Specifies whether unloading of this resource is to be performed in a limited but memory-optimized way. Requires
	 * that the resource's {@link ResourceImpl#unloaded(InternalEObject) unloaded(InternalEObject)} method is overridden
	 * and delegates to {@link ExtendedResourceAdapter#unloaded(EObject)}. The default of this option is
	 * <code>Boolean.FALSE</code>.
	 * <p>
	 * This option involves the following behavioral modifications wrt to regular
	 * {@link ResourceImpl#unloaded(InternalEObject) unload strategy}:
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
	 * 
	 * @see ResourceImpl#unloaded()
	 */
	String OPTION_UNLOAD_MEMORY_OPTIMIZED = "UNLOAD_MEMORY_OPTIMIZED"; //$NON-NLS-1$

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

	/**
	 * Augments given {@link InternalEObject proxy} to a context-aware proxy by adding key/value pairs that contain the
	 * target {@link IMetaModelDescriptor metamodel descriptor} and a context {@link URI} to the {@link URI#query()
	 * query string} of the proxy URI.
	 */
	void augmentToContextAwareProxy(EObject proxy);
}
