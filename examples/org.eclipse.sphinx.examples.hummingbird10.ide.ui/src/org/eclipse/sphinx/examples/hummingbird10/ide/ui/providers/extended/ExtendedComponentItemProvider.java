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
package org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers.extended;

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
import org.eclipse.sphinx.examples.hummingbird10.Component;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;
import org.eclipse.sphinx.examples.hummingbird10.edit.ComponentItemProvider;
import org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers.OutgoingConnectionsItemProvider;
import org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers.ParametersItemProvider;

public class ExtendedComponentItemProvider extends ComponentItemProvider {

	private ParametersItemProvider parametersItemProvider;
	private OutgoingConnectionsItemProvider outgoingConnectionsItemProvider;

	public ExtendedComponentItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		super.getChildrenFeatures(object);
		childrenFeatures.remove(Hummingbird10Package.Literals.COMPONENT__PARAMETERS);
		childrenFeatures.remove(Hummingbird10Package.Literals.COMPONENT__OUTGOING_CONNECTIONS);
		return childrenFeatures;
	}

	@Override
	public Collection<?> getChildren(Object object) {
		List<Object> children = new ArrayList<Object>(super.getChildren(object));
		children.add(getParameters((Component) object));
		children.add(getOutgoingConnections((Component) object));
		return children;
	}

	public ParametersItemProvider getParameters(Component component) {
		if (parametersItemProvider == null) {
			parametersItemProvider = new ParametersItemProvider(adapterFactory, component);
		}
		return parametersItemProvider;
	}

	public OutgoingConnectionsItemProvider getOutgoingConnections(Component component) {
		if (outgoingConnectionsItemProvider == null) {
			outgoingConnectionsItemProvider = new OutgoingConnectionsItemProvider(adapterFactory, component);
		}
		return outgoingConnectionsItemProvider;
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
		if (feature == Hummingbird10Package.Literals.COMPONENT__PARAMETERS
				|| feature == Hummingbird10Package.Literals.COMPONENT__OUTGOING_CONNECTIONS) {
			return new CommandWrapper(command) {
				@Override
				public Collection<?> getAffectedObjects() {
					Collection<?> affected = super.getAffectedObjects();
					if (affected.contains(owner)) {
						affected = Collections
								.singleton(feature == Hummingbird10Package.Literals.COMPONENT__PARAMETERS ? getParameters((Component) owner)
										: getOutgoingConnections((Component) owner));
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
		if (parametersItemProvider != null && outgoingConnectionsItemProvider != null) {
			((IDisposable) parametersItemProvider).dispose();
			((IDisposable) outgoingConnectionsItemProvider).dispose();
		}
	}
}
