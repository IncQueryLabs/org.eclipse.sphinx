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
package org.eclipse.sphinx.emf.compare.ui.viewer.structuremerge;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * Required when creating a {@link ModelCompareStructureMergeViewer} from a plugin.xml file.
 */
public class ModelCompareStructureMergeViewerCreator implements IViewerCreator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Viewer createViewer(Composite parent, CompareConfiguration config) {
		return new ModelCompareStructureMergeViewer(parent, new EMFCompareConfiguration(config));
	}
}
