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
import org.eclipse.emf.edit.command.PasteFromClipboardCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.PasteAction;

public class ExtendedPasteAction extends PasteAction {

	protected AdapterFactory customAdapterFactory;

	public ExtendedPasteAction(EditingDomain domain, AdapterFactory customAdapterFactory) {
		super(domain);
		this.customAdapterFactory = customAdapterFactory;
	}

	public ExtendedPasteAction(AdapterFactory customAdapterFactory) {
		this(null, customAdapterFactory);
	}

	@Override
	public Command createCommand(Collection<?> selection) {
		if (selection.size() == 1 && domain != null) {
			AdapterFactory oldAdapterFactory = null;
			if (customAdapterFactory != null) {
				oldAdapterFactory = ((AdapterFactoryEditingDomain) domain).getAdapterFactory();
				((AdapterFactoryEditingDomain) domain).setAdapterFactory(customAdapterFactory);
			}
			Command command = PasteFromClipboardCommand.create(domain, selection.iterator().next(), null);
			if (oldAdapterFactory != null) {
				((AdapterFactoryEditingDomain) domain).setAdapterFactory(oldAdapterFactory);
			}
			return command;
		} else {
			return UnexecutableCommand.INSTANCE;
		}
	}
}
