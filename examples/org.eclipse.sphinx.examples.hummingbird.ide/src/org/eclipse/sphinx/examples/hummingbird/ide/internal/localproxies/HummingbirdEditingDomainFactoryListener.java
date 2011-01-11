/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.examples.hummingbird.ide.internal.localproxies;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.domain.factory.ITransactionalEditingDomainFactoryListener;
import org.eclipse.sphinx.emf.workspace.localproxies.LocalProxyChangeListener;

/**
 * 
 */
public class HummingbirdEditingDomainFactoryListener implements ITransactionalEditingDomainFactoryListener {
	private LocalProxyChangeListener localProxyChangeListener = new LocalProxyChangeListener();

	/**
	 * {@inheritDoc}
	 */
	public void postCreateEditingDomain(TransactionalEditingDomain editingDomain) {
		// Install local proxy management
		editingDomain.addResourceSetListener(localProxyChangeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void preDisposeEditingDomain(TransactionalEditingDomain editingDomain) {
		// Uninstall local proxy management
		editingDomain.removeResourceSetListener(localProxyChangeListener);
	}
}
