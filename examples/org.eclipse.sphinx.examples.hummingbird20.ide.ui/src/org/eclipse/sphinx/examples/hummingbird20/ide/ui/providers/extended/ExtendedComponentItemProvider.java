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
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.OutgoingConnectionsItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.ParameterValuesItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.edit.ComponentItemProvider;

public class ExtendedComponentItemProvider extends ComponentItemProvider {

	private ParameterValuesItemProvider parameterValuesItemProvider;
	private OutgoingConnectionsItemProvider outgoingConnectionsItemProvider;

	public ExtendedComponentItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		super.getChildrenFeatures(object);
		childrenFeatures.remove(InstanceModel20Package.Literals.COMPONENT__PARAMETER_VALUES);
		childrenFeatures.remove(InstanceModel20Package.Literals.COMPONENT__OUTGOING_CONNECTIONS);
		return childrenFeatures;
	}

	@Override
	public Collection<?> getChildren(Object object) {
		if (parameterValuesItemProvider == null) {
			parameterValuesItemProvider = new ParameterValuesItemProvider(adapterFactory, (Component) object);
		}

		if (outgoingConnectionsItemProvider == null) {
			outgoingConnectionsItemProvider = new OutgoingConnectionsItemProvider(adapterFactory, (Component) object);
		}

		List<Object> children = new ArrayList<Object>(super.getChildren(object));
		children.add(parameterValuesItemProvider);
		children.add(outgoingConnectionsItemProvider);

		return children;
	}

	public Object getParameterValues() {
		return parameterValuesItemProvider;
	}

	public Object getOutgoingConnections() {
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
		if (feature == InstanceModel20Package.Literals.COMPONENT__PARAMETER_VALUES
				|| feature == InstanceModel20Package.Literals.COMPONENT__OUTGOING_CONNECTIONS) {
			return new CommandWrapper(command) {
				@Override
				public Collection<?> getAffectedObjects() {
					Collection<?> affected = super.getAffectedObjects();
					if (affected.contains(owner)) {
						affected = Collections
								.singleton(feature == InstanceModel20Package.Literals.COMPONENT__PARAMETER_VALUES ? getParameterValues()
										: getOutgoingConnections());
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
		if (parameterValuesItemProvider != null && outgoingConnectionsItemProvider != null) {
			((IDisposable) parameterValuesItemProvider).dispose();
			((IDisposable) outgoingConnectionsItemProvider).dispose();
		}
	}
}
