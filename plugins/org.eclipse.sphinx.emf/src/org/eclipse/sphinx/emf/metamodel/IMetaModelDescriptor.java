/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 *     See4sys - Added pattern for descriptors' label and support for EPackage URIs
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.metamodel;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

/**
 * Describes a meta-model. This can either be an abstract description describing a family of meta-model implementations
 * or a the description of a concrete meta-model version which is backed by an implementation. A description of a
 * concrete meta-model version is expected to be backed by an EMF implementation.
 * <p>
 * Descriptors of concrete meta-model versions from the same family are expected to be derived from the same
 * {@linkplain IMetaModelDescriptor} implementation describing the meta-model family.
 * <p>
 * Implementations of this interface shall be immutable.
 * 
 * @since 0.7.0
 */
public interface IMetaModelDescriptor {

	/**
	 * The string pattern used to create the label of this meta-model descriptor.
	 * 
	 * @since 0.7.0
	 */
	String LABEL_PATTERN = "%1s (%2s)"; //$NON-NLS-1$

	/**
	 * Returns the identifier of the described meta-model. The identifier is used to reference the meta-model. The
	 * identifier must be unique among descriptors available on the platform. It is not intended to be used for user
	 * interaction (see {@link IMetaModelDescriptor#getName()}). The identifier can also be used to reference the
	 * described meta-model from within an extension point.
	 * <p>
	 * Uses a specific <em>identifier</em> instead of <em>namespace URI</em> in order to distinguish available
	 * meta-model descriptors.
	 * <p>
	 * Namespace URI is revision dependent since when the minor version of a release changes, the namespace URI changes.
	 * As a consequence, it is more safe to refer a meta-model descriptor with its identifier (e.g. in a project's
	 * preferences).
	 * 
	 * @return The identifier of the described meta-model.
	 * @since 0.7.0
	 */
	String getIdentifier();

	/**
	 * Returns the name of the described meta-model. The name of the meta-model is a human readable identifier which can
	 * be used for user interaction.
	 * 
	 * @return The name of the described meta-model.
	 * @since 0.7.0
	 */
	String getName();

	/**
	 * Returns the namespace {@link URI} of the described meta-model. The namespace {@link URI} is a unique identifier
	 * used to link the meta-model description to registered EMF artifacts like the root
	 * {@link org.eclipse.emf.ecore.EPackage} and the {@link EFactory}.
	 * 
	 * @return The namespace {@link URI} of the described meta-model.
	 * @since 0.7.0
	 */
	URI getNamespaceURI();

	/**
	 * Returns the namespace (as a string) of the described meta-model. The namespace is a unique identifier used to
	 * link the meta-model description to registered EMF artifacts like the root {@link org.eclipse.emf.ecore.EPackage}
	 * and the {@link EFactory}.
	 * 
	 * @return The namespace of the described meta-model under a string format.
	 * @since 0.7.0
	 */
	String getNamespace();

	/**
	 * Returns the ordinal of the described meta-model. The ordinal is used for sorting IMetaModelDescriptors describing
	 * meta-model versions. If the IMetaModelDescriptor does not describe a concrete version of a meta-model or no
	 * MetaModelVersionData has been provided as there is only one version of the meta-model <code>null</code> is
	 * returned.
	 * 
	 * @return The ordinal of the described meta-model or <code>null</code> if the described meta-model is not a
	 *         concrete meta-model version.
	 * @since 0.7.0
	 */
	int getOrdinal();

	/**
	 * Returns the namespace pattern for the EPackages associated of with the described meta-model. The pattern is used
	 * to resolve the EPackages which are associated with the meta-model described. Any registered EPackage with a
	 * namespace matching the pattern is considered to be associated with the described meta-model.
	 * 
	 * @return The pattern describing all EPackages associated with the meta-model described.
	 * @since 0.7.0
	 */
	String getEPackageNsURIPattern();

