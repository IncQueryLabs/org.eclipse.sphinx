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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ExternalPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.sphinx.emf.mwe.dynamic.util.IXtendConstants;
import org.eclipse.sphinx.emf.mwe.dynamic.util.XtendUtil;

//TODO Move to new plug-in org.eclipse.sphinx.xtend.ui
@SuppressWarnings("restriction")
public class FilteredJavaElementContentProvider extends StandardJavaElementContentProvider {

	/*
	 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object element) {
		if (!exists(element)) {
			return NO_CHILDREN;
		}
		try {
			if (element instanceof IJavaModel) {
				return getJavaProjects((IJavaModel) element);
			}
			if (element instanceof IJavaProject) {
				return getPackageFragmentRoots((IJavaProject) element);
			}
			if (element instanceof IPackageFragmentRoot) {
				return getPackageFragmentRootContent((IPackageFragmentRoot) element);
			}
			if (element instanceof IPackageFragment) {
				return getPackageContent((IPackageFragment) element);
			}
			if (element instanceof IFolder) {
				return getFolderContent((IFolder) element);
			}
			if (getProvideMembers() && element instanceof ISourceReference && element instanceof IParent) {
				return ((IParent) element).getChildren();
			}
		} catch (CoreException e) {
			return NO_CHILDREN;
		}
		return NO_CHILDREN;
	}

	/*
	 * @see
	 * org.eclipse.jdt.ui.StandardJavaElementContentProvider#getPackageFragmentRoots(org.eclipse.jdt.core.IJavaProject)
	 */
	@Override
	protected Object[] getPackageFragmentRoots(IJavaProject project) throws JavaModelException {
		if (!project.getProject().isOpen()) {
			return NO_CHILDREN;
		}
		IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
		List<Object> filteredFragments = new ArrayList<Object>(roots.length);
		// Filter out package fragments that correspond to projects and replace them with the package fragments directly
		for (IPackageFragmentRoot root : roots) {
			// Exclude jar package entries, as well as external package entries
			if (!(root instanceof JarPackageFragmentRoot) && !(root instanceof ExternalPackageFragmentRoot)) {
				if (isProjectPackageFragmentRoot(root)) {
					Object[] fragments = getPackageFragmentRootContent(root);
					for (Object fragment : fragments) {
						filteredFragments.add(fragment);
					}
				} else {
					if (!root.getElementName().equals(IXtendConstants.XTEND_GEN_FOLDER_NAME)) {
						filteredFragments.add(root);
					}
				}
			}
		}
		return filteredFragments.toArray();
	}

	/*
	 * @see org.eclipse.jdt.ui.StandardJavaElementContentProvider#getPackageFragmentRootContent(org.eclipse.jdt.core.
	 * IPackageFragmentRoot)
	 */
	@Override
	protected Object[] getPackageFragmentRootContent(IPackageFragmentRoot root) throws JavaModelException {
		Object[] fragments = getNonEmptyFragments(root);
		if (isProjectPackageFragmentRoot(root)) {
			return fragments;
		}
		Object[] nonJavaResources = root.getNonJavaResources();
		if (nonJavaResources == null) {
			return fragments;
		}
		return concatenate(fragments, nonJavaResources);
	}

	protected Object[] getNonEmptyFragments(IPackageFragmentRoot root) throws JavaModelException {
		List<Object> nonEmptyFragments = new ArrayList<Object>();
		IJavaElement[] children = root.getChildren();
		for (IJavaElement child : children) {
			if (child instanceof PackageFragment) {
				// lookup xtend files inside package
				if (hasXtendResources((PackageFragment) child)) {
					nonEmptyFragments.add(child);
				} else {
					// lookup java classes inside package
					IJavaElement[] units = ((PackageFragment) child).getCompilationUnits();
					if (units.length > 0) {
						nonEmptyFragments.add(child);
					}
				}
			}
		}
		return nonEmptyFragments.toArray();
	}

	/*
	 * @see
	 * org.eclipse.jdt.ui.StandardJavaElementContentProvider#getPackageContent(org.eclipse.jdt.core.IPackageFragment)
	 */
	@Override
	protected Object[] getPackageContent(IPackageFragment fragment) throws JavaModelException {
		if (fragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
			return concatenate(fragment.getCompilationUnits(), getXtendResources(fragment));
		}
		return concatenate(fragment.getClassFiles(), fragment.getNonJavaResources());
	}

	protected boolean hasXtendResources(IPackageFragment packageFragment) throws JavaModelException {
		Object[] elements = packageFragment.getNonJavaResources();
		for (Object element : elements) {
			if (XtendUtil.isXtendFile((IFile) element)) {
				return true;
			}
		}
		return false;
	}

	protected Object[] getXtendResources(IPackageFragment fragment) throws JavaModelException {
		List<Object> filteredResources = new ArrayList<Object>();
		Object[] nonJavaResources = fragment.getNonJavaResources();
		for (Object resource : nonJavaResources) {
			if (resource instanceof IFile) {
				if (XtendUtil.isXtendFile((IFile) resource)) {
					filteredResources.add(resource);
				}
			}
		}
		return filteredResources.toArray();
	}
}