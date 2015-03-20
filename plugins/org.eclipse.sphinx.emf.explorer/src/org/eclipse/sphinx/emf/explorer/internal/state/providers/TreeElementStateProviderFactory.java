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
package org.eclipse.sphinx.emf.explorer.internal.state.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.explorer.internal.Activator;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.CommonViewer;

public class TreeElementStateProviderFactory {

	public static final String MEMENTO_TYPE_GROUP_SELECTED = Activator.getPlugin().getSymbolicName() + ".selected"; //$NON-NLS-1$
	public static final String MEMENTO_TYPE_GROUP_EXPANDED = Activator.getPlugin().getSymbolicName() + ".expanded"; //$NON-NLS-1$

	public static final String MEMENTO_TYPE_ELEMENT_PROJECT = Activator.getPlugin().getSymbolicName() + ".project"; //$NON-NLS-1$
	public static final String MEMENTO_TYPE_ELEMENT_FOLDER = Activator.getPlugin().getSymbolicName() + ".folder"; //$NON-NLS-1$
	public static final String MEMENTO_TYPE_ELEMENT_FILE = Activator.getPlugin().getSymbolicName() + ".file"; //$NON-NLS-1$
	public static final String MEMENTO_TYPE_ELEMENT_EOBJECT = Activator.getPlugin().getSymbolicName() + ".eObject"; //$NON-NLS-1$
	public static final String MEMENTO_TYPE_ELEMENT_TRANSIENT = Activator.getPlugin().getSymbolicName() + ".transient"; //$NON-NLS-1$

	public static final String MEMENTO_KEY_NAME = "name"; //$NON-NLS-1$
	public static final String MEMENTO_KEY_PATH = "path"; //$NON-NLS-1$
	public static final String MEMENTO_KEY_URI = "uri"; //$NON-NLS-1$
	public static final String MEMENTO_KEY_PARENT_URI = "parentURI"; //$NON-NLS-1$
	public static final String MEMENTO_KEY_TRANSIENT_CHILDREN = "transientChildren"; //$NON-NLS-1$

	private CommonViewer viewer;

	public TreeElementStateProviderFactory(CommonViewer viewer) {
		Assert.isNotNull(viewer);
		this.viewer = viewer;
	}

	public ITreeElementStateProvider create(Object element) {
		return create(getParentPath(element), element);
	}

	protected TreePath getParentPath(Object element) {
		IContentProvider contentProvider = viewer.getContentProvider();
		if (contentProvider instanceof ITreePathContentProvider) {
			TreePath[] parents = ((ITreePathContentProvider) contentProvider).getParents(element);
			if (parents.length > 0) {
				return parents[0];
			}
		}
		return null;
	}

	public ITreeElementStateProvider create(TreePath treePath) {
		return create(treePath.getParentPath(), treePath.getLastSegment());
	}

	protected ITreeElementStateProvider create(TreePath parentPath, Object element) {
		if (element instanceof IProject) {
			return new ProjectElementStateProvider(viewer, (IProject) element);
		} else if (element instanceof IFolder) {
			return new FolderElementStateProvider(viewer, (IFolder) element);
		} else if (element instanceof IFile) {
			return new FileElementStateProvider(viewer, (IFile) element);
		} else if (element instanceof EObject) {
			URI uri = getURI(getParentResource(parentPath, element), (EObject) element);
			return new EObjectElementStateProvider(viewer, uri);
		} else if (isTransientElement(element)) {
			if (parentPath != null) {
				Object regularParent = null;
				List<Object> transientChildren = new ArrayList<Object>();
				transientChildren.add(element);
				for (int i = parentPath.getSegmentCount() - 1; i >= 0; i--) {
					Object parent = parentPath.getSegment(i);
					if (!isTransientElement(parent)) {
						regularParent = parent;
						break;
					}
					transientChildren.add(parent);
				}
				Collections.reverse(transientChildren);

				if (regularParent instanceof EObject) {
					URI regularParentURI = getURI(getParentResource(parentPath, element), (EObject) regularParent);
					return createTransientElementProvider(transientChildren, regularParentURI);
				}
			}
		}
		return null;
	}

	protected Resource getParentResource(TreePath parentPath, Object element) {
		if (element instanceof EObject || element instanceof TransientItemProvider || element instanceof IWrapperItemProvider) {
			if (parentPath != null) {
				for (int i = parentPath.getSegmentCount() - 1; i > 0; i--) {
					Object parent = parentPath.getSegment(i);
					if (parent instanceof IFile) {
						return EcorePlatformUtil.getResource((IFile) parent);
					}
				}
			} else {
				return EcorePlatformUtil.getResource(element);
			}
		}
		return null;
	}

	protected URI getURI(final Resource parentResource, final EObject eObject) {
		if (eObject != null) {
			final TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(eObject);
			if (editingDomain != null) {
				try {
					return TransactionUtil.runExclusive(editingDomain, new RunnableWithResult.Impl<URI>() {
						@Override
						public void run() {
							setResult(EcoreResourceUtil.getURI(parentResource, eObject, true));
						}
					});
				} catch (InterruptedException ex) {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
				}
			} else {
				return EcoreResourceUtil.getURI(parentResource, eObject, true);
			}
		}
		return null;
	}

	protected boolean isTransientElement(Object element) {
		return element instanceof TransientItemProvider || element instanceof IWrapperItemProvider;
	}

	protected TransientElementStateProvider createTransientElementProvider(List<Object> transientChildren, URI regularParentURI) {
		return new TransientElementStateProvider(viewer, regularParentURI, transientChildren);
	}

	public ITreeElementStateProvider createFromMemento(IMemento elementMemento) {
		if (MEMENTO_TYPE_ELEMENT_PROJECT.equals(elementMemento.getType())) {
			return new ProjectElementStateProvider(viewer, elementMemento);
		} else if (MEMENTO_TYPE_ELEMENT_FOLDER.equals(elementMemento.getType())) {
			return new FolderElementStateProvider(viewer, elementMemento);
		} else if (MEMENTO_TYPE_ELEMENT_FILE.equals(elementMemento.getType())) {
			return new FileElementStateProvider(viewer, elementMemento);
		} else if (MEMENTO_TYPE_ELEMENT_EOBJECT.equals(elementMemento.getType())) {
			return new EObjectElementStateProvider(viewer, elementMemento);
		} else if (MEMENTO_TYPE_ELEMENT_TRANSIENT.equals(elementMemento.getType())) {
			return new TransientElementStateProvider(viewer, elementMemento);
		}
		return null;
	}
}
