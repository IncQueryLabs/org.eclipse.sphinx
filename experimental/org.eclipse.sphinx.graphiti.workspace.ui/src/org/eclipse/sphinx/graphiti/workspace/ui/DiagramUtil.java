package org.eclipse.sphinx.graphiti.workspace.ui;

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
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.graphiti.workspace.metamodel.GraphitiMMDescriptor;
import org.eclipse.sphinx.graphiti.workspace.ui.editors.BasicGraphitiDiagramEditor;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.Activator;
import org.eclipse.sphinx.graphiti.workspace.util.GraphitiResourceUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DiagramUtil {

	public static Diagram createDiagram(IPath containerPath, String diagramFileName, String diagramType, EObject diagramBusinessObject) {

		IContainer container = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(containerPath);
		if (container == null || !container.isAccessible()) {
			// NoAccessibleContainerFoundError
			String error = "CreateDiagramWizard_NoAccessibleContainerFoundError=Diagram Container is not accessible!";
			IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getSymbolicName(), error);
			// NoContainerFoundErrorTitle
			ErrorDialog.openError(ExtendedPlatformUI.getActiveShell(), "No Container Found", null, status);
			return null;
		}

		final Diagram diagram = Graphiti.getPeCreateService().createDiagram(diagramType, diagramFileName, true);

		// ------ link the diagram to the root business model
		PictogramLink link = createPictogramLink(diagram);
		link.getBusinessObjects().add(diagramBusinessObject);

		final TransactionalEditingDomain domain = WorkspaceEditingDomainUtil.getEditingDomain(diagramBusinessObject);
		IFile diagramFile = container.getFile(new Path(diagramFileName));
		final URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);

		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(domain, new Runnable() {

				public void run() {
					EcoreResourceUtil.saveNewModelResource(domain.getResourceSet(), uri, GraphitiMMDescriptor.GRAPHITI_DIAGRAM_CONTENT_TYPE_ID,
							diagram, GraphitiResourceUtil.getSaveOptions());
				}// SavingDiagramOperation=
			}, "Saving diagram...");
		} catch (OperationCanceledException ex) {

		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return diagram;
	}

	public static IEditorPart openBasicDiagramEditor(final Diagram diagram) {
		String editorID = BasicGraphitiDiagramEditor.BASIC_DIAGRAM_EDITOR_ID;
		return openDiagramEditor(diagram, editorID);
	}

	public static IEditorPart openDiagramEditor(final Diagram diagram, String editorID) {
		TransactionalEditingDomain domain = WorkspaceEditingDomainUtil.getEditingDomain(diagram);
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		DiagramEditorInput editorInput = new BasicDiagramEditorInput(EcoreUtil.getURI(diagram), domain, providerId);

		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, editorID);
		} catch (PartInitException e) {
			// OpeningEditorError
			String error = "Error while opening diagram editor";
			IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getSymbolicName(), error, e);
			// ErrorOccuredTitle
			ErrorDialog.openError(ExtendedPlatformUI.getActiveShell(), "An error occured", null, status);
			return null;
		}
	}

	private static PictogramLink createPictogramLink(Diagram diagram) {
		PictogramLink link = null;
		if (diagram != null) {
			// Create new link
			link = PictogramsFactory.eINSTANCE.createPictogramLink();
			link.setPictogramElement(diagram);

			// Add new link to diagram
			diagram.getPictogramLinks().add(link);
		}
		return link;
	}
}
