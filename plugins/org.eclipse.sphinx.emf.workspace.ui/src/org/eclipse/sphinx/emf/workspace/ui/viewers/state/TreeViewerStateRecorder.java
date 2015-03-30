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
package org.eclipse.sphinx.emf.workspace.ui.viewers.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers.ITreeElementStateProvider;
import org.eclipse.sphinx.emf.workspace.ui.viewers.state.providers.TreeElementStateProviderFactory;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.IMementoAware;

public class TreeViewerStateRecorder implements IMementoAware {

	protected TreeViewer viewer;
	protected ITreeViewerState deferredState;

	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

	public ITreeViewerState getDeferredState() {
		return deferredState;
	}

	protected void setDeferredState(ITreeViewerState deferredState) {
		if (deferredState != null) {
			// Load models behind element(s) that could not be expanded so far
			for (ITreeElementStateProvider provider : deferredState.getExpandedElements()) {
				if (provider.canUnderlyingModelBeLoaded()) {
					provider.loadUnderlyingModel();
				}
			}

			// No need to load models behind element(s) that could not be selected so far as they are naturally also
			// expanded elements
		}

		// Store as new deferred tree viewer state
		this.deferredState = deferredState != null && !deferredState.isEmpty() ? deferredState : null;
	}

	/*
	 * @see org.eclipse.ui.navigator.IMementoAware#restoreState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void restoreState(IMemento memento) {
		if (memento != null) {
			ITreeViewerState state = new TreeViewerState();
			TreeElementStateProviderFactory factory = new TreeElementStateProviderFactory(viewer);

			// Retrieve expanded element(s) to be restored from memento
			IMemento expandedMemento = memento.getChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_EXPANDED);
			if (expandedMemento != null) {
				for (IMemento elementMemento : expandedMemento.getChildren()) {
					ITreeElementStateProvider provider = factory.createFromMemento(elementMemento);
					if (provider != null) {
						state.getExpandedElements().add(provider);
					}
				}
			}

			// Retrieve selected element(s) to be restored from memento
			IMemento selectedMemento = memento.getChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_SELECTED);
			if (selectedMemento != null) {
				for (IMemento elementMemento : selectedMemento.getChildren()) {
					ITreeElementStateProvider provider = factory.createFromMemento(elementMemento);
					if (provider != null) {
						state.getSelectedElements().add(provider);
					}
				}
			}

			// Apply tree viewer state to be restored
			applyState(state);
		}
	}

	public boolean canApplyState() {
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

	public void applyState(ITreeViewerState state) {
		if (canApplyState()) {
			if (state != null && !state.isEmpty()) {
				// Extract expanded element(s) that can be restored right now
				List<ITreeElementStateProvider> expandableProviders = new ArrayList<ITreeElementStateProvider>();
				Iterator<ITreeElementStateProvider> iter = state.getExpandedElements().iterator();
				while (iter.hasNext()) {
					ITreeElementStateProvider provider = iter.next();
					if (!provider.hasUnderlyingModel() || provider.isUnderlyingModelLoaded()) {
						expandableProviders.add(provider);
						iter.remove();
					}
				}

				// Iteratively restore expanded element(s)
				/*
				 * !! Important Note !! Restrict number of iterative restoration attempts to total number of expandable
				 * elements to avoid endless loops when some expandable elements appear to be permanently unresolvable
				 * for some reason.
				 */
				for (int i = expandableProviders.size(); !expandableProviders.isEmpty() && i > 0; i--) {
					iter = expandableProviders.iterator();
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

				// Extract selected element(s) that can be restored right now
				List<ITreeElementStateProvider> selectableProviders = new ArrayList<ITreeElementStateProvider>();
				iter = state.getSelectedElements().iterator();
				while (iter.hasNext()) {
					ITreeElementStateProvider provider = iter.next();
					if (!provider.hasUnderlyingModel() || provider.isUnderlyingModelLoaded()) {
						selectableProviders.add(provider);
						iter.remove();
					}
				}

				// Restore selected element(s)
				setSelectedElements(selectableProviders);
			}
		}

		// Handle elements that could not yet be expanded or selected
		setDeferredState(state);
	}

	protected void setExpandedElements(final List<ITreeElementStateProvider> expandableProviders) {
		if (!expandableProviders.isEmpty()) {
			if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				viewer.getControl().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
							for (ITreeElementStateProvider provider : expandableProviders) {
								try {
									Object element = provider.getTreeElement();
									if (element != null) {
										viewer.setExpandedState(element, true);
									}
								} catch (Exception ex) {
									PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
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
								try {
									viewer.setSelection(new StructuredSelection(selectableElements));
								} catch (Exception ex) {
									PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
								}
							}
						}
					}
				});
			}
		}
	}

	public boolean canRecordState() {
		// Check if viewer has already been initialized
		return viewer != null;
	}

	public ITreeViewerState recordState() {
		if (canRecordState()) {
			ITreeViewerState state = new TreeViewerState();
			TreeElementStateProviderFactory factory = new TreeElementStateProviderFactory(viewer);

			// Record expanded elements
			TreePath expandedTreePaths[] = viewer.getExpandedTreePaths();
			if (expandedTreePaths.length > 0) {
				for (TreePath expandedTreePath : expandedTreePaths) {
					ITreeElementStateProvider provider = factory.create(expandedTreePath);
					if (provider != null) {
						state.getExpandedElements().add(provider);
					}
				}
			}

			// Record selected elements
			Object selectedElements[] = ((IStructuredSelection) viewer.getSelection()).toArray();
			if (selectedElements.length > 0) {
				for (Object selectedElement : selectedElements) {
					ITreeElementStateProvider provider = factory.create(selectedElement);
					if (provider != null) {
						state.getSelectedElements().add(provider);
					}
				}
			}
			return state;
		}
		return null;
	}

	/*
	 * @see org.eclipse.ui.navigator.IMementoAware#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		if (memento != null) {
			// Record current tree viewer state
			ITreeViewerState state = recordState();
			if (state != null && !state.isEmpty()) {
				// Save expanded elements to given memento
				if (!state.getExpandedElements().isEmpty()) {
					IMemento expandedMemento = memento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_EXPANDED);
					for (ITreeElementStateProvider provider : state.getExpandedElements()) {
						provider.appendToMemento(expandedMemento);
					}
				}

				// Save selected elements to given memento
				if (!state.getSelectedElements().isEmpty()) {
					IMemento selectedMemento = memento.createChild(TreeElementStateProviderFactory.MEMENTO_TYPE_GROUP_SELECTED);
					for (ITreeElementStateProvider provider : state.getSelectedElements()) {
						provider.appendToMemento(selectedMemento);
					}
				}
			}
		}
	}
}
