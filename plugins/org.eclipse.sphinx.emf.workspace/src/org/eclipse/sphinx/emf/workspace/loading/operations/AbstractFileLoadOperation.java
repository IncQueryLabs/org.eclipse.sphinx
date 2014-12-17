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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;

public abstract class AbstractFileLoadOperation extends AbstractLoadOperation {

	private Collection<IFile> files;

	public AbstractFileLoadOperation(String label, Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		super(label, mmDescriptor);
		addFiles(files);
	}

	@Override
	public ISchedulingRule getRule() {
		return getSchedulingRuleFactory().createLoadSchedulingRule(getFiles());
	}

	public Collection<IFile> getFiles() {
		return files;
	}

	@Override
	public boolean covers(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		return false;
	}

	@Override
	public boolean covers(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		int filesComparison = compare(getFiles(), files);
		int mmDescriptorsComparison = compare(getMetaModelDescriptor(), mmDescriptor);
		if (filesComparison == EQUAL) {
			if (mmDescriptorsComparison == EQUAL || mmDescriptorsComparison == GREATER_THAN) {
				return true;
			}
		} else if (filesComparison == GREATER_THAN) {
			if (mmDescriptorsComparison == EQUAL || mmDescriptorsComparison == GREATER_THAN) {
				return true;
			}
		}
		return false;
	}

	public void addFiles(Collection<IFile> files) {
		if (getFiles() == null) {
			this.files = new HashSet<IFile>();
		}
		getFiles().addAll(files);
	}
}
