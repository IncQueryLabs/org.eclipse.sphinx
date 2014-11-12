/**
 * <copyright>
 *
 * Copyright (c) 2011-2014 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [447193] Enable transient item providers to be created through adapter factories
 *     itemis - [450882] Enable navigation to ancestor tree items in Model Explorer kind of model views
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird10.ide.ui.providers;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.Disposable;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.examples.hummingbird10.edit.Hummingbird10ItemProviderAdapterFactory;

public class ExtendedHummingbird10ItemProviderAdapterFactory extends Hummingbird10ItemProviderAdapterFactory {

	protected Disposable disposable = new Disposable();

	@Override
	public Adapter createComponentAdapter() {
		if (componentItemProvider == null) {
			componentItemProvider = new ExtendedComponentItemProvider(this);
		}
		return componentItemProvider;
	}

	@Override
	public Adapter createParameterAdapter() {
		if (parameterItemProvider == null) {
			parameterItemProvider = new ExtendedParameterItemProvider(this);
		}
		return parameterItemProvider;
	}

	@Override
	public Adapter createConnectionAdapter() {
		if (connectionItemProvider == null) {
			connectionItemProvider = new ExtendedConnectionItemProvider(this);
		}
		return connectionItemProvider;
	}

	@Override
	public Object adapt(Object target, Object type) {
		Object adapter = TransientItemProvider.AdapterFactoryHelper.adapt(target, type, this);
		if (adapter != null) {
			disposable.add(adapter);
			return adapter;
		}
		return super.adapt(target, type);
	}

	@Override
	protected Adapter createAdapter(Notifier target, Object type) {
		if (type == ParametersItemProvider.class) {
			return new ParametersItemProvider(this);
		}
		if (type == OutgoingConnectionsItemProvider.class) {
			return new OutgoingConnectionsItemProvider(this);
		}
		return super.createAdapter(target, type);
	}

	@Override
	public void dispose() {
		disposable.dispose();
		super.dispose();
	}
}
