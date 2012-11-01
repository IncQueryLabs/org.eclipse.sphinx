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
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandWrapper;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.ParametersItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.PortsItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.edit.ComponentTypeItemProvider;

public class ExtendedComponentTypeItemProvider extends ComponentTypeItemProvider {

	private ParametersItemProvider parametersItemProvider;
	private PortsItemProvider portsItemProvider;

	public ExtendedComponentTypeItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Object getParent(Object object) {
		Platform platform = (Platform) super.getParent(object);
		ExtendedPlatformItemProvider platformItemProvider = (ExtendedPlatformItemProvider) adapterFactory.adapt(platform,
				ITreeItemContentProvider.class);
		return platformItemProvider != null ? platformItemProvider.getComponentTypes(platform) : null;
	}

	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		super.getChildrenFeatures(object);
		childrenFeatures.remove(TypeModel20Package.Literals.COMPONENT_TYPE__PARAMETERS);
		childrenFeatures.remove(TypeModel20Package.Literals.COMPONENT_TYPE__PORTS);
		return childrenFeatures;
	}

	@Override
	public Collection<?> getChildren(Object object) {
		List<Object> children = new ArrayList<Object>(super.getChildren(object));
		children.add(getParameters((ComponentType) object));
		children.add(getPorts((ComponentType) object));
		return children;
	}

	public ParametersItemProvider getParameters(ComponentType componentType) {
		if (parametersItemProvider == null) {
			parametersItemProvider = new ParametersItemProvider(adapterFactory, componentType);
		}
		return parametersItemProvider;
	}

	public PortsItemProvider getPorts(ComponentType componentType) {
		if (portsItemProvider == null) {
			portsItemProvider = new PortsItemProvider(adapterFactory, componentType);
		}
		return portsItemProvider;
	}

	@Override
	protected Command createAddCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Collection<?> collection, int index) {
		return createWrappedCommand(super.createAddCommand(domain, owner, feature, collection, index), owner, feature);
	}

	@Override
	protected Command createRemoveCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Collection<?> collection) {
		return createWrappedCommand(super.createRemoveCommand(domain, owner, feature, collection), owner, feature);
	}

	protected Command createWrappedCommand(Command command, final EObject owner, final EStructuralFeature feature) {
		if (feature == TypeModel20Package.Literals.COMPONENT_TYPE__PARAMETERS || feature == TypeModel20Package.Literals.COMPONENT_TYPE__PORTS) {
			return new CommandWrapper(command) {
				@Override
				public Collection<?> getAffectedObjects() {
					Collection<?> affected = super.getAffectedObjects();
					if (affected.contains(owner)) {
						affected = Collections
								.singleton(feature == TypeModel20Package.Literals.COMPONENT_TYPE__PARAMETERS ? getParameters((ComponentType) owner)
										: getPorts((ComponentType) owner));
					}
					return affected;
				}
			};
		}
		return command;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (parametersItemProvider != null && portsItemProvider != null) {
			((IDisposable) parametersItemProvider).dispose();
			((IDisposable) portsItemProvider).dispose();
		}
	}
}
