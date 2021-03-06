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
package org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.features.add;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.ManhattanConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;
import org.eclipse.sphinx.graphiti.workspace.ui.util.DiagramUtil;
import org.eclipse.sphinx.graphiti.workspace.ui.util.PropertyUtil;

public class AddRequiredInterfacesFeature extends AbstractAddFeature {

	public static final IColorConstant E_REFERENCE_FOREGROUND = new ColorConstant(98, 131, 167);

	private ManhattanConnection connection = null;
	private PictogramElement sourceShape = null;
	private PictogramElement targetShape = null;
	private Anchor sourceAnchor = null;
	private Anchor targetAnchor = null;

	public AddRequiredInterfacesFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// return true if given business object is an EReference
		// note, that the context must be an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof Port) {
			return true;
		}
		return false;

	}

	/**
	 * This method retrieves connection features from context taking care of the classic scenario (context created from
	 * the palette) and the extended Drag & Drop feature.
	 * 
	 * @param addConContext
	 */
	/*
	 * TODO : this method is a bit different from the corresponding method in AddProvidedInterface, the only difference
	 * lies in the type of the anchors (chopbox anchor vx box relative) --> refactor these methods and generalize
	 */
	private void retrieveConnectionFeatures(IAddConnectionContext addConContext) {
		if (addConContext.getProperty("sourceAnchor") == null) { //$NON-NLS-1$
			// Classic context
			sourceShape = addConContext.getSourceAnchor();
			targetShape = (PictogramElement) addConContext.getTargetAnchor().eContainer();
			connection.setStart(addConContext.getSourceAnchor());
			connection.setEnd(addConContext.getTargetAnchor());
		} else {
			// Context comes from Drag & Drop
			EObject sourceAnchorObject = (EObject) addConContext.getProperty("sourceAnchor"); //$NON-NLS-1$
			EObject targetAnchorObject = (EObject) addConContext.getProperty("targetAnchor"); //$NON-NLS-1$
			// Retrive the shape from BOs stored in properties map
			PictogramElement[] sourceShapes = getFeatureProvider().getAllPictogramElementsForBusinessObject(sourceAnchorObject);
			for (PictogramElement pe : sourceShapes) {
				// Source anchor is a box relative anchor
				if (pe instanceof BoxRelativeAnchor) {
					sourceShape = pe;
					sourceAnchor = (BoxRelativeAnchor) pe;
					break;
				}
			}
			// Retrive the shape from BOs stored in properties map
			PictogramElement[] targetShapes = getFeatureProvider().getAllPictogramElementsForBusinessObject(targetAnchorObject);
			for (PictogramElement pe : targetShapes) {
				if (pe instanceof ContainerShape) {
					targetShape = pe;
					break;
				}
			}
			EList<Anchor> targetAnchors = ((ContainerShape) targetShape).getAnchors();
			for (Anchor anchor : targetAnchors) {
				// target anchor is a chopbox anchor
				if (anchor instanceof ChopboxAnchor) {
					targetAnchor = anchor;
					break;
				}
			}
			connection.setStart(sourceAnchor);
			connection.setEnd(targetAnchor);
		}
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		Port addedEReference = (Port) context.getNewObject();

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		// CONNECTION WITH POLYLINE
		connection = peCreateService.createManhattanConnection(getDiagram());

		retrieveConnectionFeatures(addConContext);

		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(2);
		polyline.setForeground(manageColor(E_REFERENCE_FOREGROUND));

		// Add user property to connection to store EReference name
		EReference referenceId = TypeModel20Package.Literals.PORT__REQUIRED_INTERFACE;
		PropertyUtil.setReferenceName(polyline.getPictogramElement(), referenceId);

		// Update EMF resource
		PictogramLink sourcelink = sourceShape.getLink();
		// Get the associated Business Object
		final EObject sourceObject = sourcelink.getBusinessObjects().get(0);
		PictogramLink targetlink = targetShape.getLink();
		// Get the associated Business Object
		final EObject targetObject = targetlink.getBusinessObjects().get(0);

		if (context.getProperty("sourceAnchor") == null) { //$NON-NLS-1$
			// Don't update BO model in drag & drop scenario!
			DiagramUtil.addReferenceToBOResource(sourceObject, referenceId, targetObject);
		}
		// Link shape to Business Object
		link(connection, addedEReference);

		// // add dynamic text decorator for the association name
		// ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		// Text text = gaService.createDefaultText(getDiagram(), textDecorator);
		// text.setForeground(manageColor(IColorConstant.BLACK));
		// gaService.setLocation(text, 10, 0);
		//
		// // set reference name in the text decorator
		// text.setValue(referenceId.getName());

		// add static graphical decorator (composition and navigable)
		ConnectionDecorator cd;
		cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createArrow(cd);
		return connection;
	}

	private Polyline createArrow(GraphicsAlgorithmContainer gaContainer) {
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(gaContainer, new int[] { -15, 10, 0, 0, -15, -10 });
		polyline.setForeground(manageColor(E_REFERENCE_FOREGROUND));
		polyline.setLineWidth(2);
		return polyline;
	}
}