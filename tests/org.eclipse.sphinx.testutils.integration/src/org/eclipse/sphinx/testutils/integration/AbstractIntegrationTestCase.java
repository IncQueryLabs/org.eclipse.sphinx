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
package org.eclipse.sphinx.testutils.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.workspace.internal.saving.ResourceSaveIndicator;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.testutils.TestFileAccessor;
import org.eclipse.sphinx.testutils.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.internal.IInternalReferenceWorkspace;
import org.eclipse.sphinx.testutils.integration.internal.ReferenceEditingDomainDescriptor;
import org.eclipse.sphinx.testutils.integration.internal.ReferenceModelDescriptor;
import org.eclipse.sphinx.testutils.integration.internal.ReferenceProjectDescriptor;
import org.eclipse.sphinx.testutils.internal.ZipArchiveImporter;

/**
 * @param <T>
 */
@SuppressWarnings({ "nls", "restriction" })
public abstract class AbstractIntegrationTestCase<T extends IReferenceWorkspace> extends TestCase {

	private TestFileAccessor testFileAccessor = null;

	private IInternalReferenceWorkspace internalRefWks;

	protected T refWks;

	private ModelLoadingJobTracer modelLoadJobTracer = new ModelLoadingJobTracer();

	private ResourceProblemListener resourceProblemListener = new ResourceProblemListener();

	private ReferenceWorkspaceChangeListener referenceWorkspaceChangeListener = new ReferenceWorkspaceChangeListener();

	private String referenceWorkspaceSourceDirectoryPath;

	private String referenceWorkspaceSourceRootDirectoryPath;

	private final String REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_PATH = "referenceWorkspaceSourceRootDirectory.properties";

	private final String REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_KEY = "referenceWorksaceSourceDirectory";

	public AbstractIntegrationTestCase(String directoryName) {
		String tempDirectoryPath = System.getProperty("java.io.tmpdir");
		referenceWorkspaceSourceDirectoryPath = tempDirectoryPath.concat(File.separator).concat(directoryName);
		referenceWorkspaceSourceDirectoryPath = referenceWorkspaceSourceDirectoryPath.replace(File.separator + File.separator, File.separator);
		File referenceWorkspaceSourceDirectory = new File(referenceWorkspaceSourceDirectoryPath);
		if (!referenceWorkspaceSourceDirectory.exists()) {
			referenceWorkspaceSourceDirectory.mkdir();
		}
	}

	protected final TestFileAccessor getTestFileAccessor() {
		if (testFileAccessor == null) {
			testFileAccessor = new TestFileAccessor(getTestPlugin());
		}
		return testFileAccessor;
	}

	protected abstract Plugin getTestPlugin();

	protected boolean isProjectsClosedOnStartup() {
		return false;
	}

	/**
	 * Sets up the test workspace by importing the reference workspace given in parameter.
	 */
	@Override
	protected void setUp() throws Exception {
		waitForModelLoading();
		Job.getJobManager().addJobChangeListener(modelLoadJobTracer);
		// Create workspace
		initReferenceWorkspace();
		// Unzip reference of archive file. Incase it had been already unziped, read the reference workspace source root
		// directory from properties file
		if (needToUnzipArchiveFile()) {
			unzipArchiveFile(referenceWorkspaceSourceDirectoryPath);
		} else {
			referenceWorkspaceSourceRootDirectoryPath = getReferenceWorkspaceSourceRootDictionaryPath();
		}
		// Delete un-used reference projects which imported for the previous test.
		deleteUnusedReferenceProjects();

		org.eclipse.sphinx.emf.workspace.Activator.getPlugin().stopWorkspaceSynchronizing();

		importReferenceProjects();
		// In case previous test failed and tearDown() were not called, projects are still closed, open them.
		synchronizedOpenAllProjects();

		importMissingFiles();

		createInterProjectReferences();

		// Add ResourceProblemListener on all editingDomains to listening in problems when loading/unloading resources
		internalRefWks.addResourceSetProblemListener(resourceProblemListener);

		if (isProjectsClosedOnStartup()) {
			ModelLoadManager.INSTANCE.unloadWorkspace(false, null);
			synchronizedCloseAllProjects();
			waitForModelLoading();
			assertReferenceWorkspaceClosed();
		} else {

			ModelLoadManager.INSTANCE.loadWorkspace(false, null);
			waitForModelLoading();
			assertReferenceWorkspaceInitialized();
		}
		// Add ReferenceWorkspaceChangeListener to listening in workspace changes during the test.
		internalRefWks.addReferenceWorkspaceChangeListener(referenceWorkspaceChangeListener);
		org.eclipse.sphinx.emf.workspace.Activator.getPlugin().startWorkspaceSynchronizing();

	}

	/**
	 * Tears down the test workspace by deleting all projects.
	 */
	@Override
	protected void tearDown() throws Exception {

		waitForModelLoading();
		// Remove ResourceProblemListener from all editingDomains
		internalRefWks.removeResourceSetProblemListener(resourceProblemListener);
		internalRefWks.removeReferenceWorkspaceChangeListener(referenceWorkspaceChangeListener);
		// Unload resources which have been changed but not saved yet
		unloadDirtyResources();

		// Projects which have been renamed will be deleted
		deleteRenamedProjects();

		// Open projects which has been changed during the test
		synchronizedOpenProjects(detectProjectsToOpen());

		// Delete all files which have been added during the test, including renamed files
		deleteAddedFiles();

		// Delete all files which have been changed during the test
		deleteChangedFiles();

		// Unload resources are on memory only, these resources are not marked as dirty
		unloadReourcesOutsideFileDescriptor();

		// If existing Projects' description were changed, reset its by copying contents from reference file
		resetProjectsDescription();

		// If existing Projects' setting were changed, reset them by copying contents from reference file
		resetProjectsSettings();

		waitForModelLoading();

		// Clear list of resources changed
		resourceProblemListener.clearHistory();
		referenceWorkspaceChangeListener.clearHistory();

		Job.getJobManager().removeJobChangeListener(modelLoadJobTracer);

	}

