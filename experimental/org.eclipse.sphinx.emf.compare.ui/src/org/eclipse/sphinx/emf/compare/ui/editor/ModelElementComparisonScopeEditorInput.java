/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.compare.ui.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration;
import org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonScopeEditorInput;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.compare.domain.DelegatingEMFCompareEditingDomain;
import org.eclipse.sphinx.emf.compare.scope.IModelComparisonScope;
import org.eclipse.sphinx.emf.compare.ui.ModelElementComparisonScopeInput;
import org.eclipse.sphinx.emf.compare.ui.internal.Activator;
import org.eclipse.sphinx.emf.compare.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.compare.ui.util.BasicCompareUIUtil;
import org.eclipse.sphinx.emf.compare.ui.viewer.structuremerge.ModelElementStructureMergeViewerCreator;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.emf.workspace.saving.ModelSaveManager;
import org.eclipse.sphinx.emf.workspace.ui.saving.BasicModelSaveablesProvider;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.navigator.SaveablesProvider;

public class ModelElementComparisonScopeEditorInput extends ComparisonScopeEditorInput implements ISaveablesSource {

	protected SaveablesProvider modelSaveablesProvider;

	private final IComparisonScope scope;
	private final EMFCompare comparator;

	private ModelElementStructureMergeViewerCreator viewerCreator = new ModelElementStructureMergeViewerCreator();

	/**
	 * The two {@linkplain Object}s that are currently being compared.
	 * <p>
	 * <code>modelRoots[0]</code> is the <b>left</b> model root object;<br>
	 * <code>modelRoots[1]</code> is the <b>right</b> model root object.
	 */
	private Object[] modelRoots;

	public ModelElementComparisonScopeEditorInput(EMFCompareConfiguration configuration, ICompareEditingDomain editingDomain,
			AdapterFactory adapterFactory, EMFCompare comparator, IComparisonScope scope) {
		super(configuration, editingDomain, adapterFactory, comparator, scope);

		this.scope = scope;
		this.comparator = comparator;
	}

	public IComparisonScope getScope() {
		return scope;
	}

	/**
	 * Returns the save options to consider while saving the underlying model being edited. Default implementation
	 * returns the default save options provided by the Sphinx EMF platform utility {@linkplain EcoreResourceUtil}.
	 * Clients may override this method in order to specify custom options.
	 *
	 * @return The save options to consider while saving the underlying model being edited.
	 */
	protected Map<?, ?> getSaveOptions() {
		return EcoreResourceUtil.getDefaultSaveOptions();
	}

	@Override
	public boolean isDirty() {
		boolean isDirty = false;
		for (Object modelRoot : getModelRoots()) {
			if (modelRoot instanceof EObject) {
				// Return true if the model, this editor or both are dirty
				isDirty = isDirty || ModelSaveManager.INSTANCE.isDirty(((EObject) modelRoot).eResource());
			} else if (modelRoot instanceof Resource) {
				// Return true if the model, this editor or both are dirty
				isDirty = isDirty || ModelSaveManager.INSTANCE.isDirty((Resource) modelRoot);
			}
		}
		return isDirty;
	}

