/**
 * <copyright>
 *
 * Copyright (c) 2008-2015 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
 *     itemis - [427461] Add progress monitor to resource load options (useful for loading large models)
 *     itemis - [434954] Hook for overwriting conversion of EMF Diagnostics to IMarkers
 *     itemis - [442342] Sphinx doen't trim context information from proxy URIs when serializing proxyfied cross-document references
 *     itemis - [458862] Navigation from problem markers in Check Validation view to model editors and Model Explorer view broken
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.resource;

import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.scoping.IResourceScope;

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
	String URI_QUERY_FIELD_SEPARATOR = "&"; //$NON-NLS-1$

	/**
	 * Special character separating keys from values within keys/value pairs in the query of an URI.
	 */
	String URI_QUERY_KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

	/**
	 * Special character signaling the start of the fragment of an URI.
	 */
	String URI_FRAGMENT_SEPARATOR = "#"; //$NON-NLS-1$

	// Regular expression in clear text: ([^&^=]+)=?([^&^=]*)
	Pattern URI_QUERY_FIELD_PATTERN = Pattern.compile("([^" + URI_QUERY_FIELD_SEPARATOR + "^" + URI_QUERY_KEY_VALUE_SEPARATOR + "]+)=?([^" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ URI_QUERY_FIELD_SEPARATOR + "^" + URI_QUERY_KEY_VALUE_SEPARATOR + "]*)"); //$NON-NLS-1$ //$NON-NLS-2$
	int URI_QUERY_FIELD_PATTERN_KEY_GROUP_IDX = 1;
	int URI_QUERY_FIELD_PATTERN_VALUE_GROUP_IDX = 2;

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
	 * Specifies a {@link IResourceProblemMarkerFactory resource problem marker factory} that is used to convert the
	 * {@link Resource#getErrors() errors} and {@link Resource#getWarnings() warnings} on this resource to problem
	 * markers on underlying {@link IFile} after this resource has been loaded or saved.
	 *
	 * @see IResourceProblemMarkerFactory
	 * @see Resource#getErrors()
	 * @see Resource#getWarnings()
	 */
	String OPTION_PROBLEM_MARKER_FACTORY = "PROBLEM_MARKER_FACTORY"; //$NON-NLS-1$

	/**
	 * Specifies the maximum number of {@link Resource#getErrors() errors} and {@link Resource#getWarnings() warnings}
	 * on this resource that are to be converted to problem markers on underlying {@link IFile} after this resource has
	 * been loaded or saved. May be an arbitrary positive integer value or
	 * {@link #OPTION_MAX_PROBLEM_MARKER_COUNT_UNLIMITED} . The default is
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
	 * Specifies the {@link IProgressMonitor progress monitor} to be used for monitoring the progress and allow for
	 * cancellation while a {@link Resource resource} is being loaded.
	 */
	String OPTION_PROGRESS_MONITOR = "PROGRESS_MONITOR"; //$NON-NLS-1$

	String OPTION_RECORD_LINE_AND_COLUMN_NUMBERS = "OPTION_RECORD_LINE_AND_COLUMN_NUMBERS"; //$NON-NLS-1$

	String LINE_NUMBER_KEY_NAME = "lineNumber"; //$NON-NLS-1$

	String COLUMN_NUMBER_KEY_NAME = "columnNumber"; //$NON-NLS-1$

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
	 * Returns the map of options that are used to control the handling of problems encountered while the
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
	 * Returns a {@link URI} representing given {@link InternalEObject}. Clients may implement/override this method when
	 * they require URIs with custom formats to be created.
	 *
	 * @param InternalEObject
	 *            The {@link InternalEObject} for which the URI is to be created.
	 * @param resolve
	 *            indicates whether the URI should resolved against the URI of the resource which contains the provided
	 *            model object. This is useful is cases where the native model object URI evaluates in some sort of
	 *            fragment-based URI which does not contain any information about the resource that contains the model
	 *            object (e.g., hb:/#//MyComponent/MyParameterValue). By setting resolve to true, such fragment-based
	 *            URIs will be automatically expanded to a URI that starts with the URI of the model object's resource
	 *            and is followed by the fragment of the model object's native URI (e.g.,
	 *            platform:/resource/MyProject/MyResource/#//MyComponent/MyParameterValue).
	 * @return The URI for given {@link InternalEObject}, or <code>null</code> if no such could be created.
	 */
	URI getURI(EObject eObject, boolean resolve);

	/**
	 * Returns a {@link URI} representing given {@link EObject eObject} owned by {@link EObject owner} through provided
	 * {@link EStructuralFeature feature}.If the {@link EObject eObject} is stand-alone (i.e freshly removed and without
	 * attached resource) the {@link URI} is determine using the {@link EObject owner} and the
	 * {@link EStructuralFeature feature}, if the {@link EObject eObject} is still attached to a {@link Resource} the
	 * {@link URI} is calculated using same implementation as in {@link ResourceImpl#unload()}.
	 * <p>
	 * Clients may implement/override this method when they require URIs with custom formats to be created.
	 * </p>
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
	 * Returns a {@link URI} representing given {@link EObject eObject} owned by {@link EObject owner} through provided
	 * {@link EStructuralFeature feature}.If the {@link EObject eObject} is stand-alone (i.e freshly removed and without
	 * attached resource) the {@link URI} is determine using the {@link EObject owner} and the
	 * {@link EStructuralFeature feature}, if the {@link EObject eObject} is still attached to a {@link Resource} the
	 * {@link URI} is calculated using same implementation as in {@link ResourceImpl#unload()}.
	 * <p>
	 * Clients may implement/override this method when they require URIs with custom formats to be created.
	 * </p>
	 *
	 * @param oldOwner
	 *            The {@link EObject} owning the {@link EObject} before it was deleted.
	 * @param oldFeature
	 *            The {@link EStructuralFeature} of the owner containing the {@link EObject eObject} before it was
	 *            deleted.
	 * @param eObject
	 *            The {@link EObject} for which the URI is to be created.
	 * @param resolve
	 *            indicates whether the URI should resolved against the URI of the resource which contains the provided
	 *            model object. This is useful is cases where the native model object URI evaluates in some sort of
	 *            fragment-based URI which does not contain any information about the resource that contains the model
	 *            object (e.g., hb:/#//MyComponent/MyParameterValue). By setting resolve to true, such fragment-based
	 *            URIs will be automatically expanded to a URI that starts with the URI of the model object's resource
	 *            and is followed by the fragment of the model object's native URI (e.g.,
	 *            platform:/resource/MyProject/MyResource/#//MyComponent/MyParameterValue).
	 * @return The URI for given {@link EObject eObject}, or <code>null</code> if no such could be created.If the
	 *         provided {@link EObject} has no {@link Resource eResource} and no {@link EObject owner}, the returned
	 *         value is null.
	 */
	URI getURI(EObject oldOwner, EStructuralFeature oldFeature, EObject eObject, boolean resolve);

	/**
	 * Creates a {@link URI} from given <code>uriLiteral</code> that refers to an instance of given {@link EClass object
	 * type}. This method is typically called during deserialization of resources when it comes to creating proxy URIs
	 * from serialized representations of cross-document references to objects in other resources.
	 * <p>
	 * Clients may implement/override this method when they require URIs with custom formats to be created
	 * </p>
	 *
	 * @param uriLiteral
	 *            The string representation of the URI to be created.
	 * @param eClass
	 *            The type of object that the URI to be created is supposed to refer to.
	 * @return The URI corresponding to given <code>uriLiteral</code>.
	 */
	URI createURI(String uriLiteral, EClass eClass);

	/**
	 * Returns a {@link URI} representing an HREF to given {@link EObject} stored in underlying {@link Resource}. This
	 * method is typically called during serialization of resources when it comes to creating serializable
	 * representations of cross-document references for objects being referenced from other resources.
	 * <p>
	 * Clients may implement/override this method when they require HREFs with custom formats to be created.
	 * </p>
	 *
	 * @param eObject
	 *            The object for which the HREF URI is to be created.
	 * @return The HREF URI to this object from this resource.
	 */
	URI getHREF(EObject eObject);

	/**
	 * Determines whether or not the given URI string represents a valid URI.
	 *
	 * @param uri
	 *            The URI string to be validated.
	 * @return {@link Diagnostic#OK_INSTANCE} if given URI string is a valid URI or a {@link Diagnostic} with
	 *         complementary information on error otherwise.
	 */
	Diagnostic validateURI(String uri);

	/**
	 * Augments given {@link InternalEObject proxy} to a context-aware proxy by adding key/value pairs that contain the
	 * target {@link IMetaModelDescriptor metamodel descriptor} and a context {@link URI} to the {@link URI#query()
	 * query string} of the proxy URI. Those are required to support the resolution of proxified references between
	 * objects from different metamodels and to honor the {@link IResourceScope resource scope} of the proxy URI when it
	 * is being resolved.
	 *
	 * @param proxy
	 *            The proxy to be handled.
	 * @see #trimProxyContextInfo(URI)
	 */
	void augmentToContextAwareProxy(EObject proxy);

	/**
	 * If given {@link URI proxy URI} contains proxy context-related key/value pairs on its {@link URI#query() query
	 * string}, returns the URI formed by removing those key/value pairs or removing the query string entirely in case
	 * that no other key/value pairs exist; returns given proxy URI unchanged, otherwise.
	 *
	 * @param proxyURI
	 *            The context-aware proxy URI to be handled.
	 * @return The trimmed proxy URI.
	 * @see #augmentToContextAwareProxy(EObject)
	 */
	URI trimProxyContextInfo(URI proxyURI);
}