	/**
	 * Delete projects in workspace which are not used in the current test
	 * 
	 * @throws Exception
	 */
	private void deleteUnusedReferenceProjects() throws Exception {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (IProject project : getUnusedReferenceProjects()) {
					try {
						synchronizedDeleteProject(project);
					} catch (Exception ex) {
						fail("Exception while deleting unused projects." + ex.getMessage());
					}
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, null);
		waitForModelLoading();

	}

	/**
	 * Detect projects that have resources changed during the test. If projects were closed in the test, we need to open
	 * it to delete changed files or reset project description and settings
	 * 
	 * @return
	 */
	private Collection<IProject> detectProjectsToOpen() {
		Set<IProject> projectsToOpen = new HashSet<IProject>();
		if (!(referenceWorkspaceChangeListener.getAddedFiles().size() > 0 || referenceWorkspaceChangeListener.getChangedFiles().size() > 0
				|| referenceWorkspaceChangeListener.getChangedDescriptionProjects().size() > 0 || referenceWorkspaceChangeListener
				.getChangedSettingProjects().size() > 0)) {
			return Collections.emptySet();
		}
		for (IFile changedFile : referenceWorkspaceChangeListener.getChangedFiles()) {
			assertNotNull(changedFile);
			IProject project = changedFile.getProject();
			assertNotNull(project);
			if (project.exists() && !project.isOpen()) {
				projectsToOpen.add(project);
			}
		}
		for (IFile addedFile : referenceWorkspaceChangeListener.getAddedFiles()) {
			assertNotNull(addedFile);
			IProject project = addedFile.getProject();
			if (project.exists() && !project.isOpen()) {
				projectsToOpen.add(project);
			}
		}
		for (String projectName : referenceWorkspaceChangeListener.getChangedDescriptionProjects()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (!(project == null || project.exists() || project.isOpen())) {
				projectsToOpen.add(project);
			}
		}
		for (String projectName : referenceWorkspaceChangeListener.getChangedSettingProjects().keySet()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (!(project == null || project.exists() || project.isOpen())) {
				projectsToOpen.add(project);
			}
		}
		return projectsToOpen;

	}

