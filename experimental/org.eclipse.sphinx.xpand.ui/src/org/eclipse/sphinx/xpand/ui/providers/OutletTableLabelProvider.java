/**
 * <copyright>
 * 
 * Copyright (c) See4sys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xpand.ui.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.sphinx.xpand.outlet.ExtendedOutlet;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xpand2.output.Outlet;

public class OutletTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof Outlet) {
			ExtendedOutlet outlet = (ExtendedOutlet) element;
			switch (columnIndex) {
			case 0:
				return outlet.getName() == null ? "<default>" : outlet.getName();//$NON-NLS-1$
			case 1:
				return outlet.getPathExpression();
			default:
				return ""; //$NON-NLS-1$
			}
		}
		return element.toString();
	}

}
