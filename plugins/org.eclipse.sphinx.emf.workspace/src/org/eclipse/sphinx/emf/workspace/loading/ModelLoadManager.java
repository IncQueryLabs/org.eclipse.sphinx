/**
 * <copyright>
 *
 * Copyright (c) 2008-2014 BMW Car IT, itemis, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     BMW Car IT - [374883] Improve handling of out-of-sync workspace files during descriptor initialization
 *     itemis - [393021] ClassCastExceptions raised during loading model resources with Sphinx are ignored
 *     itemis - [409458] Enhance ScopingResourceSetImpl#getEObjectInScope() to enable cross-document references between model files with different metamodels
 *     itemis - [418005] Add support for model files with multiple root elements
 *     itemis - [409510] Enable resource scope-sensitive proxy resolutions without forcing metamodel implementations to subclass EObjectImpl
 *     itemis - [427461] Add progress monitor to resource load options (useful for loading large models)
 *     itemis - [454092] Loading model resources
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.scoping.ProjectResourceScope;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.emf.workspace.internal.loading.UnresolveUnreachableCrossProjectReferencesJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.UpdateResourceURIJob;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.FileUnloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ModelLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ModelUnloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectLoadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.ProjectReloadOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.UnresolveUnreachableCrossProjectReferencesOperation;
import org.eclipse.sphinx.emf.workspace.loading.operations.UpdateResourceURIOperation;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * Provides API for loading, unloading, and reloading projects, models, or files.
 */
public final class ModelLoadManager {

	/**
	 * Singleton instance of this Model Load Manager.
	 */
	public static final ModelLoadManager INSTANCE = new ModelLoadManager();

	/**
	 * Private constructor for the singleton pattern that prevents from instantiation by clients.
	 */
	private ModelLoadManager() {

	}

