/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     itemis - [358082] Precedence of Xtend MetaModels gets lost in Xtend/Xpand runtime enhancements implemented in Sphinx
 *     itemis - Revised implementation (redesigned overriding points and getters/setters, improved of naming, fixed progress monitor issues)
 *     See4sys - Replaced ConvertProjectToPluginOperation with ConvertProjectToPluginProjectJob and 
 *               moved from org.eclipse.sphinx.xtendxpand.ui.jobs to org.eclipse.sphinx.xtendxpand.jobs
 *     itemis - [405696] Create separate plug-in for PDE dependencies
 * 
 * </copyright>
 */
package org.eclipse.sphinx.xtendxpand.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.sphinx.pde.jobs.ConvertProjectToPluginProjectJob;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.sphinx.xtendxpand.internal.Activator;
import org.eclipse.sphinx.xtendxpand.internal.messages.Messages;
import org.eclipse.sphinx.xtendxpand.util.XtendXpandUtil;

/**
 * A {@link WorkspaceJob workspace job} that supports conversion of {@link IProject project}s to an Xtend/Xpand-enabled
 * plug-in project.
 */
@SuppressWarnings("restriction")
public class ConvertToXtendXpandEnabledPluginProjectJob extends WorkspaceJob {

	/**
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=309163
	 * 
	 * @since 3.6
	 */
	public static final boolean HIDE_VERSION_1_7 = true;

	private static final String PROJECT_RELATIVE_JAVA_SOURCE_DEFAULT_PATH = "src"; //$NON-NLS-1$

	private static final String JAVA_CLASSPATH_JRE_CONTAINER_ENTRY_SUFFIX_J2SE_1_5 = "/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5"; //$NON-NLS-1$
	private static final String JAVA_CLASSPATH_JRE_CONTAINER_ENTRY_SUFFIX_JAVA_SE_1_6 = "/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.6"; //$NON-NLS-1$
	private static final String JAVA_CLASSPATH_JRE_CONTAINER_ENTRY_SUFFIX_JAVA_SE_1_7 = "/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7"; //$NON-NLS-1$

	private static final String JAVA_EXTENSIONS_PACKAGE_DEFAULT_NAME = "extensions"; //$NON-NLS-1$

	private static final List<String> PDE_DEFAULT_REQUIRED_BUNDLES_IDS = Arrays.asList(new String[] { "org.eclipse.xtend.util.stdlib" }); //$NON-NLS-1$

	private static final String PDE_EXECUTION_ENVIRONMENT_J2SE_15 = "J2SE-1.5"; //$NON-NLS-1$
	private static final String PDE_EXECUTION_ENVIRONMENT_JavaSE_16 = "JavaSE-1.6"; //$NON-NLS-1$
	private static final String PDE_EXECUTION_ENVIRONMENT_JavaSE_17 = "JavaSE-1.7"; //$NON-NLS-1$

	private IProject project;

	private String projectRelativeJavaSourcePath = PROJECT_RELATIVE_JAVA_SOURCE_DEFAULT_PATH;
	private String compilerCompliance;
	private String javaExtensionsPackageName;
	private List<String> requiredBundleIds = null;
	private List<String> enabledMetaModelContributorTypeNames = null;

	public ConvertToXtendXpandEnabledPluginProjectJob(String name, IProject project) {
		super(name);

		this.project = project;
		compilerCompliance = JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE);
		javaExtensionsPackageName = project.getName().toLowerCase() + "." + JAVA_EXTENSIONS_PACKAGE_DEFAULT_NAME; //$NON-NLS-1$

