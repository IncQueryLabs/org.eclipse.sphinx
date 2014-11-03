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
 * The registry for the metamodel services. Clients should use {@link DefaultMetamodelServiceProvider}.
 */
class MetaModelServiceRegistry {

	/** the singleton */
	static final MetaModelServiceRegistry INSTANCE = new MetaModelServiceRegistry(Platform.getExtensionRegistry(), Activator.getPlugin().getLog());

	private volatile Map<IMetaModelDescriptor, Map<String, ServiceClassDescriptor>> mmServices;

	private IExtensionRegistry extensionRegistry;

	private ILog logger;

	private final static String MMS_EXTENSION_ID = Activator.INSTANCE.getSymbolicName() + ".metaModelServices"; //$NON-NLS-1$

	private final static String MMS_EXTENSION_ELEM_DESCRIPTOR = "descriptor"; //$NON-NLS-1$

	private final static String MMS_EXTENSION_ELEM_SERVICE = "service"; //$NON-NLS-1$

	private final static String MMS_ATT_DESCRIPTORID = "descriptorID"; //$NON-NLS-1$

	private static final Plugin PLUGIN = Activator.getDefault();

	MetaModelServiceRegistry(IExtensionRegistry extensionRegistry, ILog logger) {
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
	<T extends IMetaModelService> T getService(IMetaModelDescriptor descriptor, Class<T> serviceClass) {
		checkInit();
		if (mmServices.containsKey(descriptor)) {
			Map<String, ServiceClassDescriptor> map = getServiceRegistrations(descriptor);
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
	private void checkInit() {
		if (mmServices == null) {

			synchronized (INSTANCE) {

				if (mmServices == null) {

					mmServices = new HashMap<IMetaModelDescriptor, Map<String, ServiceClassDescriptor>>();
					if (extensionRegistry == null) {
						return;
					}
					IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(MMS_EXTENSION_ID);
					for (IConfigurationElement cfgElem : elements) {

						if (!cfgElem.getName().equals(MMS_EXTENSION_ELEM_DESCRIPTOR)) {
							continue;
						}
						String identifier = cfgElem.getAttribute(MMS_ATT_DESCRIPTORID);

						// missing identifier, log warning
						if (identifier == null) {
							logWarning(Messages.metamodelservice_MissingMMDescriptor, cfgElem.getContributor().getName());
							continue;
						}
						// locate the corresponding meta-model descriptor
						IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(identifier);

						// invalid descriptor, log warning
						if (mmDescriptor == null) {
							logWarning(Messages.metamodelservice_UnknownMM, cfgElem.getContributor().getName(), MMS_EXTENSION_ID, identifier);
							continue;
						}
						IConfigurationElement[] children = cfgElem.getChildren(MMS_EXTENSION_ELEM_SERVICE);
						for (IConfigurationElement child : children) {
							applyServiceRegistrationConfigElement(mmDescriptor, child);
						}
					}
				}
			}
		}
	}

	private void applyServiceRegistrationConfigElement(IMetaModelDescriptor mmDescriptor, IConfigurationElement configElement) {
		try {
			applyServiceRegistration(mmDescriptor, new ServiceClassDescriptor(configElement));
		} catch (IllegalArgumentException iae) {
			logWarning(iae);
		}
	}

	private void applyServiceRegistration(IMetaModelDescriptor mmDesc, ServiceClassDescriptor registration) {
		if (isDominantServiceRegistration(mmDesc, registration)) {
			registerService(mmDesc, registration);
		} else if (!isDominantedServiceRegistration(mmDesc, registration)) {
			logWarning(Messages.metamodelservice_ServiceAlreadySet, registration.getType(), mmDesc.getIdentifier());
		}
	}

	/**
	 * Registers a service with this <code>MetaModelServiceRegistry</code> for a metamodel.
	 *
	 * @param mmDesc
	 *            the metamodel for which the service is to be registered.
	 * @param registration
	 *            the service registration
	 */
	private void registerService(IMetaModelDescriptor mmDesc, ServiceClassDescriptor registration) {
		getServiceRegistrations(mmDesc).put(registration.getType(), registration);
	}

	/**
	 * Returns the service registration for a metamodel.
	 *
	 * @param mmDesc
	 *            the metamodel descriptor of the metamodel for which to return the registration.
	 * @param serviceType
	 *            the type of the service.
	 * @return the service registration or <code>null</code> if no registration is found for the given metamodel
	 */
	private ServiceClassDescriptor getServiceRegistration(IMetaModelDescriptor mmDesc, String serviceType) {
		return getServiceRegistrations(mmDesc).get(serviceType);
	}

	/**
	 * Returns all services registrations for a metamodel.
	 *
	 * @param mmDesc
	 *            the metamodel descriptor of the metamodel for which to return the registrations.
	 * @return all service registrations for the specified metamodel
	 */
	private Map<String, ServiceClassDescriptor> getServiceRegistrations(IMetaModelDescriptor mmDesc) {
		if (!mmServices.containsKey(mmDesc)) {
			mmServices.put(mmDesc, new HashMap<String, ServiceClassDescriptor>());
		}
		return mmServices.get(mmDesc);
	}

	/**
	 * Determines if the new service registration dominates the present registration.
	 *
	 * @param mmDesc
	 *            the metamodel for which the new service is to be registered
	 * @param
	 * @return if the new service dominates the present registration
	 * @see
	 */
	private boolean isDominantServiceRegistration(IMetaModelDescriptor mmDesc, ServiceClassDescriptor registration) {
		return registration.overrides(getServiceRegistration(mmDesc, registration.getType()));
	}

	/**
	 * Determines if the new service registration is dominated by the present registration.
	 *
	 * @param mmDesc
	 *            the metamodel for which the new service is to be registered
	 * @param
	 * @return if the new service is dominated by the present registration
	 * @see
	 */
	private boolean isDominantedServiceRegistration(IMetaModelDescriptor mmDesc, ServiceClassDescriptor registration) {
		return getServiceRegistration(mmDesc, registration.getType()).overrides(registration);
	}

}
