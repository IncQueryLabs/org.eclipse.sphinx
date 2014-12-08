/**
 * <copyright>
 *
 * Copyright (c) 2008-2014 itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [454092] Loading model resources
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.internal.loading;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

/**
 * @since 0.7.0
 */
public abstract class ModelLoadJob extends LoadJob {

	protected Set<IProject> fProjects;

	protected boolean fIncludeReferencedProjects;

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 */
	public ModelLoadJob(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		super(mmDescriptor != null ? Messages.job_loadingModel : Messages.job_loadingModels, mmDescriptor);

		fIncludeReferencedProjects = includeReferencedProjects;
		addProjects(projects);
	}

	public void addProjects(Collection<IProject> projects) {
		if (fProjects == null) {
			fProjects = new HashSet<IProject>();
		}
		fProjects.addAll(projects);
		// Compute project group if referenced projects must be considered
		if (fIncludeReferencedProjects) {
			for (IProject p : projects) {
				fProjects.addAll(getProjectGroup(p, false));
			}
		}
	}

	/**
	 * @param projects
	 *            The {@linkplain IProject project}s that this model load job may cover.
	 * @param includeReferencedProjects
	 *            If <b><code>true</code></b>, consider referenced projects.
	 * @param mmDescriptor
	 *            The {@linkplain IMetaModelDescriptor meta-model descriptor} of the model which has been asked for
	 *            loading.
	 * @return <ul>
	 *         <li><tt><b>true</b>&nbsp;&nbsp;</tt> if this job covers the loading of the specified projects with the
	 *         specified meta-model descriptor;</li>
	 *         <li><tt><b>false</b>&nbsp;</tt> otherwise.</li>
	 *         </ul>
	 */
	public boolean covers(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		Set<IProject> projectGroup = new HashSet<IProject>(projects);
		if (includeReferencedProjects) {
			for (IProject p : projects) {
				projectGroup.addAll(getProjectGroup(p, false));
			}
		}
		int projectsComparison = compare(fProjects, projectGroup);
		if (projectsComparison == EQUAL || projectsComparison == GREATER_THAN) {
			int mmDescriptorsComparison = compare(fMMDescriptor, mmDescriptor);
			if (mmDescriptorsComparison == EQUAL || mmDescriptorsComparison == GREATER_THAN) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Computes a group of projects from the given context object. Referencing projects are also taken into account if
	 * flag <code>includeReferencingProjects</code> is set to <code>true</code>. The supported object types are:
	 * <ul>
	 * <li>{@linkplain org.eclipse.core.resources.IResource}</li>
	 * <li>{@linkplain org.eclipse.emf.ecore.EObject}</li>
	 * <li>{@linkplain org.eclipse.emf.ecore.resource.Resource}</li>
	 * </ul>
	 * First, this method tries to retrieve the direct parent project of that context object accordingly to its type.
	 * Then, it delegates the project group computing to
	 * {@linkplain ExtendedPlatform#getProjectGroup(IProject, boolean)}.
	 * <p>
	 *
	 * @param contextObject
	 *            A context object whose scope must be computed.
	 * @param includeReferencingProjects
	 *            If <code>true</code> also includes referencing projects.
	 * @return The list projects that constitute the project group inside which the context object exists.
	 */
	private Collection<IProject> getProjectGroup(Object contextObject, boolean includeReferencingProjects) {
		if (contextObject instanceof IResource) {
			IResource contextResource = (IResource) contextObject;
			return ExtendedPlatform.getProjectGroup(contextResource.getProject(), includeReferencingProjects);
		} else {
			IFile contextFile = EcorePlatformUtil.getFile(contextObject);
			if (contextFile != null) {
				return ExtendedPlatform.getProjectGroup(contextFile.getProject(), includeReferencingProjects);
			}
		}
		return Collections.emptySet();
	}
}
