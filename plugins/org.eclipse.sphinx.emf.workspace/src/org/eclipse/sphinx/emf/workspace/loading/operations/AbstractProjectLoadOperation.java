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
package org.eclipse.sphinx.emf.workspace.loading.operations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

public abstract class AbstractProjectLoadOperation extends AbstractLoadOperation {

	private Collection<IProject> projects;
	private boolean includeReferencedProjects;

	public AbstractProjectLoadOperation(String label, Collection<IProject> projects, boolean includeReferencedProjects,
			IMetaModelDescriptor mmDescriptor) {
		super(label, mmDescriptor);
		this.includeReferencedProjects = includeReferencedProjects;
		addProjects(projects);
	}

	@Override
	public ISchedulingRule getRule() {
		return getSchedulingRuleFactory().createLoadSchedulingRule(getProjects(), isIncludeReferencedProjects());
	}

	public Collection<IProject> getProjects() {
		return projects;
	}

	public boolean isIncludeReferencedProjects() {
		return includeReferencedProjects;
	}

	@Override
	public boolean covers(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		Set<IProject> projectGroup = new HashSet<IProject>(projects);
		if (includeReferencedProjects) {
			for (IProject p : projects) {
				projectGroup.addAll(ExtendedPlatform.getProjectGroup(p, false));
			}
		}
		int projectsComparison = compare(getProjects(), projectGroup);
		if (projectsComparison == EQUAL || projectsComparison == GREATER_THAN) {
			int mmDescriptorsComparison = compare(getMetaModelDescriptor(), mmDescriptor);
			if (mmDescriptorsComparison == EQUAL || mmDescriptorsComparison == GREATER_THAN) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean covers(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		Set<IProject> projects = new HashSet<IProject>();
		for (IFile file : files) {
			projects.add(file.getProject());
		}
		return covers(projects, false, null);
	}

	public void addProjects(Collection<IProject> projects) {
		if (projects == null) {
			projects = new HashSet<IProject>();
		}
		projects.addAll(projects);
		// Compute project group if referenced projects must be considered
		if (includeReferencedProjects) {
			for (IProject p : projects) {
				projects.addAll(getProjectGroup(p, false));
			}
		}
	}
}
