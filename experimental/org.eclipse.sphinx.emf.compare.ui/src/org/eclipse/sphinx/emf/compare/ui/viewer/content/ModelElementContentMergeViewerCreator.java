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
package org.eclipse.sphinx.emf.compare.ui.viewer.content;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * Required when creating a {@link ModelElementContentMergeViewer} from a plugin.xml file.
 */
public class ModelElementContentMergeViewerCreator implements IViewerCreator {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Viewer createViewer(Composite parent, CompareConfiguration config) {
		return new ModelElementContentMergeViewer(parent, config);
	}
}
