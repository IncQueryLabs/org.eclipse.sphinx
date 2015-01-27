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
package org.eclipse.sphinx.emf.compare.ui.viewer.structuremerge;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.EMFCompareStructureMergeViewer;
import org.eclipse.sphinx.emf.compare.scope.IModelComparisonScope;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.swt.widgets.Composite;

public class ModelElementStructureMergeViewer extends EMFCompareStructureMergeViewer {

	public ModelElementStructureMergeViewer(Composite parent, EMFCompareConfiguration config) {
		super(parent, config);
	}

	// FIXME we need to override this method but not visible.
	// Therefore, we performed required actions, e.g., model load when preparing the editor input.
	// @Override
	// protected void compareInputChanged(final ICompareInput input, IProgressMonitor monitor) {
	// if (input instanceof ModelElementComparisonScopeEditorInput) {
	// IComparisonScope scope = ((ModelElementComparisonScopeEditorInput) input).getScope();
	// if (scope instanceof IModelComparisonScope) {
	// loadModel((IModelComparisonScope) scope);
	//
	// Resource leftResource = EcorePlatformUtil.getResource(((IModelComparisonScope) scope).getLeftFile());
	// Resource rightResource = EcorePlatformUtil.getResource(((IModelComparisonScope) scope).getRightFile());
	// ((IModelComparisonScope) scope).setDelegate(new DefaultComparisonScope(leftResource, rightResource, null));
	//
	// EMFCompareConfiguration compareConfiguration = getCompareConfiguration();
	// ICompareEditingDomain editingDomain = compareConfiguration.getEditingDomain();
	// if (editingDomain instanceof DelegatingEMFCompareEditingDomain) {
	// ICompareEditingDomain delegatingEditingDomain = BasicCompareUIUtil.createEMFCompareEditingDomain(leftResource,
	// rightResource,
	// null);
	// ((DelegatingEMFCompareEditingDomain) editingDomain).setDelegate(delegatingEditingDomain);
	// }
	// }
	// }
	// }

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
}