	protected void init() {
		if (modelSaveablesProvider == null) {
			modelSaveablesProvider = createModelSaveablesProvider();
			if (getWorkbenchPart() instanceof ModelCompareEditor) {
				ISaveablesLifecycleListener modelSaveablesLifecycleListener = ((ModelCompareEditor) getWorkbenchPart())
						.createModelSaveablesLifecycleListener();
				modelSaveablesProvider.init(modelSaveablesLifecycleListener);
			} else {
				if (getWorkbenchPart() == null) {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), new NullPointerException(Messages.warning_workbenchPartNull));
				} else {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), new RuntimeException(
							Messages.warning_workbenchPartInstanceofModelCompareEditor));
				}
			}
		}
	}

	/**
	 * @return The root object of the model part that is currently being edited in this editor or <code>null</code> if
	 *         no such is available.
	 */
	public Object[] getModelRoots() {
		if (modelRoots == null) {
			modelRoots = new Object[2];
		}

		if (scope != null) {
			if (scope instanceof IModelComparisonScope) {
				// File-based comparison
				if (((IModelComparisonScope) scope).isFileBasedComparison()) {
					if (modelRoots[0] == null) {
						modelRoots[0] = ((IModelComparisonScope) scope).getLeftFile();
					}
					if (modelRoots[1] == null) {
						modelRoots[1] = ((IModelComparisonScope) scope).getRightFile();
					}
				} else {
					Object left = modelRoots[0];
					if (left == null || left instanceof EObject
							&& (((EObject) left).eIsProxy() || ((EObject) left).eResource() == null || !((EObject) left).eResource().isLoaded())) {
						// FIXME
						modelRoots[0] = scope.getLeft();
					}
					Object right = modelRoots[1];
					if (right == null || right instanceof EObject
							&& (((EObject) right).eIsProxy() || ((EObject) right).eResource() == null || !((EObject) right).eResource().isLoaded())) {
						// FIXME
						modelRoots[1] = scope.getRight();
					}
				}
			}
		} else {
			// FIXME message comparison null
			IStatus warning = new Status(IStatus.WARNING, Activator.getPlugin().getSymbolicName(), Messages.warning_inputMatchModelNull,
					new NullPointerException());
			PlatformLogUtil.logAsWarning(Activator.getPlugin(), warning);
		}

		return modelRoots;
	}

	protected SaveablesProvider createModelSaveablesProvider() {
		return new BasicModelSaveablesProvider();
	}

	/**
	 * @see Bug 892 - Indicate files being compared in compare editor tab title
	 * @see #setTitle(String)
	 */
	@Override
	protected Object doPrepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		init();
		String title;

		// FIXME The models should be loaded on demand in the
		// org.eclipse.sphinx.emf.compare.ui.viewer.structuremerge.ModelElementStructureMergeViewer. We performed model
		// load step here, if needed, due to an API restriction from AMF compare side.
		if (scope instanceof IModelComparisonScope && ((IModelComparisonScope) scope).isFileBasedComparison()) {
			loadModel((IModelComparisonScope) scope, monitor);

			Resource leftResource = EcorePlatformUtil.getResource(((IModelComparisonScope) scope).getLeftFile());
			Resource rightResource = EcorePlatformUtil.getResource(((IModelComparisonScope) scope).getRightFile());
			((IModelComparisonScope) scope).setDelegate(new DefaultComparisonScope(leftResource, rightResource, null));

			EMFCompareConfiguration compareConfiguration = getCompareConfiguration();
			ICompareEditingDomain editingDomain = compareConfiguration.getEditingDomain();
			if (editingDomain instanceof DelegatingEMFCompareEditingDomain) {
				ICompareEditingDomain delegatingEditingDomain = BasicCompareUIUtil.createEMFCompareEditingDomain(leftResource, rightResource, null);
				((DelegatingEMFCompareEditingDomain) editingDomain).setDelegate(delegatingEditingDomain);
			}
		}

		getCompareConfiguration().setEMFComparator(comparator);
		Object input = new ModelElementComparisonScopeInput(scope, getAdapterFactory());

		String leftLabel = getLeftLabel();
		String rightLabel = getRightLabel();
		String ancestorLabel = getAncestorLabel();
		if (ancestorLabel == null) {
			title = NLS.bind(Messages.twoWay_title, leftLabel, rightLabel);
		} else {
			title = NLS.bind(Messages.threeWay_title, new String[] { ancestorLabel, leftLabel, rightLabel });
		}
		setTitle(title);
		return input;
	}

	protected void loadModel(IModelComparisonScope comparisonScope, IProgressMonitor monitor) {
		if (comparisonScope != null && comparisonScope.isFileBasedComparison()) {
			final Set<IFile> filesToBeLoaded = new HashSet<IFile>();
			IFile leftFile = comparisonScope.getLeftFile();
			if (leftFile != null) {
				filesToBeLoaded.add(leftFile);
			}
			IFile rightFile = comparisonScope.getRightFile();
			if (rightFile != null) {
				filesToBeLoaded.add(rightFile);
			}

			if (!filesToBeLoaded.isEmpty()) {
				ModelLoadManager.INSTANCE.loadFiles(filesToBeLoaded, false, monitor);
			}
		}
	}

	/**
	 * Returns the label of the left compared object to use it in the title and the tool tip of the compare editor.
	 *
	 * @return As specified above.
	 */
	protected String getRightLabel() {
		String rightLabel = ""; //$NON-NLS-1$
		Object rightRoot = getModelRoots()[1];

		if (rightRoot instanceof EObject) {
			Resource rightResource = ((EObject) rightRoot).eResource();
			String fragment = rightResource.getURIFragment((EObject) rightRoot);
			fragment = fragment.lastIndexOf(ExtendedResource.URI_QUERY_SEPARATOR) == -1 ? "" : fragment.substring(0, fragment.lastIndexOf(ExtendedResource.URI_QUERY_SEPARATOR)); //$NON-NLS-1$
			rightLabel = rightResource.getURI().toPlatformString(true).concat(ExtendedResource.URI_FRAGMENT_SEPARATOR + fragment);
		} else if (rightRoot instanceof IFile) {
			// TODO
			rightLabel = ((IFile) rightRoot).getName();
		}

		return rightLabel;
	}

	/**
	 * Returns the label of the right compared object to use it in the title and the tool tip of the compare editor.
	 *
	 * @return As specified above.
	 */
	protected String getLeftLabel() {
		String leftLabel = ""; //$NON-NLS-1$;
		Object leftRoot = getModelRoots()[0];

		if (leftRoot instanceof EObject) {
			Resource leftResource = ((EObject) leftRoot).eResource();
			String fragment = leftResource.getURIFragment((EObject) leftRoot);
			fragment = fragment.lastIndexOf(ExtendedResource.URI_QUERY_SEPARATOR) == -1 ? "" : fragment.substring(0, fragment.lastIndexOf(ExtendedResource.URI_QUERY_SEPARATOR)); //$NON-NLS-1$
			leftLabel = leftResource.getURI().toPlatformString(true).concat(ExtendedResource.URI_FRAGMENT_SEPARATOR + fragment);
		} else if (leftRoot instanceof IFile) {
			// TODO
			leftLabel = ((IFile) leftRoot).getName();
		}

		return leftLabel;
	}

	/**
	 * Returns the label of the ancestor object in case of three way comparison.
	 *
	 * @return As specified above.
	 */
	protected String getAncestorLabel() {
		// FIXME
		// Resource ancestorResource = preparedInput.getAncestorResource();
		// if (ancestorResource != null) {
		// return ancestorResource.getURI().toString();
		// }
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO
		// if (preparedInput != null) {
		// String leftLabel = getLeftLabel();
		// String rightLabel = getRightLabel();
		// String ancestorLabel = getAncestorLabel();
		// if (ancestorLabel == null) {
		// return NLS.bind(Messages.twoWay_tooltip, leftLabel, rightLabel);
		// } else {
		// return NLS.bind(Messages.threeWay_tooltip, new Object[] { ancestorLabel, leftLabel, rightLabel });
		// }
		// }
		// Fall back
		return super.getToolTipText();
	}

	@Override
	public void saveChanges(IProgressMonitor monitor) {
		for (Object modelRoot : getModelRoots()) {
			Resource resource = null;
			if (modelRoot instanceof EObject) {
				resource = ((EObject) modelRoot).eResource();
			} else if (modelRoot instanceof IFile) {
				resource = EcorePlatformUtil.getResource((IFile) modelRoot);
			}
			// Save the all dirty resources of underlying model
			if (resource != null) {
				ModelSaveManager.INSTANCE.saveModel(resource, getSaveOptions(), false, monitor);
			}
		}
	}

	@Override
	public Saveable[] getActiveSaveables() {
		return getSaveables();
	}

	@Override
	public Saveable[] getSaveables() {
		Set<Saveable> saveables = new HashSet<Saveable>();
		if (modelSaveablesProvider != null) {
			if (getModelRoots()[0] != null) {
				Saveable leftSaveable = modelSaveablesProvider.getSaveable(getModelRoots()[0]);
				if (leftSaveable != null) {
					saveables.add(leftSaveable);
				}
			}

			if (getModelRoots()[1] != null) {
				Saveable rightSaveable = modelSaveablesProvider.getSaveable(getModelRoots()[1]);
				if (rightSaveable != null) {
					saveables.add(rightSaveable);
				}
			}
		}
		return saveables.toArray(new Saveable[saveables.size()]);

	}

	@Override
	protected void finalize() throws Throwable {
		if (modelSaveablesProvider != null) {
			modelSaveablesProvider.dispose();
			modelSaveablesProvider = null;
		}
		super.finalize();
	}

	@Override
	protected void handleDispose() {
		super.handleDispose();

		ICompareEditingDomain editingDomain = getEditingDomain();
		if (editingDomain instanceof IDisposable) {
			((IDisposable) editingDomain).dispose();
		}
	}
}
