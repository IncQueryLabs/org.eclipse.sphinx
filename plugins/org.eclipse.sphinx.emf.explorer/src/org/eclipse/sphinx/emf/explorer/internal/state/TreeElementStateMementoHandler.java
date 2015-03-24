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
package org.eclipse.sphinx.emf.explorer.internal.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.sphinx.emf.explorer.internal.state.providers.ITreeElementStateProvider;
import org.eclipse.sphinx.emf.explorer.internal.state.providers.TreeElementStateProviderFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.IMementoAware;

public class TreeElementStateMementoHandler implements IMementoAware {

	protected CommonViewer viewer;
	protected IMemento deferredMemento;

	public void setViewer(CommonViewer viewer) {
		this.viewer = viewer;
	}

	public boolean canRestoreState() {
		// Check if viewer and the viewer's input have already been initialized
		/*
		 * !! Important Note !! CommonNavigator#createPartControl() indirectly calls #restoreState() at a moment where
		 * the viewer's input has not yet been set, and, consequently, the viewer has not yet had any chance to create
		 * the mappings between tree elements and tree item widgets (see
		 * org.eclipse.jface.viewers.StructuredViewer#elementMap for details). However, the latter are a prerequisite
		 * and must exist before the restoration of expansion state and selection can be started.
		 */
		return viewer != null && viewer.getInput() != null;
	}

