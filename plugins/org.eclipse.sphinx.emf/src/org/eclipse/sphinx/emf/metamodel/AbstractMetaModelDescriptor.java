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
 *     See4sys - Added support for EPackage URIs and inheritance of descriptors
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.metamodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.sphinx.platform.util.ReflectUtil;

/**
 * A base implementation for meta-model descriptors. Provides default implementations for all methods specified by the
 * <code>IMetaModelDescriptor</code> interface.
 * <p>
 * Extends {@linkplain PlatformObject} in order to be adaptable by Eclipse adapters mechanism.
 * 
 * @see IMetaModelDescriptor
 * @since 0.7.0
 */
public abstract class AbstractMetaModelDescriptor extends PlatformObject implements IMetaModelDescriptor {

	protected static final String URI_SEGMENT_SEPARATOR = "/"; //$NON-NLS-1$

	private String fIdentifier;
	protected URI fBaseNamespaceURI;
	private URI fNamespaceURI;
	private String fEPackageNsURIPostfixPattern;
	private MetaModelVersionData fVersionData;
	private String fEPackageNsURIPattern;
	private EPackage fRootEPackage = null;
	private Collection<EPackage> fEPackages = null;
	private List<String> fContentTypeIds;

	private Registry fEPkgRegistry;

	public Registry getEPackageRegistry() {
		if (fEPkgRegistry == null) {
			fEPkgRegistry = EPackage.Registry.INSTANCE;
		}
		return fEPkgRegistry;
	}

	// This method is only needed for testing the MetaModelDescriptor class
	protected void setEPackageRegistry(EPackage.Registry ePkgRegistry) {
		fEPkgRegistry = ePkgRegistry;
	}

	/**
	 * Creates a descriptor for a meta-model. This constructor is to be used if the descriptor shall describe an
	 * abstract meta-model family (without an implementation backing it) or a meta-model (backed by an implementation)
	 * where only one version exists. The {@link AbstractMetaModelDescriptor#getRootEPackage()} root EPackage should not
	 * have any Sub-packages.
	 * 
	 * @param identifier
	 *            The identifier of the described meta-model. A unique identifier used for referencing the meta-model
	 *            from within an extension point.
	 * @param namespace
	 *            The namespace URI of the meta-model described.
	 */
	protected AbstractMetaModelDescriptor(String identifier, String namespace) {
		this(identifier, namespace, (MetaModelVersionData) null);
	}

	/**
	 * Creates a descriptor for a meta-model. This constructor is to be used if the descriptor shall describe a
	 * meta-model (backed by an implementation) where only one version exists. The
	 * {@link AbstractMetaModelDescriptor#getRootEPackage()} root EPackage can have Sub-packages.
	 * 
	 * @param identifier
	 *            The identifier of the described meta-model. A unique identifier used for referencing the meta-model
	 *            from within an extension point.
	 * @param baseNamespace
	 *            Usually the namespace of the root EPackage.
	 * @param ePackageNsURIPostfixPattern
	 *            A regular expression when appended to the baseNamespace will match the namespace of the meta-model
	 *            sub-packages.
	 * @see #getRootEPackage()
	 */
	protected AbstractMetaModelDescriptor(String identifier, String baseNamespace, String ePackageNsURIPostfixPattern) {
		Assert.isNotNull(identifier);
		Assert.isNotNull(baseNamespace);

		fIdentifier = identifier;
		try {
			fBaseNamespaceURI = new URI(baseNamespace);
		} catch (URISyntaxException ex) {
			throw new WrappedException(ex);
		}
		fEPackageNsURIPostfixPattern = ePackageNsURIPostfixPattern;
		initNamespace(); // Not calculated lazily in order to detect malformed nsPostfixes
	}

