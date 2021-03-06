/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     See4sys - added support for problem markers on model objects (rather than 
 *               only on workspace resources). Unfortunately, there was no other 
 *               choice than copying the whole code from 
 *               org.eclipse.ui.views.markers.internal for that purpose because 
 *               many of the relevant classes, methods, and fields are private or
 *               package private.
 *******************************************************************************/
package org.eclipse.sphinx.emf.validation.ui.views;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * ActionMarkerProperties is the action for opening a properties dialog.
 */
public class ActionMarkerProperties extends SelectionProviderAction {

	private IWorkbenchPart part;

	private String markerName;

	/**
	 * Create a new instance of the receiver.
	 * 
	 * @param part
	 * @param provider
	 * @param markerName
	 *            the name describing the specific type of marker.
	 */
	public ActionMarkerProperties(IWorkbenchPart part, ISelectionProvider provider, String markerName) {
		super(provider, MarkerMessages.propertiesAction_title);
		setEnabled(false);
		this.part = part;
		this.markerName = markerName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (!isEnabled()) {
			return;
		}
		Object obj = getStructuredSelection().getFirstElement();
		if (!(obj instanceof ConcreteMarker)) {
			return;
		}
		ConcreteMarker marker = (ConcreteMarker) obj;
		DialogMarkerProperties dialog = new DialogMarkerProperties(part.getSite().getShell(), MarkerMessages.propertiesDialog_title, markerName);
		dialog.setMarker(marker.getMarker());
		dialog.open();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.actions.SelectionProviderAction#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(selection != null && selection.size() == 1);
	}
}
