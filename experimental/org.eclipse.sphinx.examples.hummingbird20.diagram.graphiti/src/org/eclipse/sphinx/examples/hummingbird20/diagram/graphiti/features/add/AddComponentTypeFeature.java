/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;

/**
 * Graphiti feature for adding Hummingbird 2.0 {@link ComponentType} elements.
 */
public class AddComponentTypeFeature extends AbstractAddShapeFeature {

	private static final IColorConstant COMPONENT_TYPE_COLOR_TEXT_FOREGROUND = new ColorConstant(51, 51, 153);
	private static final IColorConstant COMPONENT_TYPE_COLOR_FOREGROUND = new ColorConstant(255, 102, 0);
	private static final IColorConstant COMPONENT_TYPE_COLOR_BACKGROUND = new ColorConstant(255, 204, 153);
	private static final int COMPONENT_TYPE_WIDTH_DEFAULT = 100;
	private static final int COMPONENT_TYPE_HEIGHT_DEFAULT = 50;
	private static final int COMPONENT_TYPE_HEIGHT_UPPER_COMPARTMENT_DEFAULT = 20;

	public AddComponentTypeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/*
	 * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
	 */
	public boolean canAdd(IAddContext context) {
		// Is it an add request for a ComponentType?
		if (context.getNewObject() instanceof ComponentType) {
			// Is it an add request for adding the ComponentType to a diagram?
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	/*
	 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	public PictogramElement add(IAddContext context) {
		ComponentType componentTypeToAdd = (ComponentType) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		// CONTAINER SHAPE WITH RECTANGLE

		// Create container shape
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);
		{

			// Create and set graphics algorithm
			Rectangle rectangle = gaService.createRectangle(containerShape);
			rectangle.setForeground(manageColor(COMPONENT_TYPE_COLOR_FOREGROUND));
			rectangle.setBackground(manageColor(COMPONENT_TYPE_COLOR_BACKGROUND));
			rectangle.setLineWidth(2);
			gaService.setLocationAndSize(rectangle, context.getX(), context.getY(), COMPONENT_TYPE_WIDTH_DEFAULT, COMPONENT_TYPE_HEIGHT_DEFAULT);

			// Create link and wire it up
			link(containerShape, componentTypeToAdd);
		}

		// SHAPE WITH LINE
		{
			// Create shape for line
			Shape lineShape = peCreateService.createShape(containerShape, false);

			// Create and set graphics algorithm
			Polyline polyline = gaService.createPolyline(lineShape, new int[] { 0, COMPONENT_TYPE_HEIGHT_UPPER_COMPARTMENT_DEFAULT,
					COMPONENT_TYPE_WIDTH_DEFAULT, COMPONENT_TYPE_HEIGHT_UPPER_COMPARTMENT_DEFAULT });
			polyline.setForeground(manageColor(COMPONENT_TYPE_COLOR_FOREGROUND));
			polyline.setLineWidth(2);
		}

		// SHAPE WITH TEXT
		{
			// Create shape for text
			Shape textShape = peCreateService.createShape(containerShape, false);

			// Create and set text graphics algorithm
			Text text = gaService.createText(textShape, componentTypeToAdd.getName());
			text.setForeground(manageColor(COMPONENT_TYPE_COLOR_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.getFont().setBold(true);
			gaService.setLocationAndSize(text, 0, 0, COMPONENT_TYPE_WIDTH_DEFAULT, COMPONENT_TYPE_HEIGHT_UPPER_COMPARTMENT_DEFAULT);

			// Create link and wire it up
			link(textShape, componentTypeToAdd);
		}

		return containerShape;
	}
}