	/**
	 * Creates a descriptor for a concrete version of a meta-model. The constructor is to be used if the descriptor
	 * shall describe a concrete version of a meta-model (backed by an implementation).
	 * 
	 * @param identifier
	 *            The identifier of the described meta-model. A unique identifier used for referencing the meta-model
	 *            from within an extension point.
	 * @param baseNamespace
	 *            The (base) namespace URI of the meta-model described. Is equal to the full namespace URI if no version
	 *            data is provided.
	 * @param versionData
	 *            Data describing the meta-model version details.
	 */
	protected AbstractMetaModelDescriptor(String identifier, String baseNamespace, MetaModelVersionData versionData) {
		Assert.isNotNull(identifier);
		Assert.isNotNull(baseNamespace);

		fIdentifier = identifier;
		try {
			fBaseNamespaceURI = new URI(baseNamespace);
		} catch (URISyntaxException ex) {
			throw new WrappedException(ex);
		}
		fVersionData = versionData;
		initNamespace(); // Not calculated lazily in order to detect malformed nsPostfixes
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getIdentifier()
	 */
	public String getIdentifier() {
		return fIdentifier;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getName()
	 */
	public String getName() {
		if (fVersionData != null) {
			return fVersionData.getName();
		}

		EPackage rootPackage = getRootEPackage();
		if (rootPackage != null) {
			return rootPackage.getName();
		}

		return getIdentifier();
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getNamespaceURI()
	 */
	public URI getNamespaceURI() {
		return fNamespaceURI;
	}

	/**
	 * Initializes the meta-model's namespace.
	 */
	protected void initNamespace() {
		StringBuilder namespace = new StringBuilder(fBaseNamespaceURI.toString());
		if (fVersionData != null && fVersionData.getNsPostfix() != null && fVersionData.getNsPostfix().length() > 0) {
			namespace.append(URI_SEGMENT_SEPARATOR);
			namespace.append(fVersionData.getNsPostfix());
		}
		try {
			fNamespaceURI = new URI(namespace.toString());
		} catch (URISyntaxException ex) {
			throw new WrappedException(ex);
		}
	}

	protected URI getBaseNamespaceURI() {
		return fBaseNamespaceURI;
	}

	protected String getNsPostfix() {
		if (fVersionData != null) {
			return fVersionData.getNsPostfix();
		}
		return null;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getNamespace()
	 */
	public String getNamespace() {
		return getNamespaceURI().toString();
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getOrdinal()
	 */
	public int getOrdinal() {
		if (fVersionData != null) {
			return fVersionData.getOrdinal();
		}
		return -1;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getEPackageNsURIPattern()
	 */
	public String getEPackageNsURIPattern() {
		if (fEPackageNsURIPattern == null) {
			initEPackageNsURIPattern();
		}
		return fEPackageNsURIPattern;
	}

	/**
	 * Initializes the URI pattern used for determining if an EPackage belongs to the meta-model or not.
	 */
	protected void initEPackageNsURIPattern() {
		StringBuilder buffer = new StringBuilder(fBaseNamespaceURI.toString());
		if (fEPackageNsURIPostfixPattern != null) {
			buffer.append(URI_SEGMENT_SEPARATOR);
			buffer.append(fEPackageNsURIPostfixPattern);
		} else if (fVersionData != null) {
			buffer.append(URI_SEGMENT_SEPARATOR);
			buffer.append(fVersionData.getEPackageNsURIPostfixPattern());
		}
		fEPackageNsURIPattern = buffer.toString();
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getEPackages()
	 */
	public Collection<EPackage> getEPackages() {
		synchronized (getEPackageRegistry()) {
			if (fEPackages == null) {
				Set<EPackage> ePackages = new HashSet<EPackage>();
				Set<String> safeNsURIs = new HashSet<String>(getEPackageRegistry().keySet());
				for (String nsURI : safeNsURIs) {
					if (nsURI.matches(getEPackageNsURIPattern())) {
						ePackages.add(getEPackageRegistry().getEPackage(nsURI));
					}
				}
				fEPackages = Collections.unmodifiableSet(ePackages);
			}
		}
		return fEPackages;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getRootEPackage()
	 */
	public EPackage getRootEPackage() {
		synchronized (getEPackageRegistry()) {
			if (fRootEPackage == null) {
				fRootEPackage = getEPackageRegistry().getEPackage(getNamespace());
				if (fRootEPackage == null) {
					// FIXME Consider to remove this part. In case that an older metamodel version has had a root
					// EPackage
					// but the latest metamodel version doesn't proceeding this way cannot be possibly a good thing
					// Refer to compatible namespaces URI if no package can be found under native namespace URI
					for (URI compatibleNamespaceURI : getCompatibleNamespaceURIs()) {
						fRootEPackage = getEPackageRegistry().getEPackage(compatibleNamespaceURI.toString());
						if (fRootEPackage != null) {
							break;
						}
					}
				}
			}
		}
		return fRootEPackage;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getEPackage()
	 */
	@Deprecated
	public EPackage getEPackage() {
		return getRootEPackage();
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#isEPackageRegistered()
	 */
	@Deprecated
	public boolean isEPackageRegistered() {
		return getRootEPackage() != null;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getRootEFactory()
	 */
	public EFactory getRootEFactory() {
		EPackage ePackage = getRootEPackage();
		if (ePackage != null) {
			return ePackage.getEFactoryInstance();
		}
		return null;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor#getEFactory()
	 */
	@Deprecated
	public EFactory getEFactory() {
		return getRootEFactory();
	}

	public List<String> getContentTypeIds() {
		if (fContentTypeIds == null) {
			initContentTypeIds();
		}
		return Collections.unmodifiableList(fContentTypeIds);
	}

	protected void initContentTypeIds() {
		/*
		 * !! Important Note !! Initialize content type ids in a safe copy but not directly in the resulting list field
		 * to avoid that when multiple threads pass through this method some of these threads are already operating on
		 * the returned content type ids while others are still initializing them.
		 */
		List<String> safeContentTypeIds = new ArrayList<String>(1);
		String defaultContentTypeId = getDefaultContentTypeId();
		if (defaultContentTypeId != null && defaultContentTypeId.length() > 0) {
			safeContentTypeIds.add(defaultContentTypeId);
		}
		fContentTypeIds = safeContentTypeIds;
	}

	public String getDefaultContentTypeId() {
		return getRootEPackageContentTypeId();
	}

	protected String getRootEPackageContentTypeId() {
		try {
			EPackage rootPackage = getRootEPackage();
			if (rootPackage != null) {
				Object contentTypeId = ReflectUtil.getFieldValue(rootPackage, "eCONTENT_TYPE"); //$NON-NLS-1$;
				if (contentTypeId instanceof String) {
					return (String) contentTypeId;
				}
			}
		} catch (Exception ex) {
			// Ignore exception
		}
		return ""; //$NON-NLS-1$
	}

	public List<String> getCompatibleContentTypeIds() {
		Set<String> compatibleContentTypeIds = new HashSet<String>();
		for (IMetaModelDescriptor resourceVersionDescriptor : getCompatibleResourceVersionDescriptors()) {
			if (resourceVersionDescriptor != this) {
				compatibleContentTypeIds.addAll(resourceVersionDescriptor.getContentTypeIds());
				compatibleContentTypeIds.addAll(resourceVersionDescriptor.getCompatibleContentTypeIds());
			}
		}
		return Collections.unmodifiableList(new ArrayList<String>(compatibleContentTypeIds));
	}

	public List<URI> getCompatibleNamespaceURIs() {
		List<URI> compatibleNamespaceURIs = new ArrayList<URI>();
		for (IMetaModelDescriptor resourceVersionDescriptor : getCompatibleResourceVersionDescriptors()) {
			if (resourceVersionDescriptor != this) {
				compatibleNamespaceURIs.add(resourceVersionDescriptor.getNamespaceURI());
			}
		}
		return Collections.unmodifiableList(compatibleNamespaceURIs);
	}

	public Collection<IMetaModelDescriptor> getCompatibleResourceVersionDescriptors() {
		return Collections.emptyList();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractMetaModelDescriptor) {
			AbstractMetaModelDescriptor otherRelease = (AbstractMetaModelDescriptor) obj;
			return getIdentifier().equals(otherRelease.getIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fIdentifier.hashCode();
	}

	@Override
	public String toString() {
		return getIdentifier();
	}
}