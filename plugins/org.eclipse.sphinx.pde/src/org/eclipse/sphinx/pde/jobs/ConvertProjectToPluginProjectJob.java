/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.pde.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.pde.core.build.IBuild;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginLibrary;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.internal.core.ClasspathComputer;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.TargetPlatformHelper;
import org.eclipse.pde.internal.core.build.WorkspaceBuildModel;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.eclipse.pde.internal.core.util.IdUtil;
import org.eclipse.sphinx.pde.internal.Activator;
import org.eclipse.sphinx.pde.internal.messages.Messages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.osgi.framework.Constants;

@SuppressWarnings("restriction")
public class ConvertProjectToPluginProjectJob extends WorkspaceJob {

	private IProject[] projectsToConvert;

	private String fLibraryName;
	private String[] fSrcEntries;
	private String[] fLibEntries;

	/**
	 * Workspace operation to convert the specified project into a plug-in project.
	 * 
	 * @param theProjectsToConvert
	 *            The project to be converted.
	 */
	public ConvertProjectToPluginProjectJob(IProject[] theProjectsToConvert) {
		super(Messages.job_convertProjectToPlugin);
		projectsToConvert = theProjectsToConvert;
	}

	/**
	 * Convert a normal java project into a plug-in project.
	 * 
	 * @param monitor
	 *            Progress monitor
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(Messages.converting, projectsToConvert.length);

			for (IProject projectToConvert : projectsToConvert) {
				convertProject(projectToConvert, monitor);
				monitor.worked(1);
			}

		} catch (CoreException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		} finally {
			monitor.done();
		}
	}

	private void convertProject(IProject projectToConvert, IProgressMonitor monitor) throws CoreException {

		// Do early checks to make sure we can get out fast if we're not setup
		// properly
		if (projectToConvert == null || !projectToConvert.exists()) {
			return;
		}

		// Nature check - do we need to do anything at all?
		if (projectToConvert.hasNature(PDE.PLUGIN_NATURE)) {
			return;
		}

		CoreUtility.addNatureToProject(projectToConvert, PDE.PLUGIN_NATURE, monitor);

		loadClasspathEntries(projectToConvert, monitor);
		loadLibraryName(projectToConvert);

		createManifestFile(PDEProject.getManifest(projectToConvert), monitor);

		IFile buildFile = PDEProject.getBuildProperties(projectToConvert);
		if (!buildFile.exists()) {
			WorkspaceBuildModel model = new WorkspaceBuildModel(buildFile);
			IBuild build = model.getBuild(true);
			IBuildEntry entry = model.getFactory().createEntry(IBuildEntry.BIN_INCLUDES);
			if (PDEProject.getPluginXml(projectToConvert).exists()) {
				entry.addToken(ICoreConstants.PLUGIN_FILENAME_DESCRIPTOR);
			}
			if (PDEProject.getManifest(projectToConvert).exists()) {
				entry.addToken(ICoreConstants.MANIFEST_FOLDER_NAME);
			}
			for (String fLibEntrie : fLibEntries) {
				entry.addToken(fLibEntrie);
			}

			if (fSrcEntries.length > 0) {
				entry.addToken(fLibraryName);
				IBuildEntry source = model.getFactory().createEntry(IBuildEntry.JAR_PREFIX + fLibraryName);
				for (String fSrcEntrie : fSrcEntries) {
					source.addToken(fSrcEntrie);
				}
				build.add(source);
			}
			if (entry.getTokens().length > 0) {
				build.add(entry);
			}

			model.save();
		}
	}

	private void loadClasspathEntries(IProject project, IProgressMonitor monitor) {
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] currentClassPath = new IClasspathEntry[0];
		List<String> sources = new ArrayList<String>();
		List<String> libraries = new ArrayList<String>();
		try {
			currentClassPath = javaProject.getRawClasspath();
		} catch (JavaModelException ex) {
		}
		for (IClasspathEntry element : currentClassPath) {
			int contentType = element.getEntryKind();
			if (contentType == IClasspathEntry.CPE_SOURCE) {
				String relativePath = getRelativePath(element, project);
				if (relativePath.equals("")) { //$NON-NLS-1$
					sources.add("."); //$NON-NLS-1$
				} else {
					sources.add(relativePath + "/"); //$NON-NLS-1$
				}
			} else if (contentType == IClasspathEntry.CPE_LIBRARY) {
				String path = getRelativePath(element, project);
				if (path.length() > 0) {
					libraries.add(path);
				} else {
					libraries.add("."); //$NON-NLS-1$
				}
			}
		}
		fSrcEntries = sources.toArray(new String[sources.size()]);
		fLibEntries = libraries.toArray(new String[libraries.size()]);

		IClasspathEntry[] classPath = new IClasspathEntry[currentClassPath.length + 1];
		System.arraycopy(currentClassPath, 0, classPath, 0, currentClassPath.length);
		classPath[classPath.length - 1] = ClasspathComputer.createContainerEntry();
		try {
			javaProject.setRawClasspath(classPath, monitor);
		} catch (JavaModelException ex) {
		}
	}

	private String getRelativePath(IClasspathEntry cpe, IProject project) {
		IPath path = project.getFile(cpe.getPath()).getProjectRelativePath();
		return path.removeFirstSegments(1).toString();
	}

	private void loadLibraryName(IProject project) {
		if (isOldTarget() || fLibEntries.length > 0 && fSrcEntries.length > 0) {
			String libName = project.getName();
			int i = libName.lastIndexOf("."); //$NON-NLS-1$
			if (i != -1) {
				libName = libName.substring(i + 1);
			}
			fLibraryName = libName + ".jar"; //$NON-NLS-1$
		} else {
			fLibraryName = "."; //$NON-NLS-1$
		}
	}

	private String createInitialName(String id) {
		int loc = id.lastIndexOf('.');
		if (loc == -1) {
			return id;
		}
		StringBuffer buf = new StringBuffer(id.substring(loc + 1));
		buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
		return buf.toString();
	}

	private void createManifestFile(IFile file, IProgressMonitor monitor) throws CoreException {
		WorkspaceBundlePluginModel model = new WorkspaceBundlePluginModel(file, null);
		model.load();
		IBundle pluginBundle = model.getBundleModel().getBundle();

		String pluginId = pluginBundle.getHeader(Constants.BUNDLE_SYMBOLICNAME);
		String pluginName = pluginBundle.getHeader(Constants.BUNDLE_NAME);
		String pluginVersion = pluginBundle.getHeader(Constants.BUNDLE_VERSION);

		boolean missingInfo = pluginId == null || pluginName == null || pluginVersion == null;

		// If no ID exists, create one
		if (pluginId == null) {
			pluginId = IdUtil.getValidId(file.getProject().getName());
		}
		// At this point, the plug-in ID is not null

		// If no version number exists, create one
		if (pluginVersion == null) {
			pluginVersion = "1.0.0.qualifier"; //$NON-NLS-1$
		}

		// If no name exists, create one using the non-null pluginID
		if (pluginName == null) {
			pluginName = createInitialName(pluginId);
		}

		pluginBundle.setHeader(Constants.BUNDLE_SYMBOLICNAME, pluginId);
		pluginBundle.setHeader(Constants.BUNDLE_VERSION, pluginVersion);
		pluginBundle.setHeader(Constants.BUNDLE_NAME, pluginName);

		if (missingInfo) {
			IPluginModelFactory factory = model.getPluginFactory();
			IPluginBase base = model.getPluginBase();
			if (fLibraryName != null && !fLibraryName.equals(".")) { //$NON-NLS-1$
				IPluginLibrary library = factory.createLibrary();
				library.setName(fLibraryName);
				library.setExported(true);
				base.add(library);
			}
			for (String fLibEntrie : fLibEntries) {
				IPluginLibrary library = factory.createLibrary();
				library.setName(fLibEntrie);
				library.setExported(true);
				base.add(library);
			}
			if (TargetPlatformHelper.getTargetVersion() >= 3.1) {
				pluginBundle.setHeader(Constants.BUNDLE_MANIFESTVERSION, "2"); //$NON-NLS-1$
			}
		}

		model.save();
		monitor.done();
	}

	private boolean isOldTarget() {
		return TargetPlatformHelper.getTargetVersion() < 3.1;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

		try {
			monitor.beginTask(Messages.converting, projectsToConvert.length);

			for (IProject projectToConvert : projectsToConvert) {
				convertProject(projectToConvert, monitor);
				monitor.worked(1);
			}

		} catch (CoreException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}
