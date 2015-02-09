/**
 * <copyright>
 *
 * Copyright (c) BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BMW Car IT - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.metamodel.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.metamodel.services.IMetaModelService;
import org.eclipse.sphinx.platform.util.ExtensionClassDescriptor;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ServiceClassDescriptor extends ExtensionClassDescriptor<IMetaModelService> {

	private final static String ATTR_TYPE = "type"; //$NON-NLS-1$

	private final static String NODE_APPLICABLE_FOR = "applicableFor"; //$NON-NLS-1$

	private final static String ATTR_META_MODEL_DESCRIPTOR_ID_PATTERN = "metaModelDescriptorIdPattern"; //$NON-NLS-1$

	private String typeName;
	private Class<IMetaModelService> serviceType;
	private Set<String> mmDescIdPatterns;

	public ServiceClassDescriptor(IConfigurationElement configurationElement) {
		super(configurationElement);

		typeName = configurationElement.getAttribute(ATTR_TYPE);
		Assert.isNotNull(typeName, Messages.error_missingMetaModelServiceType);

		initMetaModelDescIdPatterns(configurationElement);
	}

	private void initMetaModelDescIdPatterns(IConfigurationElement configurationElement) {
		// Create the Set
		mmDescIdPatterns = new HashSet<String>();
		// Add mmDescIdPattern to the Set
		IConfigurationElement[] applicableForElements = configurationElement.getChildren(NODE_APPLICABLE_FOR);

		for (IConfigurationElement applicableFor : applicableForElements) {
			String mmDescIdPattern = applicableFor.getAttribute(ATTR_META_MODEL_DESCRIPTOR_ID_PATTERN);
			// Missing mmDescIdPattern, continue
			if (mmDescIdPattern == null) {
				continue;
			}
			mmDescIdPatterns.add(mmDescIdPattern);
		}
	}

	public String getTypeName() {
		return typeName;
	}

	@SuppressWarnings("unchecked")
	public Class<IMetaModelService> getServiceType() {
		if (serviceType == null) {
			try {
				serviceType = (Class<IMetaModelService>) Platform.getBundle(getContributorPluginId()).loadClass(typeName);
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return serviceType;
	}

	public List<IMetaModelDescriptor> getMetaModelDescriptors() {
		List<IMetaModelDescriptor> mmDescriptors = new ArrayList<IMetaModelDescriptor>();
		for (String mmDescIdPattern : mmDescIdPatterns) {
			// Handle the case which contribute a service for any meta-model
			if (".*".equals(mmDescIdPattern) || ".+".equals(mmDescIdPattern)) { //$NON-NLS-1$ //$NON-NLS-2$
				mmDescriptors.add(MetaModelDescriptorRegistry.ANY_MM);
			} else {
				// Locate the corresponding meta-model descriptors
				mmDescriptors.addAll(MetaModelDescriptorRegistry.INSTANCE.getDescriptors(mmDescIdPattern));
			}
		}
		return mmDescriptors;
	}

	public Set<String> getUnknownMetaModelDescIdPatterns() {
		Set<String> result = new HashSet<String>();
		if (mmDescIdPatterns.isEmpty()) {
			return Collections.emptySet();
		}
		for (String mmDescIdPattern : mmDescIdPatterns) {
			if (!(".*".equals(mmDescIdPattern) || ".+".equals(mmDescIdPattern)) //$NON-NLS-1$ //$NON-NLS-2$
					&& MetaModelDescriptorRegistry.INSTANCE.getDescriptors(mmDescIdPattern).isEmpty()) {
				result.add(mmDescIdPattern);
			}
		}
		return result;
	}
}
