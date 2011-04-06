package org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.wizards;

import org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.wizards.pages.Hummingbird20PlatformDiagramContainerWizardPage;
import org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.wizards.pages.Hummingbird20PlatformDiagramRootWizardPage;
import org.eclipse.sphinx.graphiti.workspace.metamodel.GraphitiMMDescriptor;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.AbstractCreateDiagramWizard;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages.AbstractDiagramContainerWizardPage;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages.AbstractDiagramRootWizardPage;

public class Hummingbird20CreatePlatformDiagramWizard extends AbstractCreateDiagramWizard {

	@Override
	protected AbstractDiagramContainerWizardPage createDiagramContainerWizardPage() {
		return new Hummingbird20PlatformDiagramContainerWizardPage(Hummingbird20PlatformDiagramContainerWizardPage.class.getSimpleName(), selection,
				GraphitiMMDescriptor.GRAPHITI_DIAGRAM_DEFAULT_FILE_EXTENSION);
	}

	@Override
	protected AbstractDiagramRootWizardPage createDiagramRootWizardPage() {
		return new Hummingbird20PlatformDiagramRootWizardPage(Hummingbird20PlatformDiagramRootWizardPage.class.getSimpleName());
	}
}