	private Map<TransactionalEditingDomain, Collection<Resource>> detectResourcesToUnload(final IProject project,
			final boolean includeReferencedScopes, final IMetaModelDescriptor mmFilter, IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(project);

		// Investigate all editing domains for loaded resources which belong to given project (or one of its referenced
		// projects if required)
		/*
		 * !! Important Note !! For the sake of robustness, it is necessary to consider all editing domains but not only
		 * the those which would be returned by WorkspaceEditingDomainUtil#getEditingDomains(IContainer). Although not
		 * really intended by Sphinx workspace management it might anyway happen that wired kind of applications manage
		 * to populate editing domains with resources which don't belong to any IModelDescriptor. In this case,
		 * WorkspaceEditingDomainUtil#getEditingDomains(IContainer) is likely to return an incomplete set of editing
		 * domains for given project and we would have no chance to make sure that actually all resources from given
		 * project (and its referenced projects if required) get unloaded.
		 */
		Collection<TransactionalEditingDomain> editingDomains = WorkspaceEditingDomainUtil.getAllEditingDomains();
		SubMonitor progress = SubMonitor.convert(monitor, editingDomains.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload = new HashMap<TransactionalEditingDomain, Collection<Resource>>();
		for (final TransactionalEditingDomain editingDomain : editingDomains) {
			try {
				List<Resource> resourcesToUnloadInEditingDomain = TransactionUtil.runExclusive(editingDomain,
						new RunnableWithResult.Impl<List<Resource>>() {
							@Override
							public void run() {
								List<Resource> resources = new ArrayList<Resource>();
								ProjectResourceScope projectResourceScope = new ProjectResourceScope(project);
								for (Resource resource : editingDomain.getResourceSet().getResources()) {
									if (projectResourceScope.belongsTo(resource, includeReferencedScopes)) {
										if (mmFilter != null) {
											IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(resource);
											if (mmDescriptor == null) {
												mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getOldDescriptor(resource);
											}
											if (mmFilter.equals(mmDescriptor)) {
												resources.add(resource);
											}
										} else {
											resources.add(resource);
										}
									}
								}
								setResult(resources);
							}
						});
				if (resourcesToUnloadInEditingDomain.size() > 0) {
					resourcesToUnload.put(editingDomain, resourcesToUnloadInEditingDomain);
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return resourcesToUnload;
	}

	/**
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadWorkspace(boolean async, IProgressMonitor monitor) {
		loadAllProjects(null, async, monitor);
	}

	/**
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadAllProjects(final IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		// Collect root projects in workspace
		Collection<IProject> projects = ExtendedPlatform.getRootProjects();

		// No projects found?
		if (projects.size() == 0) {
			return;
		}

		// Load models from root projects including referenced projects
		loadProjects(projects, true, mmDescriptor, async, monitor);
	}

	/**
	 * @param project
	 * @param includeReferencedProjects
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadProject(IProject project, boolean includeReferencedProjects, boolean async, IProgressMonitor monitor) {
		loadProjects(Collections.singleton(project), includeReferencedProjects, null, async, monitor);
	}

	/**
	 * @param project
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadProject(IProject project, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor, boolean async,
			IProgressMonitor monitor) {
		loadProjects(Collections.singleton(project), includeReferencedProjects, mmDescriptor, async, monitor);
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadProjects(Collection<IProject> projects, boolean includeReferencedProjects, boolean async, IProgressMonitor monitor) {
		loadProjects(projects, includeReferencedProjects, null, async, monitor);
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadProjects(final Collection<IProject> projects, final boolean includeReferencedProjects, final IMetaModelDescriptor mmDescriptor,
			boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(projects);

		if (!projects.isEmpty()) {
			ProjectLoadOperation projectLoadOperation = new ProjectLoadOperation(projects, includeReferencedProjects, mmDescriptor);
			LoadOperationRunnerHelper.run(projectLoadOperation, async, monitor);
		}
	}

	/**
	 * @param file
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadFile(IFile file, boolean async, IProgressMonitor monitor) {
		loadFiles(Collections.singleton(file), null, async, monitor);
	}

	/**
	 * @param file
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadFile(IFile file, IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		loadFiles(Collections.singleton(file), mmDescriptor, async, monitor);
	}

	/**
	 * @param files
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadFiles(Collection<IFile> files, boolean async, IProgressMonitor monitor) {
		loadFiles(files, null, async, monitor);
	}

	/**
	 * @param files
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadFiles(final Collection<IFile> files, final IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(files);

		if (!files.isEmpty()) {
			FileLoadOperation fileLoadOperation = new FileLoadOperation(files, mmDescriptor);
			LoadOperationRunnerHelper.run(fileLoadOperation, async, monitor);
		}
	}

	/**
	 * @param uri
	 *            a model URI
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadURI(URI uri, boolean async, IProgressMonitor monitor) {
		loadURIs(Collections.singleton(uri), null, async, monitor);
	}

	/**
	 * @param uri
	 *            a model URI
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadURI(URI uri, IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		loadURIs(Collections.singleton(uri), mmDescriptor, async, monitor);
	}

	/**
	 * @param uris
	 *            a set of model URIs
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadURIs(Collection<URI> uris, boolean async, IProgressMonitor monitor) {
		loadURIs(uris, null, async, monitor);
	}

	/**
	 * @param uris
	 *            a set of model URIs
	 * @param mmDescriptor
	 *            the metamodel descriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadURIs(final Collection<URI> uris, final IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(uris);
		loadFiles(getFiles(uris), mmDescriptor, async, monitor);
	}

	/**
	 * @param project
	 * @param includeReferencedProjects
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadProject(IProject project, boolean includeReferencedProjects, boolean async, IProgressMonitor monitor) {
		unloadProjects(Collections.singleton(project), includeReferencedProjects, null, async, monitor);
	}

	/**
	 * @param project
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadProject(IProject project, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor, boolean async,
			IProgressMonitor monitor) {
		unloadProjects(Collections.singleton(project), includeReferencedProjects, mmDescriptor, async, monitor);
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadProjects(Collection<IProject> projects, boolean includeReferencedProjects, boolean async, IProgressMonitor monitor) {
		unloadProjects(projects, includeReferencedProjects, null, async, monitor);
	}

	public void unloadAllProjects(final IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		// Collect root projects in workspace
		Collection<IProject> projects = ExtendedPlatform.getRootProjects();

		// No projects found?
		if (projects.size() == 0) {
			return;
		}

		// Unload models from root projects including referenced projects
		unloadProjects(projects, true, mmDescriptor, async, monitor);
	}

	/**
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadWorkspace(boolean async, IProgressMonitor monitor) {
		unloadAllProjects(null, async, monitor);
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadProjects(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor, boolean async,
			IProgressMonitor monitor) {
		Assert.isNotNull(projects);
		try {
			for (IProject project : projects) {
				String taskName = mmDescriptor != null ? NLS.bind(Messages.task_unloadingModelInProject, mmDescriptor.getName(), project.getName())
						: NLS.bind(Messages.task_unloadingModelsInProject, project.getName());
				SubMonitor progress = SubMonitor.convert(monitor, taskName, 100);
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				// Detect files to unload in given project and its referenced projects
				/*
				 * !! Important Note !! Perform this part always synchronously. Otherwise - when being called right
				 * before the project is closed or deleted - the affected files might no longer exist and couldn't be
				 * retrieved anymore by the time where an asynchronous job would be running.
				 */
				// FIXME Include referenced projects only if only the given project or its child projects but no other
				// root projects reference them; add an appropriate method to ExtendedPlatform for that purpose
				Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload = detectResourcesToUnload(project, includeReferencedProjects,
						mmDescriptor, progress.newChild(1));
				if (resourcesToUnload.size() == 0) {
					progress.done();
					continue;
				}

				// Unload resources; perform memory-optimized unloading if given project is a root project, i.e. is not
				// referenced by any other project
				internalUnloadResources(resourcesToUnload, ExtendedPlatform.isRootProject(project), async, progress.newChild(99));
			}
		} catch (OperationCanceledException ex) {
			// Ignore exception
		}
	}

	private void internalUnloadResources(final Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload,
			final boolean memoryOptimized, boolean async, IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(resourcesToUnload);

		if (!resourcesToUnload.isEmpty()) {
			ModelUnloadOperation unloadModelResourceOperation = new ModelUnloadOperation(resourcesToUnload, memoryOptimized);
			LoadOperationRunnerHelper.run(unloadModelResourceOperation, async, monitor);
		}
	}

	/**
	 * @param file
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadFile(IFile file, boolean async, IProgressMonitor monitor) {
		unloadFiles(Collections.singleton(file), null, false, async, monitor);
	}

	/**
	 * @param file
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadFile(IFile file, IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		unloadFiles(Collections.singleton(file), mmDescriptor, false, async, monitor);
	}

	/**
	 * @param files
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadFiles(Collection<IFile> files, boolean memoryOptimized, boolean async, IProgressMonitor monitor) {
		unloadFiles(files, null, memoryOptimized, async, monitor);
	}

	/**
	 * @param files
	 * @param mmDescriptor
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadFiles(final Collection<IFile> files, final IMetaModelDescriptor mmDescriptor, final boolean memoryOptimized, boolean async,
			IProgressMonitor monitor) {
		Assert.isNotNull(files);

		if (!files.isEmpty()) {
			FileUnloadOperation fileUnloadOperation = new FileUnloadOperation(files, mmDescriptor, memoryOptimized);
			LoadOperationRunnerHelper.run(fileUnloadOperation, async, monitor);
		}
	}

	/**
	 * @param uri
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadURI(URI uri, boolean async, IProgressMonitor monitor) {
		unloadURIs(Collections.singleton(uri), null, false, async, monitor);
	}

	/**
	 * @param uri
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadURI(URI uri, IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		unloadURIs(Collections.singleton(uri), mmDescriptor, false, async, monitor);
	}

	/**
	 * @param uris
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadURIs(Collection<URI> uris, boolean memoryOptimized, boolean async, IProgressMonitor monitor) {
		unloadURIs(uris, null, memoryOptimized, async, monitor);
	}

	/**
	 * @param uris
	 * @param mmDescriptor
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadURIs(final Collection<URI> uris, final IMetaModelDescriptor mmDescriptor, final boolean memoryOptimized, boolean async,
			IProgressMonitor monitor) {
		Assert.isNotNull(uris);
		unloadFiles(getFiles(uris), mmDescriptor, memoryOptimized, async, monitor);
	}

	/**
	 * @param project
	 * @param includeReferencedProjects
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadProject(IProject project, boolean includeReferencedProjects, boolean async, IProgressMonitor monitor) {
		reloadProjects(Collections.singleton(project), includeReferencedProjects, null, async, monitor);
	}

	/**
	 * @param project
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadProject(IProject project, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor, boolean async,
			IProgressMonitor monitor) {
		reloadProjects(Collections.singleton(project), includeReferencedProjects, mmDescriptor, async, monitor);
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadProjects(Collection<IProject> projects, boolean includeReferencedProjects, boolean async, IProgressMonitor monitor) {
		reloadProjects(projects, includeReferencedProjects, null, async, monitor);
	}

	/**
	 * @param projects
	 * @param includeReferencedProjects
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadProjects(final Collection<IProject> projects, final boolean includeReferencedProjects, final IMetaModelDescriptor mmDescriptor,
			boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(projects);

		if (!projects.isEmpty()) {
			ProjectReloadOperation projectReloadOperation = new ProjectReloadOperation(projects, includeReferencedProjects, mmDescriptor);
			LoadOperationRunnerHelper.run(projectReloadOperation, async, monitor);
		}
	}

	/**
	 * @param file
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadFile(IFile file, boolean async, IProgressMonitor monitor) {
		reloadFiles(Collections.singleton(file), null, false, async, monitor);
	}

	/**
	 * @param file
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadFile(IFile file, IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		reloadFiles(Collections.singleton(file), mmDescriptor, false, async, monitor);
	}

	/**
	 * @param files
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadFiles(Collection<IFile> files, boolean memoryOptimized, boolean async, IProgressMonitor monitor) {
		reloadFiles(files, null, memoryOptimized, async, monitor);
	}

	/**
	 * @param files
	 * @param mmDescriptor
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadFiles(final Collection<IFile> files, final IMetaModelDescriptor mmDescriptor, final boolean memoryOptimized, boolean async,
			IProgressMonitor monitor) {
		Assert.isNotNull(files);

		if (!files.isEmpty()) {
			FileReloadOperation fileReloadOperation = new FileReloadOperation(files, mmDescriptor, memoryOptimized);
			LoadOperationRunnerHelper.run(fileReloadOperation, async, monitor);
		}
	}

	/**
	 * @param uri
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadURI(URI uri, boolean async, IProgressMonitor monitor) {
		reloadURIs(Collections.singleton(uri), null, false, async, monitor);
	}

	/**
	 * @param uri
	 * @param mmDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadURI(URI uri, IMetaModelDescriptor mmDescriptor, boolean async, IProgressMonitor monitor) {
		reloadURIs(Collections.singleton(uri), mmDescriptor, false, async, monitor);
	}

	/**
	 * @param uris
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadURIs(Collection<URI> uris, boolean memoryOptimized, boolean async, IProgressMonitor monitor) {
		reloadURIs(uris, null, memoryOptimized, async, monitor);
	}

	/**
	 * @param uris
	 * @param mmDescriptor
	 * @param memoryOptimized
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void reloadURIs(final Collection<URI> uris, final IMetaModelDescriptor mmDescriptor, final boolean memoryOptimized, boolean async,
			IProgressMonitor monitor) {
		Assert.isNotNull(uris);
		reloadFiles(getFiles(uris), mmDescriptor, memoryOptimized, async, monitor);
	}

	/**
	 * Unresolves (i.e. proxifies) all references of all models inside given set of {@link IProject projects} pointing
	 * to elements of models in {@link IProject project}s which are outside the scope of the underlying
	 * {@link IModelDescriptor model descriptor}s. This method typically needs to be called when references between
	 * {@link IProject project}s are removed in order to make sure that models in the formerly referencing
	 * {@link IProject project}s do no longer reference any elements of models in the formerly referenced
	 * {@link IProject project}s.
	 *
	 * @param projects
	 *            The project of which the unreachable cross project references are to be unresolved.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 * @see IModelDescriptor#getReferencedRoots()
	 */
	public void unresolveUnreachableCrossProjectReferences(final Collection<IProject> projects, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(projects);

		// Perform unresolving procedure in all given projects as well as in all projects that reference those
		final HashSet<IProject> projectsWithUnreachableCrossRefrencesToUnresolve = new HashSet<IProject>(projects);
		for (IProject project : projects) {
			Collection<IProject> referencingProjects = ExtendedPlatform.getAllReferencingProjects(project);
			projectsWithUnreachableCrossRefrencesToUnresolve.addAll(referencingProjects);
		}

		UnresolveUnreachableCrossProjectReferencesOperation unresolveUnreachableCrossProjectReferencesOperation = new UnresolveUnreachableCrossProjectReferencesOperation(
				projectsWithUnreachableCrossRefrencesToUnresolve);
		if (async && projectsWithUnreachableCrossRefrencesToUnresolve.size() > 0) {
			Job job = new UnresolveUnreachableCrossProjectReferencesJob(unresolveUnreachableCrossProjectReferencesOperation);
			job.setPriority(Job.BUILD);
			job.setRule(unresolveUnreachableCrossProjectReferencesOperation.getRule());
			job.schedule();
		} else {
			try {
				unresolveUnreachableCrossProjectReferencesOperation.run(monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			} catch (CoreException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
	}

	/**
	 * Loads all resources owned by the provided {@link IModelDescriptor model descriptor} (i.e all the persisted
	 * resources owned by the model)
	 *
	 * @param modelDescriptor
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadModel(IModelDescriptor modelDescriptor, boolean async, IProgressMonitor monitor) {
		loadModel(modelDescriptor, true, async, monitor);
	}

	/**
	 * Loads all resources owned by the provided {@link IModelDescriptor model descriptor} (i.e all the persisted
	 * resources owned by the model)
	 *
	 * @param modelDescriptor
	 *            the {@link IModelDescriptor model descriptor} describing the model.
	 * @param includeReferencedScopes
	 *            determine weither or not the referenced scopes must be taken into account while retrieving the
	 *            persisted files to load.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadModel(final IModelDescriptor modelDescriptor, boolean includeReferencedScopes, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(modelDescriptor);

		final Collection<IFile> persistedFiles = modelDescriptor.getPersistedFiles(includeReferencedScopes);
		if (!persistedFiles.isEmpty()) {
			ModelLoadOperation modelLoadOperation = new ModelLoadOperation(modelDescriptor, persistedFiles);
			LoadOperationRunnerHelper.run(modelLoadOperation, async, monitor);
		}
	}

	/**
	 * Loads in memory all persisted resources owned by the model described by the {@link IModelDescriptor model
	 * Descriptor}s provided in argument.
	 *
	 * @param modelDescriptors
	 *            {@link IModelDescriptor model Descriptor}s describing the models to load.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadModels(Collection<IModelDescriptor> modelDescriptors, boolean async, IProgressMonitor monitor) {
		loadModels(modelDescriptors, true, async, monitor);
	}

	/**
	 * Loads in memory all persisted resources owned by the model described by the {@link IModelDescriptor model
	 * Descriptor}s provided in argument.
	 *
	 * @param modelDescriptors
	 *            {@link IModelDescriptor model Descriptor}s describing the models to load.
	 * @param includeReferencedScopes
	 *            Boolean that determine if the {@link IModelDescriptor model Descriptor}s referenced by the
	 *            {@link IModelDescriptor model Descriptor}s provided in argument must be loaded too.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void loadModels(Collection<IModelDescriptor> modelDescriptors, boolean includeReferencedScopes, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(modelDescriptors);
		SubMonitor progress = SubMonitor.convert(monitor, modelDescriptors.size());

		for (IModelDescriptor modelDescriptor : modelDescriptors) {
			loadModel(modelDescriptor, includeReferencedScopes, async, progress.newChild(1));

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	/**
	 * Unloads all resources owned by the {@link IModelDescriptor model Descriptor} provided in argument.
	 *
	 * @param modelDescriptor
	 *            The {@link IModelDescriptor model Descriptor} for the one resources must be unloaded
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadModel(IModelDescriptor modelDescriptor, boolean async, IProgressMonitor monitor) {
		unloadModel(modelDescriptor, true, async, monitor);
	}

	/**
	 * Unloads all resources owned by the {@link IModelDescriptor model Descriptor} provided in argument.
	 *
	 * @param modelDescriptor
	 *            The {@link IModelDescriptor model Descriptor} for the one resources must be unloaded.
	 * @param includeReferencedScopes
	 *            Boolean that determine if the {@link IModelDescriptor model Descriptor}s referenced by the
	 *            {@link IModelDescriptor model Descriptor} provided in argument must be unloaded too.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadModel(final IModelDescriptor modelDescriptor, final boolean includeReferencedScopes, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(modelDescriptor);

		// Collect resources to unload in given model
		Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload = new HashMap<TransactionalEditingDomain, Collection<Resource>>();
		resourcesToUnload.put(modelDescriptor.getEditingDomain(), modelDescriptor.getLoadedResources(includeReferencedScopes));

		// Unload resources; perform memory-optimized unloading
		internalUnloadResources(resourcesToUnload, true, async, monitor);
	}

	/**
	 * Unload all resources owned by the {@link IModelDescriptor model Descriptor}s provided in argument.
	 *
	 * @param modelDescriptors
	 *            The {@link IModelDescriptor model Descriptor}s owning resources that must be unloaded.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadModels(Collection<IModelDescriptor> modelDescriptors, boolean async, IProgressMonitor monitor) {
		unloadModels(modelDescriptors, true, async, monitor);
	}

	/**
	 * Unload all resources owned by the {@link IModelDescriptor model Descriptor}s provided in argument.
	 *
	 * @param modelDescriptors
	 *            The {@link IModelDescriptor model Descriptor}s owning resources that must be unloaded.
	 * @param includeReferencedScopes
	 *            Boolean that determine if the {@link IModelDescriptor model Descriptor}s referenced by the
	 *            {@link IModelDescriptor model Descriptor}s provided in argument must be unloaded too.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void unloadModels(Collection<IModelDescriptor> modelDescriptors, boolean includeReferencedScopes, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(modelDescriptors);
		SubMonitor progress = SubMonitor.convert(monitor, modelDescriptors.size());

		for (IModelDescriptor modelDescriptor : modelDescriptors) {
			unloadModel(modelDescriptor, includeReferencedScopes, async, progress.newChild(1));

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	/**
	 * Updates {@link URI}s of {@link Resource resource}s behind the old {@link IFile file}s (keys) in the given map
	 * according to the new {@link IPath path}s (values) they are mapped to. May be run synchronously or asynchronously.
	 *
	 * @param filesToUpdate
	 *            A map specifying relevant old {@link IFile file}s along with their respective new {@link IPath path}s.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 */
	public void updateResourceURIs(final Map<IFile, IPath> filesToUpdate, boolean async, IProgressMonitor monitor) {
		Assert.isNotNull(filesToUpdate);

		UpdateResourceURIOperation updateResourceURIOperation = new UpdateResourceURIOperation(filesToUpdate);
		if (async && filesToUpdate.size() > 0) {
			Job job = new UpdateResourceURIJob(updateResourceURIOperation);
			job.setPriority(Job.BUILD);
			job.setRule(updateResourceURIOperation.getRule());
			job.schedule();
		} else {
			try {
				updateResourceURIOperation.run(monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			} catch (CoreException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
	}

	private Collection<IFile> getFiles(Collection<URI> uris) {
		Set<IFile> files = new HashSet<IFile>();
		if (uris != null) {
			Set<URI> modelResourceURIs = new HashSet<URI>();
			for (URI uri : uris) {
				// Removing URI fragment
				modelResourceURIs.add(uri.trimFragment());
			}

			for (URI modelResourceURI : modelResourceURIs) {
				final IFile file = EcorePlatformUtil.getFile(modelResourceURI);
				if (file != null) {
					files.add(file);
				}
			}
		}
		return files;
	}
}