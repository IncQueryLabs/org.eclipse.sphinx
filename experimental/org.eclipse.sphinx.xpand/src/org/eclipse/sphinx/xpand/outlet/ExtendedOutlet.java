/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xpand.outlet;

import org.eclipse.core.internal.resources.projectvariables.ProjectLocationVariableResolver;
import org.eclipse.core.internal.resources.projectvariables.WorkspaceLocationVariableResolver;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.xpand.internal.EObjectAdapaterFactory;
import org.eclipse.xpand2.output.Outlet;

@SuppressWarnings("restriction")
public class ExtendedOutlet extends Outlet {

	public static final String VARIABLE_ARGUMENT_SEPARATOR = ":"; //$NON-NLS-1$

	public static final String VARIABLE_WORKSPACE_LOC = WorkspaceLocationVariableResolver.NAME.toLowerCase();

	public static final String VARIABLE_PROJECT_LOC = ProjectLocationVariableResolver.NAME.toLowerCase();

	private String pathExpression = null;

	public ExtendedOutlet() {
	}

	public ExtendedOutlet(IContainer container) {
		setPath(container);
	}

	public ExtendedOutlet(String name, IContainer container) {
		setName(name);
		setPath(container);
	}

	public ExtendedOutlet(String pathExpression, IProject project) {
		setPathExpression(pathExpression, project);
	}

	public ExtendedOutlet(String name, String pathExpression, IProject project) {
		setName(name);
		setPathExpression(pathExpression, project);
	}

	public String getPathExpression() {
		return pathExpression;
	}

	public void setPathExpression(String pathExpression, IProject project) {
		this.pathExpression = pathExpression;
		setPath(resolvePathExpression(pathExpression, project));
	}

	protected String resolvePathExpression(String pathExpression, IProject project) {
		Assert.isNotNull(pathExpression);

		try {
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();

			// Does path expression contain a project_loc variable?
			// TODO Try to leverage this to get rid of EObjectAdapterFactory
			if (pathExpression.contains(VARIABLE_PROJECT_LOC) && project != null) {
				// Add name of given project as argument to path expression
				pathExpression = pathExpression.replaceFirst(VARIABLE_PROJECT_LOC,
						VARIABLE_PROJECT_LOC + VARIABLE_ARGUMENT_SEPARATOR + project.getName());
			}

			// Add the EObjectAdapterFactory to the registry so that project_loc variable can be resolved when the
			// selection is an EObject
			Platform.getAdapterManager().registerAdapters(EObjectAdapaterFactory.INSTANCE, EObject.class);

			// Resolve path expression
			String resolvedPath = manager.performStringSubstitution(pathExpression);

			// Is resolved path absolute?
			IPath path = new Path(resolvedPath);
			if (path.isAbsolute()) {
				// Return that absolute path
				return path.toFile().getAbsolutePath();
			} else {
				// Is resolved path workspace-relative?
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
				if (resource != null) {
					// Convert to corresponding absolute file system path
					IPath location = resource.getLocation();
					if (location != null) {
						return location.toFile().getAbsolutePath();
					}
				} else {
					// Let file system perform conversion to corresponding absolute path
					return path.toFile().getAbsolutePath();
				}
			}
		} catch (CoreException ex) {
			// Ignore exception, just return null
		} finally {
			Platform.getAdapterManager().unregisterAdapters(EObjectAdapaterFactory.INSTANCE, EObject.class);
		}
		return null;
	}

	public void setPath(IContainer container) {
		setPath(container.getLocation().toFile().getAbsolutePath());
		pathExpression = container.getFullPath().makeRelative().toString();
	}

	/**
	 * Returns the {@link IContainer container} behind the {@link #getPath() path} of this outlet in case that it
	 * references a project or folder in the workspace.
	 * 
	 * @return The {@link IContainer container} corresponding to the {@link #getPath() path} of this outlet or
	 *         <code>null</code> if this outlet has no path or a path referencing a location outside the workspace.
	 */
	public IContainer getContainer() {
		String path = getPath();
		if (path != null) {
			return ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(path));
		}
		return null;
	}

	/*
	 * @see org.eclipse.xpand2.output.Outlet#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		if (getName() != null) {
			result.append(getName() + ":"); //$NON-NLS-1$
		}
		if (getPath() != null) {
			result.append(getPath());
		} else if (getPathExpression() != null) {
			result.append(getPathExpression());
		}
		if (isAppend() || !isOverwrite()) {
			result.append("(").append("overwrite=").append(isOverwrite()).append(",append=").append(isAppend()).append(",fileEncoding=").append(getFileEncoding()) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					.append(")"); //$NON-NLS-1$
		}
		return result.toString();
	}
}
