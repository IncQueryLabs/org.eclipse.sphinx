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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
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

/**
 * The registry for the metamodel services. Clients should use {@link DefaultMetaModelServiceProvider}.
 */
public class MetaModelServiceRegistry {

	/** The singleton */
	static final MetaModelServiceRegistry INSTANCE = new MetaModelServiceRegistry(Platform.getExtensionRegistry(), Activator.getPlugin().getLog());

	// private Map<String, ServiceClassDescriptor> idToMetaModelServiceMap;
	private volatile Map<IMetaModelDescriptor, Map<Class<IMetaModelService>, ServiceClassDescriptor>> mmServices;

	private IExtensionRegistry extensionRegistry;

	private ILog logger;

	private final static String MMS_EXTENSION_ID = Activator.INSTANCE.getSymbolicName() + ".metaModelServices"; //$NON-NLS-1$

	private final static String NODE_SERVICE = "service"; //$NON-NLS-1$

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
	protected <T extends IMetaModelService> T getService(IMetaModelDescriptor descriptor, Class<T> serviceType) {
		initialize();
		if (mmServices.containsKey(descriptor)) {
			Map<Class<IMetaModelService>, ServiceClassDescriptor> map = getServiceClassDescriptorsForMetaModel(descriptor);
			if (map.containsKey(serviceType)) {
				ServiceClassDescriptor serviceClassDescriptor = map.get(serviceType);
				try {
					IMetaModelService service = serviceClassDescriptor.getInstance();
					if (serviceType.isInstance(service)) {
						return (T) service;
					} else {
						logError(Messages.metamodelservice_InvalidServiceClass, serviceType.getName(), service.getClass().getName());
					}
				} catch (Throwable ex) {
					logError(ex);
				}
			}
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
		if (extensionRegistry == null) {
			return;
		}

		if (mmServices == null) {
			mmServices = new HashMap<IMetaModelDescriptor, Map<Class<IMetaModelService>, ServiceClassDescriptor>>();

			// All configuration elements from all meta-model service extensions.
			IConfigurationElement[] serviceConfigurationElements = extensionRegistry.getConfigurationElementsFor(MMS_EXTENSION_ID);

			// Create a temporary map
			Map<String, ServiceClassDescriptor> idToServiceClassDescriptorMap = new HashMap<String, ServiceClassDescriptor>();

			// First iteration to detect duplicated service id and initialize the temporary map
			for (IConfigurationElement serviceCfgElement : serviceConfigurationElements) {

				if (!serviceCfgElement.getName().equals(NODE_SERVICE)) {
					continue;
				}
				ServiceClassDescriptor serviceClassDescriptor = new ServiceClassDescriptor(serviceCfgElement);
				String serviceId = serviceClassDescriptor.getId();
				if (idToServiceClassDescriptorMap.containsKey(serviceId)) {
					logWarning(Messages.warning_serviceIdNotUnique, serviceId);
					continue;
				}
				idToServiceClassDescriptorMap.put(serviceId, serviceClassDescriptor);
			}

			// Second iteration to add services
			for (ServiceClassDescriptor serviceClassDescriptor : idToServiceClassDescriptorMap.values()) {
				String override = serviceClassDescriptor.getOverride();
				if (override != null && !idToServiceClassDescriptorMap.containsKey(override)) {
					logWarning(Messages.warning_noServiceToOverride, serviceClassDescriptor.getId(), override);
					continue;
				}
				List<IMetaModelDescriptor> mmDescriptors = serviceClassDescriptor.getMetaModelDescriptors();
				Set<String> unknownMetaModelDescIdPatterns = serviceClassDescriptor.getUnknownMetaModelDescIdPatterns();
				// No descriptor, log warning
				if (mmDescriptors.isEmpty() && unknownMetaModelDescIdPatterns.isEmpty()) {
					logWarning(Messages.metamodelservice_MissingMMDescriptor, serviceClassDescriptor.getContributorName());
					continue;
				}
				if (!unknownMetaModelDescIdPatterns.isEmpty()) {
					logWarning(Messages.metamodelservice_UnknownMM, serviceClassDescriptor.getContributorName(), MMS_EXTENSION_ID,
							unknownMetaModelDescIdPatterns);
				}
				// Add Services
				for (IMetaModelDescriptor mmDescriptor : mmDescriptors) {
					addService(mmDescriptor, serviceClassDescriptor);
				}
			}
			idToServiceClassDescriptorMap.clear();
		}
	}

	private void addService(IMetaModelDescriptor mmDescriptor, ServiceClassDescriptor newServiceClassDescriptor) {
		try {
			ServiceClassDescriptor existingServiceClassDescriptorWithSameMMDescAndSameType = getServiceClassDescriptor(mmDescriptor,
					newServiceClassDescriptor.getServiceType());
			if (existingServiceClassDescriptorWithSameMMDescAndSameType == null) {
				getServiceClassDescriptorsForMetaModel(mmDescriptor).put(newServiceClassDescriptor.getServiceType(), newServiceClassDescriptor);
			} else {
				if (newServiceClassDescriptor.overrides(existingServiceClassDescriptorWithSameMMDescAndSameType)) {
					getServiceClassDescriptorsForMetaModel(mmDescriptor).put(newServiceClassDescriptor.getServiceType(), newServiceClassDescriptor);
				} else if (existingServiceClassDescriptorWithSameMMDescAndSameType.overrides(newServiceClassDescriptor)) {
					// Nothing to do
				} else {
					// Conflicting services not overriding each other
					logWarning(Messages.metamodelservice_ServiceAlreadyExists, newServiceClassDescriptor.getServiceType(),
							mmDescriptor.getIdentifier());
				}
			}
		} catch (IllegalArgumentException ex) {
			logWarning(ex);
		}
	}

	/**
	 * Registers a service with this <code>MetaModelServiceRegistry</code> for a metamodel.
	 *
	 * @param mmDescriptor
	 *            the metamodel for which the service is to be registered.
	 * @param serviceType
	 *            the type of the service
	 * @param serviceClass
	 *            the class which implements the service
	 */
	protected void addService(IMetaModelDescriptor mmDescriptor, Class<IMetaModelService> serviceType, Class<? extends IMetaModelService> serviceClass) {
		Assert.isLegal(!serviceClass.isInterface());

		ServiceClassDescriptor serviceClassDesc = new ServiceClassDescriptor(serviceType, serviceClass);
		getServiceClassDescriptorsForMetaModel(mmDescriptor).put(serviceType, serviceClassDesc);
	}

	/**
	 * Returns the service registration for a metamodel.
	 *
	 * @param mmDescriptor
	 *            the metamodel descriptor of the metamodel for which to return the registration.
	 * @param serviceType
	 *            the type of the service.
	 * @return the service class descriptor or <code>null</code> if no service class descriptor is found for the given
	 *         metamodel
	 */
	private ServiceClassDescriptor getServiceClassDescriptor(IMetaModelDescriptor mmDescriptor, Class<IMetaModelService> serviceType) {
		return getServiceClassDescriptorsForMetaModel(mmDescriptor).get(serviceType);
	}

	/**
	 * Returns all service class descriptors for a metamodel.
	 *
	 * @param mmDescriptor
	 *            the metamodel descriptor of the metamodel for which to return the registrations.
	 * @return all service class descriptors for the specified metamodel
	 */
	private Map<Class<IMetaModelService>, ServiceClassDescriptor> getServiceClassDescriptorsForMetaModel(IMetaModelDescriptor mmDescriptor) {
		if (!mmServices.containsKey(mmDescriptor)) {
			mmServices.put(mmDescriptor, new HashMap<Class<IMetaModelService>, ServiceClassDescriptor>());
		}
		return mmServices.get(mmDescriptor);
	}
}
