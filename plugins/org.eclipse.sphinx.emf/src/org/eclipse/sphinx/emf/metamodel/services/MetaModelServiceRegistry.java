/**
 * <copyright>
 *
 * Copyright (c) Continental Engineering Services, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Continental Engineering Services - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.metamodel.services;

import static org.eclipse.sphinx.platform.util.StatusUtil.createErrorStatus;
import static org.eclipse.sphinx.platform.util.StatusUtil.createWarningStatus;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.internal.metamodel.services.ServiceClassDescriptor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;

/**
 * The registry for the metamodel services. Clients should use {@link DefaultMetaModelServiceProvider}.
 */
public class MetaModelServiceRegistry {

	/** The singleton */
	static final MetaModelServiceRegistry INSTANCE = new MetaModelServiceRegistry(Platform.getExtensionRegistry(), Activator.getPlugin().getLog());

	private volatile Map<IMetaModelDescriptor, Map<String, ServiceClassDescriptor>> mmServices;

	private IExtensionRegistry extensionRegistry;

	private ILog logger;

	private final static String MMS_EXTENSION_ID = Activator.INSTANCE.getSymbolicName() + ".metaModelServices"; //$NON-NLS-1$

	private final static String MMS_EXTENSION_ELEM_DESCRIPTOR = "descriptor"; //$NON-NLS-1$

	private final static String MMS_EXTENSION_ELEM_SERVICE = "service"; //$NON-NLS-1$

	private final static String MMS_ATT_DESCRIPTORID = "descriptorID"; //$NON-NLS-1$

	private static final Plugin PLUGIN = Activator.getDefault();

	private MetaModelServiceRegistry(IExtensionRegistry extensionRegistry, ILog logger) {
		this.extensionRegistry = extensionRegistry;
		this.logger = logger;
	}

	/**
	 * For the given meta-model descriptor<code>descriptor</code>, obtain the requested service of given
	 * <code>serviceClass</code> class <br>
	 * NOTE: if the requested service is not implemented for the given descriptor, a warning is logged and
	 * <code>null</code> is returned
	 */
	@SuppressWarnings("unchecked")
	protected <T extends IMetaModelService> T getService(IMetaModelDescriptor descriptor, Class<T> serviceClass) {
		initialize();
		if (mmServices.containsKey(descriptor)) {
			Map<String, ServiceClassDescriptor> map = getServiceClassDescriptorsForMetaModel(descriptor);
			String serviceClassName = serviceClass.getCanonicalName();
			if (!map.containsKey(serviceClassName)) {
				// unimplemented service, log error
				logError(Messages.metamodelservice_ServiceNotImplemented, serviceClassName, descriptor.getIdentifier());
			} else {
				ServiceClassDescriptor wrapper = map.get(serviceClassName);
				try {
					IMetaModelService service = wrapper.getInstance();
					if (serviceClass.isInstance(service)) {
						return (T) service;
					} else {
						logError(Messages.metamodelservice_InvalidServiceClass, serviceClassName, service.getClass().getCanonicalName());
					}
				} catch (Throwable ex) {
					logError(ex);
				}
			}
		} else {
			// unknown descriptor, log warning
			logWarning(Messages.metamodelservice_UnknownDescriptor, descriptor);
		}
		return null;
	}

	private void logWarning(String msgId, Object... objects) {
		logWarning(new RuntimeException(NLS.bind(msgId, objects)));
	}

	private void logWarning(Throwable throwable) {
		logger.log(createWarningStatus(PLUGIN, throwable));
	}

	private void logError(String msgId, Object... objects) {
		logError(new RuntimeException(NLS.bind(msgId, objects)));
	}

	private void logError(Throwable throwable) {
		logger.log(createErrorStatus(PLUGIN, throwable));
	}

	/**
	 * Initialize internal data by reading from platform registry
	 */
	private void initialize() {
		if (mmServices == null) {

			synchronized (INSTANCE) {

				if (mmServices == null) {

					mmServices = new HashMap<IMetaModelDescriptor, Map<String, ServiceClassDescriptor>>();
					if (extensionRegistry == null) {
						return;
					}
					IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(MMS_EXTENSION_ID);
					for (IConfigurationElement element : elements) {

						if (!element.getName().equals(MMS_EXTENSION_ELEM_DESCRIPTOR)) {
							continue;
						}
						String identifier = element.getAttribute(MMS_ATT_DESCRIPTORID);

						// missing identifier, log warning
						if (identifier == null) {
							logWarning(Messages.metamodelservice_MissingMMDescriptor, element.getContributor().getName());
							continue;
						}
						// locate the corresponding meta-model descriptor
						IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(identifier);

						// invalid descriptor, log warning
						if (mmDescriptor == null) {
							logWarning(Messages.metamodelservice_UnknownMM, element.getContributor().getName(), MMS_EXTENSION_ID, identifier);
							continue;
						}
						IConfigurationElement[] children = element.getChildren(MMS_EXTENSION_ELEM_SERVICE);
						for (IConfigurationElement child : children) {
							registerMetaModelService(mmDescriptor, child);
						}
					}
				}
			}
		}
	}

	private void registerMetaModelService(IMetaModelDescriptor mmDescriptor, IConfigurationElement configElement) {
		try {
			ServiceClassDescriptor descriptor = new ServiceClassDescriptor(configElement);
			if (descriptor.overrides(getServiceClassDescriptor(mmDescriptor, descriptor.getType()))) {
				addService(mmDescriptor, descriptor);
			}
			// FIXME NPE
			else if (!getServiceClassDescriptor(mmDescriptor, descriptor.getType()).overrides(descriptor)) {
				logWarning(Messages.metamodelservice_ServiceAlreadyExists, descriptor.getType(), mmDescriptor.getIdentifier());
			}
		} catch (IllegalArgumentException iae) {
			logWarning(iae);
		}
	}

	/**
	 * Registers a service with this <code>MetaModelServiceRegistry</code> for a metamodel.
	 *
	 * @param mmDescriptor
	 *            the metamodel for which the service is to be registered.
	 * @param serviceClassDescriptor
	 *            the service registration
	 */
	protected void addService(IMetaModelDescriptor mmDescriptor, ServiceClassDescriptor serviceClassDescriptor) {
		getServiceClassDescriptorsForMetaModel(mmDescriptor).put(serviceClassDescriptor.getType(), serviceClassDescriptor);
	}

	/**
	 * Returns the service registration for a metamodel.
	 *
	 * @param mmDescriptor
	 *            the metamodel descriptor of the metamodel for which to return the registration.
	 * @param serviceType
	 *            the type of the service.
	 * @return the service registration or <code>null</code> if no registration is found for the given metamodel
	 */
	private ServiceClassDescriptor getServiceClassDescriptor(IMetaModelDescriptor mmDescriptor, String serviceType) {
		return getServiceClassDescriptorsForMetaModel(mmDescriptor).get(serviceType);
	}

	/**
	 * Returns all services registrations for a metamodel.
	 *
	 * @param mmDescriptor
	 *            the metamodel descriptor of the metamodel for which to return the registrations.
	 * @return all service registrations for the specified metamodel
	 */
	private Map<String, ServiceClassDescriptor> getServiceClassDescriptorsForMetaModel(IMetaModelDescriptor mmDescriptor) {
		if (!mmServices.containsKey(mmDescriptor)) {
			mmServices.put(mmDescriptor, new HashMap<String, ServiceClassDescriptor>());
		}
		return mmServices.get(mmDescriptor);
	}

}
