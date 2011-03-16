/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
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
package org.eclipse.sphinx.graphiti.workspace.ui.providers;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.sphinx.emf.explorer.BasicExplorerLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ElementTreeLabelProvider extends LabelProvider {
	ILabelProvider workbenchLabelProvider;
	ILabelProvider emfLabelProvider;

	public ElementTreeLabelProvider() {
		workbenchLabelProvider = WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();
		emfLabelProvider = new BasicExplorerLabelProvider();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IResource) {
			return workbenchLabelProvider.getText(element);
		}
		return emfLabelProvider.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IResource) {
			return workbenchLabelProvider.getImage(element);
		}
		return emfLabelProvider.getImage(element);
	}
}
