/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
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
package org.eclipse.sphinx.graphiti.workspace.ui.wizards;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.graphiti.workspace.metamodel.GraphitiMMDescriptor;
import org.eclipse.sphinx.graphiti.workspace.ui.BasicDiagramEditorInput;
import org.eclipse.sphinx.graphiti.workspace.ui.editors.BasicGraphitiDiagramEditor;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.Activator;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages.DiagramContainerWizardPage;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages.DiagramTypeWizardPage;
import org.eclipse.sphinx.graphiti.workspace.util.GraphitiResourceUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * The Class CreateDiagramWizard.
 */
// TODO (aakar)Make this wizard basic and just for one diagram type
public class CreateDiagramWizard extends BasicNewResourceWizard {

	private static final String WIZARD_WINDOW_TITLE = "New Diagram"; //$NON-NLS-1$

	private DiagramContainerWizardPage diagramContainerPage;
	private DiagramTypeWizardPage diagramTypePage;

	private Diagram diagram;

	@Override
	public void addPages() {
		super.addPages();
		diagramContainerPage = new DiagramContainerWizardPage(Messages.DiagramContainerWizardPage_PageName, selection,
				GraphitiMMDescriptor.GRAPHITI_DIAGRAM_DEFAULT_FILE_EXTENSION);
		diagramContainerPage.setTitle(Messages.DiagramContainerWizardPage_PageTitle);
		diagramContainerPage.setDescription(Messages.DiagramContainerWizardPage_PageDescription);
		addPage(diagramContainerPage);

		diagramTypePage = new DiagramTypeWizardPage(Messages.DiagramTypeWizardPage_DiagramTypeField);
		addPage(diagramTypePage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle(WIZARD_WINDOW_TITLE);
	}

	@Override
	public boolean performFinish() {
		String diagramName = diagramContainerPage.getFileName();
		IPath containerPath = diagramContainerPage.getContainerFullPath();

		String diagramTypeId = diagramTypePage.getSelectedType();
		EObject diagramBusinessObject = (EObject) diagramTypePage.getSelectedModelObject();

		IContainer container = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(containerPath);

		if (container == null || !container.isAccessible()) {
			String error = Messages.CreateDiagramWizard_NoAccessibleContainerFoundError;
			IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getSymbolicName(), error);
			ErrorDialog.openError(getShell(), Messages.CreateDiagramWizard_NoContainerFoundErrorTitle, null, status);
			return false;
		}

		diagram = Graphiti.getPeCreateService().createDiagram(diagramTypeId, diagramName, true);

		// ------ link the diagram to the root business model
		PictogramLink link = createPictogramLink(diagram);
		link.getBusinessObjects().add(diagramBusinessObject);

		String editorID = BasicGraphitiDiagramEditor.BASIC_DIAGRAM_EDITOR_ID;
		final TransactionalEditingDomain domain = WorkspaceEditingDomainUtil.getEditingDomain(diagramBusinessObject);
		IFile diagramFile = container.getFile(new Path(diagramName));
		final URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);

		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(domain, new Runnable() {

				public void run() {
					EcoreResourceUtil.saveNewModelResource(domain.getResourceSet(), uri, GraphitiMMDescriptor.GRAPHITI_DIAGRAM_CONTENT_TYPE_ID,
							diagram, GraphitiResourceUtil.getSaveOptions());

				}
			}, Messages.CreateDiagramWizard_SavingDiagramOperation);
		} catch (OperationCanceledException ex) {

		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		DiagramEditorInput editorInput = new BasicDiagramEditorInput(EcoreUtil.getURI(diagram), domain, providerId);

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, editorID);
		} catch (PartInitException e) {
			String error = Messages.CreateDiagramWizard_OpeningEditorError;
			IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getSymbolicName(), error, e);
			ErrorDialog.openError(getShell(), Messages.DiagramTypeWizardPage_ErrorOccuredTitle, null, status);
			return false;
		}

		return true;
	}

	public Diagram getDiagram() {
		return diagram;
	}

	private PictogramLink createPictogramLink(Diagram pe) {
		PictogramLink link = null;
		if (getDiagram() != null) {
			// create new link
			link = PictogramsFactory.eINSTANCE.createPictogramLink();
			link.setPictogramElement(pe);

			// add new link to diagram
			getDiagram().getPictogramLinks().add(link);
		}
		return link;
	}

}
