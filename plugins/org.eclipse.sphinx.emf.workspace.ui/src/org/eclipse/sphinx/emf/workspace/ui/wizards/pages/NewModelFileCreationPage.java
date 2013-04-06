/**
 * <copyright>
 *
 * Copyright (c) 2013 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [403728] NewModelProjectCreationPage and NewModelFileCreationPage should provided hooks for creating additional controls
 *     itemis - [405023] Enable NewModelFileCreationPage to be used without having to pass an instance of NewModelFileProperties to its constructor
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.workspace.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.ui.wizards.BasicNewModelFileWizard.NewModelFileProperties;
import org.eclipse.sphinx.platform.preferences.IProjectWorkspacePreference;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Basic main page for a wizard that creates a file resource. The new model file is to be created based on the given
 * {@linkplain NewModelFileProperties new model file properties} (metamodel, ePackage and eClassifier).
 * <p>
 * This page may be used by clients as it is; it may also be subclassed to suit. Subclasses may override validatePage(),
 * getFileExtensionErrorMessage(), getRequiredProjectNatureErrorMessage(), hasRequiredProjectNature(), setVisible(),
 * getDefaultFileExtension(), etc.
 */
public class NewModelFileCreationPage extends WizardNewFileCreationPage {

	protected IStructuredSelection selection;
	protected String requiredProjectNatureId;
	protected IMetaModelDescriptor mmDescriptor = null;
	protected IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference = null;

	protected boolean noValidFileExtensionsForContentTypeIdFoundProblemLoggedOnce = false;

	/**
	 * Creates a new instance of new model file creation wizard page.
	 * 
	 * @param pageId
	 *            the name of the page
	 * @param selection
	 *            the current resource selection
	 * @param requiredProjectNatureId
	 *            the required project nature id
	 * @param mmDescriptor
	 *            the {@linkplain IMetaModelDescriptor metamodel} behind the model file to be created
	 */
	public NewModelFileCreationPage(String pageId, IStructuredSelection selection, String requiredProjectNatureId, IMetaModelDescriptor mmDescriptor) {
		super(pageId, selection);

		this.selection = selection;
		this.requiredProjectNatureId = requiredProjectNatureId;
		this.mmDescriptor = mmDescriptor;

		setTitle(Messages.title_newModelFile);
		setDescription(Messages.description_newModelFileCreationPage);
	}

	/**
	 * Creates a new instance of new model file creation wizard page.
	 * 
	 * @param pageId
	 *            the name of the page
	 * @param selection
	 *            the current resource selection
	 * @param requiredProjectNatureId
	 *            the required project nature id
	 * @param newModelFileProperties
	 *            the {@linkplain NewModelFileProperties new model file properties} carrying choices from previous
	 *            wizard page(s)
	 */
	public NewModelFileCreationPage(String pageId, IStructuredSelection selection, String requiredProjectNatureId,
			NewModelFileProperties newModelFileProperties) {
		this(pageId, selection, requiredProjectNatureId, newModelFileProperties != null ? newModelFileProperties.getMetaModelDescriptor() : null);
	}

	/**
	 * Creates a new instance of the new model file creation wizard page.
	 * 
	 * @param pageId
	 *            the name of the page
	 * @param selection
	 *            the current resource selection
	 * @param requiredProjectNatureId
	 *            the required project nature id
	 * @param metaModelVersionPreference
	 *            the metamodel version {@linkplain IProjectWorkspacePreference preference}
	 */
	public NewModelFileCreationPage(String pageId, IStructuredSelection selection, String requiredProjectNatureId,
			IProjectWorkspacePreference<? extends IMetaModelDescriptor> metaModelVersionPreference) {
		super(pageId, selection);

		this.selection = selection;
		this.requiredProjectNatureId = requiredProjectNatureId;
		this.metaModelVersionPreference = metaModelVersionPreference;

		setTitle(Messages.title_newModelFile);
		setDescription(Messages.description_newModelFileCreationPage);
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		createAdditionalControls((Composite) getControl());
	}

	/**
	 * Creates controls for specific project creation options to be placed behind those for file name, container and
	 * advanced options (which are created by {@link WizardNewFileCreationPage#createControl(Composite)}).
	 * <p>
	 * This implementation does nothing.
	 * </p>
	 * This method may be overridden by subclasses to provide custom implementations.
	 * 
	 * @param parent
	 *            the parent composite
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createControl(Composite)
	 */
	protected void createAdditionalControls(Composite parent) {
		// Do nothing by default
	}

