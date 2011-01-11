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
package org.eclipse.sphinx.emf.ui.actions;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.DeleteAction;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.edit.LocalProxyChangeListener;
import org.eclipse.sphinx.platform.util.ReflectUtil;

public class ExtendedDeleteAction extends DeleteAction {

	protected AdapterFactory customAdapterFactory;

	public ExtendedDeleteAction(EditingDomain domain, boolean removeAllReferences, AdapterFactory customAdapterFactory) {
		super(domain, removeAllReferences);
		this.customAdapterFactory = customAdapterFactory;
	}

	public ExtendedDeleteAction(boolean removeAllReferences, AdapterFactory customAdapterFactory) {
		this(null, removeAllReferences, customAdapterFactory);
	}

	@Override
	public Command createCommand(Collection<?> selection) {
		if (domain != null) {
			// Don't clean up all references to deleted model object if editing domain supports converting it into a
			// proxy
			if (supportsProxyficationOfRemovedElements(domain)) {
				removeAllReferences = false;
			}
			AdapterFactory oldAdapterFactory = null;
			if (customAdapterFactory != null) {
				oldAdapterFactory = ((AdapterFactoryEditingDomain) domain).getAdapterFactory();
				((AdapterFactoryEditingDomain) domain).setAdapterFactory(customAdapterFactory);
			}
			Command command = super.createCommand(selection);
			if (oldAdapterFactory != null) {
				((AdapterFactoryEditingDomain) domain).setAdapterFactory(oldAdapterFactory);
			}
			return command;
		}
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * Tests if given {@link EditingDomain editing domain} supports automatic conversion of removed model objects into
	 * proxies.
	 * 
	 * @param domain
	 *            The {@link EditingDomain editing domain} to be investigated.
	 * @return <code>true</code> if given {@link EditingDomain editing domain} supports automatic conversion of removed
	 *         model objects into proxies, or <code>false</code> otherwise.
	 * @see LocalProxyChangeListener
	 */
	protected boolean supportsProxyficationOfRemovedElements(EditingDomain domain) {
		if (domain instanceof TransactionalEditingDomain) {
			try {
				ResourceSetListener[] listeners = (ResourceSetListener[]) ReflectUtil.invokeInvisibleMethod(domain, "getPostcommitListeners");//$NON-NLS-1$
				for (ResourceSetListener listner : listeners) {
					if (listner instanceof LocalProxyChangeListener) {
						return true;
					}
				}
			} catch (Exception ex) {
				// Ignore exception
			}
		}
		return false;
	}
}
