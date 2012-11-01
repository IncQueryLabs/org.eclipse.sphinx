/**
 * <copyright>
 * 
 * Copyright (c) 2011-2012 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [393312] Make sure that transient item providers created by extended item providers can be used before the getChildren() method of the latter has been called
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.extended;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptorDecorator;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.sphinx.emf.edit.ExtendedItemPropertyDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.edit.ConnectionItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.edit.InterfaceItemProvider;

public class ExtendedConnectionItemProvider extends ConnectionItemProvider {

	public ExtendedConnectionItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Object getParent(Object object) {
		Component component = (Component) super.getParent(object);
		ExtendedComponentItemProvider componentItemProvider = (ExtendedComponentItemProvider) adapterFactory.adapt(component,
				ITreeItemContentProvider.class);
		return componentItemProvider != null ? componentItemProvider.getOutgoingConnections(component) : null;
	}

	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		super.getChildrenFeatures(object);
		// Remove Description object as child of connection
		childrenFeatures.remove(Common20Package.Literals.IDENTIFIABLE__DESCRIPTION);
		// Display source port and target component references as children
		childrenFeatures.add(InstanceModel20Package.Literals.CONNECTION__SOURCE_PORT);
		childrenFeatures.add(InstanceModel20Package.Literals.CONNECTION__TARGET_COMPONENT);

		return childrenFeatures;
	}

	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		// for refreshing the view if source port and/or target component references are updated
		switch (notification.getFeatureID(Connection.class)) {
		case InstanceModel20Package.CONNECTION__SOURCE_PORT:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
		case InstanceModel20Package.CONNECTION__TARGET_COMPONENT:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);

	}

	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			Port sourcePort = ((Connection) object).getSourcePort();
			if (sourcePort != null) {
				addRequiredInterface(sourcePort.getRequiredInterface(), getString("_UI_Port_requiredInterface_feature")); //$NON-NLS-1$				
			}
		}
		return itemPropertyDescriptors;
	}

	private void addRequiredInterface(Interface requiredInterface, final String featureName) {
		InterfaceItemProvider interfaceItemProvider = (InterfaceItemProvider) ((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory()
				.adapt(requiredInterface, IItemPropertySource.class);
		if (interfaceItemProvider != null) {
			List<IItemPropertyDescriptor> descriptors = interfaceItemProvider.getPropertyDescriptors(requiredInterface);
			for (IItemPropertyDescriptor descriptor : descriptors) {
				itemPropertyDescriptors.add(new ItemPropertyDescriptorDecorator(requiredInterface, descriptor) {
					@Override
					public String getCategory(Object thisObject) {
						return featureName;
					}

					@Override
					public String getId(Object thisObject) {
						return featureName + getDisplayName(thisObject);
					}
				});
			}
		}
	}

	@Override
	protected void addSourcePortPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(new ExtendedItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(), getString("_UI_Connection_sourcePort_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", "_UI_Connection_sourcePort_feature", "_UI_Connection_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				InstanceModel20Package.Literals.CONNECTION__SOURCE_PORT, true, false, true, null, null, null) {
			@Override
			public Collection<?> getChoiceOfValues(Object object) {
				Connection connection = (Connection) object;

				// No option if the connection is not correctly defined or the components don't have a type
				Component sourceComponent = connection.getSourceComponent();
				if (sourceComponent == null || sourceComponent.getType() == null) {
					return null;
				}
				Component targetComponent = connection.getTargetComponent();
				if (targetComponent == null || targetComponent.getType() == null) {
					return null;
				}

				// Return ports that fit target component provided interfaces
				List<Port> sourcePorts = sourceComponent.getType().getPorts();
				List<Port> availablePorts = new ArrayList<Port>();
				for (Port port : sourcePorts) {
					if (targetComponent.getType().getProvidedInterfaces() != null && port.getRequiredInterface() != null
							&& targetComponent.getType().getProvidedInterfaces().contains(port.getRequiredInterface())) {
						availablePorts.add(port);
					}
				}
				return availablePorts;
			}
		});
	}
}