	/**
	 * Returns the {@linkplain IProject project} behind the containing resource as entered or selected by the user.
	 * 
	 * @return the {@linkplain IProject container project} or <code>null</code> if it is not yet known
	 */
	public IProject getContainerProject() {
		IPath containerFullPath = getContainerFullPath();
		if (containerFullPath != null && containerFullPath.segmentCount() >= 1) {
			// Return project behind current container path
			return ResourcesPlugin.getWorkspace().getRoot().getProject(containerFullPath.segment(0));
		}
		return null;
	}

	/**
	 * Returns the new {@linkplain IFile file} behind the current file name as entered or selected by the user.
	 * 
	 * @return the new {@linkplain IFile file} or <code>null</code> if it is not yet known
	 */
	public IFile getNewFile() {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName()));
	}

	/**
	 * Returns a unique default name for the new file to be created which is composed of its
	 * {@link #getDefaultNewFileBaseName() default base name} and the {@link #getDefaultNewFileExtension() default
	 * extension}. If this file name already exists new file names are generated by appending an integer number to the
	 * default base name (or to the default extension if the default base name is omitted) until a new file name has
	 * been found that is not yet used.
	 * 
	 * @see #getDefaultNewFileBaseName()
	 * @see #getDefaultNewFileExtension()
	 */
	protected String getUniqueDefaultNewFileName(IContainer container) {
		String baseName = getDefaultNewFileBaseName();
		String extension = getDefaultNewFileExtension();
		if (baseName != null || extension != null) {
			String fileName = createNewFileName(baseName, extension, -1);
			for (int i = 1; container.findMember(fileName) != null; ++i) {
				fileName = createNewFileName(baseName, extension, i);
			}
			return fileName;
		}
		return null;
	}

	/**
	 * Creates a name for the new file to be created using provided base name, extension and number.
	 * 
	 * @param baseName
	 *            the base name of the new file, or <code>null</code> if new file should have no base name
	 * @param extension
	 *            the extension of the new file, or <code>null</code> if new file should have no extension
	 * @param number
	 *            a non-negative number to be appended to the new file's base name - or to its extension in case that
	 *            the former is omitted - or -1 if no number should be appended at all
	 * @return the new file name
	 */
	protected String createNewFileName(String baseName, String extension, int number) {
		StringBuilder fileName = new StringBuilder();
		if (baseName != null) {
			fileName.append(baseName);
			if (number != -1) {
				fileName.append(number);
			}
		}
		if (extension != null) {
			fileName.append("."); //$NON-NLS-1$
			fileName.append(extension);
			if (baseName == null && number != -1) {
				fileName.append(number);
			}
		}
		return fileName.toString();
	}

	/**
	 * Gets the default base name of the new file, a string "default" by default.
	 */
	protected String getDefaultNewFileBaseName() {
		return Messages.default_modelFileBaseName;
	}

	/**
	 * Returns the default file extension of the new model file to be created.
	 * 
	 * @return the first string value of File Extension defined for the content type by default. If more than one file
	 *         extensions are defined for the content type, the clients should provide their specific overridden
	 *         methods.
	 */
	protected String getDefaultNewFileExtension() {
		if (!getValidFileExtensions().isEmpty()) {
			return getValidFileExtensions().iterator().next();
		}
		return null;
	}

	/**
	 * Returns the {@link IMetaModelDescriptor metamodel descriptor} of the new file to be created.
	 * 
	 * @return the new file's metamodel descriptor
	 */
	protected IMetaModelDescriptor getNewFileMetaModelDescriptor() {
		if (mmDescriptor != null) {
			return mmDescriptor;
		}
		if (metaModelVersionPreference != null) {
			return metaModelVersionPreference.get(getContainerProject());
		}
		return null;
	}

	/**
	 * Returns the content type identifier for the metamodel descriptor behind the {@linkplain newModelFileProperties
	 * new model file properties}
	 */
	protected String getNewFileContentTypeId() {
		IMetaModelDescriptor mmDescriptor = getNewFileMetaModelDescriptor();
		if (mmDescriptor != null) {
			return mmDescriptor.getDefaultContentTypeId();
		}
		return null;
	}

	/*
	 * Overridden to initialize wizard page with default file name
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			// Try to determine container behind current selection
			if (selection != null && !selection.isEmpty()) {
				Object selected = selection.iterator().next();

				// Refer to the parent folder or project if selected element is a file
				if (selected instanceof IFile) {
					selected = ((IFile) selected).getParent();
				}

				// Focus wizard on folder or project behind selected element in case it is a container
				if (selected instanceof IContainer) {
					setContainerFullPath(((IContainer) selected).getFullPath());

					String fileName = getUniqueDefaultNewFileName((IContainer) selected);
					if (fileName != null) {
						setFileName(fileName);
					}
				}
			}
		}
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
	 */
	@Override
	protected boolean validatePage() {
		// Let Eclipse check if the model file has a container project or folder, etc.
		if (!super.validatePage()) {
			return false;
		}

		// Make sure that we are in a (model) project that has the required nature
		IProject containerProject = getContainerProject();
		if (containerProject != null && !hasRequiredProjectNature(containerProject)) {
			setErrorMessage(getRequiredProjectNatureErrorMessage());
			return false;
		}

		// Make sure the model file has a valid extension
		String fileExtension = new Path(getFileName()).getFileExtension();
		Collection<String> validFileExtensions = getValidFileExtensions();
		if (!validFileExtensions.isEmpty() && !validFileExtensions.contains(fileExtension)) {
			setErrorMessage(getFileExtensionErrorMessage(validFileExtensions));
			return false;
		}

		return true;
	}

	/**
	 * Checks if the specified {@linkplain IProject project} has the required {@linkplain IProjectNature nature} that
	 * has been provided to this {@linkplain NewModelFileCreationPage}.    
	 * 
	 * @param project
	 *            the {@linkplain IProject project} to be checked; must not be <code>null</code> and must be 
	 *            <em>accessible</em>
	 * @return <code>true</code> if specified {@linkplain IProject project} has the required {@linkplain IProjectNature
	 *         nature} or no nature is required, <code>false</code> otherwise.
	 */
	protected boolean hasRequiredProjectNature(IProject project) {
		Assert.isNotNull(project);
		Assert.isTrue(project.isAccessible());

		if (requiredProjectNatureId == null) {
			return true;
		}

		try {
			return project.hasNature(requiredProjectNatureId);
		} catch (CoreException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		}
		return false;
	}

	/**
	 * Returns the valid file extensions for model files. They are retrieved from the content type to be used when
	 * creating new model file.
	 * 
	 * @return A collection of valid file extensions for model files
	 */
	protected Collection<String> getValidFileExtensions() {
		String contentTypeId = getNewFileContentTypeId();
		if (contentTypeId != null) {
			Collection<String> validFileExtensions = ExtendedPlatform.getContentTypeFileExtensions(contentTypeId);
			if (validFileExtensions.isEmpty()) {
				if (!noValidFileExtensionsForContentTypeIdFoundProblemLoggedOnce) {
					PlatformLogUtil.logAsWarning(Activator.getPlugin(), new RuntimeException(
							"No valid file extensions for content type identifer '" + contentTypeId + "' found.")); //$NON-NLS-1$ //$NON-NLS-2$
					noValidFileExtensionsForContentTypeIdFoundProblemLoggedOnce = true;
				}
			}
			return validFileExtensions;
		}
		return Collections.emptySet();
	}

	protected String getRequiredProjectNatureErrorMessage() {
		return Messages.error_requiredProjectNature;
	}

	protected String getFileExtensionErrorMessage(Collection<String> validFileExtensions) {
		return NLS.bind(Messages.error_fileExtension, convertFileExtensionsToString(validFileExtensions));
	}

	/**
	 * Converts the given collection of file extensions into a string.
	 * 
	 * @param fileExtensions
	 *            the collection of file extensions to be converted
	 * @return the file extensions as string
	 */
	protected String convertFileExtensionsToString(Collection<String> fileExtensions) {
		Assert.isNotNull(fileExtensions);

		StringBuilder buf = new StringBuilder();
		ListIterator<String> iter = new ArrayList<String>(fileExtensions).listIterator();
		while (iter.hasNext()) {
			if (iter.hasPrevious()) {
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append("*."); //$NON-NLS-1$
			buf.append(iter.next());
		}
		return buf.toString();
	}
}
