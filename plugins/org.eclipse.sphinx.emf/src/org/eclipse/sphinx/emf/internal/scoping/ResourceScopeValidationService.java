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
package org.eclipse.sphinx.emf.internal.scoping;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.scoping.IResourceScope;
import org.eclipse.sphinx.emf.scoping.IResourceScopeProvider;
import org.eclipse.sphinx.emf.scoping.ResourceScopeProviderRegistry;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * Provides methods validating for validating {@link IFile}s with regard to their {@link IResourceScope resource scope}
 * and creating or cleaning corresponding problem markers. The validation itself is delegated to the
 * {@link IResourceScopeProvider resource scope provider} which is associated with the type of the {@link IFile file} in
 * question.
 * 
 * @see IResourceScopeMarker#RESOURCE_SCOPING_PROBLEM
 */
public class ResourceScopeValidationService {

	/**
	 * Singleton instance.
	 */
	public static ResourceScopeValidationService INSTANCE = new ResourceScopeValidationService();

	// Private default constructor for singleton pattern
	private ResourceScopeValidationService() {
	}

	/**
	 * Validates the provided collection of {@link IFile file}s with regard to their {@link IResourceScope resource
	 * scope}. The validation itself is delegated to the {@link IResourceScopeProvider resource scope provider} which is
	 * associated with the type of each {@link IFile file}. If it results in indicating any problem a
	 * {@link IResourceScopeMarker#RESOURCE_SCOPING_PROBLEM} marker is created for the {@link IFile file} in question.
	 * 
	 * @param files
	 *            The collection of {@link IFile file}s to be validated in terms of resource scoping.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void validateFiles(final Collection<IFile> files, boolean async, final IProgressMonitor monitor) {
		Assert.isNotNull(files);

		if (async && files.size() > 0) {
			// Check first if job should really be created or not
			Job job = new WorkspaceJob(Messages.job_validatingResourceScopes) {
				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					runValidateFiles(files, monitor);
					return Status.OK_STATUS;
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};

			/*
			 * !! Important Note !! Updating problem marker jobs must be scheduled on workspace root to avoid that
			 * multiple of them run in parallel. Otherwise they would cause deadlocks even though the files which they
			 * are acting upon are different. The reason is that the IFile#createMarker() and IMarker#setAttributes()
			 * methods being called by the updating problem marker jobs try to start an exclusive workspace operation
			 * which is scheduled upon the marker rule of each underlying file. But for some reason which we don't
			 * understand marker rules of file are always null... (see
			 * org.eclipse.core.internal.resources.Rules#markerRule for details)
			 */
			job.setPriority(Job.BUILD);
			job.setRule(ResourcesPlugin.getWorkspace().getRoot());
			job.schedule();
		} else {
			try {
				runValidateFiles(files, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runValidateFiles(final Collection<IFile> files, final IProgressMonitor monitor) {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_validatingResourceScopes, files.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (IFile file : files) {
			try {
				if (file != null && file.isAccessible()) {
					// Delete old resource scoping problem maker if any
					file.deleteMarkers(IResourceScopeMarker.RESOURCE_SCOPING_PROBLEM, false, IResource.DEPTH_ZERO);

					/*
					 * Performance optimization: Check if current file is a potential model file by investigating it's
					 * extension. This helps excluding obvious non-model files right away and avoids potentially lengthy
					 * but useless processing of the same.
					 */
					if (ResourceScopeProviderRegistry.INSTANCE.hasApplicableFileExtension(file)) {
						// Retrieve resource scope provider associated with given file
						IMetaModelDescriptor effectiveDescriptor = MetaModelDescriptorRegistry.INSTANCE.getEffectiveDescriptor(file);
						IResourceScopeProvider resourceScopeProvider = ResourceScopeProviderRegistry.INSTANCE
								.getResourceScopeProvider(effectiveDescriptor);
						if (resourceScopeProvider != null) {
							// Validate file in terms of resource scoping
							Diagnostic diagnostic = resourceScopeProvider.validate(file);
							if (diagnostic != null && !diagnostic.equals(Diagnostic.OK_INSTANCE)) {
								// Delete all other old problem markers - as resource is a model resource and out of
								// scope they most likely make no longer any sense
								file.deleteMarkers(null, false, IResource.DEPTH_ZERO);

								// Create new resource scoping problem maker
								createProblemMarkerForDiagnostic(file, diagnostic);
							}
						}
					}
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	private void createProblemMarkerForDiagnostic(IFile file, Diagnostic diagnostic) throws CoreException {
		Assert.isNotNull(file);
		Assert.isLegal(file.isAccessible());
		Assert.isNotNull(diagnostic);

		IMarker marker = file.createMarker(IResourceScopeMarker.RESOURCE_SCOPING_PROBLEM);
		int severity = diagnostic.getSeverity();
		if (severity == Diagnostic.ERROR) {
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} else if (severity == Diagnostic.WARNING) {
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		} else if (severity == Diagnostic.INFO) {
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
		}
		marker.setAttribute(IMarker.MESSAGE, diagnostic.getMessage());
	}

	/**
	 * Removes all {@link IResourceScopeMarker#RESOURCE_SCOPING_PROBLEM resource scoping problem marker}s from the
	 * {@link IFile file}s in provided collection.
	 * 
	 * @param files
	 *            The collection of {@link IFile file}s to be cleaned.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void cleanFiles(final Collection<IFile> files, boolean async, final IProgressMonitor monitor) {
		Assert.isNotNull(files);

		if (async && files.size() > 0) {
			// Check first if job should really be created or not
			Job job = new WorkspaceJob(Messages.job_cleaningResourceScopeMarkers) {
				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					runCleanFiles(files, monitor);
					return Status.OK_STATUS;
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};

			/*
			 * !! Important Note !! Updating problem marker jobs must be scheduled on workspace root to avoid that
			 * multiple of them run in parallel. Otherwise they would cause deadlocks even though the files which they
			 * are acting upon are different. The reason is that the IFile#createMarker() and IMarker#setAttributes()
			 * methods being called by the updating problem marker jobs try to start an exclusive workspace operation
			 * which is scheduled upon the marker rule of each underlying file. But for some reason which we don't
			 * understand marker rules of file are always null... (see
			 * org.eclipse.core.internal.resources.Rules#markerRule for details)
			 */
			job.setPriority(Job.BUILD);
			job.setRule(ResourcesPlugin.getWorkspace().getRoot());
			job.schedule();
		} else {
			try {
				runCleanFiles(files, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runCleanFiles(Collection<IFile> files, IProgressMonitor monitor) {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_cleaningResourceScopeMarkers, files.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (IFile file : files) {
			try {
				if (file != null && file.isAccessible()) {
					/*
					 * Performance optimization: Check if current file is a potential model file by investigating it's
					 * extension. This helps excluding obvious non-model files right away and avoids potentially lengthy
					 * but useless processing of the same.
					 */
					if (ResourceScopeProviderRegistry.INSTANCE.hasApplicableFileExtension(file)) {
						// Delete old resource scoping problem maker if any
						file.deleteMarkers(IResourceScopeMarker.RESOURCE_SCOPING_PROBLEM, true, IResource.DEPTH_ZERO);
					}
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}
}
