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
package org.eclipse.sphinx.testutils.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.testutils.TestFileAccessor;

@SuppressWarnings("nls")
public class ZipArchiveImporter {

	private ZipContentProvider zipContentProvider;
	private static final String PROJECT_SETTING_FOLDER = ".setting";
	private String directoryRoot = null;

	/**
	 * Use this constructor if you want to import projects from a zip file into test workspace, then call
	 * importExistingProjectsFromArchive method.
	 */
	public ZipArchiveImporter() {
	}

	public String getDirectoryRoot() {
		return directoryRoot;
	}

	/**
	 * Answer a handle to the zip file currently specified as being the source. Return null if this file does not exist
	 * or is not of valid format.
	 * 
	 * @throws CoreException
	 */
	private ZipFile getZipSourceFile(TestFileAccessor inputFileAccessor, String inputFileName) throws CoreException {
		try {
			URI inputFileURI = inputFileAccessor.getInputFileURI(inputFileName, true);
			if (inputFileURI != null) {
				String inputFilePath = inputFileURI.getPath();
				if (inputFilePath != null && inputFilePath.length() > 0) {
					return new ZipFile(inputFilePath.toString());
				}
			}
			return null;
		} catch (Exception ex) {
			throw new CoreException(StatusUtil.createErrorStatus(Activator.getPlugin(), ex));
		}
	}

