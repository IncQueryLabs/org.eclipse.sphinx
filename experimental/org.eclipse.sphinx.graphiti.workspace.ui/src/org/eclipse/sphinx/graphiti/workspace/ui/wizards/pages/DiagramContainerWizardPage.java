package org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class DiagramContainerWizardPage extends WizardNewFileCreationPage {

	private String extension;

	public DiagramContainerWizardPage(String pageName, IStructuredSelection selection, String extension) {
		super(pageName, selection);
		this.extension = extension;
	}

	public URI getURI() {
		return URI.createPlatformResourceURI(getFilePath().toString(), false);
	}

	protected String getExtension() {
		return extension;
	}

	protected IPath getFilePath() {
		IPath path = getContainerFullPath();
		if (path == null) {
			path = new Path(""); //$NON-NLS-1$
		}
		String fileName = getFileName();
		if (fileName != null) {
			path = path.append(fileName);
		}
		return path;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		setFileName(ExtendedPlatform.createUniqueFileName(getContainerFullPath(), "default." + getExtension())); //$NON-NLS-1$
		setPageComplete(validatePage());
	}

	@Override
	protected boolean validatePage() {
		if (!super.validatePage()) {
			return false;
		}
		String extension = getExtension();
		if (extension != null && !getFilePath().getFileExtension().equals(extension)) {
			setErrorMessage(NLS.bind(Messages.DiagramContainerWizardPage_PageExtensionError, extension));
			return false;
		}
		return true;
	}
}
