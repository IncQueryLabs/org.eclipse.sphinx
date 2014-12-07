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
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;

/**
 * @since 0.7.0
 */
public abstract class FileLoadJob extends LoadJob {

	/**
	 * The list of files this loading job is supposed to cover.
	 */
	protected Collection<IFile> fFiles;

	/**
	 * Constructor.
	 *
	 * @param files
	 *            The list of files this loading job is supposed to cover.
	 * @param mmDescriptor
	 *            The {@linkplain IMetaModelDescriptor meta-model descriptor} considered for loading.
	 */
	public FileLoadJob(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		super(Messages.job_loadingModelResources, mmDescriptor);
		// fFiles = filter(files, mmDescriptor);
		addFiles(files);
	}

	public void addFiles(Collection<IFile> files) {
		if (fFiles == null) {
			fFiles = new HashSet<IFile>();
		}
		fFiles.addAll(files);
	}

	/**
	 * @param files
	 *            The list of files this loading job is supposed to cover.
	 * @param mmDescriptor
	 *            The {@linkplain IMetaModelDescriptor meta-model descriptor} considered for loading.
	 * @return <ul>
	 *         <li><tt><b>true</b>&nbsp;&nbsp;</tt> if this job covers the loading of the specified files with the
	 *         specified meta-model descriptor;</li>
	 *         <li><tt><b>false</b>&nbsp;</tt> otherwise.</li>
	 *         </ul>
	 */
	public boolean covers(Collection<IFile> files, IMetaModelDescriptor mmDescriptor) {
		// Collection<IFile> filteredFiles = filter(files, mmDescriptor);
		int filesComparison = compare(fFiles, files);
		int mmDescriptorsComparison = compare(fMMDescriptor, mmDescriptor);
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
}
