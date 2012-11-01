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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.sphinx.examples.hummingbird10.Component;
import org.eclipse.sphinx.examples.hummingbird10.edit.ConnectionItemProvider;

public class ExtendedConnectionItemProvider extends ConnectionItemProvider {

	public ExtendedConnectionItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Object getParent(Object object) {
		Component component = (Component) super.getParent(object);
		ExtendedComponentItemProvider componentItemProvider = (ExtendedComponentItemProvider) adapterFactory.adapt(component,
				IEditingDomainItemProvider.class);
		return componentItemProvider != null ? componentItemProvider.getOutgoingConnections(component) : null;
	}
}
