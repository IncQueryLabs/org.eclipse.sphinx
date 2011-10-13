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
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.ComponentTypesItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.InterfacesItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.edit.PlatformItemProvider;

public class ExtendedPlatformItemProvider extends PlatformItemProvider {

	private ComponentTypesItemProvider componentTypesItemProvider;
	private InterfacesItemProvider interfacesItemProvider;

	public ExtendedPlatformItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		super.getChildrenFeatures(object);
		childrenFeatures.remove(TypeModel20Package.Literals.PLATFORM__COMPONENT_TYPES);
		childrenFeatures.remove(TypeModel20Package.Literals.PLATFORM__INTERFACES);
		return childrenFeatures;
	}

	@Override
	public Collection<?> getChildren(Object object) {
		if (componentTypesItemProvider == null) {
			componentTypesItemProvider = new ComponentTypesItemProvider(adapterFactory, (Platform) object);
		}

		if (interfacesItemProvider == null) {
			interfacesItemProvider = new InterfacesItemProvider(adapterFactory, (Platform) object);
		}

		List<Object> children = new ArrayList<Object>(super.getChildren(object));
		children.add(componentTypesItemProvider);
		children.add(interfacesItemProvider);

		return children;
	}

	public Object getComponentTypes() {
		return componentTypesItemProvider;
	}

	public Object getInterfaces() {
		return interfacesItemProvider;
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
		if (feature == TypeModel20Package.Literals.PLATFORM__COMPONENT_TYPES || feature == TypeModel20Package.Literals.PLATFORM__INTERFACES) {
			return new CommandWrapper(command) {
				@Override
				public Collection<?> getAffectedObjects() {
					Collection<?> affected = super.getAffectedObjects();
					if (affected.contains(owner)) {
						affected = Collections.singleton(feature == TypeModel20Package.Literals.PLATFORM__COMPONENT_TYPES ? getComponentTypes()
								: getInterfaces());
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
		if (componentTypesItemProvider != null && interfacesItemProvider != null) {
			((IDisposable) componentTypesItemProvider).dispose();
			((IDisposable) interfacesItemProvider).dispose();
		}
	}
}
