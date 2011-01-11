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

import org.eclipse.compare.contentmergeviewer.ContentMergeViewer;
import org.eclipse.emf.compare.ui.viewer.content.ModelContentMergeViewer;
import org.eclipse.emf.compare.ui.viewer.content.part.IModelContentMergeViewerTab;
import org.eclipse.emf.compare.ui.viewer.content.part.ModelContentMergeTabFolder;
import org.eclipse.swt.widgets.Composite;

/**
 * Describes a part of a {@linkplain ModelElementContentMergeViewer}.
 */
public class ModelElementContentMergeTabFolder extends ModelContentMergeTabFolder {

	/**
	 * Instantiates a {@linkplain ModelElementContentMergeTabFolder} given its parent {@link Composite} and its side.
	 * 
	 * @param viewer
	 *            Parent viewer of this viewer part.
	 * @param composite
	 *            Parent {@link Composite} for this part.
	 * @param side
	 *            Comparison side of this part. Must be one of {@link EMFCompareConstants#LEFT
	 *            EMFCompareConstants.RIGHT}, {@link EMFCompareConstants#RIGHT EMFCompareConstants.LEFT} or
	 *            {@link EMFCompareConstants#ANCESTOR EMFCompareConstants.ANCESTOR}.
	 */
	public ModelElementContentMergeTabFolder(ModelContentMergeViewer viewer, Composite composite, int side) {
		super(viewer, composite, side);
	}

	@Override
	protected IModelContentMergeViewerTab createModelContentMergeDiffTab(Composite parent) {
		return new ModelElementContentMergeDiffTab(parent, partSide, this);
	}

	/**
	 * @return The parent {@linkplain ContentMergeViewer viewer} of this {@linkplain ModelElementContentMergeTabFolder
	 *         tab folder}.
	 */
	public ContentMergeViewer getContentMergeViewer() {
		return parentViewer;
	}
}
