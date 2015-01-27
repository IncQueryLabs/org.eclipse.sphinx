/**
 * <copyright>
 *
 * Copyright (c) 2008-2014 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [457704] Integrate EMF compare 3.x in Sphinx
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.compare.ui.viewer.structuremerge;

import org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration;
import org.eclipse.emf.compare.ide.ui.internal.contentmergeviewer.tree.TreeContentMergeViewerContentProvider;

// FIXME don't need this class because EMFCompareStructureMergeViewerContentProvider is a private class cannot be extended
public class ModelElementStructureMergeContentProvider extends TreeContentMergeViewerContentProvider {

	public ModelElementStructureMergeContentProvider(EMFCompareConfiguration compareConfig) {
		super(compareConfig);
	}

	// FIXME
	// /**
	// * @param compareConfig
	// * The compare configuration.
	// */
	// public ModelElementContentMergeContentProvider(CompareConfiguration compareConfig) {
	// super(compareConfig);
	// }
	//
	// @Override
	// public Object getAncestorContent(Object element) {
	// Object ancestorElement = null;
	// if (element instanceof ModelCompareInput) {
	// ModelCompareInput input = (ModelCompareInput) element;
	// Object ancestorContentFromMatch = getAncestorContentFromMatch(input);
	// if (ancestorContentFromMatch != null) {
	// return ancestorContentFromMatch;
	// }
	// Object ancestorContentFromDiff = getAncestorContentFromDiff(input);
	// if (ancestorContentFromDiff != null) {
	// return ancestorContentFromDiff;
	// }
	// ITypedElement ancestor = input.getAncestor();
	// if (ancestor instanceof TypedElementWrapper) {
	// ancestorElement = ((TypedElementWrapper) ancestor).getObject();
	// } else {
	// ancestorElement = ancestor;
	// }
	// } else if (element instanceof ICompareInput) {
	// ancestorElement = ((ICompareInput) element).getAncestor();
	// }
	// return ancestorElement;
	// }
	//
	// @Override
	// public Object getLeftContent(Object element) {
	// Object leftElement = null;
	// if (element instanceof ModelCompareInput) {
	// ModelCompareInput input = (ModelCompareInput) element;
	// Object leftContentFromMatch = getLeftContentFromMatch(input);
	// if (leftContentFromMatch != null) {
	// return leftContentFromMatch;
	// }
	// Object leftContentFromDiff = getLeftContentFromDiff(input);
	// if (leftContentFromDiff != null) {
	// return leftContentFromDiff;
	// }
	// ITypedElement left = input.getLeft();
	// if (left instanceof TypedElementWrapper) {
	// leftElement = ((TypedElementWrapper) left).getObject();
	// } else {
	// leftElement = left;
	// }
	// } else if (element instanceof ICompareInput) {
	// leftElement = ((ICompareInput) element).getLeft();
	// }
	// return leftElement;
	// }
	//
	// @Override
	// public Object getRightContent(Object element) {
	// Object rightElement = null;
	// if (element instanceof ModelCompareInput) {
	// ModelCompareInput input = (ModelCompareInput) element;
	// Object rightContentFromMatch = getRightContentFromMatch(input);
	// if (rightContentFromMatch != null) {
	// return rightContentFromMatch;
	// }
	// Object rightContentFromDiff = getRightContentFromDiff(input);
	// if (rightContentFromDiff != null) {
	// return rightContentFromDiff;
	// }
	// ITypedElement right = input.getRight();
	// if (right instanceof TypedElementWrapper) {
	// rightElement = ((TypedElementWrapper) right).getObject();
	// } else {
	// rightElement = right;
	// }
	// } else if (element instanceof ICompareInput) {
	// rightElement = ((ICompareInput) element).getRight();
	// }
	// return rightElement;
	// }
	//
	// protected Object getLeftContentFromMatch(ModelCompareInput input) {
	// Object match = input.getMatch();
	// if (match instanceof MatchModel) {
	// MatchModel matchModel = (MatchModel) match;
	// if (matchModel.getLeftRoots() != null && !matchModel.getLeftRoots().isEmpty()) {
	// return matchModel.getLeftRoots().get(0);
	// }
	// }
	// return null;
	// }
	//
	// protected Object getLeftContentFromDiff(ModelCompareInput input) {
	// Object diff = input.getDiff();
	// if (diff instanceof DiffModel) {
	// DiffModel diffModel = (DiffModel) diff;
	// if (diffModel.getLeftRoots() != null && !diffModel.getLeftRoots().isEmpty()) {
	// return diffModel.getLeftRoots().get(0);
	// }
	// }
	// return null;
	// }
	//
	// protected Object getRightContentFromMatch(ModelCompareInput input) {
	// Object match = input.getMatch();
	// if (match instanceof MatchModel) {
	// MatchModel matchModel = (MatchModel) match;
	// if (matchModel.getRightRoots() != null && !matchModel.getRightRoots().isEmpty()) {
	// return matchModel.getRightRoots().get(0);
	// }
	// }
	// return null;
	// }
	//
	// protected Object getRightContentFromDiff(ModelCompareInput input) {
	// Object diff = input.getDiff();
	// if (diff instanceof DiffModel) {
	// DiffModel diffModel = (DiffModel) diff;
	// if (diffModel.getRightRoots() != null && !diffModel.getRightRoots().isEmpty()) {
	// return diffModel.getRightRoots().get(0);
	// }
	// }
	// return null;
	// }
	//
	// protected Object getAncestorContentFromMatch(ModelCompareInput input) {
	// Object match = input.getMatch();
	// if (match instanceof MatchModel) {
	// MatchModel matchModel = (MatchModel) match;
	// if (matchModel.getAncestorRoots() != null && !matchModel.getAncestorRoots().isEmpty()) {
	// return matchModel.getAncestorRoots().get(0);
	// }
	// }
	// return null;
	// }
	//
	// protected Object getAncestorContentFromDiff(ModelCompareInput input) {
	// Object diff = input.getDiff();
	// if (diff instanceof DiffModel) {
	// DiffModel diffModel = (DiffModel) diff;
	// if (diffModel.getAncestorRoots() != null && !diffModel.getAncestorRoots().isEmpty()) {
	// return diffModel.getAncestorRoots().get(0);
	// }
	// }
	// return null;
	// }
}
