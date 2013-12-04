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
import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSetSnapshot;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.ComparisonSnapshot;
import org.eclipse.emf.compare.ui.ICompareInputDetailsProvider;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.compare.ui.viewer.content.ModelContentMergeViewer;
import org.eclipse.emf.compare.ui.viewer.content.part.ModelContentMergeTabFolder;
import org.eclipse.sphinx.emf.compare.ui.ModelElementCompareInput;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 */
public class ModelElementContentMergeViewer extends ModelContentMergeViewer {

	/**
	 * @param parent
	 * @param config
	 */
	public ModelElementContentMergeViewer(Composite parent, CompareConfiguration config) {
		super(parent, config);
	}

	@Override
	protected IMergeViewerContentProvider createMergeViewerContentProvider() {
		return new ModelElementContentMergeContentProvider(configuration);
	}

	@Override
	protected ModelContentMergeTabFolder createModelContentMergeTabFolder(Composite composite, int side) {
		return new ModelElementContentMergeTabFolder(this, composite, side);
	}

	@Override
	protected ModelCompareInput createModelCompareInput(ICompareInputDetailsProvider provider, ComparisonSnapshot snapshot) {
		if (snapshot instanceof ComparisonResourceSetSnapshot) {
			return new ModelElementCompareInput(((ComparisonResourceSetSnapshot) snapshot).getMatchResourceSet(),
					((ComparisonResourceSetSnapshot) snapshot).getDiffResourceSet(), provider);
		}
		return new ModelElementCompareInput(((ComparisonResourceSnapshot) snapshot).getMatch(), ((ComparisonResourceSnapshot) snapshot).getDiff(),
				provider);
	}
}