	/**
	 * Unload all resources created in the previous test. These resources were added to ResourceSets but were not saved
	 * and not marked as dirty. So we need to detect and unload them
	 */
	private void unloadReourcesOutsideFileDescriptor() {
		List<Resource> resourcesToUnload = new ArrayList<Resource>();
		for (IMetaModelDescriptor metaModelDescriptor : internalRefWks.getReferenceEditingDomainDescritpors().keySet()) {
			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(),
					metaModelDescriptor);
			if (editingDomain != null) {
				Set<String> referenceFileNames = internalRefWks.getReferenceFileNames(metaModelDescriptor);
				for (Resource resource : editingDomain.getResourceSet().getResources()) {
					if (!referenceFileNames.contains(resource.getURI().lastSegment())) {
						resourcesToUnload.add(resource);
					}
				}
			}
			EcorePlatformUtil.unloadResources(editingDomain, resourcesToUnload, true, null);
			waitForModelLoading();
		}

	}

	/**
	 * Delete all projects which were renamed during a test
	 * 
	 * @throws Exception
	 */
	private void deleteRenamedProjects() throws Exception {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (String projectName : referenceWorkspaceChangeListener.getRenamedProjects()) {
					IProject project = EcorePlugin.getWorkspaceRoot().getProject(projectName);
					if (project.exists()) {
						try {
							synchronizedDeleteProject(project);
						} catch (Exception ex) {
							fail("Exception while deleting renamed project(s):" + ex.getMessage());
						}
					}
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, null);
		waitForModelLoading();

	}

	/**
	 * Delete files which has been changed in the test. These files will be imported again in setUp() of next test if
	 * they are required for the test.
	 * 
	 * @throws Exception
	 */
	private void deleteChangedFiles() throws Exception {
		for (IFile changedFile : referenceWorkspaceChangeListener.getChangedFiles()) {
			if (changedFile != null && changedFile.isAccessible()) {
				synchronizedDeleteFile(changedFile);
			}
		}
	}

	/**
	 * Delete all new files which have been added during the test.
	 * 
	 * @throws Exception
	 */
	private void deleteAddedFiles() throws Exception {
		for (IFile addedFile : referenceWorkspaceChangeListener.getAddedFiles()) {
			if (refWks.getAllReferenceFiles().contains(addedFile)) {
				continue;
			} else if (addedFile != null && addedFile.isAccessible()) {
				synchronizedDeleteFile(addedFile);
			}
		}
	}

	/**
	 * Unload all dirty resources in editingDomains
	 */
	private void unloadDirtyResources() {
		for (IMetaModelDescriptor metaModelDescriptor : internalRefWks.getReferenceEditingDomainDescritpors().keySet()) {
			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(),
					metaModelDescriptor);
			if (editingDomain != null) {
				ResourceSaveIndicator resourceSaveIndicator = new ResourceSaveIndicator(editingDomain);
				Collection<Resource> dirtyResources = resourceSaveIndicator.getDirtyResources();
				EcorePlatformUtil.unloadResources(editingDomain, dirtyResources, true, null);
			}
		}

	}

	/**
	 * Reset description of projects which have been changed during the test by copying .project file from source
	 * reference file
	 */
	private void resetProjectsDescription() {
		for (String projectName : referenceWorkspaceChangeListener.getChangedDescriptionProjects()) {
			IProject project = EcorePlugin.getWorkspaceRoot().getProject(projectName);
			if (project.exists()) {
				assertTrue(project.isOpen());
				copyProjectDecsription(projectName, project.getFullPath());
			}
		}
	}

	/**
	 * Reset settings of projects which have been changed during the test by copying .settings folder from source
	 * reference directory
	 */
	private void resetProjectsSettings() {
		for (String projectName : referenceWorkspaceChangeListener.getChangedSettingProjects().keySet()) {
			IProject prj = EcorePlugin.getWorkspaceRoot().getProject(projectName);
			if (prj.exists()) {
				assertTrue(prj.isOpen());
				copyProjectSettings(projectName, prj.getFullPath());
			}
		}
	}

	/**
	 * Check if it is necessary to unzip reference archive file
	 * 
	 * @return <code><b>true</b></code> If the reference archive file was already unziped and reference workspace root
	 *         directory's path were saved to readMe.properties. Otherwise, return <code><b>false</b></code>
	 */
	boolean needToUnzipArchiveFile() {
		// FIXME Don't check for non-existence only but also if reference workspace zip archive is newer than extracted
		// reference workspace
		File refernceDir = new File(referenceWorkspaceSourceDirectoryPath.toString());
		File readMeFile = new File(referenceWorkspaceSourceDirectoryPath.toString() + File.separator + REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_PATH);
		return !(refernceDir.exists() && readMeFile.exists());

	}

	/**
	 * Save the reference workspace source root directory path to read
	 * 
	 * @param referenceWorkspaceSourceDirectoryRoot
	 *            the
	 */
	private void saveReferenceWorkspaceSourceRootDirectoryPath(String referenceWorkspaceSourceDirectoryRoot) {
		Properties properties = new Properties();
		properties.put(REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_KEY, referenceWorkspaceSourceDirectoryRoot);
		try {

			File proFile = new File(referenceWorkspaceSourceDirectoryPath + File.separator + REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_PATH);
			if (!proFile.exists()) {
				proFile.createNewFile();
			}

			OutputStream outPropFile = new FileOutputStream(proFile);
			properties.store(outPropFile, "Reference File Path Dictionary");
			outPropFile.close();
		} catch (IOException ex) {
			fail("Cannot save source directory path to file at:" + referenceWorkspaceSourceDirectoryRoot + ":" + ex.getMessage());
		}
	}

	private String getReferenceWorkspaceSourceRootDictionaryPath() throws FileNotFoundException, IOException {

		Properties properties = new Properties();
		// open stream of readMe.properties
		InputStream filePathDictionaryInputStream = openFileInputStream(referenceWorkspaceSourceDirectoryPath.toString() + File.separator
				+ REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_PATH);
		assertNotNull(filePathDictionaryInputStream);
		properties.load(filePathDictionaryInputStream);
		return properties.getProperty(REFERENCE_WORKSPACE_SOURCE_ROOT_DIRECTORY_KEY);

	}

	private void initReferenceWorkspace() {
		final String[] referenceProjectNames = getProjectsToLoad();
		refWks = createReferenceWorkspace(referenceProjectNames);
		internalRefWks = (IInternalReferenceWorkspace) refWks;

		waitForModelLoading();
	}

	protected void importReferenceProjects() throws CoreException {

		Set<IProject> missingProjects = getMissingProjects();
		importProjectsToWorkspace(missingProjects);

	}

	private Set<IProject> getMissingProjects() {
		Set<IProject> missingReferenceProjects = new HashSet<IProject>();
		for (ReferenceProjectDescriptor descriptor : internalRefWks.getReferenceProjectDescriptors()) {
			if (!descriptor.getProject().exists()) {
				missingReferenceProjects.add(descriptor.getProject());
			}
		}
		return missingReferenceProjects;
	}

	protected Set<IProject> getAllReferenceProjects() {
		Set<IProject> referenceProjects = new HashSet<IProject>();
		for (ReferenceProjectDescriptor descriptor : internalRefWks.getReferenceProjectDescriptors()) {
			referenceProjects.add(descriptor.getProject());
		}
		return referenceProjects;
	}

	private Set<IProject> getUnusedReferenceProjects() {
		IProject[] projects = EcorePlugin.getWorkspaceRoot().getProjects();
		Set<IProject> unusedReferenceProjects = new HashSet<IProject>();
		if (getProjectsToLoad() == null) {
			return Collections.emptySet();
		}
		for (IProject project : projects) {
			boolean used = false;
			// Import all projects

			for (String prjName : getProjectsToLoad()) {
				if (prjName.equals(project.getName())) {
					used = true;
				}
			}
			if (!used) {
				unusedReferenceProjects.add(project);
			}
		}

		return unusedReferenceProjects;
	}

	private Set<IFile> getMissingFile() {
		Set<IFile> missingReferenceFiles = new HashSet<IFile>();
		for (IFile file : refWks.getAllReferenceFiles()) {
			if (!file.exists()) {
				missingReferenceFiles.add(file);
			}
		}
		return missingReferenceFiles;
	}

	protected void importFilesToWorkspace(Set<IFile> missingFiles) throws CoreException {
		for (IFile missingFile : missingFiles) {
			IPath filePath = missingFile.getFullPath();
			IProject project = missingFile.getProject();
			if (!project.exists()) {
				project.create(new NullProgressMonitor());
				project.open(new NullProgressMonitor());
			} else if (!project.isAccessible()) {
				project.open(new NullProgressMonitor());
			}
			importDirectoryToWorkspace(getFullPathOfReferenceSourceFile(filePath.toString()), filePath);
		}
	}

	private void importMissingFiles() throws CoreException {
		Set<IFile> missingReferenceFiles = getMissingFile();
		importFilesToWorkspace(missingReferenceFiles);
	}

	private void unzipArchiveFile(final String targetLocation) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				ZipArchiveImporter zipArchiveImpoter = new ZipArchiveImporter();
				zipArchiveImpoter.unZipArchiveFile(new TestFileAccessor(internalRefWks.getReferenceWorkspacePlugin()),
						internalRefWks.getReferenceWorkspaceArchiveFileName(), targetLocation);
				referenceWorkspaceSourceRootDirectoryPath = zipArchiveImpoter.getDirectoryRoot();
				saveReferenceWorkspaceSourceRootDirectoryPath(zipArchiveImpoter.getDirectoryRoot());

			}

		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, null);

	}

	protected String[] getProjectsToLoad() {
		return null;
	}

	private T createReferenceWorkspace(String[] referenceProjectNames) {
		T referenceWorkspace = doCreateReferenceWorkspace(referenceProjectNames);
		Assert.isTrue(referenceWorkspace instanceof IInternalReferenceWorkspace);
		return referenceWorkspace;
	}

	protected abstract T doCreateReferenceWorkspace(String[] referenceProjectNames);

	/**
	 * Setting project description will reload the project
	 */
	private void createInterProjectReferences() throws CoreException {
		for (String[] projectPair : getProjectReferences()) {
			if (projectPair != null) {
				if (projectPair.length >= 2) {
					IProject parent = refWks.getReferenceProject(projectPair[0]);
					IProject child = refWks.getReferenceProject(projectPair[1]);
					if (parent != null && parent.isAccessible() && child != null && child.isAccessible()) {
						// Add 'child' project in list of projects referenced by
						// 'parent' project
						IProjectDescription parentDescription = parent.getDescription();
						parentDescription.setReferencedProjects(new IProject[] { child });
						parent.setDescription(parentDescription, null);
					}
				}
			}
		}
	}

	protected String[][] getProjectReferences() {
		return new String[0][0];
	}

	private void assertReferenceWorkspaceInitialized() throws Exception {
		assertExpectedReferenceProjectsExist();
		assertExpectedReferenceFilesExist();
		assertExpectedReferenceModelResourcesLoaded();
		assertExpectedReferenceModelDescriptorsExist();
	}

	private void assertExpectedReferenceProjectsExist() {
		Set<IProject> missingReferenceProjects = getMissingProjects();

		if (missingReferenceProjects.size() > 0) {
			System.err.println("Missing reference project(s):");
			for (IProject project : missingReferenceProjects) {
				System.err.println("  " + project.getFullPath());
			}
		}
		assertEquals("Missing reference project(s).", 0, missingReferenceProjects.size());

	}

	private void assertExpectedReferenceFilesExist() {
		Set<IFile> missingReferenceFiles = getMissingFile();
		if (missingReferenceFiles.size() > 0) {
			System.err.println("Missing reference file(s):");
			for (IFile file : missingReferenceFiles) {
				System.err.println("  " + file.getFullPath());
			}
		}
		assertEquals("Missing reference file(s).", 0, missingReferenceFiles.size());
	}

	private void assertExpectedReferenceModelResourcesLoaded() {
		Map<TransactionalEditingDomain, Set<URI>> missingReferenceModelResources = new HashMap<TransactionalEditingDomain, Set<URI>>();

		for (IMetaModelDescriptor metaModelDescriptor : internalRefWks.getReferenceEditingDomainDescritpors().keySet()) {
			ReferenceEditingDomainDescriptor editingDomainDescriptor = internalRefWks.getReferenceEditingDomainDescritpors().get(metaModelDescriptor);

			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(),
					metaModelDescriptor);
			assertNotNull(editingDomain);
			Set<URI> actualResourceURIs = new HashSet<URI>();
			for (Resource resource : editingDomain.getResourceSet().getResources()) {
				actualResourceURIs.add(resource.getURI());
			}

			Set<URI> missingResourceURIs = new HashSet<URI>(editingDomainDescriptor.getResourceURIs());
			missingResourceURIs.removeAll(actualResourceURIs);
			if (missingResourceURIs.size() > 0) {
				missingReferenceModelResources.put(editingDomain, missingResourceURIs);
			}
		}

		if (missingReferenceModelResources.size() > 0) {
			for (TransactionalEditingDomain editingDomain : missingReferenceModelResources.keySet()) {
				System.err.println("Missing model resource(s) in editing domain '" + editingDomain.getID() + "':");
				Set<URI> missingResourceURIs = missingReferenceModelResources.get(editingDomain);

				for (URI uri : missingResourceURIs) {
					System.err.println("  " + uri.toString());
				}
				for (Resource errorResource : resourceProblemListener.getErrorResources()) {
					if (missingResourceURIs.contains(errorResource.getURI())) {
						if (errorResource.getErrors().get(0) instanceof Exception) {
							throw new WrappedException((Exception) errorResource.getErrors().get(0));
						} else {
							throw new RuntimeException(errorResource.getErrors().get(0).getMessage());
						}
					}

				}
				assertEquals("Missing model resource(s) in editing domain '" + editingDomain.getID() + "'. " + missingResourceURIs.toString(), 0,
						missingResourceURIs.size());
			}
		}
	}

	private void assertExpectedReferenceModelDescriptorsExist() {
		Map<IProject, Set<ReferenceModelDescriptor>> missingModelDescriptors = new HashMap<IProject, Set<ReferenceModelDescriptor>>();

		for (ReferenceProjectDescriptor referenceProjectDescriptor : internalRefWks.getReferenceProjectDescriptors()) {
			Set<ReferenceModelDescriptor> missingModelDescriptorsInProject = new HashSet<ReferenceModelDescriptor>();
			missingModelDescriptorsInProject.addAll(referenceProjectDescriptor.getReferenceModelDescriptors());

			for (ReferenceModelDescriptor referenceModelDescriptor : referenceProjectDescriptor.getReferenceModelDescriptors()) {
				for (IModelDescriptor modelDescriptor : ModelDescriptorRegistry.INSTANCE.getModels(referenceProjectDescriptor.getProject())) {
					if (referenceModelDescriptor.getMetaModelDescriptor().equals(modelDescriptor.getMetaModelDescriptor())
							&& referenceModelDescriptor.getEditingDomainName().equals(modelDescriptor.getEditingDomain().getID())
							&& referenceModelDescriptor.getRootProject().equals(modelDescriptor.getScope().getRoot())) {
						missingModelDescriptorsInProject.remove(referenceModelDescriptor);
						break;
					}
				}
			}

			if (missingModelDescriptorsInProject.size() > 0) {
				missingModelDescriptors.put(referenceProjectDescriptor.getProject(), missingModelDescriptorsInProject);
			}
		}

		if (missingModelDescriptors.size() > 0) {
			for (IProject project : missingModelDescriptors.keySet()) {
				System.err.println("Missing model(s) in project '" + project.getName() + "':");
				Set<ReferenceModelDescriptor> missingModelDescriptorsInProject = missingModelDescriptors.get(project);
				for (ReferenceModelDescriptor referenceModelDescriptor : missingModelDescriptorsInProject) {
					System.err.println("  " + referenceModelDescriptor);
				}
				assertEquals("Missing model(s) in project '" + project.getName() + "'.", 0, missingModelDescriptorsInProject.size());
			}
		}
	}

	private void assertReferenceWorkspaceClosed() {
		assertAllModelDescriptorsRemoved();
		assertAllProjectsClosed();
		assertAllModelResourcesUnloaded();
	}

	private void assertAllModelDescriptorsRemoved() {
		assertWorkspaceModelsSizeEquals(0);
	}

	private void assertAllModelResourcesUnloaded() {
		for (TransactionalEditingDomain editingDomain : WorkspaceEditingDomainUtil.getAllEditingDomains()) {
			assertEditingDomainResourcesSizeEquals(editingDomain, 0);
		}
	}

	private void assertAllProjectsClosed() {
		Set<IProject> openProjects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.isOpen()) {
				openProjects.add(project);
			}
		}
		if (openProjects.size() > 0) {
			System.err.println("Unexpected number of open project(s):");
			for (IProject project : openProjects) {
				System.err.println("  " + project.getFullPath());
			}
		}
		assertTrue("Unexpected number of open project(s).", openProjects.size() == 0);
	}

	protected void assertEditingDomainResourcesSizeEquals(TransactionalEditingDomain editingDomain, int expected) {
		assertNotNull(editingDomain);

		EList<Resource> resources = editingDomain.getResourceSet().getResources();
		if (expected == 0 && resources.size() > 0) {
			System.err.println("Unexpected number of resources in editingDomain " + editingDomain.getID() + ":");
			for (Resource resource : resources) {
				System.err.println("  " + resource.getURI());
			}
		}
		assertEquals("Unexpected number of resources in editingDomain " + editingDomain.getID(), expected, resources.size());
	}

	protected void assertEditingDomainDoesNotContainResource(TransactionalEditingDomain editingDomain, Resource resource) {
		assertNotNull(editingDomain);
		assertFalse(editingDomain.getResourceSet().getResources().contains(resource));
	}

	protected void assertEditingDomainContainsResource(TransactionalEditingDomain editingDomain, Resource resource) {
		assertNotNull(editingDomain);
		assertTrue(editingDomain.getResourceSet().getResources().contains(resource));
	}

	protected void assertEditingDomainContainsResources(TransactionalEditingDomain editingDomain, Collection<Resource> resources) {
		assertNotNull(resources);
		for (Resource resource : resources) {
			assertEditingDomainContainsResource(editingDomain, resource);
		}
	}

	protected void assertEditingDomainDoesNotContainResources(TransactionalEditingDomain editingDomain, Collection<Resource> resources) {
		if (resources != null && resources.size() > 0) {
			for (Resource resource : resources) {
				assertEditingDomainDoesNotContainResource(editingDomain, resource);
			}
		}
	}

	// FIXME Consider to rely on URI rather than just resource name
	protected void assertEditingDomainContainsResource(TransactionalEditingDomain editingDomain, String resourceName) {
		assertNotNull(editingDomain);
		assertNotNull(resourceName);
		for (Resource res : editingDomain.getResourceSet().getResources()) {
			if (resourceName.equals(res.getURI().lastSegment())) {
				assertTrue(true);
				return;
			}

		}
		assertTrue("Editing domain " + editingDomain.getID() + " does not contain resource named '" + resourceName + "'.", false);
	}

	// FIXME Consider to rely on URI rather than just resource name
	protected void assertEditingDomainDoesNotContainResource(TransactionalEditingDomain editingDomain, String resourceName) {
		if (resourceName == null || editingDomain == null) {
			assertTrue(true);
			return;
		}
		for (Resource res : editingDomain.getResourceSet().getResources()) {
			if (resourceName.equals(res.getURI().lastSegment())) {
				assertTrue(false);
			}

		}
		assertTrue(true);
	}

	// FIXME Consider to rely on URI rather than just resource name
	protected void assertEditingDomainContainsNamedResources(TransactionalEditingDomain editingDomain, Collection<String> resourceNames) {
		assertNotNull(resourceNames);
		if (resourceNames.size() == 0) {
			assertTrue(false);
			return;
		}
		for (String resourceName : resourceNames) {
			assertEditingDomainContainsResource(editingDomain, resourceName);
		}

	}

	// FIXME Consider to rely on URI rather than just resource name
	protected void assertEditingDomainDoesNotContainNamedResources(TransactionalEditingDomain editingDomain, Collection<String> resourceNames) {
		if (resourceNames == null) {
			assertTrue(true);
			return;
		}
		if (resourceNames.size() == 0) {
			assertTrue(true);
			return;
		}
		for (String resourceName : resourceNames) {
			assertEditingDomainDoesNotContainResource(editingDomain, resourceName);
		}
	}

	// FIXME Consider to rely on IProject rather than just project name
	protected void assertReferenceProjectAllResourcesLoaded(String projectName) {
		ReferenceProjectDescriptor referenceProjectDescriptor = internalRefWks.getReferenceProjectDescriptor(projectName);
		assertNotNull("Project " + projectName + " does not exist in test reference workspace", referenceProjectDescriptor);
		Map<IMetaModelDescriptor, Set<IFile>> files = referenceProjectDescriptor.getFiles();
		if (files != null) {
			for (IMetaModelDescriptor metaModelDescriptor : files.keySet()) {
				if (metaModelDescriptor != MetaModelDescriptorRegistry.NO_MM) {
					assertReferenceProjectResourcesLoaded(metaModelDescriptor, projectName);
				}
			}
		}

	}

	// FIXME Consider to rely on IProject rather than just project name
	protected void assertReferenceProjectAllResourcesNotLoaded(String projectName) {
		ReferenceProjectDescriptor referenceProjectDescriptor = internalRefWks.getReferenceProjectDescriptor(projectName);
		assertNotNull("Project " + projectName + " does not exist in test reference workspace", referenceProjectDescriptor);
		Map<IMetaModelDescriptor, Set<IFile>> files = referenceProjectDescriptor.getFiles();
		if (files != null) {
			for (IMetaModelDescriptor metaModelDescriptor : files.keySet()) {
				if (metaModelDescriptor != MetaModelDescriptorRegistry.NO_MM) {
					assertReferenceProjectResourcesNotLoaded(metaModelDescriptor, projectName);
				}
			}
		}

	}

	// FIXME Consider to rely on IProject rather than just project name
	protected void assertReferenceProjectResourcesLoaded(IMetaModelDescriptor metaModelDescriptor, String projectName) {
		ReferenceProjectDescriptor referenceProjectDescriptor = internalRefWks.getReferenceProjectDescriptor(projectName);
		assertNotNull("Project " + projectName + " does not exist in test reference workspace", referenceProjectDescriptor);
		assertTrue(referenceProjectDescriptor.getProject().exists());
		List<String> filesNames = referenceProjectDescriptor.getFileNames(metaModelDescriptor);
		if (internalRefWks.getReferenceEditingDomainDescriptor(metaModelDescriptor) == null) {
			assertTrue("No such resources in editing domain descriptor", false);
		}
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(),
				metaModelDescriptor);
		assertNotNull(editingDomain);
		assertEditingDomainContainsNamedResources(editingDomain, filesNames);

	}

	// FIXME Consider to rely on IProject rather than just project name
	protected void assertReferenceProjectResourcesNotLoaded(IMetaModelDescriptor metaModelDescriptor, String projectName) {
		ReferenceProjectDescriptor referenceProjectDescriptor = internalRefWks.getReferenceProjectDescriptor(projectName);
		assertNotNull(referenceProjectDescriptor);
		List<String> filesNames = referenceProjectDescriptor.getFileNames(metaModelDescriptor);
		if (internalRefWks.getReferenceEditingDomainDescriptor(metaModelDescriptor) == null) {
			assertTrue(true);
			return;
		}
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(ResourcesPlugin.getWorkspace().getRoot(),
				metaModelDescriptor);
		assertNotNull(editingDomain);
		assertEditingDomainDoesNotContainNamedResources(editingDomain, filesNames);
	}

	protected void assertWorkspaceModelsSizeEquals(int expected) {
		Collection<IModelDescriptor> modelDescriptors = ModelDescriptorRegistry.INSTANCE.getModels(ResourcesPlugin.getWorkspace().getRoot());
		if (expected == 0 && modelDescriptors.size() > 0) {
			System.err.println("Unexpected number of model descriptor(s) in workspace:");
			for (IModelDescriptor modelDescriptor : modelDescriptors) {
				System.err.println("  " + modelDescriptor);
			}
		}
		assertEquals("Unexpected number of model descriptors in workspace.", expected, modelDescriptors.size());
	}

	protected void assertProjectModelsSizeEquals(IProject project, int expected) {
		Collection<IModelDescriptor> modelDescriptors = ModelDescriptorRegistry.INSTANCE.getModels(project);
		if (expected == 0 && modelDescriptors.size() > 0) {
			System.err.println("Unexpected number of model descriptor(s) in project '" + project.getName() + "':");
			for (IModelDescriptor modelDescriptor : modelDescriptors) {
				System.err.println("  " + modelDescriptor);
			}
		}
		assertEquals("Unexpected number of model descriptor(s) in project '" + project.getName() + "'.", expected, modelDescriptors.size());
	}

	protected void assertProjectHasModels(IProject project, IMetaModelDescriptor metaModelDescriptor) {
		Collection<IModelDescriptor> models = ModelDescriptorRegistry.INSTANCE.getModels(project, metaModelDescriptor);
		assertTrue("Missing '" + metaModelDescriptor + "' model descriptor in project '" + project.getName() + "'.", !models.isEmpty());
	}

	protected void assertProjectHasNoModels(IProject project, IMetaModelDescriptor metaModelDescriptor) {
		Collection<IModelDescriptor> models = ModelDescriptorRegistry.INSTANCE.getModels(project, metaModelDescriptor);
		assertTrue("Unexpected '" + metaModelDescriptor + "' model descriptor in project '" + project.getName() + "'.", models.isEmpty());
	}

	protected void synchronizedLoadFile(IFile file) {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.loadFile(file, false, null);
		waitForModelLoading();
	}

	protected void synchronizedUnloadFile(IFile file) {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.unloadFile(file, false, null);
		waitForModelLoading();
	}

	protected void synchronizedDeleteFile(final IFile file) throws Exception {
		assertNotNull(file);

		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				file.delete(true, true, monitor);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedMoveFile(final IFile file, final IPath target) throws CoreException {
		assertNotNull(file);
		assertNotNull(target);
		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				file.move(target, true, new NullProgressMonitor());
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();

	}

	protected void synchronizedRenameFile(final IFile file, final String newname) throws CoreException {
		assertNotNull(file);
		assertNotNull(newname);
		assertFalse(newname.length() == 0);
		waitForModelLoading();

		final IPath newPath = file.getFullPath().removeLastSegments(1).append(newname);
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				file.move(newPath, true, new NullProgressMonitor());
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();

	}

	protected void deleteExternalFile(String path) {
		URI uri = URI.createURI(path, true);
		URI fileURI = EcoreResourceUtil.convertToAbsoluteFileURI(uri);
		File file = new File(fileURI.toFileString());
		if (file.exists()) {
			if (!file.delete()) {
				throw new RuntimeException("Unable to delete external file: " + path);
			}
		}
	}

	protected void synchronizedLoadProject(IProject project, boolean includeReferencedProjects) {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.loadProject(project, includeReferencedProjects, false, null);
		waitForModelLoading();

	}

	protected void synchronizedLoadAllProjects() {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.loadProjects(Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects()), false, false, null);
		waitForModelLoading();

	}

	protected void synchronizedUnloadProject(IProject project, boolean includeReferencedProjects) {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.unloadProject(project, includeReferencedProjects, false, null);
		waitForModelLoading();

	}

	protected void synchronizedUnloadProjects(Collection<IProject> projectsToUnload, boolean includeReferenceProjects) {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.unloadProjects(projectsToUnload, includeReferenceProjects, false, null);
		waitForModelLoading();

	}

	protected void synchronizedUnloadAllProjects() {
		waitForModelLoading();
		ModelLoadManager.INSTANCE.unloadProjects(Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects()), false, false, null);
		waitForModelLoading();

	}

	protected void synchronizedOpenProject(final IProject project) throws Exception {
		assertNotNull(project);

		waitForModelLoading();
		assertTrue(Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING).length == 0);

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.open(monitor);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedOpenProjects(final Collection<IProject> projects) throws Exception {
		assertNotNull(projects);

		waitForModelLoading();
		assertTrue(Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING).length == 0);

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (IProject project : projects) {
					project.open(monitor);
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedOpenAllProjects() throws Exception {
		waitForModelLoading();
		assertTrue(Job.getJobManager().find(IExtendedPlatformConstants.FAMILY_MODEL_LOADING).length == 0);
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				MultiStatus errorStatus = new MultiStatus(Activator.getPlugin().getSymbolicName(), IStatus.ERROR,
						"Problems encountered while opening projects.", new RuntimeException());
				Collection<IProject> safeProjects = new ArrayList<IProject>(Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects()));
				SubMonitor progress = SubMonitor.convert(monitor, safeProjects.size());
				for (IProject project : safeProjects) {
					try {
						project.open(progress.newChild(1));
					} catch (CoreException ex) {
						errorStatus.add(ex.getStatus());
					}
				}
				if (errorStatus.getChildren().length > 0) {
					throw new CoreException(errorStatus);
				}
				waitForModelLoading();
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedRenameProject(final IProject project, final String newName) throws Exception {
		assertNotNull(project);

		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.move(project.getFullPath().removeLastSegments(1).append(newName), true, monitor);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedCloseProject(final IProject project) throws Exception {
		assertNotNull(project);
		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.close(monitor);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedCloseAllProjects() throws Exception {
		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				MultiStatus errorStatus = new MultiStatus(Activator.getPlugin().getSymbolicName(), IStatus.ERROR,
						"Problems encountered while closing projects.", new RuntimeException());
				Collection<IProject> safeProjects = new ArrayList<IProject>(Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects()));
				SubMonitor progress = SubMonitor.convert(monitor, safeProjects.size());
				for (IProject project : safeProjects) {
					try {
						project.close(progress.newChild(1));
					} catch (CoreException ex) {
						errorStatus.add(ex.getStatus());
					}
				}
				if (errorStatus.getChildren().length > 0) {
					throw new CoreException(errorStatus);
				}
				waitForModelLoading();
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	protected void synchronizedDeleteProject(final IProject project) throws Exception {
		assertNotNull(project);

		waitForModelLoading();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				safeDeleteProject(project, monitor);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		waitForModelLoading();
	}

	private void safeDeleteProject(IProject project, IProgressMonitor monitor) throws CoreException {
		/*
		 * !! Important Note !! There may be processes in different threads that periodically lock files in the project
		 * under deletion (e.g. Java Indexing). This lets project deletions sporadically end up in CoreExceptions with
		 * the message "Problems encountered while deleting files.". Since it's just a test, we simply try to repeat the
		 * project deletion a couple times with a random wait of a second or two in between attempts. This should
		 * normally reduce the rate of failures down to "good enough".
		 */
		for (int i = 0; i < 5; i++) {
			try {
				project.delete(true, true, monitor);
				return;
			} catch (CoreException ex) {
				if (i == 4) {
					throw ex;
				} else {
					try {
						Thread.sleep(500 + Math.round(1500 * Math.random()));
					} catch (InterruptedException iex) {
						// Ignore exception
					}
				}
			}
		}
	}

	protected void synchronizedDeleteWorkspace() throws CoreException {
		waitForModelLoading();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				workspace.getRoot().delete(true, true, null);
			}
		};
		workspace.run(runnable, workspace.getRoot(), IWorkspace.AVOID_UPDATE, null);
		waitForModelLoading();
	}

	protected Resource getProjectResource(IProject project, String fileName) {
		assertNotNull(project);
		assertNotNull(fileName);
		IFile file = project.getFile(fileName);
		return EcorePlatformUtil.getResource(file);
	}

	protected Collection<IFile> getAllNonDotFiles() {
		Collection<IFile> allFiles = new HashSet<IFile>();
		for (IProject project : ExtendedPlatform.getRootProjects()) {
			for (IFile file : ExtendedPlatform.getAllFiles(project, true)) {
				if (!file.getName().startsWith(".") && !hasDotParent(file)) {
					allFiles.add(file);
				}
			}
		}
		return allFiles;
	}

	private boolean hasDotParent(IResource resource) {
		assertNotNull(resource);
		IContainer parent = resource.getParent();
		if (parent != null) {
			if (parent.getName().startsWith(".")) {
				return true;
			}
			return hasDotParent(parent);
		}
		return false;
	}

	protected void assertProxiesResolved(Resource inputResource) {
		assertNotNull(inputResource);
		for (TreeIterator<EObject> allContents = inputResource.getAllContents(); allContents.hasNext();) {
			EObject object = allContents.next();
			assertFalse(object.eIsProxy());
		}
	}

	/**
	 * Blocks the calling thread until model loading completes.
	 */
	protected void waitForModelLoading() {
		try {
			Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, null);
		} catch (Exception ex) {
			// Ignore exception
		}
	}

	/**
	 * Blocks the calling thread until automatic build completes.
	 */
	protected void waitForAutoBuild() {
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (Exception ex) {
			// Ignore exception
		}
	}

	/**
	 * Schedule the job for execution and wait until the execution is done. Return the status returned by the job
	 * 
	 * @param job
	 * @return
	 * @throws TimeoutException
	 * @throws BrokenBarrierException
	 * @throws InterruptedException
	 */
	protected IStatus scheduleAndWait(Job job) throws Exception {
		final CyclicBarrier barrier = new CyclicBarrier(2);
		final IStatus result[] = new IStatus[1];
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				result[0] = event.getResult();
				try {
					barrier.await(10, TimeUnit.SECONDS);
				} catch (Exception ex) {
					// Ignore exception
				}
			}
		});
		job.schedule();

		// Wait for job to finish
		try {
			barrier.await(10, TimeUnit.SECONDS);
		} catch (Exception ex) {
			// Interrupt the job if timeout has elapsed or some other exception
			// has occurred
			job.getThread().interrupt();
			throw ex;
		}

		return result[0];
	}

	/**
	 * Fail the testcase and use the Throwable as the cause
	 * 
	 * @param throwable
	 */
	protected void fail(Throwable throwable) {
		AssertionFailedError error = new AssertionFailedError();
		error.initCause(throwable);
		throw error;
	}

	private class ModelLoadingJobTracer extends JobChangeAdapter {

		private boolean isModelLoadingJob(IJobChangeEvent event) {
			if (event != null) {
				Job job = event.getJob();
				if (job != null) {
					return job.belongsTo(IExtendedPlatformConstants.FAMILY_MODEL_LOADING);
				}
			}
			return false;
		}

		@Override
		public void scheduled(IJobChangeEvent event) {
			if (isModelLoadingJob(event)) {
				// TODO Surround with appropriate tracing option
				// System.out.println("Model loading job '" +
				// event.getJob().getName() + "' -> SCHEDULED");
			}
		}

		@Override
		public void running(IJobChangeEvent event) {
			if (isModelLoadingJob(event)) {
				// TODO Surround with appropriate tracing option
				// System.out.println("Model loading job '" +
				// event.getJob().getName() + "' -> RUNNING");
			}
		}

		@Override
		public void done(IJobChangeEvent event) {
			if (isModelLoadingJob(event)) {
				// TODO Surround with appropriate tracing option
				// System.out.println("Model loading job '" +
				// event.getJob().getName() + "' -> DONE");
			}
		}
	}

	/**
	 * Get input stream from of a file with given location
	 * 
	 * @throws FileNotFoundException
	 */
	protected InputStream openFileInputStream(String referenceFilePath) throws FileNotFoundException {
		File referenceFile = new File(referenceFilePath);
		if (referenceFile.exists()) {
			return new FileInputStream(referenceFile);
		}
		return null;
	}

	protected void importDirectoryToWorkspace(String referenceSourceName, IPath targetLocation) {
		assertNotNull(referenceSourceName);
		File referenceFile = new File(referenceSourceName);
		if (referenceFile.exists()) {
			if (referenceFile.isDirectory()) {
				// Copy files and folders inside the reference directory
				if (!isProjectPath(targetLocation)) {
					IFolder targetFolder = EcorePlugin.getWorkspaceRoot().getFolder(targetLocation);
					if (!targetFolder.exists()) {
						try {
							targetFolder.create(true, true, null);
						} catch (Exception ex) {
							// In case exception because of target folder was already exist, just ignore it.
							if (ex.getMessage().contains("already exists")) {
								// Do nothing
							} else {
								fail("Cannot create new folder in working workspace: " + targetLocation.toString() + " because of " + ex.getMessage());
							}
						}

					}
				}
				for (String fileName : referenceFile.list()) {
					importDirectoryToWorkspace(referenceFile + File.separator + fileName, targetLocation.append(fileName));
				}
			} else {
				IFile copiedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetLocation);
				InputStream sourceFileContents = null;
				try {
					sourceFileContents = openFileInputStream(referenceSourceName);
				} catch (Exception ex) {
					fail("Cannot open input stream from reference file:" + referenceSourceName + "because of " + ex.getMessage());
				} finally {
					// If container of file to import is not project
					if (!isProjectPath(targetLocation.removeLastSegments(1))) {
						IFolder container = EcorePlugin.getWorkspaceRoot().getFolder(targetLocation.removeLastSegments(1));
						if (!container.exists()) {
							try {
								container.create(true, true, null);
							} catch (Exception ex) {
								if (ex.getMessage().contains("already exists")) {
									// Do nothing
								} else {
									fail("Cannot create container for target file in workspace: " + targetLocation + " because of " + ex.getMessage());
								}
							}

						}
					}
					if (copiedFile.exists()) {
						// Overwrite old file's contents
						try {
							copiedFile.setContents(sourceFileContents, true, false, null);
						} catch (Exception ex) {
							fail("Cannot overwrite contents of file exisiting in working workspace:" + copiedFile.getFullPath().toString());
						}

					} else {
						// create new file at the targetLocation
						try {
							copiedFile.create(sourceFileContents, true, null);
						} catch (Exception ex2) {
							// In case, there is an exception while creating new file because of target file some how
							// was already exist, try to overwrite its contents
							if (ex2.getMessage().contains("already exists")) {
								// Overwrite old file's contents
								try {
									copiedFile.setContents(sourceFileContents, true, false, null);
								} catch (Exception ex) {
									fail("Cannot overwrite contents of file exisiting in working workspace:" + copiedFile.getFullPath().toString());
								}
							} else {
								fail("Cannot create new file to working workspace:" + copiedFile.getFullPath().toString() + " because of "
										+ ex2.getMessage());
							}
						}

					}
				}
			}
		}
	}

	private boolean isProjectPath(IPath targetLocation) {
		return targetLocation.segmentCount() == 1;
	}

	protected void copyProjectSettings(String projectName, IPath projectPath) {
		importDirectoryToWorkspace(getFullPathOfReferenceSourceFile(projectName + "/.settings"), projectPath.append(".settings"));
	}

	protected void copyProjectDecsription(String projectName, IPath projectPath) {
		importDirectoryToWorkspace(getFullPathOfReferenceSourceFile(projectName + "/.project"), projectPath.append(".project"));
	}

	protected void importProjectToWorkspace(String projectName) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		importProjectToWorkspace(project);
	}

	protected void importProjectsToWorkspace(Set<IProject> missingReferenceProjects) throws CoreException {
		for (IProject project : missingReferenceProjects) {
			importProjectToWorkspace(project);
		}
	}

	protected void importProjectToWorkspace(IProject project) throws CoreException {
		if (!project.exists()) {
			project.create(new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		} else if (!project.isAccessible()) {
			project.open(new NullProgressMonitor());
		}
		// Copy/override project description and project setting
		importDirectoryToWorkspace(getFullPathOfReferenceSourceFile(project.getName()), project.getFullPath());

	}

	protected String getFullPathOfReferenceSourceFile(String fileName) {
		String result = referenceWorkspaceSourceRootDirectoryPath + File.separator + fileName;
		result = result.replace("/", File.separator);
		result = result.replace(File.separator + File.separator, File.separator);
		return result;
	}

	public ReferenceWorkspaceChangeListener getReferenceWorkspaceChangeListener() {
		return referenceWorkspaceChangeListener;
	}

	public ResourceProblemListener getResourceProblemListener() {
		return resourceProblemListener;
	}
}
