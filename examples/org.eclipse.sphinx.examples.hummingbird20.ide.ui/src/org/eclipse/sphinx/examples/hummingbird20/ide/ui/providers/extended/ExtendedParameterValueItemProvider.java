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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.edit.ParameterValueItemProvider;

public class ExtendedParameterValueItemProvider extends ParameterValueItemProvider {

	public ExtendedParameterValueItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Object getParent(Object object) {
		Object component = super.getParent(object);
		ExtendedComponentItemProvider componentItemProvider = (ExtendedComponentItemProvider) adapterFactory.adapt(component,
				IEditingDomainItemProvider.class);
		return componentItemProvider != null ? componentItemProvider.getParameterValues() : null;
	}
}
