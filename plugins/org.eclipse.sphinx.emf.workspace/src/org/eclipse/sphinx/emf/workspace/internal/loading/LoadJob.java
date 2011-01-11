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
package org.eclipse.sphinx.emf.workspace.internal.loading;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;

/**
 * @since 0.7.0
 */
public abstract class LoadJob extends Job {

	protected IMetaModelDescriptor fMMDescriptor;

	/**
	 * @param name
	 */
	protected LoadJob(String name, IMetaModelDescriptor mmDescriptor) {
		super(name);
		fMMDescriptor = mmDescriptor;
	}

	protected static int DIFFERENT = 0x00;
	protected static int EQUAL = 0x01;
	/**
	 * contains
	 */
	protected static int GREATER_THAN = 0x02;
	/**
	 * included in
	 */
	protected static int SMALLER_THAN = 0x03;

	static int compare(IMetaModelDescriptor mmd1, IMetaModelDescriptor mmd2) {
		if (mmd1 == null || MetaModelDescriptorRegistry.ANY_MM.equals(mmd1)) {
			if (mmd2 == null || MetaModelDescriptorRegistry.ANY_MM.equals(mmd2)) {
				return EQUAL;
			} else {
				return GREATER_THAN;
			}
		} else if (mmd2 == null || MetaModelDescriptorRegistry.ANY_MM.equals(mmd2)) {
			return SMALLER_THAN;
		} else if (mmd1.getClass().isAssignableFrom(mmd2.getClass())) {
			return GREATER_THAN;
		} else if (mmd2.getClass().isAssignableFrom(mmd1.getClass())) {
			return SMALLER_THAN;
		} else {
			return DIFFERENT;
		}
	}

	/**
	 * @param list1
	 * @param list2
	 * @return
	 */
	static <T> int compare(Collection<T> list1, Collection<T> list2) {
		int from1ContainedIn2 = 0;
		int from2ContainedIn1 = 0;
		for (T o : list1) {
			if (list2.contains(o)) {
				from1ContainedIn2++;
			}
		}
		for (T o : list2) {
			if (list1.contains(o)) {
				from2ContainedIn1++;
			}
		}
		if (from1ContainedIn2 == from2ContainedIn1 && from1ContainedIn2 == list1.size() && from2ContainedIn1 == list2.size()) {
			return EQUAL;
		} else if (from1ContainedIn2 == list1.size()) {
			return SMALLER_THAN;
		} else if (from2ContainedIn1 == list2.size()) {
			return GREATER_THAN;
		} else {
			return DIFFERENT;
		}
	}

	/**
	 * @param files
	 * @param mmDescriptor
	 * @return
	 */
	public static boolean shouldCreateJob(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		Set<IProject> projects = new HashSet<IProject>();
		for (IFile file : files) {
			projects.add(file.getProject());
		}
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				if (((ModelLoadJob) job).covers(projects, false, null)) {
					return false;
				}
			} else if (job instanceof FileLoadJob) {
				if (((FileLoadJob) job).covers(files, mmDescriptor)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @return
	 */
	public static boolean shouldCreateJob(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor) {
		for (Job job : Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING)) {
			if (job instanceof ModelLoadJob) {
				if (((ModelLoadJob) job).covers(projects, includeReferencedProjects, mmDescriptor)) {
					return false;
				}
			}
		}
		return true;
	}
}
