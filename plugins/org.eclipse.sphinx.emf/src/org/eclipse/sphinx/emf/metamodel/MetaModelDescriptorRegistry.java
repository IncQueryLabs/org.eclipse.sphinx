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
 *     See4sys - Added support for EPackage URIs
 *     BMW Car IT - Added robustness and support for singleton instantiation of descriptors
 *     See4sys - Added facilities for retrieving descriptor(s) from identifier, name, ordinal, object, etc.
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.metamodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.content.ContentType;
import org.eclipse.core.internal.content.ContentTypeHandler;
import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.impl.RootXMLContentHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.RootXMLContentHandlerImpl.Describer;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.internal.metamodel.IFileMetaModelDescriptorCache;
import org.eclipse.sphinx.emf.scoping.ResourceScopeProviderRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.ReflectUtil;

/**
 * 
 */
@SuppressWarnings("restriction")
public class MetaModelDescriptorRegistry implements IAdaptable {

	/**
	 * The singleton instance of this registry.
	 */
	public static final MetaModelDescriptorRegistry INSTANCE = new MetaModelDescriptorRegistry();

	/**
	 * A default meta-model descriptor for any type of meta model.
	 */
	public static final IMetaModelDescriptor ANY_MM = new AnyMetaModelDescriptor();

	/**
	 * A default meta-model descriptor for no meta model.
	 */
	public static final IMetaModelDescriptor NO_MM = new NoMetaModelDescriptor();

	private static final String EXTP_META_MODEL_DESCRIPTORS = "org.eclipse.sphinx.emf.metaModelDescriptors"; //$NON-NLS-1$
	private static final String NODE_DESCRIPTOR = "descriptor"; //$NON-NLS-1$
	private static final String NODE_TARGET_DESCRIPTOR = "targetDescriptorProvider";//$NON-NLS-1$
	private static final String NODE_CONTENT_TYPE = "contentType";//$NON-NLS-1$
	private static final String NODE_FILE_TYPE = "fileType";//$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_EXTENSION = "extension";//$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_OVERRIDE = "override";//$NON-NLS-1$
	/**
	 * The extension registry.
	 */
	private IExtensionRegistry fExtensionRegistry;

	/**
	 * The contributed meta-model descriptors.
	 */
	private Map<String, IMetaModelDescriptor> fMetaModelDescriptors = Collections.synchronizedMap(new HashMap<String, IMetaModelDescriptor>());

	/**
	 * The contributed target meta-model descriptor providers.
	 */
	private Map<String, ITargetMetaModelDescriptorProvider> fContentTypeIdToTargetMetaModelDescriptorProviderIds = new HashMap<String, ITargetMetaModelDescriptorProvider>();

	private Map<String, ITargetMetaModelDescriptorProvider> fFileExtensionToTargetMetaModelDescriptorProviderIds = new HashMap<String, ITargetMetaModelDescriptorProvider>();

	private Map<String, ITargetMetaModelDescriptorProvider> fAllTargetMetaModelDescriptorProviders = new HashMap<String, ITargetMetaModelDescriptorProvider>();

	private FileMetaModelDescriptorCache fFileMetaModelDescriptorCache = new FileMetaModelDescriptorCache();

	private Map<String, String> fNamespaceContentTypeIdCache = new HashMap<String, String>();

	/**
	 * Private constructor for the singleton pattern.
	 * <p>
	 * Asks for the synchronized reading of contributions to <em>Meta-Model Descriptors</em> extension point.
	 */
	private MetaModelDescriptorRegistry() {
		readContributedDescriptors();
		readContributedTargetMetaModelDescriptorProviders();
	}

	private IExtensionRegistry getExtensionRegistry() {
		if (fExtensionRegistry == null) {
			fExtensionRegistry = Platform.getExtensionRegistry();
		}
		return fExtensionRegistry;
	}

	// FIXME Should be entirely removed as soon as integration tests will be available.
	// Only used for testing
	public void setExtensionRegistry(IExtensionRegistry extensionRegistry) {
		fExtensionRegistry = extensionRegistry;
		fMetaModelDescriptors.clear();
		readContributedDescriptors();
	}

