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
package org.eclipse.sphinx.emf.ui.properties.descriptors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.ui.properties.BasicTransactionalAdvancedPropertySection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;


public class BasicAdvancedSectionDescriptor extends AbstractSectionDescriptor {

	private String id;

	private String targetTab;

	private int ENABLE_FOR_ONE = 1;

	public BasicAdvancedSectionDescriptor(String id, String targetTab) {
		this.id = id;
		this.targetTab = targetTab;
	}

	public String getId() {

		return id;
	}

	public String getTargetTab() {

		return targetTab;
	}

	public ISection getSectionClass() {

		return new BasicTransactionalAdvancedPropertySection();
	}

	@Override
	public boolean appliesTo(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection && selection.isEmpty() == false) {
			if (getEnablesFor() != ISectionDescriptor.ENABLES_FOR_ANY && ((IStructuredSelection) selection).size() != getEnablesFor()) {
				/**
				 * enablesFor does not match the size of the selection, do not display section.
				 */
				return false;
			}
		}
		return true;
	}

	@Override
	public int getEnablesFor() {

		return ENABLE_FOR_ONE;
	}
}
