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

import org.eclipse.compare.IResourceProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.compare.ui.TypedElementWrapper;
import org.eclipse.emf.compare.ui.util.EMFCompareConstants;
import org.eclipse.emf.compare.ui.viewer.content.part.ModelContentMergeTabFolder;
import org.eclipse.emf.compare.ui.viewer.content.part.diff.ModelContentMergeDiffTab;
import org.eclipse.emf.compare.util.AdapterUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.sphinx.emf.compare.ui.internal.Activator;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Composite;

/**
 * Represents the tree view under a {@linkplain ModelElementContentMergeTabFolder}'s diff tab.
 */
public class ModelElementContentMergeDiffTab extends ModelContentMergeDiffTab {

	/**
	 * Creates a tree viewer under the given parent control.
	 * 
	 * @param parentComposite
	 *            The parent {@link Composite} for this tree viewer.
	 * @param side
	 *            Side of this viewer part.
	 * @param parentFolder
	 *            Parent folder of this tab.
	 */
	public ModelElementContentMergeDiffTab(Composite parentComposite, int side, ModelContentMergeTabFolder parentFolder) {
		super(parentComposite, side, parentFolder);

		if (parent instanceof ModelElementContentMergeTabFolder) {
			final ModelElementContentMergeTabFolder tabFolder = (ModelElementContentMergeTabFolder) parent;
			addFilter(new ViewerFilter() {
				/**
				 * {@inheritDoc}
				 * <p>
				 * Element selection criteria:
				 * <ol>
				 * the root input element of the diff model (from the corresponding side) must be one of the ancestors
				 * of the specified <code>element</code>.
				 * </ol>
				 */
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (!(element instanceof EObject)) {
						return false;
					}
					Object input = tabFolder.getContentMergeViewer().getInput();
					if (input instanceof ModelCompareInput) {
						ModelCompareInput mcInput = (ModelCompareInput) input;
						Object typedElement = null;
						switch (partSide) {
						case EMFCompareConstants.LEFT:
							Object leftFromMatch = getLeftFromMatch(mcInput);
							if (leftFromMatch != null) {
								typedElement = leftFromMatch;
								break;
							}
							Object leftFromDiff = getLeftFromDiff(mcInput);
							if (leftFromDiff != null) {
								typedElement = leftFromDiff;
								break;
							}
							typedElement = mcInput.getLeft();
							break;
						case EMFCompareConstants.RIGHT:
							Object rightFromMatch = getRightFromMatch(mcInput);
							if (rightFromMatch != null) {
								typedElement = rightFromMatch;
								break;
							}
							Object rightFromDiff = getRightFromDiff(mcInput);
							if (rightFromDiff != null) {
								typedElement = rightFromDiff;
								break;
							}
							typedElement = mcInput.getRight();
							break;
						case EMFCompareConstants.ANCESTOR:
							Object ancestorFromMatch = getAncestorFromMatch(mcInput);
							if (ancestorFromMatch != null) {
								typedElement = ancestorFromMatch;
								break;
							}
							Object ancestorFromDiff = getAncestorFromDiff(mcInput);
							if (ancestorFromDiff != null) {
								typedElement = ancestorFromDiff;
								break;
							}
							typedElement = mcInput.getAncestor();
							break;
						}
						if (typedElement instanceof EObject) {
							EObject obj = (EObject) typedElement;
							return EcoreUtil.isAncestor(obj, (EObject) element);
						} else if (typedElement instanceof TypedElementWrapper) {
							EObject inputObject = ((TypedElementWrapper) typedElement).getObject();
							return EcoreUtil.isAncestor(inputObject, (EObject) element);
						} else if (typedElement instanceof IResourceProvider) {
							return true;
						}
					}
					return false;
				}
			});
		}
	}

	@Override
	public void setReflectiveInput(Object input) {
		// We *need* to invalidate the cache here since setInput() would try to
		// use it otherwise
		clearCaches();

		final AdapterFactory adapterFactory = AdapterUtils.getAdapterFactory();
		setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		if (input instanceof EObject) {
			EObject eContainer = ((EObject) input).eContainer();
			if (eContainer != null) {
				setInput(eContainer);
			} else {
				setInput(((EObject) input).eResource());
			}
		} else if (input instanceof IResourceProvider) {
			IFile resource = (IFile) ((IResourceProvider) input).getResource();
			setInput(EcorePlatformUtil.getResource(resource));
		}

		try {
			setupCaches();
		} catch (RuntimeException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException("Caches initialization failed.", ex)); //$NON-NLS-1$
		}
		needsRedraw = true;
	}

	protected Object getLeftFromMatch(ModelCompareInput input) {
		Object match = input.getMatch();
		if (match instanceof MatchModel) {
			MatchModel matchModel = (MatchModel) match;
			if (matchModel.getLeftRoots() != null && !matchModel.getLeftRoots().isEmpty()) {
				return matchModel.getLeftRoots().get(0);
			}
		}
		return null;
	}

	protected Object getLeftFromDiff(ModelCompareInput input) {
		Object diff = input.getDiff();
		if (diff instanceof DiffModel) {
			DiffModel diffModel = (DiffModel) diff;
			if (diffModel.getLeftRoots() != null && !diffModel.getLeftRoots().isEmpty()) {
				return diffModel.getLeftRoots().get(0);
			}
		}
		return null;
	}

	protected Object getRightFromMatch(ModelCompareInput input) {
		Object match = input.getMatch();
		if (match instanceof MatchModel) {
			MatchModel matchModel = (MatchModel) match;
			if (matchModel.getRightRoots() != null && !matchModel.getRightRoots().isEmpty()) {
				return matchModel.getRightRoots().get(0);
			}
		}
		return null;
	}

	protected Object getRightFromDiff(ModelCompareInput input) {
		Object diff = input.getDiff();
		if (diff instanceof DiffModel) {
			DiffModel diffModel = (DiffModel) diff;
			if (diffModel.getRightRoots() != null && !diffModel.getRightRoots().isEmpty()) {
				return diffModel.getRightRoots().get(0);
			}
		}
		return null;
	}

	protected Object getAncestorFromMatch(ModelCompareInput input) {
		Object match = input.getMatch();
		if (match instanceof MatchModel) {
			MatchModel matchModel = (MatchModel) match;
			if (matchModel.getAncestorRoots() != null && !matchModel.getAncestorRoots().isEmpty()) {
				return matchModel.getAncestorRoots().get(0);
			}
		}
		return null;
	}

	protected Object getAncestorFromDiff(ModelCompareInput input) {
		Object diff = input.getDiff();
		if (diff instanceof DiffModel) {
			DiffModel diffModel = (DiffModel) diff;
			if (diffModel.getAncestorRoots() != null && !diffModel.getAncestorRoots().isEmpty()) {
				return diffModel.getAncestorRoots().get(0);
			}
		}
		return null;
	}
}
