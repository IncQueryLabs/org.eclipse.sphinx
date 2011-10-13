/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.extended;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptorDecorator;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
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
		Object component = super.getParent(object);
		ExtendedComponentItemProvider componentItemProvider = (ExtendedComponentItemProvider) adapterFactory.adapt(component,
				IEditingDomainItemProvider.class);
		return componentItemProvider != null ? componentItemProvider.getOutgoingConnections() : null;
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
}
