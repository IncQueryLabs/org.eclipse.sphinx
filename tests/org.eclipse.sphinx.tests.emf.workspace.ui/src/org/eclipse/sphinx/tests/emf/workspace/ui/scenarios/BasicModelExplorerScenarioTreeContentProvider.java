/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.workspace.ui.scenarios;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.edit.ExtendedDelegatingWrapperItemProvider;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;

public class BasicModelExplorerScenarioTreeContentProvider implements ITreeContentProvider {

	private AdapterFactory adapterFactory = new AdapterFactoryImpl();

	protected TransientItemProvider createTransientItemProvider() {
		return new TransientItemProvider(adapterFactory);
	}

	protected IWrapperItemProvider createWrapperItemProvider(Object value) {
		return new ExtendedDelegatingWrapperItemProvider(value, null, null, -1, adapterFactory);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public void dispose() {
	}
}