	/**
	 * Reads contributions to <em>Meta-Model Descriptor</em> extension point.
	 * <p>
	 * <table>
	 * <tr valign=top>
	 * <td><b>Note</b>&nbsp;&nbsp;</td>
	 * <td>It is recommended to call this method inside a block <tt><b>synchronized</b></tt> on the encapsulated
	 * <code>fMetaModelDescriptors</code> field in order to avoid inconsistencies in registered meta-model
	 * {@linkplain IMetaModelDescriptor descriptor}s in case of concurrent read/adds.</td>
	 * </tr>
	 * </table>
	 */
	private void readContributedDescriptors() {
		IExtensionRegistry extensionRegistry = getExtensionRegistry();
		if (extensionRegistry != null) {
			IExtension[] extensions = extensionRegistry.getExtensionPoint(EXTP_META_MODEL_DESCRIPTORS).getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] configElements = extension.getConfigurationElements();
				for (IConfigurationElement configElement : configElements) {
					try {
						if (NODE_DESCRIPTOR.equals(configElement.getName())) {
							String id = configElement.getAttribute(ATTR_ID);
							IMetaModelDescriptor mmDescriptor = null;
							try {
								String className = configElement.getAttribute(ATTR_CLASS);
								Class<?> clazz = Platform.getBundle(configElement.getContributor().getName()).loadClass(className);
								mmDescriptor = (IMetaModelDescriptor) ReflectUtil.getFieldValue(clazz, "INSTANCE"); //$NON-NLS-1$
							} catch (ClassNotFoundException e) {
								PlatformLogUtil.logAsError(Activator.getPlugin(), e);
							} catch (IllegalAccessException e) {
								PlatformLogUtil.logAsError(Activator.getPlugin(), e);
							} catch (NoSuchFieldException noSuchFieldEx) {
								PlatformLogUtil.logAsInfo(Activator.getPlugin(), noSuchFieldEx);
								mmDescriptor = (IMetaModelDescriptor) configElement.createExecutableExtension(ATTR_CLASS);
							}
							if (!id.equals(mmDescriptor.getIdentifier())) {
								throw new RuntimeException(NLS.bind(Messages.error_mmDescriptorIdentifierNotEqual, id, mmDescriptor.getIdentifier()));
							}
							addDescriptor(mmDescriptor);
						}
					} catch (Exception ex) {
						PlatformLogUtil.logAsError(Activator.getDefault(), ex);
					}
				}
			}
		}
	}

	protected IMetaModelDescriptor createDescriptor(EPackage ePackage) {
		Assert.isNotNull(ePackage);

		String ePackageClassName = ePackage.getClass().getName();
		String id = ePackageClassName.substring(0, ePackageClassName.lastIndexOf(".")); //$NON-NLS-1$
		if (id.endsWith(".impl") || id.endsWith(".util")) {//$NON-NLS-1$ //$NON-NLS-2$ 
			id = id.substring(0, id.lastIndexOf(".")); //$NON-NLS-1$
		}

		return new DefaultMetaModelDescriptor(id, ePackage.getNsURI(), ePackage.getName());
	}

	/**
	 * Add the specified {@link IMetaModelDescriptor mmDescriptor} to this registry (if not already added).
	 * 
	 * @param mmDescriptor
	 *            The meta-model {@linkplain IMetaModelDescriptor descriptor} to add to this registry.
	 */
	public void addDescriptor(IMetaModelDescriptor mmDescriptor) {
		if (mmDescriptor != null && !mmDescriptor.equals(ANY_MM) && !mmDescriptor.equals(NO_MM)) {
			String id = mmDescriptor.getIdentifier();
			if (id == null) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(),
						new RuntimeException(NLS.bind(Messages.warning_mmDescriptorHasNoIdentifier, mmDescriptor.getName())));
			}
			if (fMetaModelDescriptors.containsKey(id)) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(),
						new RuntimeException(NLS.bind(Messages.warning_mmDescriptorIdentifierNotUnique, id)));
			}
			fMetaModelDescriptors.put(id, mmDescriptor);
		}
	}

	/**
	 * Reads contributions to <em>Meta-Model Descriptor/TargetMetaModelDescriptorProvider</em> extension point.
	 */
	private void readContributedTargetMetaModelDescriptorProviders() {
		IExtensionRegistry extensionRegistry = getExtensionRegistry();
		if (extensionRegistry != null) {
			Set<String> overriddenIds = new HashSet<String>();
			IExtension[] extensions = extensionRegistry.getExtensionPoint(EXTP_META_MODEL_DESCRIPTORS).getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] configElements = extension.getConfigurationElements();
				overriddenIds.addAll(getOverriddenTargetMetaModelDescriptorProviderIds(configElements));
			}
			for (IExtension extension : extensions) {
				IConfigurationElement[] configElements = extension.getConfigurationElements();
				readContributedTargetMetaModelDescriptorProviders(configElements, overriddenIds);
			}
		}
	}

	private void readContributedTargetMetaModelDescriptorProviders(IConfigurationElement[] configElements, Set<String> overriddenIds) {
		Assert.isNotNull(configElements);
		Assert.isNotNull(overriddenIds);

		for (IConfigurationElement configElement : configElements) {
			try {
				if (NODE_TARGET_DESCRIPTOR.equals(configElement.getName())) {
					String id = configElement.getAttribute(ATTR_ID);
					if (!overriddenIds.contains(id)) {
						ITargetMetaModelDescriptorProvider provider = (ITargetMetaModelDescriptorProvider) configElement
								.createExecutableExtension(ATTR_CLASS);
						if (addTargetDescriptorProvider(id, provider)) {
							for (IConfigurationElement childConfigElement : configElement.getChildren()) {
								if (NODE_CONTENT_TYPE.equals(childConfigElement.getName())) {
									String contenTypeId = childConfigElement.getAttribute(ATTR_ID);
									addTargetDescriptorProviderForContentTypeId(contenTypeId, provider);
								} else if (NODE_FILE_TYPE.equals(childConfigElement.getName())) {
									String fileExtension = childConfigElement.getAttribute(ATTR_EXTENSION);
									addTargetDescriptorProviderForFileExtension(fileExtension, provider);
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			}
		}
	}

	private Set<String> getOverriddenTargetMetaModelDescriptorProviderIds(IConfigurationElement[] configElements) {
		Assert.isNotNull(configElements);

		Set<String> overriddenIds = new HashSet<String>();
		for (IConfigurationElement configElement : configElements) {
			if (NODE_TARGET_DESCRIPTOR.equals(configElement.getName())) {
				String overriddenTargetMetaModelDescriptorId = configElement.getAttribute(ATTR_OVERRIDE);
				if (overriddenTargetMetaModelDescriptorId != null) {
					if (!overriddenIds.contains(overriddenTargetMetaModelDescriptorId)) {
						overriddenIds.add(overriddenTargetMetaModelDescriptorId);
					} else {
						PlatformLogUtil.logAsWarning(
								Activator.getPlugin(),
								new RuntimeException(NLS.bind(Messages.warning_multipleTargetMetaModelDescriptorProvidersOverride,
										overriddenTargetMetaModelDescriptorId)));
					}
				}
			}
		}
		return overriddenIds;
	}

	private boolean addTargetDescriptorProvider(String id, ITargetMetaModelDescriptorProvider targetMetaModelDescriptorProvider) {
		Assert.isNotNull(targetMetaModelDescriptorProvider);

		if (id == null) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(), new RuntimeException(Messages.warning_targetMetaModelDescriptorProviderWithoutId));
			return false;
		}
		if (fAllTargetMetaModelDescriptorProviders.containsKey(id)) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(),
					new RuntimeException(NLS.bind(Messages.warning_targetMetaModelDescriptorProviderIdNotUnique, id)));
			return false;
		}

		fAllTargetMetaModelDescriptorProviders.put(id, targetMetaModelDescriptorProvider);
		return true;
	}

	private void addTargetDescriptorProviderForFileExtension(String fileExtension,
			ITargetMetaModelDescriptorProvider targetMetaModelDescriptorProvider) {
		Assert.isNotNull(targetMetaModelDescriptorProvider);
		Assert.isLegal(fAllTargetMetaModelDescriptorProviders.containsValue(targetMetaModelDescriptorProvider));

		if (fileExtension == null) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(),
					new RuntimeException(NLS.bind(Messages.warning_fileExtensionForTargetMetaModelDescriptorProviderMustNotBeNull, fileExtension)));
		}
		if (fFileExtensionToTargetMetaModelDescriptorProviderIds.containsKey(fileExtension)) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(),
					new RuntimeException(NLS.bind(Messages.warning_fileExtensionForTargetMetaModelDescriptorProviderNotUnique, fileExtension)));
		}

		fFileExtensionToTargetMetaModelDescriptorProviderIds.put(fileExtension, targetMetaModelDescriptorProvider);
	}

	private void addTargetDescriptorProviderForContentTypeId(String contentTypeId,
			ITargetMetaModelDescriptorProvider targetMetaModelDescriptorProvider) {
		Assert.isNotNull(targetMetaModelDescriptorProvider);
		Assert.isLegal(fAllTargetMetaModelDescriptorProviders.containsValue(targetMetaModelDescriptorProvider));

		if (contentTypeId == null) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(),
					new RuntimeException(NLS.bind(Messages.warning_contentTypeIdForTargetMetaModelDescriptorProviderMustNotBeNull, contentTypeId)));

		}
		if (fContentTypeIdToTargetMetaModelDescriptorProviderIds.containsKey(contentTypeId)) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(),
					new RuntimeException(NLS.bind(Messages.warning_contentTypeIdForTargetMetaModelDescriptorProviderNotUnique, contentTypeId)));
		}

		fContentTypeIdToTargetMetaModelDescriptorProviderIds.put(contentTypeId, targetMetaModelDescriptorProvider);
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType.equals(IFileMetaModelDescriptorCache.class)) {
			return fFileMetaModelDescriptorCache;
		}
		return null;
	}

	/**
	 * @param mmDescriptor
	 * @return
	 */
	public List<IMetaModelDescriptor> getResolvedDescriptors(IMetaModelDescriptor mmDescriptor) {
		List<IMetaModelDescriptor> resolvedDescriptors = new ArrayList<IMetaModelDescriptor>();
		for (IMetaModelDescriptor descriptor : getDescriptors(mmDescriptor)) {
			if (descriptor.getRootEPackage() != null) {
				resolvedDescriptors.add(descriptor);
			}
		}
		return resolvedDescriptors;
	}

	public IMetaModelDescriptor getDescriptor(Object object) {
		if (object instanceof String) {
			return getDescriptor((String) object);
		} else if (object instanceof URI) {
			return getDescriptor((URI) object);
		} else if (object instanceof IFile) {
			return getDescriptor((IFile) object);
		} else if (object instanceof Resource) {
			return getDescriptor((Resource) object);
		} else if (object instanceof EObject) {
			return getDescriptor((EObject) object);
		} else if (object instanceof IWrapperItemProvider) {
			return getDescriptor((IWrapperItemProvider) object);
		} else if (object instanceof FeatureMap.Entry) {
			return getDescriptor((FeatureMap.Entry) object);
		} else if (object instanceof TransientItemProvider) {
			return getDescriptor((TransientItemProvider) object);
		} else if (object instanceof EClass) {
			return getDescriptor((EClass) object);
		} else if (object instanceof EPackage) {
			return getDescriptor((EPackage) object);
		}
		return null;
	}

	/**
	 * Used in UI component to return a list of sorted descriptors.
	 * 
	 * @param mmDescriptor
	 * @return
	 * @since 0.7.0
	 */
	public <T extends IMetaModelDescriptor> List<T> getDescriptors(T mmDescriptor, boolean sorted) {
		List<T> descriptors = new ArrayList<T>();
		if (mmDescriptor != null) {
			synchronized (fMetaModelDescriptors) {
				for (IMetaModelDescriptor descriptor : fMetaModelDescriptors.values()) {
					if (mmDescriptor == ANY_MM || mmDescriptor.getClass().isInstance(descriptor)) {
						@SuppressWarnings("unchecked")
						T desc = (T) descriptor;
						descriptors.add(desc);
					}
				}
			}
		}
		if (sorted) {
			Collections.sort(descriptors, new Comparator<T>() {
				public int compare(IMetaModelDescriptor mmd1, IMetaModelDescriptor mmd2) {
					String label1 = String.format(IMetaModelDescriptor.LABEL_PATTERN, mmd1.getName(), mmd1.getNamespace());
					String label2 = String.format(IMetaModelDescriptor.LABEL_PATTERN, mmd2.getName(), mmd2.getNamespace());
					return label1.compareTo(label2);
				}
			});
		}
		return descriptors;
	}

	/**
	 * @param mmDescriptor
	 * @return
	 */
	public <T extends IMetaModelDescriptor> List<T> getDescriptors(T mmDescriptor) {
		return getDescriptors(mmDescriptor, false);
	}

	/**
	 * @param identifier
	 *            A meta-model descriptor identifier.
	 * @return The meta-model descriptor contributed with the specified identifier.
	 */
	public IMetaModelDescriptor getDescriptor(String identifier) {
		if (ANY_MM.getIdentifier().equals(identifier)) {
			return ANY_MM;
		}
		return fMetaModelDescriptors.get(identifier);
	}

	/**
	 * @param idPattern
	 *            A regular expression which the identifiers of the returned meta-model descriptors must match.
	 * @return Meta-model descriptors whose identifier matches the specified regular expression.
	 */
	public List<IMetaModelDescriptor> getDescriptors(String idPattern) {
		if (ANY_MM.getIdentifier().equals(idPattern)) {
			return Collections.singletonList(ANY_MM);
		}

		List<IMetaModelDescriptor> mmDescriptors = new ArrayList<IMetaModelDescriptor>();
		if (idPattern != null) {
			synchronized (fMetaModelDescriptors) {
				for (String identifier : fMetaModelDescriptors.keySet()) {
					if (identifier.matches(idPattern)) {
						mmDescriptors.add(fMetaModelDescriptors.get(identifier));
					}
				}
			}
		}
		return mmDescriptors;
	}

	/**
	 * @param mmDescriptor
	 * @param ordinal
	 * @return
	 */
	public <T extends IMetaModelDescriptor> T getDescriptor(T mmDescriptor, final int ordinal) {
		return getDescriptor(mmDescriptor, new IDescriptorFilter() {
			public boolean accept(IMetaModelDescriptor descriptor) {
				return descriptor.getOrdinal() == ordinal;
			}
		});
	}

	/**
	 * @param mmDescriptor
	 * @param name
	 * @return
	 */
	public <T extends IMetaModelDescriptor> T getDescriptor(T mmDescriptor, final String name) {
		return getDescriptor(mmDescriptor, new IDescriptorFilter() {
			public boolean accept(IMetaModelDescriptor descriptor) {
				return descriptor.getName().equals(name);
			}
		});
	}

	/**
	 * In order to obtain the meta-model descriptor for the given resource, this method first retrieves the namespace
	 * URI specified in the header of this resource; then, the effective retrieval of the meta-model descriptor from
	 * that namespace URI is delegated to the right method {@link #getDescriptor(URI)}.
	 * 
	 * @see MetaModelDescriptorRegistry#getDescriptor(URI)
	 * @param resource
	 *            The resource whose meta-model descriptor must be returned.
	 * @return The meta-model descriptor of the specified resource.
	 */
	public IMetaModelDescriptor getDescriptor(final Resource resource) {
		/*
		 * Performance optimization: Theoretically we could just all the time rely on the id of the content type behind
		 * given file and retrieve the metamodel descriptor from there. However, keeping in mind that content type
		 * detection is an incredibly slow affair we must not do that but proceed in the following order: For loaded
		 * resources the fastest option is to retrieve the metamodel descriptor from the nsURI of the EPackage behind
		 * the root EObject in the resource. For resources that have just been created but not loaded (and therefore no
		 * EObject content) yet we try to retrieve the metamodel descriptor from the underlying workspace file so as to
		 * benefit from metamodel descriptor that potentially has already been cached for the same. At last, we rely on
		 * the resource's content type which enables us to determine the metamodel descriptors of files that are not
		 * loaded yet and located outside the workspace.
		 */

		// Try to retrieve descriptor from model root object in given resource (applies to loaded resources)
		EObject modelRoot = null;
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(resource);
		if (editingDomain != null) {
			try {
				modelRoot = (EObject) editingDomain.runExclusive(new RunnableWithResult.Impl<EObject>() {
					public void run() {
						setResult(EcoreResourceUtil.getModelRoot(resource));
					}
				});
			} catch (InterruptedException ex) {
				// Ignore exception
			}
		} else {
			modelRoot = EcoreResourceUtil.getModelRoot(resource);
		}
		if (modelRoot != null) {
			IMetaModelDescriptor mmDescriptor = getDescriptor(modelRoot);
			if (mmDescriptor != null) {
				return mmDescriptor;
			}
		}

		// Try to retrieve descriptor from underlying workspace file (applies to resources that have been
		// created but not loaded - and therefore no EObject content - yet and are located inside the workspace)
		IFile file = EcorePlatformUtil.getFile(resource);
		if (file != null) {
			return getDescriptor(file);
		}

		// Try to retrieve descriptor from content type id behind given resource (applies to resources that have been
		// created but not loaded - and therefore no EObject content - yet and are located outside the workspace)
		final String contentTypeId = EcoreResourceUtil.getContentTypeId(resource.getURI());
		if (contentTypeId != null) {
			return getDescriptor(ANY_MM, new IDescriptorFilter() {
				public boolean accept(IMetaModelDescriptor descriptor) {
					if (descriptor.getContentTypeIds().contains(contentTypeId)) {
						return true;
					}
					if (descriptor.getCompatibleContentTypeIds().contains(contentTypeId)) {
						return true;
					}
					return false;
				}
			});
		}

		return null;
	}

	/**
	 * Returns the {@link IMetaModelDescriptor meta-model descriptor} for given {@link IFile file}.
	 * 
	 * @param file
	 *            The {@link IFile file} whose {@link IMetaModelDescriptor meta-model descriptor} is to be returned.
	 * @return The {@link IMetaModelDescriptor meta-model descriptor} of given {@link IFile}, or <code>null</code> if no
	 *         such could be determined.
	 */
	public IMetaModelDescriptor getDescriptor(IFile file) {
		if (file != null) {
			// Try to retrieve meta-model descriptor from cache
			if (fFileMetaModelDescriptorCache.hasDescriptor(file)) {
				return fFileMetaModelDescriptorCache.getDescriptor(file);
			} else {
				try {
					IMetaModelDescriptor mmDescriptor = null;

					// Retrieve content type id of given file
					final String contentTypeId = fastGetContentTypeId(file);

					// Determine corresponding meta-model descriptor
					if (contentTypeId != null) {
						mmDescriptor = getDescriptor(ANY_MM, new IDescriptorFilter() {
							public boolean accept(IMetaModelDescriptor descriptor) {
								if (descriptor.getContentTypeIds().contains(contentTypeId)) {
									return true;
								}
								if (descriptor.getCompatibleContentTypeIds().contains(contentTypeId)) {
									return true;
								}
								return false;
							}
						});
					}

					// Cache resulting meta-model descriptor
					if (file.isAccessible()) {
						fFileMetaModelDescriptorCache.addDescriptor(file, mmDescriptor);
					}

					return mmDescriptor;
				} catch (Exception ex) {
					// Ignore exception, just return null
				}
			}
		}
		return null;
	}

	private String fastGetContentTypeId(IFile file) throws CoreException {
		// Content type id for given file already cached?
		if (ExtendedPlatform.hasCachedContentTypeId(file)) {
			// Retrieve content type id of given file normally - we know that it will be quick
			return ExtendedPlatform.getContentTypeId(file);
		} else {
			try {
				/*
				 * Performance optimization: Use optimized detection of content type only if there is a realistic chance
				 * that given file is a model file inside an existing scope. For any other file it would just add some
				 * useless extra time to the native content type retrieval process.
				 */
				if (!ResourceScopeProviderRegistry.INSTANCE.isNotInAnyScope(file)) {
					/*
					 * Performance optimization: Try to determine meta-model descriptor from model namespace in given
					 * file. This works significantly more quickly than retrieving the file's content type and looking
					 * up the corresponding meta-model descriptor. In case the meta-model descriptor can be found this
					 * way we go on and try to deduce the file's content type id from the model namespace which we have
					 * found and cache it for the given file. If successful, this enables us to completely avoid very
					 * slow native content type detection later on (e.g. during Resource creation in
					 * ExtendedResourceSetImpl#demandCreateResource(URI)) resulting in a significant acceleration when
					 * content type dependent operations on many files need to be carried out (e.g. model loading).
					 */
					// Try to retrieve model namespace which might be present in given file
					String namespace = EcorePlatformUtil.readModelNamespace(file);

					// Has a model namespace been found?
					IMetaModelDescriptor mmDescriptor = null;
					if (namespace != null) {
						// Determine corresponding meta-model descriptor
						mmDescriptor = getDescriptor(new URI(namespace));
					}

					// Does a matching meta-model descriptor exist?
					if (mmDescriptor != null) {
						// Try to retrieve content type id for given model namespace
						String contentTypeId = getContentTypeIdFromDescriber(namespace);
						// Is a matching content type id available?
						if (contentTypeId != null) {
							// Cache content type id for given file to accelerate subsequent content type
							// dependent operations
							ExtendedPlatform.setCachedContentTypeId(file, contentTypeId);
							return contentTypeId;
						}
					} else {
						/*
						 * Performance optimization: If the namespace-based retrieval of the meta-model descriptor
						 * didn't succeed and given file is an XML file that has a namespace then we can be sure that
						 * the XML file is not a model file. Consequently, we can immediately remember it is such and
						 * can spare out the lengthy analysis or its content type.
						 */
						if (namespace != null) {
							// Set cached content type id for given file to unspecified to accelerate subsequent
							// content type dependent operations
							ExtendedPlatform.setCachedContentTypeId(file, IExtendedPlatformConstants.CONTENT_TYPE_ID_NON_MODEL_XML_FILE);
							return IExtendedPlatformConstants.CONTENT_TYPE_ID_NON_MODEL_XML_FILE;
						}
					}
				}

				// If we are still here then we have to retrieve the content type id of given file natively - and accept
				// that it will take time
				return ExtendedPlatform.getContentTypeId(file);
			} catch (Exception ex) {
				// Ignore exception, just return null
			}
		}
		return null;
	}

	/**
	 * Returns content type id for given model namespace in case that an EMF {@link Describer content type describer}
	 * has been contributed for this model namespace and matches it.
	 * 
	 * @param namespace
	 *            The model namespace for which the corresponding content type id is to be found
	 * @return The content type id for given model namespace, or <code>null</code> if no such could be determined.
	 */
	private String getContentTypeIdFromDescriber(String namespace) {
		if (fNamespaceContentTypeIdCache.containsKey(namespace)) {
			return fNamespaceContentTypeIdCache.get(namespace);
		}

		// Try to retrieve content type describer which matches model namespace
		List<String> contentTypeIdCandidates = new ArrayList<String>(2);
		for (IContentType contentType : ContentTypeManager.getInstance().getAllContentTypes()) {
			IContentDescriber describer = null;
			if (contentType instanceof ContentType) {
				describer = ((ContentType) contentType).getDescriber();
			} else if (contentType instanceof ContentTypeHandler) {
				describer = ((ContentTypeHandler) contentType).getTarget().getDescriber();
			}

			if (describer instanceof RootXMLContentHandlerImpl.Describer) {
				try {
					ContentHandler contentHandler = (ContentHandler) ReflectUtil.getInvisibleFieldValue(describer, "contentHandler"); //$NON-NLS-1$
					Boolean matching = (Boolean) ReflectUtil.invokeInvisibleMethod(contentHandler, "isMatchingNamespace", namespace); //$NON-NLS-1$
					if (matching) {
						// Exact match?
						String contentHandlerNamespace = (String) ReflectUtil.getInvisibleFieldValue(contentHandler, "namespace"); //$NON-NLS-1$
						if (contentHandlerNamespace == null && namespace == null || contentHandlerNamespace != null && namespace != null
								&& contentHandlerNamespace.length() == namespace.length()) {
							// Return id of exactly matching content type
							return contentType.getId();
						} else {
							// Remember id of matching content type as candidate
							contentTypeIdCandidates.add(contentType.getId());
						}
					}
				} catch (Exception ex) {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
				}
			}
		}

		// Return best fitting content type id candidate if any
		String contentTypeId = null;
		if (contentTypeIdCandidates.size() > 1) {
			Collections.sort(contentTypeIdCandidates);
			contentTypeId = contentTypeIdCandidates.get(contentTypeIdCandidates.size() - 1);
		} else if (contentTypeIdCandidates.size() == 1) {
			contentTypeId = contentTypeIdCandidates.get(0);
		}

		fNamespaceContentTypeIdCache.put(namespace, contentTypeId);
		return contentTypeId;
	}

	/**
	 * Returns the old {@link IMetaModelDescriptor meta-model descriptor} which given {@link Resource resource} had had
	 * before is was changed or deleted. !Important note! The information will only be available during processing of
	 * the method
	 * org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry.FileMetaModelDescriptorCache.removeDescriptor(IFile)
	 * called from org.eclipse.sphinx.emf.internal.MetaModelDescriptorCacheAndModelDescriptorRegistryUpdater.
	 * handleModelResourceUnloaded(Collection<Resource>).In any other use case the method will behave as if there was no
	 * old meta-model descriptor available. The reason is that old meta-model descriptor is removed as soon as model
	 * descriptor has been removed.
	 * 
	 * @param resource
	 *            The {@link Resource resource} whose old {@link IMetaModelDescriptor meta-model descriptor} is to be
	 *            returned.
	 * @return The old {@link IMetaModelDescriptor meta-model descriptor} of given {@link Resource}, or
	 *         <code>null</code> {@link IFile file} hadn't had any {@link IMetaModelDescriptor meta-model descriptor}
	 *         before it was changed or deleted.
	 */
	public IMetaModelDescriptor getOldDescriptor(Resource resource) {
		IFile file = EcorePlatformUtil.getFile(resource);
		return getOldDescriptor(file);
	}

	/**
	 * Returns the old {@link IMetaModelDescriptor meta-model descriptor} which given {@link IFile file} had had before
	 * is was changed or deleted. !Important note! The information will only be available during processing of the
	 * method
	 * org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry.FileMetaModelDescriptorCache.removeDescriptor(IFile)
	 * called from org.eclipse.sphinx.emf.internal.MetaModelDescriptorCacheAndModelDescriptorRegistryUpdater.
	 * handleModelResourceUnloaded(Collection<Resource>).In any other use case the method will behave as if there was no
	 * old meta-model descriptor available. The reason is that old meta-model descriptor is removed as soon as model
	 * descriptor has been removed.
	 * 
	 * @param file
	 *            The {@link IFile file} whose old {@link IMetaModelDescriptor meta-model descriptor} is to be returned.
	 * @return The old {@link IMetaModelDescriptor meta-model descriptor} of given {@link IFile}, or <code>null</code>
	 *         {@link IFile file} hadn't had any {@link IMetaModelDescriptor meta-model descriptor} before it was
	 *         changed or deleted.
	 */
	public IMetaModelDescriptor getOldDescriptor(IFile file) {
		return fFileMetaModelDescriptorCache.getOldDescriptor(file);
	}

	/**
	 * @param eObject
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(EObject eObject) {
		if (eObject != null) {
			// Retrieve meta-model descriptor from EClass behind given EObject in case that it is still up to date
			IMetaModelDescriptor descriptor = getDescriptor(eObject.eClass());
			IMetaModelDescriptor oldDescriptor = getOldDescriptor(eObject.eResource());
			if (descriptor != oldDescriptor) {
				return descriptor;
			} else {
				// Try to retrieve an up to date meta-model descriptor from underlying file otherwise
				IFile file = EcorePlatformUtil.getFile(eObject.eResource());
				return getDescriptor(file);
			}
		}
		return null;
	}

	/**
	 * @param eClass
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(EClass eClass) {
		if (eClass != null) {
			return getDescriptor(eClass.getEPackage());
		}
		return null;
	}

	/**
	 * @param ePackage
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(EPackage ePackage) {
		if (ePackage != null) {
			final String nsURI = ePackage.getNsURI();
			if (nsURI != null) {
				return getDescriptor(ANY_MM, new IDescriptorFilter() {
					public boolean accept(IMetaModelDescriptor descriptor) {
						return nsURI.matches(descriptor.getEPackageNsURIPattern());
					}
				});
			}
		}
		return null;
	}

	/**
	 * @param wrapperItemProvider
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(IWrapperItemProvider wrapperItemProvider) {
		if (wrapperItemProvider != null) {
			Object unwrapped = AdapterFactoryEditingDomain.unwrap(wrapperItemProvider);
			IMetaModelDescriptor mmDescriptor = getDescriptor(unwrapped);
			if (mmDescriptor != null) {
				return mmDescriptor;
			}
			return getDescriptor(wrapperItemProvider.getOwner());
		}
		return null;
	}

	/**
	 * @param entry
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(FeatureMap.Entry entry) {
		Object unwrapped = AdapterFactoryEditingDomain.unwrap(entry);
		return getDescriptor(unwrapped);
	}

	/**
	 * @param transientItemProvider
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(TransientItemProvider transientItemProvider) {
		if (transientItemProvider != null) {
			Notifier target = transientItemProvider.getTarget();
			return getDescriptor(target);
		}
		return null;
	}

	/**
	 * @param namespaceURI
	 * @return
	 */
	public IMetaModelDescriptor getDescriptor(final URI namespaceURI) {
		if (namespaceURI != null) {
			synchronized (fMetaModelDescriptors) {
				IMetaModelDescriptor mmDescriptor = getDescriptor(ANY_MM, new IDescriptorFilter() {
					public boolean accept(IMetaModelDescriptor mmDescriptor) {
						if (namespaceURI.toString().matches(mmDescriptor.getNamespace())) {
							return true;
						}
						if (namespaceURI.toString().matches(mmDescriptor.getEPackageNsURIPattern())) {
							return true;
						}
						for (URI compatibleNamepaceURI : mmDescriptor.getCompatibleNamespaceURIs()) {
							if (namespaceURI.toString().matches(compatibleNamepaceURI.toString())) {
								return true;
							}
						}
						for (IMetaModelDescriptor compatibleResourceVersionDescriptor : mmDescriptor.getCompatibleResourceVersionDescriptors()) {
							if (namespaceURI.toString().matches(compatibleResourceVersionDescriptor.getNamespace())) {
								return true;
							}
							if (namespaceURI.toString().matches(compatibleResourceVersionDescriptor.getEPackageNsURIPattern())) {
								return true;
							}
						}
						return false;
					}
				});

				// No static meta-model descriptor found?
				if (mmDescriptor == null) {
					// Try to retrieve Ecore model behind given namespace and dynamically create a new meta-model
					// descriptor
					EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(namespaceURI.toString());
					if (ePackage != null) {
						mmDescriptor = createDescriptor(ePackage);
						addDescriptor(mmDescriptor);
					}
				}
				return mmDescriptor;
			}
		}
		return null;
	}

	/**
	 * @param class
	 * @return
	 * @deprecated It is not recommended use this method because the {@link IMetaModelDescriptor metamodel descriptor}
	 *             retrieval strategy implemented here has the side effect of that it triggers a full initialization of
	 *             all {@link EPackage}s behind potentially all {@link IMetaModelDescriptor metamodel descriptor}s. This
	 *             can have significant impact on runtime performance and may cause that the {@link EPackage}s of
	 *             metamodels become initialized even though not a single instance of these metamodels exists in the
	 *             workspace.
	 */
	@Deprecated
	public IMetaModelDescriptor getDescriptor(final Class<?> clazz) {
		if (clazz != null) {
			IMetaModelDescriptor descriptor = getDescriptor(ANY_MM, new IDescriptorFilter() {
				public boolean accept(IMetaModelDescriptor descriptor) {
					// Test if the class name of one of the metamodel's EPackages is the prefix of the given class' name
					for (EPackage ePackage : descriptor.getEPackages()) {
						if (clazz.getName().startsWith(ePackage.getClass().getName())) {
							return true;
						}
					}
					return false;
				}
			});
			return descriptor;
		}
		return null;
	}

	/**
	 * @param mmDescriptor
	 * @param filter
	 * @return
	 */
	private <T extends IMetaModelDescriptor> T getDescriptor(T mmDescriptor, IDescriptorFilter filter) {
		for (T descriptor : getDescriptors(mmDescriptor)) {
			if (filter.accept(descriptor)) {
				return descriptor;
			}
		}
		return null;
	}

	/**
	 * Retrieves {@link IMetaModelDescriptor target meta-model descriptor} behind the given {@link IFile file}.
	 * 
	 * @param file
	 *            The {@link IFile file} to be investigated.
	 * @return The {@link IFile file}'s {@link IMetaModelDescriptor target meta-model descriptor} or <code>null</code>
	 *         if no such exists.
	 */
	public IMetaModelDescriptor getTargetDescriptor(IFile file) {
		ITargetMetaModelDescriptorProvider provider = getTargetMetaModelDescriptorProvider(file);
		if (provider != null) {
			return provider.getDescriptor(file);
		}
		return null;
	}

	/**
	 * Retrieves {@link IMetaModelDescriptor target meta-model descriptor} behind the given {@link Resource resource}.
	 * 
	 * @param resource
	 *            The {@link Resource resource} to be investigated.
	 * @return The {@link Resource resource}'s {@link IMetaModelDescriptor target meta-model descriptor} or
	 *         <code>null</code> if no such exists.
	 */
	public IMetaModelDescriptor getTargetDescriptor(Resource resource) {
		ITargetMetaModelDescriptorProvider provider = getTargetMetaModelDescriptorProvider(resource);
		if (provider != null) {
			return provider.getDescriptor(resource);
		}
		return null;
	}

	/**
	 * Retrieves the extensions of all file types which are associated with a {@link IMetaModelDescriptor target
	 * meta-model descriptor}. Includes both file extensions for which a {@link IMetaModelDescriptor target meta-model
	 * descriptor} has been specified directly and file extensions supported by content types for which a
	 * {@link IMetaModelDescriptor target meta-model descriptor} has been defined.
	 * 
	 * @return A {@link Collection collection} with all file types associated with a {@link IMetaModelDescriptor target
	 *         meta-model descriptor} or an empty {@link Collection collection} if no such exist.
	 */
	public Collection<String> getFileExtensionsAssociatedWithTargetDescriptors() {
		Collection<String> extensions = new HashSet<String>(2);
		extensions.addAll(fFileExtensionToTargetMetaModelDescriptorProviderIds.keySet());
		for (String contentTypeId : fContentTypeIdToTargetMetaModelDescriptorProviderIds.keySet()) {
			extensions.addAll(ExtendedPlatform.getContentTypeFileExtensions(contentTypeId));
		}
		return Collections.unmodifiableCollection(extensions);
	}

	private ITargetMetaModelDescriptorProvider getTargetMetaModelDescriptorProvider(IFile file) {
		try {
			ITargetMetaModelDescriptorProvider provider = null;
			if (file != null) {
				String fileExtension = file.getFileExtension();
				provider = fFileExtensionToTargetMetaModelDescriptorProviderIds.get(fileExtension);
				if (provider == null) {
					String contentTypeId = fastGetContentTypeId(file);
					provider = fContentTypeIdToTargetMetaModelDescriptorProviderIds.get(contentTypeId);
				}
			}
			return provider;
		} catch (CoreException ex) {
			// Ignore exception, just return null
		}
		return null;
	}

	private ITargetMetaModelDescriptorProvider getTargetMetaModelDescriptorProvider(Resource resource) {
		IFile file = EcorePlatformUtil.getFile(resource);
		return getTargetMetaModelDescriptorProvider(file);
	}

	/**
	 * Retrieves {@link IMetaModelDescriptor meta-model descriptor} behind the given {@link IFile file} which will be
	 * eventually effective or relevant to clients. This is the {@link IFile}'s {@link IMetaModelDescriptor target
	 * meta-model descriptor} if such one is available or the {@link IFile}'s native {@link IMetaModelDescriptor
	 * meta-model descriptor} otherwise.
	 * 
	 * @param file
	 *            The {@link IFile file} to be investigated.
	 * @return The {@link IFile file}'s effective {@link IMetaModelDescriptor meta-model descriptor} or
	 *         <code>null</code> if no such exists.
	 */
	public IMetaModelDescriptor getEffectiveDescriptor(IFile file) {
		// Try to retrieve target meta-model descriptor
		IMetaModelDescriptor descriptor = getTargetDescriptor(file);
		if (descriptor == null) {
			// Retrieve native meta-model descriptor otherwise
			descriptor = getDescriptor(file);
		}
		return descriptor;
	}

	/**
	 * Retrieves {@link IMetaModelDescriptor meta-model descriptor} behind the given {@link Resource resource} which
	 * will be eventually effective or relevant to clients. This is the {@link IFile}'s {@link IMetaModelDescriptor
	 * target meta-model descriptor} if such one is available or the {@link IFile}'s native {@link IMetaModelDescriptor
	 * meta-model descriptor} otherwise.
	 * 
	 * @param resource
	 *            The {@link Resource resource} to be investigated.
	 * @return The {@link Resource resource}'s effective {@link IMetaModelDescriptor meta-model descriptor} or
	 *         <code>null</code> if no such exists.
	 */
	public IMetaModelDescriptor getEffectiveDescriptor(Resource resource) {
		// Try to retrieve target meta-model descriptor
		IMetaModelDescriptor descriptor = getTargetDescriptor(resource);
		if (descriptor == null) {
			// Retrieve native meta-model descriptor otherwise
			descriptor = getDescriptor(resource);
		}
		return descriptor;
	}

	/**
	 * Returns a {@link IMetaModelDescriptor descriptor} describing the version of specified {@link Resource resource}.
	 * This is done by retrieving the resource's {@link EcoreResourceUtil#readModelNamespace(Resource) model namespace}
	 * and finding a matching {@link IMetaModelDescriptor metamodel descriptor} or
	 * {@link IMetaModelDescriptor#getCompatibleResourceVersionDescriptors() compatible resource version descriptor}.
	 * 
	 * @param resource
	 *            The {@link Resource resource} whose version descriptor is to be retrieved.
	 * @return The resource's {@link IMetaModelDescriptor version descriptor} or <code>null</code> if no such could be
	 *         determined.
	 * @see EcoreResourceUtil#readModelNamespace(Resource)
	 * @see #getCompatibleResourceVersionDescriptors()
	 */
	public IMetaModelDescriptor getResourceVersionDescriptor(Resource resource) {
		try {
			String resourceNamespace = EcoreResourceUtil.readModelNamespace(resource);
			if (resourceNamespace != null) {
				IMetaModelDescriptor mmDescriptor = getDescriptor(new URI(resourceNamespace));
				if (mmDescriptor != null) {
					// Newly created resources typically match implemented metamodel version exactly
					if (resourceNamespace.matches(mmDescriptor.getNamespace())) {
						return mmDescriptor;
					}
					if (resourceNamespace.matches(mmDescriptor.getEPackageNsURIPattern())) {
						return mmDescriptor;
					}

					// Resource version is older than but compatible with metamodel version
					for (IMetaModelDescriptor compatibleResourceVersionDescriptor : mmDescriptor.getCompatibleResourceVersionDescriptors()) {
						if (resourceNamespace.matches(compatibleResourceVersionDescriptor.getNamespace())) {
							return compatibleResourceVersionDescriptor;
						}
						if (resourceNamespace.matches(compatibleResourceVersionDescriptor.getEPackageNsURIPattern())) {
							return compatibleResourceVersionDescriptor;
						}
					}
				}
			}
		} catch (URISyntaxException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}

	/**
	 * 
	 */
	private interface IDescriptorFilter {

		/**
		 * @param descriptor
		 * @return
		 */
		public boolean accept(IMetaModelDescriptor descriptor);
	}

	private class FileMetaModelDescriptorCache implements IFileMetaModelDescriptorCache {

		/**
		 * A cache of {@link IFile file}/{@link IMetaModelDescriptor meta-model descriptor} associations for
		 * accelerating {@link IMetaModelDescriptor meta-model descriptor} retrieval.
		 */
		private Map<IFile, IMetaModelDescriptor> fFileMetaModelDescriptors = Collections.synchronizedMap(new HashMap<IFile, IMetaModelDescriptor>());

		/**
		 * A cache of {@link IFile file}/{@link IMetaModelDescriptor meta-model descriptor} associations for enabling
		 * retrieval of old {@link IMetaModelDescriptor meta-model descriptor}s for {@link IFile file}s which have been
		 * changed or deleted.
		 */
		private Map<IFile, IMetaModelDescriptor> fOldFileMetaModelDescriptors = Collections
				.synchronizedMap(new HashMap<IFile, IMetaModelDescriptor>());

		/*
		 * @see
		 * org.eclipse.sphinx.emf.metamodel.InternalMetaModelDescriptorRegistry#addCachedMetaModelDescriptor(org.eclipse
		 * . core.resources.IFile, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)
		 */
		public void addDescriptor(IFile file, IMetaModelDescriptor mmDescriptor) {
			if (file != null) {
				// Encode and cache given meta-model descriptor
				fFileMetaModelDescriptors.put(file, mmDescriptor != null ? mmDescriptor : NO_MM);
			}
		}

		boolean hasDescriptor(IFile file) {
			if (file != null) {
				return fFileMetaModelDescriptors.containsKey(file);
			}
			return false;
		}

		IMetaModelDescriptor getDescriptor(IFile file) {
			if (file != null) {
				IMetaModelDescriptor mmDescriptor = fFileMetaModelDescriptors.get(file);

				// Decode and return cached meta-model descriptor
				return mmDescriptor != NO_MM ? mmDescriptor : null;
			}
			return null;
		}

		/*
		 * @see
		 * org.eclipse.sphinx.emf.internal.metamodel.IFileMetaModelDescriptorCache#moveDescriptor(org.eclipse.core.resources
		 * .IFile, org.eclipse.core.resources.IFile)
		 */
		public void moveDescriptor(IFile oldFile, IFile newFile) {
			if (oldFile != null && newFile != null) {
				synchronized (fFileMetaModelDescriptors) {
					IMetaModelDescriptor mmDescriptor = fFileMetaModelDescriptors.remove(oldFile);
					if (mmDescriptor != null) {
						if (!mmDescriptor.equals(MetaModelDescriptorRegistry.NO_MM)) {
							fOldFileMetaModelDescriptors.put(oldFile, mmDescriptor);
						}
						/*
						 * !! Important Note !! Don't keep old metamodel descriptor as metamodel descriptor for moved
						 * file because it might no longer be a model file (e.g., because its extension has been
						 * changed)
						 */
					}
				}

				// Make sure that cached content id gets purged along with cached metamodel descriptor
				/*
				 * !! Important Note !! This should normally be the business of ContentTypeIdCachePurger. However, we
				 * have to do so here as well because we must avoid that clients end up calling
				 * MetaModelDescriptorRegistry#getDescriptor(IFile) before ContentTypeIdCachePurger has got an
				 * opportunity to do its job. Otherwise it could happen that the meta model descriptor for the file in
				 * question gets retrieved from an obsolete but still cached content type id.
				 */
				ExtendedPlatform.removeCachedContentTypeId(newFile);
			}
		}

		/*
		 * @see
		 * org.eclipse.sphinx.emf.metamodel.InternalMetaModelDescriptorRegistry#removeCachedMetaModelDescriptor(org.
		 * eclipse .core.resources.IFile)
		 */
		public void removeDescriptor(IFile file) {
			if (file != null) {
				synchronized (fFileMetaModelDescriptors) {
					IMetaModelDescriptor removedMMDescriptor = fFileMetaModelDescriptors.remove(file);
					if (removedMMDescriptor != null && !removedMMDescriptor.equals(MetaModelDescriptorRegistry.NO_MM)) {
						fOldFileMetaModelDescriptors.put(file, removedMMDescriptor);
					}
				}

				// Make sure that cached content id gets purged along with cached metamodel descriptor
				/*
				 * !! Important Note !! This should normally be the business of ContentTypeIdCachePurger. However, we
				 * have to do so here as well because we must avoid that clients end up calling
				 * MetaModelDescriptorRegistry#getDescriptor(IFile) before ContentTypeIdCachePurger has got an
				 * opportunity to do its job. Otherwise it could happen that the meta model descriptor for the file in
				 * question gets retrieved from an obsolete but still cached content type id.
				 */
				ExtendedPlatform.removeCachedContentTypeId(file);
			}
		}

		IMetaModelDescriptor getOldDescriptor(IFile file) {
			if (file != null) {
				return fOldFileMetaModelDescriptors.get(file);
			}
			return null;
		}

		/*
		 * @see org.eclipse.sphinx.emf.internal.metamodel.IFileMetaModelDescriptorCache#clearOldDescriptors()
		 */
		public void clearOldDescriptors() {
			fOldFileMetaModelDescriptors.clear();
		}
	}

	private static class DefaultMetaModelDescriptor extends AbstractMetaModelDescriptor {

		protected DefaultMetaModelDescriptor(String identifier, String namespace, String name) {
			super(identifier, namespace, name);
		}

		/*
		 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor#getDefaultContentTypeId()
		 */
		@Override
		public String getDefaultContentTypeId() {
			return ""; //$NON-NLS-1$
		}
	}

	private static final class AnyMetaModelDescriptor extends DefaultMetaModelDescriptor {

		public AnyMetaModelDescriptor() {
			super("org.eclipse.sphinx.emf.metamodel.any", "http://any.mm", "Any metamodel"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private static final class NoMetaModelDescriptor extends DefaultMetaModelDescriptor {

		public NoMetaModelDescriptor() {
			super("org.eclipse.sphinx.emf.metamodel.no", "http://no.mm", "No metamodel"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
}
