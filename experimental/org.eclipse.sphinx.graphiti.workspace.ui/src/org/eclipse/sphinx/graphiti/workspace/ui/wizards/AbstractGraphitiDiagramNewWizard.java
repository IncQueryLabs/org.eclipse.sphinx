/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     itemis - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.graphiti.workspace.ui.wizards;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.graphiti.dt.IDiagramType;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.sphinx.emf.wizard.newmodel.AbstractModelNewWizard;
import org.eclipse.sphinx.emf.wizard.pages.AbstractModelInitialObjectCreationPage;
import org.eclipse.sphinx.emf.wizard.pages.AbstractModelNewFileCreationPage;
import org.eclipse.sphinx.graphiti.workspace.metamodel.GraphitiMMDescriptor;
import org.eclipse.sphinx.graphiti.workspace.ui.internal.Activator;
import org.eclipse.sphinx.graphiti.workspace.ui.util.DiagramUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * An abstract implementation of a Graphiti diagram creation wizard.
 */
public abstract class AbstractGraphitiDiagramNewWizard extends AbstractModelNewWizard {

	protected String graphitiDiagramType;

	protected String emfContentType;

	protected List<String> businessObjectsFileExtension;

	public AbstractGraphitiDiagramNewWizard() {
		super();
		// Set content type of the associated BO model
		initEMFContentType();
		// Set diagram type if redefined in subclasses
		initDiagramType();
	}

	/**
	 * This initializes the metamodel package
	 */
	protected abstract void initEMFContentType();

	/**
	 * This initializes the metamodel factory
	 */
	protected abstract void initDiagramType();

	/**
	 * @return the emfContentType
	 */
	public String getContentType() {
		return emfContentType;
	}

	/**
	 * @param emfContentType
	 *            the emfContentType to set
	 */
	public void setContentType(String contentType) {
		emfContentType = contentType;
	}

	/**
	 * @param fileExtension
	 *            : the extension of the EMF model file tha holds the BO model
	 */
	public void setBOFileExtension(List<String> fileExtension) {
		businessObjectsFileExtension = fileExtension;
	}

	/**
	 * @return the business objects file extension
	 */
	public String getBOFileExtension() {
		return businessObjectsFileExtension.get(0);
	}

	/**
	 * Verifiy diagram type against string declared in extension point
	 * 
	 * @return
	 */
	protected boolean verifyDiagramType() {
		for (IDiagramType candidateDiagramType : GraphitiUi.getExtensionManager().getDiagramTypes()) {
			if (candidateDiagramType.getId().toString() == graphitiDiagramType) {
				return true;
			}
		}
		return false;
	}

	public void setDiagramType(String diagramType) {
		graphitiDiagramType = diagramType;
	}

	public Diagram createDiagram(IPath containerPath, String diagramFileName, String diagramType, EObject diagramBusinessObject) {
		// Create resource
		ResourceSet resourceSet = new ResourceSetImpl();
		IFile modelFile = getModelFile();
		URI uri = URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true);
		Resource graphitiResource = resourceSet.createResource(uri, GraphitiMMDescriptor.GRAPHITI_DIAGRAM_CONTENT_TYPE_ID);

		// Create Diagram and add it to Graphiti resource
		final Diagram diagram = Graphiti.getPeCreateService().createDiagram(diagramType, diagramFileName, true);

		// link the diagram to the root business model
		PictogramLink link = createPictogramLink(diagram);
		link.getBusinessObjects().add(diagramBusinessObject);

		// add Diagram object and link to Graphiti resource
		graphitiResource.getContents().add(diagram);
		graphitiResource.getContents().add(link);

		// Save the contents of the Graphiti resource to the file system.
		Map<Object, Object> options = new HashMap<Object, Object>();
		options.put(XMLResource.OPTION_ENCODING, initialObjectCreationPage.getEncoding());
		try {
			graphitiResource.save(options);
		} catch (IOException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), "Problem when saving resource holding business object!"); //$NON-NLS-1$
		}

		return diagram;
	}

	@Override
	public boolean performFinish() {
		// Compute the URI of the EMF resource
		IPath containerPath = newFileCreationPage.getContainerFullPath();
		IPath emfResourcePath = getModelFile().getFullPath().removeFileExtension().addFileExtension(getBOFileExtension());
		URI uri = URI.createPlatformResourceURI(emfResourcePath.toString(), true);

		// Create EMF resource for the BO model.
		if (!super.createEMFResource(uri, emfContentType)) {
			return false;
		}

		// Create the Diagram
		String diagramName = newFileCreationPage.getFileName();
		Diagram diagram = createDiagram(containerPath, diagramName, graphitiDiagramType, modelRoot);
		if (diagram != null) {
			// Use the Default Graphiti diagram editor
			DiagramUtil.openBasicDiagramEditor(diagram);
			return true;
		}
		return false;
	}

	public static PictogramLink createPictogramLink(Diagram diagram) {
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

	@Override
	protected void createInitialObjectCreationPage(String MODEL_WIZARD_NAME) {
		// Create the initial object selection page
		initialObjectCreationPage = new AbstractModelInitialObjectCreationPage("Whatever2", getInitialObjectNames(), editPlugin); //$NON-NLS-1$
		initialObjectCreationPage.setTitle(Activator.INSTANCE.getString("_UI_DiagramWizard_label", new Object[] { MODEL_WIZARD_NAME })); //$NON-NLS-1$
		initialObjectCreationPage.setDescription(Activator.INSTANCE.getString("_UI_Wizard_initial_business_object_description")); //$NON-NLS-1$

	}

	@Override
	protected void createFileCreationPage(String MODEL_WIZARD_NAME, List<String> FILE_EXTENSIONS) {
		// Create the creation page
		newFileCreationPage = new AbstractModelNewFileCreationPage("Whatever", selection); //$NON-NLS-1$
		newFileCreationPage.setTitle(Activator.INSTANCE.getString("_UI_DiagramWizard_label", new Object[] { MODEL_WIZARD_NAME })); //$NON-NLS-1$
		newFileCreationPage.setDescription(Activator.INSTANCE.getString("_UI_DiagramWizard_description", new Object[] { MODEL_WIZARD_NAME })); //$NON-NLS-1$
		newFileCreationPage.setFileName(Activator.INSTANCE.getString("_UI_DiagramEditorFilenameDefaultBase") + "." + FILE_EXTENSIONS.get(0)); //$NON-NLS-1$ //$NON-NLS-2$
		newFileCreationPage.setFileExtensions(FILE_EXTENSIONS);
	}

}