	/*
	 * @see org.eclipse.ui.navigator.IMementoAware#restoreState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void restoreState(IMemento memento) {
		if (canRestoreState() && memento != null) {
			TreeElementStateProviderFactory treeElementFactory = new TreeElementStateProviderFactory(viewer);

			// Retrieve expanded element(s) to be restored
			List<ITreeElementStateProvider> expandableProviders = new ArrayList<ITreeElementStateProvider>();
			List<ITreeElementStateProvider> deferredExpandableProviders = new ArrayList<ITreeElementStateProvider>();
			IMemento expandedMemento = memento.getChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_EXPANDED);
			if (expandedMemento != null) {
				for (IMemento elementMemento : expandedMemento.getChildren()) {
					ITreeElementStateProvider provider = treeElementFactory.createFromMemento(elementMemento);
					if (provider != null) {
						if (!provider.hasUnderlyingModel() || provider.isUnderlyingModelLoaded()) {
							expandableProviders.add(provider);
						} else {
							deferredExpandableProviders.add(provider);
						}
					}
				}

				// Iteratively restore expanded element(s)
				/*
				 * !! Important Note !! Restrict number of iterative restoration attempts to total number of expandable
				 * elements to avoid endless loops when some expandable elements appear to be permanently unresolvable
				 * for some reason.
				 */
				for (int i = expandableProviders.size(); i > 0; i--) {
					Iterator<ITreeElementStateProvider> iter = expandableProviders.iterator();
					while (iter.hasNext()) {
						ITreeElementStateProvider provider = iter.next();
						if (provider.isStale()) {
							iter.remove();
						} else if (provider.isResolved()) {
							if (!provider.canBeExpanded()) {
								iter.remove();
							} else if (provider.isExpanded()) {
								iter.remove();
							}
						}
					}

					setExpandedElements(expandableProviders);
				}
			}

			// Retrieve selected element(s) to be restored
			List<ITreeElementStateProvider> selectableProviders = new ArrayList<ITreeElementStateProvider>();
			List<ITreeElementStateProvider> deferredSelectableProviders = new ArrayList<ITreeElementStateProvider>();
			IMemento selectedMemento = memento.getChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_SELECTED);
			if (selectedMemento != null) {
				for (IMemento elementMemento : selectedMemento.getChildren()) {
					ITreeElementStateProvider provider = treeElementFactory.createFromMemento(elementMemento);
					if (provider != null) {
						if (provider.isUnderlyingModelLoaded()) {
							selectableProviders.add(provider);
						} else {
							deferredSelectableProviders.add(provider);
						}
					}
				}

				// Restore selected element(s)
				setSelectedElements(selectableProviders);
			}

			// Handle elements that could not yet be expanded or selected
			processDeferredElements(memento.getType(), deferredExpandableProviders, deferredSelectableProviders);
		}
	}

	protected void processDeferredElements(String deferredMementoType, List<ITreeElementStateProvider> deferredExpandableProviders,
			List<ITreeElementStateProvider> deferredSelectableProviders) {
		Assert.isNotNull(deferredExpandableProviders);
		Assert.isNotNull(deferredSelectableProviders);

		IMemento newDeferredMemento = XMLMemento.createWriteRoot(deferredMementoType);

		// Save elements that can not be expanded yet back to deferred memento
		IMemento newDeferredExpandedMemento = newDeferredMemento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_EXPANDED);
		for (ITreeElementStateProvider provider : deferredExpandableProviders) {
			if (provider.canUnderlyingModelBeLoaded()) {
				provider.loadUnderlyingModel();
				provider.appendToMemento(newDeferredExpandedMemento);
			}
		}

		// Save elements that can not be selected yet back to deferred memento
		IMemento newDeferredSelectedMemento = newDeferredMemento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_SELECTED);
		for (ITreeElementStateProvider provider : deferredSelectableProviders) {
			provider.appendToMemento(newDeferredSelectedMemento);
		}

		// Store deferred memento if needed
		if (newDeferredExpandedMemento.getChildren().length > 0 || newDeferredSelectedMemento.getChildren().length > 0) {
			deferredMemento = newDeferredMemento;
		} else {
			deferredMemento = null;
		}
	}

	protected void setExpandedElements(final List<ITreeElementStateProvider> expandableProviders) {
		if (!expandableProviders.isEmpty()) {
			if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				viewer.getControl().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
							for (ITreeElementStateProvider provider : expandableProviders) {
								Object element = provider.getTreeElement();
								if (element != null) {
									viewer.setExpandedState(element, true);
								}
							}
						}
					}
				});
			}
		}
	}

	protected void setSelectedElements(final List<ITreeElementStateProvider> selectableProviders) {
		if (!selectableProviders.isEmpty()) {
			if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				viewer.getControl().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
							List<Object> selectableElements = new ArrayList<Object>();
							for (ITreeElementStateProvider provider : selectableProviders) {
								Object element = provider.getTreeElement();
								if (element != null) {
									selectableElements.add(element);
								}
							}
							if (!selectableElements.isEmpty()) {
								viewer.setSelection(new StructuredSelection(selectableElements));
							}
						}
					}
				});
			}
		}
	}

	public IMemento getDeferredMemento() {
		return deferredMemento;
	}

	public boolean canSaveState() {
		// Check if viewer has already been initialized
		return viewer != null;
	}

	/*
	 * @see org.eclipse.ui.navigator.IMementoAware#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		if (canSaveState() && memento != null) {
			TreeElementStateProviderFactory treeElementFactory = new TreeElementStateProviderFactory(viewer);

			// Save expanded elements
			TreePath expandedTreePaths[] = viewer.getExpandedTreePaths();
			if (expandedTreePaths.length > 0) {
				IMemento expandedMemento = memento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_EXPANDED);
				for (TreePath expandedTreePath : expandedTreePaths) {
					ITreeElementStateProvider provider = treeElementFactory.create(expandedTreePath);
					if (provider != null) {
						provider.appendToMemento(expandedMemento);
					}
				}
			}

			// Save selected elements
			Object selectedElements[] = ((IStructuredSelection) viewer.getSelection()).toArray();
			if (selectedElements.length > 0) {
				IMemento selectedMemento = memento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_SELECTED);
				for (Object selectedElement : selectedElements) {
					ITreeElementStateProvider provider = treeElementFactory.create(selectedElement);
					if (provider != null) {
						provider.appendToMemento(selectedMemento);
					}
				}
			}
		}
	}
}