	/**
	 * TODO JavaDoc
	 * 
	 * @return
	 */
	Collection<EPackage> getEPackages();

	/**
	 * Returns the top-level {@link org.eclipse.emf.ecore.EPackage} which is associated with the described meta-model.
	 * An EPackage is considered to be the top-level EPackage associated with the described meta-model if it has been
	 * registered to the {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} with the namespace of the
	 * described meta-model.
	 * 
	 * @return The EPackage associated with the described meta-model.
	 * @since 0.7.0
	 * @deprecated Use #getERootPackage() instead.
	 */
	@Deprecated
	EPackage getEPackage();

	/**
	 * Determines if the top-level {@link org.eclipse.emf.ecore.EPackage} associated with the described meta-model is
	 * registered. The {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} is to be consolidated if the
	 * top-level package is actually registered (see also {@link IMetaModelDescriptor#getRootEPackage()}).
	 * 
	 * @return <code>true</true> if the associated top-level EPackage is registered else <code>false</code>;
	 * @since 0.7.0
	 * @deprecated Use #getERootPackage() != null instead.
	 */
	@Deprecated
	boolean isEPackageRegistered();

	/**
	 * Returns the {@link EFactory} which is associated with the described meta-model. An EFactory is considered to be
	 * associated with the described meta-model if the {@link org.eclipse.emf.ecore.EPackage} it belongs to is
	 * associated with the described meta-model (see {@link IMetaModelDescriptor#getERootPackage()}).
	 * 
	 * @return The EFactory associated with the described meta-model.
	 * @since 0.7.0
	 * @deprecated Use #getERootFactory() instead.
	 */
	@Deprecated
	EFactory getEFactory();

	/**
	 * Returns the identifiers of all {@link org.eclipse.core.runtime.content.IContentType content type}s which are
	 * associated with the described meta-model.
	 * 
	 * @return The identifiers of the content types associated with the described meta-model.
	 * @since 0.7.0
	 */
	List<String> getContentTypeIds();

	/**
	 * Returns the identifier of the default {@link org.eclipse.core.runtime.content.IContentType content type} for the
	 * described meta-model.
	 * <p>
	 * The default content type identifier typically is the content type identifier which has been specified in the
	 * generator model options for the {@link #getERootPackage root package} of the described meta-model and is
	 * available on the {@link #getERootPackage root package}'s static #eCONTENT_TYPE field.
	 * </p>
	 * 
	 * @return The identifier of the default content type for the described meta-model, or an empty string if no such is
	 *         available.
	 * @since 0.7.0
	 * @see #getERootPackage()
	 */
	String getDefaultContentTypeId();

	/**
	 * Returns a collection of namespace {@link URI}s which refer to older but all the same compatible versions of the
	 * meta-model described by this {@link IMetaModelDescriptor meta-model descriptor}.
	 * 
	 * @return A list of compatible namespace {@link URI}s.
	 * @see #getNamespaceURI()
	 * @since 0.7.0
	 */
	List<URI> getCompatibleNamespaceURIs();

	/**
	 * Returns a collection of content type ids which are supported by older but all the same compatible versions of the
	 * meta-model described by this {@link IMetaModelDescriptor meta-model descriptor}.
	 * 
	 * @return A list of compatible content type ids.
	 * @see #getContentTypeIds()
	 * @since 0.7.0
	 */
	List<String> getCompatibleContentTypeIds();

	/**
	 * Returns a collection of {@link IMetaModelDescriptor meta-model descriptor}s identifying resource versions which
	 * are older but all the same compatible with the meta-model described by this {@link IMetaModelDescriptor
	 * meta-model descriptor}.
	 * 
	 * @return A list of compatible resource version {@link IMetaModelDescriptor descriptor}s.
	 * @since 0.7.0
	 */
	Collection<IMetaModelDescriptor> getCompatibleResourceVersionDescriptors();
}