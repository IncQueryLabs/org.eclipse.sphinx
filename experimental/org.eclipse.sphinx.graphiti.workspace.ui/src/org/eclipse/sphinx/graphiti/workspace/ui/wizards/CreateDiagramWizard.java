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

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.graphiti.workspace.metamodel.GraphitiMMDescriptor;
import org.eclipse.sphinx.graphiti.workspace.ui.DiagramUtil;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.messages.Messages;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages.DiagramContainerWizardPage;
import org.eclipse.sphinx.graphiti.workspace.ui.wizards.pages.DiagramTypeWizardPage;
import org.eclipse.ui.IWorkbench;
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
		String diagramType = diagramTypePage.getSelectedType();
		EObject diagramBusinessObject = (EObject) diagramTypePage.getSelectedModelObject();
		diagram = DiagramUtil.createDiagram(containerPath, diagramName, diagramType, diagramBusinessObject);
		if (diagram != null) {
			DiagramUtil.openBasicDiagramEditor(diagram);
			return true;
		}
		return false;
	}

	public Diagram getDiagram() {
		return diagram;
	}
}
