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
package org.eclipse.sphinx.emf.workspace.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelper;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelperAdapterFactory;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.blacklist.ModelIndex;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.lookupresolver.EcoreIndex;
import org.eclipse.sphinx.emf.internal.resource.ResourceProblemMarkerService;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.scoping.IResourceScope;
import org.eclipse.sphinx.emf.scoping.IResourceScopeProvider;
import org.eclipse.sphinx.emf.scoping.ProjectResourceScope;
import org.eclipse.sphinx.emf.scoping.ResourceScopeProviderRegistry;
import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.emf.workspace.internal.loading.FileLoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.LoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadingPerformanceStats;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadingPerformanceStats.ModelEvent;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;

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
		// Nothing to do
	}

	private Map<TransactionalEditingDomain, Collection<IFile>> detectFilesToLoad(Collection<IFile> files, IMetaModelDescriptor mmFilter,
			boolean ignoreIfAlreadyLoaded, IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, files.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad = new HashMap<TransactionalEditingDomain, Collection<IFile>>();
		for (IFile file : files) {
			try {
				// Exclude inaccessible files
				if (file.isAccessible()) {
					/*
					 * Performance optimization: Check if current file is a potential model file inside an existing
					 * scope. This helps excluding obvious non-model files and model files that are out of scope right
					 * away and avoids potentially lengthy but useless processing of the same.
					 */
					if (!ResourceScopeProviderRegistry.INSTANCE.isNotInAnyScope(file)) {
						progress.subTask(NLS.bind(Messages.subtask_analyzingFile, file.getFullPath()));

						// Skip files which don't make it through specified meta model filter
						IMetaModelDescriptor effectiveMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getEffectiveDescriptor(file);
						if (mmFilter == null || mmFilter.getClass().isInstance(effectiveMMDescriptor)) {
							// Ignore files that already have been loaded unless specified otherwise
							if (!ignoreIfAlreadyLoaded || !EcorePlatformUtil.isFileLoaded(file)) {
								// Retrieve resource scope which current file belongs to
								IResourceScopeProvider resourceScopeProvider = ResourceScopeProviderRegistry.INSTANCE
										.getResourceScopeProvider(effectiveMMDescriptor);
								if (resourceScopeProvider != null) {
									IResourceScope resourceScope = resourceScopeProvider.getScope(file);
									if (resourceScope != null) {
										// Retrieve editing domain which current file is mapped to
										TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getMappedEditingDomain(file);
										if (editingDomain != null) {
											// Retrieve already existing load file request for current editing domain
											Collection<IFile> filesToLoadInEditingDomain = filesToLoad.get(editingDomain);
											if (filesToLoadInEditingDomain == null) {
												filesToLoadInEditingDomain = new HashSet<IFile>();
												filesToLoad.put(editingDomain, filesToLoadInEditingDomain);
											}
											// Add current file current load file request
											filesToLoadInEditingDomain.add(file);
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return filesToLoad;
	}

	private Map<TransactionalEditingDomain, Map<IFile, IPath>> detectFilesToUpdateResourceURIFor(Map<IFile, IPath> files, IProgressMonitor monitor) {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, files.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Map<IFile, IPath>> filesToUpdateResourceURIFor = new HashMap<TransactionalEditingDomain, Map<IFile, IPath>>();
		for (final IFile oldFile : files.keySet()) {
			try {
				/*
				 * Performance optimization: Check if current file is a potential model file inside an existing scope.
				 * This helps excluding obvious non-model files and model files that are out of scope right away and
				 * avoids potentially lengthy but useless processing of the same.
				 */
				if (!ResourceScopeProviderRegistry.INSTANCE.isNotInAnyScope(oldFile)) {
					progress.subTask(NLS.bind(Messages.subtask_analyzingFile, oldFile.getFullPath()));

					TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getCurrentEditingDomain(oldFile);
					if (editingDomain != null) {
						Map<IFile, IPath> filesToUpdateResourceURIForInEditingDomain = filesToUpdateResourceURIFor.get(editingDomain);
						if (filesToUpdateResourceURIForInEditingDomain == null) {
							filesToUpdateResourceURIForInEditingDomain = new HashMap<IFile, IPath>();
							filesToUpdateResourceURIFor.put(editingDomain, filesToUpdateResourceURIForInEditingDomain);
						}
						filesToUpdateResourceURIForInEditingDomain.put(oldFile, files.get(oldFile));
					}
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return filesToUpdateResourceURIFor;
	}

	private Map<TransactionalEditingDomain, Collection<IFile>> detectFilesToUnload(Collection<IFile> files, IMetaModelDescriptor mmFilter,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, files.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Collection<IFile>> filesToUnload = new HashMap<TransactionalEditingDomain, Collection<IFile>>();
		for (IFile file : files) {
			try {
				/*
				 * Performance optimization: Check if current file is a potential model file by investigating it's
				 * extension. This helps excluding obvious non-model files right away and avoids potentially lengthy but
				 * useless processing of the same.
				 */
				if (ResourceScopeProviderRegistry.INSTANCE.hasApplicableFileExtension(file)) {
					progress.subTask(NLS.bind(Messages.subtask_analyzingFile, file.getFullPath()));

					// Skip files which don't make it through specified meta model filter
					IMetaModelDescriptor effectiveMMDescriptor = MetaModelDescriptorRegistry.INSTANCE.getEffectiveDescriptor(file);
					if (mmFilter == null || mmFilter.getClass().isInstance(effectiveMMDescriptor)) {
						// Retrieve editing domains in which current file is currently loaded
						/*
						 * !! Important Note !! For the sake of robustness, it is necessary to consider all editing
						 * domains but not only the one which would be returned by
						 * WorkspaceEditingDomainUtil#getCurrentEditingDomain(IFile). Although not really intended by
						 * Sphinx workspace management it might anyway happen that the same file gets loaded into
						 * multiple editing domains. Typical reasons for this are e.g. lazy loading of one file from
						 * multiple other files which are in different editing domains or programatic action by some
						 * application. We then have to make sure that the given file gets unloaded from all editing
						 * domains it is in.
						 */
						for (TransactionalEditingDomain editingDomain : WorkspaceEditingDomainUtil.getAllEditingDomains()) {
							if (EcorePlatformUtil.isFileLoaded(editingDomain, file)) {
								Collection<IFile> filesToUnloadInEditingDomain = filesToUnload.get(editingDomain);
								if (filesToUnloadInEditingDomain == null) {
									filesToUnloadInEditingDomain = new HashSet<IFile>();
									filesToUnload.put(editingDomain, filesToUnloadInEditingDomain);
								}
								filesToUnloadInEditingDomain.add(file);
							}
						}
					}
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return filesToUnload;
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

	private int getFilesToLoadCount(Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad) {
		Assert.isNotNull(filesToLoad);

		int count = 0;
		for (TransactionalEditingDomain editingDomain : filesToLoad.keySet()) {
			Collection<IFile> filesToLoadInEditingDomain = filesToLoad.get(editingDomain);
			count += filesToLoadInEditingDomain.size();
		}
		return count;
	}

	private int getFilesToUnloadCount(Map<TransactionalEditingDomain, Collection<IFile>> filesToUnload) {
		Assert.isNotNull(filesToUnload);

		int count = 0;
		for (TransactionalEditingDomain editingDomain : filesToUnload.keySet()) {
			count += filesToUnload.get(editingDomain).size();
		}
		return count;
	}

	private int getResourcesToUnloadCount(Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload) {
		Assert.isNotNull(resourcesToUnload);

		int count = 0;
		for (TransactionalEditingDomain editingDomain : resourcesToUnload.keySet()) {
			count += resourcesToUnload.get(editingDomain).size();
		}
		return count;
	}

	private ISchedulingRule createLoadSchedulingRule(Collection<IProject> projects, boolean includeReferencedProjects) {
		Assert.isNotNull(projects);

		/*
		 * Performance optimization: Create a scheduling rule on a per project basis only if number of projects is
		 * reasonably low.
		 */
		if (projects.size() < ExtendedPlatform.LIMIT_INDIVIDUAL_RESOURCES_SCHEDULING_RULE) {
			Set<ISchedulingRule> rules = new HashSet<ISchedulingRule>();
			for (IProject project : projects) {
				if (!includeReferencedProjects) {
					rules.add(project);
				} else {
					rules.addAll(ExtendedPlatform.getProjectGroup(project, false));
				}
			}
			return MultiRule.combine(rules.toArray(new ISchedulingRule[rules.size()]));
		} else {
			// Return workspace root as scheduling rule otherwise
			return ResourcesPlugin.getWorkspace().getRoot();
		}
	}

	private ISchedulingRule createLoadSchedulingRule(Collection<IFile> files) {
		/*
		 * Performance optimization: Create a scheduling rule on a per file basis only if number of files is reasonably
		 * low.
		 */
		if (files.size() < ExtendedPlatform.LIMIT_INDIVIDUAL_RESOURCES_SCHEDULING_RULE) {
			Assert.isNotNull(files);

			Set<ISchedulingRule> rules1 = new HashSet<ISchedulingRule>();
			for (IFile file : files) {
				ISchedulingRule rule = createLoadSchedulingRule(file);
				if (rule != null) {
					rules1.add(rule);
				}
			}
			Collection<ISchedulingRule> rules = rules1;
			return MultiRule.combine(rules.toArray(new ISchedulingRule[rules.size()]));
		} else {
			// Return workspace root as scheduling rule otherwise
			return ResourcesPlugin.getWorkspace().getRoot();
		}
	}

	private ISchedulingRule createLoadSchedulingRule(IFile file) {
		Assert.isNotNull(file);

		// Use parent resource as rule because URIConverterImpl may refresh file
		return file.getParent();
	}

	private ISchedulingRule createLoadSchedulingRule(Map<TransactionalEditingDomain, Collection<Resource>> resources) {
		Assert.isNotNull(resources);

		Collection<Resource> allResources = new HashSet<Resource>();
		for (Collection<Resource> resourcesInEditingDomain : resources.values()) {
			allResources.addAll(resourcesInEditingDomain);
		}

		/*
		 * Performance optimization: Create a scheduling rule on a per resource basis only if number of resources is
		 * reasonably low.
		 */
		if (allResources.size() < ExtendedPlatform.LIMIT_INDIVIDUAL_RESOURCES_SCHEDULING_RULE) {
			Set<ISchedulingRule> rules = new HashSet<ISchedulingRule>();
			for (Resource resource : allResources) {
				IFile file = EcorePlatformUtil.getFile(resource);
				rules.add(createLoadSchedulingRule(file));
			}
			return MultiRule.combine(rules.toArray(new ISchedulingRule[rules.size()]));
		} else {
			// Return workspace root as scheduling rule otherwise
			return ResourcesPlugin.getWorkspace().getRoot();
		}
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

		if (async && projects.size() > 0) {
			// Check first if job should really be created or not
			if (!LoadJob.shouldCreateJob(projects, includeReferencedProjects, mmDescriptor)) {
				return;
			}
			Job job = new ModelLoadJob(projects, includeReferencedProjects, mmDescriptor) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runLoadProjects(projects, includeReferencedProjects, mmDescriptor, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(projects, includeReferencedProjects));
			job.schedule();
		} else {
			try {
				runLoadProjects(projects, includeReferencedProjects, mmDescriptor, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runLoadProjects(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(projects);

		for (IProject project : projects) {
			String taskName = mmDescriptor != null ? NLS.bind(Messages.task_loadingModelInProject, mmDescriptor.getName(), project.getName()) : NLS
					.bind(Messages.task_loadingModelsInProject, project.getName());
			SubMonitor progress = SubMonitor.convert(monitor, taskName, 100);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			Collection<IFile> files = ExtendedPlatform.getAllFiles(project, includeReferencedProjects);
			progress.worked(1);

			// No files found?
			if (files.size() == 0) {
				progress.done();
				continue;
			}

			String context = ModelLoadingPerformanceStats.ModelContext.CONTEXT_LOAD_PROJECT.getName() + " " + project.getName(); //$NON-NLS-1$
			ModelLoadingPerformanceStats.INSTANCE.openContext(context);
			runDetectAndLoadModelFiles(files, mmDescriptor, progress.newChild(99));
			ModelLoadingPerformanceStats.INSTANCE.closeAndLogCurrentContext();
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

		if (async && files.size() > 0) {
			// Check first if job should really be created or not
			if (!LoadJob.shouldCreateJob(files, mmDescriptor)) {
				return;
			}
			Job job = new FileLoadJob(files, mmDescriptor) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runDetectAndLoadModelFiles(files, mmDescriptor, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(files));
			job.schedule();
		} else {
			try {
				runDetectAndLoadModelFiles(files, mmDescriptor, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runDetectAndLoadModelFiles(Collection<IFile> files, IMetaModelDescriptor mmDescriptor, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_loadingModelFiles, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		String contextLoadFiles = ModelLoadingPerformanceStats.ModelContext.CONTEXT_LOAD_FILES.getName();
		ModelLoadingPerformanceStats.INSTANCE.openContextIfFirstOne(contextLoadFiles);

		// Detect model resources among given files
		ModelEvent eventDetectFilesToLoad = ModelLoadingPerformanceStats.ModelEvent.EVENT_DETECT_FILES_TO_LOAD;
		ModelLoadingPerformanceStats.INSTANCE.startNewEvent(eventDetectFilesToLoad, files);

		Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad = detectFilesToLoad(files, mmDescriptor, true, progress.newChild(10));
		ModelLoadingPerformanceStats.INSTANCE.endEvent(eventDetectFilesToLoad, files);

		// Nothing to load?
		if (filesToLoad.size() == 0) {
			ModelLoadingPerformanceStats.INSTANCE.closeAndLogCurrentContext();
			progress.done();
			// TODO Surround with appropriate tracing option
			// System.out.println("[ModelLoadManager#runDetectAndLoadModelFiles()] No model files to be loaded");
			return;
		}
		ModelLoadingPerformanceStats.INSTANCE.closeContext(contextLoadFiles);
		runLoadModelFiles(filesToLoad, progress.newChild(90));
		// TODO Surround with appropriate tracing option
		// System.out.println("[ModelLoadManager#runDetectAndLoadModelFiles()] Loaded " +
		// getFilesToLoadCount(filesToLoad) + " model file(s)");
	}

	private void runLoadModelFiles(Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(filesToLoad);
		SubMonitor progress = SubMonitor.convert(monitor, filesToLoad.keySet().size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		String contextLoadFiles = ModelLoadingPerformanceStats.ModelContext.CONTEXT_LOAD_FILES.getName();
		boolean openedContext = ModelLoadingPerformanceStats.INSTANCE.openContextIfFirstOne(contextLoadFiles);

		// Iterate over editing domains of files to load
		for (TransactionalEditingDomain editingDomain : filesToLoad.keySet()) {
			loadModelFilesInEditingDomain(editingDomain, filesToLoad.get(editingDomain), progress.newChild(1));
		}

		// Perform a full garbage collection
		ExtendedPlatform.performGarbageCollection();

		// Close current performance stats context
		if (openedContext) {
			ModelLoadingPerformanceStats.INSTANCE.closeAndLogCurrentContext();
		}
	}

	private void loadModelFilesInEditingDomain(final TransactionalEditingDomain editingDomain, final Collection<IFile> filesToLoadInEditingDomain,
			final IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(editingDomain);
		Assert.isNotNull(filesToLoadInEditingDomain);

		try {
			editingDomain.runExclusive(new Runnable() {
				public void run() {
					SubMonitor progress = SubMonitor.convert(monitor, 100);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}

					// Disable resolution of fragment-based proxies while model loading is ongoing
					ProxyHelper proxyHelper = ProxyHelperAdapterFactory.INSTANCE.adapt(editingDomain.getResourceSet());
					if (proxyHelper != null) {
						proxyHelper.setIgnoreFragmentBasedProxies(true);
					}

					// Load files into editing domain
					SubMonitor loadProgress = progress.newChild(85).setWorkRemaining(filesToLoadInEditingDomain.size());
					Set<IFile> loadedFiles = new HashSet<IFile>();
					Map<IFile, Exception> failedFiles = new HashMap<IFile, Exception>();
					for (IFile file : filesToLoadInEditingDomain) {
						loadProgress.subTask(NLS.bind(Messages.subtask_loadingFile, file.getFullPath().toString()));
						try {
							ModelLoadingPerformanceStats.INSTANCE.startNewEvent(ModelLoadingPerformanceStats.ModelEvent.EVENT_LOAD_FILE, file
									.getFullPath().toString());

							try {
								EcorePlatformUtil.loadModelRoot(editingDomain, file);
								loadedFiles.add(file);
							} catch (Exception ex) {
								// Ignore exception, it has already been recorded as error on resource and will be
								// converted to a problem marker later on (see
								// org.eclipse.sphinx.emf.util.EcoreResourceUtil.loadModelResource(ResourceSet, URI,
								// Map<?, ?>, boolean) and
								// org.eclipse.sphinx.emf.internal.resource.ResourceProblemHandler for details)
							}

							ModelLoadingPerformanceStats.INSTANCE.endEvent(ModelLoadingPerformanceStats.ModelEvent.EVENT_LOAD_FILE, file
									.getFullPath().toString());
						} catch (Exception ex) {
							failedFiles.put(file, new XMIException(NLS.bind(Messages.error_problemOccurredWhenLoadingResource, file.getFullPath()),
									ex, file.getFullPath().toString(), 1, 1));
						}

						loadProgress.worked(1);
						if (loadProgress.isCanceled()) {
							throw new OperationCanceledException();
						}
						editingDomain.yield();
					}

					// Handle problems that may have been encountered during loading
					ResourceProblemMarkerService.INSTANCE.updateProblemMarkers(failedFiles, true, null);

					if (proxyHelper != null) {
						// Update unresolved proxy blacklist according to newly loaded files
						updateUnresolvedProxyBlackList(loadedFiles, proxyHelper.getBlackList());

						// Perform a performance-optimized resolution of fragment-based proxies
						forceProxyResolution(loadedFiles, proxyHelper.getLookupResolver(), progress.newChild(15));

						// Re-enable resolution of fragment-based proxies
						proxyHelper.setIgnoreFragmentBasedProxies(false);
					}
				}
			});
		} catch (InterruptedException ex) {
			PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
		}
	}

	private void updateUnresolvedProxyBlackList(Collection<IFile> files, ModelIndex blackList) {
		Assert.isNotNull(files);
		Assert.isNotNull(blackList);

		for (IFile file : files) {
			Resource resource = EcorePlatformUtil.getResource(file);
			blackList.updateIndexOnResourceLoaded(resource);
		}
	}

	/**
	 * This is a fast implementation of proxy resolution. The idea is to use a map of URI -> EObject to quickly get
	 * object.
	 */
	private void forceProxyResolution(Collection<IFile> files, EcoreIndex lookupResolver, IProgressMonitor monitor) {
		Assert.isNotNull(files);
		Assert.isNotNull(lookupResolver);
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		ModelLoadingPerformanceStats.INSTANCE.startNewEvent(ModelLoadingPerformanceStats.ModelEvent.EVENT_RESOLVE_PROXY, files);

		// Get complete set of model resources from given set of loaded files
		progress.subTask(Messages.subtask_initializingProxyResolution);
		Collection<Resource> resources = new HashSet<Resource>();
		for (IFile file : files) {
			Resource resource = EcorePlatformUtil.getResource(file);
			if (resource != null && !resources.contains(resource)) {
				resources.addAll(EcorePlatformUtil.getResourcesInModel(resource, true));
			}
		}

		synchronized (lookupResolver) {
			// Initialize lookup-based proxy resolver
			lookupResolver.init(resources);
			progress.worked(10);

			// Request resolution of all proxies in given resources
			SubMonitor resolveProxiesProgress = progress.newChild(90).setWorkRemaining(files.size());
			for (Resource resource : resources) {
				resolveProxiesProgress.subTask(NLS.bind(Messages.subtask_resolvingProxiesInResource, resource.getURI().toPlatformString(true)));

				EObjectUtil.resolveAll(resource);

				resolveProxiesProgress.worked(1);
				if (resolveProxiesProgress.isCanceled()) {
					throw new OperationCanceledException();
				}
			}

			// Clear lookup-based proxy resolver
			lookupResolver.clear();
		}

		// Handle problems that may have been encountered during proxy resolution
		ResourceProblemMarkerService.INSTANCE.updateProblemMarkers(resources, true, null);

		// Perform a full garbage collection
		ExtendedPlatform.performGarbageCollection();

		ModelLoadingPerformanceStats.INSTANCE.endEvent(ModelLoadingPerformanceStats.ModelEvent.EVENT_RESOLVE_PROXY, files);
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

		if (async && resourcesToUnload.size() > 0) {
			Job job = new Job(Messages.job_unloadingModelResources) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runUnloadModelResources(resourcesToUnload, memoryOptimized, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(resourcesToUnload));
			job.schedule();
		} else {
			runUnloadModelResources(resourcesToUnload, memoryOptimized, monitor);
		}
	}

	private void runUnloadModelResources(Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload, boolean memoryOptimized,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(resourcesToUnload);
		SubMonitor progress = SubMonitor.convert(monitor, getResourcesToUnloadCount(resourcesToUnload));
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (TransactionalEditingDomain editingDomain : resourcesToUnload.keySet()) {
			Collection<Resource> resourcesToUnloadInEditingDomain = resourcesToUnload.get(editingDomain);
			EcorePlatformUtil.unloadResources(editingDomain, resourcesToUnloadInEditingDomain, memoryOptimized,
					progress.newChild(resourcesToUnloadInEditingDomain.size()));

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		// Perform a full garbage collection
		ExtendedPlatform.performGarbageCollection();
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

		if (async && files.size() > 0) {
			Job job = new Job(Messages.job_unloadingModelResources) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runDetectAndUnloadModelFiles(files, mmDescriptor, memoryOptimized, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(files));
			job.schedule();
		} else {
			try {
				runDetectAndUnloadModelFiles(files, mmDescriptor, memoryOptimized, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runDetectAndUnloadModelFiles(Collection<IFile> files, IMetaModelDescriptor mmDescriptor, boolean memoryOptimized,
			IProgressMonitor monitor) throws OperationCanceledException {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_unloadingModelFiles, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Collection<IFile>> filesToUnload = detectFilesToUnload(files, mmDescriptor, progress.newChild(10));

		// Nothing to unload?
		if (filesToUnload.size() == 0) {
			progress.done();

			// TODO Surround with appropriate tracing option
			// System.out.println("[ModelLoadManager#runDetectAndUnloadModelFiles()] No model files to be unloaded");
			return;
		}

		runUnloadModelFiles(filesToUnload, memoryOptimized, progress.newChild(90));

		// TODO Surround with appropriate tracing option
		// System.out.println("[ModelLoadManager#runDetectAndUnloadModelFiles()] Unloaded " +
		// getFilesToUnloadCount(filesToUnload) + " model file(s)");
	}

	private void runUnloadModelFiles(Map<TransactionalEditingDomain, Collection<IFile>> filesToUnload, boolean memoryOptimized,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(filesToUnload);
		SubMonitor progress = SubMonitor.convert(monitor, getFilesToUnloadCount(filesToUnload));
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (TransactionalEditingDomain editingDomain : filesToUnload.keySet()) {
			Collection<IFile> filesToUnloadInEditingDomain = filesToUnload.get(editingDomain);
			int totalWork = filesToUnloadInEditingDomain.size();
			EcorePlatformUtil.unloadFiles(editingDomain, filesToUnloadInEditingDomain, memoryOptimized, progress.newChild(totalWork));
		}

		// Perform a full garbage collection
		ExtendedPlatform.performGarbageCollection();
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

		if (async && projects.size() > 0) {
			Job job = new Job(mmDescriptor != null ? Messages.job_reloadingModel : Messages.job_reloadingModels) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runReloadProjects(projects, includeReferencedProjects, mmDescriptor, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(projects, includeReferencedProjects));
			job.schedule();
		} else {
			try {
				runReloadProjects(projects, includeReferencedProjects, mmDescriptor, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runReloadProjects(Collection<IProject> projects, boolean includeReferencedProjects, IMetaModelDescriptor mmDescriptor,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(projects);

		for (IProject project : projects) {
			String taskName = mmDescriptor != null ? NLS.bind(Messages.task_reloadingModelInProject, mmDescriptor.getName(), project.getName()) : NLS
					.bind(Messages.task_reloadingModelsInProject, project.getName());
			SubMonitor progress = SubMonitor.convert(monitor, taskName, 100);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			// Collect files in given project and its referenced projects
			Collection<IFile> files = ExtendedPlatform.getAllFiles(project, includeReferencedProjects);
			progress.worked(1);

			// No files found?
			if (files.size() == 0) {
				progress.done();
				return;
			}

			// Reload files; perform memory-optimized unloading if given project is a root project, i.e. is not
			// referenced
			// by any other project
			runDetectAndReloadModelFiles(files, mmDescriptor, ExtendedPlatform.isRootProject(project), progress.newChild(99));
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

		if (async && files.size() > 0) {
			Job job = new Job(Messages.job_reloadingModelResources) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runDetectAndReloadModelFiles(files, mmDescriptor, memoryOptimized, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(files));
			job.schedule();
		} else {
			try {
				runDetectAndReloadModelFiles(files, mmDescriptor, memoryOptimized, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runDetectAndReloadModelFiles(Collection<IFile> files, IMetaModelDescriptor mmDescriptor, boolean memoryOptimized,
			IProgressMonitor monitor) throws OperationCanceledException {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_reloadingModelFiles, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Collection<IFile>> filesToUnload = detectFilesToUnload(files, mmDescriptor, progress.newChild(5));
		Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad = detectFilesToLoad(files, mmDescriptor, false, progress.newChild(10));

		// Nothing to reload?
		if (filesToUnload.size() == 0 && filesToLoad.size() == 0) {
			progress.done();

			// TODO Surround with appropriate tracing option
			// System.out.println("[ModelLoadManager#runDetectAndReloadModelFiles()] No model files to be reloaded");
			return;
		}

		runReloadModelFiles(filesToUnload, filesToLoad, memoryOptimized, progress.newChild(85));

		// TODO Surround with appropriate tracing option
		// System.out.println("[ModelLoadManager#runDetectAndReloadModelFiles()] Loaded " +
		// getFilesToLoadCount(filesToLoad) + " and unloaded "
		// + getFilesToUnloadCount(filesToUnload) + " model file(s)");
	}

	private void runReloadModelFiles(final Map<TransactionalEditingDomain, Collection<IFile>> filesToUnload,
			final Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad, final boolean memoryOptimized, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(filesToUnload);
		Assert.isNotNull(filesToLoad);
		final SubMonitor progress = SubMonitor.convert(monitor, getFilesToUnloadCount(filesToUnload) + getFilesToLoadCount(filesToLoad));
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Separate out editing domains with files to be unloaded only
		Map<TransactionalEditingDomain, Collection<IFile>> filesToUnloadOnly = new HashMap<TransactionalEditingDomain, Collection<IFile>>();
		for (TransactionalEditingDomain editingDomain : new HashSet<TransactionalEditingDomain>(filesToUnload.keySet())) {
			if (!filesToLoad.containsKey(editingDomain)) {
				filesToUnloadOnly.put(editingDomain, filesToUnload.get(editingDomain));
				filesToUnload.remove(editingDomain);
			}
		}

		// Separate out editing domains with files to be loaded only
		Map<TransactionalEditingDomain, Collection<IFile>> filesToLoadOnly = new HashMap<TransactionalEditingDomain, Collection<IFile>>();
		for (TransactionalEditingDomain editingDomain : new HashSet<TransactionalEditingDomain>(filesToLoad.keySet())) {
			if (!filesToUnload.containsKey(editingDomain)) {
				filesToLoadOnly.put(editingDomain, filesToLoad.get(editingDomain));
				filesToLoad.remove(editingDomain);
			}
		}

		// Process editing domains with files to be unloaded only
		if (filesToUnloadOnly.size() > 0) {
			runUnloadModelFiles(filesToUnloadOnly, memoryOptimized, progress.newChild(getFilesToUnloadCount(filesToUnloadOnly)));
		}

		// Process editing domains with files to be actually reloaded
		for (final TransactionalEditingDomain editingDomain : filesToUnload.keySet()) {
			try {
				// Create read transaction to ensure that reload procedure is atomic
				editingDomain.runExclusive(new Runnable() {
					public void run() {
						// Unload files to be unloaded from current editing domain
						Collection<IFile> filesToUnloadInEditingDomain = filesToUnload.get(editingDomain);
						int totalWork = filesToUnloadInEditingDomain.size();
						EcorePlatformUtil.unloadFiles(editingDomain, filesToUnloadInEditingDomain, memoryOptimized, progress.newChild(totalWork));

						// Load files to be loaded into current editing domain
						Collection<IFile> filesToLoadInEditingDomain = filesToLoad.get(editingDomain);
						totalWork = filesToLoadInEditingDomain.size();
						loadModelFilesInEditingDomain(editingDomain, filesToLoadInEditingDomain, progress.newChild(totalWork));
					}
				});
			} catch (InterruptedException ex) {
				PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
			}
		}

		// Process editing domains with files to be loaded only
		if (filesToLoadOnly.size() > 0) {
			runLoadModelFiles(filesToLoadOnly, progress.newChild(getFilesToLoadCount(filesToLoadOnly)));
		}

		// Perform a full garbage collection
		ExtendedPlatform.performGarbageCollection();
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

		if (async && projectsWithUnreachableCrossRefrencesToUnresolve.size() > 0) {
			Job job = new Job(Messages.job_unresolvingUnreachableCrossProjectReferences) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runUnresolveUnreachableCrossProjectReferences(projectsWithUnreachableCrossRefrencesToUnresolve, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(projectsWithUnreachableCrossRefrencesToUnresolve, false));
			job.schedule();
		} else {
			try {
				runUnresolveUnreachableCrossProjectReferences(projectsWithUnreachableCrossRefrencesToUnresolve, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runUnresolveUnreachableCrossProjectReferences(final Collection<IProject> projects, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(projects);

		for (IProject project : projects) {
			Collection<IModelDescriptor> modelsInProject = ModelDescriptorRegistry.INSTANCE.getModels(project);
			SubMonitor progress = SubMonitor.convert(monitor,
					NLS.bind(Messages.task_unresolvingUnreachableCrossProjectReferencesInProject, project.getName()), modelsInProject.size());
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}

			for (IModelDescriptor modelDescriptor : modelsInProject) {
				unresolveUnreachableCrossProjectReferencesInModel(modelDescriptor, progress.newChild(1));

				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		}
	}

	private void unresolveUnreachableCrossProjectReferencesInModel(final IModelDescriptor modelDescriptor, final IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(modelDescriptor);

		Runnable runnable = new Runnable() {
			public void run() {
				Collection<Resource> resources = modelDescriptor.getLoadedResources(true);
				SubMonitor progress = SubMonitor.convert(monitor, resources.size());
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				for (Resource resource : resources) {
					progress.subTask(NLS.bind(Messages.subtask_unresolvingUnreachableCrossProjectReferencesInResource, resource.getURI()));
					try {
						EObject modelRoot = EcoreResourceUtil.getModelRoot(resource);
						if (modelRoot != null) {
							TreeIterator<EObject> allContents = modelRoot.eAllContents();
							while (allContents.hasNext()) {
								EObject object = allContents.next();
								for (EReference reference : object.eClass().getEAllReferences()) {
									if (!reference.isContainment() && !reference.isContainer()) {
										if (reference.isMany()) {
											@SuppressWarnings("unchecked")
											EList<EObject> referencedObjects = (EList<EObject>) object.eGet(reference);
											List<EObject> safeReferencedObjects = new ArrayList<EObject>(referencedObjects);
											for (EObject referencedObject : safeReferencedObjects) {
												if (referencedObject != null && !referencedObject.eIsProxy()) {
													if (!modelDescriptor.belongsTo(referencedObject.eResource(), true)) {
														referencedObjects.remove(referencedObject);
														referencedObjects.add(EObjectUtil.createProxyFrom(referencedObject));
													}
												}
											}
										} else {
											EObject referencedObject = (EObject) object.eGet(reference);
											if (referencedObject != null && !referencedObject.eIsProxy()) {
												if (!modelDescriptor.belongsTo(referencedObject.eResource(), true)) {
													object.eSet(reference, EObjectUtil.createProxyFrom(referencedObject));
												}
											}
										}
									}
								}

								if (progress.isCanceled()) {
									throw new OperationCanceledException();
								}
							}
						}
					} catch (Exception ex) {
						PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
					}
					progress.worked(1);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}
					modelDescriptor.getEditingDomain().yield();
				}
			}
		};

		try {
			/*
			 * !! Important note !! There seems to be a highly critical bug in EMF Transaction: We have observed that
			 * when a write transaction and a read transaction are started at the same time on the same editing domain
			 * in two different threads, the editing domain is accessed concurrently but not exclusively by both
			 * transactions. As a consequence, they both try to start and shut down the editing domain's change recorder
			 * which may lead to unpredictable results ranging from unexpected rollbacks to deadlocks. As a workaround,
			 * we run the write transaction with a OPTION_NO_UNDO which avoids that the write transaction accesses the
			 * editing domain's change recorder. We would do so even in absence of this potential EMF Transaction bug
			 * because we don't want enable users to undo the unresolving of unreachable cross project references
			 * anyway.
			 */
			// FIXME File bug to EMF Transaction: Editing domain accessed concurrently when a write transaction and a
			// read transaction are started at the same time by two different threads
			IOperationHistory operationHistory = WorkspaceTransactionUtil.getOperationHistory(modelDescriptor.getEditingDomain());
			Map<String, Object> options = WorkspaceTransactionUtil.getDefaultTransactionOptions();
			options.put(Transaction.OPTION_NO_UNDO, Boolean.TRUE);
			WorkspaceTransactionUtil.executeInWriteTransaction(modelDescriptor.getEditingDomain(), runnable,
					Messages.operation_unresolvingUnreachableCrossProjectReferencesInModel, operationHistory, options, null);
		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
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
		IMetaModelDescriptor mmDescriptor = modelDescriptor.getMetaModelDescriptor();
		if (async && persistedFiles.size() > 0) {
			// Check first if job should really be created or not
			if (!LoadJob.shouldCreateJob(persistedFiles, mmDescriptor)) {
				return;
			}
			Job job = new FileLoadJob(persistedFiles, mmDescriptor) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runCollectAndLoadModelFiles(modelDescriptor.getEditingDomain(), persistedFiles, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(persistedFiles));
			job.schedule();
		} else {
			try {
				runCollectAndLoadModelFiles(modelDescriptor.getEditingDomain(), persistedFiles, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
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

	private void runCollectAndLoadModelFiles(TransactionalEditingDomain editingDomain, Collection<IFile> files, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(files);
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_loadingModelFiles, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Collect model files to load
		SubMonitor collectProgress = progress.newChild(1).setWorkRemaining(files.size());
		Map<TransactionalEditingDomain, Collection<IFile>> filesToLoad = new HashMap<TransactionalEditingDomain, Collection<IFile>>();
		for (IFile file : files) {
			try {
				// Exclude inaccessible files
				if (file.isAccessible()) {
					// Ignore files that already have been loaded
					if (!EcorePlatformUtil.isFileLoaded(file)) {
						// Retrieve already existing load file request for given editing domain
						Collection<IFile> filesToLoadInEditingDomain = filesToLoad.get(editingDomain);
						if (filesToLoadInEditingDomain == null) {
							filesToLoadInEditingDomain = new HashSet<IFile>();
							filesToLoad.put(editingDomain, filesToLoadInEditingDomain);
						}
						// Add current file to load file request
						filesToLoadInEditingDomain.add(file);
					}
				}
			} catch (Exception ex) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
			}

			collectProgress.worked(1);
			if (collectProgress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		// Nothing to load?
		if (filesToLoad.size() == 0) {
			progress.done();
			// TODO Surround with appropriate tracing option
			// System.out.println("[ModelLoadManager#runCollectAndLoadModelFiles()] No model files to be loaded");
			return;
		}
		runLoadModelFiles(filesToLoad, progress.newChild(99));
		// TODO Surround with appropriate tracing option
		// System.out.println("[ModelLoadManager#runCollectAndLoadModelFiles()] Loaded " +
		// getFilesToLoadCount(filesToLoad) + " model file(s)");
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

		if (async && filesToUpdate.size() > 0) {
			Job job = new Job(Messages.job_updatingResourceURIs) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						runDetectAndUpdateResourceURIs(filesToUpdate, monitor);
						return Status.OK_STATUS;
					} catch (OperationCanceledException ex) {
						return Status.CANCEL_STATUS;
					} catch (Exception ex) {
						return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
					}
				}

				@Override
				public boolean belongsTo(Object family) {
					return IExtendedPlatformConstants.FAMILY_MODEL_LOADING.equals(family)
							|| IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
				}
			};
			job.setPriority(Job.BUILD);
			job.setRule(createLoadSchedulingRule(filesToUpdate.keySet()));
			job.schedule();
		} else {
			try {
				runDetectAndUpdateResourceURIs(filesToUpdate, monitor);
			} catch (OperationCanceledException ex) {
				// Ignore exception
			}
		}
	}

	private void runDetectAndUpdateResourceURIs(Map<IFile, IPath> filesToUpdate, IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(filesToUpdate);
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_updatingResourceURIs, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		Map<TransactionalEditingDomain, Map<IFile, IPath>> filesToUpdateResourceURIFor = detectFilesToUpdateResourceURIFor(filesToUpdate,
				progress.newChild(10));

		// Nothing to update?
		if (filesToUpdateResourceURIFor.size() == 0) {
			progress.done();
			return;
		}

		runUpdateResourceURIs(filesToUpdateResourceURIFor, progress.newChild(90));
	}

	private void runUpdateResourceURIs(Map<TransactionalEditingDomain, Map<IFile, IPath>> filesToUpdateResourceURIFor, IProgressMonitor monitor)
			throws OperationCanceledException {
		Assert.isNotNull(filesToUpdateResourceURIFor);
		SubMonitor progress = SubMonitor.convert(monitor, filesToUpdateResourceURIFor.keySet().size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (TransactionalEditingDomain editingDomain : filesToUpdateResourceURIFor.keySet()) {
			updateResourceURIsInEditingDomain(editingDomain, filesToUpdateResourceURIFor.get(editingDomain), progress.newChild(1));
		}
	}

	private void updateResourceURIsInEditingDomain(final TransactionalEditingDomain editingDomain,
			final Map<IFile, IPath> filesToUpdateResourceURIForInEditingDomain, final IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(editingDomain);
		Assert.isNotNull(filesToUpdateResourceURIForInEditingDomain);

		try {
			editingDomain.runExclusive(new Runnable() {
				public void run() {
					SubMonitor progress = SubMonitor.convert(monitor, filesToUpdateResourceURIForInEditingDomain.size());
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}

					for (IFile oldFile : filesToUpdateResourceURIForInEditingDomain.keySet()) {
						progress.subTask(NLS.bind(Messages.subtask_updatingResourceURI, oldFile.getFullPath().toString()));

						URI oldURI = EcorePlatformUtil.createURI(oldFile.getFullPath());
						Resource resource = editingDomain.getResourceSet().getResource(oldURI, false);
						if (resource != null) {
							IPath newPath = filesToUpdateResourceURIForInEditingDomain.get(oldFile);
							URI newURI = EcorePlatformUtil.createURI(newPath);
							resource.setURI(newURI);
						}

						progress.worked(1);
						if (progress.isCanceled()) {
							throw new OperationCanceledException();
						}
						editingDomain.yield();
					}
				}
			});
		} catch (InterruptedException ex) {
			PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
		}
	}
}
