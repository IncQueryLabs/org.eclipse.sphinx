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
package org.eclipse.sphinx.examples.hummingbird.ide.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sphinx.examples.hummingbird.ide.internal.Activator;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

/**
 * {@link IProjectNature Project nature} for {@link IProject}s containing Hummingbird models.
 */
public class HummingbirdNature implements IProjectNature {

	/**
	 * The id of this {@link IProjectNature project nature}.
	 */
	public static final String ID = Activator.getPlugin().getSymbolicName() + ".HummingbirdNature"; //$NON-NLS-1$

	/**
	 * The project to which this {@link IProjectNature project nature} applies.
	 */
	private IProject project;

	/**
	 * Adds a {@link HummingbirdNature Hummingbird nature} to the given {@link IProject project}.
	 * 
	 * @param project
	 *            The {@link IProject project} to be handled.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 * @throws CoreException
	 *             If the {@link IProject project} does not exist or is not open.
	 */
	public static void addTo(IProject project, IProgressMonitor monitor) throws CoreException {
		ExtendedPlatform.addNature(project, ID, monitor);
	}

	/**
	 * Removes the {@link HummingbirdNature Hummingbird nature} from the given {@link IProject project}.
	 * 
	 * @param project
	 *            The {@link IProject project} to be handled.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 * @throws CoreException
	 *             If the {@link IProject project} does not exist or is not open.
	 */
	public static void removeFrom(IProject project, IProgressMonitor monitor) throws CoreException {
		ExtendedPlatform.removeNature(project, ID, monitor);
	}

	/*
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		// Do nothing
	}

	/*
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		// Do nothing
	}

	/*
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
}