		setPriority(Job.BUILD);
		setRule(ResourcesPlugin.getWorkspace().getRoot());
	}

	public String getProjectRelativeJavaSourcePath() {
		return projectRelativeJavaSourcePath;
	}

	public void setProjectRelativeJavaSourcePath(String projectRelativeJavaSourcePath) {
		this.projectRelativeJavaSourcePath = projectRelativeJavaSourcePath;
	}

	public String getCompilerCompliance() {
		return compilerCompliance;
	}

	public void setCompilerCompliance(String compilerCompliance) {
		validateCompilerCompliance(compilerCompliance);
		this.compilerCompliance = compilerCompliance;
	}

	public String getJavaExtensionsPackageName() {
		return javaExtensionsPackageName;
	}

	public void setJavaExtensionsPackageName(String javaExtensionsPackageName) {
		this.javaExtensionsPackageName = javaExtensionsPackageName;
	}

	public List<String> getRequiredBundleIds() {
		if (requiredBundleIds == null) {
			requiredBundleIds = new ArrayList<String>(PDE_DEFAULT_REQUIRED_BUNDLES_IDS);
		}
		return requiredBundleIds;
	}

	public List<String> getEnabledMetamodelContributorTypeNames() {
		if (enabledMetaModelContributorTypeNames == null) {
			enabledMetaModelContributorTypeNames = new ArrayList<String>();
		}
		return enabledMetaModelContributorTypeNames;
	}

	/*
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_ConvertingToXtendXpandEnabledPluginProject, 5);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Convert project to Java project
		convertToJavaProject(progress.newChild(1));

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Convert project to plug-in project
		convertToPluginProject(progress.newChild(1));

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Convert to Xtend/Xpand project
		convertToXtendXpandProject(progress.newChild(1));

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}
		return Status.OK_STATUS;
	}

	protected void convertToJavaProject(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_ConvertingToJavaProject, 3);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Add Java nature
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			ExtendedPlatform.addNature(project, JavaCore.NATURE_ID, progress.newChild(1));
		}

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Compute Java source path and make sure that all folders on it exist
		IPath javaSourcePath;
		String projectRelativeJavaSourcePath = getProjectRelativeJavaSourcePath();
		if (projectRelativeJavaSourcePath != null && projectRelativeJavaSourcePath.length() > 0) {
			javaSourcePath = project.getFullPath().append(projectRelativeJavaSourcePath);
			IPath projectRelativeJavaSourcePathObj = javaSourcePath.removeFirstSegments(1);
			IFolder javaSourceFolder = project.getFolder(projectRelativeJavaSourcePathObj);
			if (!javaSourceFolder.exists()) {
				SubMonitor createSourceFolderProgress = progress.newChild(1).setWorkRemaining(javaSourcePath.segmentCount());
				for (int i = projectRelativeJavaSourcePathObj.segmentCount() - 1; i >= 0; i--) {
					IFolder folder = project.getFolder(projectRelativeJavaSourcePathObj.removeLastSegments(i));
					if (!folder.exists()) {
						folder.create(false, true, createSourceFolderProgress.newChild(1));
					} else {
						createSourceFolderProgress.worked(1);
					}
				}
			}
		} else {
			javaSourcePath = project.getFullPath();
		}

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Set compiler compliance
		IJavaProject javaProject = JavaCore.create(project);
		String compilerCompliance = getCompilerCompliance();
		if (compilerCompliance != null && !JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE).equals(compilerCompliance)) {
			javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, compilerCompliance);
			javaProject.setOption(JavaCore.COMPILER_SOURCE, compilerCompliance);
			javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, compilerCompliance);
		} else if (compilerCompliance == null) {
			compilerCompliance = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
		}

		// Setup Java classpath
		List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
		classpathEntries.addAll(Arrays.asList(javaProject.getRawClasspath()));

		// Classpath entry for Java source folder required?
		if (javaSourcePath.segmentCount() > 1) {
			// Remove existing classpath entries pointing at direct or indirect parents of Java source folder
			IClasspathEntry sourceClasspathEntry = JavaCore.newSourceEntry(javaSourcePath);
			for (Iterator<IClasspathEntry> iter = classpathEntries.iterator(); iter.hasNext();) {
				IClasspathEntry classpathEntry = iter.next();
				if (classpathEntry.getPath().isPrefixOf(javaSourcePath)) {
					iter.remove();
				}
			}

			// Add new classpath entry for actual Java source folder
			classpathEntries.add(0, sourceClasspathEntry);
		}

		// Remove existing classpath entries pointing at JRE system library
		IClasspathEntry jreClasspathEntry = JavaCore.newVariableEntry(new Path(JavaRuntime.JRELIB_VARIABLE), new Path(JavaRuntime.JRESRC_VARIABLE),
				new Path(JavaRuntime.JRESRCROOT_VARIABLE));
		for (Iterator<IClasspathEntry> i = classpathEntries.iterator(); i.hasNext();) {
			IClasspathEntry classpathEntry = i.next();
			if (classpathEntry.getPath().isPrefixOf(jreClasspathEntry.getPath())) {
				i.remove();
			}
		}

		// Add new classpath entry for actual JRE system library
		String jreContainer = JavaRuntime.JRE_CONTAINER;
		if (JavaCore.VERSION_1_5.equals(compilerCompliance)) {
			jreContainer += JAVA_CLASSPATH_JRE_CONTAINER_ENTRY_SUFFIX_J2SE_1_5;
		} else if (JavaCore.VERSION_1_6.equals(compilerCompliance)) {
			jreContainer += JAVA_CLASSPATH_JRE_CONTAINER_ENTRY_SUFFIX_JAVA_SE_1_6;
		} else if (JavaCore.VERSION_1_7.equals(compilerCompliance)) {
			jreContainer += JAVA_CLASSPATH_JRE_CONTAINER_ENTRY_SUFFIX_JAVA_SE_1_7;
		}
		classpathEntries.add(JavaCore.newContainerEntry(new Path(jreContainer)));

		// Apply Java classpath
		IClasspathEntry[] entries = new IClasspathEntry[classpathEntries.size()];
		int i = 0;
		for (IClasspathEntry entry : classpathEntries) {
			entries[i] = entry;
			i++;
		}
		javaProject.setRawClasspath(entries, progress.newChild(1));
	}

	protected void convertToPluginProject(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_ConvertingToPluginProject, 2);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Convert to plug-in project
		try {
			ConvertProjectToPluginProjectJob convertProjectToPluginOperation = new ConvertProjectToPluginProjectJob(new IProject[] { project });
			convertProjectToPluginOperation.runInWorkspace(progress.newChild(1));
		} catch (Exception ex) {
			IStatus status = StatusUtil.createErrorStatus(Activator.getDefault(), ex);
			throw new CoreException(status);
		}

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Add required bundles and required execution environment
		StringBuilder requiredBundleIdsStr = new StringBuilder();
		for (Iterator<String> iter = getRequiredBundleIds().iterator(); iter.hasNext();) {
			String id = iter.next();
			requiredBundleIdsStr.append(id);
			if (iter.hasNext()) {
				requiredBundleIdsStr.append(","); //$NON-NLS-1$
			}
		}

		// Add required execution environment
		String requiredExecutionEnvironment = null;
		String compilerCompliance = getCompilerCompliance();
		if (JavaCore.VERSION_1_5.equals(compilerCompliance)) {
			requiredExecutionEnvironment = PDE_EXECUTION_ENVIRONMENT_J2SE_15;
		} else if (JavaCore.VERSION_1_6.equals(compilerCompliance)) {
			requiredExecutionEnvironment = PDE_EXECUTION_ENVIRONMENT_JavaSE_16;
		} else if (JavaCore.VERSION_1_7.equals(compilerCompliance)) {
			requiredExecutionEnvironment = PDE_EXECUTION_ENVIRONMENT_JavaSE_17;
		}

		WorkspaceBundlePluginModel model = new WorkspaceBundlePluginModel(PDEProject.getManifest(project), null);
		model.load();
		IBundle pluginBundle = model.getBundleModel().getBundle();
		pluginBundle.setHeader(org.osgi.framework.Constants.REQUIRE_BUNDLE, requiredBundleIdsStr.toString());
		if (requiredExecutionEnvironment != null) {
			pluginBundle.setHeader(org.osgi.framework.Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, requiredExecutionEnvironment);
		}
		model.save();
	}

	protected void convertToXtendXpandProject(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, Messages.task_ConvertingToXtendXpandProject, 3);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Add Xtend/Xpand nature
		if (!project.hasNature(XtendXpandUtil.XTEND_XPAND_NATURE_ID)) {
			ExtendedPlatform.addNature(project, XtendXpandUtil.XTEND_XPAND_NATURE_ID, progress.newChild(1));
		}

		// Set enabled Xtend metamodel contributors
		if (!getEnabledMetamodelContributorTypeNames().isEmpty()) {
			StringBuilder enabledMetaModelContributorTypeNamesStr = new StringBuilder();
			for (Iterator<String> iter = getEnabledMetamodelContributorTypeNames().iterator(); iter.hasNext();) {
				String contributorTypeName = iter.next();
				enabledMetaModelContributorTypeNamesStr.append(contributorTypeName);
				if (iter.hasNext()) {
					enabledMetaModelContributorTypeNamesStr.append(","); //$NON-NLS-1$
				}
			}

			IEclipsePreferences prefs = new ProjectScope(project).getNode(XtendXpandUtil.XTEND_SHARED_UI_PLUGIN_ID);
			prefs.put(XtendXpandUtil.PREFERENCE_KEY_PROJECT_SPECIFIC_METAMODEL, Boolean.TRUE.toString());
			prefs.put(XtendXpandUtil.PREFERENCE_KEY_METAMODEL_CONTRIBUTOR, enabledMetaModelContributorTypeNamesStr.toString());
			try {
				prefs.flush();
			} catch (Exception ex) {
				IStatus status = StatusUtil.createErrorStatus(Activator.getDefault(), ex);
				throw new CoreException(status);
			}
		}
		progress.worked(1);

		// Add Java package for Java-based Xtend extensions
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragmentRoot rootPackageFragmentRoot = javaProject.getPackageFragmentRoot(project.getFolder(getProjectRelativeJavaSourcePath()));
		rootPackageFragmentRoot.createPackageFragment(getJavaExtensionsPackageName(), true, progress.newChild(1));
	}

	protected void validateCompilerCompliance(String compliance) {
		Assert.isLegal(compliance.equals(JavaCore.VERSION_1_6) || compliance.equals(JavaCore.VERSION_1_5),
				NLS.bind(Messages.error_JRECompliance_NotSupported, JavaCore.VERSION_1_5, JavaCore.VERSION_1_6));
		IVMInstall install = JavaRuntime.getDefaultVMInstall();
		if (install instanceof IVMInstall2) {
			String compilerCompliance = getCompilerCompliance((IVMInstall2) install, compliance);
			// compliance to set must be equal or less than compliance level from VMInstall.
			if (!compilerCompliance.equals(compliance)) {
				try {
					float complianceToSet = Float.parseFloat(compliance);
					float complianceFromVM = Float.parseFloat(compilerCompliance);
					Assert.isLegal(complianceToSet <= complianceFromVM,
							NLS.bind(Messages.error_JRECompliance_NotCompatible, compliance, compilerCompliance));
				} catch (NumberFormatException ex) {
					PlatformLogUtil.logAsWarning(Activator.getDefault(), ex);
				}
			}
		}
	}

	protected String getCompilerCompliance(IVMInstall2 vMInstall, String defaultCompliance) {
		String version = vMInstall.getJavaVersion();
		if (version == null) {
			return defaultCompliance;
		} else if (version.startsWith(JavaCore.VERSION_1_7)) {
			return HIDE_VERSION_1_7 ? JavaCore.VERSION_1_6 : JavaCore.VERSION_1_7;
		} else if (version.startsWith(JavaCore.VERSION_1_6)) {
			return JavaCore.VERSION_1_6;
		} else if (version.startsWith(JavaCore.VERSION_1_5)) {
			return JavaCore.VERSION_1_5;
		} else if (version.startsWith(JavaCore.VERSION_1_4)) {
			return JavaCore.VERSION_1_4;
		} else if (version.startsWith(JavaCore.VERSION_1_3)) {
			return JavaCore.VERSION_1_3;
		} else if (version.startsWith(JavaCore.VERSION_1_2)) {
			return JavaCore.VERSION_1_3;
		} else if (version.startsWith(JavaCore.VERSION_1_1)) {
			return JavaCore.VERSION_1_3;
		}
		return defaultCompliance;
	}
}