	/**
	 * Extract a zipfile to the target location
	 * 
	 * @throws CoreException
	 */
	public void unzipArchiveFile(final TestFileAccessor inputFileAccessor, final String inputFileName, final String targetLocation)
			throws CoreException {

		ZipFile zipSourceFile = getZipSourceFile(inputFileAccessor, inputFileName);
		if (zipSourceFile == null) {
			String msg = "Zip file '" + inputFileName + "' in plug-in '" + inputFileAccessor.getTargetPlugin().getBundle().getSymbolicName()
					+ "' doesn't exist or has an invalid format.";
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), msg));
		}

		directoryRoot = unzipArchiveFile(zipSourceFile, targetLocation);
	}

	@SuppressWarnings("rawtypes")
	private String unzipArchiveFile(ZipFile zipSourceFile, String targetLocation) throws CoreException {

		String referenceWorspaceRootDirectoryPath = null;
		Enumeration en = zipSourceFile.entries();

		while (en.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) en.nextElement();

			if (entry.isDirectory()) {
				continue;
			}
			InputStream stm;
			try {
				stm = zipSourceFile.getInputStream(entry);
			} catch (java.io.IOException e) {
				String msg = "Cannot get input stream for '" + entry.toString() + "' in zip file '";
				throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), msg));
			}
			String targetFilename = targetLocation + File.separator + entry.toString();
			targetFilename = targetFilename.replace('/', File.separatorChar);
			int lastSlash = targetFilename.lastIndexOf(File.separator);
			// Create directory if needed
			if (lastSlash > 0) {
				String dirName = targetFilename.substring(0, lastSlash);
				new File(dirName).mkdirs();

			}
			// Get the reference workspace root directory path
			if (referenceWorspaceRootDirectoryPath == null && targetFilename.contains(".project")) {
				// Remove 2 last segments of .project file to get reference workspace source root directory's path
				referenceWorspaceRootDirectoryPath = removeLastSegments(targetFilename, 2);

			}

			// Copy zip entry to output directory
			streamToFile(stm, targetFilename);
		}
		return referenceWorspaceRootDirectoryPath;
	}

	private String removeLastSegments(String path, int n) {
		String result = path;
		for (int i = 0; i < n; i++) {

			int lastSlashIndex = result.lastIndexOf(File.separator);
			if (lastSlashIndex > 0) {
				result = result.substring(0, lastSlashIndex);
			}
		}
		return result;
	}

	private static void streamToFile(InputStream stm, String targetFilename) throws CoreException {
		FileOutputStream stmOut = null;
		byte[] buffer = new byte[1024];
		int bytecount;
		try {
			stmOut = new FileOutputStream(new File(targetFilename));
		} catch (java.io.IOException e) {
			String msg = "Error opening output file '" + targetFilename + "': " + e.toString();
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), msg));
		}
		try {
			while ((bytecount = stm.read(buffer)) > 0) {
				stmOut.write(buffer, 0, bytecount);
			}
			stmOut.close();
		}

		catch (java.io.IOException e) {
			String msg = "Error writing output file '" + targetFilename + "': " + e.toString();
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), msg));
		}

	}

	/**
	 * Import selected projects stored in an Archive file into current workspace. If used under Junit Test context,
	 * project are created in Junit-Workspace.
	 * 
	 * @param inputFileAccessor
	 *            The file accessor retrieving the zip file.
	 * @param inputFileName
	 *            The name of the archive file
	 * @param projectNameFilter
	 *            List of string containing name of the projects included in the archive that must be loaded.
	 * @throws CoreException
	 * @throws Exception
	 * @TODO throws exception to the upper call level.
	 */
	public void importArchive(final TestFileAccessor inputFileAccessor, final String inputFileName, final String[] projectNameFilter)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				ZipFile zipSourceFile = getZipSourceFile(inputFileAccessor, inputFileName);
				if (zipSourceFile == null) {
					String msg = "Zip file '" + inputFileName + "' in plug-in '" + inputFileAccessor.getTargetPlugin().getBundle().getSymbolicName()
							+ "' doesn't exist or has an invalid format.";
					throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), msg));
				}
				zipContentProvider = new ZipContentProvider(zipSourceFile);
				importProjects(zipContentProvider.getRoot(), 0, projectNameFilter);
			}
		};

		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
	}

	public void importArchiveToProject(final TestFileAccessor inputFileAccessor, final String inputFileName, final IProject project)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				ZipFile zipSourceFile = getZipSourceFile(inputFileAccessor, inputFileName);
				if (zipSourceFile == null) {
					String msg = "Zip file '" + inputFileName + "' in plug-in '" + inputFileAccessor.getTargetPlugin().getBundle().getSymbolicName()
							+ "' doesn't exist or has an invalid format.";
					throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), msg));
				}
				zipContentProvider = new ZipContentProvider(zipSourceFile);
				long startTime = System.currentTimeMillis();
				importObject(zipContentProvider.getRoot(), project);
				long finishTime = System.currentTimeMillis();
				System.out.println("Time for importing files to project:" + (finishTime - startTime));
			}
		};

		ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
	}

	/**
	 * Returns the container resource that the passed file system object should be imported into.
	 * 
	 * @param fileSystemObject
	 *            the file system object being imported
	 * @return the container resource that the passed file system object should be imported into
	 * @exception CoreException
	 *                if this method failed
	 */
	private IContainer getParentContainer(Object fileSystemObject, IContainer destinationContainer) throws CoreException {
		IPath pathname = new Path(zipContentProvider.getFullPath(fileSystemObject));
		IContainer container = destinationContainer;
		IPath path = pathname.removeLastSegments(1);
		int segmentCount = path.segmentCount();

		for (int i = 0; i < segmentCount; i++) {
			container = destinationContainer.getFolder(new Path(path.segment(i)));
			if (!container.exists()) {
				((IFolder) container).create(false, true, null);
			}
		}

		return container;
	}

	private void importProjects(Object entry, int level, String[] projectNameFilter) throws CoreException {
		List<Object> children = zipContentProvider.getChildren(entry);
		MultiStatus errorStatus = new MultiStatus(Activator.getPlugin().getSymbolicName(), IStatus.ERROR,
				"Problems encountered while importing projects.", new RuntimeException());

		if (children != null) {
			for (Object child : children) {
				if (zipContentProvider.isFolder(child)) {
					String folderName = zipContentProvider.getLabel(child);
					if (folderName.equals(PROJECT_SETTING_FOLDER)) {
						continue;
					}
					importProjects(child, level + 1, projectNameFilter);
				}
				String elementLabel = zipContentProvider.getLabel(child);
				if (elementLabel.equals(IProjectDescription.DESCRIPTION_FILE_NAME)) {
					String projectName = null;
					if (child != null) {
						InputStream contentsStream = zipContentProvider.getContents(child);
						try {
							// If we can get a description pull the name from there
							if (contentsStream != null) {
								IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().loadProjectDescription(contentsStream);
								if (projectDescription != null) {
									projectName = projectDescription.getName();
								}
							}
						} catch (CoreException ex) {
							errorStatus.add(ex.getStatus());
						} finally {
							if (contentsStream != null) {
								try {
									contentsStream.close();
								} catch (IOException ex) {
									// Ignore exception
								}
							}
						}
					}
					if (projectName != null) {
						try {
							if (projectNameFilter != null) {
								for (String filtered : projectNameFilter) {
									if (filtered.equals(projectName)) {
										importProject(projectName, entry, level);
										continue;
									}
								}

							} else {
								importProject(projectName, entry, level);
							}
						} catch (CoreException ex) {
							errorStatus.add(ex.getStatus());
						}
					}
				}
			}

		}
		if (errorStatus.getChildren().length > 0) {
			throw new CoreException(errorStatus);
		}
	}

	private void importProject(String projectName, Object projectEntry, int level) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (projectEntry != null) {
			List<Object> fileSystemObjects = zipContentProvider.getChildren(projectEntry);
			zipContentProvider.setStrip(level);
			if (!project.exists()) {
				project.create(new NullProgressMonitor());
				project.open(new NullProgressMonitor());
			} else if (!project.isAccessible()) {
				project.open(new NullProgressMonitor());
			}

			for (Object fileSystemObject : fileSystemObjects) {
				importObject(fileSystemObject, project);
			}
		}
	}

	/**
	 * Imports the specified file system object recursively into the workspace. If the import fails, adds a status
	 * object to the list to be returned by <code>getStatus</code>.
	 * 
	 * @param entry
	 *            the Zip file entry object to be imported
	 * @param policy
	 *            determines how the file system object and children are imported
	 * @throws CoreException
	 * @exception OperationCanceledException
	 *                if canceled
	 */
	private void importObject(Object entry, IContainer destinationProject) throws CoreException {
		if (zipContentProvider.isFolder(entry)) {
			importFolder(entry, destinationProject);
			for (Object children : zipContentProvider.getChildren(entry)) {
				importObject(children, destinationProject);
			}
		} else {
			importFile(entry, destinationProject);
		}

	}

	/**
	 * Imports the specified file system container object into the workspace. If the import fails, adds a status object
	 * to the list to be returned by <code>getResult</code>.
	 * 
	 * @param folderObject
	 *            the file system container object to be imported
	 * @param policy
	 *            determines how the folder object and children are imported
	 * @return the policy to use to import the folder's children
	 * @throws CoreException
	 */
	private void importFolder(Object folderObject, IContainer destinationProject) throws CoreException {
		IContainer containerResource = getParentContainer(folderObject, destinationProject);
		IPath containerPath = containerResource.getFullPath();
		IPath resourcePath = containerPath.append(zipContentProvider.getLabel(folderObject));
		IWorkspaceRoot root = destinationProject.getWorkspace().getRoot();

		// Do not attempt the import if the resource path is unchanged. This may happen
		// when importing from a zip file.
		if (resourcePath.equals(containerPath) || root.exists(resourcePath)) {
			return;
		}
		root.getFolder(resourcePath).create(false, true, null);
	}

	/**
	 * Imports the specified file system object into the workspace. If the import fails, adds a status object to the
	 * list to be returned by <code>getResult</code>.
	 * 
	 * @param fileObject
	 *            the file system object to be imported
	 * @param policy
	 *            determines how the file object is imported
	 * @throws CoreException
	 */
	private void importFile(Object fileObject, IContainer destinationProject) throws CoreException {
		IContainer resourceParentContainer = getParentContainer(fileObject, destinationProject);

		String fileObjectPath = zipContentProvider.getFullPath(fileObject);
		IFile targetResource = resourceParentContainer.getFile(new Path(zipContentProvider.getLabel(fileObject)));

		// Ensure that the source and target are not the same
		IPath targetPath = targetResource.getLocation();
		// Use java.io.File for comparison to avoid platform specific case issues
		if (targetPath != null && targetPath.toFile().equals(new File(fileObjectPath))) {
			return;
		}

		InputStream contentStream = null;
		try {
			contentStream = zipContentProvider.getContents(fileObject);
			if (targetResource.exists()) {
				targetResource.setContents(contentStream, IResource.KEEP_HISTORY, null);
			} else {
				targetResource.create(contentStream, false, null);
			}

			if (fileObject instanceof File) {
				targetResource.setResourceAttributes(ResourceAttributes.fromFile((File) fileObject));
			}
		} finally {
			try {
				if (contentStream != null) {
					contentStream.close();
				}
			} catch (IOException e) {
				// Ignore exception
			}
		}
	}

	public class ZipContentProvider {

		private ZipFile zipFile;

		private ZipEntry root = new ZipEntry("/");

		private Map<ZipEntry, List<Object>> children;

		private Map<IPath, ZipEntry> directoryEntryCache = new HashMap<IPath, ZipEntry>();

		private int stripLevel;

		public ZipContentProvider(ZipFile sourceFile) {
			Assert.isNotNull(sourceFile);
			zipFile = sourceFile;
			stripLevel = 0;
		}

		public List<Object> getChildren(Object element) {
			if (children == null) {
				initialize();
			}

			return children.get(element);
		}

		public InputStream getContents(Object element) throws CoreException {
			try {
				return zipFile.getInputStream((ZipEntry) element);
			} catch (IOException ex) {
				IStatus status = StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
				throw new CoreException(status);
			}
		}

		/**
		 * Returns the entry that this importer uses as the root sentinel.
		 * 
		 * @return java.util.zip.ZipEntry
		 */
		public Object getRoot() {
			return root;
		}

		/**
		 * 
		 */
		public String getLabel(Object element) {
			if (element.equals(root)) {
				return ((ZipEntry) element).getName();
			}

			return stripPath(new Path(((ZipEntry) element).getName()).lastSegment());
		}

		public boolean isFolder(Object element) {
			return ((ZipEntry) element).isDirectory();
		}

		public void setStrip(int level) {
			stripLevel = level;
		}

		public String getFullPath(Object element) {
			return stripPath(((ZipEntry) element).getName());
		}

		/**
		 * Creates a new container zip entry with the specified name, iff it has not already been created. If the parent
		 * of the given element does not already exist it will be recursively created as well.
		 * 
		 * @param pathname
		 *            The path representing the container
		 * @return The element represented by this pathname (it may have already existed)
		 */
		protected ZipEntry createContainer(IPath pathname) {
			ZipEntry existingEntry = directoryEntryCache.get(pathname);
			if (existingEntry != null) {
				return existingEntry;
			}

			ZipEntry parent;
			if (pathname.segmentCount() == 1) {
				parent = root;
			} else {
				parent = createContainer(pathname.removeLastSegments(1));
			}
			ZipEntry newEntry = new ZipEntry(pathname.toString());
			directoryEntryCache.put(pathname, newEntry);
			children.put(newEntry, new ArrayList<Object>());

			List<Object> parentChildList = children.get(parent);
			parentChildList.add(newEntry);
			return newEntry;
		}

		/**
		 * Creates a new file zip entry with the specified name.
		 */
		protected void createFile(ZipEntry entry) {
			IPath pathname = new Path(entry.getName());
			ZipEntry parent;
			if (pathname.segmentCount() == 1) {
				parent = root;
			} else {
				parent = directoryEntryCache.get(pathname.removeLastSegments(1));
			}

			List<Object> childList = children.get(parent);
			childList.add(entry);
		}

		/**
		 * Initializes this object's children table based on the contents of the specified source file.
		 */
		protected void initialize() {
			children = new HashMap<ZipEntry, List<Object>>(1000);

			children.put(root, new ArrayList<Object>());
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				IPath path = new Path(entry.getName()).addTrailingSeparator();

				if (entry.isDirectory()) {
					createContainer(path);
				} else {
					// Ensure the container structure for all levels above this is initialized
					// Once we hit a higher-level container that's already added we need go no further
					int pathSegmentCount = path.segmentCount();
					if (pathSegmentCount > 1) {
						createContainer(path.uptoSegment(pathSegmentCount - 1));
					}
					createFile(entry);
				}
			}
		}

		/*
		 * Strip the leading directories from the path
		 */
		private String stripPath(String path) {
			String pathOrig = new String(path);
			for (int i = 0; i < stripLevel; i++) {
				int firstSep = path.indexOf('/');
				// If the first character was a seperator we must strip to the next
				// separator as well
				if (firstSep == 0) {
					path = path.substring(1);
					firstSep = path.indexOf('/');
				}
				// No separator was present so we're in a higher directory right
				// now
				if (firstSep == -1) {
					return pathOrig;
				}
				path = path.substring(firstSep);
			}
			return path;
		}
	}

}
